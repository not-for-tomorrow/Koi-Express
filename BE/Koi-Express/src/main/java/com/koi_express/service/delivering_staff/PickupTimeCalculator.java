package com.koi_express.service.delivering_staff;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PickupTimeCalculator {

    @Value("${delivery.speed:60}")
    private BigDecimal averageSpeedKmPerHour;

    @Value("${delivery.buffer-time:1}")
    private long additionalTimeForDelaysInHours;

    public LocalDateTime calculatePickupTime(BigDecimal kilometers) {

        BigDecimal timeInHours = kilometers.divide(averageSpeedKmPerHour, 2, RoundingMode.HALF_UP);

        BigDecimal timeInMinutes = timeInHours.multiply(new BigDecimal("60.0"));

        return LocalDateTime.now().plusMinutes(timeInMinutes.longValue()).plusHours(additionalTimeForDelaysInHours);
    }
}
