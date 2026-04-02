package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generates simulated alert events for patients.
 *
 * <p>This class models alert behavior where alerts can be triggered and later resolved.
 * Each patient has an associated alert state that changes over time based on
 * probabilistic conditions.
 */
public class AlertGenerator implements PatientDataGenerator {

    /** Random number generator used to simulate alert triggering and resolution. */
    public static final Random randomGenerator = new Random();

    /** Tracks the current alert state for each patient (true = active, false = resolved). */
    private boolean[] AlertStates; // false = resolved, true = pressed

    /**
     * Creates an alert generator for the specified number of patients.
     *
     * @param patientCount the number of patients whose alert states will be tracked
     */
    public AlertGenerator(int patientCount) {

        AlertStates = new boolean[patientCount + 1];
        
    }

    /**
     * Generates an alert event for the specified patient and sends it to the output strategy.
     *
     * <p>If an alert is already active, there is a high probability (90%) that it will be resolved.
     * If no alert is active, a new alert may be triggered based on a probability derived from
     * a Poisson-like process.
     *
     * @param patientId the unique identifier of the patient
     * @param outputStrategy the strategy used to output the generated alert event
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {

        try {

            if (AlertStates[patientId]) {

                // If alert is active, simulate resolution with high probability
                if (randomGenerator.nextDouble() < 0.9) { // 90% chance to resolve
                    AlertStates[patientId] = false;

                    // Output the alert resolution event
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }

            } else {

                // Probability-based triggering using an exponential distribution model
                double Lambda = 0.1; // Average rate of alerts
                double p = -Math.expm1(-Lambda); // Probability of at least one alert
                boolean alertTriggered = randomGenerator.nextDouble() < p;

                if (alertTriggered) {
                    AlertStates[patientId] = true;

                    // Output the alert trigger event
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }

        } catch (Exception e) {

            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();

        }
    }
}