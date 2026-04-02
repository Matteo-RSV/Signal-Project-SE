package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of {@link OutputStrategy} that writes simulated patient data to files.
 *
 * <p>This class stores output in text files within a specified base directory.
 * Each type of data (label) is written to a separate file. Data is appended
 * to the file if it already exists.
 */
public class FileOutputStrategy implements OutputStrategy {

    // Renamed the field to camelCase to follow the correct java style: BaseDirectory -> baseDirectory
    private String baseDirectory;

    // Renamed the field to camelCase to follow the correct java style: file_map -> fileMap 
    // Reduced the field's visibility to private (public -> private) to improve encapsulation and follow the java style for best practices
    private final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    /**
     * Constructs a file-based output strategy using the specified directory.
     *
     * @param baseDirectory the directory where output files will be created and stored
     */
    public FileOutputStrategy(String baseDirectory) {

        // Updated constructor assignment to match the corrected camelCase field name (baseDirectory)
        this.baseDirectory = baseDirectory;

    }

    /**
     * Outputs patient data by writing it to a file corresponding to the data label.
     *
     * <p>If the output directory does not exist, it will be created. Each label
     * is associated with a specific file, and data is appended to that file.
     *
     * @param patientId the unique identifier of the patient
     * @param timestamp the time at which the data was generated
     * @param label the type of data (e.g., ECG, blood pressure)
     * @param data the generated data value formatted as a string
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {

        try {

            // Creates the base output directory 
            Files.createDirectories(Paths.get(baseDirectory));

        } catch (IOException e) {

            System.err.println("Error creating base directory: " + e.getMessage());

            return;

        }

        // Computes the output file path for the given label
        // Renamed the local variable to camelCase to follow the correct java style: FilePath -> filePath
        String filePath = fileMap.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        // Adds the generated patient data to the output file
        try (PrintWriter out = new PrintWriter(

                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {

            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);

        // Narrowed the exception handling to IOException for clearer and more precise error handling    
        } catch (IOException e) {

            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());

        }
    }
}