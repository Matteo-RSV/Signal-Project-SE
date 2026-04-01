package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

public class FileOutputStrategy implements OutputStrategy {

    //Renamed the field to camelCase to follow the correct java style: BaseDirectory -> baseDirectory
    private String baseDirectory;

    // Renamed the field to camelCase to follow the correct java style: file_map -> fileMap 
    // Reduced the field's visibility to private (public -> private) to improve encapsulation and follow the java style for best practices
    private final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    public FileOutputStrategy(String baseDirectory) {

        // Updated constructor assignment to match the corrected camelCase field name (baseDirectory)
        this.baseDirectory = baseDirectory;

    }

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