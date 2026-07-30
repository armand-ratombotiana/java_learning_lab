# 07 - Time-Series Database

## Topics Covered
- Time-series data modeling (metric + timestamp + tags/fields)
- Retention policies and data lifecycle
- Downsampling (rollups, pre-aggregation)
- Compression (delta-of-delta, XOR, run-length, dictionary)
- Partitioning by time (time-bucketed partitions)
- Chunk-based storage (InfluxDB TSM, TimescaleDB hypertables)

## Goal
Understand the specialized storage and query techniques for time-series workloads.

## Exercises

1. Design a time-series schema for CPU/memory metrics from 10K hosts.
2. Implement delta-of-delta + XOR compression (inspired by Facebook Gorilla paper).
3. Create time-bucketed partitions and show partition pruning.
4. Implement a retention policy and downsampling job.