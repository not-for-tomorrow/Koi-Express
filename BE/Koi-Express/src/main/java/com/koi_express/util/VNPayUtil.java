package com.koi_express.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import jakarta.servlet.http.HttpServletRequest;

public class VNPayUtil {

    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }

            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] keyBytes = key.getBytes();
            final SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "HmacSHA512");
            hmac512.init(secretKeySpec);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate hash", e);
        }
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ipAdress;
        try {
            ipAdress = request.getHeader("X-Forwarded-For");
            if (ipAdress == null) {
                ipAdress = request.getRemoteAddr();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get IP address", e);
        }
        return ipAdress;
    }

    public static String getRandomNumber(int len) {
        Random rmd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rmd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static String getPaymentURL(Map<String, String> params, boolean encodeKey) {
        return params.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                .sorted(Map.Entry.comparingByKey())
                .map(entry ->
                        (encodeKey ? URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII) : entry.getKey())
                                + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII))
                .collect(Collectors.joining("&"));
    }
}