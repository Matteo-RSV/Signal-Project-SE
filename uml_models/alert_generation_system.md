# Alert Generation System

This subsystem is responsible for deciding when a patient reading has crossed a limit and needs attention. `AlertGenerator` does the main checking work. It looks at the newest reading, gets the threshold settings for that patient, and decides whether the reading should turn into an alert. Keeping that logic in one place makes it easier to add new rules later without changing how alerts are sent out.

`AlertRule` and `ThresholdProfile` keep the threshold logic organized. The rules describe what counts as a problem for a metric, while the threshold profile keeps patient-specific settings. That matters in a hospital setting because one patient may need tighter blood pressure limits than another. `PatientDataSnapshot` is a small class for the current reading being checked, instead of mixing raw data details directly into the generator.

`AlertManager` handles what happens after an alert is created. It stores open alerts and sends them to the right place, such as a nurse dashboard or another response queue. This keeps alert checking separate from alert routing. `DataStorage` is only used for reading the latest data and saving the alert record, so the alert subsystem does not take over long-term storage responsibilities.

Access rules are kept fairly narrow. `AlertGenerator` can read threshold settings and current readings, but patient identity is checked through `PatientIdentifier` before an alert is linked to someone. That reduces the chance of raising an alert for the wrong patient. Overall, the design is modular because alert checking, patient lookup, rule handling, and routing are all kept as separate responsibilities.
