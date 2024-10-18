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
            logger.info("Processing payment for order ID: {}", orderId);

            Orders order = orderService.findOrderById(orderId);
            String paymentUrl = vnPayService.createVnPayPayment(order);

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
    public ResponseObject<String> payCallbackHandler(HttpServletRequest request) {
        try {
            Map<String, String> vnpParams = request.getParameterMap().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()[0]));

            long orderId = Long.parseLong(vnpParams.get("vnp_TxnRef"));
            logger.info("Received payment callback for order ID: {}", orderId);

            ApiResponse<String> response = orderService.confirmCommitFeePayment(orderId, vnpParams);

            if (response.getCode() == HttpStatus.OK.value()) {
                logger.info("Payment success for order ID: {}", orderId);
                return new ResponseObject<>(HttpStatus.OK, "Thanh toán thành công", "Payment Success");
            } else {
                logger.warn("Payment verification failed for order ID: {}", orderId);
                return new ResponseObject<>(
                        HttpStatus.BAD_REQUEST, "Xác minh thanh toán thất bại", "Payment Verification Failed");
            }
        } catch (Exception e) {
            logger.error("Error handling payment callback", e);
            return new ResponseObject<>(HttpStatus.INTERNAL_SERVER_ERROR, "Error handling payment callback", null);
        }
    }
}
