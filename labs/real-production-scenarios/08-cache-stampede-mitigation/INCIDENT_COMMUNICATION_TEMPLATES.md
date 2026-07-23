# Lab 08 — Cache Stampede: Communication Templates

## Initial Alert

```
Title: [SEV1] Cache Stampede — Product Catalog Database Overloaded
Service: Product Catalog
Severity: SEV1

Metrics:
- Cache hit ratio: 20% (normal: 95%)
- DB queries/sec: 50,000 (normal: 2,000)
- DB CPU: 100%
- P99 latency: 10s (normal: 50ms)

Impact: All product catalog requests failing or timing out
Cause: Popular product cache key expired, 500 app instances hit DB simultaneously
```

## Status Updates

```
STATUS #1 — Cache Stampede Investigation

500 concurrent requests for same cache key triggered mass DB hit.
Cache was serving stale data but switch to fresh-miss caused stampede.
Mitigation: Serving stale cache data (stale-while-revalidate).
DB load returning to normal (20K → 5K queries/sec).
```

```
STATUS #2 — Resolved

Stale cache serving restored normal latency.
DB load back to 2,000 queries/sec.
Probabilistic early expiration and lock-based regeneration deployed.
Post-mortem: cache TTL was set to 1 hour with no jitter.
Added: TTL jitter, stale-while-revalidate, distributed lock on regeneration.
```
