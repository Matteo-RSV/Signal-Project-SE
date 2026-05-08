# Part 5 Step 4 WebSocket Reader

## What I inspected

I checked these files before adding the new reader:

- `src/main/java/com/data_management/DataReader.java`
- `src/main/java/com/data_management/DataStorage.java`
- `src/main/java/com/data_management/FileDataReader.java`
- `src/main/java/com/cardio_generator/outputs/WebSocketOutputStrategy.java`
- `src/main/java/com/cardio_generator/HealthDataSimulator.java`
- `pom.xml`

## Classes created

I created these classes:

- `src/main/java/com/data_management/WebSocketDataReader.java`
- `src/main/java/com/data_management/WebSocketReaderMain.java`

## What the new WebSocket reader does

`WebSocketDataReader` implements `DataReader`.

It:

- connects to a WebSocket server using a URI like `ws://localhost:8080`
- listens for messages continuously
- prints each received message to the console for now
- opens the connection in `start(...)`
- closes the connection in `stop()`

I did not add full parsing and storage yet, because this step was mainly about proving that the client connection works.

## How it connects to the simulator

The simulator already sends WebSocket messages in this format:

`patientId,timestamp,label,data`

Example:

`1,1778235021517,ECG,-0.31172943531850494`

`WebSocketDataReader` uses the Java-WebSocket client class to connect to the simulator server at a URI such as:

`ws://localhost:8080`

## WebSocket event handling added

The new reader has simple handling for:

- `onOpen`
  - prints that the connection was successful
- `onMessage`
  - prints each incoming message
- `onClose`
  - prints that the connection was closed
- `onError`
  - prints a useful error message

The error handling is simple on purpose, but it avoids crashing the whole program on a basic connection problem.

## Commands used

I used these commands for this step:

1. `mvn test`

2. Simulator command used for manual checking:

`java -cp "target\classes;C:\Users\matte\.m2\repository\org\java-websocket\Java-WebSocket\1.5.2\Java-WebSocket-1.5.2.jar;C:\Users\matte\.m2\repository\org\slf4j\slf4j-api\1.7.36\slf4j-api-1.7.36.jar" com.cardio_generator.Main --patient-count 1 --output websocket:8080`

3. Client command used for manual checking:

`java -cp "target\classes;C:\Users\matte\.m2\repository\org\java-websocket\Java-WebSocket\1.5.2\Java-WebSocket-1.5.2.jar;C:\Users\matte\.m2\repository\org\slf4j\slf4j-api\1.7.36\slf4j-api-1.7.36.jar" com.data_management.WebSocketReaderMain ws://localhost:8080`

## Did the project compile?

Yes.

`mvn test` completed successfully after the new classes were added.

## Were messages received?

Yes.

During the manual check, the client connected and printed live messages from the simulator, for example:

- `Received WebSocket message: 1,1778235020518,Saturation,92.0%`
- `Received WebSocket message: 1,1778235021517,ECG,-0.31172943531850494`
- `Received WebSocket message: 1,1778235025518,Saturation,96.0%`

So the WebSocket client/reader is able to connect and receive real-time data.

## Errors or warnings found

I did not get a connection failure during the successful test.

I did see this runtime warning from SLF4J:

- `SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".`
- `SLF4J: Defaulting to no-operation (NOP) logger implementation`

This warning did not stop the simulator or the client from working.

I did not change `pom.xml` in this step because the Java-WebSocket dependency was already present and the code compiled with it.

## Test result

I ran:

`mvn test`

Result:

- Build success
- 40 tests run
- 0 failures
- 0 errors
- 0 skipped

## What remains for the next step

The next step should be to make the WebSocket reader do more than print messages.

That likely means:

1. parse each incoming message
2. convert it into patient ID, timestamp, label, and value
3. store it in `DataStorage`
4. then connect that stored data to the alert checking flow

So at this point, the connection part works, but the final parsing and storage logic is still the next job.
