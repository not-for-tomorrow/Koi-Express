package com.koi_express.service.order.price;

import com.koi_express.enums.KoiType;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class KoiPriceCalculator {

    private static final Logger logger = Logger.getLogger(KoiPriceCalculator.class.getName());

    public static double calculateTotalPrice(KoiType koiType, int quantity, double length) {
        validateInputs(quantity, length);

        double basePrice = getBasePrice(koiType, length);
        double quantityFactor = getQuantityFactor(quantity);

        double totalPrice = basePrice * quantityFactor * quantity;
        logger.info(String.format("Total price for %d %s koi: %.2f", quantity, koiType.name(), totalPrice));
        return totalPrice;
    }

    private static double getBasePrice(KoiType koiType, double length) {
        switch (koiType) {
            case KOI_NHAT_BAN:
                return calculateBasePriceForType(length, 50_000, 150_000, 250_000);
            case KOI_VIET_NAM:
                return calculateBasePriceForType(length, 50_000, 100_000, 150_000);
            case KOI_CHAU_AU:
                return calculateBasePriceForType(length, 120_000, 220_000, 450_000);
            default:
                throw new IllegalArgumentException("Invalid Koi Type");
        }
    }

    private static double calculateBasePriceForType(double length, double priceSmall, double priceMedium, double priceLarge) {
        if (length <= 30) {
            return priceSmall;
        } else if (length > 30 && length <= 50) {
            return priceMedium;
        } else {
            return priceLarge;
        }
    }

    private static double getQuantityFactor(int quantity) {
        int range = (quantity - 1) / 5;
        double factor = 1.0 + (range * 0.1);
        logger.info(String.format("Quantity factor for %d koi: %.2f", quantity, factor));
        return factor;
    }

    private static void validateInputs(int quantity, double length) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be greater than 0");
        }
    }
}
