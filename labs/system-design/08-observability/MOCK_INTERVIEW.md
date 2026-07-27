# Mock Interview: Observability

> System Design Mock Interview — 45-minute session

---

## Setup

**Role**: Platform Engineer Interviewer  
**Candidate Level**: Senior Engineer (L5)  
**Problem**: Design an observability platform for a microservices architecture with 500+ services.

---

## Transcript

**Interviewer**: "Our company has 500+ microservices running on Kubernetes across 3 regions. We need to understand what's happening in production. Design the observability platform covering logs, metrics, and traces."

**Candidate**: "Observability has three pillars: logs (discrete events), metrics (aggregated measurements), and traces (request-level end-to-end view). I'll design each pillar and how they integrate."

**Interviewer**: "Start with logging."

**Candidate**: "Each service writes structured logs (JSON format) to stdout/stderr. A sidecar container (Fluentd or Filebeat) reads the logs and sends them to a central log aggregator. I'd use Elasticsearch for storage and querying, with Kibana for visualization. The log pipeline: Fluentd → Kafka (buffering) → Logstash (processing) → Elasticsearch. Retention: 30 days hot, 1 year cold."

**Interviewer**: "Metrics?"

**Candidate**: "Prometheus for metrics collection and alerting. Each service exposes a `/metrics` endpoint. Prometheus scrapes these endpoints (pull model). For durability, Prometheus remote-writes to Thanos (long-term storage in object store). Grafana for dashboards. Key metrics per service: RED (Rate, Errors, Duration) — request rate, error rate, latency distribution (p50, p95, p99, p99.9)."

**Interviewer**: "Distributed tracing?"

**Candidate**: "OpenTelemetry for instrumentation (growing standard). Each service propagates trace context via HTTP headers (traceparent). Traces are sampled (1% by default, 100% for specific services or error traces). The tracing backend: Jaeger or Tempo. Integrating traces with logs: each log line includes the trace_id, so we can correlate logs to specific requests."

**Interviewer**: "How do you handle the scale of observability data?"

**Candidate**: "Tiered storage: hot data in fast storage (SSD), warm data in standard, cold data in object store. Downsampling: after 7 days, aggregate metrics to 1-minute resolution, then to 1-hour after 30 days. Sampling: traces are sampled by default. Adaptive sampling: automatically sample more during incidents, less during normal operation."

**Interviewer**: "How do you design actionable alerts?"

**Candidate**: "Alert on symptoms, not causes. Alert on user-impacting signals: high latency, error rate, low availability. Use multi-dimensional alerting: if p99 > 500ms for 5 minutes, page the on-call. Avoid alert fatigue: each service should have at most 5-10 significant alerts. Runbooks attached to each alert. For detection: use ML-based anomaly detection for baseline-fluctuating metrics."

---

## Key Takeaways

- **Three pillars**: Logs (Elasticsearch), Metrics (Prometheus), Traces (OpenTelemetry)
- **Structured logging**: JSON format, sidecar collection
- **RED metrics**: Rate, Errors, Duration — the essential microservice metrics
- **Trace sampling**: Adaptive sampling balances cost and coverage
- **Alert on symptoms**: User-impacting signals, not internal counters
- **Correlation**: trace_id in logs bridges observability pillars
