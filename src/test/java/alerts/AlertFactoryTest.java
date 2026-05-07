package alerts;

import com.alerts.Alert;
import com.alerts.AlertFactory;
import com.alerts.BloodOxygenAlert;
import com.alerts.BloodOxygenAlertFactory;
import com.alerts.BloodPressureAlert;
import com.alerts.BloodPressureAlertFactory;
import com.alerts.ECGAlert;
import com.alerts.ECGAlertFactory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlertFactoryTest {

    @Test
    void bloodPressureAlertFactoryCreatesBloodPressureAlert() {
        AlertFactory factory = new BloodPressureAlertFactory();

        Alert alert = factory.createAlert("1", "Blood pressure increasing trend alert", 1000L);

        assertTrue(alert instanceof BloodPressureAlert);
        assertEquals("1", alert.getPatientId());
        assertEquals("Blood pressure increasing trend alert", alert.getCondition());
        assertEquals(1000L, alert.getTimestamp());
        assertFalse(alert.getMessage().isEmpty());
    }

    @Test
    void bloodOxygenAlertFactoryCreatesBloodOxygenAlert() {
        AlertFactory factory = new BloodOxygenAlertFactory();

        Alert alert = factory.createAlert("2", "Low saturation alert", 2000L);

        assertTrue(alert instanceof BloodOxygenAlert);
        assertEquals("2", alert.getPatientId());
        assertEquals("Low saturation alert", alert.getCondition());
        assertEquals(2000L, alert.getTimestamp());
        assertFalse(alert.getMessage().isEmpty());
    }

    @Test
    void ecgAlertFactoryCreatesEcgAlert() {
        AlertFactory factory = new ECGAlertFactory();

        Alert alert = factory.createAlert("3", "ECG abnormal peak alert", 3000L);

        assertTrue(alert instanceof ECGAlert);
        assertEquals("3", alert.getPatientId());
        assertEquals("ECG abnormal peak alert", alert.getCondition());
        assertEquals(3000L, alert.getTimestamp());
        assertFalse(alert.getMessage().isEmpty());
    }
}
