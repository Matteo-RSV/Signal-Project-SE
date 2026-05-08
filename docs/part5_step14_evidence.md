# Part 5 Step 14 Evidence

## Evidence files created

I created these main evidence files in `docs/part5_evidence`:

- `part5_websocket_simulator_running.txt`
- `part5_websocket_client_receiving_data.txt`
- `part5_realtime_data_processed.txt`
- `part5_maven_tests_passing.txt`

There are also matching `stdout` and `stderr` helper files for these logs in the same folder.

## What each file proves

### `part5_websocket_simulator_running.txt`

This log shows that:

- the WebSocket simulator/server started
- it used WebSocket output mode
- it listened on port `8080`
- a client connection was accepted

### `part5_websocket_client_receiving_data.txt`

This log shows that:

- the WebSocket client connected to `ws://localhost:8080`
- the client received live real-time messages
- messages included ECG and saturation data

### `part5_realtime_data_processed.txt`

This log shows that:

- the integration test for the real-time flow ran
- WebSocket-style messages were handled
- invalid messages were skipped
- alert-related flow was reached
- Maven reported build success for that integration test run

### `part5_maven_tests_passing.txt`

This log shows that:

- the full `mvn test` run completed
- the full test suite passed
- Maven reported `67` tests run, `0` failures, `0` errors, `0` skipped

## Commands used

I used these commands or command forms:

1. Simulator evidence:

`java -cp "target\classes;C:\Users\matte\.m2\repository\org\java-websocket\Java-WebSocket\1.5.2\Java-WebSocket-1.5.2.jar;C:\Users\matte\.m2\repository\org\slf4j\slf4j-api\1.7.36\slf4j-api-1.7.36.jar" com.cardio_generator.Main --patient-count 1 --output websocket:8080`

2. Client evidence:

`java -cp "target\classes;C:\Users\matte\.m2\repository\org\java-websocket\Java-WebSocket\1.5.2\Java-WebSocket-1.5.2.jar;C:\Users\matte\.m2\repository\org\slf4j\slf4j-api\1.7.36\slf4j-api-1.7.36.jar" com.data_management.WebSocketReaderMain ws://localhost:8080`

3. Real-time processed evidence:

`mvn "-Dtest=com.data_management.WebSocketRealtimeFlowIntegrationTest" test`

4. Final test evidence:

`mvn test`

## Does the evidence show successful results?

Yes.

The logs show:

- the simulator started
- the client connected
- live messages were received
- real-time processing tests passed
- the full Maven test suite passed

## Missing evidence

I did not create image screenshots.

I used text logs instead because they are more reliable in this terminal workspace and still show the real command output honestly.
