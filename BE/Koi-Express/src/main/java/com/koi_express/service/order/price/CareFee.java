package com.koi_express.service.order.price;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.koi_express.enums.KoiType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CareFee {

    private static final Logger logger = LoggerFactory.getLogger(CareFee.class);

    public enum Size {
        LESS_THAN_30_CM,
        SIZE_30_TO_50_CM,
        GREATER_THAN_50_CM
    }

    private final Map<String, BigDecimal> priceTable = new HashMap<>();

    public CareFee() {
        // Japan Koi Pricing
        priceTable.put("KOI_NHAT_BAN:LESS_THAN_30_CM", BigDecimal.valueOf(50000));
        priceTable.put("KOI_NHAT_BAN:SIZE_30_TO_50_CM", BigDecimal.valueOf(80000));
        priceTable.put("KOI_NHAT_BAN:GREATER_THAN_50_CM", BigDecimal.valueOf(120000));

        // Vietnam Koi Pricing
        priceTable.put("KOI_VIET_NAM:LESS_THAN_30_CM", BigDecimal.valueOf(30000));
        priceTable.put("KOI_VIET_NAM:SIZE_30_TO_50_CM", BigDecimal.valueOf(60000));
        priceTable.put("KOI_VIET_NAM:GREATER_THAN_50_CM", BigDecimal.valueOf(100000));

        // Europe Koi Pricing
        priceTable.put("KOI_CHAU_AU:LESS_THAN_30_CM", BigDecimal.valueOf(50000));
        priceTable.put("KOI_CHAU_AU:SIZE_30_TO_50_CM", BigDecimal.valueOf(80000));
        priceTable.put("KOI_CHAU_AU:GREATER_THAN_50_CM", BigDecimal.valueOf(120000));
    }

    public BigDecimal calculateCareFee(KoiType type, Size size, int quantity) {
        if (type == null || size == null) {
            throw new IllegalArgumentException("Invalid input: type or size is null");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        String key = String.format("%s:%s", type.name(), size.name());
        BigDecimal feePerFish = priceTable.get(key);

        if (feePerFish == null) {
            logger.warn("Price not found for combination: type={}, size={}", type, size);
            throw new IllegalArgumentException("Price not available for this combination");
        }

        // Multiply the fee per fish by the quantity using BigDecimal.multiply()
        BigDecimal totalFee = feePerFish.multiply(BigDecimal.valueOf(quantity));
        logger.info("Care fee for {} koi of type {} and size {}: {} VND", quantity, type, size, totalFee);
        return totalFee.setScale(0, BigDecimal.ROUND_HALF_UP);
    }
}
