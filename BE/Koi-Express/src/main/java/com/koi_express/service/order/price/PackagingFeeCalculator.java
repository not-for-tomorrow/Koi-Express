package com.koi_express.service.order.price;

import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class PackagingFeeCalculator {

    private static final Logger logger = Logger.getLogger(PackagingFeeCalculator.class.getName());

    private static final int SMALL_FISH_FEE = 15000; // VND per fish for fish < 30 cm
    private static final int MEDIUM_FISH_FEE = 150000; // VND per fish for fish between 30-50 cm
    private static final int LARGE_FISH_FEE = 250000; // VND per fish for fish > 50 cm
    private static final int SMALL_FISH_THRESHOLD = 30; // fish size < 30 cm
    private static final int MEDIUM_FISH_THRESHOLD = 50; // fish size between 30-50 cm

    public int calculateTotalPackagingFee(int quantity, double length) {
        int totalFee = 0;

        if (length <= 0) {
            logger.warning("Invalid fish dimensions. Length and weight must be greater than 0.");
            throw new IllegalArgumentException("Invalid fish dimensions. Length and weight must be greater than 0.");
        }

        if (length < SMALL_FISH_THRESHOLD) {
            totalFee += quantity * SMALL_FISH_FEE;
        } else if (length <= MEDIUM_FISH_THRESHOLD) {
            totalFee += quantity * MEDIUM_FISH_FEE;
        } else {
            totalFee += quantity * LARGE_FISH_FEE;
        }

        logger.info(String.format("Quantity: %d, Length: %.2f cm, Weight: %.2f kg", quantity, length));
        logger.info("Total packaging fee: " + totalFee + " VND");

        return totalFee;
    }
}
