package com.koi_express.service.order.price;

import com.koi_express.enums.KoiType;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class KoiPriceCalculator {

    private static final Logger logger = Logger.getLogger(KoiPriceCalculator.class.getName());

    public static double calculateTotalPrice(KoiType koiType, int quantity, double length, double weight) {
        validateInputs(quantity, length, weight);

        double basePrice = getBasePrice(koiType, length, weight);
        double quantityFactor = getQuantityFactor(quantity);

        double totalPrice = basePrice * quantityFactor * quantity;
        logger.info(String.format("Total price for %d %s koi: %.2f", quantity, koiType.name(), totalPrice));
        return totalPrice;
    }

    private static double getBasePrice(KoiType koiType, double length, double weight) {
        switch (koiType) {
            case KOI_NHAT_BAN:
                return calculateBasePriceForType(length, weight, 50_000, 150_000, 200_000, 250_000, 400_000, 500_000);
            case KOI_VIET_NAM:
                return calculateBasePriceForType(length, weight, 50_000, 75_000, 100_000, 150_000, 200_000, 300_000);
            case KOI_CHAU_AU:
                return calculateBasePriceForType(length, weight, 120_000, 170_000, 220_000, 270_000, 450_000, 550_000);
            default:
                throw new IllegalArgumentException("Invalid Koi Type");
        }
    }

    private static double calculateBasePriceForType(double length, double weight,
                                                    double priceSmallLight, double priceSmallHeavy,
                                                    double priceMediumLight, double priceMediumHeavy,
                                                    double priceLargeLight, double priceLargeHeavy) {
                        if (length <= 30) {
                            return weight <= 1.5 ? priceSmallLight : priceSmallHeavy;
                        } else if (length > 30 && length <= 50) {
                            return weight <= 3 ? priceMediumLight : priceMediumHeavy;
                        } else {
                            return weight <= 5 ? priceLargeLight : priceLargeHeavy;
                        }
                    }

    private static double getQuantityFactor(int quantity) {
        int range = (quantity - 1) / 5;
        double factor = 1.0 + (range * 0.1);
        logger.info(String.format("Quantity factor for %d koi: %.2f", quantity, factor));
        return factor;
    }

    private static void validateInputs(int quantity, double length, double weight) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (length <= 0 || weight <= 0) {
            throw new IllegalArgumentException("Length and weight must be greater than 0");
        }
    }

}
