# Lab 15 — Disaster Recovery Failover: SLI/SLO/SLA

## Service: Global Application (Multi-Region)

### SLIs

| Indicator | Definition | Target |
|-----------|-----------|--------|
| Failover time | Incident start to full traffic restoration | < 5 minutes |
| Failover success rate | Successful failovers / total attempts | > 99% |
| Data loss per failover | Lost transactions per event | 0 |
| Replication lag | Cross-region data latency | < 5 seconds |
| Health check accuracy | False positive health failures | < 0.1% |

### SLOs

| SLI | Target | Error Budget |
|-----|--------|-------------|
| Failover RTO < 5 min | 99.99% | ~4.38 min/month |
| Zero data loss on failover | 99.999% | ~0.44 min/month |
| Cross-region lag < 5s | 99.9% | ~43.8 min/month |

### Alerting

```yaml
groups:
  - name: dr_alerts
    rules:
      - alert: RegionUnhealthy
        expr: region_health_status{status="unhealthy"} == 1
        for: 1m
        annotations:
          summary: "Region {{ $labels.region }} health check failed"

      - alert: ReplicationLagHigh
        expr: region_replication_lag_seconds > 5
        for: 5m
        annotations:
          summary: "Cross-region replication lag > 5 seconds"

      - alert: FailoverRequired
        expr: region_health_status{region="primary"} == 0
        for: 5m
        annotations:
          summary: "Primary region unhealthy for 5 minutes — initiate failover"
```
