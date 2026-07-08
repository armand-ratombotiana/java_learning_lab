# Performance Optimization for Streaming Analytics

## Stream Processing
Match parallelism to Kafka partition count. Use RocksDB state backend for large state. Tune checkpoint interval (30-60s for streaming).

## Materialized Views
Use UPSERT sinks to minimize write operations. Batch writes to sink (buffer + flush). Partition materialized views by time for efficient pruning.

## Dashboards
Pre-aggregate at stream level (dashboard never reads raw data). Cache dashboard queries (5-30s TTL). Use push-based WebSocket for live updates.

## Alerting
Evaluate alert conditions in stream processor (not dashboard). Deduplicate alerts. Escalate based on severity and duration.
