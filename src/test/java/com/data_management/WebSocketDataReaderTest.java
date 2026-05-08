package com.data_management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WebSocketDataReaderTest {

    @BeforeEach
    void clearStorage() {
        DataStorage.getInstance().clearData();
    }

    @Test
    void parseMessageReadsStandardMeasurementFields() {
        WebSocketDataReader reader = new WebSocketDataReader(URI.create("ws://localhost:8080"));

        PatientRecord record = reader.parseMessage("1,1000,ECG,0.42");

        assertNotNull(record);
        assertEquals(1, record.getPatientId());
        assertEquals(1000L, record.getTimestamp());
        assertEquals("ECG", record.getRecordType());
        assertEquals(0.42, record.getMeasurementValue());
    }

    @Test
    void parseMessageConvertsSaturationAndAlertValues() {
        WebSocketDataReader reader = new WebSocketDataReader(URI.create("ws://localhost:8080"));

        PatientRecord saturationRecord = reader.parseMessage("2,2000,Saturation,95.0%");
        PatientRecord alertRecord = reader.parseMessage("3,3000,Alert,triggered");

        assertNotNull(saturationRecord);
        assertEquals(95.0, saturationRecord.getMeasurementValue());
        assertNotNull(alertRecord);
        assertEquals(1.0, alertRecord.getMeasurementValue());
    }

    @Test
    void parseMessageReturnsNullForInvalidMessages() {
        WebSocketDataReader reader = new WebSocketDataReader(URI.create("ws://localhost:8080"));

        assertNull(reader.parseMessage(null));
        assertNull(reader.parseMessage(""));
        assertNull(reader.parseMessage("1,1000,ECG"));
        assertNull(reader.parseMessage("one,1000,ECG,0.42"));
        assertNull(reader.parseMessage("1,1000,UnknownType,0.42"));
        assertNull(reader.parseMessage("1,1000,Alert,maybe"));
    }

    @Test
    void handleMessageStoresValidMessagesAndSkipsInvalidOnes() {
        WebSocketDataReader reader = new WebSocketDataReader(URI.create("ws://localhost:8080"));
        DataStorage storage = DataStorage.getInstance();

        reader.setDataStorage(storage);
        reader.handleMessage("4,4000,SystolicPressure,120");
        reader.handleMessage("4,5000,broken");

        List<PatientRecord> records = storage.getRecords(4, 0L, 10_000L);
        assertEquals(1, records.size());
        assertEquals("SystolicPressure", records.get(0).getRecordType());
        assertEquals(120.0, records.get(0).getMeasurementValue());
        assertTrue(storage.getRecords(99, 0L, 10_000L).isEmpty());
    }
}
