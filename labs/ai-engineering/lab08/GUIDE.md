# AI Observability — Deep Dive Guide

## Observability Pillars

For AI systems, observability extends beyond traditional metrics:

| Pillar | What It Measures | Why It Matters |
|--------|-----------------|----------------|
| Token Usage | Input/output tokens per request | Cost attribution |
| Latency | Response time distribution | User experience |
| Cost | Per-model, per-user, per-feature | Budget management |
| Drift | Distribution shifts in data | Model quality |
| Quality | Accuracy, relevance, safety | Business outcomes |

## Code Walkthrough: MetricsCollector

The `MetricsCollector` stores timestamped metrics in a `CopyOnWriteArrayList` for thread-safe concurrent access:

- `record(name, value)`: Appends a new data point
- `getByPrefix(prefix)`: Filters metrics by name prefix
- `average(name)`: Computes the mean for a specific metric
- Useful for aggregation queries (e.g., average latency per operation)

## Token Tracking and Cost Attribution

The `TokenTracker` tracks tokens per model:

- `AtomicLong` counters for input/output totals (lock-free, thread-safe)
- Records per-request metrics into the collector
- `CostCalculator` maps model names to per-1K-token prices
- Cost = (total_tokens / 1000) * price_per_1K

## Latency Monitoring

The `LatencyMonitor` wraps any operation with timing:

- Generic `measure(operation, task)` method
- Captures wall-clock time using `System.nanoTime()`
- Records latency in milliseconds to the collector
- Reports average latency per operation name

## Drift Detection

### KL Divergence

KL divergence measures how one probability distribution diverges from another:

```
KL(P || Q) = Σ P(i) * log(P(i) / Q(i))
```

- P = reference distribution (training data)
- Q = current distribution (production data)
- Higher values indicate more drift

### Population Stability Index (PSI)

PSI is a symmetric variant of KL divergence commonly used in finance:

```
PSI = Σ (P(i) - Q(i)) * ln(P(i) / Q(i))
```

## Production Considerations

- Export metrics to time-series databases (Prometheus, InfluxDB)
- Set up dashboards and alerts for anomaly detection
- Use distributed tracing to correlate requests across pipeline stages
- Sample metrics at high granularity, aggregate for storage
- Implement token budgets per user to prevent cost spikes