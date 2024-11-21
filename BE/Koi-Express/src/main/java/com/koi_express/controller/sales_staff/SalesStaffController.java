package com.koi_express.controller.sales_staff;

import java.util.List;

import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.customer.Support;
import com.koi_express.entity.order.Orders;
import com.koi_express.exception.AppException;
import com.koi_express.jwt.JwtUtil;
import com.koi_express.service.manager.ManageCustomerService;
import com.koi_express.service.sale_staff.SalesStaffService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales")
@PreAuthorize("hasRole('SALES_STAFF')")
@RequiredArgsConstructor
public class SalesStaffController {

    private static final Logger logger = LoggerFactory.getLogger(SalesStaffController.class);

    private final SalesStaffService salesStaffService;
    private final ManageCustomerService manageCustomerService;
    private final JwtUtil jwtUtil;

    @GetMapping("/orders/pending")
    public ResponseEntity<Page<Orders>> getPendingOrders(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        Pageable paging = PageRequest.of(page, size);
        Page<Orders> pendingOrders = salesStaffService.getPendingOrders(paging);

        logger.info("Fetched pending orders for sales staff with pagination: page {}, size {}", page, size);

        return new ResponseEntity<>(pendingOrders, HttpStatus.OK);
    }

    @PutMapping("/accept/{orderId}")
    public ResponseEntity<ApiResponse<String>> acceptOrder(
            @PathVariable Long orderId, @RequestHeader("Authorization") String token) {
        try {
            logger.info("Processing order acceptance for order ID: {}", orderId);

            String cleanedToken = token.replace("Bearer ", "").trim();

            String role = jwtUtil.extractRole(cleanedToken);
            String userId = jwtUtil.extractUserId(cleanedToken, role);

            logger.info(
                    "Sales staff with ID: {} and role: {} attempting to accept order ID: {}", userId, role, orderId);

            ApiResponse<String> response = salesStaffService.acceptOrder(orderId);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error processing order acceptance for order ID: {}", orderId, e);
            ApiResponse<String> errorResponse =
                    new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error accepting order", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/customers")
    public ResponseEntity<ApiResponse<List<Customers>>> getAllCustomers() {

        ApiResponse<List<Customers>> customersPage = manageCustomerService.getAllCustomers();

        return new ResponseEntity<>(customersPage, HttpStatus.OK);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<Customers>> getCustomerById(@PathVariable Long customerId) {
        try {
            Customers customer = manageCustomerService.getCustomerById(customerId);

            logger.info("Fetched details for customer ID: {}", customerId);
            return new ResponseEntity<>(
                    new ApiResponse<>(HttpStatus.OK.value(), "Customer details retrieved successfully", customer),
                    HttpStatus.OK);
        } catch (AppException e) {
            logger.error("Error retrieving customer details for ID: {}", customerId, e);
            return new ResponseEntity<>(
                    new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Unexpected error retrieving customer details for ID: {}", customerId, e);
            return new ResponseEntity<>(
                    new ApiResponse<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error retrieving customer details", null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/support/pending")
    public ResponseEntity<Page<Support>> getPendingSupport(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        Pageable paging = PageRequest.of(page, size);
        Page<Support> pendingOrders = salesStaffService.getPendingSupport(paging);

        logger.info("Fetched pending orders for sales staff with pagination: page {}, size {}", page, size);

        return new ResponseEntity<>(pendingOrders, HttpStatus.OK);
    }
}
