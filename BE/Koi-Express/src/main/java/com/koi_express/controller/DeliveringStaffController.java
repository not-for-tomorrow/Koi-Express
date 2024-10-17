package com.koi_express.controller;

import java.io.File;
import java.util.List;

import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.order.Orders;
import com.koi_express.service.deliveringStaff.DeliveringStaffService;
import com.koi_express.service.verification.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/delivering/orders")
@PreAuthorize("hasRole('DELIVERING_STAFF')")
public class DeliveringStaffController {


    private final DeliveringStaffService deliveringStaffService;

    private final S3Service s3Service;

    public DeliveringStaffController(S3Service s3Service, DeliveringStaffService deliveringStaffService) {
        this.s3Service = s3Service;
        this.deliveringStaffService = deliveringStaffService;
    }

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

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file")MultipartFile file) {

        try {
            File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
            file.transferTo(tempFile);

            String fileUrl = s3Service.uploadFile(file.getOriginalFilename(), tempFile);

            tempFile.delete();

            return ResponseEntity.ok(fileUrl);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed");
        }
    }
}
