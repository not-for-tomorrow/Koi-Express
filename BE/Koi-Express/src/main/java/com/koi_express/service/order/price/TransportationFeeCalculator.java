package com.koi_express.service.order.price;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TransportationFeeCalculator {

    private static final Logger logger = LoggerFactory.getLogger(TransportationFeeCalculator.class);

    private final BigDecimal baseFeePerKm;
    private final BigDecimal fuelPrice;
    private final BigDecimal smallTruckFuelConsumption;
    private final BigDecimal mediumTruckFuelConsumption;
    private final BigDecimal largeTruckFuelConsumption;

    public TransportationFeeCalculator(
            @Value("${transportation.baseFeePerKm:5200}") BigDecimal baseFeePerKm,
            @Value("${transportation.fuelPrice:19000}") BigDecimal fuelPrice,
            @Value("${transportation.smallTruckFuelConsumption:9.0}") BigDecimal smallTruckFuelConsumption,
            @Value("${transportation.mediumTruckFuelConsumption:14.0}") BigDecimal mediumTruckFuelConsumption,
            @Value("${transportation.largeTruckFuelConsumption:21.0}") BigDecimal largeTruckFuelConsumption) {
        this.baseFeePerKm = baseFeePerKm;
        this.fuelPrice = fuelPrice;
        this.smallTruckFuelConsumption = smallTruckFuelConsumption;
        this.mediumTruckFuelConsumption = mediumTruckFuelConsumption;
        this.largeTruckFuelConsumption = largeTruckFuelConsumption;
    }

    public BigDecimal calculateDistanceFee(BigDecimal kilometers) {
        validateDistance(kilometers);

        if (kilometers.compareTo(BigDecimal.ZERO) == 0) {
            logger.info("Total Fee for 0 km: 0 VND");
            return BigDecimal.ZERO;
        }

        BigDecimal fuelConsumption = getFuelConsumption(kilometers);
        logger.info("Fuel Consumption for distance {} km: {} liters per 100 km", kilometers, fuelConsumption);

        BigDecimal distanceFee = kilometers.multiply(baseFeePerKm);
        logger.info("Distance Fee for {} km: {} VND", kilometers, distanceFee);

        BigDecimal fuelCost = kilometers
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                .multiply(fuelConsumption)
                .multiply(fuelPrice);

        logger.info("Fuel Cost for {} km: {} VND", kilometers, fuelCost);

        BigDecimal totalFee = distanceFee.add(fuelCost);
        logger.info("Total Fee for {} km: {} VND", kilometers, totalFee);

        BigDecimal roundedFee =
                totalFee.divide(BigDecimal.valueOf(1000), 0, RoundingMode.UP).multiply(BigDecimal.valueOf(1000));
        logger.info("Total Fee for {} km after rounding to nearest thousand: {} VND", kilometers, roundedFee);

        return roundedFee;
    }

    private BigDecimal getFuelConsumption(BigDecimal kilometers) {
        if (kilometers.compareTo(BigDecimal.valueOf(300)) < 0) {
            return smallTruckFuelConsumption;
        } else if (kilometers.compareTo(BigDecimal.valueOf(800)) < 0) {
            return mediumTruckFuelConsumption;
        } else {
            return largeTruckFuelConsumption;
        }
    }

    private void validateDistance(BigDecimal kilometers) {
        if (kilometers.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Distance cannot be negative");
        }
    }

    public BigDecimal calculateCommitmentFee(BigDecimal kilometers) {
        if (kilometers.compareTo(BigDecimal.TEN) < 0) {
            logger.info("Commitment fee not applicable for distances below 10 km.");
            return BigDecimal.ZERO;
        }

        BigDecimal totalFee = calculateDistanceFee(kilometers);
        BigDecimal commitmentFee = totalFee.multiply(BigDecimal.valueOf(0.30));
        logger.info("Commitment Fee for {} km: {} VND", kilometers, commitmentFee);
        return commitmentFee.setScale(0, RoundingMode.HALF_UP);
    }
}
