package com.alerts;

/**
 * Checks blood pressure values against low and high thresholds.
 */
public class BloodPressureStrategy implements AlertStrategy {
    private double lowThreshold;
    private double highThreshold;

    /**
     * Creates a blood pressure strategy with simple thresholds.
     *
     * @param lowThreshold the low alert threshold
     * @param highThreshold the high alert threshold
     */
    public BloodPressureStrategy(double lowThreshold, double highThreshold) {
        this.lowThreshold = lowThreshold;
        this.highThreshold = highThreshold;
    }

    @Override
    public boolean checkAlert(double value) {
        return value < lowThreshold || value > highThreshold;
    }
}
