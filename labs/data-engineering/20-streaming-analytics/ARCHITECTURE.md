# Streaming Analytics Architecture

```
[Event Sources]
    |
[Kafka / Kinesis: Ingestion]
    |
[Flink SQL / Kinesis Analytics: Processing]
    |
[Materialized Views (PostgreSQL/ClickHouse)]
    |
[REST API / Grafana]
    |
[Users: Dashboards, Alerts, Applications]

Typical Query:
INSERT INTO materialized_view
SELECT window_start, metric, COUNT(*), AVG(value)
FROM stream
GROUP BY TUMBLE(event_ts, INTERVAL '1' MINUTE), metric
```
