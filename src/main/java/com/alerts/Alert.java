package com.alerts;

/**
 * Represents an alert generated for a patient.
 *
 * <p>An alert contains information about the patient, the condition that triggered
 * the alert, and the time at which the alert occurred. This class acts as a data
 * container used by the alert system.
 */
public class Alert {

    /** The unique identifier of the patient associated with the alert. */
    private String patientId;

    /** The condition or event that triggered the alert. */
    private String condition;

    /** The timestamp indicating when the alert was generated. */
    private long timestamp;

    /**
     * Constructs a new {@code Alert} with the specified details.
     *
     * @param patientId the identifier of the patient
     * @param condition the condition that triggered the alert
     * @param timestamp the time at which the alert occurred
     */
    public Alert(String patientId, String condition, long timestamp) {

        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
        
    }

    /**
     * Returns the identifier of the patient associated with this alert.
     *
     * @return the patient ID as a string
     */
    public String getPatientId() {

        return patientId;

    }

    /**
     * Returns the condition that triggered this alert.
     *
     * @return the alert condition
     */
    public String getCondition() {

        return condition;

    }

    /**
     * Returns the timestamp of when the alert was generated.
     *
     * @return the alert timestamp in milliseconds
     */
    public long getTimestamp() {

        return timestamp;

    }
}