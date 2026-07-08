# Performance Optimization for Data Observability

## Profiling Performance
Sample for large tables (0.1-1%). Approximate counts (HyperLogLog). Incremental profiling (only changed partitions).

## Anomaly Detection
Use window functions for historical computation. Pre-compute baseline statistics. Cache profiles for non-changing data.

## Lineage Processing
Batch lineage queries for efficiency. Incremental graph updates. Index lineage queries for impact analysis.

## Alert Throttling
Deduplicate alerts (same table, same check, consecutive failures). Cooldown period between alerts. Escalate only for persistent or high-severity issues.
