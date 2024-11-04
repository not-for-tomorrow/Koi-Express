package com.koi_express.controller.payment;

import java.util.Map;
import java.util.stream.Collectors;

import com.koi_express.dto.payment.PaymentDTO;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.dto.response.ResponseObject;
import com.koi_express.entity.order.Orders;
import com.koi_express.service.order.OrderService;
import com.koi_express.service.payment.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class VNPayController {

    private static final Logger logger = LoggerFactory.getLogger(VNPayController.class);

    private final VNPayService vnPayService;
    private final OrderService orderService;

    @GetMapping("/vn-pay")
    public ResponseObject<PaymentDTO.VNPayResponse> pay(@RequestParam("orderId") Long orderId) {
        try {
            if (orderId == null || orderId <= 0) {
                return new ResponseObject<>(HttpStatus.BAD_REQUEST, "Invalid order ID", null);
            }

            logger.info("Processing payment for order ID: {}", orderId);

            Orders order = orderService.findOrderById(orderId);
            String paymentUrl = String.valueOf(vnPayService.createVnPayPayment(order));

            PaymentDTO.VNPayResponse paymentResponse = PaymentDTO.VNPayResponse.builder()
                    .code("ok")
                    .message("success")
                    .paymentUrl(paymentUrl)
                    .build();

            logger.info("Payment URL generated for order ID: {}", orderId);
            return new ResponseObject<>(HttpStatus.OK, "Payment URL generated successfully", paymentResponse);
        } catch (Exception e) {
            logger.error("Error processing payment for order ID: {}", orderId, e);
            return new ResponseObject<>(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing payment", null);
        }
    }

    @GetMapping("/vn-pay-callback")
    public ResponseEntity<Object> payCallbackHandler(HttpServletRequest request) {
        try {
            Map<String, String> vnpParams = request.getParameterMap().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()[0]));

            if (!vnpParams.containsKey("vnp_TxnRef") || !vnpParams.containsKey("vnp_ResponseCode")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing transaction reference or response code");
            }

            String transactionRef = vnpParams.get("vnp_TxnRef");
            if (transactionRef == null || transactionRef.isEmpty()) {
                logger.error("Transaction reference is missing or empty in callback.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid transaction reference");
            }

            boolean isFinalPayment = transactionRef.contains("_final");

            Long orderId;
            Long userId = null;
            if (transactionRef.contains("_")) {
                String[] parts = transactionRef.split("_");
                orderId = Long.parseLong(parts[0]);
                if (isFinalPayment && parts.length > 2) {
                    userId = Long.parseLong(parts[1]);
                }
            } else {
                orderId = Long.parseLong(transactionRef);
            }

            String responseCode = vnpParams.get("vnp_ResponseCode");
            logger.info("Received payment callback for order ID: {} with response code: {}", orderId, responseCode);

            ApiResponse<String> response;
            if (isFinalPayment) {
                if (userId == null) {
                    logger.error("User ID is missing for final payment in transaction reference.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User ID missing for final payment");
                }
                response = orderService.confirmPaymentFromStorage(userId);
            } else {
                response = orderService.confirmCommitFeePayment(orderId, vnpParams);
            }

            if ("00".equals(responseCode)) {
                String successUrl = isFinalPayment ? System.getenv("RETURN_URL_FINAL_PAYMENT_SUCCESS") : System.getenv("RETURN_URL_COMMIT_FEE_SUCCESS");
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header("Location", successUrl)
                        .build();
            } else {
                String failureUrl = isFinalPayment ? System.getenv("RETURN_URL_FINAL_PAYMENT_FAILURE") : System.getenv("RETURN_URL_COMMIT_FEE_FAILURE");
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header("Location", failureUrl)
                        .build();
            }
        } catch (Exception e) {
            logger.error("Error handling payment callback", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error handling payment callback");
        }
    }

}
