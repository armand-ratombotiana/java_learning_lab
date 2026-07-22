# Incident Report: Kafka Consumer Lag

## Incident ID: INC-2026-0803-KAFKA
## Date: August 3, 2026
## Severity: SEV2 (High)

## Timeline (All times UTC)

### 14:00 — Production Rate Spike
- Marketing campaign launches at 14:00 UTC (planned)
- Order event production rate increases from 2,000 msg/s to 5,000 msg/s (150% increase)
- Kafka cluster handles production rate without issue (broker metrics normal)
- Consumer group starts falling behind

### 14:07 — DB Connection Pool Exhaustion
- Consumer's PostgreSQL connection pool (max=50) reaches 100% utilization
- Each consumer processing thread takes 200ms for DB writes
- Under 5,000 msg/s load, threads hold connections longer than they can be released
- Connection acquisition timeout errors begin:
  ```
  org.postgresql.util.PSQLException: FATAL: remaining connection slots are reserved
  ```

### 14:15 — Consumer Processing Degradation
- Average processing time per message increases from 200ms to 2,500ms
- Consumer poll timeout (max.poll.interval.ms) set to 300,000ms (5 minutes)
- First consumer exceeds poll interval and triggers rebalance
- Lag at this point: ~145,000 messages

### 14:20 — Rebalance Storm Begins
- Consumer #3 exceeds max.poll.interval.ms (5 min) → leaves group
- Group coordinator triggers rebalance
- During rebalance, all consumers pause processing (eager rebalancing)
- Lag grows faster during rebalance pauses
- After rebalance, consumers resume but immediately start timing out again
- Cycle repeats: timeout → rebalance → timeout → rebalance

### 14:23 — First Rebalance (Consumer #3 exits)
- Kafka broker logs:
  ```
  [Consumer group coordinator] Member order-processor-3 left group with generation 47
  [Consumer group coordinator] Rebalancing group order-processor-group with generation 48
  ```

### 14:25 — Monitoring Alert
- Grafana dashboard shows `records-lag-max` at 197,000
- `kafka_consumer_group_CurrentOffset` stall detected
- PagerDuty alert: `KafkaConsumerLagHigh` fires
- Severity: SEV2 (by runbook, 100,000 lag threshold)

### 14:27 — Initial Investigation (SRE)
- SRE checks consumer group status:
  ```bash
  kafka-consumer-groups.sh --bootstrap-server prod-events.confluent.cloud:9092 \
    --group order-processor-group --describe
  ```
- Output shows 9 of 12 consumers with LAG > 50,000
- 3 consumers marked DEAD (not responding to heartbeats)
- Consumer group state: STABLE (in between rebalances)

### 14:35 — Rebalance Frequency Analysis
- SRE checks rebalance frequency via broker metrics:
  ```bash
  # From broker JMX
  kafka.consumer.group:type=consumer-group-metrics,group=order-processor-group
  ```
- 7 rebalances in the last 30 minutes
- Average between-rebalance time: 4 minutes (less than 5-minute session timeout)

### 14:42 — Consumer Logs Investigation
- SRE checks consumer application logs:
  ```bash
  kubectl logs -n production -l app=order-processor-consumer --tail=200
  ```
- Logs show repeated:
  ```
  WARN  o.a.k.c.c.i.ConsumerCoordinator - Consumer order-processor-3
    has exceeded the max poll interval. Leaving the group.
  ERROR c.a.o.c.OrderEventConsumer - Error processing message:
    org.postgresql.util.PSQLException: Connection pool exhausted
  ```

### 14:58 — Root Cause Identified
- DB team identifies PostgreSQL connection pool as bottleneck
- `pgbouncer` metrics show all 50 connections saturated
- Average query time: 200ms (normal: 10ms)
- Connection wait time: 2,300ms (normal: < 5ms)
- Root cause: Recent schema migration added new index rebuild causing table locks

### 15:10 — DB Fix Applied
- DB team kills the stuck migration process
- Connection pool immediately available
- Average query time drops from 200ms to 15ms
- Connection wait time drops to < 10ms

### 15:12 — Consumer Recovery
- Consumers start processing faster
- DB bottleneck removed
- Processing time per message: 15ms (down from 2,500ms)
- Lag continues growing (incoming rate still > processing rate)

### 15:14 — Second Issue Discovered
- Even with DB fix, lag is not draining fast enough
- Consumer max.poll.records=500 limits per-batch throughput
- At 5,000 msg/s, consumers can only process 500 per batch
- Need to increase batch size to keep up

### 15:20 — Consumer Configuration Tuning
- SRE updates consumer configuration:
  ```properties
  max.poll.records=5000
  max.poll.interval.ms=600000  # 10 minutes
  session.timeout.ms=30000  # 30 seconds
  heartbeat.interval.ms=10000  # 10 seconds
  ```

### 15:25 — Rebalances Continue
- With updated config, rebalances still occurring due to eager rebalancing
- SRE identifies need for cooperative rebalancing and static group membership

### 15:30 — Static Group Membership Applied
- Consumer config updated:
  ```properties
  group.instance.id=order-processor-1  # Per consumer
  ```
- `partition.assignment.strategy=CooperativeStickyAssignor`
- Rolling restart of consumer pods with new config
- Rebalances become cooperative (incremental, not stop-the-world)

### 15:45 — Lag Drain Begins
- With cooperative rebalancing, consumers no longer pause during rebalance
- Processing rate: 6,000 msg/s (exceeds production rate of 5,000 msg/s)
- Lag starts decreasing from peak of 2,718,493
- Estimated drain time: 7.5 minutes (2.7M / 6,000 msg/s)

