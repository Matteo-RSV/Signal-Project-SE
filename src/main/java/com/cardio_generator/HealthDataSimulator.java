package com.cardio_generator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.cardio_generator.generators.AlertGenerator;
import com.cardio_generator.generators.BloodPressureDataGenerator;
import com.cardio_generator.generators.BloodSaturationDataGenerator;
import com.cardio_generator.generators.BloodLevelsDataGenerator;
import com.cardio_generator.generators.ECGDataGenerator;
import com.cardio_generator.outputs.ConsoleOutputStrategy;
import com.cardio_generator.outputs.FileOutputStrategy;
import com.cardio_generator.outputs.OutputStrategy;
import com.cardio_generator.outputs.TcpOutputStrategy;
import com.cardio_generator.outputs.WebSocketOutputStrategy;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * The {@code HealthDataSimulator} class is the main entry point of the application.
 * It is responsible for simulating real-time patient health data by generating
 * different types of medical measurements (e.g., ECG, blood pressure, saturation).
 *
 * <p>The simulator schedules periodic tasks for multiple patients and outputs
 * the generated data using a configurable {@link OutputStrategy}.
 *
 * <p>Supported output types include console, file, WebSocket, and TCP socket output.
 */
public class HealthDataSimulator {

    /** Default number of patients to simulate. */
    private static int patientCount = 50;

    /** Scheduler used to run periodic data generation tasks. */
    private static ScheduledExecutorService scheduler;

    /** Strategy used to output generated data. Defaults to console output. */
    private static OutputStrategy outputStrategy = new ConsoleOutputStrategy();

    /** Random number generator used for scheduling variability. */
    private static final Random random = new Random();

    /**
     * Main method that starts the health data simulation.
     *
     * <p>This method parses command-line arguments, initializes patient IDs,
     * and schedules data generation tasks for each patient.
     *
     * @param args command-line arguments specifying simulation configuration
     * @throws IOException if there is an error creating output directories
     */
    public static void main(String[] args) throws IOException {

        parseArguments(args);

        scheduler = Executors.newScheduledThreadPool(patientCount * 4);

        List<Integer> patientIds = initializePatientIds(patientCount);
        Collections.shuffle(patientIds);

        scheduleTasksForPatients(patientIds);
    }

