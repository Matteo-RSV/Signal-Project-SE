package alerts;

import com.alerts.Alert;
import com.alerts.BloodPressureAlert;
import com.alerts.PriorityAlertDecorator;
import com.alerts.RepeatedAlertDecorator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlertDecoratorTest {

    @Test
    void priorityAlertDecoratorKeepsOriginalDataAndAddsPriorityText() {
        Alert alert = new BloodPressureAlert("1", "Blood pressure critical threshold alert", 1000L);
        Alert decoratedAlert = new PriorityAlertDecorator(alert);

        assertEquals("1", decoratedAlert.getPatientId());
        assertEquals("Blood pressure critical threshold alert", decoratedAlert.getCondition());
        assertEquals(1000L, decoratedAlert.getTimestamp());
        assertTrue(decoratedAlert.getMessage().contains("[PRIORITY]"));
        assertTrue(decoratedAlert.getMessage().contains(alert.getMessage()));
    }

    @Test
    void repeatedAlertDecoratorKeepsOriginalDataAndAddsRepeatedCheckText() {
        Alert alert = new BloodPressureAlert("2", "Blood pressure increasing trend alert", 2000L);
        Alert decoratedAlert = new RepeatedAlertDecorator(alert);

        assertEquals("2", decoratedAlert.getPatientId());
        assertEquals("Blood pressure increasing trend alert", decoratedAlert.getCondition());
        assertEquals(2000L, decoratedAlert.getTimestamp());
        assertTrue(decoratedAlert.getMessage().contains("[REPEATED CHECK]"));
        assertTrue(decoratedAlert.getMessage().contains(alert.getMessage()));
    }

    @Test
    void decoratorsCanBeCombinedAndStillKeepOriginalData() {
        Alert alert = new BloodPressureAlert("3", "Blood pressure decreasing trend alert", 3000L);
        Alert decoratedAlert = new RepeatedAlertDecorator(new PriorityAlertDecorator(alert));

        assertEquals("3", decoratedAlert.getPatientId());
        assertEquals("Blood pressure decreasing trend alert", decoratedAlert.getCondition());
        assertEquals(3000L, decoratedAlert.getTimestamp());
        assertTrue(decoratedAlert.getMessage().contains("[PRIORITY]"));
        assertTrue(decoratedAlert.getMessage().contains("[REPEATED CHECK]"));
        assertTrue(decoratedAlert.getMessage().contains(alert.getMessage()));
    }
}
