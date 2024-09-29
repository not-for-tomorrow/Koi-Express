package com.koi_express.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.koi_express.JWT.JwtUtil;
import com.koi_express.dto.request.OrderRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.Customers;
import com.koi_express.entity.Orders;
import com.koi_express.enums.PackingMethod;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import com.koi_express.repository.CustomersRepository;
import com.koi_express.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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
    private CustomersRepository customersRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // Create Order with OrderRequest, order with add into database base on customerId in payload of token
    public ApiResponse<Orders> createOrder(OrderRequest orderRequest, String token) {

        try {
            String customerId = jwtUtil.extractCustomerId(token);
            logger.info("Extracted customerId: {}", customerId);

            Customers customer = customersRepository.findById(Long.parseLong(customerId))
                    .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));
            logger.info("Retrieved customer: {}", customer);

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

            double totalFee = calculateTotalFee(orderRequest);
            order.setTotalFee(totalFee);

            Orders savedOrder = orderRepository.save(order);
            logger.info("Order created successfully: {}", savedOrder);

            return new ApiResponse<>(HttpStatus.OK.value(), "Order created successfully", savedOrder);
        } catch (Exception e) {
            logger.error("Error creating order: ", e);
            throw new AppException(ErrorCode.ORDER_CREATION_FAILED);
        }
    }

    public double calculateTotalFee(OrderRequest orderRequest) {

        double BASE_PRICE_PER_KG = 10000;
        double INSURANCE_COST_FER_FISH = 50000;
        double SPECIAL_CARE_COST_FER_FISH = 100000;
        double HEALTH_CHECK_COST_FER_FISH = 50000;
        double TAX_RATE = 0.05;
        double BASIC_PACKAGING_COST_FER_FISH = 50000;
        double SPECIAL_PACKAGING_COST_FER_FISH = 100000;
        final double FUEL_COST_PER_KM = 10000;

        int quantity = orderRequest.getKoiQuantity();
        double weightFee = orderRequest.getKoiQuantity();
        double distance  = calculateDistance(orderRequest.getOriginLocation(), orderRequest.getDestinationLocation());
        boolean isInsurance = orderRequest.isInsurance();
        boolean isSpecialCare = orderRequest.isSpecialCare();
        boolean isHealthCheck = orderRequest.isHealthCheck();
        PackingMethod packingMethod = orderRequest.getPackingMethod();

        double totalFee = 0;

        double basePrice = weightFee * BASE_PRICE_PER_KG;
        totalFee += basePrice;


        if(isInsurance) {
            totalFee += INSURANCE_COST_FER_FISH;
        }

        if(isSpecialCare) {
            totalFee += SPECIAL_CARE_COST_FER_FISH;
        }

        if(isHealthCheck) {
            totalFee += HEALTH_CHECK_COST_FER_FISH;
        }

        if(packingMethod == PackingMethod.NORMAL_PACKAGING) {
            totalFee += BASIC_PACKAGING_COST_FER_FISH;
        } else if(packingMethod == PackingMethod.SPECIAL_PACKAGING) {
            totalFee += SPECIAL_PACKAGING_COST_FER_FISH;
        }

        double tax = totalFee * TAX_RATE;
        totalFee += tax;

        double fuelCost = calculateFuelCost(distance);
        totalFee += fuelCost;

        return totalFee;
    }

    private double calculateDistance(String originLocation, String destinationLocation) {
        return 0;
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
////          chuyển đổi t m sang km
//            return distanceInMeters / 1000.0;
//        } catch (Exception e) {
//            logger.error("Error calculating distance: ", e);
//            throw new AppException(ErrorCode.GOOGLE_MAPS_API_ERROR);
//        }
//    }

    private double calculateFuelCost(double distance) {
        double FUEL_COST_PER_KM = 10000;
        return distance * FUEL_COST_PER_KM;
    }

}
