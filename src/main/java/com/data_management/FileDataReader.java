package com.data_management;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads simulator output files from a directory and stores the parsed records in
 * {@link DataStorage}.
 */
public class FileDataReader implements DataReader {
    private static final Pattern OUTPUT_PATTERN = Pattern.compile(
            "Patient ID: (\\d+), Timestamp: (\\d+), Label: ([^,]+), Data: (.+)");

    private final Path inputDirectory;

    /**
     * Creates a reader for simulator output files stored in a directory.
     *
     * @param directoryPath the directory that contains output files
     */
    public FileDataReader(String directoryPath) {
        this.inputDirectory = Paths.get(directoryPath);
    }

    /**
     * Reads each output file in the configured directory and stores valid records.
     *
     * @param dataStorage the storage where parsed data is written
     * @throws IOException if the directory cannot be read
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        if (!Files.isDirectory(inputDirectory)) {
            throw new IOException("Input directory does not exist: " + inputDirectory);
        }

        try (DirectoryStream<Path> files = Files.newDirectoryStream(inputDirectory, "*.txt")) {
            for (Path file : files) {
                for (String line : Files.readAllLines(file)) {
                    parseLine(line, dataStorage);
                }
            }
        }
    }

    private void parseLine(String line, DataStorage dataStorage) {
        Matcher matcher = OUTPUT_PATTERN.matcher(line.trim());
        if (!matcher.matches()) {
            return;
        }

        int patientId = Integer.parseInt(matcher.group(1));
        long timestamp = Long.parseLong(matcher.group(2));
        String label = matcher.group(3).trim();
        String data = matcher.group(4).trim();

        Double measurementValue = parseMeasurementValue(label, data);
        if (measurementValue == null) {
            return;
        }

        dataStorage.addPatientData(patientId, measurementValue, label, timestamp);
    }

    private Double parseMeasurementValue(String label, String data) {
        if ("Alert".equals(label)) {
            // The simulator writes alert button states as strings, so they are mapped
            // to numeric values that still fit the existing PatientRecord design.
            if ("triggered".equalsIgnoreCase(data)) {
                return 1.0;
            }
            if ("resolved".equalsIgnoreCase(data)) {
                return 0.0;
            }
        }

        String numericValue = data.endsWith("%") ? data.substring(0, data.length() - 1) : data;

        try {
            return Double.parseDouble(numericValue);
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
