package com.alerts;

/**
 * Creates alert objects.
 */
public interface AlertFactory {

    /**
     * Creates an alert with the given values.
     *
     * @param patientId the patient ID
     * @param condition the alert condition
     * @param timestamp the alert timestamp
     * @return the created alert
     */
    Alert createAlert(String patientId, String condition, long timestamp);
}
