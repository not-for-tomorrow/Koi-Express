package com.koi_express.controller.delivering_staff;

import java.util.List;

import com.koi_express.jwt.JwtUtil;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.order.Orders;
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

    private final DeliveringStaffService deliveringStaffService;
    private final JwtUtil jwtUtil;

    @GetMapping("/assigned-orders")
    public ResponseEntity<ApiResponse<List<Orders>>> getAssignedOrdersByDeliveringStaff(@RequestHeader("Authorization") String token) {
        try {
            String cleanedToken = jwtUtil.cleanToken(token);
            String deliveringStaffIdStr = jwtUtil.extractUserId(cleanedToken, "DELIVERING_STAFF");
            Long deliveringStaffId = Long.parseLong(deliveringStaffIdStr);

            List<Orders> orders = deliveringStaffService.getAssignedOrdersByDeliveringStaff(deliveringStaffId);
            if (orders.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse<>(HttpStatus.NO_CONTENT.value(), "No assigned orders found", orders));
            }
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Assigned orders retrieved", orders));
        } catch (Exception e) {
            logger.error("Error retrieving assigned orders for delivering staff ID: {}", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error retrieving assigned orders", null));
        }
    }

    @PostMapping("/pickup/{orderId}")
    public ResponseEntity<ApiResponse<String>> pickupOrder(
            @PathVariable Long orderId, @RequestHeader("Authorization") String token) {

        try {
            String cleanedToken = jwtUtil.cleanToken(token);
            String deliveringStaffIdStr = jwtUtil.extractUserId(cleanedToken, "DELIVERING_STAFF");

            Long deliveringStaffId = Long.parseLong(deliveringStaffIdStr);

            ApiResponse<String> response = deliveringStaffService.pickupOrder(orderId, deliveringStaffId);

            return ResponseEntity.status(response.getCode()).body(response);
        } catch (Exception e) {
            logger.error("Error picking up order ID: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to pick up order", e.getMessage()));
        }
    }

    @PostMapping("/complete-delivery/{orderId}")
    public ResponseEntity<ApiResponse<String>> completeDelivery(
            @PathVariable Long orderId, @RequestHeader("Authorization") String token) {

        try {
            String cleanedToken = jwtUtil.cleanToken(token);
            String deliveringStaffIdStr = jwtUtil.extractUserId(cleanedToken, "DELIVERING_STAFF");
            Long deliveringStaffId = Long.parseLong(deliveringStaffIdStr);

            ApiResponse<String> response = deliveringStaffService.completeDelivery(orderId, deliveringStaffId);

            return ResponseEntity.status(response.getCode()).body(response);
        } catch (Exception e) {
            logger.error("Error completing delivery for order ID: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to complete delivery", e.getMessage()));
        }
    }

}
