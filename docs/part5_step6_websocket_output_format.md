# Part 5 Step 6 WebSocket Output Format

## What I inspected

I inspected these files:

- `src/main/java/com/cardio_generator/outputs/WebSocketOutputStrategy.java`
- `src/main/java/com/cardio_generator/outputs/OutputStrategy.java`
- `src/main/java/com/cardio_generator/HealthDataSimulator.java`
- generator classes in `src/main/java/com/cardio_generator/generators`
- `src/main/java/com/data_management/WebSocketDataReader.java`
- `src/main/java/com/data_management/FileDataReader.java`

## Original WebSocket message format

The original format sent by `WebSocketOutputStrategy` was:

`patientId,timestamp,label,data`

It was created with:

`String.format("%d,%d,%s,%s", patientId, timestamp, label, data)`

## Was the original format usable as-is?

Yes.

The current format already includes the main information needed for real-time processing:

- patient ID
- timestamp
- measurement type or label
- measurement value

It also works for alert data because alert messages are sent with:

- label: `Alert`
- data: `triggered` or `resolved`

It also works for saturation data because the `%` stays inside the data field, for example:

- `1,1778235020518,Saturation,92.0%`

## Any fixes made?

No logic change was needed.

I did not change the actual message format because it was already simple and complete enough for the WebSocket client to read.

I only updated comments to document the exact field order more clearly in:

- `WebSocketOutputStrategy`
- `WebSocketDataReader`

## Final message format example

The final format is still:

`patientId,timestamp,label,data`

Example ECG message:

`1,1712345678901,ECG,0.42`

Example saturation message:

`1,1712345678902,Saturation,95.0%`

Example alert message:

`1,1712345678903,Alert,triggered`

## Compatibility notes with the WebSocket client

This format is compatible with the current `WebSocketDataReader` because the client already receives each message as one text string.

For the next step, the client can parse the message by splitting it into these four parts:

1. patient ID
2. timestamp
3. label
4. data

This is not the same text format as the Part 4 file reader, which expected:

`Patient ID: ..., Timestamp: ..., Label: ..., Data: ...`

But the WebSocket format is still clear and simple, so it is fine to keep it as the real-time format.

## Test result

I ran:

`mvn test`

Result:

- Build success
- 40 tests run
- 0 failures
- 0 errors
- 0 skipped

## Recommended next step

The next step should be to add the real parsing logic inside `WebSocketDataReader`.

That means:

1. split each incoming message into four parts
2. convert the values into the right types
3. store them in `DataStorage`
4. after that, connect the stored live data to the alert logic
