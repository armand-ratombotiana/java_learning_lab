# Lab 07 — Circuit Breaker: SLI/SLO/SLA Definitions

## Service: Payment Processing (with Circuit Breaker)

### SLIs

| Indicator | Definition | Target |
|-----------|-----------|--------|
| Circuit state | OPEN time / total time per downstream | < 5% OPEN |
| Failure rate | Failed calls / total calls per downstream | < 1% |
| Fallback rate | Fallback responses / total responses | < 10% |
| Call volume | Calls per second per downstream | Monitor |
| Circuit transitions | OPEN/CLOSED transitions per hour | < 3 |
| Latency P99 | When circuit CLOSED | < 500ms |

### SLOs

| SLI | Target | Error Budget |
|-----|--------|-------------|
| Availability (all downstreams) | 99.95% | ~21.9 min/month |
| Successful fallback rate | 99% | ~7.3 hours/month |
| Circuit not in OPEN > 1 hour | 99.9% | ~43.8 min/month |
| P99 latency < 500ms | 99% | ~7.3 hours/month |

### Alerting

```yaml
groups:
  - name: circuit_breaker
    rules:
      - alert: CircuitBreakerOpen
        expr: resilience4j_circuitbreaker_state{state="open"} == 1
        for: 5m
        annotations:
          summary: "Circuit OPEN for {{ $labels.name }} for > 5 minutes"

      - alert: CircuitFlapping
        expr: changes(resilience4j_circuitbreaker_state[1h]) > 10
        annotations:
          summary: "Circuit flapping — frequent OPEN/CLOSE transitions"

      - alert: HighFallbackRate
        expr: rate(resilience4j_circuitbreaker_calls{kind="successful",type="fallback"}[5m])
              / rate(resilience4j_circuitbreaker_calls_total[5m]) > 0.5
        annotations:
          summary: "> 50% of calls using fallback for {{ $labels.name }}"
```
