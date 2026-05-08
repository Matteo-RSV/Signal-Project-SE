# Part 5 Step 3 DataReader Streaming Update

## What DataReader looked like before

Before this step, `DataReader` only had one method:

`void readData(DataStorage dataStorage) throws IOException;`

That worked for Part 4 because the file reader only needed to read a folder one time.

## What I changed

I changed `DataReader` so it now looks like this idea:

- `start(DataStorage dataStorage)`
- `stop()`

I kept the `DataStorage` parameter in `start(...)` because it was the simplest way to keep the current project structure working without adding extra setup code.

## Why this change helps

This new shape is better for streaming.

- a file reader can start, do one pass, and finish
- a future WebSocket reader can start, keep receiving data, and stop later

So the interface now works for both:

- old file-based reading
- future real-time streaming

## Which classes had to be updated

I updated these classes:

- `src/main/java/com/data_management/DataReader.java`
- `src/main/java/com/data_management/FileDataReader.java`
- `src/main/java/com/data_management/DataStorage.java`
- `src/test/java/data_management/FileDataReaderTest.java`

## How the file-based reader still works

`FileDataReader` still reads the same `*.txt` files from Part 4.

The old behavior was not deleted. It was just moved into `start(...)`.

So now:

- `start(dataStorage)` reads the files once and stores the parsed records
- `stop()` is simple and just marks the reader as stopped

This keeps the Part 4 reader working while also preparing the project for a future streaming reader.

## Small code notes

I also added simple comments in the code to explain:

- why `DataReader` now uses start/stop
- how that fits future WebSocket streaming
- why `FileDataReader` still only reads once

I did not create a WebSocket data reader yet.

## Test result

I ran:

`mvn test`

Result:

- Build success
- 40 tests run
- 0 failures
- 0 errors
- 0 skipped

So the interface change did not break the existing tests.

## What remains for the next step

The next step is to add the real streaming reader.

That will probably mean:

1. create a WebSocket-based class that implements `DataReader`
2. put the connection logic in `start(...)`
3. put the closing logic in `stop()`
4. parse incoming messages and store them in `DataStorage`

At this step, the project is only prepared for that work. The WebSocket client itself is not implemented yet.
