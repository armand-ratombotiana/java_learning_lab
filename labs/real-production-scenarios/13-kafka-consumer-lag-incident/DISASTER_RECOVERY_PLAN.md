# Lab 13 — Kafka Consumer Lag: Disaster Recovery Plan

## Recovery Objectives

| Metric | Target |
|--------|--------|
| RTO (stop lag growth) | 15 minutes |
| RPO (message loss) | 0 (messages persist in Kafka) |
| MTD before data loss | Based on retention (default 7 days) |

## Scenarios

### Scenario A: Consumer Lag Growing

**Trigger:** Processing rate < produce rate for 5+ minutes
**Recovery:**
1. Identify root cause: slow processing, too few consumers, downstream bottleneck
2. If slow processing: optimize message handler, add caching, reduce work
3. If too few consumers: scale up (if partitions available) or increase partitions
4. If downstream bottleneck: add circuit breaker, bulkhead, fallback
5. If poison message: skip offset, send to DLQ
6. Monitor lag decreasing trend

### Scenario B: Consumer Crashed

**Trigger:** Consumer group has no active members, lag accumulating
**Recovery:**
1. Restart consumer application
2. Verify consumer rejoins group and partitions reassigned
3. Monitor that lag starts decreasing
4. If crash repeats: check OOM, config error, dependency failure
5. Add health checks and auto-restart

### Scenario C: Kafka Broker Failure

**Trigger:** Broker goes down, partitions on that broker unavailable
**Recovery:**
1. Kafka automatically elects new leader for affected partitions (if min.insync.replicas configured)
2. Consumers rebalance to new leader
3. Monitor for under-replicated partitions
4. Restart/replace failed broker
5. Verify partition replication returns to normal

## Runbook

```yaml
symptoms:
  - "Consumer lag > 10K and growing"
  - "Processing rate < produce rate"
  - "Increasing message age"

diagnosis:
  - "Check consumer lag per partition: kafka-consumer-groups --describe"
  - "Check consumer logs for errors"
  - "Check downstream service latency"
  - "Check consumer instance count vs partition count"
  - "Check for poison messages (same partition always failing)"

mitigation:
  - "Scale up consumers (up to partition count)"
  - "Restart consumer group (if stuck)"
  - "Skip poison message offset"
  - "Rollback if recent deployment caused slowdown"

fix:
  - "Optimize message processing"
  - "Add circuit breaker to downstream calls"
  - "Add backpressure handling"
  - "Increase partitions if needed (Kafka admin API)"
```
