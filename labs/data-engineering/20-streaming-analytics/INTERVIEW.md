# Interview Questions: Streaming Analytics

### Architecture
**Q**: How would you design a real-time analytics platform?
**A**: Kafka for ingestion -> Flink SQL for processing -> Materialized views in PostgreSQL/ClickHouse -> Grafana for dashboards. Alerting via threshold-based or anomaly detection on streaming aggregates.

### Flink SQL
**Q**: What Flink SQL features are important for streaming analytics?
**A**: Windowed aggregations (TUMBLE, HOP, SESSION), WATERMARK for event time, upsert/sink to materialized views, CDC source connectors for database streams.

### Lambda vs Kappa
**Q**: Lambda vs Kappa architecture for streaming analytics?
**A**: Lambda: batch + stream (reconciled). Kappa: streaming only (simpler). Lambda is redundant if streaming can provide sufficient accuracy. Kappa is preferred for modern streaming analytics.

### Dashboard Design
**Q**: How do you ensure real-time dashboards stay performant?
**A**: Pre-aggregate in stream processor (not raw events to dashboard). Use materialized views. Implement TTL on data freshness. Push updates instead of poll. Cache dashboard queries.
