package com.koi_express.service.Order;

import com.koi_express.JWT.JwtUtil;
import com.koi_express.dto.request.OrderRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.Customers;
import com.koi_express.entity.Orders;
import com.koi_express.enums.OrderStatus;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import com.koi_express.repository.OrderRepository;
import com.koi_express.service.ManagerService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

//    private static final String GOOGLE_MAPS_API_KEY = "AIzaSyBEtydz_RCAU5lDodbyLDOf4UJcHhAWXgI";
//    private static final String GOOGLE_MAPS_DISTANCE_MATRIX_URL = "https://maps.googleapis.com/maps/api/distancematrix/json";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JavaMailSender  javaMailSender;

    @Autowired
    private OrderFeeCalculator orderFeeCalculator;

    @Autowired
    private ManagerService managerService;


    // Create Order with OrderRequest, order with add into database base on customerId in payload of token
    public ApiResponse<Orders> createOrder(OrderRequest orderRequest, String token) {

        try {
            String customerId = jwtUtil.extractCustomerId(token);
            Customers customer = managerService.getCustomerById(Long.parseLong(customerId));
            Orders orders = buildOrder(orderRequest, customer);
            Orders savedOrder = orderRepository.save(orders);
            logger.info("Order created successfully: {}", savedOrder);

            sendOrderConfirmationEmail(customer.getEmail(), savedOrder);

            return new ApiResponse<>(HttpStatus.OK.value(), "Order created successfully", savedOrder);
        } catch (Exception e) {
            logger.error("Error creating order: ", e);
            throw new AppException(ErrorCode.ORDER_CREATION_FAILED);
        }
    }

    private void sendOrderConfirmationEmail(String recipientEmail, Orders order) throws IOException {

        try {

            String htmlTemplate = loadEmailTemplate("Order Confirmation.html");

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{{CustomerName}}", order.getCustomer().getFullName());
            placeholders.put("{{OrderID}}", String.valueOf(order.getOrderId()));
            placeholders.put("{{OrderDate}}", order.getCreatedAt().toString());
            placeholders.put("{{Address}}", order.getDestinationLocation());
            placeholders.put("{{TotalAmount}}", String.format("%.2f", order.getTotalFee()));
            placeholders.put("{{TrackOrderLink}}", "https://koiexpress.com/track-order/" + order.getOrderId());

            htmlTemplate = replacePlaceholders(htmlTemplate, placeholders);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(recipientEmail);
            helper.setSubject("Order Confirmation - Koi Express");
            helper.setText(htmlTemplate, true);
            javaMailSender.send(message);

            logger.info("Order confirmation email sent to: {}", recipientEmail);
        } catch (Exception e) {
            logger.error("Error sending order confirmation email: ", e);
            throw new AppException(ErrorCode.EMAIL_SENDING_FAILED);
        }
    }

    private String loadEmailTemplate(String fileName) throws IOException {

        ClassPathResource resource = new ClassPathResource(fileName);

        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    private String replacePlaceholders(String template, Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            template = template.replace(entry.getKey(), entry.getValue());
        }
        return template;
    }

    private String extractCustomerIdFromToken(String token) {
        return jwtUtil.extractCustomerId(token);
    }

    private Orders buildOrder(OrderRequest orderRequest, Customers customer) {
        Orders order = new Orders();
        order.setCustomer(customer);
        order.setRecipientName(orderRequest.getRecipientName());
        order.setRecipientPhone(orderRequest.getRecipientPhone());
        order.setKoiType(orderRequest.getKoiType());
        order.setKoiQuantity(orderRequest.getKoiQuantity());
        order.setOriginLocation(orderRequest.getOriginLocation());
        order.setDestinationLocation(orderRequest.getDestinationLocation());
        order.setPackingMethod(orderRequest.getPackingMethod());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setInsurance(orderRequest.isInsurance());
        order.setSpecialCare(orderRequest.isSpecialCare());
        order.setHealthCheck(orderRequest.isHealthCheck());

        double totalFee = orderFeeCalculator.calculateTotalFee(orderRequest);
        order.setTotalFee(totalFee);

        return order;
    }

//    private double calculateDistance(String originLocation, String destinationLocation) {
//
//        try {
//            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(GOOGLE_MAPS_DISTANCE_MATRIX_URL)
//                    .queryParam("origins", originLocation)
//                    .queryParam("destinations", destinationLocation)
//                    .queryParam("key", GOOGLE_MAPS_API_KEY);
//
//            String url = builder.toUriString();
//            String response = restTemplate.getForObject(url, String.class);
//
//            logger.info("Response from Google Maps API: {}", response);
//
//
//            // Parse JSON response to get distance
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode root = objectMapper.readTree(response);
//
//            if(!root.has("rows") || !root.path("rows").has(0) || !root.path("rows").get(0).has("elements")) {
//                throw new AppException(ErrorCode.GOOGLE_MAPS_API_ERROR, "Invalid response from Google Maps API");
//            }
//
//            JsonNode jsonNode = root.path("rows").get(0).path("elements").get(0);
//            String elementStatus = jsonNode.path("status").asText();
//            if(!elementStatus.equals("OK")) {
//                throw new AppException(ErrorCode.GOOGLE_MAPS_API_ERROR, "Invalid response from Google Maps API");
//            }
//
//            double distanceInMeters = jsonNode.path("distance").path("value").asDouble();
//
////          chuyển đổi tu m sang km
//            return distanceInMeters / 1000.0;
//        } catch (Exception e) {
//            logger.error("Error calculating distance: ", e);
//            throw new AppException(ErrorCode.GOOGLE_MAPS_API_ERROR);
//        }
//    }



//    Cancel Order
    public ApiResponse<String> cancelOrder(Long orderId) {
        Orders orders = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (orders.getStatus() == OrderStatus.CANCELED || orders.getStatus() == OrderStatus.COMPLETED) {
            throw  new AppException(ErrorCode.ORDER_ALREADY_PROCESSED, "Order has already been processed");
        }

        orders.setStatus(OrderStatus.CANCELED);
        orderRepository.save(orders);

        logger.info("Order with ID {} has been canceled", orderId);
        return new ApiResponse<>(HttpStatus.OK.value(), "Order canceled successfully", null);
    }

//    Delivered Order
    public ApiResponse<String> deliveredOrder(Long orderId) {
        Orders orders = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (orders.getStatus() == OrderStatus.CANCELED || orders.getStatus() == OrderStatus.COMPLETED) {
            throw  new AppException(ErrorCode.ORDER_ALREADY_PROCESSED, "Order has already been processed");
        }

        orders.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(orders);

        logger.info("Order with ID {} has been delivered", orderId);
        return new ApiResponse<>(HttpStatus.OK.value(), "Order delivered successfully", null);
    }

    public Page<Orders> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }
}
