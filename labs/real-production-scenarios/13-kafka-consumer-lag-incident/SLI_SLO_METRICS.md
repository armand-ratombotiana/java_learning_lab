# Lab 13 — Kafka Consumer Lag: SLI/SLO/SLA

## Service: Event Processing (Kafka)

### SLIs

| Indicator | Definition | Target |
|-----------|-----------|--------|
| Consumer lag | Max lag across all partitions | < 10,000 messages |
| Consumer lag growth | Lag increase rate (msg/min) | 0 (flat) |
| Processing rate | Messages consumed per second | = produce rate |
| Rebalance frequency | Consumer group rebalances per hour | < 1 |
| Processing error rate | Failed messages / total | < 0.1% |
| Message age | Time from produce to consume | < 60 seconds |

### SLOs

| SLI | Target | Error Budget |
|-----|--------|-------------|
| Lag < 10K per partition | 99% | ~7.3 hours/month |
| Zero growing lag (12 hours) | 99.9% | ~43.8 min/month |
| Processing success rate | 99.99% | ~4.38 min/month |
| P99 message age < 60s | 99% | ~7.3 hours/month |

### Alerting

```yaml
groups:
  - name: kafka_alerts
    rules:
      - alert: ConsumerLagHigh
        expr: kafka_consumer_lag > 10000
        for: 5m
        annotations:
          summary: "Consumer lag > 10K for {{ $labels.consumer_group }} / {{ $labels.topic }}"

      - alert: ConsumerLagGrowing
        expr: delta(kafka_consumer_lag[15m]) > 1000
        for: 10m
        annotations:
          summary: "Consumer lag growing for {{ $labels.consumer_group }}"

      - alert: RebalanceFrequent
        expr: rate(kafka_consumer_group_rebalance_total[15m]) > 0.1
        for: 5m
        annotations:
          summary: "Frequent rebalances for {{ $labels.consumer_group }}"
```
