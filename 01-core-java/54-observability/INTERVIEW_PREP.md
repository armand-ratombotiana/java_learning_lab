# Module 54: Observability & Distributed Tracing - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is OpenTelemetry, and what problem does it solve in modern microservices?
**Answer**:
Historically, if a company used Jaeger for tracing and Prometheus for metrics, developers had to instrument their code using the Jaeger SDK and the Prometheus SDK. If the company later decided to switch to Datadog, they had to rip out all the old SDK code and rewrite it using the Datadog SDK. This created massive vendor lock-in.
**OpenTelemetry (OTel)** is an open-source standard (CNCF). It provides a single, vendor-agnostic SDK. Developers instrument their code *once* using OTel. The application sends data to an OTel Collector, which translates the telemetry into the format required by the backend vendor (Prometheus, Datadog, Splunk). If you change vendors, you simply update the Collector config file; no application code needs to change.

### Q2: How does a Trace ID actually propagate across multiple independent microservices over HTTP?
**Answer**:
When an HTTP request enters the system (e.g., at the API Gateway), the tracing instrumentation generates a unique Trace ID.
When the first microservice uses an HTTP Client (like `RestTemplate` or `WebClient`) to call a downstream microservice, the tracing instrumentation automatically intercepts the outgoing HTTP request and injects the Trace ID into the HTTP Headers. 
The W3C Trace Context standard defines the `traceparent` header for this exact purpose. The downstream service reads this header, adopts the Trace ID, and continues the chain.

### Q3: What is the difference between Log Aggregation and Log Monitoring?
**Answer**:
- **Log Aggregation** (e.g., ELK Stack) is the mechanical process of collecting raw log files from hundreds of scattered containers, parsing them into JSON, and dumping them into a centralized database (Elasticsearch) so they can be easily searched during debugging.
- **Log Monitoring** goes a step further. It involves setting up automated rules that continuously scan the aggregated logs in real-time to detect anomalies, error spikes, or security breaches, and triggering alerts (e.g., sending a Slack message or PagerDuty alert if the phrase `NullPointerException` appears more than 50 times in one minute).

---

## 💻 Whiteboarding Scenarios

### Scenario 1: The Disappearing Trace
**Problem**: An interviewer presents a microservices architecture. Service A receives a request, generates a trace, and publishes an event to an Apache Kafka topic. Service B consumes the event from Kafka and processes it. However, in Jaeger, Service A's trace ends abruptly at the Kafka publish, and Service B's processing shows up as a completely separate, disconnected trace. Why? How do you fix it?

**Solution**:
The trace context was lost because it was not propagated through the message broker. 
Unlike HTTP, which has standard headers, messaging systems require explicit context injection.
**Fix**: The tracing instrumentation in Service A must be configured to inject the `Trace ID` and `Span ID` into the **Kafka Message Headers**. The instrumentation in Service B must be configured to extract those headers upon consumption and apply them to the local `MDC` (Mapped Diagnostic Context) so the trace continues unbroken across the asynchronous boundary.