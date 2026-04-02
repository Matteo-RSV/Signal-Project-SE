package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * Implementation of {@link OutputStrategy} that sends simulated patient data
 * over a TCP socket connection.
 *
 * <p>This class starts a TCP server on a specified port and waits for a client
 * to connect. Once connected, generated data is sent to the client in real time.
 */
public class TcpOutputStrategy implements OutputStrategy {

    /** Server socket used to accept incoming TCP client connections. */
    private ServerSocket serverSocket;

    /** Socket representing the connected client. */
    private Socket clientSocket;

    /** Writer used to send data to the connected client. */
    private PrintWriter out;

    /**
     * Constructs a TCP output strategy that listens for connections on the given port.
     *
     * <p>A background thread is started to accept a client connection without blocking
     * the main simulation thread.
     *
     * @param port the port number on which the TCP server will listen for incoming connections
     */
    public TcpOutputStrategy(int port) {
        try {
            
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + port);

            // Accept clients in a new thread to not block the main thread
            Executors.newSingleThreadExecutor().submit(() -> {
                try {

                    clientSocket = serverSocket.accept();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.out.println("Client connected: " + clientSocket.getInetAddress());

                } catch (IOException e) {

                    e.printStackTrace();

                }
            });
        } catch (IOException e) {

            e.printStackTrace();

        }
    }

    /**
     * Sends a formatted data message to the connected TCP client.
     *
     * <p>If no client is connected, the method does nothing. Data is formatted
     * as a comma-separated string containing patient information.
     *
     * @param patientId the unique identifier of the patient
     * @param timestamp the time at which the data was generated
     * @param label the type of data (e.g., ECG, blood pressure)
     * @param data the generated data value formatted as a string
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {

        if (out != null) {

            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);

        }

    }
}