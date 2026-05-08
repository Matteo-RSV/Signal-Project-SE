# Part 5 Step 15 Code Documentation

## Files reviewed

I reviewed these Part 5 files:

- `src/main/java/com/data_management/DataReader.java`
- `src/main/java/com/data_management/WebSocketDataReader.java`
- `src/main/java/com/data_management/DataStorage.java`
- `src/main/java/com/cardio_generator/outputs/WebSocketOutputStrategy.java`
- `src/main/java/com/alerts/AlertGenerator.java`
- `src/test/java/com/data_management/WebSocketDataReaderTest.java`
- `src/test/java/com/data_management/WebSocketRealtimeFlowIntegrationTest.java`

## Files where comments were added or improved

I improved comments in:

- `src/main/java/com/data_management/DataReader.java`
- `src/main/java/com/data_management/WebSocketDataReader.java`
- `src/main/java/com/data_management/DataStorage.java`
- `src/main/java/com/cardio_generator/outputs/WebSocketOutputStrategy.java`
- `src/main/java/com/alerts/AlertGenerator.java`

## What areas were documented

I added or improved comments around:

- why `DataReader` uses `start()` and `stop()`
- how the WebSocket reader stores its shared `DataStorage`
- why the WebSocket connection setup is done before live messages arrive
- what `onOpen`, `onMessage`, `onClose`, and `onError` are responsible for
- how the WebSocket message is split and parsed
- how invalid messages are skipped safely
- how alert values and saturation values are converted
- how real-time data is stored in `DataStorage`
- why duplicate records are skipped
- why synchronized storage methods are used
- how live stored data is passed to the existing alert logic

## Behavior change confirmation

I did not intentionally change program behavior in this step.

This was a documentation-only step. The edits were comments and Javadoc improvements to make the Part 5 real-time flow easier to understand.

## Maven test result

I ran:

`mvn test`

Result:

- Build success
- 67 tests run
- 0 failures
- 0 errors
- 0 skipped
