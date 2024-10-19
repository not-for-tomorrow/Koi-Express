package com.koi_express.service.order.price;

import com.koi_express.enums.KoiType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Component
public class CareFee {

    private static final Logger logger = Logger.getLogger(CareFee.class.getName());

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
            logger.warning(String.format("Price not found for combination: %s", key));
            throw new IllegalArgumentException("Price not available for this combination");
        }

        // Multiply the fee per fish by the quantity using BigDecimal.multiply()
        return feePerFish.multiply(BigDecimal.valueOf(quantity));
    }
}
