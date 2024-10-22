package com.koi_express.service.order.price;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

@Component
public class TransportationFeeCalculator {

    private static final Logger logger = Logger.getLogger(TransportationFeeCalculator.class.getName());

    private static final BigDecimal BASE_FEE_PER_KM = new BigDecimal("5200");
    private static final BigDecimal FUEL_PRICE = new BigDecimal("19000");
    private static final BigDecimal SHORT_DISTANCE_FUEL_CONSUMPTION = new BigDecimal("11.0");
    private static final BigDecimal LONG_DISTANCE_FUEL_CONSUMPTION = new BigDecimal("14.0");

    public static BigDecimal calculateTotalFee(BigDecimal kilometers) {
        if (kilometers.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Distance cannot be negative");
        }

        if (kilometers.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        } else if (kilometers.compareTo(BigDecimal.ONE) <= 0) {
            return BASE_FEE_PER_KM.setScale(0, RoundingMode.HALF_UP);
        }

        BigDecimal fuelConsumption = getFuelConsumption(kilometers);
        BigDecimal distanceFee = kilometers.multiply(BASE_FEE_PER_KM);
        BigDecimal fuelCost = calculateFuelCost(kilometers, fuelConsumption);

        BigDecimal totalFee = distanceFee.add(fuelCost);
        logger.info(String.format(
                "Distance: %.2f km, Distance Fee: %.2f VND, Fuel Cost: %.2f VND, Total Fee: %.2f VND",
                kilometers, distanceFee, fuelCost, totalFee));

        return totalFee.setScale(0, RoundingMode.HALF_UP);
    }

    // Xác định mức tiêu thụ nhiên liệu dựa trên khoảng cách
    private static BigDecimal getFuelConsumption(BigDecimal kilometers) {
        return kilometers.compareTo(BigDecimal.valueOf(300)) <= 0
                ? SHORT_DISTANCE_FUEL_CONSUMPTION
                : LONG_DISTANCE_FUEL_CONSUMPTION;
    }

    // Tính toán chi phí nhiên liệu
    private static BigDecimal calculateFuelCost(BigDecimal kilometers, BigDecimal fuelConsumption) {
        return kilometers
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                .multiply(fuelConsumption)
                .multiply(FUEL_PRICE)
                .setScale(0, RoundingMode.HALF_UP);
    }

    // Tính phí cam kết dựa trên tổng phí
    public static BigDecimal calculateCommitmentFee(BigDecimal kilometers) {
        BigDecimal totalFee = calculateTotalFee(kilometers);
        BigDecimal commitmentFee = totalFee.multiply(BigDecimal.valueOf(0.30));
        logger.info(String.format("Commitment Fee for %.2f km: %.2f VND", kilometers, commitmentFee));
        return commitmentFee.setScale(0, RoundingMode.HALF_UP);
    }
}
