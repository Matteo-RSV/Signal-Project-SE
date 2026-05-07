package com.alerts;

/**
 * Checks whether a value should trigger an alert.
 */
public interface AlertStrategy {

    /**
     * Returns true if the value should trigger an alert.
     *
     * @param value the value to check
     * @return true when the alert condition is met
     */
    boolean checkAlert(double value);
}
