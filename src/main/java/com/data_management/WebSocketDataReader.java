package com.data_management;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

/**
 * Reads live data from the simulator through a WebSocket connection.
 *
 * <p>This reader does not fully parse and store the incoming messages yet. For
 * now it connects to the simulator and prints each message so the real-time
 * connection can be tested.
 */
public class WebSocketDataReader implements DataReader {
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
        this.dataStorage = dataStorage;
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

                // The storage reference is kept for the next step when messages will
                // be parsed and saved instead of only being printed.
                if (dataStorage != null) {
                    System.out.println("Received WebSocket message: " + message);
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
                System.err.println("WebSocket reader error: " + message);
            }
        };
    }
}
