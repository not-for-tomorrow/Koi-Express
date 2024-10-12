package com.koi_express.service.order;

import com.koi_express.enums.KoiType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class KoiPriceCalculatorTest {

    @Test
    void calculateTotalPrice_KoiNhatBan_SmallLengthSmallWeight() {
        double totalPrice = KoiPriceCalculator.calculateTotalPrice(KoiType.KOI_NHAT_BAN, 1, 25, 1.0);
        assertEquals(100_000, totalPrice);
    }

    @Test
    void calculateTotalPrice_KoiNhatBan_MediumLengthMediumWeight() {
        double totalPrice = KoiPriceCalculator.calculateTotalPrice(KoiType.KOI_NHAT_BAN, 1, 40, 2.5);
        assertEquals(200_000, totalPrice);
    }

    @Test
    void calculateTotalPrice_KoiNhatBan_LargeLengthLargeWeight() {
        double totalPrice = KoiPriceCalculator.calculateTotalPrice(KoiType.KOI_NHAT_BAN, 1, 55, 4.5);
        assertEquals(400_000, totalPrice);
    }

    @Test
    void calculateTotalPrice_KoiVietNam_SmallLengthSmallWeight() {
        double totalPrice = KoiPriceCalculator.calculateTotalPrice(KoiType.KOI_VIET_NAM, 1, 25, 1.0);
        assertEquals(50_000, totalPrice);
    }

    @Test
    void calculateTotalPrice_KoiVietNam_MediumLengthMediumWeight() {
        double totalPrice = KoiPriceCalculator.calculateTotalPrice(KoiType.KOI_VIET_NAM, 1, 40, 2.5);
        assertEquals(100_000, totalPrice);
    }

    @Test
    void calculateTotalPrice_KoiVietNam_LargeLengthLargeWeight() {
        double totalPrice = KoiPriceCalculator.calculateTotalPrice(KoiType.KOI_VIET_NAM, 1, 55, 4.5);
        assertEquals(200_000, totalPrice);
    }

    @Test
    void calculateTotalPrice_KoiChauAu_SmallLengthSmallWeight() {
        double totalPrice = KoiPriceCalculator.calculateTotalPrice(KoiType.KOI_CHAU_AU, 1, 25, 1.0);
        assertEquals(120_000, totalPrice);
    }

    @Test
    void calculateTotalPrice_KoiChauAu_MediumLengthMediumWeight() {
        double totalPrice = KoiPriceCalculator.calculateTotalPrice(KoiType.KOI_CHAU_AU, 1, 40, 2.5);
        assertEquals(220_000, totalPrice);
    }

    @Test
    void calculateTotalPrice_KoiChauAu_LargeLengthLargeWeight() {
        double totalPrice = KoiPriceCalculator.calculateTotalPrice(KoiType.KOI_CHAU_AU, 1, 55, 4.5);
        assertEquals(450_000, totalPrice);
    }

    @Test
    void calculateTotalPrice_InvalidKoiType() {
        assertThrows(IllegalArgumentException.class, () -> {
            KoiPriceCalculator.calculateTotalPrice(null, 1, 25, 1.0);
        });
    }
}