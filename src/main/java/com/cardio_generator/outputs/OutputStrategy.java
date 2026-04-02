package com.cardio_generator.outputs;

/**
 * Defines a strategy for outputting simulated patient data.
 *
 * <p>Implementations of this interface determine how and where generated
 * patient data is delivered, such as to the console, files, network sockets,
 * or other destinations.
 */
public interface OutputStrategy {

    /**
     * Outputs a single piece of patient data using a specific strategy.
     *
     * <p>This method is called by data generators to deliver simulated
     * measurements to the chosen output destination.
     *
     * @param patientId the unique identifier of the patient associated with the data
     * @param timestamp the time at which the data was generated, typically in milliseconds
     * @param label the type or category of the data (e.g., ECG, blood pressure)
     * @param data the generated data value formatted as a string
     */
    void output(int patientId, long timestamp, String label, String data);
    
}