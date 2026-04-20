# Patient Identification System

This subsystem makes sure that incoming monitoring data is attached to the correct patient. That is important because a wrong match could send readings, alerts, or history to the wrong person. The design keeps matching, record lookup, access control, and anomaly handling separate so the identity process stays easier to follow and safer to maintain.

`PatientIdentifier` handles the actual matching logic. It checks the incoming token, compares demographic details, and decides how strong the match is. The result is stored in `MatchResult`, which makes it clear whether the patient was matched, not found, or still needs review. `HospitalPatient` stores the patient details that matter here, such as the name, record number, admission status, and a short history note.

`PatientRepository` is responsible for retrieving patient records, instead of mixing database-style lookup into the matching class. `IdentityManager` sits above both parts and coordinates the full process. It asks the repository for possible matches, calls the identifier, and logs an `IdentityAnomaly` if there is no safe result. This is where mismatch and anomaly handling is made explicit, rather than letting bad identity data slip through.

Access rules are handled by `AccessPolicy`. Even if a patient is matched, the requester must still be allowed to open that record. If needed, sensitive fields can be hidden before the record is returned. This keeps privacy in mind and limits how much identity data is exposed outside the subsystem. The overall design is modular because each class has one main job, which makes the subsystem easier to extend and less likely to turn into one large identity class.
