# Lab 02 — Thread Deadlock Analysis: SLI/SLO/SLA Definitions

## Service: Distributed Lock Coordinator

### Service Overview

The Distributed Lock Coordinator manages lease coordination across 200+ nodes in a global deployment, providing distributed locking for critical resources.

### Service Level Indicators (SLIs)

#### Availability SLI

| Indicator | Definition | Target |
|-----------|-----------|--------|
| Request success rate | Lease acquisition success / total attempts | > 99.9% |
| Service availability | Up minutes / total minutes | > 99.99% |

#### Latency SLI

| Request Type | P50 Target | P95 Target | P99 Target |
|-------------|-----------|-----------|-----------|
| Lease acquisition | < 5ms | < 20ms | < 50ms |
| Lease renewal | < 2ms | < 10ms | < 30ms |
| Lease release | < 1ms | < 5ms | < 15ms |

#### Contention SLI

| Metric | Definition | Warning | Critical |
|--------|-----------|---------|----------|
| Lock contention rate | Threads blocked / total active threads | > 10% | > 30% |
| Thread pool utilization | Active threads / max threads | > 70% | > 90% |
| Deadlock detection rate | Deadlocks detected per hour | > 0 | > 1 |
| Lock acquisition timeout | tryLock failures per minute | > 10 | > 100 |

### Service Level Objectives (SLOs)

| SLI | SLO Target | Error Budget (monthly) |
|-----|-----------|----------------------|
| Request success rate | 99.95% | ~21.9 minutes |
| P99 lease acquisition latency < 50ms | 99% | ~7.3 hours |
| Deadlock-free operation | 100% | 0 tolerance |
| Thread pool utilization < 80% | 99.9% | ~43.8 minutes |

### Error Budget Policy

- Deadlocks automatically consume the entire error budget for the month
- Any deadlock detection triggers an immediate incident review
- Three deadlock events in rolling 30 days triggers mandatory architecture review

### Alerting Rules

```yaml
groups:
  - name: deadlock_alerts
    rules:
      - alert: JavaDeadlockDetected
        expr: jvm_thread_deadlock_count > 0
        for: 0m
        annotations:
          summary: "Java deadlock detected on {{ $labels.instance }}"

      - alert: HighLockContention
        expr: rate(jvm_thread_blocked_count[1m]) > 100
        for: 5m
        annotations:
          summary: "High lock contention on {{ $labels.instance }}"

      - alert: ThreadPoolExhausted
        expr: jvm_thread_pool_active > jvm_thread_pool_max * 0.8
        for: 2m
        annotations:
          summary: "Thread pool > 80% utilized on {{ $labels.instance }}"
```
