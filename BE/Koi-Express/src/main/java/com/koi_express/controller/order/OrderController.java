package com.koi_express.controller.order;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.koi_express.dto.OrderWithCustomerDTO;
import com.koi_express.dto.request.OrderRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.order.Orders;
import com.koi_express.entity.shipment.Shipments;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import com.koi_express.jwt.JwtUtil;
import com.koi_express.service.order.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;
    private final JwtUtil jwtUtil;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createOrder(
            @Valid @RequestBody OrderRequest orderRequest, HttpServletRequest request) {
        String token = extractToken(request);
        logger.info("Processing order creation for token: {}", token);
        ApiResponse<Map<String, Object>> response = orderService.createOrder(orderRequest, token);
        logger.info("Response sent to client: {}", response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<ApiResponse<String>> cancelOrder(@PathVariable Long orderId) {
        logger.info("Canceling order with ID: {}", orderId);
        ApiResponse<String> response = orderService.cancelOrder(orderId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/deliver/{orderId}")
    public ResponseEntity<ApiResponse<String>> deliverOrder(@PathVariable Long orderId) {
        try {
            logger.info("Marking order as delivered with ID: {}", orderId);
            ApiResponse<String> response = orderService.deliveredOrder(orderId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error marking order as delivered with ID {}: ", orderId, e);
            throw new AppException(ErrorCode.ORDER_DELIVERY_FAILED);
        }
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('SALES_STAFF')")
    @GetMapping(value = "/all-orders", produces = "application/json")
    public ResponseEntity<ApiResponse<List<OrderWithCustomerDTO>>> getAllOrders() {
        ApiResponse<List<OrderWithCustomerDTO>> response = orderService.getAllOrders();
        if (response.getResult() == null || response.getResult().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<OrderWithCustomerDTO>>> getOrderHistory(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {

        logger.info("Fetching order history with status: {}, fromDate: {}, toDate: {}", status, fromDate, toDate);

        String token = authorizationHeader.substring(7);

        ApiResponse<List<OrderWithCustomerDTO>> response =
                orderService.getOrderHistoryByFilters(token, status, fromDate, toDate);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderWithCustomerDTO> getOrderWithDetails(
            @PathVariable Long orderId, HttpServletRequest request) {
        logger.info("Fetching order with details for orderId: {}", orderId);

        String token = request.getHeader("Authorization").substring(7);
        String role = jwtUtil.extractRole(token);
        String userId = jwtUtil.extractUserId(token, role);
        logger.info("Extracted role: {}, Extracted userId: {}", role, userId);

        Orders order = orderService.getOrderWithDetails(orderId).getOrder();
        Customers customer = order.getCustomer();
        Shipments shipments = order.getShipment();

        OrderWithCustomerDTO response = OrderWithCustomerDTO.builder()
                .order(order)
                .customer(customer)
                .shipments(shipments)
                .build();
        return ResponseEntity.ok(response);
    }

    private String extractToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        } else {
            throw new IllegalArgumentException("Authorization header must be provided");
        }
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/calculate-order-price")
    public ApiResponse<Map<String, BigDecimal>> calculateOrderPrice(@Valid @RequestBody OrderRequest orderRequest, HttpSession session) {
        try {
            ApiResponse<Map<String, Object>> feesResponse = orderService.calculateOrderPrice(orderRequest);

            Map<String, BigDecimal> fees = feesResponse.getResult().entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> new BigDecimal(entry.getValue().toString())));

            session.setAttribute("distanceFee", fees.get("distanceFee"));
            session.setAttribute("commitmentFee", fees.get("commitmentFee"));

            return ApiResponse.success("Order price calculated successfully", fees);
        } catch (Exception e) {
            logger.error("Error calculating order price for kilometers {}: ", orderRequest, e);
            throw new AppException(ErrorCode.ORDER_PRICE_CALCULATION_FAILED);
        }
    }

    @GetMapping("/get-fees")
    public ApiResponse<Map<String, BigDecimal>> getFees(HttpSession session) {
        Map<String, BigDecimal> fees = new HashMap<>();
        fees.put("distanceFee", (BigDecimal) session.getAttribute("distanceFee"));
        fees.put("commitmentFee", (BigDecimal) session.getAttribute("commitmentFee"));
        return ApiResponse.success("Order price get successfully", fees);
    }
}
