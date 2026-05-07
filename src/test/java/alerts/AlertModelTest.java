package alerts;

import com.alerts.Alert;
import com.alerts.BasicAlert;
import com.alerts.BloodOxygenAlert;
import com.alerts.BloodPressureAlert;
import com.alerts.ECGAlert;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlertModelTest {

    @Test
    void bloodPressureAlertStoresValuesAndImplementsInterface() {
        BloodPressureAlert alert = new BloodPressureAlert("1", "Blood pressure increasing trend alert", 1000L);

        assertEquals("1", alert.getPatientId());
        assertEquals("Blood pressure increasing trend alert", alert.getCondition());
        assertEquals(1000L, alert.getTimestamp());
        assertFalse(alert.getMessage().isEmpty());
        assertTrue(alert instanceof Alert);
    }

    @Test
    void bloodOxygenAlertStoresValuesAndImplementsInterface() {
        BloodOxygenAlert alert = new BloodOxygenAlert("2", "Low saturation alert", 2000L);

        assertEquals("2", alert.getPatientId());
        assertEquals("Low saturation alert", alert.getCondition());
        assertEquals(2000L, alert.getTimestamp());
        assertFalse(alert.getMessage().isEmpty());
        assertTrue(alert instanceof Alert);
    }

    @Test
    void ecgAlertStoresValuesAndImplementsInterface() {
        ECGAlert alert = new ECGAlert("3", "ECG abnormal peak alert", 3000L);

        assertEquals("3", alert.getPatientId());
        assertEquals("ECG abnormal peak alert", alert.getCondition());
        assertEquals(3000L, alert.getTimestamp());
        assertFalse(alert.getMessage().isEmpty());
        assertTrue(alert instanceof Alert);
    }

    @Test
    void basicAlertStoresValuesAndImplementsInterface() {
        BasicAlert alert = new BasicAlert("4", "Triggered alert", 4000L);

        assertEquals("4", alert.getPatientId());
        assertEquals("Triggered alert", alert.getCondition());
        assertEquals(4000L, alert.getTimestamp());
        assertFalse(alert.getMessage().isEmpty());
        assertTrue(alert instanceof Alert);
    }
}
