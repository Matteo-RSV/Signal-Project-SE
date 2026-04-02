package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generates simulated blood oxygen saturation data for patients.
 *
 * <p>This class maintains the last known saturation value for each patient and
 * produces small variations over time to simulate realistic changes in blood
 * oxygen levels. Generated values are constrained to a healthy range before
 * being sent to the configured {@link OutputStrategy}.
 */
public class BloodSaturationDataGenerator implements PatientDataGenerator {

    /** Random number generator used to simulate small saturation changes. */
    private static final Random random = new Random();

    /** Stores the most recent saturation value for each patient. */
    private int[] lastSaturationValues;

    /**
     * Creates a blood saturation generator for the specified number of patients.
     *
     * <p>Each patient is initialized with a baseline saturation value between
     * 95% and 100%.
     *
     * @param patientCount the number of patients for whom saturation data will be generated
     */
    public BloodSaturationDataGenerator(int patientCount) {

        lastSaturationValues = new int[patientCount + 1];

        // Initialize with baseline saturation values for each patient
        for (int i = 1; i <= patientCount; i++) {

            lastSaturationValues[i] = 95 + random.nextInt(6); // Initializes with a value between 95 and 100

        }
        
    }

    /**
     * Generates a new blood saturation reading for the specified patient and
     * sends it to the provided output strategy.
     *
     * <p>The generated value is based on the patient’s previous reading with a
     * small random fluctuation. The result is limited to the range 90% to 100%
     * before being output.
     *
     * @param patientId the unique identifier of the patient whose saturation data is generated
     * @param outputStrategy the strategy used to output the generated saturation value
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {

            // Simulate blood saturation values
            int variation = random.nextInt(3) - 1; // -1, 0, or 1 to simulate small fluctuations
            int newSaturationValue = lastSaturationValues[patientId] + variation;

            // Ensure the saturation stays within a realistic and healthy range
            newSaturationValue = Math.min(Math.max(newSaturationValue, 90), 100);
            lastSaturationValues[patientId] = newSaturationValue;
            outputStrategy.output(patientId, System.currentTimeMillis(), "Saturation",
                    Double.toString(newSaturationValue) + "%");

        } catch (Exception e) {

            System.err.println("An error occurred while generating blood saturation data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.

        }
    }
}