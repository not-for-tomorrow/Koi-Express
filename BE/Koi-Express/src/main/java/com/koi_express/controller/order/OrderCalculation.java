package com.koi_express.controller.order;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.koi_express.jwt.JwtUtil;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.enums.KoiType;
import com.koi_express.service.order.price.KoiInvoiceCalculator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
    private final OrderSessionManager sessionManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/calculate-total-fee")
    public ResponseEntity<ApiResponse<Map<String, Object>>> calculateTotalFee(
            @RequestBody Map<String, List<Map<String, Object>>> requestBody,
            HttpSession session,
            HttpServletRequest request) {

        if (session == null) {
            logger.error("Session is null. Cannot proceed.");
            return new ResponseEntity<>(
                    new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Session is null", null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String token = request.getHeader("Authorization").substring(7);
        String role = jwtUtil.extractRole(token);
        String userId = jwtUtil.extractUserId(token, role);

        Map<String, Object> sessionData = sessionManager.retrievePickupOrderSessionData(session, role, userId);
        if (sessionData == null
                || !sessionData.containsKey("distanceFee")
                || !sessionData.containsKey("commitmentFee")) {
            return new ResponseEntity<>(
                    new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Session data missing", null),
                    HttpStatus.BAD_REQUEST);
        }

        BigDecimal distanceFee = sessionData.get("distanceFee") != null ? (BigDecimal) sessionData.get("distanceFee") : BigDecimal.ZERO;
        BigDecimal commitmentFee = sessionData.get("commitmentFee") != null ? (BigDecimal) sessionData.get("commitmentFee") : BigDecimal.ZERO;

        List<Map<String, Object>> koiList = requestBody.get("koiList");

        BigDecimal totalKoiFee = BigDecimal.ZERO;
        BigDecimal totalCareFee = BigDecimal.ZERO;
        BigDecimal totalPackagingFee = BigDecimal.ZERO;
        BigDecimal totalRemainingTransportationFee = BigDecimal.ZERO;
        BigDecimal totalInsuranceFee = BigDecimal.ZERO;
        BigDecimal totalVat = BigDecimal.ZERO;
        BigDecimal grandTotalFee = BigDecimal.ZERO;

        for (Map<String, Object> koi : koiList) {
            KoiType koiType = KoiType.valueOf((String) koi.get("koiType"));
            Integer koiQuantity = koi.get("quantity") != null ? (Integer) koi.get("quantity") : 0;
            BigDecimal koiSize = koi.get("koiSize") != null ? new BigDecimal(koi.get("koiSize").toString()) : BigDecimal.ZERO;

            ApiResponse<Map<String, BigDecimal>> feeResponse =
                    koiInvoiceCalculator.calculateTotalPrice(koiType, koiQuantity, koiSize, distanceFee, commitmentFee);
            Map<String, BigDecimal> individualFee = feeResponse.getResult();

            // Tính tổng các loại phí
            totalKoiFee = totalKoiFee.add(individualFee.getOrDefault("koiFee", BigDecimal.ZERO));
            totalCareFee = totalCareFee.add(individualFee.getOrDefault("careFee", BigDecimal.ZERO));
            totalPackagingFee = totalPackagingFee.add(individualFee.getOrDefault("packagingFee", BigDecimal.ZERO));
            totalRemainingTransportationFee = totalRemainingTransportationFee.add(individualFee.getOrDefault("remainingTransportationFee", BigDecimal.ZERO));
            totalInsuranceFee = totalInsuranceFee.add(individualFee.getOrDefault("insuranceFee", BigDecimal.ZERO));
            totalVat = totalVat.add(individualFee.getOrDefault("vat", BigDecimal.ZERO));
            grandTotalFee = grandTotalFee.add(individualFee.getOrDefault("totalFee", BigDecimal.ZERO));
        }

        // Chuẩn bị phản hồi
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("koiFee", totalKoiFee);
        responseData.put("careFee", totalCareFee);
        responseData.put("packagingFee", totalPackagingFee);
        responseData.put("remainingTransportationFee", totalRemainingTransportationFee);
        responseData.put("insuranceFee", totalInsuranceFee);
        responseData.put("vat", totalVat);
        responseData.put("totalFee", grandTotalFee);

        sessionManager.storeCalculationSessionData(session, role, userId, responseData);

        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value(), "Total fee calculated", responseData), HttpStatus.OK);
    }

}
