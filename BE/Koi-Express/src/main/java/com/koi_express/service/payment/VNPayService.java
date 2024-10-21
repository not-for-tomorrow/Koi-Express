package com.koi_express.service.payment;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

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

        BigDecimal amount = order.getOrderDetail().getCommitmentFee().multiply(BigDecimal.valueOf(100));
        String bankCode = "NCB";
        String orderId = String.valueOf(order.getOrderId());

        Map<String, String> vnpParamsMap = buildVnPayParams(order, amount, bankCode);

        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap);
        logger.info("Request params: {}", vnpParamsMap); // Log tham số đầu vào
        logger.info("Query string before signature: {}", queryUrl); // Log chuỗi URL trước khi tạo chữ ký

        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), queryUrl);
        logger.info("Generated secure hash: {}", vnpSecureHash); // Log chữ ký đã tạo

        vnpParamsMap.put("vnp_SecureHash", URLEncoder.encode(vnpSecureHash, StandardCharsets.UTF_8));

        String fullPaymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + VNPayUtil.getPaymentURL(vnpParamsMap);
        logger.info("Generated VNPay payment URL: {}", fullPaymentUrl);

        return new ApiResponse<>(200, "Payment URL generated successfully", fullPaymentUrl);
    }


    public boolean verifyPayment(Map<String, String> vnpParams) throws IOException {
        String vnpSecureHash = vnpParams.get("vnp_SecureHash");
        vnpParams.remove("vnp_SecureHash");

        PaymentData paymentData = VNPayUtil.getPaymentData(vnpParams);

        // Tạo lại chữ ký để so sánh
        String calculatedHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), VNPayUtil.getPaymentURL(vnpParams));

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

    // Xây dựng các tham số cho VNPay và đảm bảo sắp xếp theo thứ tự alphabet
    private Map<String, String> buildVnPayParams(Orders order, BigDecimal amount, String bankCode) {
        Map<String, String> vnpParamsMap = new TreeMap<>(vnPayConfig.getVNPayConfig());

        // Chuyển đổi BigDecimal sang số nguyên (đơn vị nhỏ nhất)
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount.longValue()));
        vnpParamsMap.put("vnp_TxnRef", String.valueOf(order.getOrderId()));
        vnpParamsMap.put("vnp_OrderInfo", "Thanh toan commit fee cho don hang: " + order.getOrderId());
        vnpParamsMap.put("vnp_BankCode", bankCode);
        vnpParamsMap.put("vnp_IpAddr", "127.0.0.1");  // Cần thay bằng IP thực tế trong môi trường sản xuất

        return vnpParamsMap;
    }
}