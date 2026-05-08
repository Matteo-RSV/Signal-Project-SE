package com.data_management;

import java.io.IOException;
import java.net.URI;

/**
 * Small manual runner for testing the WebSocket reader.
 *
 * <p>It connects to the simulator at ws://localhost:8080 by default and keeps
 * the program running until the user presses Enter.
 */
public class WebSocketReaderMain {
    private static final long DEFAULT_HEADLESS_RUN_TIME_MILLIS = 10_000L;

    /**
     * Starts the WebSocket reader for manual testing.
     *
     * @param args optional first argument with a WebSocket URI
     */
    public static void main(String[] args) {
        String serverAddress = args.length > 0 ? args[0] : "ws://localhost:8080";
        WebSocketDataReader reader = new WebSocketDataReader(URI.create(serverAddress));

        try {
            reader.start(DataStorage.getInstance());
            waitForStopSignal();
        } catch (IOException exception) {
            System.err.println("Could not start WebSocket reader: " + exception.getMessage());
        } finally {
            reader.stop();
        }
    }

    private static void waitForStopSignal() throws IOException {
        if (System.console() != null) {
            System.out.println("WebSocket reader is running. Press Enter to stop.");
            System.in.read();
            return;
        }

        // When this class is started without an interactive console, keep it alive
        // for a short time so connection testing is still possible.
        System.out.println("WebSocket reader is running for " + (DEFAULT_HEADLESS_RUN_TIME_MILLIS / 1000)
                + " seconds.");
        try {
            Thread.sleep(DEFAULT_HEADLESS_RUN_TIME_MILLIS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }
}
