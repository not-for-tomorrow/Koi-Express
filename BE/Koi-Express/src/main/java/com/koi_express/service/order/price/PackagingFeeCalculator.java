package com.koi_express.service.order.price;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.logging.Logger;

@Component
public class PackagingFeeCalculator {

    private static final Logger logger = Logger.getLogger(PackagingFeeCalculator.class.getName());

    private static final BigDecimal SMALL_FISH_FEE = BigDecimal.valueOf(15000); // VND per fish for fish < 30 cm
    private static final BigDecimal MEDIUM_FISH_FEE = BigDecimal.valueOf(150000); // VND per fish for fish between 30-50 cm
    private static final BigDecimal LARGE_FISH_FEE = BigDecimal.valueOf(250000); // VND per fish for fish > 50 cm
    private static final BigDecimal SMALL_FISH_THRESHOLD = BigDecimal.valueOf(30); // fish size < 30 cm
    private static final BigDecimal MEDIUM_FISH_THRESHOLD = BigDecimal.valueOf(50); // fish size between 30-50 cm

    public BigDecimal calculateTotalPackagingFee(int quantity, BigDecimal length) {
        if (length.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warning("Invalid fish dimensions. Length must be greater than 0.");
            throw new IllegalArgumentException("Invalid fish dimensions. Length must be greater than 0.");
        }

        BigDecimal totalFee;

        if (length.compareTo(SMALL_FISH_THRESHOLD) < 0) {
            totalFee = SMALL_FISH_FEE.multiply(BigDecimal.valueOf(quantity));
        } else if (length.compareTo(MEDIUM_FISH_THRESHOLD) <= 0) {
            totalFee = MEDIUM_FISH_FEE.multiply(BigDecimal.valueOf(quantity));
        } else {
            totalFee = LARGE_FISH_FEE.multiply(BigDecimal.valueOf(quantity));
        }

        logger.info(String.format("Quantity: %d, Length: %.2f cm", quantity, length));
        logger.info("Total packaging fee: " + totalFee + " VND");

        return totalFee.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
