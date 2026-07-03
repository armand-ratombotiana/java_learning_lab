# Module 54: Observability & Distributed Tracing - Quizzes

---

## Q1: The Three Pillars
What are the three pillars of Observability?

A) CI, CD, and Source Control
B) Metrics, Logs, and Traces
C) Hardware, Software, and Networking
D) Alerting, Dashboards, and Reports

**Answer**: B
**Explanation**: Metrics provide numeric aggregations of system health, Logs provide detailed event descriptions, and Traces track the flow of requests across distributed boundaries.

---

## Q2: Distributed Tracing Identifiers
In a distributed tracing system, what is the difference between a Trace ID and a Span ID?

A) Trace ID is for the database, Span ID is for the web server.
B) Trace ID uniquely identifies an entire user request as it travels through multiple microservices. Span ID uniquely identifies a specific operation (like a single database query or HTTP call) within that larger trace.
C) Trace ID is used for security, Span ID is for logging.
D) There is no difference; they are synonymous.

**Answer**: B
**Explanation**: The Trace ID remains constant across the entire transaction, allowing tools like Zipkin to stitch all the logs together. New Span IDs are generated every time a new service boundary is crossed.

---

## Q3: High Cardinality
Why should you avoid tagging time-series metrics (like in Prometheus) with a `UserId`?

A) It causes high cardinality, which leads to exponential memory consumption and can crash the time-series database.
B) It is illegal under GDPR.
C) Prometheus cannot process strings.
D) It slows down the Java application significantly.

**Answer**: A
**Explanation**: Time-series databases create a new time-series for every unique combination of tags. If you have 1 million users, tagging by `UserId` creates 1 million time-series for a single metric, exhausting database memory. Tags should be bounded (e.g., HTTP status 200, 404, 500).