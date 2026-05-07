package com.alerts;

/**
 * Checks heart rate values against simple thresholds.
 */
public class HeartRateStrategy implements AlertStrategy {
    private static final double LOW_HEART_RATE = 50.0;
    private static final double HIGH_HEART_RATE = 120.0;

    @Override
    public boolean checkAlert(double value) {
        return value < LOW_HEART_RATE || value > HIGH_HEART_RATE;
    }
}
