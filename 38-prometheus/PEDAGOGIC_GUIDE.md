# Pedagogic Guide - Prometheus

## Learning Path

### Phase 1: Metrics Fundamentals
1. Metric types and use cases
2. Pull vs push architecture
3. Exposition format
4. Labels and dimensions

### Phase 2: Instrumentation
1. Micrometer setup with Spring Boot
2. Custom metrics with MeterRegistry
3. Aspect-oriented metrics
4. Timer and counter utilities

### Phase 3: PromQL
1. Basic queries and filtering
2. Aggregation operators
3. Functions (rate, increase, histogram_quantile)
4. Subqueries and recording rules

### Phase 4: Alerting
1. Alert rule syntax
2. Alertmanager configuration
3. Alert grouping and routing
4. Silence and inhibition

### Phase 5: Operations
1. Scrape configuration
2. Service discovery
3. Storage and retention
4. High availability setup

## Metric Types

| Type | Description | Use Cases |
|------|-------------|-----------|
| Counter | Only increases | Requests, errors |
| Gauge | Can vary | Connections, temp |
| Histogram | Buckets | Latency, size |
| Summary | Quantiles | Response time |

## Interview Topics

| Topic | Question |
|-------|----------|
| Pull vs Push | Why does Prometheus use pull? |
| Cardinality | How to avoid high cardinality labels? |
| Storage | How does Prometheus store data? |
| Alerting | How to reduce alert noise? |
| Scale | How to scale Prometheus? |

## Common Functions

| Function | Purpose |
|----------|---------|
| `rate()` | Per-second rate |
| `increase()` | Total increase |
| `histogram_quantile()` | Percentile calculation |
| `sum()` | Aggregation |
| `topk()` | Top N results |

## Next Steps
- Explore Prometheus federation for scaling
- Learn about Thanos for long-term storage
- Study alert routing with Alertmanager