### 15:53 — Lag Below Threshold
- Lag drops below 100,000 (alert threshold)
- PandDuty alert auto-resolved
- Processing latency: 12 seconds (down from 3h 45m)

### 16:02 — All Lag Drained
- Lag reaches 0
- Consumer now processing in real-time
- Grafana dashboard shows green across all metrics
- No rebalances in last 30 minutes

### 16:15 — Post-Incident Investigation Begins
- Team reviews rebalance storm metrics
- JMX metrics show 17 rebalances during incident
- Each rebalance cost 30+ seconds of processing time
- Static group membership would have prevented all 17 rebalances
- max.poll.records=500 confirmed as too low for peak load

### 18:35 — Incident Declared Resolved
- All Kafka consumer metrics back to normal
- No rebalances in last 2 hours
- Lag at 0 (healthy)
- Monitoring confirmed operational

## Detailed Timeline Metrics

### Consumer Group State Changes

```
14:00 — Group state: STABLE, generation: 46
        Members: 12/12 active
        Assignment: RangeAssignor

14:15 — Consumer #3 exceeds max.poll.interval.ms
        Group rebalance triggered (generation 47)
        Group state: REBALANCING
        All consumers STOPPED processing

14:16 — Rebalance complete (generation 47)
        Group state: STABLE
        Assignment redistributed among 11 consumers
        Lag increase during rebalance: +120,000 messages

14:18 — Consumer #8 exceeds max.poll.interval.ms
        Group rebalance triggered (generation 48)
        ...

        [Pattern continues: timeout → rebalance → timeout → rebalance]
        
15:30 — Static group membership applied
        CooperativeStickyAssignor configured
        Group state: STABLE (generation 64)
        No further rebalances
```

### JMX Metric Snapshots

```
14:00 — Pre-incident baseline:
  records-lag-max: 12,847
  records-consumed-rate: 2,100 msg/s
  records-lag-min: 234
  fetch-rate: 4.2 req/s

14:30 — During rebalance storm:
  records-lag-max: 845,000
  records-consumed-rate: 200 msg/s (degraded)
  records-lag-min: 45,000
  fetch-rate: 1.1 req/s
  
15:45 — After fix:
  records-lag-max: 2,100,000 (peak) then decreasing
  records-consumed-rate: 6,100 msg/s (exceeds production)
  records-lag-min: 850,000 (draining)
  fetch-rate: 8.7 req/s

16:02 — Recovered:
  records-lag-max: 0
  records-consumed-rate: 5,100 msg/s
  records-lag-min: 0
  fetch-rate: 5.3 req/s
```

### Database Connection Pool Metrics

```
Metric: HikariCP connection pool (max=50)

14:00: active=15, idle=35, pending=0, wait=0ms
14:05: active=28, idle=22, pending=3, wait=12ms
14:07: active=50, idle=0, pending=47, wait=2,300ms ← saturation
14:10: active=50, idle=0, pending=50, wait=5,000ms ← pool exhausted
14:15: active=50, idle=0, pending=50, wait=10,000ms ← connections timing out
15:10: active=12, idle=38, pending=0, wait=5ms ← after migration killed
15:30: active=35, idle=65 (pool increased to 100), pending=0, wait=2ms
```

### Kafka Broker Metrics During Incident

```
Metric: Rebalance rate (per hour)

14:00-15:00: 12 rebalances/hour (normal: < 1/hour)
15:00-16:00: 5 rebalances/hour (after DB fix, before static membership)
16:00-17:00: 0 rebalances/hour (after static membership)

Metric: Consumer group size
14:00: 12 members
14:15: 11 members (consumer #3 left)
14:20: 10 members
14:30: 9 members (lowest)
15:30: 12 members (recovered)

Metric: Network I/O
14:00: 45 MB/s in, 12 MB/s out
14:30: 15 MB/s in, 3 MB/s out (reduced due to rebalances)
15:30: 55 MB/s in, 18 MB/s out (back to normal)
```

### Consumer Processing Time Histogram

```
Processing time per message (before fix):
  p50: 210ms
  p90: 450ms  
  p95: 1,200ms
  p99: 3,500ms
  max: 12,000ms (connection timeout)

Processing time per message (after fix):
  p50: 12ms
  p90: 28ms
  p95: 45ms
  p99: 120ms
  max: 500ms
```

### Rebalance Duration Analysis

```
Rebalance #1: duration 28s (generation 46→47)
  - Leave notification: 2s
  - Group coordinator election: 3s
  - Partition assignment: 5s
  - Consumer sync: 18s

Rebalance #5: duration 35s (generation 50→51)
  - Multiple consumers leaving simultaneously
  - Extended sync period

Rebalance #12: duration 31s (generation 57→58)
  - Typical rebalance pattern

Average rebalance duration: 31.7s
Total time in rebalance (17 rebalances): 538s (9 minutes)
Production during rebalance time: 5,000 msg/s × 538s = 2,690,000 messages
This accounts for 99% of peak lag!
```

## Post-Incident Actions

1. Implement static group membership (group.instance.id) for all consumer groups
2. Switch to cooperative rebalancing (CooperativeStickyAssignor)
3. Increase max.poll.records from 500 to 5000
4. Add lag alert at 10,000 (not 100,000) for earlier detection
5. Add rebalance monitoring and alerting
6. Add DB connection pool monitoring integration
7. Implement async processing pattern to decouple Kafka polling from DB writes
8. Add consumer health dashboard with lag, rebalance count, processing latency
9. Implement consumer auto-scaling based on lag (KEDA)
10. Add circuit breaker for downstream database dependency
