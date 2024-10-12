package com.koi_express.service.order;

public class TransportationFeeCalculator {

    public static double calculateTotalFee(double kilometers) {
        if (kilometers < 0) {
            throw new IllegalArgumentException("Distance cannot be negative");
        }

        if (kilometers == 0) {
            return 0.0;  // No fee for zero distance
        } else if (kilometers <= 1) {
            return 5200;  // Flat fee for distances up to 1 km
        }

        double feePerKm = 5200;
        double fuelPrice = 19000;
        double fuelConsumption;

        if (kilometers <= 300) {
            // Small truck logic: 11 liters per 100 km
            fuelConsumption = 11.0;
        } else {
            // Large truck logic: 14 liters per 100 km
            fuelConsumption = 14.0;
        }

        // Calculate the total transportation fee
        double distanceFee = kilometers * feePerKm;
        double fuelCost = (kilometers / 100) * fuelConsumption * fuelPrice;

        return distanceFee + fuelCost;
    }

    public static double calculateCommitmentFee(double kilometers) {
        double totalFee = calculateTotalFee(kilometers);
        return totalFee * 0.30;  // 30% commitment fee
    }
}
