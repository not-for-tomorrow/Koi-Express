package com.koi_express.service.payment;

import com.koi_express.config.VNPayConfig;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Service
public class VNPayService {

    @Autowired
    private VNPayConfig vnPayConfig;

    public String createPaymentUrl(String orderId, Long amount, String customerName, String bankCode) {

        if (orderId == null || customerName == null || bankCode == null) {
            throw new IllegalArgumentException("Order ID, Customer Name, and Bank Code cannot be null.");
        }

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", vnPayConfig.getTmnCode());
        vnpParams.put("vnp_Amount", String.valueOf(amount * 100)); // Nhân với 100
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", orderId);
        vnpParams.put("vnp_OrderInfo", customerName + " - Phí cam kết sử dụng dịch vụ");
        vnpParams.put("vnp_OrderType", "billpayment");
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
        vnpParams.put("vnp_BankCode", bankCode);
        vnpParams.put("vnp_CreateDate", getCurrentTimeStamp());

        // Xâu chuỗi tham số theo thứ tự
        StringBuilder hashData = new StringBuilder();
        StringBuilder queryString = new StringBuilder();
        try {
            vnpParams.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        if (entry.getValue() != null) { // Check if value is not null
                            try {
                                queryString.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                                        .append("=")
                                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                                        .append("&");
                                hashData.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                                        .append("=")
                                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                                        .append("&");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }

        queryString.deleteCharAt(queryString.length() - 1);
        hashData.deleteCharAt(hashData.length() - 1);

        // Tạo secure hash
        String secureHash = HmacUtils.hmacSha512Hex(vnPayConfig.getHashSecret(), hashData.toString());
        return vnPayConfig.getVnpUrl() + "?" + queryString.toString() + "&vnp_SecureHash=" + secureHash;
    }

    private String getCurrentTimeStamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(TimeZone.getTimeZone("Etc/GMT+7"));
        return formatter.format(new Date());
    }
}
