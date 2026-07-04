# Observability - PERFORMANCE

## Overhead of Observability

### Metrics Overhead
| Metric Type | Collection Overhead | Storage Per Day (1 instance) |
|------------|-------------------|------------------------------|
| JVM metrics | < 0.1% CPU | ~1 MB |
| Custom counters | < 0.05% CPU | ~0.5 MB |
| Request metrics | < 0.1% CPU | ~5 MB |
| Timer/Summary | < 0.2% CPU | ~10 MB |

### Logging Overhead
| Logging Strategy | CPU Impact | Disk I/O |
|-----------------|-----------|----------|
| String concat + console | 1-2% | Minimal |
| Structured JSON + async | 2-3% | Low |
| Debug level (verbose) | 5-10% | Can be high |
| Async appender | 0.5% | Buffered writes |

### Tracing Overhead
| Sampling Rate | CPU Overhead | Network | Storage |
|--------------|-------------|---------|---------|
| 0% (disabled) | 0% | 0 | 0 |
| 1% (low volume) | 0.5-1% | Low | ~200 MB/day |
| 10% (medium) | 2-3% | Moderate | ~2 GB/day |
| 100% (high volume) | 5-10% | High | ~20 GB/day |

## Optimization Tips

### Sampling Strategies
```yaml
# Head-based consistent probability sampling
otel.traces.sampler: parentbased_traceidratio
otel.traces.sampler.arg: 0.05  # 5% sample

# Tail-based sampling (collect all, decide later)
# Use OpenTelemetry Collector with tail sampling processor
```

### Metric Aggregation
```yaml
# Reduce metric cardinality
management.metrics.export.prometheus:
  step: 30s  # scrape interval
  descriptions: false  # skip descriptions for performance
```

### Log Level Management
```yaml
# Production: INFO level
logging.level.com.example: INFO

# Debug a specific service at runtime
# POST /actuator/loggers/com.example.service.OrderService
# {"configuredLevel": "DEBUG"}
```

### Async Logging
```xml
<!-- Use async appender to avoid blocking -->
<appender name="ASYNC" class="ch.qos.logback.classic.AsyncAppender">
    <neverBlock>true</neverBlock>
    <queueSize>1024</queueSize>
    <appender-ref ref="JSON"/>
</appender>
```

## Resources for Observability Stack

| Component | Memory (per instance) | CPU |
|-----------|---------------------|-----|
| Prometheus | 2-8 GB | 1-2 cores |
| Grafana | 500 MB - 2 GB | 0.5-1 core |
| Jaeger | 1-4 GB | 1-2 cores |
| Elasticsearch | 4-16 GB | 2-8 cores |
| Loki | 1-4 GB | 1-2 cores |