    /**
     * Parses command-line arguments to configure the simulator.
     *
     * <p>Supported options include:
     * <ul>
     *     <li>{@code -h}: Displays help information</li>
     *     <li>{@code --patient-count <count>}: Sets number of simulated patients</li>
     *     <li>{@code --output <type>}: Defines output method</li>
     * </ul>
     *
     * @param args command-line arguments provided to the program
     * @throws IOException if file output directories cannot be created
     */
    private static void parseArguments(String[] args) throws IOException {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-h":
                    printHelp();
                    System.exit(0);
                    break;

                case "--patient-count":
                    if (i + 1 < args.length) {
                        try {
                            patientCount = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException e) {
                            System.err.println(
                                    "Error: Invalid number of patients. Using default value: " + patientCount);
                        }
                    }
                    break;

                case "--output":
                    if (i + 1 < args.length) {
                        String outputArg = args[++i];

                        if (outputArg.equals("console")) {
                            outputStrategy = new ConsoleOutputStrategy();

                        } else if (outputArg.startsWith("file:")) {
                            String baseDirectory = outputArg.substring(5);
                            Path outputPath = Paths.get(baseDirectory);

                            if (!Files.exists(outputPath)) {
                                Files.createDirectories(outputPath);
                            }

                            outputStrategy = new FileOutputStrategy(baseDirectory);

                        } else if (outputArg.startsWith("websocket:")) {
                            try {
                                int port = Integer.parseInt(outputArg.substring(10));
                                outputStrategy = new WebSocketOutputStrategy(port);
                                System.out.println("WebSocket output will be on port: " + port);
                            } catch (NumberFormatException e) {
                                System.err.println(
                                        "Invalid port for WebSocket output. Please specify a valid port number.");
                            }

                        } else if (outputArg.startsWith("tcp:")) {
                            try {
                                int port = Integer.parseInt(outputArg.substring(4));
                                outputStrategy = new TcpOutputStrategy(port);
                                System.out.println("TCP socket output will be on port: " + port);
                            } catch (NumberFormatException e) {
                                System.err.println(
                                        "Invalid port for TCP output. Please specify a valid port number.");
                            }

                        } else {
                            System.err.println("Unknown output type. Using default (console).");
                        }
                    }
                    break;

                default:
                    System.err.println("Unknown option '" + args[i] + "'");
                    printHelp();
                    System.exit(1);
            }
        }
    }

    /**
     * Prints usage instructions and available command-line options to the console.
     */
    private static void printHelp() {
        System.out.println("Usage: java HealthDataSimulator [options]");
        System.out.println("Options:");
        System.out.println("  -h                       Show help and exit.");
        System.out.println(
                "  --patient-count <count>  Specify the number of patients to simulate data for (default: 50).");
        System.out.println("  --output <type>          Define the output method. Options are:");
        System.out.println("                             'console' for console output,");
        System.out.println("                             'file:<directory>' for file output,");
        System.out.println("                             'websocket:<port>' for WebSocket output,");
        System.out.println("                             'tcp:<port>' for TCP socket output.");
    }

    /**
     * Initializes a list of patient IDs from 1 to the specified count.
     *
     * @param patientCount the number of patients to simulate
     * @return a list of sequential patient IDs
     */
    private static List<Integer> initializePatientIds(int patientCount) {

        List<Integer> patientIds = new ArrayList<>();
        for (int i = 1; i <= patientCount; i++) {
            
            patientIds.add(i);

        }
        
        return patientIds;
    }

    /**
     * Schedules periodic data generation tasks for each patient.
     *
     * <p>Each patient will have multiple generators producing different types
     * of medical data at different time intervals.
     *
     * @param patientIds list of patient IDs for which tasks are scheduled
     */
    private static void scheduleTasksForPatients(List<Integer> patientIds) {

        ECGDataGenerator ecgDataGenerator = new ECGDataGenerator(patientCount);
        BloodSaturationDataGenerator bloodSaturationDataGenerator = new BloodSaturationDataGenerator(patientCount);
        BloodPressureDataGenerator bloodPressureDataGenerator = new BloodPressureDataGenerator(patientCount);
        BloodLevelsDataGenerator bloodLevelsDataGenerator = new BloodLevelsDataGenerator(patientCount);
        AlertGenerator alertGenerator = new AlertGenerator(patientCount);

        for (int patientId : patientIds) {

            scheduleTask(() -> ecgDataGenerator.generate(patientId, outputStrategy), 1, TimeUnit.SECONDS);
            scheduleTask(() -> bloodSaturationDataGenerator.generate(patientId, outputStrategy), 1, TimeUnit.SECONDS);
            scheduleTask(() -> bloodPressureDataGenerator.generate(patientId, outputStrategy), 1, TimeUnit.MINUTES);
            scheduleTask(() -> bloodLevelsDataGenerator.generate(patientId, outputStrategy), 2, TimeUnit.MINUTES);
            scheduleTask(() -> alertGenerator.generate(patientId, outputStrategy), 20, TimeUnit.SECONDS);

        }

    }

    /**
     * Schedules a recurring task using the scheduler.
     *
     * <p>The task will run at a fixed rate with a small random initial delay.
     *
     * @param task the task to execute periodically
     * @param period the interval between executions
     * @param timeUnit the time unit of the period
     */
    private static void scheduleTask(Runnable task, long period, TimeUnit timeUnit) {

        scheduler.scheduleAtFixedRate(task, random.nextInt(5), period, timeUnit);

    }
}