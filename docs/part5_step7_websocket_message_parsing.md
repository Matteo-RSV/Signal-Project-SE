# Part 5 Step 7 WebSocket Message Parsing

## What I inspected

I inspected these files:

- `src/main/java/com/data_management/WebSocketDataReader.java`
- `src/main/java/com/cardio_generator/outputs/WebSocketOutputStrategy.java`
- `src/main/java/com/data_management/DataReader.java`
- `src/main/java/com/data_management/DataStorage.java`
- `src/main/java/com/data_management/FileDataReader.java`
- `src/main/java/com/data_management/Patient.java`
- `src/main/java/com/data_management/PatientRecord.java`

## Final message format expected by the parser

The parser now expects this WebSocket format:

`patientId,timestamp,label,data`

This matches the format already sent by `WebSocketOutputStrategy`.

## Fields extracted

The parser extracts these four fields:

1. patient ID
2. timestamp
3. measurement type or label
4. data value

After that:

- normal numeric values are parsed as `double`
- saturation values like `95.0%` are converted to `95.0`
- alert values are converted like this:
  - `triggered` -> `1.0`
  - `resolved` -> `0.0`

## Parsing behavior added

I added parsing logic inside `WebSocketDataReader`.

Main points:

- `onMessage(...)` now calls `handleMessage(...)`
- `handleMessage(...)` prints the message, parses it, and stores valid data
- `parseMessage(...)` splits the message into four parts
- valid parsed values are stored using:
  - `DataStorage.addPatientData(...)`

So this step already connects parsing to the existing storage method.

## Examples of valid messages

These are valid examples:

- `1,1712345678901,ECG,0.42`
- `1,1712345678902,Saturation,95.0%`
- `1,1712345678903,SystolicPressure,120`
- `1,1712345678904,Alert,triggered`
- `1,1712345678905,Alert,resolved`

## Examples of invalid messages

These are invalid examples:

- empty message
  - ``
- missing fields
  - `1,1000,ECG`
- wrong number format
  - `one,1000,ECG,0.42`
- unknown measurement type
  - `1,1000,UnknownType,0.42`
- bad alert value
  - `1,1000,Alert,maybe`
- corrupted message
  - `4,5000,broken`

## How invalid messages are handled

Invalid messages do not crash the program.

Instead, the reader:

- prints a warning to standard error
- skips the bad message
- keeps running

This is done for:

- empty messages
- missing fields
- invalid number formats
- unknown labels
- invalid data values

## What remains for storage integration

The basic storage integration is already working in this step because valid parsed messages are now saved with `DataStorage.addPatientData(...)`.

The next part still left to do is what happens after the data is stored, for example:

1. deciding when alert checking should run for live data
2. deciding whether it should run after every message or after a small batch
3. adding any extra workflow needed for real-time monitoring

## Test result

I ran:

`mvn test`

Result:

- Build success
- 44 tests run
- 0 failures
- 0 errors
- 0 skipped

## Recommended next step

The next step should be to connect the live stored data to the alert system.

A simple next move would be:

1. receive and store each valid WebSocket message
2. find the matching patient in storage
3. run the existing alert logic at a simple point in the live flow
