package com.koi_express.service.Order;

import com.koi_express.dto.request.OrderRequest;
import com.koi_express.enums.PackingMethod;
import org.springframework.stereotype.Component;

@Component
public class OrderFeeCalculator {

    public double calculateTotalFee(OrderRequest orderRequest) {

        double BASE_PRICE_PER_KG = 10000;
        double INSURANCE_COST_FER_FISH = 50000;
        double SPECIAL_CARE_COST_FER_FISH = 100000;
        double HEALTH_CHECK_COST_FER_FISH = 50000;
        double TAX_RATE = 0.05;
        double BASIC_PACKAGING_COST_FER_FISH = 50000;
        double SPECIAL_PACKAGING_COST_FER_FISH = 100000;
        double FUEL_COST_PER_KM = 10000;
z
        int quantity = orderRequest.getKoiQuantity();
        double weightFee = orderRequest.getKoiQuantity();
        double distance  = calculateDistance(orderRequest.getOriginLocation(), orderRequest.getDestinationLocation());
        boolean isInsurance = orderRequest.isInsurance();
        boolean isSpecialCare = orderRequest.isSpecialCare();
        boolean isHealthCheck = orderRequest.isHealthCheck();
        PackingMethod packingMethod = orderRequest.getPackingMethod();

        double totalFee = 0;

        double basePrice = weightFee * BASE_PRICE_PER_KG;
        totalFee += basePrice;


        if(isInsurance) {
            totalFee += INSURANCE_COST_FER_FISH;
        }

        if(isSpecialCare) {
            totalFee += SPECIAL_CARE_COST_FER_FISH;
        }

        if(isHealthCheck) {
            totalFee += HEALTH_CHECK_COST_FER_FISH;
        }

        if(packingMethod == PackingMethod.NORMAL_PACKAGING) {
            totalFee += BASIC_PACKAGING_COST_FER_FISH;
        } else if(packingMethod == PackingMethod.SPECIAL_PACKAGING) {
            totalFee += SPECIAL_PACKAGING_COST_FER_FISH;
        }

        double tax = totalFee * TAX_RATE;
        totalFee += tax;

        double fuelCost = calculateFuelCost(distance);
        totalFee += fuelCost;

        return totalFee;
    }

    private double calculateFuelCost(double distance) {
        double FUEL_COST_PER_KM = 10000;
        return distance * FUEL_COST_PER_KM;
    }

    private double calculateDistance(String originLocation, String destinationLocation) {
        return 0;
    }
}
