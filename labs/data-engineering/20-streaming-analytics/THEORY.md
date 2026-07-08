# Streaming Analytics Theory

## Streaming Analytics Architecture
Ingestion layer: Kafka, Kinesis, Event Hub (buffers streams). Processing layer: Flink, Kafka Streams, Kinesis Analytics (computes aggregations). Storage layer: ClickHouse, PostgreSQL, Druid (materialized views for fast queries). Visualization layer: Grafana, Superset, custom dashboards.

## Flink SQL for Analytics
Window functions: TUMBLE (fixed non-overlapping), HOP (fixed overlapping), SESSION (activity-based). Watermarks: define event-time completeness. Aggregations: COUNT, SUM, AVG, MIN, MAX, DISTINCT. Joins: stream-stream, stream-table (lookup), stream-table (temporal). Upsert/CDC: maintain materialized views.

## Materialized Views
Pre-computed query results that update incrementally. Approach: Flink SQL writes upserts to sink table (PostgreSQL, ClickHouse). Incremental: only changed rows are re-computed. Benefits: dashboard queries are fast (read from pre-computed store). Trade-off: slight staleness (materialization latency).

## Real-Time Dashboard Patterns
Push-based: server pushes updates to browser via WebSocket (live updates). Pull-based: dashboard polls fresh data on interval (every 5s). Hybrid: initial load from materialized view + incremental push for changes. Aggregation: pre-aggregate in stream processor to reduce dashboard query load.
