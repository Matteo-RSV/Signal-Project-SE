package com.data_management;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import com.alerts.Alert;
import com.alerts.AlertGenerator;

/**
 * Manages storage and retrieval of patient data within a healthcare monitoring
 * system.
 * This class serves as a repository for all patient records, organized by
 * patient IDs.
 */
public class DataStorage {
    private static DataStorage instance;
    private Map<Integer, Patient> patientMap; // Stores patient objects indexed by their unique patient ID.

    /**
     * Constructs a new instance of DataStorage, initializing the underlying storage
     * structure.
     */
    private DataStorage() {
        this.patientMap = new ConcurrentHashMap<>();
    }

    /**
     * Returns the single data storage instance.
     *
     * @return the data storage instance
     */
    public static synchronized DataStorage getInstance() {
        if (instance == null) {
            instance = new DataStorage();
        }
        return instance;
    }

    /**
     * Adds or updates patient data in the storage.
     * If the patient does not exist, a new Patient object is created and added to
     * the storage.
     * Otherwise, the new data is added to the existing patient's records.
     *
     * @param patientId        the unique identifier of the patient
     * @param measurementValue the value of the health metric being recorded
     * @param recordType       the type of record, e.g., "HeartRate",
     *                         "BloodPressure"
     * @param timestamp        the time at which the measurement was taken, in
     *                         milliseconds since the Unix epoch
     */
    public synchronized void addPatientData(int patientId, double measurementValue, String recordType, long timestamp) {
        // Real-time messages may arrive one after another, so the same patient
        // should be reused and only new records should be appended.
        Patient patient = patientMap.computeIfAbsent(patientId, Patient::new);

        // If the exact same message arrives again, keep the old record and skip
        // adding a duplicate.
        patient.addRecordIfNotDuplicate(measurementValue, recordType, timestamp);
    }

    /**
     * Retrieves a list of PatientRecord objects for a specific patient, filtered by
     * a time range.
     *
     * @param patientId the unique identifier of the patient whose records are to be
     *                  retrieved
     * @param startTime the start of the time range, in milliseconds since the Unix
     *                  epoch
     * @param endTime   the end of the time range, in milliseconds since the Unix
     *                  epoch
     * @return a list of PatientRecord objects that fall within the specified time
     *         range
     */
    public synchronized List<PatientRecord> getRecords(int patientId, long startTime, long endTime) {
        Patient patient = patientMap.get(patientId);
        if (patient != null) {
            return patient.getRecords(startTime, endTime);
        }
        return new ArrayList<>(); // return an empty list if no patient is found
    }

    /**
     * Retrieves a collection of all patients stored in the data storage.
     *
     * @return a list of all patients
     */
    public synchronized List<Patient> getAllPatients() {
        return new ArrayList<>(patientMap.values());
    }

    /**
     * Clears all stored patient data.
     */
    public synchronized void clearData() {
        patientMap.clear();
    }

    /**
     * The main method for the DataStorage class.
     * Loads simulator output files into storage, prints the stored records, and
     * evaluates alert conditions for the loaded patients.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        if (args.length == 0 || "-h".equals(args[0]) || "--help".equals(args[0])) {
            printUsage();
            return;
        }

        String inputDirectory = args[0];
        DataStorage storage = DataStorage.getInstance();
        storage.clearData();
        DataReader reader = new FileDataReader(inputDirectory);

        try {
            reader.start(storage);
        } catch (IOException exception) {
            System.err.println("Unable to read simulator output from '" + inputDirectory + "': "
                    + exception.getMessage());
            return;
        } finally {
            reader.stop();
        }

        List<Patient> patients = storage.getAllPatients();
        patients.sort(Comparator.comparingInt(Patient::getPatientId));

        if (patients.isEmpty()) {
            System.out.println("No patient data was loaded from " + inputDirectory + ".");
            return;
        }

        printPatientRecords(patients);
        printAlerts(storage, patients);
    }

    private static void printUsage() {
        System.out.println("Usage: java DataStorage <input-directory>");
        System.out.println("Reads simulator output files from the given directory, stores");
        System.out.println("the records, and evaluates alert conditions for each patient.");
    }

    private static void printPatientRecords(List<Patient> patients) {
        for (Patient patient : patients) {
            System.out.println("Patient ID: " + patient.getPatientId());
            List<PatientRecord> records = patient.getRecords(Long.MIN_VALUE, Long.MAX_VALUE);
            records.sort(Comparator.comparingLong(PatientRecord::getTimestamp));
            for (PatientRecord record : records) {
                System.out.println("  Type: " + record.getRecordType()
                        + ", Data: " + record.getMeasurementValue()
                        + ", Timestamp: " + record.getTimestamp());
            }
        }
    }

    private static void printAlerts(DataStorage storage, List<Patient> patients) {
        AlertGenerator alertGenerator = new AlertGenerator(storage);
        for (Patient patient : patients) {
            alertGenerator.evaluateData(patient);
        }

        List<Alert> alerts = alertGenerator.getTriggeredAlerts();
        if (alerts.isEmpty()) {
            System.out.println("No alerts were triggered for the loaded data.");
            return;
        }

        System.out.println("Triggered alerts:");
        for (Alert alert : alerts) {
            System.out.println("  Patient ID: " + alert.getPatientId()
                    + ", Condition: " + alert.getCondition()
                    + ", Timestamp: " + alert.getTimestamp());
        }
    }
}
