package com.alerts;

/**
 * Alert used for blood oxygen problems.
 */
public class BloodOxygenAlert implements Alert {
    private String patientId;
    private String condition;
    private long timestamp;

    /**
     * Creates a blood oxygen alert.
     *
     * @param patientId the patient ID
     * @param condition the alert condition
     * @param timestamp the alert timestamp
     */
    public BloodOxygenAlert(String patientId, String condition, long timestamp) {
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
        return "Blood oxygen alert for patient " + patientId + ": " + condition;
    }
}
