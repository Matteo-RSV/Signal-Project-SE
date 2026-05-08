package com.data_management;

import java.io.IOException;

/**
 * Simple reader interface for both old file input and new live input.
 *
 * <p>Part 4 used a file reader that worked once and finished. Part 5 adds a
 * WebSocket reader that may stay connected for longer, so the interface now
 * uses start/stop instead of a single read method.
 */
public interface DataReader {
    /**
     * Starts reading data and sending it into the given storage.
     *
     * <p>This start/stop shape works for both old one-time readers and future
     * streaming readers. A file reader can read once inside {@code start(...)},
     * while a WebSocket reader can keep running until {@link #stop()} is called.
     *
     * @param dataStorage the storage where data will be stored
     * @throws IOException if there is an error reading from the source
     */
    void start(DataStorage dataStorage) throws IOException;

    /**
     * Stops the reader.
     *
     * <p>For the file reader this can stay simple because the work finishes after
     * one pass. A future streaming reader can use this to close its connection and
     * stop receiving data.
     */
    void stop();
}
