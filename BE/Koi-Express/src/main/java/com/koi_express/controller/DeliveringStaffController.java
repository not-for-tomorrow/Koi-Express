package com.koi_express.controller;

import java.io.File;
import java.util.List;

import com.koi_express.JWT.JwtUtil;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.order.OrderDetail;
import com.koi_express.entity.order.Orders;
import com.koi_express.exception.AppException;
import com.koi_express.service.deliveringStaff.DeliveringStaffService;
import com.koi_express.service.image.ShipmentInspectionImageService;
import com.koi_express.service.order.OrderService;
import com.koi_express.service.order.price.KoiInvoiceCalculator;
import com.koi_express.service.payment.VNPayService;
import com.koi_express.service.verification.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/delivering/orders")
@PreAuthorize("hasRole('DELIVERING_STAFF')")
public class DeliveringStaffController {

    private static final Logger logger = LoggerFactory.getLogger(DeliveringStaffController.class);

    private final DeliveringStaffService deliveringStaffService;
    private final S3Service s3Service;
    private final JwtUtil jwtUtil;

    public DeliveringStaffController(
            S3Service s3Service,
            DeliveringStaffService deliveringStaffService,
            JwtUtil jwtUtil,
            ShipmentInspectionImageService shipmentInspectionImageService,
            KoiInvoiceCalculator koiInvoiceCalculator,
            VNPayService vnpayService,
            OrderService orderService) {
        this.s3Service = s3Service;
        this.deliveringStaffService = deliveringStaffService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/{id}/assigned-orders")
    public ResponseEntity<ApiResponse<List<Orders>>> getAssignedOrdersByDeliveringStaff(@PathVariable Long id) {
        try {
            List<Orders> orders = deliveringStaffService.getAssignedOrdersByDeliveringStaff(id);
            if (orders.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new ApiResponse<>(HttpStatus.NO_CONTENT.value(), "No assigned orders found", orders));
            }
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Assigned orders retrieved", orders));
        } catch (Exception e) {
            logger.error("Error retrieving assigned orders for delivering staff ID: {}", id, e);
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

    @PostMapping("/upload/{staffId}/{orderDate}/{category}")
    public ResponseEntity<ApiResponse<String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @PathVariable String staffId,
            @PathVariable String orderDate,
            @PathVariable String category) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "File is empty", null));
        }

        try {
            File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
            file.transferTo(tempFile);

            logger.info("Uploading file to S3...");
            String fileUrl = s3Service.uploadFile(staffId, orderDate, category, tempFile);

            if (fileUrl == null || fileUrl.isEmpty()) {
                logger.error("File URL not generated.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "File upload failed", null));
            }

            logger.info("File uploaded to S3 successfully. URL: {}", fileUrl);

            boolean deleted = tempFile.delete();
            if (!deleted) {
                logger.warn("Temporary file {} could not be deleted", tempFile.getAbsolutePath());
            }

            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "File uploaded successfully", fileUrl));

        } catch (Exception e) {
            logger.error("File upload failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(), "File upload failed", e.getMessage()));
        }
    }
}