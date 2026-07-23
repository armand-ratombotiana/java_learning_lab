# Lab 06 — Production Deployment Rollback: SLI/SLO/SLA Definitions

## Service: Deployment Pipeline

### Deployment SLIs

| Indicator | Definition | Target |
|-----------|-----------|--------|
| Deployment success rate | Successful deployments / total | > 99% |
| Rollback rate | Rollbacks / total deployments | < 5% |
| Deployment duration | Time from commit to production | < 30 minutes |
| Rollback speed | Time from decision to completion | < 5 minutes |
| Change failure rate | Deployments causing incidents / total | < 15% |
| Mean time to recover | Time from incident to rollback to normal | < 10 minutes |

### SLOs

| SLI | Target | Error Budget (monthly) |
|-----|--------|----------------------|
| Deployment success rate | 99% | ~7.3 hours |
| Rollback completion < 5 min | 99.9% | ~43.8 minutes |
| MTTR < 10 min | 99% | ~7.3 hours |
| Zero failed deployments per week | 95% | ~36.5 hours |

### Alerting

```yaml
groups:
  - name: deployment_alerts
    rules:
      - alert: DeploymentFailure
        expr: deployment_status{status="failed"} > 0
        for: 0m
        annotations:
          summary: "Deployment {{ $labels.service }} failed"

      - alert: RollbackTriggered
        expr: rollback_count > 0
        for: 0m
        annotations:
          summary: "Rollback triggered for {{ $labels.service }}"

      - alert: PostDeploymentErrorRate
        expr: rate(http_requests_total{status=~"5.."}[5m]) > 0.1
        for: 1m
        annotations:
          summary: "Elevated error rate post-deployment for {{ $labels.service }}"
```
