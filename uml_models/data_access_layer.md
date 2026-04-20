# Data Access Layer

The Data Access Layer sits between the external signal generator and the rest of CHMS. Its job is to read raw incoming data, turn that data into a clean internal object, and pass it on without making the core system care whether the data came from a TCP stream, a WebSocket, or a file. That separation is useful because the input source may change, but the rest of the system should not need to change with it.

`DataListener` gives all listener types the same basic contract. `TCPDataListener`, `WebSocketDataListener`, and `FileDataListener` each deal with a different source, but they all know how to open a source, read a message, and close it again. This keeps transport-specific details inside the listener classes instead of leaking them into the rest of the design.

Parsing is handled separately by `DataParser`. The two parser classes, `JsonDataParser` and `CsvDataParser`, show that the data format can vary independently from the transport. For example, a file and a TCP stream could both use JSON, so it makes sense to keep parsing logic out of the listener classes. Both parsers create the same `IncomingReading` object, which gives the rest of CHMS one standard shape to work with.

`DataSourceAdapter` ties the pieces together. It uses one listener and one parser, checks that the reading is valid, and forwards it to `MeasurementReceiver`, which stands for the next internal step in the system. That means the rest of CHMS only sees cleaned-up readings, not raw socket text or file lines. The design is modular because listening, parsing, and handoff are all separate jobs, which makes future extensions easier.
