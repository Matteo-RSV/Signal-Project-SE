package alerts;

import com.alerts.BloodPressureStrategy;
import com.alerts.HeartRateStrategy;
import com.alerts.OxygenSaturationStrategy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlertStrategyTest {

    @Test
    void bloodPressureStrategyReturnsFalseForNormalSystolicValue() {
        BloodPressureStrategy strategy = new BloodPressureStrategy(90.0, 180.0);

        assertFalse(strategy.checkAlert(120.0));
    }

    @Test
    void bloodPressureStrategyReturnsTrueForHighSystolicValue() {
        BloodPressureStrategy strategy = new BloodPressureStrategy(90.0, 180.0);

        assertTrue(strategy.checkAlert(181.0));
    }

    @Test
    void bloodPressureStrategyReturnsTrueForLowSystolicValue() {
        BloodPressureStrategy strategy = new BloodPressureStrategy(90.0, 180.0);

        assertTrue(strategy.checkAlert(89.0));
    }

    @Test
    void bloodPressureStrategyKeepsBoundaryValuesNormal() {
        BloodPressureStrategy strategy = new BloodPressureStrategy(90.0, 180.0);

        assertFalse(strategy.checkAlert(90.0));
        assertFalse(strategy.checkAlert(180.0));
    }

    @Test
    void heartRateStrategyReturnsFalseForNormalHeartRate() {
        HeartRateStrategy strategy = new HeartRateStrategy();

        assertFalse(strategy.checkAlert(75.0));
    }

    @Test
    void heartRateStrategyReturnsTrueForVeryHighHeartRate() {
        HeartRateStrategy strategy = new HeartRateStrategy();

        assertTrue(strategy.checkAlert(121.0));
    }

    @Test
    void heartRateStrategyReturnsTrueForVeryLowHeartRate() {
        HeartRateStrategy strategy = new HeartRateStrategy();

        assertTrue(strategy.checkAlert(49.0));
    }

    @Test
    void heartRateStrategyKeepsBoundaryValuesNormal() {
        HeartRateStrategy strategy = new HeartRateStrategy();

        assertFalse(strategy.checkAlert(50.0));
        assertFalse(strategy.checkAlert(120.0));
    }

    @Test
    void oxygenSaturationStrategyReturnsFalseForNormalOxygenSaturation() {
        OxygenSaturationStrategy strategy = new OxygenSaturationStrategy();

        assertFalse(strategy.checkAlert(95.0));
    }

    @Test
    void oxygenSaturationStrategyReturnsTrueForLowOxygenSaturation() {
        OxygenSaturationStrategy strategy = new OxygenSaturationStrategy();

        assertTrue(strategy.checkAlert(91.0));
    }

    @Test
    void oxygenSaturationStrategyKeepsBoundaryValueNormal() {
        OxygenSaturationStrategy strategy = new OxygenSaturationStrategy();

        assertFalse(strategy.checkAlert(92.0));
    }
}
