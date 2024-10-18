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
                                TransportationFeeCalculator transportationFeeCalculator,
                                @Value("${invoice.specializedVehicleFee:150000}") int specializedVehicleFee,
                                @Value("${invoice.vatRate:0.10}") double vatRate,
                                @Value("${invoice.insuranceRate:0.05}") double insuranceRate) {
        this.koiPriceCalculator = koiPriceCalculator;
        this.careFeeCalculator = careFeeCalculator;
        this.packagingFeeCalculator = packagingFeeCalculator;
        this.transportationFeeCalculator = transportationFeeCalculator;
        this.specializedVehicleFee = specializedVehicleFee;
        this.vatRate = vatRate;
        this.insuranceRate = insuranceRate;
    }

    public double calculateTotalPrice(KoiType koiType, int quantity, double length, double distance, double commitmentFee) {
        validateInputs(quantity, length, distance);

        CareFee.Size size = convertLengthToSize(length);

        // 1. Koi price
        double fishPrice = koiPriceCalculator.calculateTotalPrice(koiType, quantity, length);

        // 2. Care fee
        double careFee = careFeeCalculator.calculateCareFee(koiType, size, quantity);

        // 3. Packaging fee
        double packagingFee = packagingFeeCalculator.calculateTotalPackagingFee(quantity, length);

        // 4. Transportation fee
        double transportationFee = transportationFeeCalculator.calculateTotalFee(distance);

        // 5.1. Phí vận chuyển còn lại sau khi trừ phí cam kết
        double remainingTransportationFee = calculateRemainingTransportationFee(transportationFee, commitmentFee);

        // 6. Insurance fee
        double insuranceFee = calculateInsuranceFee(fishPrice, careFee, packagingFee, remainingTransportationFee);

        // 7. subtotal
        double subtotal = calculateSubtotal(fishPrice, careFee, packagingFee, remainingTransportationFee, insuranceFee);

        // 8. VAT
        double vat = calculateVAT(subtotal);

        // 9. Total price
        double totalPrice = subtotal + vat;

        logger.info(String.format("Total price for %d %s koi: %.2f VND", quantity, koiType.name(), totalPrice));
        return totalPrice;
    }

    private double calculateRemainingTransportationFee(double transportationFee, double commitmentFee) {
        return transportationFee - commitmentFee;
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

    private void validateInputs(int quantity, double length, double distance) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be greater than 0");
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

}
