package com.data_management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.alerts.Alert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WebSocketRealtimeFlowIntegrationTest {

    @BeforeEach
    void clearStorage() {
        DataStorage.getInstance().clearData();
    }

    @Test
    void handlesNormalMeasurementFlow() {
        WebSocketDataReader reader = new WebSocketDataReader(URI.create("ws://localhost:8080"));
        DataStorage storage = DataStorage.getInstance();
        reader.setDataStorage(storage);

        reader.handleMessage("11,1000,ECG,0.42");

        List<PatientRecord> records = storage.getRecords(11, 0L, 2_000L);
        assertEquals(1, records.size());
        assertEquals(11, records.get(0).getPatientId());
        assertEquals(1000L, records.get(0).getTimestamp());
        assertEquals("ECG", records.get(0).getRecordType());
        assertEquals(0.42, records.get(0).getMeasurementValue());
    }

    @Test
    void handlesRepeatedUpdatesForSamePatient() {
        WebSocketDataReader reader = new WebSocketDataReader(URI.create("ws://localhost:8080"));
        DataStorage storage = DataStorage.getInstance();
        reader.setDataStorage(storage);

        reader.handleMessage("12,1000,Saturation,97.0%");
        reader.handleMessage("12,2000,Saturation,95.0%");
        reader.handleMessage("12,2000,Saturation,95.0%");

        List<PatientRecord> records = storage.getRecords(12, 0L, 3_000L);
        assertEquals(2, records.size());
        assertEquals(97.0, records.get(0).getMeasurementValue());
        assertEquals(95.0, records.get(1).getMeasurementValue());
    }

    @Test
    void skipsInvalidMessageWithoutCorruptingStorage() {
        WebSocketDataReader reader = new WebSocketDataReader(URI.create("ws://localhost:8080"));
        DataStorage storage = DataStorage.getInstance();
        reader.setDataStorage(storage);

        reader.handleMessage("13,1000,SystolicPressure,120");
        reader.handleMessage("broken message");

        List<PatientRecord> records = storage.getRecords(13, 0L, 2_000L);
        assertEquals(1, records.size());
        assertEquals("SystolicPressure", records.get(0).getRecordType());
        assertTrue(storage.getRecords(999, 0L, 2_000L).isEmpty());
    }

    @Test
    void makesStoredRealtimeDataAvailableToAlertChecks() {
        WebSocketDataReader reader = new WebSocketDataReader(URI.create("ws://localhost:8080"));
        DataStorage storage = DataStorage.getInstance();
        reader.setDataStorage(storage);

        reader.handleMessage("14,0,Saturation,97.0%");
        reader.handleMessage("14,300000,Saturation,91.0%");

        Set<String> conditions = storage.checkAlertsForPatient(14).stream()
                .map(Alert::getCondition)
                .collect(Collectors.toSet());

        assertTrue(conditions.contains("Low saturation alert"));
        assertTrue(conditions.contains("Rapid saturation drop alert"));
    }
}
