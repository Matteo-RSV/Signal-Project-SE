package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generates simulated electrocardiogram (ECG) data for patients.
 *
 * <p>This class produces ECG signal values that mimic a simplified cardiac
 * waveform. The waveform is generated using sinusoidal components representing
 * different parts of a heartbeat (P wave, QRS complex, and T wave), combined
 * with small random noise to simulate variability.
 *
 * <p>The generator maintains the last ECG value for each patient to support
 * continuous signal generation over time. Generated values are sent to an
 * {@link OutputStrategy}, meaning this class has the side effect of outputting
 * data rather than returning it.
 */
public class ECGDataGenerator implements PatientDataGenerator {

    /** Random number generator used for waveform variability and noise. */
    private static final Random random = new Random();

    /**
     * Stores the last generated ECG value for each patient.
     *
     * <p>The array is indexed by patient ID, with index 0 unused.
     */
    private double[] lastEcgValues;

    /** Constant value of PI used in waveform calculations. */
    private static final double PI = Math.PI;

    /**
     * Constructs an ECG data generator and initializes ECG values for each patient.
     *
     * <p>Each patient is assigned an initial ECG value of 0. These values are
     * updated over time as new ECG data is generated.
     *
     * @param patientCount the number of patients for whom ECG data will be generated;
     *                     expected to match the IDs used in the simulation
     */
    public ECGDataGenerator(int patientCount) {
        
        lastEcgValues = new double[patientCount + 1];

        for (int i = 1; i <= patientCount; i++) {

            lastEcgValues[i] = 0;

        }
    }

    /**
     * Generates a simulated ECG value for a given patient and outputs it.
     *
     * <p>This method computes a new ECG value using a simplified waveform model
     * and sends it to the provided {@link OutputStrategy}. The generated value is
     * also stored as the last ECG value for the patient to support continuity in
     * future calculations.
     *
     * <p>This method does not return a value. Its primary effect is outputting ECG
     * data with a timestamp.
     *
     * @param patientId the ID of the patient for whom ECG data is generated;
     *                  must correspond to a valid index in the internal arrays
     * @param outputStrategy the output mechanism used to publish the ECG data
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {

            double ecgValue = simulateEcgWaveform(patientId, lastEcgValues[patientId]);

            outputStrategy.output(patientId, System.currentTimeMillis(), "ECG", Double.toString(ecgValue));

            lastEcgValues[patientId] = ecgValue;

        } catch (Exception e) {

            System.err.println("An error occurred while generating ECG data for patient " + patientId);
            e.printStackTrace();

        }
    }

    /**
     * Simulates an ECG waveform value using sinusoidal components.
     *
     * <p>This method generates a simplified ECG signal by combining:
     * <ul>
     *     <li>P wave (low amplitude sinusoid)</li>
     *     <li>QRS complex (higher frequency sinusoid)</li>
     *     <li>T wave (phase-shifted sinusoid)</li>
     * </ul>
     * along with small random noise to introduce variability.
     *
     * <p>The waveform frequency is based on a simulated heart rate between
     * approximately 60 and 80 beats per minute. The current system time is used
     * to create a continuously evolving signal.
     *
     * @param patientId the ID of the patient (not directly used in calculation,
     *                  but included for consistency with generator design)
     * @param lastEcgValue the previously generated ECG value for the patient;
     *                     represents the prior state of the signal
     * @return a double value representing the simulated ECG signal at the current time
     */
    private double simulateEcgWaveform(int patientId, double lastEcgValue) {

        double hr = 60.0 + random.nextDouble() * 20.0;
        double t = System.currentTimeMillis() / 1000.0;
        double ecgFrequency = hr / 60.0;

        double pWave = 0.1 * Math.sin(2 * PI * ecgFrequency * t);
        double qrsComplex = 0.5 * Math.sin(2 * PI * 3 * ecgFrequency * t);
        double tWave = 0.2 * Math.sin(2 * PI * 2 * ecgFrequency * t + PI / 4);

        return pWave + qrsComplex + tWave + random.nextDouble() * 0.05;

    }
}