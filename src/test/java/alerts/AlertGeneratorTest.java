package alerts;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlertGeneratorTest {
    private static final long ONE_MINUTE = 60_000L;

    @Test
    void evaluateDataTriggersBloodPressureTrendAlerts() {
        AlertGenerator generator = new AlertGenerator(new DataStorage());

        Patient increasingPatient = new Patient(1);
        increasingPatient.addRecord(100.0, "SystolicPressure", 1L);
        increasingPatient.addRecord(112.0, "SystolicPressure", 2L);
        increasingPatient.addRecord(125.0, "SystolicPressure", 3L);

        generator.evaluateData(increasingPatient);

        assertTrue(alertConditions(generator).contains("Blood pressure increasing trend alert"));

        Patient decreasingPatient = new Patient(2);
        decreasingPatient.addRecord(95.0, "DiastolicPressure", 10L);
        decreasingPatient.addRecord(80.0, "DiastolicPressure", 20L);
        decreasingPatient.addRecord(68.0, "DiastolicPressure", 30L);

        generator.evaluateData(decreasingPatient);

        assertTrue(alertConditions(generator).contains("Blood pressure decreasing trend alert"));
    }

    @Test
    void evaluateDataDoesNotTriggerTrendAlertForSmallBloodPressureChanges() {
        AlertGenerator generator = new AlertGenerator(new DataStorage());
        Patient patient = new Patient(3);
        patient.addRecord(120.0, "SystolicPressure", 1L);
        patient.addRecord(128.0, "SystolicPressure", 2L);
        patient.addRecord(135.0, "SystolicPressure", 3L);

        generator.evaluateData(patient);

        assertFalse(alertConditions(generator).contains("Blood pressure increasing trend alert"));
        assertFalse(alertConditions(generator).contains("Blood pressure decreasing trend alert"));
    }

    @Test
    void evaluateDataRespectsCriticalBloodPressureThresholdBoundaries() {
        AlertGenerator generator = new AlertGenerator(new DataStorage());

        Patient boundaryPatient = new Patient(4);
        boundaryPatient.addRecord(180.0, "SystolicPressure", 1L);
        boundaryPatient.addRecord(90.0, "SystolicPressure", 2L);
        boundaryPatient.addRecord(120.0, "DiastolicPressure", 3L);
        boundaryPatient.addRecord(60.0, "DiastolicPressure", 4L);

        generator.evaluateData(boundaryPatient);

        assertFalse(alertConditions(generator).contains("Blood pressure critical threshold alert"));

        Patient criticalPatient = new Patient(5);
        criticalPatient.addRecord(181.0, "SystolicPressure", 10L);
        criticalPatient.addRecord(59.0, "DiastolicPressure", 20L);

        generator.evaluateData(criticalPatient);

        assertTrue(alertConditions(generator).contains("Blood pressure critical threshold alert"));
    }

    @Test
    void evaluateDataTriggersLowSaturationAndRapidDropAlerts() {
        AlertGenerator generator = new AlertGenerator(new DataStorage());
        Patient patient = new Patient(6);
        patient.addRecord(97.0, "Saturation", 0L);
        patient.addRecord(91.0, "Saturation", 5 * ONE_MINUTE);

        generator.evaluateData(patient);

        Set<String> conditions = alertConditions(generator);
        assertTrue(conditions.contains("Low saturation alert"));
        assertTrue(conditions.contains("Rapid saturation drop alert"));
    }

    @Test
    void evaluateDataDoesNotTriggerRapidDropOutsideTimeWindowOrThreshold() {
        AlertGenerator generator = new AlertGenerator(new DataStorage());

        Patient outsideWindowPatient = new Patient(7);
        outsideWindowPatient.addRecord(97.0, "Saturation", 0L);
        outsideWindowPatient.addRecord(91.0, "Saturation", 11 * ONE_MINUTE);

        generator.evaluateData(outsideWindowPatient);
        assertFalse(alertConditions(generator).contains("Rapid saturation drop alert"));

        Patient smallDropPatient = new Patient(8);
        smallDropPatient.addRecord(97.0, "Saturation", 0L);
        smallDropPatient.addRecord(93.0, "Saturation", 5 * ONE_MINUTE);

        generator.evaluateData(smallDropPatient);
        assertFalse(alertConditions(generator).contains("Rapid saturation drop alert"));
    }

    @Test
    void evaluateDataTriggersCombinedHypotensiveHypoxemiaAlertOnlyWhenBothSignalsAreLow() {
        AlertGenerator generator = new AlertGenerator(new DataStorage());

        Patient combinedAlertPatient = new Patient(9);
        combinedAlertPatient.addRecord(88.0, "SystolicPressure", 0L);
        combinedAlertPatient.addRecord(91.0, "Saturation", 5 * ONE_MINUTE);

        generator.evaluateData(combinedAlertPatient);
        assertTrue(alertConditions(generator).contains("Hypotensive hypoxemia alert"));

        AlertGenerator separateGenerator = new AlertGenerator(new DataStorage());
        Patient separatedSignalsPatient = new Patient(10);
        separatedSignalsPatient.addRecord(88.0, "SystolicPressure", 0L);
        separatedSignalsPatient.addRecord(91.0, "Saturation", 15 * ONE_MINUTE);

        separateGenerator.evaluateData(separatedSignalsPatient);
        assertFalse(alertConditions(separateGenerator).contains("Hypotensive hypoxemia alert"));
    }

    @Test
    void evaluateDataTriggersEcgAbnormalPeakAlertUsingSlidingWindowAverage() {
        AlertGenerator generator = new AlertGenerator(new DataStorage());
        Patient patient = new Patient(11);

        patient.addRecord(0.20, "ECG", 1L);
        patient.addRecord(0.30, "ECG", 2L);
        patient.addRecord(0.25, "ECG", 3L);
        patient.addRecord(0.35, "ECG", 4L);
        patient.addRecord(0.40, "ECG", 5L);
        patient.addRecord(1.80, "ECG", 6L);

        generator.evaluateData(patient);

        assertTrue(alertConditions(generator).contains("ECG abnormal peak alert"));
    }

    @Test
    void evaluateDataTracksTriggeredAlertStateFromAlertRecords() {
        AlertGenerator generator = new AlertGenerator(new DataStorage());
        Patient patient = new Patient(12);
        patient.addRecord(1.0, "Alert", 1L);

        generator.evaluateData(patient);
        assertTrue(alertConditions(generator).contains("Triggered alert"));

        patient.addRecord(0.0, "Alert", 2L);
        generator.evaluateData(patient);

        assertFalse(alertConditions(generator).contains("Triggered alert"));
    }

    @Test
    void evaluateDataLeavesNormalPatientWithoutAlerts() {
        AlertGenerator generator = new AlertGenerator(new DataStorage());
        Patient patient = new Patient(13);
        patient.addRecord(118.0, "SystolicPressure", 1L);
        patient.addRecord(121.0, "SystolicPressure", 2L);
        patient.addRecord(124.0, "SystolicPressure", 3L);
        patient.addRecord(78.0, "DiastolicPressure", 4L);
        patient.addRecord(96.0, "Saturation", 5L);
        patient.addRecord(95.0, "Saturation", 6L);
        patient.addRecord(0.20, "ECG", 7L);
        patient.addRecord(0.24, "ECG", 8L);
        patient.addRecord(0.18, "ECG", 9L);
        patient.addRecord(0.28, "ECG", 10L);
        patient.addRecord(0.22, "ECG", 11L);
        patient.addRecord(0.30, "ECG", 12L);

        generator.evaluateData(patient);

        List<Alert> alerts = generator.getTriggeredAlerts();
        assertTrue(alerts.isEmpty());
    }

    private Set<String> alertConditions(AlertGenerator generator) {
        return generator.getTriggeredAlerts().stream()
                .map(Alert::getCondition)
                .collect(Collectors.toSet());
    }
}
