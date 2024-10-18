package com.koi_express.service.order.price;

import com.koi_express.enums.KoiType;
import org.springframework.stereotype.Component;

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

    private final Map<String, Double> priceTable = new HashMap<>();

    public CareFee() {
        // Japan Koi Pricing
        priceTable.put("KOI_NHAT_BAN:LESS_THAN_30_CM", 50000.0);
        priceTable.put("KOI_NHAT_BAN:SIZE_30_TO_50_CM", 80000.0);
        priceTable.put("KOI_NHAT_BAN:GREATER_THAN_50_CM", 120000.0);

        // Vietnam Koi Pricing
        priceTable.put("KOI_VIET_NAM:LESS_THAN_30_CM", 30000.0);
        priceTable.put("KOI_VIET_NAM:SIZE_30_TO_50_CM", 60000.0);
        priceTable.put("KOI_VIET_NAM:GREATER_THAN_50_CM", 100000.0);

        // Europe Koi Pricing
        priceTable.put("KOI_CHAU_AU:LESS_THAN_30_CM", 50000.0);
        priceTable.put("KOI_CHAU_AU:SIZE_30_TO_50_CM", 80000.0);
        priceTable.put("KOI_CHAU_AU:GREATER_THAN_50_CM", 120000.0);
    }

    public double calculateCareFee(KoiType type, Size size, int quantity) {
        if (type == null || size == null) {
            throw new IllegalArgumentException("Invalid input: type or size is null");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        String key = String.format("%s:%s", type.name(), size.name());
        Double feePerFish = priceTable.get(key);

        if (feePerFish == null) {
            logger.warning(String.format("Price not found for combination: %s", key));
            throw new IllegalArgumentException("Price not available for this combination");
        }

        return feePerFish * quantity;
    }
}
