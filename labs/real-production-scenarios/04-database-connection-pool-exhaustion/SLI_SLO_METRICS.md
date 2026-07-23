# Lab 04 — Connection Pool Exhaustion: SLI/SLO/SLA Definitions

## Service: Order Management System (RDS-backed)

### SLIs

| Indicator | Definition | Target |
|-----------|-----------|--------|
| Connection acquisition success | Connections acquired / requested | > 99.99% |
| Connection acquisition latency | P99 time to get connection | < 10ms |
| Active connections | % of maxPoolSize used | < 80% |
| Pending threads | Threads waiting for connection | Always 0 |
| Connection timeout count | Timeouts per minute | 0 |

### SLOs

| SLI | Target | Error Budget |
|-----|--------|-------------|
| Connection acquisition success | 99.99% | ~4.38 min/month |
| P99 acquire latency < 10ms | 99% | ~7.3 hours/month |
| Zero connection timeouts | 100% | 0 tolerance |
| Pool utilization < 80% | 99.9% | ~43.8 min/month |

### Burn Rate Alerts

```yaml
groups:
  - name: connection_pool
    rules:
      - alert: PoolExhaustion
        expr: hikaricp_connections_active / hikaricp_connections_max > 0.9
        for: 1m
        annotations:
          summary: "Connection pool > 90% utilized"

      - alert: ConnectionTimeouts
        expr: rate(hikaricp_connections_timeout_total[5m]) > 0
        for: 1m
        annotations:
          summary: "Connection timeout detected"

      - alert: PendingThreads
        expr: hikaricp_connections_pending > 0
        for: 30s
        annotations:
          summary: "Threads waiting for connection"
```

### Key Metrics

| Metric | Warning | Critical |
|--------|---------|----------|
| Active/Total | > 70% | > 90% |
| Pending threads | > 0 | > 10 |
| Timeout rate | > 0/min | > 5/min |
| Acquire time P99 | > 5ms | > 10ms |
| Leak alerts | Any | > 3/hour |
