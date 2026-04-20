# Data Storage System

The purpose of this subsystem is to keep patient measurements stored safely so they can be checked later, either for current care or for past trends. `DataStorage` is the main class that holds the records and applies the basic storage rules. It is not responsible for deciding who is allowed to read data, which helps keep its job focused.

`PatientData` represents one recorded measurement at one point in time. It stores the patient ID, the kind of reading, the measured value, the unit, and the timestamp. This makes historical lookup possible because records can be searched by patient and time range. A measurement can also have a correction history. That is why `PatientData` is linked to `DataVersion`, which keeps track of updated values, who changed them, when the change happened, and why it was needed.

`DataRetriever` handles read requests from staff. Instead of letting every part of the system query storage directly, it goes through `AccessController` first. The access controller checks the request details and can filter the returned record based on the requester role. This is important in a medical system because not every user should see every piece of information in the same way.

The subsystem also includes `RetentionPolicy`, which decides whether old records should be archived or deleted, and `AuditEntry`, which keeps a log of who accessed data. That gives the model a more realistic hospital feel. The overall design stays maintainable because storage, retrieval, access control, version tracking, and data cleanup are all separated instead of being pushed into one large class.
