package com.koi_express.controller;

import java.util.List;

import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.order.Orders;
import com.koi_express.service.deliveringStaff.DeliveringStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delivering/orders")
@PreAuthorize("hasRole('DELIVERING_STAFF')")
public class DeliveringStaffController {

    @Autowired
    private DeliveringStaffService deliveringStaffService;

    @GetMapping("/{id}/assigned-orders")
    public ResponseEntity<List<Orders>> getAssignedOrdersByDeliveringStaff(@PathVariable Long id) {
        List<Orders> orders = deliveringStaffService.getAssignedOrdersByDeliveringStaff(id);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/pickup/{orderId}")
    public ResponseEntity<ApiResponse<String>> pickupOrder(@PathVariable Long orderId) {

        ApiResponse<String> response = deliveringStaffService.pickupOrder(orderId);

        return ResponseEntity.status(response.getCode()).body(response);
    }
}
