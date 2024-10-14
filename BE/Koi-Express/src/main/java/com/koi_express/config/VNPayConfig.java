package com.koi_express.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class VNPayConfig {

    @Value("${spring.vnpay.tmn-code}")
    private String tmnCode;

    @Value("${spring.vnpay.hash-secret}")
    private String hashSecret;

    @Value("${spring.vnpay.return-url}")
    private String returnUrl;

    @Value("${spring.vnpay.vnp-url}")
    private String vnpUrl;
}
