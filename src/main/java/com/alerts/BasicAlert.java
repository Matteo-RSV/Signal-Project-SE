package com.alerts;

/**
 * A simple alert class for general alert types.
 */
public class BasicAlert implements Alert {
    private String patientId;
    private String condition;
    private long timestamp;

    /**
     * Creates a basic alert.
     *
     * @param patientId the patient ID
     * @param condition the alert condition
     * @param timestamp the alert timestamp
     */
    public BasicAlert(String patientId, String condition, long timestamp) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
    }

    @Override
    public String getPatientId() {
        return patientId;
    }

    @Override
    public String getCondition() {
        return condition;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String getMessage() {
        return "Alert for patient " + patientId + ": " + condition;
    }
}
