package data_management;

import com.data_management.DataStorage;
import com.data_management.FileDataReader;
import com.data_management.PatientRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileDataReaderTest {

    @TempDir
    Path tempDirectory;

    @BeforeEach
    void clearStorage() {
        DataStorage.getInstance().clearData();
    }

    @Test
    void startParsesValidFilesAndIgnoresMalformedLines() throws IOException {
        Files.writeString(tempDirectory.resolve("SystolicPressure.txt"),
                "Patient ID: 1, Timestamp: 1000, Label: SystolicPressure, Data: 118\n"
                        + "This line should be ignored\n");
        Files.writeString(tempDirectory.resolve("Saturation.txt"),
                "Patient ID: 1, Timestamp: 2000, Label: Saturation, Data: 91%\n");
        Files.writeString(tempDirectory.resolve("Alert.txt"),
                "Patient ID: 1, Timestamp: 3000, Label: Alert, Data: triggered\n"
                        + "Patient ID: 2, Timestamp: 4000, Label: Alert, Data: resolved\n");
        Files.writeString(tempDirectory.resolve("ignored.log"),
                "Patient ID: 99, Timestamp: 5000, Label: ECG, Data: 2.5\n");

        DataStorage storage = DataStorage.getInstance();
        FileDataReader reader = new FileDataReader(tempDirectory.toString());

        reader.start(storage);

        List<PatientRecord> patientOneRecords = storage.getRecords(1, 0L, 10_000L);
        patientOneRecords.sort(Comparator.comparingLong(PatientRecord::getTimestamp));

        assertEquals(3, patientOneRecords.size());
        assertEquals("SystolicPressure", patientOneRecords.get(0).getRecordType());
        assertEquals(118.0, patientOneRecords.get(0).getMeasurementValue());
        assertEquals("Saturation", patientOneRecords.get(1).getRecordType());
        assertEquals(91.0, patientOneRecords.get(1).getMeasurementValue());
        assertEquals("Alert", patientOneRecords.get(2).getRecordType());
        assertEquals(1.0, patientOneRecords.get(2).getMeasurementValue());

        List<PatientRecord> patientTwoRecords = storage.getRecords(2, 0L, 10_000L);
        assertEquals(1, patientTwoRecords.size());
        assertEquals(0.0, patientTwoRecords.get(0).getMeasurementValue());
        assertTrue(storage.getRecords(99, 0L, 10_000L).isEmpty());
    }

    @Test
    void startLeavesStorageEmptyWhenDirectoryHasNoInputFiles() throws IOException {
        DataStorage storage = DataStorage.getInstance();
        FileDataReader reader = new FileDataReader(tempDirectory.toString());

        reader.start(storage);

        assertTrue(storage.getAllPatients().isEmpty());
    }

    @Test
    void startThrowsWhenDirectoryDoesNotExist() {
        Path missingDirectory = tempDirectory.resolve("missing");
        FileDataReader reader = new FileDataReader(missingDirectory.toString());

        assertThrows(IOException.class, () -> reader.start(DataStorage.getInstance()));
    }
}
