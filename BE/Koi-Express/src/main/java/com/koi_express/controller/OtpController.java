package com.koi_express.controller;

import com.koi_express.service.OtpService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/send")
    public String sendOtp(@RequestParam String phoneNumber) {
        String otp = otpService.generateOtp();
        otpService.sendOtp(phoneNumber, otp);
        return "OTP sent successfully";
    }

}
