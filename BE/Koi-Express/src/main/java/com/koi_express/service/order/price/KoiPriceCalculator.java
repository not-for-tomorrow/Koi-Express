package com.koi_express.service.order.price;

import com.koi_express.enums.KoiType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.logging.Logger;

@Component
public class KoiPriceCalculator {

    private static final Logger logger = Logger.getLogger(KoiPriceCalculator.class.getName());

    public static BigDecimal calculateTotalPrice(KoiType koiType, int quantity, double length) {
        validateInputs(quantity, length);

        BigDecimal basePrice = getBasePrice(koiType, length);
        BigDecimal quantityFactor = getQuantityFactor(quantity);

        // Calculate total price using BigDecimal.multiply()
        BigDecimal totalPrice = basePrice.multiply(quantityFactor).multiply(BigDecimal.valueOf(quantity));

        logger.info(String.format("Total price for %d %s koi: %.2f", quantity, koiType.name(), totalPrice));
        return totalPrice;
    }

    private static BigDecimal getBasePrice(KoiType koiType, double length) {
        switch (koiType) {
            case KOI_NHAT_BAN:
                return calculateBasePriceForType(length, BigDecimal.valueOf(50_000), BigDecimal.valueOf(150_000), BigDecimal.valueOf(250_000));
            case KOI_VIET_NAM:
                return calculateBasePriceForType(length, BigDecimal.valueOf(50_000), BigDecimal.valueOf(100_000), BigDecimal.valueOf(150_000));
            case KOI_CHAU_AU:
                return calculateBasePriceForType(length, BigDecimal.valueOf(120_000), BigDecimal.valueOf(220_000), BigDecimal.valueOf(450_000));
            default:
                throw new IllegalArgumentException("Invalid Koi Type");
        }
    }

    private static BigDecimal calculateBasePriceForType(double length, BigDecimal priceSmall, BigDecimal priceMedium, BigDecimal priceLarge) {
        if (length <= 30) {
            return priceSmall;
        } else if (length > 30 && length <= 50) {
            return priceMedium;
        } else {
            return priceLarge;
        }
    }

    private static BigDecimal getQuantityFactor(int quantity) {
        int range = (quantity - 1) / 5;
        BigDecimal factor = BigDecimal.valueOf(1.0 + (range * 0.1));
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
