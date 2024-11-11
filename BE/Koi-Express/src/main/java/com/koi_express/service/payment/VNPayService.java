package com.koi_express.service.payment;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

import com.koi_express.config.VNPayConfig;
import com.koi_express.dto.payment.PaymentData;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.order.Orders;
import com.koi_express.util.VNPayUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VNPayService {

    private static final Logger logger = LoggerFactory.getLogger(VNPayService.class);
    private final VNPayConfig vnPayConfig;

    public ApiResponse<String> createVnPayPayment(Orders order) {
        if (order == null
                || order.getOrderDetail() == null
                || order.getOrderDetail().getCommitmentFee() == null) {
            throw new IllegalArgumentException("Invalid order details provided.");
        }

        BigDecimal amount = order.getOrderDetail().getCommitmentFee().multiply(BigDecimal.valueOf(100));
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("Invalid commitment fee amount for order ID: {}", order.getOrderId());
            return new ApiResponse<>(400, "Commitment fee must be greater than zero", null);
        }

        String transactionRef = order.getOrderId() + "_" + System.currentTimeMillis();

        return generateVnPayPaymentUrl(order, amount, transactionRef);
    }

    public ApiResponse<String> createVnPayPaymentWithTotalFee(Orders order, BigDecimal totalFee) {
        if (order == null || totalFee == null) {
            logger.error("Invalid order or totalFee provided.");
            throw new IllegalArgumentException("Invalid order or totalFee provided.");
        }

        BigDecimal amount = totalFee.multiply(BigDecimal.valueOf(100));
        String transactionRef = generateTransactionRef(order.getOrderId(), true);

        return generateVnPayPaymentUrl(order, amount, transactionRef);
    }

    private String generateTransactionRef(Long orderId, boolean isFinal) {
        return orderId + "_" + System.currentTimeMillis() + (isFinal ? "_final" : "");
    }

    private ApiResponse<String> generateVnPayPaymentUrl(Orders order, BigDecimal amount, String transactionRef) {
        try {
            String bankCode = "NCB";
            Map<String, String> vnpParamsMap = buildVnPayParams(order, amount, bankCode, transactionRef);

            String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap);
            logger.info("Request params: {}", vnpParamsMap);
            logger.info("Query string before signature: {}", queryUrl);

            String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), queryUrl);
            vnpParamsMap.put("vnp_SecureHash", vnpSecureHash);

            String fullPaymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + VNPayUtil.getPaymentURL(vnpParamsMap);
            logger.info("Generated VNPay payment URL: {}", fullPaymentUrl);

            return new ApiResponse<>(200, "Payment URL generated successfully", fullPaymentUrl);
        } catch (Exception e) {
            logger.error("Failed to generate VNPay payment URL for order ID: {}", order.getOrderId(), e);
            return new ApiResponse<>(500, "Error generating payment URL", null);
        }
    }

    public ApiResponse<String> refund(Orders order, BigDecimal amount) {
        try {
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                logger.error("Invalid refund amount for order ID: {}", order.getOrderId());
                return new ApiResponse<>(400, "Refund amount must be greater than zero", null);
            }

            String transactionRef = generateTransactionRef(order.getOrderId(), false);
            Map<String, String> refundParams = buildRefundParams(order, amount, transactionRef);

            String refundUrl = VNPayUtil.getRefundURL(refundParams);
            String refundResponse = VNPayUtil.sendRefundRequest(refundUrl);

            if (VNPayUtil.verifyRefundResponse(refundResponse, vnPayConfig.getSecretKey())) {
                logger.info("Successfully refunded amount {} for order ID {}", amount, order.getOrderId());
                return new ApiResponse<>(200, "Refund processed successfully", refundResponse);
            } else {
                logger.warn("Refund verification failed for order ID {}", order.getOrderId());
                return new ApiResponse<>(500, "Refund verification failed", null);
            }
        } catch (Exception e) {
            logger.error("Error processing refund for order ID {}: {}", order.getOrderId(), e.getMessage());
            return new ApiResponse<>(500, "Error processing refund", e.getMessage());
        }
    }

    public boolean verifyPayment(Map<String, String> vnpParams) throws IOException {
        String vnpSecureHash = vnpParams.get("vnp_SecureHash");
        vnpParams.remove("vnp_SecureHash");

        String calculatedHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), VNPayUtil.getPaymentURL(vnpParams));
        if (!calculatedHash.equals(vnpSecureHash)) {
            logger.error("Payment verification failed for transaction: {}", vnpParams.get("vnp_TxnRef"));
            return false;
        }

        PaymentData paymentData = VNPayUtil.getPaymentData(vnpParams);
        if (!"00".equals(paymentData.getResponseCode())) {
            logger.error("Payment failed with response code: {}", paymentData.getResponseCode());
            return false;
        }

        return true;
    }

    private Map<String, String> buildVnPayParams(
            Orders order, BigDecimal amount, String bankCode, String transactionRef) {
        Map<String, String> vnpParamsMap = new TreeMap<>(vnPayConfig.getVNPayConfig());

        vnpParamsMap.put("vnp_Amount", String.valueOf(amount.longValue()));
        vnpParamsMap.put("vnp_TxnRef", transactionRef);
        vnpParamsMap.put("vnp_OrderInfo", "Thanh toan: " + order.getOrderId());
        vnpParamsMap.put("vnp_BankCode", bankCode);
        vnpParamsMap.put("vnp_IpAddr", "127.0.0.1");

        vnpParamsMap.put("vnp_ExpireDate", VNPayUtil.calculateExpireDate());

        return vnpParamsMap;
    }

    private Map<String, String> buildRefundParams(Orders order, BigDecimal amount, String transactionRef) {
        Map<String, String> refundParams = new TreeMap<>();

        refundParams.put(
                "vnp_Amount",
                String.valueOf(amount.multiply(BigDecimal.valueOf(100)).longValue()));
        refundParams.put("vnp_TxnRef", transactionRef);
        refundParams.put("vnp_OrderInfo", "Refund for order ID: " + order.getOrderId());
        refundParams.put("vnp_IpAddr", "127.0.0.1");
        refundParams.put("vnp_TransactionType", "02");
        refundParams.put("vnp_RefundDate", VNPayUtil.calculateCurrentDate());

        return refundParams;
    }
}
