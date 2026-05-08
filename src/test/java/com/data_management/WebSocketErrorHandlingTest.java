package com.data_management;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WebSocketErrorHandlingTest {

    @BeforeEach
    void clearStorage() {
        DataStorage.getInstance().clearData();
    }

    @Test
    void skipsMessageWithMissingPatientId() {
        WebSocketDataReader reader = new WebSocketDataReader(URI.create("ws://localhost:8080"));

        assertNull(reader.parseMessage(",1000,ECG,0.42"));
    }

    @Test
    void skipsMessageWithMissingMeasurementType() {
        WebSocketDataReader reader = new WebSocketDataReader(URI.create("ws://localhost:8080"));

        assertNull(reader.parseMessage("1,1000,,0.42"));
    }

    @Test
    void skipsMessageWithMissingValue() {
        WebSocketDataReader reader = new WebSocketDataReader(URI.create("ws://localhost:8080"));

        assertNull(reader.parseMessage("1,1000,ECG,"));
    }

    @Test
    void skipsIncompleteAlertMessage() {
        WebSocketDataReader reader = new WebSocketDataReader(URI.create("ws://localhost:8080"));

        assertNull(reader.parseMessage("1,1000,Alert,"));
    }

    @Test
    void invalidMessageDoesNotDamageExistingValidData() {
        WebSocketDataReader reader = new WebSocketDataReader(URI.create("ws://localhost:8080"));
        DataStorage storage = DataStorage.getInstance();
        reader.setDataStorage(storage);

        reader.handleMessage("15,1000,ECG,0.42");
        reader.handleMessage("15,1001,,0.50");

        List<PatientRecord> records = storage.getRecords(15, 0L, 2_000L);
        assertEquals(1, records.size());
        assertEquals("ECG", records.get(0).getRecordType());
        assertEquals(0.42, records.get(0).getMeasurementValue());
    }

    @Test
    void stopIsSafeBeforeConnectionAndWhenCalledTwice() {
        WebSocketDataReader reader = new WebSocketDataReader(URI.create("ws://localhost:8080"));

        assertDoesNotThrow(reader::stop);
        assertDoesNotThrow(reader::stop);
    }

    @Test
    void connectionFailureDoesNotCrashProgram() {
        WebSocketDataReader reader = new WebSocketDataReader(URI.create("ws://127.0.0.1:1"));

        assertDoesNotThrow(() -> reader.start(DataStorage.getInstance()));
        assertDoesNotThrow(reader::stop);
    }
}
