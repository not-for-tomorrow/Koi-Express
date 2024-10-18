package com.koi_express.service.payment;

import java.io.IOException;
import java.util.Map;

import com.koi_express.config.VNPayConfig;
import com.koi_express.dto.payment.PaymentData;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.order.Orders;
import com.koi_express.util.VNPayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VNPayService {

    private static final Logger logger = LoggerFactory.getLogger(VNPayService.class);

    @Autowired
    private VNPayConfig vnPayConfig;

    public ApiResponse<String> createVnPayPayment(Orders order) {
        if (order == null
                || order.getOrderDetail() == null
                || order.getOrderDetail().getCommitmentFee() == null) {
            throw new IllegalArgumentException("Invalid order details provided.");
        }

        long amount = order.getOrderDetail().getCommitmentFee().longValue() * 100;
        String bankCode = "NCB";
        String orderId = String.valueOf(order.getOrderId());

        Map<String, String> vnpParamsMap = buildVnPayParams(order, amount, bankCode);

        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        logger.info("VNPay query URL before adding hash: {}", queryUrl);

        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        logger.info("Generated VNPay secure hash: {}", vnpSecureHash);

        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;

        String fullPaymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        logger.info("Generated VNPay payment URL: {}", fullPaymentUrl);

        // Return full payment URL
        return new ApiResponse<>(200, "Payment URL generated successfully", fullPaymentUrl);
    }

    public boolean verifyPayment(Map<String, String> vnpParams) throws IOException {
        String vnpSecureHash = vnpParams.get("vnp_SecureHash");
        vnpParams.remove("vnp_SecureHash");

        PaymentData paymentData = VNPayUtil.getPaymentData(vnpParams);

        String calculatedHash =
                VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), VNPayUtil.getPaymentURL(vnpParams, false));

        if (!calculatedHash.equals(vnpSecureHash)) {
            logger.error("Payment verification failed for transaction: {}", vnpParams.get("vnp_TxnRef"));
            return false;
        }

        if (!"00".equals(paymentData.getResponseCode())) {
            logger.error("Payment failed with response code: {}", paymentData.getResponseCode());
            return false;
        }

        return true;
    }

    private Map<String, String> buildVnPayParams(Orders order, long amount, String bankCode) {
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        vnpParamsMap.put("vnp_TxnRef", String.valueOf(order.getOrderId()));
        vnpParamsMap.put("vnp_OrderInfo", "Thanh toan commit fee cho don hang: " + order.getOrderId());
        vnpParamsMap.put("vnp_BankCode", bankCode);
        vnpParamsMap.put("vnp_IpAddr", "127.0.0.1");
        return vnpParamsMap;
    }
}