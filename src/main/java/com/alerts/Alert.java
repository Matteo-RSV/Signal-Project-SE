package com.alerts;

/**
 * Represents a simple alert in the monitoring system.
 */
public interface Alert {

    /**
     * Returns the patient ID connected to this alert.
     *
     * @return the patient ID
     */
    String getPatientId();

    /**
     * Returns the alert condition.
     *
     * @return the condition
     */
    String getCondition();

    /**
     * Returns the time when the alert happened.
     *
     * @return the timestamp
     */
    long getTimestamp();

    /**
     * Returns a readable alert message.
     *
     * @return the message
     */
    String getMessage();
}
