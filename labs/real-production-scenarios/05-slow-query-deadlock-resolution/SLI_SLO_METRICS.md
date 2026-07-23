# Lab 05 — Slow Query / Deadlock: SLI/SLO/SLA Definitions

## Service: Financial Batch Processing (Oracle DB)

### SLIs

| Indicator | Definition | Target |
|-----------|-----------|--------|
| Batch job duration | END_OF_DAY_SETTLEMENT completion time | < 60 minutes |
| Query elapsed time | P99 of individual query duration | < 10 seconds |
| Disk reads per query | Physical reads per execution | < 100K per batch |
| Temp space per query | Temp tablespace used per execution | < 1GB |
| Full table scans | FTS on tables > 1M rows | 0 |
| Deadlock rate | Database deadlocks per day | 0 |

### SLOs

| SLI | Target | Error Budget (monthly) |
|-----|--------|----------------------|
| Batch job < 1 hour | 99% | ~7.3 hours |
| Zero full table scans on large tables | 99.9% | ~43.8 minutes |
| Zero deadlocks | 100% | 0 tolerance |
| Query P99 < 10s | 99% | ~7.3 hours |

### Key Metrics

| Metric | Warning | Critical |
|--------|---------|----------|
| Batch duration | > 75 min | > 90 min |
| Query elapsed P99 | > 5s | > 10s |
| Disk reads (per batch) | > 500K | > 1M |
| Temp space per query | > 500MB | > 1GB |
| Optimizer stats age | > 7 days | > 14 days |
| Plan changes detected | Any | > 2 in 30 days |

### Alerting

```yaml
groups:
  - name: batch_alerts
    rules:
      - alert: BatchJobDuration
        expr: batch_job_duration_seconds{job="END_OF_DAY_SETTLEMENT"} > 5400
        annotations:
          summary: "Batch job exceeding 90-minute threshold"

      - alert: FullTableScanDetected
        expr: rate(oracle_physical_reads_total[5m]) > 100000
        for: 10m
        annotations:
          summary: "High physical reads — possible full table scan"

      - alert: DatabaseDeadlock
        expr: rate(oracle_deadlock_count[5m]) > 0
        annotations:
          summary: "Database deadlock detected"

      - alert: StaleOptimizerStats
        expr: oracle_table_stats_age_days > 7
        annotations:
          summary: "Optimizer statistics stale for {{ $labels.table }}"
```
