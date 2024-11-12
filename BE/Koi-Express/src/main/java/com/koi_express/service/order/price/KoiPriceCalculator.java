package com.koi_express.service.order.price;

import java.math.BigDecimal;
import java.util.logging.Logger;

import com.koi_express.enums.KoiType;
import org.springframework.stereotype.Service;

@Service
public class KoiPriceCalculator {

    private static final Logger logger = Logger.getLogger(KoiPriceCalculator.class.getName());

    public static BigDecimal calculateTotalPrice(KoiType koiType, int quantity, BigDecimal koiSize) {
        validateInputs(quantity, koiSize);

        BigDecimal basePrice = getBasePrice(koiType, koiSize);
        BigDecimal quantityFactor = BigDecimal.ONE;

        if (quantity > 1) {
            quantityFactor = getQuantityFactor(quantity);
        }

        BigDecimal totalPrice = basePrice.multiply(quantityFactor).multiply(BigDecimal.valueOf(quantity));

        if (logger.isLoggable(java.util.logging.Level.INFO)) {
            logger.info(String.format("Total price for %d %s koi: %.2f", quantity, koiType.name(), totalPrice));
        }
        return totalPrice;
    }

    private static BigDecimal getBasePrice(KoiType koiType, BigDecimal koiSize) {
        return switch (koiType) {
            case KOI_NHAT_BAN -> calculateBasePriceForType(
                    koiSize.doubleValue(),
                    BigDecimal.valueOf(50_000),
                    BigDecimal.valueOf(150_000),
                    BigDecimal.valueOf(250_000));
            case KOI_VIET_NAM -> calculateBasePriceForType(
                    koiSize.doubleValue(),
                    BigDecimal.valueOf(50_000),
                    BigDecimal.valueOf(100_000),
                    BigDecimal.valueOf(150_000));
            case KOI_CHAU_AU -> calculateBasePriceForType(
                    koiSize.doubleValue(),
                    BigDecimal.valueOf(120_000),
                    BigDecimal.valueOf(220_000),
                    BigDecimal.valueOf(450_000));
        };
    }

    private static BigDecimal calculateBasePriceForType(
            double length, BigDecimal priceSmall, BigDecimal priceMedium, BigDecimal priceLarge) {
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

        if (logger.isLoggable(java.util.logging.Level.INFO)) {
            logger.info(String.format("Quantity factor for %d koi: %.2f", quantity, factor));
        }
        return factor;
    }

    private static void validateInputs(int quantity, BigDecimal koiSize) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (koiSize.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Koi size must be greater than 0");
        }
    }
}
