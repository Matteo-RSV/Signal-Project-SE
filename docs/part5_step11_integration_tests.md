# Part 5 Step 11 Integration Tests

## Integration test files created or modified

I created this integration test file:

- `src/test/java/com/data_management/WebSocketRealtimeFlowIntegrationTest.java`

I did not remove or weaken the unit tests from Step 10.

## What full flows are tested

The new integration tests cover these flows:

1. normal measurement flow
   - WebSocket-style message
   - `WebSocketDataReader.handleMessage(...)`
   - parsing
   - `DataStorage` update
   - stored data read back and checked

2. repeated update for the same patient
   - multiple messages for one patient
   - repeated duplicate message
   - storage keeps the useful updates
   - duplicate record is not added again

3. invalid message handling
   - valid message first
   - broken message after that
   - storage still stays correct
   - invalid message does not corrupt stored data

4. alert-related flow
   - WebSocket-style messages update storage
   - alert access method reads the new stored data
   - expected alert conditions can be found

## Was a real WebSocket connection used?

No.

I tested the message handler directly by calling:

- `WebSocketDataReader.handleMessage(...)`

## Why that approach was chosen

I chose the direct message-handler approach because:

- it still tests the real in-project flow after a message arrives
- it avoids fragile socket timing in unit/integration tests
- it keeps the tests simple and readable
- it matches your instruction that this approach is acceptable if a real server would make the tests too complex

So these tests still cover:

`message -> parser -> DataStorage -> alert access`

without needing a full live socket setup.

## Maven test result

I ran:

`mvn test`

Result:

- Build success
- 60 tests run
- 0 failures
- 0 errors
- 0 skipped

## Remaining work for error handling tests and final validation

There are still some later checks that would be useful:

1. a real end-to-end run with the simulator and WebSocket reader connected together
2. longer-running live tests to watch alert behavior over time
3. checking whether repeated alert printing should be reduced
4. validating the packaged run path again, not only the test path
