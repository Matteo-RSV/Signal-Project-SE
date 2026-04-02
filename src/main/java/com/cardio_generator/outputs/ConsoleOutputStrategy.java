package com.cardio_generator.outputs;

/**
 * Outputs generated patient data directly to the console.
 *
 * <p>This class is a concrete implementation of the {@link OutputStrategy}
 * interface. It formats simulated patient data as a single line of text and
 * prints it to standard output.
 *
 * <p>This strategy is useful for simple execution, debugging, and verifying
 * that the simulator is generating data correctly without writing to files
 * or sending data over a network.
 */
public class ConsoleOutputStrategy implements OutputStrategy {

    /**
     * Outputs one generated data item for a patient to the console.
     *
     * <p>The output includes the patient ID, timestamp, label, and data value,
     * formatted as a single human-readable line. This method does not return a
     * value. Its main side effect is printing text to standard output.
     *
     * @param patientId the ID of the patient associated with the generated data
     * @param timestamp the time at which the data was generated, represented as
     *                  milliseconds since the Unix epoch
     * @param label the name of the measurement or data type being output, such as
     *              {@code ECG} or {@code Cholesterol}
     * @param data the generated value to output, represented as a string
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {

        System.out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
                
    }
}