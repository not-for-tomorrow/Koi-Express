package com.koi_express.controller;

import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
public class VNPayIPNController {

    @PostMapping("/ipn")
    public String handleIPN(@RequestParam Map<String, String> params) {

        String secureHash = params.get("vnp_SecureHash");

        params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        String hashData = params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((p1, p2) -> p1 + "&" + p2)
                .orElse("");

        String calculatedHash = HmacUtils.hmacSha512Hex("FFFXMZ8PS02VN9YOZSMLWCWDDF5GOCPG", hashData);

        if(secureHash.equals(calculatedHash)) {
            // Cập nhật trạng thái thanh toán vào hệ thống
            return "{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}";
        } else {
            return "{\"RspCode\":\"97\",\"Message\":\"Invalid Checksum\"}";
        }

    }
}
