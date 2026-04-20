# Code Coverage Report

This document serves as the Part 3 coverage evidence file for the CHMS assignment.

## How the coverage report was generated

The coverage report was generated with Maven and JaCoCo using:

```sh
mvn clean verify
```

The JaCoCo Maven plugin is configured in `pom.xml` and runs during the Maven lifecycle.

## Generated report location

After running the command above, the HTML coverage report is available at:

- `target/site/jacoco/index.html`

The supporting machine-readable outputs are also generated at:

- `target/site/jacoco/jacoco.csv`
- `target/site/jacoco/jacoco.xml`
- `target/jacoco.exec`

## Coverage summary

The coverage summary below is based on the generated `target/site/jacoco/jacoco.csv` file after the final `mvn clean verify` run on 2026-04-20.

- Instructions covered: 918 of 2305 total, about 39.83%
- Lines covered: 204 of 520 total, about 39.23%
- Methods covered: 35 of 89 total, about 39.33%

## What was not tested and why

The current unit tests focus on the Part 3 functionality requested in the assignment manual:

- `Patient.getRecords`
- `FileDataReader`
- `AlertGenerator.evaluateData`
- alert triggering logic and related data handling

The following areas have little or no direct unit-test coverage:

- `HealthDataSimulator`
- the random data generator classes under `com.cardio_generator.generators`
- network output classes such as `TcpOutputStrategy` and `WebSocketOutputStrategy`
- console/file output adapters
- the new jar dispatch wrapper `Main`

These areas were not the primary target of the Part 3 unit-testing tasks, and several of them are difficult to test with small deterministic unit tests because they rely on:

- continuous scheduling
- randomness
- live socket/network behavior
- external runtime interaction rather than pure business logic

For this submission, the tested priority was the storage, parsing, and alert-analysis workflow required by the assignment.
