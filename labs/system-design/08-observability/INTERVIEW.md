# Observability - INTERVIEW

## Common Interview Questions

### Q1: Explain the three pillars of observability.
**Answer**: Logs (detailed event records), Metrics (aggregated, numeric time-series data), Traces (request flow across services). Together they provide a complete picture: logs tell you what happened, metrics tell you what's happening, traces tell you where it happened.

### Q2: What are the RED and USE methods?
**Answer**: RED (Rate, Errors, Duration) for services: request rate, error rate, latency distribution. USE (Utilization, Saturation, Errors) for resources: CPU utilization, queue saturation, error count.

### Q3: How do you choose trace sampling rate?
**Answer**: Consider storage cost vs investigative value. 1% is typical for high-volume services. Always sample errors 100%. Consider tail-based sampling (collect all, keep interesting ones). Multiply: 1000 RPS × 86400 seconds × 1KB × 1% = ~864MB/day.

### Q4: What's the difference between monitoring and observability?
**Answer**: Monitoring tells you a system is in a known bad state (predefined dashboards, thresholds). Observability lets you understand unknown unknowns — debug issues you didn't anticipate. Monitoring needs predefined questions; observability enables open-ended exploration.

### Q5: How do you handle high-cardinality metrics?
**Answer**: Don't put high-cardinality labels (user IDs, session IDs, request IDs) on metrics. Use logs/traces for those details. Add only low-cardinality labels (service name, region, status code, endpoint pattern).

### Q6: What is an SLO and how do you calculate error budget?
**Answer**: SLO = Service Level Objective (e.g., 99.9% availability over 30 days). Error budget = (1 - SLO) × time period. For 99.9% monthly: 0.001 × 30 × 24 × 60 = 43.2 minutes of allowed downtime. If you've exceeded your error budget, stop deploying and focus on reliability.

### Q7: How do you implement distributed tracing?
**Answer**: Use OpenTelemetry SDK or auto-instrumentation agent. Propagate trace context (traceparent header) across service calls. Export spans to Jaeger or similar. Sample appropriately. Use trace ID to correlate spans from all services involved.

## System Design Problem: Design an Observability Platform

### Requirements
- Monitor 200 microservices
- Store 30 days of metrics and 7 days of traces
- Support incident investigation (correlate logs, metrics, traces)

### Proposed Solution
- **Agent**: OpenTelemetry collector on each node
- **Metrics**: Prometheus + Thanos (long-term)
- **Logs**: Fluentd → Elasticsearch cluster
- **Traces**: OpenTelemetry collector → Jaeger
- **Visualization**: Grafana (metrics), Kibana (logs), Jaeger UI (traces)
- **Alerting**: Prometheus Alertmanager → PagerDuty + Slack
- **Storage**: 30-day retention for metrics, 7-day for traces, 14-day for logs
- **Sampling**: 5% head-based, 100% of errors
