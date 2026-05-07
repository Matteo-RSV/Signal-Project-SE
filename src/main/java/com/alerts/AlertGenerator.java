package com.alerts;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

/**
 * Monitors patient data and determines whether alert conditions are met.
 *
 * <p>This class uses a {@link DataStorage} instance to access patient
 * information and provides methods for evaluating data and triggering alerts
 * when abnormal conditions are detected.
 */
public class AlertGenerator {
    private static final long TEN_MINUTES_IN_MILLIS = 10L * 60L * 1000L;
    private static final int ECG_WINDOW_SIZE = 5;
    private static final double ECG_PEAK_MULTIPLIER = 3.0;
    private static final double ECG_PEAK_MIN_DELTA = 1.0;

    private final List<Alert> triggeredAlerts;
    private final AlertFactory bloodPressureAlertFactory;
    private final AlertFactory bloodOxygenAlertFactory;
    private final AlertFactory ecgAlertFactory;
    private final AlertStrategy systolicBloodPressureStrategy;
    private final AlertStrategy diastolicBloodPressureStrategy;
    private final AlertStrategy lowSystolicBloodPressureStrategy;
    private final AlertStrategy oxygenSaturationStrategy;

    // Updated the Javadoc for clarity and readability in line with the Google Java Style Guide
    /**
     * Creates an alert generator that uses the specified data storage source.
     *
     * @param dataStorage the data storage component used to access patient data
     */
    public AlertGenerator(DataStorage dataStorage) {
        if (dataStorage == null) {
            throw new IllegalArgumentException("dataStorage cannot be null");
        }

        this.triggeredAlerts = new ArrayList<>();
        this.bloodPressureAlertFactory = new BloodPressureAlertFactory();
        this.bloodOxygenAlertFactory = new BloodOxygenAlertFactory();
        this.ecgAlertFactory = new ECGAlertFactory();
        this.systolicBloodPressureStrategy = new BloodPressureStrategy(90.0, 180.0);
        this.diastolicBloodPressureStrategy = new BloodPressureStrategy(60.0, 120.0);
        this.lowSystolicBloodPressureStrategy = new BloodPressureStrategy(90.0, Double.MAX_VALUE);
        this.oxygenSaturationStrategy = new OxygenSaturationStrategy();
        
    }

    /**
     * Returns the alerts most recently triggered for the evaluated patients.
     *
     * @return a copy of the triggered alert list
     */
    public List<Alert> getTriggeredAlerts() {
        return new ArrayList<>(triggeredAlerts);
    }

