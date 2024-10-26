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
    private final BigDecimal shortDistanceFuelConsumption;
    private final BigDecimal longDistanceFuelConsumption;

    public TransportationFeeCalculator(
            @Value("${transportation.baseFeePerKm:5200}") BigDecimal baseFeePerKm,
            @Value("${transportation.fuelPrice:19000}") BigDecimal fuelPrice,
            @Value("${transportation.shortDistanceFuelConsumption:11.0}") BigDecimal shortDistanceFuelConsumption,
            @Value("${transportation.longDistanceFuelConsumption:14.0}") BigDecimal longDistanceFuelConsumption) {
        this.baseFeePerKm = baseFeePerKm;
        this.fuelPrice = fuelPrice;
        this.shortDistanceFuelConsumption = shortDistanceFuelConsumption;
        this.longDistanceFuelConsumption = longDistanceFuelConsumption;
    }

    public BigDecimal calculateTotalFee(BigDecimal kilometers) {
        validateDistance(kilometers);

        if (kilometers.compareTo(BigDecimal.ZERO) == 0) {
            logger.info("Total Fee for 0 km: 0 VND");
            return BigDecimal.ZERO;
        }

        BigDecimal fuelConsumption = getFuelConsumption(kilometers);
        logger.info("Fuel Consumption for distance {} km: {} liters per 100 km", kilometers, fuelConsumption);

        BigDecimal distanceFee = kilometers.multiply(baseFeePerKm);
        logger.info("Distance Fee for {} km: {} VND", kilometers, distanceFee);

        BigDecimal fuelCost = calculateFuelCost(kilometers, fuelConsumption);
        logger.info("Fuel Cost for {} km: {} VND", kilometers, fuelCost);

        BigDecimal totalFee = distanceFee.add(fuelCost);
        logger.info("Total Fee for {} km: {} VND", kilometers, totalFee);

        return totalFee.setScale(0, RoundingMode.HALF_UP);
    }

    private BigDecimal getFuelConsumption(BigDecimal kilometers) {
        return kilometers.compareTo(BigDecimal.valueOf(300)) <= 0
                ? shortDistanceFuelConsumption
                : longDistanceFuelConsumption;
    }

    private BigDecimal calculateFuelCost(BigDecimal kilometers, BigDecimal fuelConsumption) {
        BigDecimal fuelCost = kilometers
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                .multiply(fuelConsumption)
                .multiply(fuelPrice);
        return fuelCost.setScale(0, RoundingMode.HALF_UP);
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

        BigDecimal totalFee = calculateTotalFee(kilometers);
        BigDecimal commitmentFee = totalFee.multiply(BigDecimal.valueOf(0.30));
        logger.info("Commitment Fee for {} km: {} VND", kilometers, commitmentFee);
        return commitmentFee.setScale(0, RoundingMode.HALF_UP);
    }

}
