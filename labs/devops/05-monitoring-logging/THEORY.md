# Monitoring & Logging Theory

## Three Pillars of Observability
1. **Metrics**: Numerical measurements over time (CPU, memory, request rate, latency).
2. **Logs**: Timestamped, immutable records of discrete events (errors, access logs).
3. **Traces**: End-to-end request tracking across distributed services (request path, spans).

## Prometheus Concepts
- **Metric types**: Counter (cumulative), Gauge (point-in-time), Histogram (bucketed), Summary (quantiles).
- **Pull model**: Prometheus scrapes metrics from targets at configured intervals.
- **PromQL**: Query language for selecting and aggregating time-series data.
- **Alertmanager**: Handles alerts (deduplication, grouping, routing, silence).

## Grafana
- **Dashboard**: Visual interface for metric exploration and monitoring.
- **Data source**: Prometheus, Elasticsearch, InfluxDB, CloudWatch, etc.
- **Panels**: Graph, Stat, Table, Heatmap, Gauge — configurable with queries.
- **Alerting**: Centralized alerting with notification channels (Slack, email, PagerDuty).

## ELK Stack
- **Elasticsearch**: Distributed search and analytics engine (stores logs).
- **Logstash**: Server-side data processing pipeline (ingest, transform, send).
- **Kibana**: Visualization UI for Elasticsearch data.
- **Beats**: Lightweight shippers (Filebeat for logs, Metricbeat for metrics).

## Alerting Concepts
- **Alert rule**: Condition that triggers an alert (e.g., CPU > 90% for 5 min).
- **Notification**: Delivery mechanism (Slack, email, PagerDuty, webhook).
- **Silence**: Suppress notifications for known issues or maintenance windows.
