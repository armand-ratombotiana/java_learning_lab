# Module 54: Observability & Distributed Tracing - Deep Dive

**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-53 (especially Microservices and Spring Boot)  
**Estimated Reading Time**: 60 minutes  

---

## 📚 Table of Contents

1. [The Three Pillars of Observability](#pillars)
2. [Distributed Tracing Concepts](#tracing)
3. [Metrics & Micrometer](#metrics)
4. [Centralized Logging](#logging)
5. [OpenTelemetry](#opentelemetry)

---

## 1. The Three Pillars of Observability <a name="pillars"></a>
Observability is the ability to understand the internal state of a system simply by looking at its external outputs. It relies on three pillars:
- **Metrics**: Aggregated numerical data over a period of time (e.g., CPU usage, HTTP error rates).
- **Logs**: Immutable, timestamped records of discrete events (e.g., an exception stack trace).
- **Traces**: A representation of a single user request as it travels through a distributed system.

---

## 2. Distributed Tracing Concepts <a name="tracing"></a>
In microservices, a single request might hit 10 different services. Tracing correlates these scattered logs.
- **Trace ID**: A unique identifier generated at the API Gateway and passed to every downstream service. It represents the entire request.
- **Span ID**: Represents a specific operation within the trace (e.g., a database query or an HTTP call to another service).
- Tools: Zipkin, Jaeger, Grafana Tempo. Spring Boot 3 uses Micrometer Tracing (replacing Spring Cloud Sleuth).

---

## 3. Metrics & Micrometer <a name="metrics"></a>
Micrometer is to metrics what SLF4J is to logging—a facade that abstracts away the underlying monitoring system (e.g., Prometheus, Datadog, New Relic).
Spring Boot Actuator integrates tightly with Micrometer to expose metrics at `/actuator/prometheus`.

```java
@RestController
public class OrderController {
    private final Counter orderCounter;

    public OrderController(MeterRegistry registry) {
        this.orderCounter = registry.counter("orders.placed.total", "type", "online");
    }

    @PostMapping("/order")
    public void placeOrder() {
        orderCounter.increment();
        // process order
    }
}
```

---

## 4. Centralized Logging <a name="logging"></a>
Microservices logs are useless if you have to SSH into 50 different machines to read them.
- **ELK/EFK Stack**: Elasticsearch, Logstash/Fluentd, Kibana.
- Logs are written to `stdout` in JSON format. Fluentd (a daemonset in Kubernetes) scrapes the logs, parses the JSON, and ships them to Elasticsearch for centralized querying via Kibana.

---

## 5. OpenTelemetry <a name="opentelemetry"></a>
OpenTelemetry (OTel) is an open-source standard created by the CNCF (Cloud Native Computing Foundation). Instead of using different SDKs for Prometheus, Jaeger, and Datadog, developers instrument their application *once* using the OTel SDK. The OTel Collector then receives the telemetry data and translates it to any vendor format.