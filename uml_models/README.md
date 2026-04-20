# UML Models

This folder contains the UML work for the CHMS design. Each subsystem has:

- a PlantUML source file (`.puml`) that can be rendered into a UML class diagram
- a short Markdown explanation file (`.md`) that explains the design choices and class responsibilities

The four subsystem areas are:

| Subsystem | Diagram Source | Explanation |
| --- | --- | --- |
| Alert Generation System | [alert_generation_system.puml](alert_generation_system.puml) | [alert_generation_system.md](alert_generation_system.md) |
| Data Storage System | [data_storage_system.puml](data_storage_system.puml) | [data_storage_system.md](data_storage_system.md) |
| Patient Identification System | [patient_identification_system.puml](patient_identification_system.puml) | [patient_identification_system.md](patient_identification_system.md) |
| Data Access Layer | [data_access_layer.puml](data_access_layer.puml) | [data_access_layer.md](data_access_layer.md) |

Together, these files show how the CHMS can be split into smaller subsystems with clear roles and cleaner boundaries. They are documentation only and do not change how the simulator runs.
