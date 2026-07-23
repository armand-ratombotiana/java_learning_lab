# Lab 08 — Cache Stampede: SLI/SLO/SLA Definitions

## Service: Product Catalog (Cache-heavy)

### SLIs

| Indicator | Definition | Target |
|-----------|-----------|--------|
| Cache hit ratio | Cache hits / total requests | > 90% |
| Database load from cache misses | Queries due to miss / total | < 10% |
| Stale data served | Stale responses / total | < 1% |
| Cache regeneration latency | Time to regenerate cache entry | < 500ms |
| Concurrent regenerations | Requests regenerating same key | = 1 |

### SLOs

| SLI | Target | Error Budget |
|-----|--------|-------------|
| Cache hit ratio > 90% | 99% | ~7.3 hours/month |
| Zero stampede events | 99.99% | ~4.38 min/month |
| Regeneration latency < 500ms | 99% | ~7.3 hours/month |

### Alerting

```yaml
groups:
  - name: cache_alerts
    rules:
      - alert: CacheHitRatioDrop
        expr: rate(cache_hits_total[5m]) / rate(cache_requests_total[5m]) < 0.8
        for: 2m
        annotations:
          summary: "Cache hit ratio dropped below 80%"

      - alert: ConcurrentRegenerations
        expr: rate(cache_regenerations_total[5m]) > 1
        for: 1m
        annotations:
          summary: "Multiple concurrent regenerations — potential stampede!"

      - alert: DatabaseQuerySurge
        expr: rate(db_queries_from_miss_total[5m]) > rate(db_queries_from_miss_total[30m]) * 2
        annotations:
          summary: "Database query surge from cache misses"
```
