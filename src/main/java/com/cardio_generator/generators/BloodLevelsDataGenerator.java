package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generates simulated blood level data for patients in the health monitoring
 * simulation.
 *
 * <p>This class produces three laboratory-style measurements for each patient:
 * cholesterol, white blood cell count, and red blood cell count. Each patient is
 * assigned a baseline value for each measurement when the generator is created.
 * Later generated values vary slightly around those baselines to simulate more
 * realistic repeated measurements over time.
 *
 * <p>The generated values are sent to the provided {@link OutputStrategy} rather
 * than being returned directly. As a result, calling the generation method has
 * the side effect of outputting three data points for the specified patient.
 */
public class BloodLevelsDataGenerator implements PatientDataGenerator {

    /** Random number generator used to initialize and vary simulated blood values. */
    private static final Random random = new Random();

    /**
     * Stores each patient's baseline cholesterol value.
     *
     * <p>The array uses patient IDs as indices, so index 0 is intentionally unused.
     */
    private final double[] baselineCholesterol;

    /**
     * Stores each patient's baseline white blood cell count.
     *
     * <p>The array uses patient IDs as indices, so index 0 is intentionally unused.
     */
    private final double[] baselineWhiteCells;

    /**
     * Stores each patient's baseline red blood cell count.
     *
     * <p>The array uses patient IDs as indices, so index 0 is intentionally unused.
     */
    private final double[] baselineRedCells;

    /**
     * Creates a blood level generator and initializes baseline values for each
     * patient.
     *
     * <p>For every patient ID from 1 through {@code patientCount}, this constructor
     * assigns initial baseline values for cholesterol, white blood cells, and red
     * blood cells. These baseline values are later used as the center point for
     * small random variations during generation.
     *
     * @param patientCount the number of patients for whom baseline blood values are
     *                     initialized; expected to be a positive number matching the
     *                     patient IDs used by the simulator
     */
    public BloodLevelsDataGenerator(int patientCount) {
        
        baselineCholesterol = new double[patientCount + 1];
        baselineWhiteCells = new double[patientCount + 1];
        baselineRedCells = new double[patientCount + 1];

        for (int i = 1; i <= patientCount; i++) {

            baselineCholesterol[i] = 150 + random.nextDouble() * 50;
            baselineWhiteCells[i] = 4 + random.nextDouble() * 6;
            baselineRedCells[i] = 4.5 + random.nextDouble() * 1.5;

        }

    }

    /**
     * Generates simulated blood level measurements for a single patient and outputs
     * them using the supplied output strategy.
     *
     * <p>This method creates values near the patient's stored baseline values to
     * simulate realistic variation over time. It generates and outputs three
     * measurements: cholesterol, white blood cells, and red blood cells. Each
     * measurement is emitted with the current system time as its timestamp.
     *
     * <p>This method does not return any value. Its primary side effect is sending
     * the generated data to the given {@link OutputStrategy}.
     *
     * @param patientId the ID of the patient whose blood level data is generated;
     *                  this ID is expected to correspond to a valid initialized index
     *                  in the baseline arrays
     * @param outputStrategy the output mechanism used to publish the generated blood
     *                       level measurements
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {

            double cholesterol = baselineCholesterol[patientId] + (random.nextDouble() - 0.5) * 10;
            double whiteCells = baselineWhiteCells[patientId] + (random.nextDouble() - 0.5) * 1;
            double redCells = baselineRedCells[patientId] + (random.nextDouble() - 0.5) * 0.2;

            outputStrategy.output(patientId, System.currentTimeMillis(), "Cholesterol", Double.toString(cholesterol));
            outputStrategy.output(patientId, System.currentTimeMillis(), "WhiteBloodCells", Double.toString(whiteCells));
            outputStrategy.output(patientId, System.currentTimeMillis(), "RedBloodCells", Double.toString(redCells));

        } catch (Exception e) {

            System.err.println("An error occurred while generating blood levels data for patient " + patientId);
            e.printStackTrace();

        }
    }
}