package com.data_management;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.alerts.Alert;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

/**
 * Reads live data from the simulator through a WebSocket connection.
 *
 * <p>This reader connects to the simulator, reads each incoming message, parses
 * the message fields, stores valid data in {@link DataStorage}, and then lets
 * the existing alert logic read the updated patient data. The simulator
 * currently sends messages in this order: patient ID, timestamp, label, data
 * value.
 */
public class WebSocketDataReader implements DataReader {
    private static final Set<String> SUPPORTED_LABELS = new HashSet<>(Arrays.asList(
            "ECG",
            "Saturation",
            "SystolicPressure",
            "DiastolicPressure",
            "Cholesterol",
            "WhiteBloodCells",
            "RedBloodCells",
            "Alert"));

    private final URI serverUri;
    private WebSocketClient client;
    private boolean running;
    private DataStorage dataStorage;

    /**
     * Creates a WebSocket reader for the given server address.
     *
     * @param serverUri the WebSocket server URI, for example ws://localhost:8080
     */
    public WebSocketDataReader(URI serverUri) {
        this.serverUri = serverUri;
    }

    /**
     * Stores the shared data storage used by the live reader.
     *
     * <p>This is set before the connection starts so received messages can be
     * saved as soon as they arrive.
     */
    void setDataStorage(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Starts the WebSocket reader.
     *
     * <p>This opens the connection because a streaming reader needs to stay
     * connected while new messages arrive.
     *
     * @param dataStorage the storage that may be used in a later step
     * @throws IOException if the connection cannot be opened
     */
    @Override
    public void start(DataStorage dataStorage) throws IOException {
        setDataStorage(dataStorage);
        this.client = createClient();
        this.running = true;

        try {
            boolean connected = client.connectBlocking(5, TimeUnit.SECONDS);
            if (!connected) {
                running = false;
                System.err.println("Could not connect to WebSocket server at " + serverUri);
                client.close();
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            running = false;
            throw new IOException("WebSocket connection was interrupted", exception);
        } catch (Exception exception) {
            running = false;
            System.err.println("Error while starting WebSocket reader: " + exception.getMessage());
            stop();
        }
    }

    /**
     * Stops the WebSocket reader.
     *
     * <p>This closes the connection safely because a streaming reader should stop
     * receiving messages when it is no longer needed.
     */
    @Override
    public void stop() {
        running = false;

        if (client != null) {
            try {
                client.closeBlocking();
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                System.err.println("WebSocket reader was interrupted while closing.");
            } catch (Exception exception) {
                System.err.println("Error while closing WebSocket reader: " + exception.getMessage());
            } finally {
                client = null;
            }
        }
    }

    private WebSocketClient createClient() {
        return new WebSocketClient(serverUri) {
            /**
             * Called when the client connects to the WebSocket server.
             *
             * @param handshake information returned by the server during connection
             */
            @Override
            public void onOpen(ServerHandshake handshake) {
                // A simple connection message helps confirm that the live reader
                // reached the simulator successfully.
                System.out.println("Connected to WebSocket server: " + serverUri);
            }

            /**
             * Called whenever a new message arrives from the simulator.
             *
             * @param message the message sent by the server
             */
            @Override
            public void onMessage(String message) {
                if (!running) {
                    return;
                }

                if (dataStorage != null) {
                    // Keep the real-time flow in one place: read the text message,
                    // parse it, store it, and then let alerts check the new data.
                    handleMessage(message);
                } else {
                    System.out.println("Received WebSocket message without storage: " + message);
                }
            }

            /**
             * Called when the server closes the connection or when the client closes it.
             *
             * @param code the close status code
             * @param reason the close reason, if one was sent
             * @param remote true if the other side closed the connection
             */
            @Override
            public void onClose(int code, String reason, boolean remote) {
                running = false;
                // The connection can close because the reader stopped, the server
                // stopped, or the network failed. In all cases the reader should
                // end cleanly instead of crashing.
                System.out.println("WebSocket connection closed. Reason: " + reason);
            }

            /**
             * Called when a connection or message error happens.
             *
             * @param exception the error that happened
             */
            @Override
            public void onError(Exception exception) {
                String message = exception == null ? "Unknown WebSocket error" : exception.getMessage();
                // A warning is enough here because the reader should stay stable
                // even when one connection attempt fails.
                System.err.println("WebSocket reader error: " + message);
            }
        };
    }

    /**
     * Reads one message from the WebSocket connection and stores it when valid.
     *
     * <p>The message is split into four comma-separated parts. Invalid messages are
     * skipped after printing a warning so the reader does not crash.
     *
     * @param message the incoming WebSocket message
     */
    void handleMessage(String message) {
        System.out.println("Received WebSocket message: " + message);

        if (dataStorage == null) {
            System.err.println("Skipping WebSocket message because no data storage is set.");
            return;
        }

        PatientRecord parsedRecord = parseMessage(message);
        if (parsedRecord == null) {
            return;
        }

        // DataStorage reuses the same patient object and skips exact duplicate
        // messages, so live updates can be added one by one safely.
        dataStorage.addPatientData(
                parsedRecord.getPatientId(),
                parsedRecord.getMeasurementValue(),
                parsedRecord.getRecordType(),
                parsedRecord.getTimestamp());

        // After a valid live update is stored, check alerts for just that patient.
        // If the patient does not have enough data yet, the alert logic simply
        // returns no alerts.
        printTriggeredAlerts(parsedRecord.getPatientId());
    }

    /**
     * Parses one WebSocket message into a patient record.
     *
     * <p>The expected format is: patientId,timestamp,label,data. The data part is
     * converted into a numeric value so it can be stored in {@link DataStorage}.
     *
     * @param message the incoming message text
     * @return a parsed record, or {@code null} when the message is invalid
     */
    PatientRecord parseMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            System.err.println("Skipping empty WebSocket message.");
            return null;
        }

        // The simulator sends four comma-separated parts in a fixed order:
        // patient ID, timestamp, label, and data value.
        String[] parts = message.split(",", 4);
        if (parts.length != 4) {
            System.err.println("Skipping invalid WebSocket message: " + message);
            return null;
        }

        String patientIdText = parts[0].trim();
        String timestampText = parts[1].trim();
        String label = parts[2].trim();
        String data = parts[3].trim();

        if (label.isEmpty() || data.isEmpty()) {
            System.err.println("Skipping WebSocket message with missing fields: " + message);
            return null;
        }

        if (!SUPPORTED_LABELS.contains(label)) {
            System.err.println("Skipping WebSocket message with unknown measurement type: " + message);
            return null;
        }

        try {
            int patientId = Integer.parseInt(patientIdText);
            long timestamp = Long.parseLong(timestampText);

            Double measurementValue = parseMeasurementValue(label, data);
            if (measurementValue == null) {
                System.err.println("Skipping WebSocket message with invalid data value: " + message);
                return null;
            }

            return new PatientRecord(patientId, measurementValue, label, timestamp);
        } catch (NumberFormatException exception) {
            System.err.println("Skipping WebSocket message with invalid number format: " + message);
            return null;
        }
    }

    private Double parseMeasurementValue(String label, String data) {
        if ("Alert".equals(label)) {
            // Alert messages use words instead of numbers, so they are mapped to
            // numeric values that fit the existing PatientRecord design.
            if ("triggered".equalsIgnoreCase(data)) {
                return 1.0;
            }
            if ("resolved".equalsIgnoreCase(data)) {
                return 0.0;
            }
            return null;
        }

        // Saturation values arrive with a percent sign, but the stored record
        // still needs one numeric measurement value.
        String numericValue = data.endsWith("%") ? data.substring(0, data.length() - 1) : data;

        try {
            return Double.parseDouble(numericValue);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private void printTriggeredAlerts(int patientId) {
        List<Alert> alerts = dataStorage.checkAlertsForPatient(patientId);
        for (Alert alert : alerts) {
            // Printing alerts here gives simple live feedback without changing the
            // older alert classes.
            System.out.println("Triggered alert: " + alert.getMessage());
        }
    }
}
