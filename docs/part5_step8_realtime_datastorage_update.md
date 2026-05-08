# Part 5 Step 8 Real-Time DataStorage Update

## What I inspected

I inspected these files:

- `src/main/java/com/data_management/DataStorage.java`
- `src/main/java/com/data_management/WebSocketDataReader.java`
- `src/main/java/com/data_management/DataReader.java`
- `src/main/java/com/data_management/FileDataReader.java`
- `src/main/java/com/data_management/Patient.java`
- `src/main/java/com/data_management/PatientRecord.java`
- `src/main/java/com/alerts/AlertGenerator.java`

## How DataStorage worked before

Before this step:

- `DataStorage` used a `HashMap`
- `addPatientData(...)` created a patient if needed
- then it always appended a new record
- it did not check for duplicate real-time messages
- it did not have simple synchronization for live updates

That was fine for file loading, but real-time WebSocket messages can arrive repeatedly or while other parts of the program are reading the same patient data.

## What was changed for real-time updates

I made small changes to support real-time storage:

- `DataStorage` now uses `ConcurrentHashMap`
- important `DataStorage` methods are now `synchronized`
- `Patient` record methods used by storage are now `synchronized`
- duplicate checking was added before a new record is stored

The main behavior is still simple:

- if a patient is new, create that patient
- if the patient already exists, reuse that patient
- append new records
- skip exact duplicate records

## How WebSocketDataReader now stores parsed data

`WebSocketDataReader` was already parsing valid messages in Step 7.

Now when it calls:

`dataStorage.addPatientData(...)`

the storage layer safely handles repeated real-time messages by:

- reusing the existing patient object
- appending only new records
- skipping an identical record if the same message arrives again

## How duplicate patient records are avoided

Duplicate prevention is based on these fields:

- patient ID
- record type
- timestamp
- measurement value

If all of those match an already stored record for the same patient, the new record is skipped.

So repeated WebSocket messages like the same alert arriving twice will not create duplicate stored records.

## How concurrency is handled

I kept concurrency handling simple:

- `DataStorage` uses `ConcurrentHashMap`
- key `DataStorage` methods are `synchronized`
- `Patient` methods that add or read records are also `synchronized`

This is a basic and readable way to reduce problems when:

- live WebSocket data is being added
- another part of the program is reading patient records at the same time

## Test result

I ran:

`mvn test`

Result:

- Build success
- 46 tests run
- 0 failures
- 0 errors
- 0 skipped

## What remains for alert integration and testing

The storage part now supports real-time updates better, but the next step is still about alerts.

What remains:

1. decide when live alert evaluation should run
2. connect stored WebSocket data to the existing alert logic
3. test a real end-to-end flow where simulator data arrives and alerts are checked during runtime
