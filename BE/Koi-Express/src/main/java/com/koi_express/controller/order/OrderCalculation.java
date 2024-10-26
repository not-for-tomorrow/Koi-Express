package com.koi_express.controller.order;

import java.math.BigDecimal;
import java.util.Map;

import com.koi_express.jwt.JwtUtil;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.enums.KoiType;
import com.koi_express.service.order.price.KoiInvoiceCalculator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderCalculation {

    private static final Logger logger = LoggerFactory.getLogger(OrderCalculation.class);

    private final KoiInvoiceCalculator koiInvoiceCalculator;
    private final OrderSessionManager sessionManager;
    private final JwtUtil jwtUtil;

    public OrderCalculation(KoiInvoiceCalculator koiInvoiceCalculator, OrderSessionManager sessionManager, JwtUtil jwtUtil) {
        this.koiInvoiceCalculator = koiInvoiceCalculator;
        this.sessionManager = sessionManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/calculate-total-fee")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> calculateTotalFee(
            @RequestParam KoiType koiType,
            @RequestParam BigDecimal koiSize,
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

        Map<String, Object> sessionData = sessionManager.retrieveSessionData(session, role, userId);
        if (sessionData == null
                || !sessionData.containsKey("koiQuantity")
                || !sessionData.containsKey("distanceFee")
                || !sessionData.containsKey("commitmentFee")) {
            return new ResponseEntity<>(
                    new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Session data missing", null),
                    HttpStatus.BAD_REQUEST);
        }

        Integer koiQuantity = (Integer) sessionData.get("koiQuantity");
        BigDecimal distanceFee = (BigDecimal) sessionData.get("distanceFee");
        BigDecimal commitmentFee = (BigDecimal) sessionData.get("commitmentFee");

        ApiResponse<Map<String, BigDecimal>> response =
                koiInvoiceCalculator.calculateTotalPrice(koiType, koiQuantity, koiSize, distanceFee, commitmentFee);
        sessionManager.storeCalculationSessionData(session, role, userId, response.getResult());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
