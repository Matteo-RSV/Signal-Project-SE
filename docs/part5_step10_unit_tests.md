# Part 5 Step 10 Unit Tests

## Test files modified

I updated these test files:

- `src/test/java/com/data_management/WebSocketDataReaderTest.java`
- `src/test/java/data_management/DataStorageTest.java`

I did not need to create a new test file because the project already had good test files for the WebSocket reader and storage classes.

## What each group of tests checks

### WebSocket reader tests

The WebSocket reader tests now check:

- valid ECG message parsing
- valid blood pressure message parsing
- valid saturation parsing
- valid alert parsing
- empty message handling
- missing field handling
- wrong patient ID format
- wrong value format
- unknown measurement type
- corrupted or random text
- valid messages being stored
- duplicate live messages being ignored
- stored live data being available to the alert logic
- incomplete data not crashing alert checks

### DataStorage real-time tests

The DataStorage tests now check:

- new patient data is stored
- existing patient data can be updated with more records
- duplicate real-time messages do not create duplicate records
- multiple measurements for one patient are stored safely
- alert access methods added in previous steps still work with stored live data

## Production code changes needed for testability

No production code changes were needed in this step.

The code already had enough package-level access and simple methods for testing.

## Maven test result

I ran:

`mvn test`

Result:

- Build success
- 56 tests run
- 0 failures
- 0 errors
- 0 skipped

## Remaining gaps for integration testing

These are still unit tests, not full integration tests.

What still remains for later:

1. a longer live run with the simulator and WebSocket reader together
2. checking alert timing during real streaming
3. checking how repeated live alerts should be shown or reduced
4. checking the packaged run path again, not just unit-tested code paths
