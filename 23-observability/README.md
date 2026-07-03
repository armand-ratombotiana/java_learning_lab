# 23 - Observability (Micrometer & OpenTelemetry)

Application observability with Micrometer metrics and OpenTelemetry distributed tracing. Covers meter types (Counter, Gauge, Timer, DistributionSummary), tagged metrics, span creation and context propagation, distributed traces across services, and error tracking.

## Prerequisites

- Java 11+
- Maven 3.x

## Key Concepts

- **Micrometer**: Counter (increment operations), Gauge (current value), Timer (latency measurement), DistributionSummary (sample distribution)
- Tagged metrics for dimensional analysis
- **OpenTelemetry**: Trace/Span model, SpanContext (traceId, spanId, parentSpanId), context propagation
- Distributed tracing across service boundaries
- Span attributes, status (OK/ERROR), and timing
- Exporter pipeline (collector, backend)

## Module Structure

- `micrometer-learning/` - Micrometer metrics collection simulation
- `opentelemetry-learning/` - OpenTelemetry distributed tracing simulation

## Learning Objectives

- Collect application metrics using Micrometer meter types
- Create and manage distributed traces with OpenTelemetry
- Understand context propagation across service boundaries

## Estimated Time

- 3-4 hours

## How to Build

```bash
cd 23-observability
mvn clean package
```

Run individual labs:

```bash
cd micrometer-learning
mvn compile exec:java -Dexec.mainClass="com.learning.micrometer.Lab"

cd opentelemetry-learning
mvn compile exec:java -Dexec.mainClass="com.learning.otel.Lab"
```
