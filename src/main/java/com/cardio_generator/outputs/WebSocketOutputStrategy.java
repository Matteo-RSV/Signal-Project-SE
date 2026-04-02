package com.cardio_generator.outputs;

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

/**
 * Outputs generated patient data to connected clients via a WebSocket server.
 *
 * <p>This class implements the {@link OutputStrategy} interface by broadcasting
 * simulated patient data over a WebSocket connection. A server is started on a
 * specified port, and any connected clients will receive real-time updates.
 *
 * <p>The output data is formatted as a comma-separated string containing the
 * patient ID, timestamp, label, and data value. Each call to the output method
 * sends the generated data to all currently connected clients.
 */
public class WebSocketOutputStrategy implements OutputStrategy {

    /** WebSocket server used to manage client connections and broadcast data. */
    private WebSocketServer server;

    /**
     * Constructs a WebSocket output strategy and starts the server.
     *
     * <p>The server listens for incoming client connections on the specified port.
     * Once started, clients can connect and receive real-time simulated data.
     *
     * @param port the port number on which the WebSocket server listens for connections
     */
    public WebSocketOutputStrategy(int port) {

        server = new SimpleWebSocketServer(new InetSocketAddress(port));
        System.out.println("WebSocket server created on port: " + port + ", listening for connections...");
        server.start();
        
    }

    /**
     * Sends generated patient data to all connected WebSocket clients.
     *
     * <p>The data is formatted as a comma-separated string containing the patient ID,
     * timestamp, label, and value. This method does not return a value. Its primary
     * side effect is broadcasting data over the network to connected clients.
     *
     * @param patientId the ID of the patient associated with the generated data
     * @param timestamp the time at which the data was generated, represented as
     *                  milliseconds since the Unix epoch
     * @param label the name of the measurement (e.g., {@code ECG}, {@code Cholesterol})
     * @param data the generated value to be sent, represented as a string
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {

        String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);

        for (WebSocket conn : server.getConnections()) {

            conn.send(message);

        }
    }

    /**
     * Internal WebSocket server implementation used to handle client connections
     * and server lifecycle events.
     *
     * <p>This class extends {@link WebSocketServer} and provides basic logging for
     * connection events such as opening, closing, errors, and server startup.
     */
    private static class SimpleWebSocketServer extends WebSocketServer {

        /**
         * Constructs the WebSocket server with the specified network address.
         *
         * @param address the socket address (host and port) on which the server listens
         */
        public SimpleWebSocketServer(InetSocketAddress address) {

            super(address);

        }

        /**
         * Called when a new client establishes a connection to the server.
         *
         * @param conn the WebSocket connection that was opened
         * @param handshake the initial handshake data from the client
         */
        @Override
        public void onOpen(WebSocket conn, org.java_websocket.handshake.ClientHandshake handshake) {

            System.out.println("New connection: " + conn.getRemoteSocketAddress());

        }

        /**
         * Called when a client connection is closed.
         *
         * @param conn the WebSocket connection that was closed
         * @param code the closure code
         * @param reason the reason for closure
         * @param remote indicates whether the closure was initiated remotely
         */
        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {

            System.out.println("Closed connection: " + conn.getRemoteSocketAddress());

        }

        /**
         * Called when a message is received from a client.
         *
         * <p>This implementation does not process incoming messages.
         *
         * @param conn the WebSocket connection that sent the message
         * @param message the message received from the client
         */
        @Override
        public void onMessage(WebSocket conn, String message) {
            // Not used in this context
        }

        /**
         * Called when an error occurs in the WebSocket server.
         *
         * @param conn the WebSocket connection where the error occurred (may be null)
         * @param ex the exception that was thrown
         */
        @Override
        public void onError(WebSocket conn, Exception ex) {

            ex.printStackTrace();

        }

        /**
         * Called when the WebSocket server starts successfully.
         */
        @Override
        public void onStart() {

            System.out.println("Server started successfully");

        }
    }
}