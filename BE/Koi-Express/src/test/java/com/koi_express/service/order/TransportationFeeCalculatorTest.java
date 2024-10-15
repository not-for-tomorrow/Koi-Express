import static org.junit.jupiter.api.Assertions.*;

import com.koi_express.service.order.TransportationFeeCalculator;
import org.junit.jupiter.api.Test;

public class TransportationFeeCalculatorTest {

    @Test
    void calculateTotalFee_ShortDistance() {
        double totalFee = TransportationFeeCalculator.calculateTotalFee(100);
        assertEquals(100 * 5200 + (100 / 100) * 11.0 * 19000, totalFee, 0.01);
    }

    @Test
    void calculateTotalFee_LongDistance() {
        double totalFee = TransportationFeeCalculator.calculateTotalFee(400);
        assertEquals(400 * 5200 + (400 / 100) * 14.0 * 19000, totalFee, 0.01);
    }

    @Test
    void calculateTotalFee_ExactThresholdDistance() {
        double totalFee = TransportationFeeCalculator.calculateTotalFee(300);
        assertEquals(300 * 5200 + (300 / 100) * 11.0 * 19000, totalFee, 0.01);
    }

    @Test
    void calculateTotalFee_JustBelowThresholdDistance() {
        double totalFee = TransportationFeeCalculator.calculateTotalFee(299.9);
        assertEquals(299.9 * 5200 + (299.9 / 100) * 11.0 * 19000, totalFee, 0.01);
    }

    @Test
    void calculateTotalFee_JustAboveThresholdDistance() {
        double totalFee = TransportationFeeCalculator.calculateTotalFee(300.1);
        assertEquals(300.1 * 5200 + (300.1 / 100) * 14.0 * 19000, totalFee, 0.01);
    }

    @Test
    void calculateTotalFee_SmallDistance() {
        double totalFee = TransportationFeeCalculator.calculateTotalFee(1);
        assertEquals(1 * 5200 + (1 / 100) * 11.0 * 19000, totalFee, 0.01);
    }

    @Test
    void calculateTotalFee_LargeDistance() {
        double totalFee = TransportationFeeCalculator.calculateTotalFee(1000);
        assertEquals(1000 * 5200 + (1000 / 100) * 14.0 * 19000, totalFee, 0.01);
    }

    @Test
    void calculateTotalFee_DistanceWithSmallTruck() {
        double totalFee = TransportationFeeCalculator.calculateTotalFee(150);
        assertEquals(150 * 5200 + (150 / 100.0) * 11.0 * 19000, totalFee, 0.01);
    }

    @Test
    void calculateTotalFee_DistanceWithLargeTruck() {
        double totalFee = TransportationFeeCalculator.calculateTotalFee(350);
        assertEquals(350 * 5200 + (350 / 100.0) * 14.0 * 19000, totalFee, 0.01);
    }

    @Test
    void calculateTotalFee_DistanceWithMixedTruckUsage() {
        double totalFee = TransportationFeeCalculator.calculateTotalFee(300);
        assertEquals(300 * 5200 + (300 / 100) * 11.0 * 19000, totalFee, 0.01);
    }

    @Test
    void calculateTotalFee_DistanceWithHighPrecision() {
        double totalFee = TransportationFeeCalculator.calculateTotalFee(123.456789);
        assertEquals(123.456789 * 5200 + (123.456789 / 100) * 11.0 * 19000, totalFee, 0.01);
    }

    @Test
    void calculateTotalFee_DistanceWithZeroFuelConsumption() {
        double totalFee = TransportationFeeCalculator.calculateTotalFee(0);
        assertEquals(0, totalFee, 0.01);
    }
}
