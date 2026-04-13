# Cardio Data Simulator

The Cardio Data Simulator is a Java-based application designed to simulate real-time cardiovascular data for multiple patients. This tool is particularly useful for educational purposes, enabling students to interact with real-time data streams of ECG, blood pressure, blood saturation, and other cardiovascular signals.

## Features

- Simulate real-time ECG, blood pressure, blood saturation, and blood levels data.
- Supports multiple output strategies:
  - Console output for direct observation.
  - File output for data persistence.
  - WebSocket and TCP output for networked data streaming.
- Configurable patient count and data generation rate.
- Randomized patient ID assignment for simulated data diversity.

## Getting Started

### Prerequisites

- Java JDK 11 or newer.
- Maven for managing dependencies and compiling the application.

### Installation

1. Clone the repository:

   ```sh
   git clone https://github.com/tpepels/signal_project.git
   ```

2. Navigate to the project directory:

   ```sh
   cd signal_project
   ```

3. Compile and package the application using Maven:
   ```sh
   mvn clean package
   ```
   This step compiles the source code and packages the application into an executable JAR file located in the `target/` directory.

### Running the Simulator

After packaging, you can run the simulator directly from the executable JAR:

```sh
java -jar target/cardio_generator-1.0-SNAPSHOT.jar
```

To run with specific options (e.g., to set the patient count and choose an output strategy):

```sh
java -jar target/cardio_generator-1.0-SNAPSHOT.jar --patient-count 100 --output file:./output
```

### Supported Output Options

- `console`: Directly prints the simulated data to the console.
- `file:<directory>`: Saves the simulated data to files within the specified directory.
- `websocket:<port>`: Streams the simulated data to WebSocket clients connected to the specified port.
- `tcp:<port>`: Streams the simulated data to TCP clients connected to the specified port.

## UML Models

This repository now includes UML class diagrams for the CHMS design used in Phase 1 of the Software Engineering project. These files are documentation deliverables only and do not change the simulator's runtime behavior.

See [uml_models](uml_models/) for the complete set of PlantUML diagrams and subsystem explanations.

The subsystem diagrams currently cover:

- [Alert Generation System](uml_models/alert_generation_system.puml) with its [design explanation](uml_models/alert_generation_system.md)
- [Data Storage System](uml_models/data_storage_system.puml) with its [design explanation](uml_models/data_storage_system.md)
- [Patient Identification System](uml_models/patient_identification_system.puml) with its [design explanation](uml_models/patient_identification_system.md)
- [Data Access Layer](uml_models/data_access_layer.puml) with its [design explanation](uml_models/data_access_layer.md)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Project Members

- Student ID: I6436863
