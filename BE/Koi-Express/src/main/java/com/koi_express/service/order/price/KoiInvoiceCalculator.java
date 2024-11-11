package com.koi_express.service.order.price;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.koi_express.dto.response.ApiResponse;
import com.koi_express.enums.KoiType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KoiInvoiceCalculator {

    private static final Logger logger = Logger.getLogger(KoiInvoiceCalculator.class.getName());
    private static final Locale VIETNAM_LOCALE = Locale.of("vi", "VN");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(VIETNAM_LOCALE);

    @Value("${invoice.specializedVehicleFee:150000}") // Fee for specialized vehicle
    private int specializedVehicleFee;

    @Value("${invoice.vatRate:0.10}") // 10% VAT
    private double vatRate;

    @Value("${invoice.insuranceRate:0.05}") // 5% insurance rate
    private double insuranceRate;

    private final CareFee careFeeCalculator;
    private final PackagingFeeCalculator packagingFeeCalculator;

    // Constructor-based injection
    public KoiInvoiceCalculator(
            CareFee careFeeCalculator,
            PackagingFeeCalculator packagingFeeCalculator,
            @Value("${invoice.specializedVehicleFee:150000}") int specializedVehicleFee,
            @Value("${invoice.vatRate:0.10}") double vatRate,
            @Value("${invoice.insuranceRate:0.05}") double insuranceRate) {
        this.careFeeCalculator = careFeeCalculator;
        this.packagingFeeCalculator = packagingFeeCalculator;
        this.specializedVehicleFee = specializedVehicleFee;
        this.vatRate = vatRate;
        this.insuranceRate = insuranceRate;
    }

    public ApiResponse<Map<String, BigDecimal>> calculateTotalPrice(
            KoiType koiType, int quantity, BigDecimal koiSize, BigDecimal distanceFee, BigDecimal commitmentFee) {
        validateInputs(quantity, koiSize, distanceFee, commitmentFee);

        CareFee.Size size = convertLengthToSize(koiSize);

        BigDecimal koiFee = KoiPriceCalculator.calculateTotalPrice(koiType, quantity, koiSize);
        BigDecimal careFee = careFeeCalculator.calculateCareFee(koiType, size, quantity);
        BigDecimal packagingFee = packagingFeeCalculator.calculateFee(quantity, koiSize);
        BigDecimal remainingTransportationFee = calculateRemainingTransportationFee(distanceFee, commitmentFee);
        BigDecimal insuranceFee = calculateInsuranceFee(koiFee, careFee, packagingFee, remainingTransportationFee);
        BigDecimal subtotal =
                calculateSubtotal(koiFee, careFee, packagingFee, remainingTransportationFee, insuranceFee);
        BigDecimal vat = calculateVAT(subtotal);
        BigDecimal totalFee = subtotal.add(vat);

        Map<String, BigDecimal> feeDetails = new HashMap<>();
        feeDetails.put("koiFee", koiFee.setScale(0, RoundingMode.HALF_UP));
        feeDetails.put("careFee", careFee.setScale(0, RoundingMode.HALF_UP));
        feeDetails.put("packagingFee", packagingFee.setScale(0, RoundingMode.HALF_UP));
        feeDetails.put("remainingTransportationFee", remainingTransportationFee.setScale(0, RoundingMode.HALF_UP));
        feeDetails.put("insuranceFee", insuranceFee.setScale(0, RoundingMode.HALF_UP));
        feeDetails.put("subtotal", subtotal.setScale(0, RoundingMode.HALF_UP));
        feeDetails.put("vat", vat.setScale(0, RoundingMode.HALF_UP));
        feeDetails.put("totalFee", totalFee.setScale(0, RoundingMode.HALF_UP));
        feeDetails.put("specializedVehicleFee", BigDecimal.valueOf(specializedVehicleFee));

        if (logger.isLoggable(Level.INFO)) {
            logger.info(String.format(
                    "Total price for %d %s koi: %s", quantity, koiType.name(), CURRENCY_FORMAT.format(totalFee)));
        }

        return new ApiResponse<>(200, "Total price calculated successfully", feeDetails);
    }

    private BigDecimal calculateRemainingTransportationFee(BigDecimal distanceFee, BigDecimal commitmentFee) {
        return distanceFee.subtract(commitmentFee);
    }

    private BigDecimal calculateInsuranceFee(
            BigDecimal koiFee, BigDecimal careFee, BigDecimal packagingFee, BigDecimal transportationFee) {
        return koiFee.add(careFee).add(packagingFee).add(transportationFee).multiply(BigDecimal.valueOf(insuranceRate));
    }

    private BigDecimal calculateSubtotal(
            BigDecimal fishPrice,
            BigDecimal careFee,
            BigDecimal packagingFee,
            BigDecimal transportationFee,
            BigDecimal insuranceFee) {
        return fishPrice
                .add(careFee)
                .add(packagingFee)
                .add(transportationFee)
                .add(insuranceFee)
                .add(BigDecimal.valueOf(specializedVehicleFee));
    }

    private BigDecimal calculateVAT(BigDecimal subtotal) {
        return subtotal.multiply(BigDecimal.valueOf(vatRate));
    }

    private void validateInputs(int quantity, BigDecimal koiSize, BigDecimal distanceFee, BigDecimal commitmentFee) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (koiSize.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Koi size must be greater than 0");
        }
        if (distanceFee.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Distance fee cannot be negative");
        }
        if (commitmentFee.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Commitment fee cannot be negative");
        }
    }

    private CareFee.Size convertLengthToSize(BigDecimal koiSize) {
        if (koiSize.compareTo(BigDecimal.valueOf(30)) < 0) {
            return CareFee.Size.LESS_THAN_30_CM;
        } else if (koiSize.compareTo(BigDecimal.valueOf(50)) <= 0) {
            return CareFee.Size.SIZE_30_TO_50_CM;
        } else {
            return CareFee.Size.GREATER_THAN_50_CM;
        }
    }
}
