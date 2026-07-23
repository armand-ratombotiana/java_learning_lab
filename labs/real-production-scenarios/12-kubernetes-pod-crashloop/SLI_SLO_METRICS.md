# Lab 12 — Kubernetes Pod CrashLoop: SLI/SLO/SLA

## Service: Container Orchestration (Kubernetes)

### SLIs

| Indicator | Definition | Target |
|-----------|-----------|--------|
| Pod restart rate | Restarts per pod per hour | < 1 |
| CrashLoopBackOff count | Pods in CrashLoopBackOff | 0 |
| Pod startup time | Time from pod creation to Ready | < 30 seconds |
| Pod availability | Ready replicas / desired replicas | > 99.9% |
| Node pressure | Nodes with pressure conditions | 0 |

### SLOs

| SLI | Target | Error Budget |
|-----|--------|-------------|
| Zero CrashLoopBackOff pods | 99.9% | ~43.8 min/month |
| Pod availability > 99.9% | 99.99% | ~4.38 min/month |
| Zero OOM-killed pods | 99.9% | ~43.8 min/month |

### Alerting

```yaml
groups:
  - name: pod_alerts
    rules:
      - alert: PodCrashLoop
        expr: kube_pod_status_phase{phase="CrashLoopBackOff"} > 0
        for: 1m
        annotations:
          summary: "Pod {{ $labels.pod }} in CrashLoopBackOff"

      - alert: HighRestartRate
        expr: rate(kube_pod_container_status_restarts_total[1h]) > 10
        for: 5m
        annotations:
          summary: "High restart rate on {{ $labels.pod }}"

      - alert: OOMKilled
        expr: kube_pod_container_status_last_terminated_reason{reason="OOMKilled"} > 0
        annotations:
          summary: "Pod {{ $labels.pod }} OOM killed"
```