    // Updated the Javadoc link to reference the full method signature more clearly
    // ({@link #triggerAlert(Alert)})
    /**
     * Evaluates a patient's data to determine whether an alert should be raised.
     *
     * <p>If the patient's measurements meet a predefined alert condition, this
     * method is expected to trigger an alert through
     * {@link #triggerAlert(Alert)}.
     *
     * @param patient the patient whose data is being checked for alert conditions
     */
    public void evaluateData(Patient patient) {
        clearAlertsForPatient(patient.getPatientId());

        List<PatientRecord> allRecords = patient.getRecords(Long.MIN_VALUE, Long.MAX_VALUE);
        if (allRecords.isEmpty()) {
            return;
        }

        int patientId = patient.getPatientId();
        String patientIdText = Integer.toString(patientId);

        List<PatientRecord> systolicRecords = getRecordsByType(allRecords, "SystolicPressure");
        List<PatientRecord> diastolicRecords = getRecordsByType(allRecords, "DiastolicPressure");
        List<PatientRecord> saturationRecords = getRecordsByType(allRecords, "Saturation");
        List<PatientRecord> ecgRecords = getRecordsByType(allRecords, "ECG");
        List<PatientRecord> alertRecords = getRecordsByType(allRecords, "Alert");

        PatientRecord increasingTrendRecord = latestRecord(
                findTrendRecord(systolicRecords, true),
                findTrendRecord(diastolicRecords, true));
        if (increasingTrendRecord != null) {
            triggerAlert(bloodPressureAlertFactory.createAlert(patientIdText,
                    "Blood pressure increasing trend alert", increasingTrendRecord.getTimestamp()));
        }

        PatientRecord decreasingTrendRecord = latestRecord(
                findTrendRecord(systolicRecords, false),
                findTrendRecord(diastolicRecords, false));
        if (decreasingTrendRecord != null) {
            triggerAlert(bloodPressureAlertFactory.createAlert(patientIdText,
                    "Blood pressure decreasing trend alert", decreasingTrendRecord.getTimestamp()));
        }

        PatientRecord criticalPressureRecord = latestRecord(
                findAlertRecord(systolicRecords, systolicBloodPressureStrategy),
                findAlertRecord(diastolicRecords, diastolicBloodPressureStrategy));
        if (criticalPressureRecord != null) {
            triggerAlert(bloodPressureAlertFactory.createAlert(patientIdText,
                    "Blood pressure critical threshold alert", criticalPressureRecord.getTimestamp()));
        }

        PatientRecord lowSaturationRecord = findAlertRecord(saturationRecords, oxygenSaturationStrategy);
        if (lowSaturationRecord != null) {
            triggerAlert(bloodOxygenAlertFactory.createAlert(patientIdText, "Low saturation alert",
                    lowSaturationRecord.getTimestamp()));
        }

        PatientRecord rapidDropRecord = findRapidDropRecord(saturationRecords);
        if (rapidDropRecord != null) {
            triggerAlert(bloodOxygenAlertFactory.createAlert(patientIdText, "Rapid saturation drop alert",
                    rapidDropRecord.getTimestamp()));
        }

        long combinedAlertTimestamp = findHypotensiveHypoxemiaTimestamp(systolicRecords, saturationRecords);
        if (combinedAlertTimestamp >= 0) {
            triggerAlert(bloodOxygenAlertFactory.createAlert(patientIdText,
                    "Hypotensive hypoxemia alert", combinedAlertTimestamp));
        }

        PatientRecord ecgPeakRecord = findAbnormalEcgPeakRecord(ecgRecords);
        if (ecgPeakRecord != null) {
            triggerAlert(ecgAlertFactory.createAlert(patientIdText, "ECG abnormal peak alert",
                    ecgPeakRecord.getTimestamp()));
        }

        PatientRecord latestAlertState = findLatestAlertState(alertRecords);
        if (latestAlertState != null && latestAlertState.getMeasurementValue() >= 0.5) {
            triggerAlert(new BasicAlert(patientIdText, "Triggered alert", latestAlertState.getTimestamp()));
        }

    }

    // Updated the Javadoc for clarity and readability in line with the Google Java Style Guide
    /**
     * Triggers an alert for the monitoring system.
     *
     * <p>This method is intended to handle alert delivery, such as logging the
     * event or notifying medical staff.
     *
     * @param alert the alert containing information about the detected condition
     */
    private void triggerAlert(Alert alert) {
        for (Alert existingAlert : triggeredAlerts) {
            boolean samePatient = existingAlert.getPatientId().equals(alert.getPatientId());
            boolean sameCondition = existingAlert.getCondition().equals(alert.getCondition());
            if (samePatient && sameCondition) {
                return;
            }
        }

        triggeredAlerts.add(alert);

    }

    private void clearAlertsForPatient(int patientId) {
        String patientIdText = Integer.toString(patientId);
        triggeredAlerts.removeIf(alert -> alert.getPatientId().equals(patientIdText));
    }

    private List<PatientRecord> getRecordsByType(List<PatientRecord> allRecords, String recordType) {
        List<PatientRecord> filteredRecords = new ArrayList<>();
        for (PatientRecord record : allRecords) {
            if (recordType.equals(record.getRecordType())) {
                filteredRecords.add(record);
            }
        }
        filteredRecords.sort(Comparator.comparingLong(PatientRecord::getTimestamp));
        return filteredRecords;
    }

