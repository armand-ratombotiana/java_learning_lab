# System Design Cheatsheet — Statistics Edition

Designing data-intensive statistical systems.

## Architecture Patterns

### Real-time Statistics Pipeline
```
Events → Message Queue → Stream Processor (windowed stats) → Time-Series DB → Dashboard
```
Key: Use online algorithms (Welford's variance, t-digest for quantiles)

### Batch Analytics Platform
```
Raw Data → ETL → Data Warehouse → Statistical Engine → Reports
```
Key: Use parallel map-reduce for independent statistical computations

### A/B Testing Platform
```
Traffic Splitter → Experiment Service → Metrics Collector → Statistical Analyzer → Decision Engine
```
Key: Sequential testing with alpha spending functions

## Database Choices

| Use Case | Database | Why |
|----------|----------|-----|
| Metrics/time-series | InfluxDB, TimescaleDB | Built-in window functions, downsampling |
| Experiment data | PostgreSQL | Strong consistency, window functions |
| Feature store | Redis + S3 | Low-latency feature retrieval |
| Large-scale stats | Spark MLLib | Distributed statistical computation |

## Key Design Decisions

1. **Online vs offline computation** — Online (streaming) for real-time dashboards, offline for precise reports
2. **Approximate vs exact** — t-digest, HyperLogLog, Count-Min Sketch for large scale
3. **Idempotency** — Statistical computations must be idempotent for retry safety
4. **Monitoring** — Track p50/p95/p99 latencies, error rates, data freshness

## Scaling Statistics

| Statistic | Scaling Strategy |
|-----------|------------------|
| Mean/Sum | Partition, aggregate, combine |
| Variance | Use parallel formula: Σx² - (Σx)²/n |
| Median | t-digest, Greenwald-Khanna |
| Correlation | Partition data, compute moments, merge |
| Regression | SGD for large datasets, closed-form for small |
