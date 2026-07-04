# Observability - MATH FOUNDATION

## Metrics Aggregation

### Percentile Calculation
For latency measurement, percentiles provide more insight than averages.

P50 (median): 50% of requests are faster than this
P95: 95% of requests are faster than this (user-facing SLA target)
P99: 99% of requests are faster than this (infrastructure concern)
P999: 99.9% of requests (outliers, worth investigating)

Example:
```
Requests: [10ms, 12ms, 15ms, 20ms, 50ms, 100ms, 200ms, 500ms, 1000ms, 2000ms]
Average: 391ms (misleading — 90% are under 1s)
P50: 35ms
P95: 1500ms (most users have a good experience)
P99: 1950ms
```

## Error Budget

### SLA-based Error Budget
```
Error Budget = 1 - SLA_target
Monthly error budget for 99.9% SLA:
  Budget = (1 - 0.999) × 30 × 24 × 3600 = 2592 seconds ≈ 43 minutes
```

### Burn Rate
```
Error budget consumed per hour:
  burn_rate = error_rate / (1 - SLA_target)

If error_rate = 0.5% and SLA_target = 99.9%:
  burn_rate = 0.005 / 0.001 = 5 errors per allowed error
  Budget consumed in: 30 days / 5 = 6 days
  (Must fix within 6 days or risk missing SLA)
```

## Sampling Strategies

### Trace Sampling
```
Head-based sampling: Decide at request start (1/100 requests traced)
Tail-based sampling: Record all, decide later based on criteria
  - Always sample errors
  - Sample slow requests (P99+)
  - Sample by priority (high-value users)
```

### Storage Requirements
```
traces_per_day = requests_per_second × 86400 × sampling_rate
storage_per_day = traces_per_day × average_trace_size

Example:
  1000 RPS, 1% sampling rate, 2KB per trace:
  traces_per_day = 1000 × 86400 × 0.01 = 864,000
  storage_per_day = 864000 × 2KB = 1.7GB/day
```

## Cardinality

### Metric Cardinality Impact
```
unique_timeseries = number_of_metric_names × number_of_label_combinations

With 5 metric names and 3 labels, each with 10 values:
  5 × (10 × 10 × 10) = 5,000 time series

With 10 metric names and 5 labels, each with 100 values:
  10 × (100^5) = 10 × 10^10 = 100 billion (explosion!)
```

High cardinality (user IDs, session IDs in labels) kills Prometheus performance.

## Retention vs Storage

```
storage = ingestion_rate × retention × replication

1000 metrics/sec, 30 days retention, 2x replication:
  1000 × 86400 × 30 × 2 = 5.18 billion data points
  At 2 bytes per point: ~10GB
  With indexes: ~20-30GB
```
