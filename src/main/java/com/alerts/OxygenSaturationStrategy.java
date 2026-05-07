package com.alerts;

/**
 * Checks oxygen saturation values against a low threshold.
 */
public class OxygenSaturationStrategy implements AlertStrategy {
    private static final double LOW_SATURATION = 92.0;

    @Override
    public boolean checkAlert(double value) {
        return value < LOW_SATURATION;
    }
}
