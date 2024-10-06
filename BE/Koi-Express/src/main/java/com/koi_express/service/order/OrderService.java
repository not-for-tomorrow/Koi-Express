package com.koi_express.service.order;

import com.koi_express.JWT.JwtUtil;
import com.koi_express.dto.request.OrderRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.order.Orders;
import com.koi_express.enums.OrderStatus;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import com.koi_express.repository.OrderRepository;
import com.koi_express.service.manager.ManagerService;
import com.koi_express.service.verification.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

//    private static final String GOOGLE_MAPS_API_KEY = "AIzaSyBEtydz_RCAU5lDodbyLDOf4UJcHhAWXgI";
//    private static final String GOOGLE_MAPS_DISTANCE_MATRIX_URL = "https://maps.googleapis.com/maps/api/distancematrix/json";


    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OrderBuilder orderBuilder;


    // Create Order with OrderRequest, order with add into database base on customerId in payload of token
    public ApiResponse<Orders> createOrder(OrderRequest orderRequest, String token) {

        try {
            String customerId = jwtUtil.extractCustomerId(token);
            Customers customer = managerService.getCustomerById(Long.parseLong(customerId));
            Orders orders = orderBuilder.buildOrder(orderRequest, customer);
            Orders savedOrder = orderRepository.save(orders);
            logger.info("Order created successfully: {}", savedOrder);

            emailService.sendOrderConfirmationEmail(customer.getEmail(), savedOrder);

            return new ApiResponse<>(HttpStatus.OK.value(), "Order created successfully", savedOrder);
        } catch (Exception e) {
            logger.error("Error creating order: ", e);
            throw new AppException(ErrorCode.ORDER_CREATION_FAILED);
        }
    }

    private String extractCustomerIdFromToken(String token) {
        return jwtUtil.extractCustomerId(token);
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

//    Get All Orders
    public Page<Orders> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }
}
