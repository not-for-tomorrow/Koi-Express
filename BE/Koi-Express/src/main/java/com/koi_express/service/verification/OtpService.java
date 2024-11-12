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
    private final Map<String, Map<String, String>> otpDataByPurpose = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Long>> otpTimestampsByPurpose = new ConcurrentHashMap<>();
    private final Map<String, RegisterRequest> tempRegisterData = new ConcurrentHashMap<>();
    private static final long OTP_EXPIRATION_TIME = 5 * 60 * 1000L; // 5 minutes

    public OtpService(
            @Value("${spring.twilio.TWILIO_ACCOUNT_SID}") String accountSid,
            @Value("${spring.twilio.TWILIO_AUTH_TOKEN}") String authToken,
            @Value("${spring.twilio.TWILIO_FROM_PHONE}") String fromPhone) {

        Twilio.init(accountSid, authToken);
        this.fromPhone = fromPhone;
        logger.info("OtpService initialized with fromPhone: {}", this.fromPhone);
    }

    public String generateOtpForPurpose(String phoneNumber, String purpose) {
        String otp = String.format("%04d", new java.security.SecureRandom().nextInt(10000));
        String formattedPhoneNumber = formatPhoneNumber(phoneNumber);

        otpDataByPurpose
                .computeIfAbsent(purpose, k -> new ConcurrentHashMap<>())
                .put(formattedPhoneNumber, otp);
        otpTimestampsByPurpose
                .computeIfAbsent(purpose, k -> new ConcurrentHashMap<>())
                .put(formattedPhoneNumber, System.currentTimeMillis());

        logger.debug("Generated OTP for {} purpose: phoneNumber {}, otp {}", purpose, formattedPhoneNumber, otp);
        return otp;
    }

    public void sendOtp(String phoneNumber, String otp) {
        String formattedPhoneNumber = formatPhoneNumber(phoneNumber);
        try {
            com.twilio.rest.api.v2010.account.Message.creator(
                            new com.twilio.type.PhoneNumber(formattedPhoneNumber),
                            new com.twilio.type.PhoneNumber(fromPhone),
                            "Your OTP is: " + otp)
                    .create();
            logger.info("Sent OTP to phone number {}", maskPhoneNumber(formattedPhoneNumber));
        } catch (Exception e) {
            logger.error("Failed to send OTP to {}. Error: {}", maskPhoneNumber(formattedPhoneNumber), e.getMessage());
        }
    }

    public boolean validateOtpForPurpose(String phoneNumber, String otp, String purpose) {
        String formattedPhoneNumber = formatPhoneNumber(phoneNumber);
        Map<String, String> otpDataForPurpose = otpDataByPurpose.get(purpose);
        Map<String, Long> otpTimestampsForPurpose = otpTimestampsByPurpose.get(purpose);

        if (otpDataForPurpose == null || otpTimestampsForPurpose == null) {
            logger.warn("No OTP data found for purpose {}", purpose);
            return false;
        }

        Long timestamp = otpTimestampsForPurpose.get(formattedPhoneNumber);

        if (timestamp == null || !isOtpValid(timestamp)) {
            logger.warn("OTP expired or not found for phone number {}", formattedPhoneNumber);
            otpDataForPurpose.remove(formattedPhoneNumber);
            otpTimestampsForPurpose.remove(formattedPhoneNumber);
            return false;
        }

        String storedOtp = otpDataForPurpose.get(formattedPhoneNumber);
        if (otp.equals(storedOtp)) {
            otpDataForPurpose.remove(formattedPhoneNumber);
            otpTimestampsForPurpose.remove(formattedPhoneNumber);
            logger.info("OTP validated successfully for phone number {} for purpose {}", formattedPhoneNumber, purpose);
            return true;
        }

        logger.warn(
                "Failed OTP validation for phone number {} for purpose {}. Incorrect OTP",
                formattedPhoneNumber,
                purpose);
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
        phoneNumber = phoneNumber.replaceAll("\\s+", "");
        if (phoneNumber.startsWith("+84")) {
            phoneNumber = "0" + phoneNumber.substring(3);
        } else if (!phoneNumber.startsWith("0")) {
            phoneNumber = "0" + phoneNumber;
        }
        logger.debug("Final formatted phone number for database: {}", phoneNumber);
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

    private String maskPhoneNumber(String phoneNumber) {
        return phoneNumber.replaceAll("(\\+84)(\\d{2})(\\d+)(\\d{2})", "$1$2****$4");
    }
}
