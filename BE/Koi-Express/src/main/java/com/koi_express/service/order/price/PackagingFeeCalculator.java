package com.koi_express.service.order.price;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PackagingFeeCalculator {

    private static final Logger logger = LoggerFactory.getLogger(PackagingFeeCalculator.class);

    // Load packaging fees from configuration
    @Value("${packaging.fee.small:15000}")
    private BigDecimal smallFishFee;

    @Value("${packaging.fee.medium:150000}")
    private BigDecimal mediumFishFee;

    @Value("${packaging.fee.large:250000}")
    private BigDecimal largeFishFee;

    @Value("${packaging.threshold.small:30}")
    private BigDecimal smallFishThreshold;

    @Value("${packaging.threshold.medium:50}")
    private BigDecimal mediumFishThreshold;

    public BigDecimal calculateFee(int quantity, BigDecimal length) {
        validateInputs(quantity, length);

        BigDecimal totalFee;

        if (length.compareTo(smallFishThreshold) < 0) {
            totalFee = smallFishFee.multiply(BigDecimal.valueOf(quantity));
        } else if (length.compareTo(mediumFishThreshold) <= 0) {
            totalFee = mediumFishFee.multiply(BigDecimal.valueOf(quantity));
        } else {
            totalFee = largeFishFee.multiply(BigDecimal.valueOf(quantity));
        }

        logger.info("Quantity: {}, Length: {} cm, Total packaging fee: {} VND", quantity, length, totalFee);

        return totalFee.setScale(0, RoundingMode.HALF_UP);
    }

    private void validateInputs(int quantity, BigDecimal length) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (length.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Length must be greater than 0");
        }
    }
}
