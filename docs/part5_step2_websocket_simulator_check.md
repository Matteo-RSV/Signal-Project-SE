# Part 5 Step 2 WebSocket Simulator Check

## What I checked

I inspected these files:

- `pom.xml`
- `src/main/java/com/cardio_generator/Main.java`
- `src/main/java/com/cardio_generator/HealthDataSimulator.java`
- `src/main/java/com/cardio_generator/outputs/WebSocketOutputStrategy.java`

## How the simulator is started right now

The jar entry point is `com.cardio_generator.Main`.

From `Main.java`:

- if the first argument is `DataStorage`, it runs the storage program
- if the first argument is `HealthDataSimulator`, it runs the simulator
- if no entry point is given, it still runs `HealthDataSimulator`

So this command style is valid for this project:

`java -jar target/cardio_generator-1.0-SNAPSHOT.jar --output websocket:8080`

## Maven setup

From `pom.xml`:

- `maven-jar-plugin` sets the main class to `com.cardio_generator.Main`
- `spring-boot-maven-plugin` also sets the main class to `com.cardio_generator.Main`
- there is no `exec-maven-plugin` configured in the project
- the WebSocket library dependency is:
  - `org.java-websocket:Java-WebSocket:1.5.2`

Because there is no project-specific exec plugin setup, I used the jar/classpath approach instead of `mvn exec:java`.

## Command-line arguments supported by the simulator

From `HealthDataSimulator.java`, these arguments are supported:

- `-h`
- `--patient-count <count>`
- `--output <type>`

For output, these options exist:

- `console`
- `file:<directory>`
- `websocket:<port>`
- `tcp:<port>`

For WebSocket mode, the simulator does this:

1. reads the port from `websocket:<port>`
2. creates `new WebSocketOutputStrategy(port)`
3. prints `WebSocket output will be on port: <port>`

## Commands I used

I used these commands during the check:

1. `mvn clean package`
2. `mvn package`
3. `java -jar target/cardio_generator-1.0-SNAPSHOT.jar --output websocket:8080`
4. a short PowerShell wrapper that started this command for 5 seconds and then stopped it:

`java -cp "target\classes;C:\Users\matte\.m2\repository\org\java-websocket\Java-WebSocket\1.5.2\Java-WebSocket-1.5.2.jar;C:\Users\matte\.m2\repository\org\slf4j\slf4j-api\1.7.36\slf4j-api-1.7.36.jar" com.cardio_generator.Main --patient-count 1 --output websocket:8080`

5. `mvn test`

## What happened

### 1. `mvn clean package`

This failed during the clean step.

Exact reason:

`Failed to delete C:\Users\matte\OneDrive\Desktop\Signal-Project-SE\target\test-classes\data_management\FileDataReaderTest.class`

So the build did not fail because of WebSocket code first. It failed earlier while trying to clean the `target` folder.

### 2. `mvn package`

This got much further:

- compile step was fine
- tests passed
- jar file was built

But the package still ended with a failure in the Spring Boot repackage step.

Exact reason:

`Unable to rename 'C:\Users\matte\OneDrive\Desktop\Signal-Project-SE\target\cardio_generator-1.0-SNAPSHOT.jar' to 'C:\Users\matte\OneDrive\Desktop\Signal-Project-SE\target\cardio_generator-1.0-SNAPSHOT.jar.original'`

So the project did compile, but the full package process was not fully successful.

### 3. Running the jar directly

I tested:

`java -jar target/cardio_generator-1.0-SNAPSHOT.jar --output websocket:8080`

This did **not** start WebSocket mode successfully.

Exact error:

`java.lang.NoClassDefFoundError: org/java_websocket/server/WebSocketServer`

This means the plain jar did not have the needed runtime dependency available when started this way.

### 4. Running with the dependency jars on the classpath

I then tested with the compiled classes and the needed jars on the Java classpath.

This run **did start the simulator in WebSocket mode** on port `8080`.

Messages shown:

- `WebSocket server created on port: 8080, listening for connections...`
- `WebSocket output will be on port: 8080`
- `Server started successfully`

There was also a logging warning:

- `SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".`
- `SLF4J: Defaulting to no-operation (NOP) logger implementation`

This warning did not stop the simulator from starting.

## Did the simulator start in WebSocket mode?

Yes, but not from the simple `java -jar ...` command.

My result was:

- `java -jar ...` -> failed
- classpath-based Java run -> worked

So the simulator code and `WebSocketOutputStrategy` can start a WebSocket server, but the normal packaged-jar running path still has packaging/runtime dependency problems.

## Which port was used?

I used port `8080`.

It was available, so I did not need to switch to `8081` or `9090`.

## Does a WebSocket client still need to be created?

Yes.

At this step, I only checked that the simulator side can open the WebSocket server. A separate WebSocket client/reader is still needed on the data management side to receive and process the streamed messages.

## Test result

I ran:

`mvn test`

Result:

- Build success
- 40 tests run
- 0 failures
- 0 errors
- 0 skipped

## Main problems found

I found these main issues:

1. `mvn clean package` fails while deleting part of `target`
2. `mvn package` fails in the Spring Boot repackage rename step
3. `java -jar ...` fails because the WebSocket runtime class is not found
4. when I manually included the needed jars, the simulator started correctly

## Recommended next step

The next step should be to prepare the receiving side for Part 5:

1. keep the simulator code as it is for now
2. create a simple WebSocket client/reader that connects to the simulator
3. parse the streamed messages
4. store them in `DataStorage`
5. after that, look at the packaging problem separately so the jar can run more cleanly

Right now, the simulator side is close to usable, but the project still needs a WebSocket client and a cleaner runtime/package setup.
