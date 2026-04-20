package com.cardio_generator;

import java.io.IOException;
import java.util.Arrays;

import com.data_management.DataStorage;

/**
 * Selects which project entry point to run from the packaged jar.
 */
public class Main {

    /**
     * Runs either {@link DataStorage} or {@link HealthDataSimulator} based on the
     * first command-line argument.
     *
     * @param args command-line arguments
     * @throws IOException if the selected workflow encounters an I/O error
     */
    public static void main(String[] args) throws IOException {
        if (args.length > 0 && ("-h".equals(args[0]) || "--help".equals(args[0]))) {
            printUsage();
            return;
        }

        if (args.length > 0 && "DataStorage".equals(args[0])) {
            DataStorage.main(Arrays.copyOfRange(args, 1, args.length));
            return;
        }

        if (args.length > 0 && "HealthDataSimulator".equals(args[0])) {
            HealthDataSimulator.main(Arrays.copyOfRange(args, 1, args.length));
            return;
        }

        HealthDataSimulator.main(args);
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  java -jar cardio_generator-1.0-SNAPSHOT.jar HealthDataSimulator [simulator options]");
        System.out.println("  java -jar cardio_generator-1.0-SNAPSHOT.jar DataStorage <input-directory>");
        System.out.println();
        System.out.println("If no entry point is specified, HealthDataSimulator is used.");
    }
}
