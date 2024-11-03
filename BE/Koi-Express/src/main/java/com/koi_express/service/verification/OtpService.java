package com.koi_express.service.verification;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.koi_express.dto.request.RegisterRequest;
import com.twilio.Twilio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);

    private final String fromPhone;
    private final Map<String, String> otpData = new ConcurrentHashMap<>();
    private final Map<String, RegisterRequest> tempRegisterData = new ConcurrentHashMap<>();
    private final Map<String, Long> otpTimestamps = new ConcurrentHashMap<>();
    private static final long OTP_EXPIRATION_TIME = 5 * 60 * 1000L; // 5 minutes

    public OtpService(
            @Value("${spring.twilio.TWILIO_ACCOUNT_SID}") String accountSid,
            @Value("${spring.twilio.TWILIO_AUTH_TOKEN}") String authToken,
            @Value("${spring.twilio.TWILIO_FROM_PHONE}") String fromPhone) {

        Twilio.init(accountSid, authToken);
        this.fromPhone = fromPhone;
        logger.info("OtpService initialized with fromPhone: {}", this.fromPhone);
    }

    public void saveOtp(String phoneNumber, String otp) {
        String formattedPhoneNumber = formatPhoneNumber(phoneNumber);
        otpData.put(formattedPhoneNumber, otp);
        otpTimestamps.put(formattedPhoneNumber, System.currentTimeMillis());

        logger.debug("Saved OTP for phone number {}: {}", formattedPhoneNumber, otp);
    }

    public boolean sendOtp(String phoneNumber, String otp) {
        String formattedPhoneNumber = formatPhoneNumber(phoneNumber);
        try {
            com.twilio.rest.api.v2010.account.Message.creator(
                            new com.twilio.type.PhoneNumber(formattedPhoneNumber),
                            new com.twilio.type.PhoneNumber(fromPhone),
                            "Your OTP is: " + otp)
                    .create();
            logger.info("Sent OTP to phone number {}", maskPhoneNumber(formattedPhoneNumber));
            return true;
        } catch (Exception e) {
            logger.error("Failed to send OTP to {}. Error: {}", maskPhoneNumber(formattedPhoneNumber), e.getMessage());
            return false;
        }
    }

    private String maskPhoneNumber(String phoneNumber) {
        return phoneNumber.replaceAll("(\\+84)(\\d{2})(\\d+)(\\d{2})", "$1$2****$4");
    }

    public boolean validateOtp(String phoneNumber, String otp) {
        String formattedPhoneNumber = formatPhoneNumber(phoneNumber);
        Long timestamp = otpTimestamps.get(formattedPhoneNumber);

        if (timestamp == null || !isOtpValid(timestamp)) {
            logger.warn("OTP expired or not found for phone number {}", formattedPhoneNumber);
            otpData.remove(formattedPhoneNumber);
            otpTimestamps.remove(formattedPhoneNumber);
            return false;
        }

        String storedOtp = otpData.get(formattedPhoneNumber);
        if (otp.equals(storedOtp)) {
            otpData.remove(formattedPhoneNumber);
            otpTimestamps.remove(formattedPhoneNumber);
            logger.info("OTP validated successfully for phone number {}", formattedPhoneNumber);
            return true;
        }

        logger.warn("Failed OTP validation for phone number {}. Incorrect OTP", formattedPhoneNumber);
        return false;
    }

    private boolean isOtpValid(Long timestamp) {
        long currentTime = System.currentTimeMillis();
        boolean isValid = (currentTime - timestamp) <= OTP_EXPIRATION_TIME;
        if (!isValid) {
            logger.debug("OTP expired. Current time: {}, OTP timestamp: {}", currentTime, timestamp);
        }
        return isValid;
    }

    public String formatPhoneNumber(String phoneNumber) {
        if (!phoneNumber.startsWith("+84")) {
            if (phoneNumber.startsWith("0")) {
                phoneNumber = "+84" + phoneNumber.substring(1);
            } else {
                phoneNumber = "+84" + phoneNumber;
            }
        }
        logger.debug("Final formatted phone number: {}", phoneNumber);
        return phoneNumber;
    }

    public void saveTempRegisterRequest(RegisterRequest registerRequest) {
        String formattedPhoneNumber = formatPhoneNumber(registerRequest.getPhoneNumber());
        tempRegisterData.put(formattedPhoneNumber, registerRequest);
        logger.info("Saved temp register request for {}", formattedPhoneNumber);
    }

    public RegisterRequest getTempRegisterRequest(String phoneNumber) {
        RegisterRequest request = tempRegisterData.get(phoneNumber);
        if (request != null) {
            logger.debug("Retrieved temp register request for {}", phoneNumber);
        } else {
            logger.warn("No temp register request found for {}", phoneNumber);
        }
        return request;
    }
}
