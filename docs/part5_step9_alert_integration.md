# Part 5 Step 9 Alert Integration

## How alert logic worked before

Before this step, the alert logic already existed in `com.alerts.AlertGenerator`.

It worked mainly like this:

1. patient records were loaded into storage
2. a patient was given to `AlertGenerator.evaluateData(...)`
3. the alert generator looked through the stored records
4. it returned any triggered alerts for that patient

This already worked well for Part 4 file-based data, but it was not clearly connected to the new live WebSocket flow yet.

## What was changed for real-time data

I made small changes so the live WebSocket path can use the existing alert logic.

Main changes:

- `DataStorage` now has `getPatient(int patientId)`
- `DataStorage` now has `checkAlertsForPatient(int patientId)`
- `WebSocketDataReader` now calls alert checking after a valid message is stored

I did not rewrite the alert system itself.

## How WebSocket data reaches the alert logic

The real-time flow now works like this:

1. a WebSocket message arrives
2. `WebSocketDataReader` parses it
3. valid data is stored in `DataStorage`
4. `WebSocketDataReader` asks `DataStorage` to check alerts for that patient
5. `DataStorage` creates an `AlertGenerator` and evaluates the stored records for that patient
6. any triggered alerts are returned and printed

So the flow is now:

`WebSocket message -> WebSocketDataReader -> DataStorage -> AlertGenerator`

## How alert messages or alert checks are handled

The simulator can also send alert-type messages like:

`1,1712345678903,Alert,triggered`

Those are already parsed into stored `Alert` records with values like:

- `triggered` -> `1.0`
- `resolved` -> `0.0`

The existing alert logic already knows how to use stored `Alert` records, so no big change was needed there.

For measurements like saturation, ECG, or blood pressure, the alert generator reads the latest stored records and checks them using the same rules as before.

## Missing or incomplete data

Missing data is handled safely.

Examples:

- if the patient is not in storage yet, `checkAlertsForPatient(...)` returns an empty list
- if a message is invalid, it is skipped before storage
- if a patient does not yet have enough measurements for a certain alert rule, the alert generator simply does not trigger that alert

So the program does not crash just because some live data is still incomplete.

## Limits or assumptions

There are a few simple limits in the current version:

1. alerts are checked again after each valid live message for that patient
2. alerts are printed when found, but they are not stored in a separate live alert history yet
3. duplicate records are skipped in storage, but checking the same alert again later can still print the current alert state again
4. this step keeps the existing alert rules exactly as they are

## What remains for testing

The core real-time integration is now connected, but more end-to-end testing would still help.

Good next tests would be:

1. run the simulator and reader together for longer
2. check that alerts appear at the expected times during live streaming
3. decide whether repeated alert printing should be reduced in a future step
4. decide whether triggered alerts should also be stored or shown in a more structured way

## Test result

I ran:

`mvn test`

Result:

- Build success
- 50 tests run
- 0 failures
- 0 errors
- 0 skipped
