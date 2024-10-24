package com.koi_express.service.deliveringStaff;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class PickupTimeCalculator {

    private static final BigDecimal AVERAGE_SPEED_KM_PER_HOUR = new BigDecimal("60.0"); // Updated speed as BigDecimal
    private static final long ADDITIONAL_TIME_FOR_DELAYS_IN_HOURS = 1;

    public static LocalDateTime calculatePickupTime(BigDecimal kilometers) {

        BigDecimal timeInHours = kilometers.divide(AVERAGE_SPEED_KM_PER_HOUR, 2, RoundingMode.HALF_UP);

        BigDecimal timeInMinutes = timeInHours.multiply(new BigDecimal("60.0"));

        LocalDateTime estimatedPickupTime = LocalDateTime.now()
                .plusMinutes(timeInMinutes.longValue())
                .plusHours(ADDITIONAL_TIME_FOR_DELAYS_IN_HOURS);

        return estimatedPickupTime;
    }
}
