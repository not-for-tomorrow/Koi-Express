package com.koi_express.controller;

import com.koi_express.JWT.JwtUtil;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.order.Orders;
import com.koi_express.service.saleStaff.SalesStaffService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales/orders")
public class SalesStaffController {

    private static final Logger logger = LoggerFactory.getLogger(SalesStaffController.class);

    @Autowired
    private SalesStaffService salesStaffService;

    @Autowired
    private JwtUtil jwtUtil;

    @PreAuthorize("hasRole('SALES_STAFF')")
    @GetMapping("/pending")
    public ResponseEntity<Page<Orders>> getPendingOrders(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        Pageable paging = PageRequest.of(page, size);
        Page<Orders> pendingOrders = salesStaffService.getPendingOrders(paging);

        logger.info("Fetched pending orders for sales staff with pagination: page {}, size {}", page, size);

        return new ResponseEntity<>(pendingOrders, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('SALES_STAFF')")
    @PutMapping("/accept/{orderId}")
    public ResponseEntity<ApiResponse<String>> acceptOrder(@PathVariable Long orderId, @RequestHeader("Authorization") String token) {
        try {
            logger.info("Processing order acceptance for order ID: {}", orderId);

            String cleanedToken = token.replace("Bearer ", "").trim();

            String role = jwtUtil.extractRole(cleanedToken);
            String userId = jwtUtil.extractUserId(cleanedToken, role);

            logger.info("Sales staff with ID: {} and role: {} attempting to accept order ID: {}", userId, role, orderId);

            ApiResponse<String> response = salesStaffService.acceptOrder(orderId);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error processing order acceptance for order ID: {}", orderId, e);
            ApiResponse<String> errorResponse = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error accepting order", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
