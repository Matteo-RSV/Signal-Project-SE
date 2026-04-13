# UML Models

This directory contains the Phase 1 UML class diagram deliverables for the CHMS design. Each subsystem is documented with:

- a PlantUML source file (`.puml`) that can be rendered into a UML class diagram
- a Markdown explanation file (`.md`) that justifies the design, responsibilities, and access rules

The four subsystem packages are:

| Subsystem | Diagram Source | Explanation |
| --- | --- | --- |
| Alert Generation System | [alert_generation_system.puml](alert_generation_system.puml) | [alert_generation_system.md](alert_generation_system.md) |
| Data Storage System | [data_storage_system.puml](data_storage_system.puml) | [data_storage_system.md](data_storage_system.md) |
| Patient Identification System | [patient_identification_system.puml](patient_identification_system.puml) | [patient_identification_system.md](patient_identification_system.md) |
| Data Access Layer | [data_access_layer.puml](data_access_layer.puml) | [data_access_layer.md](data_access_layer.md) |

Together, these models describe how the CHMS can be decomposed into cohesive subsystems with clear interfaces, controlled data flow, and room for future extension. The files are documentation artifacts only and do not alter the simulator runtime.
