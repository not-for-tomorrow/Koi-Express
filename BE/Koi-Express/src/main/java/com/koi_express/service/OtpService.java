package com.koi_express.service;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Random;

@Service
public class OtpService {

    @Value("${twilio.phone.number}")
    private  String fromPhoneNumber;

    public void sendOtp(String toPhoneNumber, String otp) {

        String formattedPhoneNumber = formatPhoneNumber(toPhoneNumber);

        Message.creator(
                new PhoneNumber(formattedPhoneNumber),
                new PhoneNumber(fromPhoneNumber),
                "Your OTP is: " + otp

        ).create();

        System.out.println("Sending OTP " + otp + " to phone number " + toPhoneNumber);
    }

    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    private String formatPhoneNumber(String phoneNumber) {
        if(phoneNumber.startsWith("0")) {
            return "+84" + phoneNumber.substring(1);
        }
        return phoneNumber;
    }
}
