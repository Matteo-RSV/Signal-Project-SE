package data_management;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataStorageTest {

    @BeforeEach
    void clearStorage() {
        DataStorage.getInstance().clearData();
    }

    @Test
    void getRecordsReturnsOnlyRecordsInsideInclusiveRange() {
        Patient patient = new Patient(7);
        patient.addRecord(80.0, "HeartRate", 1_000L);
        patient.addRecord(81.0, "HeartRate", 2_000L);
        patient.addRecord(82.0, "HeartRate", 3_000L);
        patient.addRecord(83.0, "HeartRate", 4_000L);

        List<PatientRecord> records = patient.getRecords(2_000L, 3_000L);

        assertEquals(2, records.size());
        assertEquals(2_000L, records.get(0).getTimestamp());
        assertEquals(3_000L, records.get(1).getTimestamp());
    }

    @Test
    void getRecordsReturnsEmptyForEmptyPatientOrReversedRange() {
        Patient emptyPatient = new Patient(1);
        assertTrue(emptyPatient.getRecords(0L, 1_000L).isEmpty());

        Patient patient = new Patient(2);
        patient.addRecord(120.0, "SystolicPressure", 5_000L);
        patient.addRecord(80.0, "DiastolicPressure", 6_000L);

        assertTrue(patient.getRecords(7_000L, 6_000L).isEmpty());
        assertTrue(patient.getRecords(1_000L, 4_000L).isEmpty());
    }

    @Test
    void getRecordsIncludesBoundaryTimestampsExactly() {
        Patient patient = new Patient(3);
        patient.addRecord(95.0, "Saturation", 10_000L);
        patient.addRecord(96.0, "Saturation", 20_000L);
        patient.addRecord(97.0, "Saturation", 30_000L);

        List<PatientRecord> records = patient.getRecords(10_000L, 30_000L);

        assertEquals(3, records.size());
        assertEquals(10_000L, records.get(0).getTimestamp());
        assertEquals(30_000L, records.get(2).getTimestamp());
    }

    @Test
    void dataStorageGetInstanceReturnsSingletonObject() {
        DataStorage firstStorage = DataStorage.getInstance();
        DataStorage secondStorage = DataStorage.getInstance();

        assertNotNull(firstStorage);
        assertSame(firstStorage, secondStorage);
    }

    @Test
    void dataStorageSeparatesPatientsAndPreservesRecordOrder() {
        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(1, 110.0, "SystolicPressure", 100L);
        storage.addPatientData(2, 91.0, "Saturation", 200L);
        storage.addPatientData(1, 115.0, "SystolicPressure", 300L);

        List<PatientRecord> patientOneRecords = storage.getRecords(1, 0L, 1_000L);
        List<PatientRecord> patientTwoRecords = storage.getRecords(2, 0L, 1_000L);

        assertEquals(2, patientOneRecords.size());
        assertEquals(1, patientTwoRecords.size());
        assertEquals(110.0, patientOneRecords.get(0).getMeasurementValue());
        assertEquals(115.0, patientOneRecords.get(1).getMeasurementValue());
        assertEquals("Saturation", patientTwoRecords.get(0).getRecordType());
    }

    @Test
    void dataStorageReturnsEmptyListForUnknownPatient() {
        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(1, 100.0, "ECG", 100L);

        assertTrue(storage.getRecords(999, 0L, 1_000L).isEmpty());
    }
}
