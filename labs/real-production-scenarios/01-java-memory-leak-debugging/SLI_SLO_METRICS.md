# Lab 01 — Java Memory Leak: SLI/SLO/SLA Definitions

## Service: Zuul API Gateway

### Service Overview

The Zuul API Gateway is the entry point for all API traffic, routing requests to downstream microservices. It performs authentication, rate limiting, request/response transformation, and routing.

### Service Level Indicators (SLIs)

#### Availability SLI

| Indicator | Definition | Measurement Method | Data Source |
|-----------|-----------|-------------------|-------------|
| Request success rate | Fraction of HTTP requests that complete with status < 500 | `count(status < 500) / count(all requests)` | Load balancer access logs, Zuul metrics |
| Gateway uptime | Fraction of 1-minute windows where gateway processes at least 1 request | `up_minutes / total_minutes` | Prometheus UP metric, ELB health checks |

#### Latency SLI

| Indicator | Definition | Threshold | Measurement |
|-----------|-----------|-----------|-------------|
| P50 latency | Median request processing time | < 50ms | Zuul metrics (Netflix Atlas / Prometheus) |
| P95 latency | 95th percentile request processing time | < 200ms | Same as above |
| P99 latency | 99th percentile request processing time | < 500ms | Same as above |

#### Performance SLI

| Indicator | Definition | Targets |
|-----------|-----------|---------|
| GC pause time | Time spent in GC pauses per minute | P99 < 100ms, max < 500ms |
| Metaspace utilization | Percentage of MaxMetaspaceSize used | < 70% (warning at 80%, critical at 90%) |
| Heap utilization | Percentage of -Xmx used | < 70% post-GC |
| Thread pool utilization | Active threads / max threads | < 80% |

#### Throughput SLI

| Indicator | Definition | Target |
|-----------|-----------|--------|
| Requests per second | Number of requests processed per second | 50,000 RPS per cluster |
| Connection pool utilization | Active connections / max connections | < 80% |

### Service Level Objectives (SLOs)

| SLI | SLO Target | Error Budget (monthly) | Measurement Window |
|-----|-----------|----------------------|-------------------|
| Request success rate | 99.95% | ~21.9 minutes of failed requests | Rolling 30 days |
| P99 latency < 500ms | 99% | ~7.3 hours of slow requests | Rolling 28 days |
| Gateway uptime | 99.99% | ~4.38 minutes of downtime | Rolling 30 days |
| Metaspace utilization < 80% | 99.9% | ~43.8 minutes of high Metaspace | Rolling 7 days |
| GC pause P99 < 100ms | 99% | ~7.3 hours of long GC pauses | Rolling 28 days |

### Service Level Agreements (SLAs)

| SLA Commitment | Target | Penalty |
|---------------|--------|---------|
| Internal API gateway SLA | 99.95% monthly uptime | Service credits to internal teams |
| Customer-facing SLA | 99.9% monthly uptime | 10% service credit per 1% below |
| Latency SLA | P99 < 1s for 99% of requests | Escalation to engineering leadership |

### Key Metrics Dashboard

| Metric | Current Value | Target | Alert Threshold |
|--------|--------------|--------|----------------|
| Metaspace Used | 150 MB | < 200 MB | > 180 MB (warning), > 200 MB (critical) |
| Metaspace Growth Rate | 2 MB/hour | < 1 MB/hour | > 5 MB/hour for 2 hours |
| ClassLoader Count | 847 | < 500 | > 800 (warning), > 1000 (critical) |
| Active Connections | 45 of 200 | < 160 | > 160 (warning), > 190 (critical) |
| P99 Latency | 45 ms | < 500 ms | > 300 ms (warning), > 500 ms (critical) |
| Error Rate | 0.02% | < 0.1% | > 0.1% (warning), > 1% (critical) |

### Burn Rate Alerting

| Burn Rate | Time to Exhaust Error Budget | Alert Severity | Example Scenario |
|-----------|------------------------------|----------------|------------------|
| 1x | 30 days | None | Normal operation |
| 2x | 15 days | Warning | Elevated Metaspace growth |
| 5x | 6 days | Page (SEV3) | ClassLoader leak detected |
| 10x | 3 days | Page (SEV2) | Rapid Metaspace growth |
| 20x | 1.5 days | Page (SEV1) | OOM imminent |

### Multi-Window Multi-Burn-Rate Alerting Strategy

| Window | Burn Rate | SLO | Alert Condition | Severity |
|--------|-----------|-----|-----------------|----------|
| 1 hour | 14.4x | 99.95% success | Error rate > 0.72% for 1 hour | SEV2 |
| 6 hours | 6x | 99.95% success | Error rate > 0.3% for 6 hours | SEV3 |
| 3 days | 2x | 99.95% success | Error rate > 0.1% for 3 days | SEV4 |
| 1 hour | 14.4x | Metaspace < 80% | Metaspace growth rate > 10x normal for 1 hour | SEV2 |
| 6 hours | 6x | Metaspace < 80% | Metaspace growth rate > 4x normal for 6 hours | SEV3 |

### Error Budget Policy

**Error Budget:** 100% - SLO = allowed downtime/errors per month

For 99.95% availability SLO:
- Monthly error budget: ~21.9 minutes
- Quarterly error budget: ~65.7 minutes
- Yearly error budget: ~4.38 hours

**Error Budget Decisions:**
- **Error budget available:** Can deploy new features, reduce monitoring, accept technical debt
- **Error budget exhausted (warning at 50%):** Review SLO attainment, slow deployments, increase testing
- **Error budget exhausted (critical at 100%):** Freeze deployments, focus on reliability improvements, mandatory post-mortem

**Metaspace-specific error budget:**
- SLO: Metaspace utilization < 80% for 99.9% of time
- If Metaspace exceeds 80% for > 43.8 minutes in a month, error budget is consumed
- Action: Check ClassLoader count, investigate leaks, schedule restart if needed

### Alerting Rules

```yaml
# Prometheus alerting rules for Metaspace leak detection

groups:
  - name: metaspace_alerts
    rules:
      - alert: MetaspaceHighUtilization
        expr: jvm_memory_used_bytes{area="nonheap",id="Metaspace"} / jvm_memory_max_bytes{area="nonheap",id="Metaspace"} > 0.8
        for: 5m
        annotations:
          summary: "Metaspace utilization > 80% on {{ $labels.instance }}"
          
      - alert: MetaspaceGrowthAnomaly
        expr: rate(jvm_memory_used_bytes{area="nonheap",id="Metaspace"}[1h]) > 50000000
        for: 2h
        annotations:
          summary: "Metaspace growing > 50MB/hour on {{ $labels.instance }}"
          
      - alert: ClassLoaderCountAnomaly
        expr: sum by(instance) (rate(jvm_classes_loaded_classes[5m])) > 0.1
        for: 1h
        annotations:
          summary: "Class loading rate > 0.1/s indicates potential ClassLoader leak"
          
      - alert: NoClassUnloading
        expr: delta(jvm_gc_class_unloading_count{action="end of major GC"}[1h]) == 0
        for: 24h
        annotations:
          summary: "No class unloading in 24 hours on {{ $labels.instance }}"
```

### Post-Incident SLO Adjustment

After the Metaspace OOM incident, SLOs should be reviewed:
1. Did we meet our SLOs during the incident period? (No — 3-5 minutes of downtime per 6-hour window = ~2% unavailability vs. 99.99% SLO target)
2. Should we adjust SLOs based on actual performance? Adjust targets to account for known limitations until fix is deployed
3. What new SLIs should we add? Metaspace growth rate, ClassLoader count, class unloading rate
