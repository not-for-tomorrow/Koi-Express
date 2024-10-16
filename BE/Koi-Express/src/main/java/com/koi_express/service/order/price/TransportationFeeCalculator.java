package com.koi_express.service.order.price;

import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class TransportationFeeCalculator {

    private static final Logger logger = Logger.getLogger(TransportationFeeCalculator.class.getName());

    private static final double BASE_FEE_PER_KM = 5200; // VND per km
    private static final double FUEL_PRICE = 19000; // VND per liter
    private static final double SHORT_DISTANCE_FUEL_CONSUMPTION = 11.0; // Liters per 100 km for distances <= 300 km
    private static final double LONG_DISTANCE_FUEL_CONSUMPTION = 14.0; // Liters per 100 km for distances > 300 km

    public static double calculateTotalFee(double kilometers) {
        if (kilometers < 0) {
            throw new IllegalArgumentException("Distance cannot be negative");
        }

        if (kilometers == 0) {
            return 0.0;
        } else if (kilometers <= 1) {
            return BASE_FEE_PER_KM;
        }

        double fuelConsumption = getFuelConsumption(kilometers);
        double distanceFee = kilometers * BASE_FEE_PER_KM;
        double fuelCost = calculateFuelCost(kilometers, fuelConsumption);

        double totalFee = distanceFee + fuelCost;
        logger.info(String.format("Distance: %.2f km, Distance Fee: %.2f VND, Fuel Cost: %.2f VND, Total Fee: %.2f VND",
                kilometers, distanceFee, fuelCost, totalFee));

        return totalFee;
    }

    private static double getFuelConsumption(double kilometers) {
        return kilometers <= 300 ? SHORT_DISTANCE_FUEL_CONSUMPTION : LONG_DISTANCE_FUEL_CONSUMPTION;
    }

    private static double calculateFuelCost(double kilometers, double fuelConsumption) {
        return (kilometers / 100) * fuelConsumption * FUEL_PRICE;
    }

    public static double calculateCommitmentFee(double kilometers) {
        double totalFee = calculateTotalFee(kilometers);
        double commitmentFee = totalFee * 0.30;
        logger.info(String.format("Commitment Fee for %.2f km: %.2f VND", kilometers, commitmentFee));
        return commitmentFee;
    }
}