    private PatientRecord findTrendRecord(List<PatientRecord> records, boolean increasing) {
        for (int index = 2; index < records.size(); index++) {
            double first = records.get(index - 2).getMeasurementValue();
            double second = records.get(index - 1).getMeasurementValue();
            double third = records.get(index).getMeasurementValue();

            if (increasing && second - first > 10.0 && third - second > 10.0) {
                return records.get(index);
            }

            if (!increasing && first - second > 10.0 && second - third > 10.0) {
                return records.get(index);
            }
        }

        return null;
    }

    private PatientRecord findAlertRecord(List<PatientRecord> records, AlertStrategy alertStrategy) {
        for (PatientRecord record : records) {
            if (alertStrategy.checkAlert(record.getMeasurementValue())) {
                return record;
            }
        }
        return null;
    }

    private PatientRecord findRapidDropRecord(List<PatientRecord> records) {
        for (int startIndex = 0; startIndex < records.size(); startIndex++) {
            PatientRecord startRecord = records.get(startIndex);
            for (int endIndex = startIndex + 1; endIndex < records.size(); endIndex++) {
                PatientRecord endRecord = records.get(endIndex);
                long timeDifference = endRecord.getTimestamp() - startRecord.getTimestamp();
                if (timeDifference > TEN_MINUTES_IN_MILLIS) {
                    break;
                }

                if (startRecord.getMeasurementValue() - endRecord.getMeasurementValue() >= 5.0) {
                    return endRecord;
                }
            }
        }
        return null;
    }

    private long findHypotensiveHypoxemiaTimestamp(List<PatientRecord> systolicRecords,
            List<PatientRecord> saturationRecords) {
        // The manual does not define simultaneity, so the combined alert is treated as a
        // clinically related event when both low readings occur within the same
        // 10-minute monitoring window.
        for (PatientRecord systolicRecord : systolicRecords) {
            if (!lowSystolicBloodPressureStrategy.checkAlert(systolicRecord.getMeasurementValue())) {
                continue;
            }

            for (PatientRecord saturationRecord : saturationRecords) {
                if (!oxygenSaturationStrategy.checkAlert(saturationRecord.getMeasurementValue())) {
                    continue;
                }

                long difference = Math.abs(saturationRecord.getTimestamp() - systolicRecord.getTimestamp());
                if (difference <= TEN_MINUTES_IN_MILLIS) {
                    return Math.max(systolicRecord.getTimestamp(), saturationRecord.getTimestamp());
                }
            }
        }

        return -1L;
    }

    private PatientRecord findAbnormalEcgPeakRecord(List<PatientRecord> records) {
        for (int index = ECG_WINDOW_SIZE; index < records.size(); index++) {
            double averageMagnitude = 0.0;
            for (int windowIndex = index - ECG_WINDOW_SIZE; windowIndex < index; windowIndex++) {
                averageMagnitude += Math.abs(records.get(windowIndex).getMeasurementValue());
            }
            averageMagnitude /= ECG_WINDOW_SIZE;

            double currentMagnitude = Math.abs(records.get(index).getMeasurementValue());
            boolean aboveMultiplier = currentMagnitude >= averageMagnitude * ECG_PEAK_MULTIPLIER;
            boolean aboveMinimumDelta = currentMagnitude - averageMagnitude >= ECG_PEAK_MIN_DELTA;

            if (aboveMultiplier && aboveMinimumDelta) {
                return records.get(index);
            }
        }
        return null;
    }

    private PatientRecord findLatestAlertState(List<PatientRecord> alertRecords) {
        if (alertRecords.isEmpty()) {
            return null;
        }
        return alertRecords.get(alertRecords.size() - 1);
    }

    private PatientRecord latestRecord(PatientRecord firstRecord, PatientRecord secondRecord) {
        if (firstRecord == null) {
            return secondRecord;
        }
        if (secondRecord == null) {
            return firstRecord;
        }
        return firstRecord.getTimestamp() >= secondRecord.getTimestamp() ? firstRecord : secondRecord;
    }
}
