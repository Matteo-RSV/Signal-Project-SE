# Part 5 Step 12 Error Handling Tests

## Error cases tested

I added tests for these invalid or corrupted message cases:

- empty message
- random text
- missing patient ID
- missing measurement type
- missing value
- invalid number format
- unknown measurement type
- incomplete alert message

I also added a test that stores one valid message first, then sends one invalid message, and checks that the valid stored data is still there.

## Connection cases tested

I added tests for these reader/connection cases:

- `stop()` can be called safely before any connection is started
- `stop()` can be called more than once without crashing
- a failed connection attempt does not crash the program

For the connection failure case, I used a local URI with a refused port and checked that `start(...)` and `stop()` still finish safely.

## Whether production code needed small fixes

No production code changes were needed in this step.

The current `WebSocketDataReader` already handled bad messages and connection problems safely enough for these tests to pass.

## How invalid messages are handled

Invalid messages are handled by:

1. printing a warning
2. skipping the bad message
3. keeping the reader running
4. leaving existing valid data in `DataStorage` unchanged

So bad real-time input does not crash the reader and does not damage already stored patient data.

## Test files created or modified

I created:

- `src/test/java/com/data_management/WebSocketErrorHandlingTest.java`

I did not remove or weaken any earlier unit or integration tests.

## Maven test result

I ran:

`mvn test`

Result:

- Build success
- 67 tests run
- 0 failures
- 0 errors
- 0 skipped

## Remaining final validation tasks

The main things still left for later are:

1. a real end-to-end run with the simulator and reader connected together
2. checking alert behavior during a longer live session
3. checking the packaged run path again, not only the test path
4. deciding whether console warning output should stay as-is or be cleaned up later
