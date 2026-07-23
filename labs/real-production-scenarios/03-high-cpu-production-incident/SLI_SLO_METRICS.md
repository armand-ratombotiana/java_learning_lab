# Lab 03 — High CPU / ReDoS: SLI/SLO/SLA Definitions

## Service: Content Moderation Regex Engine

### Service Level Indicators

**Latency SLIs:**
- P50 regex evaluation time: < 2ms
- P99 regex evaluation time: < 50ms
- Max regex evaluation time: < 100ms (with timeout)

**CPU SLIs:**
- Per-node CPU utilization: < 60% average, < 80% peak
- Per-thread CPU: < 50% average
- Thread pool utilization: < 70% active threads

**Error Rate SLIs:**
- Regex evaluation failures: < 0.1%
- Regex timeout rate: < 0.01%
- Pattern compilation failures: < 0.001%

**Throughput SLIs:**
- Requests processed per second: 50,000 RPM per node
- Queue depth: < 100 at peak

### SLOs

| SLI | Target | Error Budget (monthly) |
|-----|--------|----------------------|
| P99 regex eval < 50ms | 99% | ~7.3 hours |
| CPU < 80% per node | 99.9% | ~43.8 minutes |
| Regex timeout rate < 0.01% | 99.99% | ~4.38 minutes |
| Error rate < 0.1% | 99.9% | ~43.8 minutes |

### Burn Rate Alerting

| Burn Rate | Time to Exhaust | Alert | Example |
|-----------|----------------|-------|---------|
| 1x | 30 days | None | Normal CPU |
| 5x | 6 days | Warning | Elevated eval time |
| 10x | 3 days | Page | ReDoS attack detected |
| 20x | 1.5 days | Critical page | Widespread CPU storm |

### Alerting Rules

```yaml
groups:
  - name: redos_alerts
    rules:
      - alert: HighRegexEvaluationTime
        expr: histogram_quantile(0.99, rate(regex_eval_seconds_bucket[5m])) > 0.05
        for: 1m
        annotations:
          summary: "P99 regex evaluation > 50ms on {{ $labels.instance }}"

      - alert: HighCPUPerThread
        expr: rate(process_cpu_seconds_total[1m]) / num_cpus > 0.8
        for: 2m
        annotations:
          summary: "CPU > 80% on {{ $labels.instance }}"

      - alert: RegexTimeoutRate
        expr: rate(regex_timeout_total[5m]) > 0.1
        for: 1m
        annotations:
          summary: "Regex timeout rate elevated — possible ReDoS attack"
```

### Key Metrics Dashboard

| Metric | Warning | Critical |
|--------|---------|----------|
| CPU per node | > 70% | > 90% |
| Regex P99 eval time | > 30ms | > 50ms |
| Pattern timeout rate | > 0.01/s | > 0.1/s |
| Thread pool active | > 70% | > 90% |
| Input length P99 | > 512 chars | > 1024 chars |
