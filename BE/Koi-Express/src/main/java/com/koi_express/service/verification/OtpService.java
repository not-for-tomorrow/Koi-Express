package com.koi_express.service.verification;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.koi_express.dto.request.RegisterRequest;
import com.twilio.Twilio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

    private final String FROM_PHONE;
    private final Map<String, String> otpData = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();
    private final Map<String, RegisterRequest> tempRegisterData = new ConcurrentHashMap<>();
    private final Map<String, Long> otpTimestamps = new ConcurrentHashMap<>();
    private static final long OTP_EXPIRATION_TIME = 5 * 60 * 1000; // 5 minutes

    public OtpService(
            @Value("${spring.twilio.TWILIO_ACCOUNT_SID}") String accountSid,
            @Value("${spring.twilio.TWILIO_AUTH_TOKEN}") String authToken,
            @Value("${spring.twilio.TWILIO_FROM_PHONE}") String fromPhone) {

        Twilio.init(accountSid, authToken);
        this.FROM_PHONE = fromPhone;
    }

    public void saveOtp(String phoneNumber, String otp) {
        otpData.put(phoneNumber, otp);
        otpTimestamps.put(phoneNumber, System.currentTimeMillis());
    }

    public void sendOtp(String phoneNumber, String otp) {
        com.twilio.rest.api.v2010.account.Message.creator(
                        new com.twilio.type.PhoneNumber(phoneNumber),
                        new com.twilio.type.PhoneNumber(FROM_PHONE),
                        "Your OTP is: " + otp)
                .create();
    }

    public boolean validateOtp(String phoneNumber, String otp) {
        String formattedPhoneNumber = formatPhoneNumber(phoneNumber);
        String storedOtp = otpData.get(formattedPhoneNumber);
        Long timestamp = otpTimestamps.get(formattedPhoneNumber);

        if (storedOtp != null && otp.equals(storedOtp) && isOtpValid(timestamp)) {
            otpData.remove(formattedPhoneNumber); // Remove OTP once validated
            otpTimestamps.remove(formattedPhoneNumber);
            return true;
        }
        return false;
    }

    private boolean isOtpValid(Long timestamp) {
        long currentTime = System.currentTimeMillis();
        return (currentTime - timestamp) <= OTP_EXPIRATION_TIME;
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
