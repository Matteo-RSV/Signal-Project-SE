package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Defines the contract for classes that generate simulated patient data.
 *
 * <p>Implementations of this interface are responsible for producing a specific
 * type of patient measurement and sending the generated output through the
 * provided {@link OutputStrategy}.
 */
public interface PatientDataGenerator {

    /**
     * Generates simulated data for the specified patient and sends it to the
     * configured output destination.
     *
     * @param patientId the unique identifier of the patient for whom data is generated
     * @param outputStrategy the strategy used to output the generated patient data
     */
    void generate(int patientId, OutputStrategy outputStrategy);
    
}