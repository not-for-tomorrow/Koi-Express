package com.koi_express.controller.delivering_staff;

import java.util.List;

import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.order.Orders;
import com.koi_express.exception.AppException;
import com.koi_express.jwt.JwtUtil;
import com.koi_express.service.delivering_staff.DeliveringStaffService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delivering/orders")
@PreAuthorize("hasRole('DELIVERING_STAFF')")
@RequiredArgsConstructor
public class DeliveringStaffController {

    private static final Logger logger = LoggerFactory.getLogger(DeliveringStaffController.class);
    private static final String ERROR_RETRIEVING_ASSIGNED_ORDERS = "Error retrieving assigned orders";
    private static final String ERROR_PICKUP_ORDER = "Failed to pick up order";

    private final DeliveringStaffService deliveringStaffService;
    private final JwtUtil jwtUtil;

    private Long extractDeliveringStaffId(String token) {
        String cleanedToken = jwtUtil.cleanToken(token);
        return Long.parseLong(jwtUtil.extractUserId(cleanedToken, "DELIVERING_STAFF"));
    }

    @GetMapping("/assigned-orders")
    public ResponseEntity<ApiResponse<List<Orders>>> getAssignedOrdersByDeliveringStaff(
            @RequestHeader("Authorization") String token) {
        try {
            Long deliveringStaffId = extractDeliveringStaffId(token);
            List<Orders> orders = deliveringStaffService.getAssignedOrdersByDeliveringStaff(deliveringStaffId);

            if (orders.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse<>(HttpStatus.NO_CONTENT.value(), "No assigned orders found", orders));
            }
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Assigned orders retrieved", orders));
        } catch (Exception e) {
            logger.error("{} for delivering staff ID: {}", ERROR_RETRIEVING_ASSIGNED_ORDERS, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(), ERROR_RETRIEVING_ASSIGNED_ORDERS, null));
        }
    }

    @GetMapping("/pickup-orders")
    public ResponseEntity<ApiResponse<List<Orders>>> getPickupOrdersByDeliveringStaff(
            @RequestHeader("Authorization") String token) {
        try {
            Long deliveringStaffId = extractDeliveringStaffId(token);
            List<Orders> orders = deliveringStaffService.getPickupOrdersByDeliveringStaff(deliveringStaffId);

            if (orders.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "No pick-up orders found", orders));
            }

            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Pick-up orders retrieved", orders));
        } catch (Exception e) {
            logger.error("{} for delivering staff ID: {}", ERROR_RETRIEVING_ASSIGNED_ORDERS, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(), ERROR_RETRIEVING_ASSIGNED_ORDERS, null));
        }
    }

    @GetMapping("/intransit-orders")
    public ResponseEntity<ApiResponse<List<Orders>>> getIntransitOrdersByDeliveringStaff(
            @RequestHeader("Authorization") String token) {
        try {
            Long deliveringStaffId = extractDeliveringStaffId(token);
            List<Orders> orders = deliveringStaffService.getInTransitOrdersByDeliveringStaff(deliveringStaffId);

            if (orders.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "No in-transit order found", orders));
            }

            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "In-transit orders retrieved", orders));
        } catch (Exception e) {
            logger.error("{} for delivering staff ID: {}", ERROR_RETRIEVING_ASSIGNED_ORDERS, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(), ERROR_RETRIEVING_ASSIGNED_ORDERS, null));
        }
    }

    @PutMapping("/pickup/{orderId}")
    public ResponseEntity<ApiResponse<String>> pickupOrder(
            @PathVariable Long orderId, @RequestHeader("Authorization") String token) {

        try {
            Long deliveringStaffId = extractDeliveringStaffId(token);
            logger.info("Delivering staff ID {} attempting to pick up order ID {}", deliveringStaffId, orderId);
            ApiResponse<String> response = deliveringStaffService.pickupOrder(orderId, deliveringStaffId);
            return ResponseEntity.status(response.getCode()).body(response);
        } catch (AppException ae) {
            logger.error("{} for order ID: {}, staff ID: {}", ERROR_PICKUP_ORDER, orderId, ae.getMessage(), ae);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ae.getMessage(), ae.getMessage()));
        } catch (Exception e) {
            logger.error("{} for order ID: {}, staff ID: {}", ERROR_PICKUP_ORDER, orderId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(), ERROR_PICKUP_ORDER, e.getMessage()));
        }
    }

    @PutMapping("/complete-order/{orderId}")
    public ResponseEntity<ApiResponse<String>> completeOrder(
            @PathVariable Long orderId, @RequestHeader("Authorization") String token) {
        try {
            Long deliveringStaffId = extractDeliveringStaffId(token);
            ApiResponse<String> response = deliveringStaffService.completeOrder(orderId, deliveringStaffId);

            return ResponseEntity.status(response.getCode()).body(response);
        } catch (AppException ae) {
            logger.error("Failed to complete order ID: {} for delivering staff ID: {}", orderId, ae.getMessage(), ae);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), ae.getMessage(), ae.getMessage()));
        } catch (Exception e) {
            logger.error("Error completing order ID: {} for delivering staff ID: {}", orderId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to complete order", null));
        }
    }
}
