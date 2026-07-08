# Debugging Streaming Analytics

## Dashboard Not Updating
Check Kafka consumer lag; verify Flink job is running; check sink database for new rows

## Wrong Aggregations
Verify watermark progress; check allowed lateness; verify event timestamps

## High Latency
Check backpressure in Flink web UI; increase parallelism; optimize sink throughput

## Data Loss
Check checkpoint failures; verify at-least-once or exactly-once semantics; check allowed lateness
