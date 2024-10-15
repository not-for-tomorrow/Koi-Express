package com.koi_express.service.order;

public class TransportationFeeCalculator {

    public static double calculateTotalFee(double kilometers) {
        if (kilometers < 0) {
            throw new IllegalArgumentException("Distance cannot be negative");
        }

        if (kilometers == 0) {
            return 0.0;
        } else if (kilometers <= 1) {
            return 5200;
        }

        double feePerKm = 5200;
        double fuelPrice = 19000;
        double fuelConsumption;

        if (kilometers <= 300) {

            fuelConsumption = 11.0;
        } else {

            fuelConsumption = 14.0;
        }

        double distanceFee = kilometers * feePerKm;
        double fuelCost = (kilometers / 100) * fuelConsumption * fuelPrice;

        return distanceFee + fuelCost;
    }

    public static double calculateCommitmentFee(double kilometers) {
        double totalFee = calculateTotalFee(kilometers);
        return totalFee * 0.30;
    }
}
