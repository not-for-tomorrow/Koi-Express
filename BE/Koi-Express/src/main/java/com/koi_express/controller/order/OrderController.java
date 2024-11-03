package com.koi_express.controller.order;

import java.util.List;
import java.util.Map;

import com.koi_express.dto.OrderWithCustomerDTO;
import com.koi_express.dto.request.OrderRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.order.Orders;
import com.koi_express.entity.shipment.Shipments;
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
    private final OrderSessionManager sessionManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createOrder(
            @Valid @RequestBody OrderRequest orderRequest, HttpServletRequest request) {
        try {
            String token = extractToken(request);
            logger.info("Processing order creation for token: {}", token);
            ApiResponse<Map<String, Object>> response = orderService.createOrder(orderRequest, token);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Authorization header is missing or invalid");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Authorization header is missing or invalid", null));
        }
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('CUSTOMER')")
    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<ApiResponse<String>> cancelOrder(@PathVariable Long orderId) {
        logger.info("Canceling order with ID: {}", orderId);
        ApiResponse<String> response = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/deliver/{orderId}")
    public ResponseEntity<ApiResponse<String>> deliverOrder(@PathVariable Long orderId) {
        logger.info("Marking order as delivered with ID: {}", orderId);
        ApiResponse<String> response = orderService.deliveredOrder(orderId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('SALES_STAFF')")
    @GetMapping(value = "/all-orders", produces = "application/json")
    public ResponseEntity<ApiResponse<List<OrderWithCustomerDTO>>> getAllOrders() {
        ApiResponse<List<OrderWithCustomerDTO>> response = orderService.getAllOrders();
        if (response.getResult() == null || response.getResult().isEmpty()) {
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.NO_CONTENT.value(), "No orders found", null));
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<Orders>>> getOrderHistory(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {
        try {
            String token = authorizationHeader.substring(7);
            logger.info("Fetching order history with status: {}, fromDate: {}, toDate: {}", status, fromDate, toDate);
            ApiResponse<List<Orders>> response = orderService.getOrderHistoryByFilters(token, status, fromDate, toDate);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error retrieving order history", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error retrieving order history", null));
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderWithCustomerDTO> getOrderWithDetails(
            @PathVariable Long orderId, HttpSession session, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            String role = jwtUtil.extractRole(token);
            String userId = jwtUtil.extractUserId(token, role);

            logger.info("Fetching order details for orderId: {} by userId: {} with role: {}", orderId, userId, role);

            Orders order = orderService.getOrderWithDetails(orderId).getOrder();
            Customers customer = order.getCustomer();
            Shipments shipments = order.getShipment();

            sessionManager.storeSessionData(session, role, userId, order);

            OrderWithCustomerDTO response = OrderWithCustomerDTO.builder()
                    .order(order)
                    .customer(customer)
                    .shipments(shipments)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error retrieving order details for orderId: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String extractToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        } else {
            logger.warn("Authorization header is missing or invalid");
            throw new IllegalArgumentException("Authorization header must be provided");
        }
    }
}
