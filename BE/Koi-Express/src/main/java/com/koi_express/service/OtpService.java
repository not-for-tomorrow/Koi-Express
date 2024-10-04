package com.koi_express.service;

import com.koi_express.dto.request.RegisterRequest;
import com.twilio.Twilio;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

@Service
public class OtpService {

    private static final String ACCOUNT_SID = "AC6ebe5c4cf2fb07c85783a6dbee771aae";
    private static final String AUTH_TOKEN = "e21dffb2f6f8a50878aabe5fab8b2806";
    private static final String FROM_PHONE = "+13182521661";

    private Map<String, String> otpData = new HashMap<>();
    private SecureRandom random = new SecureRandom();
    private Map<String, RegisterRequest> tempRegisterData = new HashMap<>();

    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public String generateOtp(String phoneNumber) {

        String otp = String.format("%04d", random.nextInt(10000));
        otpData.put(phoneNumber, otp);
        System.out.println("Generated OTP for " + phoneNumber + ": " + otp);
        return otp;
    }

    public void sendOtp(String phoneNumber) {

        String formattedPhoneNumber = formatPhoneNumber(phoneNumber);

        String otp = generateOtp(formattedPhoneNumber);

        com.twilio.rest.api.v2010.account.Message.creator(
                new com.twilio.type.PhoneNumber(phoneNumber),
                new com.twilio.type.PhoneNumber(FROM_PHONE),
                "Your OTP is: " + otp
        ).create();
    }

    public boolean validateOtp(String phoneNumber, String otp) {
        return otp.equals(otpData.get(phoneNumber));
    }

    public String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber.startsWith("0")) {
            phoneNumber = "+84" + phoneNumber.substring(1);
        }
        return phoneNumber;
    }

    public void saveTempRegisterRequest(RegisterRequest registerRequest) {
        tempRegisterData.put(registerRequest.getPhoneNumber(), registerRequest);
        System.out.println("Saved temp register request for " + registerRequest.getPhoneNumber());
    }

    public RegisterRequest getTempRegisterRequest(String phoneNumber) {
        return tempRegisterData.get(phoneNumber);
    }

}
