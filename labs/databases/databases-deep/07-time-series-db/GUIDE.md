# Time-Series Database — Deep Dive Guide

## Data Model

Two common models:
- **InfluxDB / Prometheus**: `metric_name{tag1=val1, ...} value timestamp`
- **TimescaleDB**: SQL table partitioned by time with hypertables

## Compression (Facebook Gorilla Paper)

**Timestamp compression — delta-of-delta:**
- Store first timestamp as reference
- Store delta of subsequent timestamps
- If delta-of-delta == 0: store 1 bit ('0')
- If small (within range): store 2-bit header + value
- Else: store full delta

**Value compression — XOR:**
- XOR current value with previous
- If XOR == 0: store 1 bit ('0')
- Otherwise: store meaningful bits

## Downsampling / Rollups

Materialized views that aggregate raw data into windows (1m → 5m → 1h → 1d).

```sql
CREATE MATERIALIZED VIEW metrics_1h
AS SELECT metric, time_bucket('1 hour', ts) AS bucket, AVG(val)
FROM metrics_raw GROUP BY metric, bucket;
```

## Retention

Drop old partitions rather than DELETE (DDL is cheaper):

```sql
DROP TABLE metrics_2023;
```

## Partitioning by Time

TimescaleDB hypertables automatically create chunks (partition by time + optional space).

InfluxDB shards data into shard durations (e.g., 7-day shards).