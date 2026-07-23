# Lab 13 — Kafka Consumer Lag: Communication Templates

## Initial Alert

```
Title: [SEV2] Consumer Lag Growing — Order Processing Group — 500K Behind
Service: Order Event Processing (Kafka)
Severity: SEV2

Metrics:
- Consumer group: order-processor-v2
- Topic: orders (12 partitions)
- Total lag: 500,000 messages
- Lag growth: +10,000 msg/min (still growing)
- Processing rate: 500 msg/s (normal: 2,000 msg/s)
- Max message age: 17 minutes (and increasing)

Impact: Order processing delayed 17+ minutes
Cause: Downstream DB slow query holding up processing
```

## Status Updates

```
STATUS #1 — Lag Investigation

Root cause: Newly deployed consumer makes sync HTTP call to downstream
service that is throttling. Each call takes 2s instead of 100ms.
Processing rate dropped from 2,000 msg/s to 500 msg/s.

Actions:
- Rolling back consumer deployment
- Adding circuit breaker + fallback for downstream call
- Increasing consumer instances from 6 to 12 (matching partitions)

Estimated recovery: 2 hours (consuming backlog at 2,000 msg/s)
```

```
STATUS #2 — Lag Resolved

Rollback complete. Circuit breaker deployed.
12 consumers now processing at 2,500 msg/s.
Lag decreasing: 500K → 50K (estimated full recovery in 20 min).
Message age dropping: 17 min → 2 min.

Post-mortem actions:
1. Add load testing for consumer processing
2. Add circuit breaker to all downstream calls
3. Implement backpressure detection in consumer
4. Add lag growth rate alert
```
