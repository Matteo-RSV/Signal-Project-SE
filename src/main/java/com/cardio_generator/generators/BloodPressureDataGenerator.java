package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generates simulated blood pressure data for patients.
 *
 * <p>This class produces systolic and diastolic blood pressure readings
 * that vary slightly over time to simulate realistic physiological changes.
 * Values are constrained to medically reasonable ranges before being output.
 */
public class BloodPressureDataGenerator implements PatientDataGenerator {

    /** Random number generator used to simulate variations in blood pressure. */
    private static final Random random = new Random();

    /** Stores the most recent systolic pressure value for each patient. */
    private int[] lastSystolicValues;

    /** Stores the most recent diastolic pressure value for each patient. */
    private int[] lastDiastolicValues;

    /**
     * Creates a blood pressure data generator for the specified number of patients.
     *
     * <p>Each patient is initialized with baseline systolic and diastolic values
     * within typical healthy ranges.
     *
     * @param patientCount the number of patients for whom data will be generated
     */
    public BloodPressureDataGenerator(int patientCount) {

        lastSystolicValues = new int[patientCount + 1];
        lastDiastolicValues = new int[patientCount + 1];

        // Initialize with baseline values for each patient
        for (int i = 1; i <= patientCount; i++) {

            lastSystolicValues[i] = 110 + random.nextInt(20); // 110–130
            lastDiastolicValues[i] = 70 + random.nextInt(15); // 70–85

        }
        
    }

    /**
     * Generates new blood pressure readings for the specified patient and outputs them.
     *
     * <p>The method simulates small variations from the patient’s previous values.
     * The resulting systolic pressure is constrained between 90 and 180 mmHg,
     * and diastolic pressure is constrained between 60 and 120 mmHg.
     *
     * <p>Two outputs are generated:
     * <ul>
     *     <li>Systolic pressure</li>
     *     <li>Diastolic pressure</li>
     * </ul>
     *
     * @param patientId the unique identifier of the patient
     * @param outputStrategy the strategy used to output the generated data
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {

            int systolicVariation = random.nextInt(5) - 2; // -2 to +2
            int diastolicVariation = random.nextInt(5) - 2;

            int newSystolicValue = lastSystolicValues[patientId] + systolicVariation;
            int newDiastolicValue = lastDiastolicValues[patientId] + diastolicVariation;

            // Ensure the blood pressure stays within a realistic and safe range
            newSystolicValue = Math.min(Math.max(newSystolicValue, 90), 180);
            newDiastolicValue = Math.min(Math.max(newDiastolicValue, 60), 120);

            lastSystolicValues[patientId] = newSystolicValue;
            lastDiastolicValues[patientId] = newDiastolicValue;

            outputStrategy.output(patientId, System.currentTimeMillis(), "SystolicPressure", Double.toString(newSystolicValue));


            outputStrategy.output(patientId, System.currentTimeMillis(), "DiastolicPressure", Double.toString(newDiastolicValue));

        } catch (Exception e) {

            System.err.println("An error occurred while generating blood pressure data for patient " + patientId);
            e.printStackTrace();

        }
    }
}