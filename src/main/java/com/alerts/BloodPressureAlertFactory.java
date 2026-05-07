package com.alerts;

/**
 * Factory for blood pressure alerts.
 */
public class BloodPressureAlertFactory implements AlertFactory {

    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new BloodPressureAlert(patientId, condition, timestamp);
    }
}
