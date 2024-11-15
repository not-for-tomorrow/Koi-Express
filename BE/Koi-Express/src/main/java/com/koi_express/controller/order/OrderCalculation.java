package com.koi_express.controller.order;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.order.Orders;
import com.koi_express.enums.KoiType;
import com.koi_express.enums.ShipmentCondition;
import com.koi_express.jwt.JwtUtil;
import com.koi_express.service.delivering_staff.DeliveringStaffService;
import com.koi_express.service.order.price.KoiInvoiceCalculator;
import com.koi_express.store.TemporaryStorage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderCalculation {

    private static final Logger logger = LoggerFactory.getLogger(OrderCalculation.class);

    private final KoiInvoiceCalculator koiInvoiceCalculator;
    private final JwtUtil jwtUtil;
    private final DeliveringStaffService deliveringStaffService;

    @PostMapping("/calculate-total-fee")
    public ResponseEntity<ApiResponse<Map<String, Object>>> calculateTotalFee(
            @RequestBody Map<String, Object> requestBody, HttpServletRequest request) {

        String token;
        try {
            token = request.getHeader("Authorization").substring(7);
        } catch (Exception e) {
            logger.error("Authorization header is missing or malformed");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authorization token is missing or malformed", HttpStatus.UNAUTHORIZED.value()));
        }

        String role;
        Long staffId;
        try {
            role = jwtUtil.extractRole(token);
            staffId = Long.parseLong(jwtUtil.extractUserId(token, role));
        } catch (Exception e) {
            logger.error("Error extracting role or userId from token: ", e);
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid token", HttpStatus.UNAUTHORIZED.value()));
        }

        Optional<Orders> assignedOrder = deliveringStaffService.getPickupOrdersByDeliveringStaff(staffId).stream()
                .findFirst();
        if (assignedOrder.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest("No assigned order data for this staff"));
        }

        Orders order = assignedOrder.get();
        BigDecimal distanceFee = order.getOrderDetail().getDistanceFee();
        BigDecimal commitmentFee = order.getOrderDetail().getCommitmentFee();

        List<Map<String, Object>> koiList;
        try {
            koiList = (List<Map<String, Object>>) requestBody.get("koiList");
        } catch (ClassCastException e) {
            logger.error("Failed to cast koiList: ", e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest("Invalid koiList format"));
        }

        BigDecimal totalKoiFee = BigDecimal.ZERO;
        BigDecimal totalCareFee = BigDecimal.ZERO;
        BigDecimal totalPackagingFee = BigDecimal.ZERO;
        BigDecimal totalRemainingTransportationFee = BigDecimal.ZERO;
        BigDecimal totalInsuranceFee = BigDecimal.ZERO;
        BigDecimal totalVat = BigDecimal.ZERO;
        BigDecimal grandTotalFee = BigDecimal.ZERO;

        for (Map<String, Object> koi : koiList) {
            try {
                KoiType koiType = KoiType.valueOf((String) koi.get("koiType"));
                BigDecimal koiSize = new BigDecimal(koi.get("koiSize").toString());
                ShipmentCondition shipmentCondition = ShipmentCondition.valueOf((String) koi.get("shipmentCondition"));
                int quantity = (int) koi.get("quantity");

                ApiResponse<Map<String, BigDecimal>> feeResponse = koiInvoiceCalculator.calculateTotalPrice(
                        koiType, quantity, koiSize, distanceFee, commitmentFee);

                Map<String, BigDecimal> individualFee = feeResponse.getResult();
                totalKoiFee = totalKoiFee.add(individualFee.get("koiFee"));
                totalCareFee = totalCareFee.add(individualFee.get("careFee"));
                totalPackagingFee = totalPackagingFee.add(individualFee.get("packagingFee"));
                totalRemainingTransportationFee =
                        totalRemainingTransportationFee.add(individualFee.get("remainingTransportationFee"));
                totalInsuranceFee = totalInsuranceFee.add(individualFee.get("insuranceFee"));
                totalVat = totalVat.add(individualFee.get("vat"));
                grandTotalFee = grandTotalFee.add(individualFee.get("totalFee"));

            } catch (IllegalArgumentException e) {
                logger.error("Error calculating fees for koi: ", e);
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.badRequest(e.getMessage()));
            }
        }

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("koiFee", totalKoiFee);
        responseData.put("careFee", totalCareFee);
        responseData.put("packagingFee", totalPackagingFee);
        responseData.put("remainingTransportationFee", totalRemainingTransportationFee);
        responseData.put("insuranceFee", totalInsuranceFee);
        responseData.put("vat", totalVat);
        responseData.put("totalFee", grandTotalFee);
        responseData.put("orderId", order.getOrderId());

        TemporaryStorage.getInstance().storeData(staffId, responseData);

        return ResponseEntity
                .ok(ApiResponse.success("Total fee calculated", responseData));
    }
}
