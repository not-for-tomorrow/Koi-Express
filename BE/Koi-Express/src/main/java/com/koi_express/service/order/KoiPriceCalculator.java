package com.koi_express.service.order;

import com.koi_express.enums.KoiType;

public class KoiPriceCalculator {

    public static double calculateTotalPrice(KoiType koiType, int quantity, double length, double weight) {
        double basePrice = getBasePrice(koiType, length, weight);
        double quantityFactor = getQuantityFactor(quantity);
        return basePrice * quantityFactor * quantity;
    }

    private static double getBasePrice(KoiType koiType, double length, double weight) {
        if (koiType == KoiType.KOI_NHAT_BAN) {
            if (length <= 30) {
                return weight <= 1.5 ? 100_000 : 150_000;
            } else if (length > 30 && length <= 50) {
                return weight <= 3 ? 200_000 : 250_000;
            } else if (length > 50) {
                return weight <= 5 ? 400_000 : 500_000;
            }
        } else if (koiType == KoiType.KOI_VIET_NAM) {
            if (length <= 30) {
                return weight <= 1.5 ? 50_000 : 75_000;
            } else if (length > 30 && length <= 50) {
                return weight <= 3 ? 100_000 : 150_000;
            } else if (length > 50) {
                return weight <= 5 ? 200_000 : 300_000;
            }
        } else if (koiType == KoiType.KOI_CHAU_AU) {
            if (length <= 30) {
                return weight <= 1.5 ? 120_000 : 170_000;
            } else if (length > 30 && length <= 50) {
                return weight <= 3 ? 220_000 : 270_000;
            } else if (length > 50) {
                return weight <= 5 ? 450_000 : 550_000;
            }
        }
        throw new IllegalArgumentException("Invalid Koi Type");
    }

    private static double getQuantityFactor(int quantity) {
        int range = (quantity - 1) / 5;
        return 1.0 + (range * 0.1);
    }

    /*    public static void main(String[] args) {
    	// Test the calculation
    	KoiType koiType = KoiType.KOI_CHAU_AU;
    	int quantity = 20;
    	double length = 56;
    	double weight = 4.5;

    	double totalPrice = calculateTotalPrice(koiType, quantity, length, weight);
    	System.out.printf("Total Price: %,.0f VND ", totalPrice);

    	// Example Calculation:
    	// Base Price for KOI_NHAT_BAN with length 35cm and weight 2.5kg is 200,000 VND.
    	// Quantity Factor for 12 fish is 1.2 (since it's in the range 11-15).
    	// Total Price = Base Price * Quantity Factor * Quantity
    	// Total Price = 200,000 * 1.2 * 12 = 2,880,000 VND
    }*/
}
