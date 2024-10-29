package com.koi_express.controller.order;

import java.util.Map;
import java.util.stream.Collectors;

import com.koi_express.dto.response.ApiResponse;
import com.koi_express.service.order.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderPayment {

    private static final Logger logger = LoggerFactory.getLogger(OrderPayment.class);

    private final OrderService orderService;

    @PostMapping("/payment/commit-fee/callback")
    public ResponseEntity<ApiResponse<String>> confirmCommitFeePayment(HttpServletRequest request) {
        Map<String, String> vnpParams = request.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()[0]));

        if (!vnpParams.containsKey("vnp_TxnRef")) {
            return new ResponseEntity<>(
                    new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Missing transaction reference", null),
                    HttpStatus.BAD_REQUEST);
        }

        long orderId = Long.parseLong(vnpParams.get("vnp_TxnRef"));
        logger.info("Processing commit fee payment callback for order ID: {}", orderId);

        ApiResponse<String> response = orderService.confirmCommitFeePayment(orderId, vnpParams);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/confirm-payment")
    public ResponseEntity<ApiResponse<String>> confirmPayment(HttpServletRequest request, HttpSession session) {

        try {
            ApiResponse<String> paymentResponse = orderService.confirmPayment(session, request);

            return new ResponseEntity<>(paymentResponse, HttpStatus.valueOf(paymentResponse.getCode()));

        } catch (Exception e) {
            logger.error("Error during payment confirmation: ", e);
            return new ResponseEntity<>(
                    new ApiResponse<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error processing payment", e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
