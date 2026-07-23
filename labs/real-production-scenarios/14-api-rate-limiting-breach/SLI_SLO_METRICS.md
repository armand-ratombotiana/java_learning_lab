# Lab 14 — API Rate Limiting: SLI/SLO/SLA

## Service: Public API Gateway

### SLIs

| Indicator | Definition | Target |
|-----------|-----------|--------|
| Rate limit accuracy | Actual vs. configured limit | ± 5% |
| Rate limit latency | Additional latency from rate limiting | < 1ms |
| False positive rate | Legitimate requests blocked / total | < 0.01% |
| 429 error rate | 429 responses / total requests | < 1% |
| Bust rate | Requests exceeding limit / total | < 5% |

### SLOs

| SLI | Target | Error Budget |
|-----|--------|-------------|
| Zero false positives (legitimate users blocked) | 99.99% | ~4.38 min/month |
| Rate limit latency < 1ms | 99% | ~7.3 hours/month |
| 429 rate < 1% of all requests | 99% | ~7.3 hours/month |

### Alerting

```yaml
groups:
  - name: ratelimit_alerts
    rules:
      - alert: RateLimitHigh
        expr: rate(http_requests_total{status="429"}[5m]) > 100
        for: 2m
        annotations:
          summary: "High rate of 429 responses"

      - alert: RateLimitFalsePositive
        expr: rate(ratelimit_blocked_legitimate_total[5m]) > 10
        for: 5m
        annotations:
          summary: "Possible false positives in rate limiting"

      - alert: RateLimitLatencyHigh
        expr: histogram_quantile(0.99, rate(ratelimit_latency_seconds_bucket[5m])) > 0.01
        annotations:
          summary: "P99 rate limit latency > 10ms"
```
