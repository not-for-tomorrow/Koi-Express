package com.koi_express.service.order.price;

import com.koi_express.enums.KoiType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class KoiInvoiceCalculator {

    private static final Logger logger = Logger.getLogger(KoiInvoiceCalculator.class.getName());

    @Value("${invoice.specializedVehicleFee:150000}") // Phí phương tiện chuyên dụng
    private int specializedVehicleFee;

    @Value("${invoice.vatRate:0.10}") // 10% VAT
    private double vatRate;

    @Value("${invoice.insuranceRate:0.05}") // 5% phí bảo hiểm
    private double insuranceRate;

    private final KoiPriceCalculator koiPriceCalculator;
    private final CareFee careFeeCalculator;
    private final PackagingFeeCalculator packagingFeeCalculator;
    private final TransportationFeeCalculator transportationFeeCalculator;

    // Constructor-based injection
    public KoiInvoiceCalculator(KoiPriceCalculator koiPriceCalculator,
                                CareFee careFeeCalculator,
                                PackagingFeeCalculator packagingFeeCalculator,
                                TransportationFeeCalculator transportationFeeCalculator) {
        this.koiPriceCalculator = koiPriceCalculator;
        this.careFeeCalculator = careFeeCalculator;
        this.packagingFeeCalculator = packagingFeeCalculator;
        this.transportationFeeCalculator = transportationFeeCalculator;
    }

    public double calculateTotalPrice(KoiType koiType, int quantity, double length, double weight, double distance) {
        validateInputs(quantity, length, weight, distance);

        CareFee.Size size = convertLengthToSize(length);
        CareFee.Weight weightEnum = convertWeightToEnum(weight);

        // Phí cá Koi
        double fishPrice = koiPriceCalculator.calculateTotalPrice(koiType, quantity, length, weight);

        // 1. Tính phí chăm sóc (Care fee)
        double careFee = careFeeCalculator.calculateCareFee(koiType, size, weightEnum, quantity);

        // 2. Tính phí đóng gói (Packaging fee)
        double packagingFee = packagingFeeCalculator.calculateTotalPackagingFee(quantity, length, weight);

        // 3. Tính phí vận chuyển (Transportation fee)
        double transportationFee = transportationFeeCalculator.calculateTotalFee(distance);

        // 4. Tính phí bảo hiểm (Insurance fee)
        double insuranceFee = calculateInsuranceFee(fishPrice, careFee, packagingFee, transportationFee);

        // 5. Tính tổng trước VAT (Subtotal before VAT)
        double subtotal = calculateSubtotal(fishPrice, careFee, packagingFee, transportationFee, insuranceFee);

        // 6. Tính VAT (10% của tổng trước VAT)
        double vat = calculateVAT(subtotal);

        // 7. Tính tổng tiền (Total price)
        double totalPrice = subtotal + vat;

        logger.info(String.format("Total price for %d %s koi: %.2f VND", quantity, koiType.name(), totalPrice));
        return totalPrice;
    }

    private double calculateInsuranceFee(double fishPrice, double careFee, double packagingFee, double transportationFee) {
        return (fishPrice + careFee + packagingFee + transportationFee) * insuranceRate;
    }

    private double calculateSubtotal(double fishPrice, double careFee, double packagingFee, double transportationFee, double insuranceFee) {
        return fishPrice + careFee + packagingFee + transportationFee + insuranceFee + specializedVehicleFee;
    }

    private double calculateVAT(double subtotal) {
        return subtotal * vatRate;
    }

    private void validateInputs(int quantity, double length, double weight, double distance) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (length <= 0 || weight <= 0) {
            throw new IllegalArgumentException("Length and weight must be greater than 0");
        }
        if (distance < 0) {
            throw new IllegalArgumentException("Distance cannot be negative");
        }
    }

    private CareFee.Size convertLengthToSize(double length) {
        if (length < 30) {
            return CareFee.Size.LESS_THAN_30_CM;
        } else if (length <= 50) {
            return CareFee.Size.SIZE_30_TO_50_CM;
        } else {
            return CareFee.Size.GREATER_THAN_50_CM;
        }
    }

    private CareFee.Weight convertWeightToEnum(double weight) {
        if (weight < 1.5) {
            return CareFee.Weight.LESS_THAN_1_5_KG;
        } else if (weight <= 3) {
            return CareFee.Weight.LESS_THAN_3_KG;
        } else if (weight <= 5) {
            return CareFee.Weight.LESS_THAN_5_KG;
        } else {
            return CareFee.Weight.GREATER_THAN_5_KG;
        }
    }
}
