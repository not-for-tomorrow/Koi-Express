package com.koi_express.service.order.price;

import com.koi_express.enums.KoiType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public BigDecimal calculateTotalPrice(KoiType koiType, int quantity, double length, double distance, double commitmentFee) {
        validateInputs(quantity, length, distance);

        CareFee.Size size = convertLengthToSize(length);

        // 1. Koi price
        BigDecimal fishPrice = koiPriceCalculator.calculateTotalPrice(koiType, quantity, length);

        // 2. Care fee
        BigDecimal careFee = careFeeCalculator.calculateCareFee(koiType, size, quantity);

        // 3. Packaging fee
        BigDecimal packagingFee = packagingFeeCalculator.calculateTotalPackagingFee(quantity, BigDecimal.valueOf(length));

        // 4. Transportation fee
        BigDecimal transportationFee = transportationFeeCalculator.calculateTotalFee(BigDecimal.valueOf(distance));

        // 5. Phí vận chuyển còn lại sau khi trừ phí cam kết
        BigDecimal remainingTransportationFee = calculateRemainingTransportationFee(transportationFee, BigDecimal.valueOf(commitmentFee));

        // 6. Insurance fee
        BigDecimal insuranceFee = calculateInsuranceFee(fishPrice, careFee, packagingFee, remainingTransportationFee);

        // 7. Subtotal
        BigDecimal subtotal = calculateSubtotal(fishPrice, careFee, packagingFee, remainingTransportationFee, insuranceFee);

        // 8. VAT
        BigDecimal vat = calculateVAT(subtotal);

        // 9. Total price
        BigDecimal totalPrice = subtotal.add(vat);

        logger.info(String.format("Total price for %d %s koi: %.2f VND", quantity, koiType.name(), totalPrice));
        return totalPrice.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateRemainingTransportationFee(BigDecimal transportationFee, BigDecimal commitmentFee) {
        return transportationFee.subtract(commitmentFee);
    }

    private BigDecimal calculateInsuranceFee(BigDecimal fishPrice, BigDecimal careFee, BigDecimal packagingFee, BigDecimal transportationFee) {
        return fishPrice.add(careFee).add(packagingFee).add(transportationFee).multiply(BigDecimal.valueOf(insuranceRate));
    }

    private BigDecimal calculateSubtotal(BigDecimal fishPrice, BigDecimal careFee, BigDecimal packagingFee, BigDecimal transportationFee, BigDecimal insuranceFee) {
        return fishPrice.add(careFee)
                .add(packagingFee)
                .add(transportationFee)
                .add(insuranceFee)
                .add(BigDecimal.valueOf(specializedVehicleFee));
    }

    private BigDecimal calculateVAT(BigDecimal subtotal) {
        return subtotal.multiply(BigDecimal.valueOf(vatRate));
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
