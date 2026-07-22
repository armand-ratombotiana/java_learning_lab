# Root Cause Analysis: Kafka Consumer Lag Incident

## RCA ID: RCA-2026-0803-001
## Severity: SEV2 — High Impact Production Degradation
## Duration: 4 hours 12 minutes
## Total Impact: ~$265,000 revenue loss + data freshness SLA breach

## Executive Summary

On August 3, 2026, the order-processing Kafka consumer group experienced catastrophic lag growth from 10,000 to 2.7 million messages. The incident was triggered by a database schema migration that caused connection pool exhaustion, which slowed consumer processing and triggered a rebalance storm. The rebalance storm prevented consumers from draining the backlog because each rebalance paused all consumers for 30+ seconds. The incident lasted 4 hours 12 minutes and resulted in $265,000 in lost revenue from delayed order fulfillment.

## What Happened

A marketing campaign caused a 150% spike in order event production. Simultaneously, a database schema migration (adding a new index) caused table-level locks that slowed query times from 10ms to 200ms. The consumer's PostgreSQL connection pool (max=50) became fully saturated. Slower queries meant consumers held connections longer, exceeding Kafka's `max.poll.interval.ms` (5 minutes). Each timeout triggered a consumer group rebalance. Because the group used eager rebalancing, every rebalance paused all 12 consumers for 30+ seconds, during which lag grew unchecked. This created a vicious cycle: slow processing → timeout → rebalance → lost processing time → more lag → slower processing.

## Direct Cause

Database table locks from an unoptimized schema migration caused query times to increase 20x (10ms → 200ms), exhausting the PostgreSQL connection pool and causing consumer processing to exceed `max.poll.interval.ms`.

## The 5 Whys Analysis

### Why 1: Why did consumer lag grow to 2.7 million messages?

Consumer processing throughput dropped from 2,000 msg/s to 200 msg/s because the PostgreSQL connection pool was exhausted. With max.poll.records=500 and 200ms per query, each consumer could only process 2,500 msg/s (500 / (0.2s)). This was below the production rate of 5,000 msg/s, causing lag to grow. Additionally, rebalance storms paused all consumers periodically, preventing any lag drainage.

**Evidence**: JMX metrics show consumer fetch rate dropped from 2,000 msg/s to 200 msg/s at 14:07. Connection pool metrics show 50/50 connections in use.

### Why 2: Why was the PostgreSQL connection pool exhausted?

A database schema migration (adding a composite index on `order_events(user_id, event_type, created_at)`) was running in the background. The `CREATE INDEX CONCURRENTLY` statement was supposed to run without blocking writes, but it caused significant I/O contention and table-level locks during the build phase. This increased query execution time from 10ms to 200ms.

**Evidence**: pg_stat_activity shows:
```
SELECT * FROM pg_stat_activity WHERE wait_event_type = 'Lock';
```
Multiple queries waiting on lock held by `CREATE INDEX CONCURRENTLY` on `order_events`.

### Why 3: Why did consumer timeouts trigger a rebalance storm?

The consumer configuration used:
- `max.poll.interval.ms=300000` (5 minutes)
- `session.timeout.ms=45000` (45 seconds)
- Eager rebalancing (default `org.apache.kafka.clients.consumer.CooperativeStickyAssignor`)

With 200ms processing time per message and max.poll.records=500, each poll cycle took:
- 500 × 200ms = 100 seconds per poll cycle
- Plus DB connection acquisition wait: 2,300ms per message
- Total: 500 × (200 + 2,300) = 1,250,000ms = 20.8 minutes per poll cycle

This exceeded the 5-minute `max.poll.interval.ms`, causing consumers to be removed from the group.

**Evidence**: Kafka broker logs show 17 rebalances between 14:15 and 15:30. Each rebalance was triggered by `Consumer has exceeded the max poll interval`.

### Why 4: Why did rebalances cause lag to grow so quickly?

Eager rebalancing (standard `RangeAssignor`/`RoundRobinAssignor`) uses stop-the-world semantics:
1. All consumers revoke all partitions
2. Group coordinator waits for all consumers to acknowledge
3. New assignment is calculated and distributed
4. Consumers re-fetch their assigned partitions

During steps 1-3, no consumer processes any messages. With 12 consumers and 30-second rebalance duration, each rebalance costs 12 × 30 = 360 consumer-seconds of lost processing.

At 5,000 msg/s production rate, each 30-second rebalance adds 150,000 unprocessed messages to the lag. With 17 rebalances, this contributed 17 × 150,000 = 2.55 million messages of additional lag.

**Evidence**: 
- Average rebalance duration: 32 seconds (from broker metrics)
- Lag increase during each rebalance: ~150,000
- Total rebalances: 17
- Lag from rebalances alone: 2.55M (94% of peak lag)

### Why 5: Why was there no protection against rebalance storms?

**Organizational Root Cause**: The team had not configured:
1. **Static group membership**: `group.instance.id` was not set. This means any consumer restart or timeout triggered a full rebalance rather than a targeted member removal.

2. **Cooperative rebalancing**: `partition.assignment.strategy=CooperativeStickyAssignor` was not used. Cooperative rebalancing allows consumers to continue processing non-revoked partitions during rebalance.

3. **Early lag detection**: The alert threshold (100,000 lag) was set based on normal traffic patterns (2,000 msg/s). The threshold should have been adjusted based on peak traffic (5,000 msg/s).

4. **DB bottleneck visibility**: The connection pool exhaustion metric was not integrated into the Kafka monitoring dashboard. The DB team was not aware of the consumer's dependency on connection pool availability.

5. **Consumer health monitoring**: Consumer poll interval and processing time metrics were not being monitored. The team could not see that consumers were approaching the `max.poll.interval.ms` threshold.

**Systemic Root Cause**: The organization lacked:
1. Standard consumer configuration for production (static membership, cooperative rebalancing)
2. Integrated monitoring between Kafka consumers and downstream dependencies (database, API services)
3. Load testing for consumer groups under peak traffic
4. Graceful degradation mechanisms (async processing, circuit breakers)
5. Rebalance monitoring and alerting

## Contributing Factors

1. **Marketing campaign**: Planned campaign caused 150% traffic spike; consumer scaling was not adjusted proactively
2. **Unoptimized migration**: `CREATE INDEX CONCURRENTLY` on a table with heavy write activity caused unexpected blocking
3. **No async processing**: Consumer was writing directly to DB in the poll thread; no async queue or batching
4. **No circuit breaker**: When DB connection pool was exhausted, consumers continued retrying instead of failing fast
5. **No consumer scaling**: The consumer group was not configured to scale out during traffic spikes (static replica count)

## Verification

The root cause was verified by:
1. Broker logs showing 17 rebalances triggered by max.poll.interval.ms violations
2. Database metrics showing connection pool exhaustion at 50/50
3. pg_stat_activity showing lock waits caused by CREATE INDEX CONCURRENTLY
4. Consumer logs confirming "Connection pool exhausted" errors
5. JMX metrics showing records-lag-max reaching 2,718,493
6. Grafana dashboard showing rebalance frequency and lag correlation
7. Consumer configuration showing no group.instance.id or CooperativeStickyAssignor

## Evidence Analysis

### Rebalance Storm Root Cause Confirmation

The 17 rebalances during the incident were traced to a single root cause:

```
Consumer #3 log at 14:15:
  WARN o.a.k.c.c.i.ConsumerCoordinator: 
    Consumer order-processor-3 has exceeded the max poll interval of 300000ms.
    Leaving the group.

Consumer #3 thread dump:
  "order-processor-3" #23 prio=5 os_prio=0 cpu=1247ms
    java.lang.Thread.State: BLOCKED
      at org.postgresql.core.v3.ConnectionFactoryImpl.openConnectionImpl
      - waiting to acquire connection from HikariCP pool
      at com.acmecorp.orderprocessor.service.OrderEventService.saveOrder
      
    Connection pool status:
      active: 50/50 (all connections held by other processing threads)
      pending: 47 threads waiting for connection
```

The consumer was blocked on database connection acquisition for 5+ minutes, exceeding `max.poll.interval.ms`.

### Resource Contention Analysis

The DB connection pool exhaustion created a cascading failure:

```
1. 50 processing threads each holding a DB connection
2. Each thread processing at 200ms (normal) → degraded to 2,500ms
3. Threads hold connections longer → pool exhausted
4. New processing threads wait for connections → avg wait 2,300ms  
5. Poll cycle extends from 100s to 1,250s (20.8 minutes)
6. max.poll.interval.ms (5 minutes) exceeded → consumer ejected
7. Rebalance triggered → all consumers pause
8. Lag grows faster during pause → more pressure on remaining consumers
9. More consumers hit timeout → cascade
```

### Consumer Configuration Difference Analysis

| Property | Current (Failing) | Recommended (Fixed) | Difference |
|----------|-------------------|---------------------|------------|
| max.poll.records | 500 | 5,000 | 10x |
| max.poll.interval.ms | 300,000 | 600,000 | 2x |
| session.timeout.ms | 45,000 | 30,000 | Shorter detection |
| group.instance.id | Not set | ${HOSTNAME} | Static membership |
| partition.assignment.strategy | RangeAssignor | CooperativeStickyAssignor | Incremental |
| enable.auto.commit | true | false | Manual control |
| auto.offset.reset | latest | earliest | Replay from start |

### Code Review: Missing Async Pattern

The consumer code before the fix performed synchronous DB writes:

```java
// Before (synchronous, blocking poll thread):
@KafkaListener(topics = "order-events")
public void consume(List<OrderEvent> events) {
    for (OrderEvent event : events) {
        orderEventService.processEvent(event); // Synchronous DB write
        // Blocks poll() until all events processed
        // max.poll.interval.ms ticking...
    }
}

// After (async, non-blocking poll thread):
@KafkaListener(topics = "order-events")
public void consume(List<OrderEvent> events, Acknowledgment ack) {
    List<CompletableFuture<Void>> futures = events.stream()
        .map(event -> CompletableFuture.runAsync(() -> 
            eventQueue.offer(event)))  // Fast queue offer
        .collect(Collectors.toList());
    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
        .thenRun(() -> ack.acknowledge()); // Ack after queuing
}
```

### Database Migration Analysis

The specific migration causing the issue:

```sql
-- Migration V20260803__add_order_events_composite_index.sql
-- Executed at 14:00 UTC

CREATE INDEX CONCURRENTLY idx_order_events_lookup
ON order_events (user_id, event_type, created_at);

-- Expected: Non-blocking (CONCURRENTLY)
-- Actual: Caused significant I/O contention
--   - Heavy write load on order_events table (5,000 msg/s)
--   - PostgreSQL autovacuum contention
--   - Index build caused lock contention on system catalogs
--   - Query planner chose sequential scans during build
```

### Monitoring Gap Analysis

The following metrics were available but not monitored:

| Metric | Available | Monitored | Alert Configured |
|--------|-----------|-----------|-----------------|
| records-lag-max | Yes (JMX) | Yes (Grafana) | Yes (threshold: 100K) |
| max.poll.interval.ms exceeded | Yes (logs) | No | No |
| Rebalance count | Yes (broker JMX) | No | No |
| DB connection pool utilization | Yes (HikariCP) | No | No |
| Consumer processing time | Yes (Micrometer) | No | No |
| Poll cycle duration | Yes (Kafka client) | No | No |
| DB query execution time | Yes (pg_stat) | No | No |

## Recommendations Matrix

| Priority | Recommendation | Effort | Impact | Owner | Timeline |
|----------|---------------|--------|--------|-------|----------|
| P0 | Add group.instance.id to all consumer groups | 2h | Critical | App Team | Week 1 |
| P0 | Switch to CooperativeStickyAssignor | 1h | Critical | App Team | Week 1 |
| P0 | Increase max.poll.records to 5,000 | 1h | Critical | App Team | Week 1 |
| P1 | Implement async processing pattern | 16h | High | App Team | Week 2 |
| P1 | Add lag alert at 10,000 messages | 1h | High | Observability | Week 1 |
| P1 | Add rebalance monitoring | 4h | High | Observability | Week 2 |
| P2 | Integrate DB metrics into Kafka dashboard | 8h | Medium | Observability | Week 3 |
| P2 | Implement consumer auto-scaling (KEDA) | 16h | Medium | Platform | Week 4 |
| P2 | Add circuit breaker for DB dependency | 8h | Medium | App Team | Week 4 |
| P3 | Load testing for consumer groups | 24h | Low | QA | Month 2 |
| P3 | Consumer configuration CI validation | 8h | Low | DevOps | Month 2 |

## Recommendations Summary

1. **Immediate**: Add static group membership (group.instance.id) to all consumer groups
2. **Immediate**: Switch to cooperative rebalancing (CooperativeStickyAssignor)
3. **Short-term**: Increase max.poll.records from 500 to 5000
4. **Short-term**: Add early lag alert at 10,000 (not 100,000)
5. **Medium-term**: Implement async processing pattern (decouple poll from DB writes)
6. **Medium-term**: Add rebalance monitoring and alerting
7. **Medium-term**: Integrate DB connection pool metrics into Kafka monitoring
8. **Long-term**: Implement load shedding for consumer groups under peak traffic

## Appendices

### Appendix A: Consumer Group States and Transitions

```
STABLE ──→ REBALANCING: Consumer leaves group (session timeout)
STABLE ──→ REBALANCING: New consumer joins group
STABLE ──→ REBALANCING: Heartbeat timeout (session.timeout.ms)
REBALANCING ──→ STABLE: Partition assignment complete
REBALANCING ──→ EMPTY: No members remaining
EMPTY ──→ REBALANCING: First consumer joins group

Eager vs Cooperative Rebalancing:
  Eager: ALL consumers STOP processing, revoke ALL partitions
         → Reassignment happens while NO consumer is processing
         → Lag increases for ALL partitions
         
  Cooperative: Only REVOKED partitions stop processing
               → Most consumers continue processing
               → Only affected partitions see lag increase
               → 92% reduction in rebalance cost
```

### Appendix B: Lag Calculation and Drain Time Formulas

```
Total Lag = Σ(lag_per_partition) over all partitions

Lag Growth Rate = Production Rate (msg/s) - Consumption Rate (msg/s)
  Positive → lag increasing
  Negative → lag decreasing (draining)
  Zero → steady state

Time to Drain (seconds) = Total Lag / (Consumption Rate - Production Rate)
  
Example from incident:
  Peak lag: 2,718,493 messages
  Production rate after fix: 5,000 msg/s
  Consumption rate after fix: 6,100 msg/s
  Drain rate: 6,100 - 5,000 = 1,100 msg/s
  Estimated drain time: 2,718,493 / 1,100 ≈ 2,471 seconds ≈ 41 minutes
  Actual drain time: 17 minutes (because consumers auto-scaled)

Per-Partition Imbalance Factor:
  Ideal: lag evenly distributed across 24 partitions
  Actual: some partitions have 3x more lag than others
  Imbalance = max(lag) / avg(lag) > 2 indicates partition key skew
```

### Appendix C: Consumer Configuration Validation

```yaml
# CI pipeline validation checks
validations:
  - check: "group.instance.id must be set"
    rule: "grep -q 'group.instance.id' src/main/resources/application.yml"
    severity: error
    
  - check: "CooperativeStickyAssignor must be configured"
    rule: "grep -q 'CooperativeStickyAssignor' src/main/resources/application.yml"
    severity: error
    
  - check: "max.poll.records >= 1000"
    rule: "grep 'max.poll.records' src/main/resources/application.yml | awk -F: '{gsub(/ /, \"\"); if (\$2 < 1000) exit 1}'"
    severity: warning
    
  - check: "Async processing pattern used"
    rule: "grep -q 'CompletableFuture\|@Async\|Queue' src/main/java/com/acmecorp/orderprocessor/consumer/"
    severity: warning
    
  - check: "Circuit breaker configured"
    rule: "grep -q 'circuitbreaker\|resilience4j' pom.xml"
    severity: warning
```

### Appendix D: Kafka Cluster Capacity Planning

| Metric | Current | Peak Observed | Growth Rate | Required (Next Year) |
|--------|---------|---------------|-------------|---------------------|
| Messages per second | 5,000 | 7,500 | +50% YoY | 11,250 |
| Topic partitions | 24 | 24 | +12/quarter | 72 |
| Consumer instances | 12 | 24 (scaled) | +50% YoY | 36 |
| Storage per day | 432 GB | 648 GB | +50% YoY | 972 GB |
| Network bandwidth | 45 MB/s | 85 MB/s | +50% YoY | 128 MB/s |
| Brokers | 6 | 6 | +2/year | 8 |

### Appendix E: Post-Incident Verification Commands

```bash
# Verify consumer group is stable
kafka-consumer-groups.sh --bootstrap-server prod-events.confluent.cloud:9092 \
  --group order-processor-group --describe --state

# Output should show: STABLE

# Verify no rebalances in last hour
# Check broker logs
grep "Rebalance" /var/log/kafka/server.log | grep "order-processor-group" | tail -5

# Verify all consumers have static membership
kafka-consumer-groups.sh --bootstrap-server prod-events.confluent.cloud:9092 \
  --group order-processor-group --describe --members --verbose | grep "group.instance.id"

# Verify cooperative rebalancing is active
kafka-consumer-groups.sh --bootstrap-server prod-events.confluent.cloud:9092 \
  --group order-processor-group --describe | grep "CooperativeStickyAssignor"

# Check lag is at acceptable levels
kafka-consumer-groups.sh --bootstrap-server prod-events.confluent.cloud:9092 \
  --group order-processor-group --describe | awk '{sum+=$5} END {print "Total lag:", sum}'
```

### Appendix F: Incident Timeline Summary

```
09:47 UTC — AWS Health Dashboard alert (us-east-1 degradation)
09:52 UTC — Full region failure confirmed, SEV1 declared
10:02 UTC — DR runbook issues found (AMI, SG, RDS class)
10:18 UTC — RDS replication lag: 720 seconds (12 minutes)
10:28 UTC — RDS read replica promoted to primary
10:35 UTC — ECR images not in us-west-2
10:48 UTC — CloudFormation dependency error fixed
11:15 UTC — Application containers rebuilt and deployed
11:28 UTC — ElastiCache being provisioned in us-west-2
11:35 UTC — Route53 DNS cutover initiated
12:10 UTC — CloudFront origins updated
12:22 UTC — First successful user traffic to us-west-2
13:47 UTC — All critical services operational
14:30 UTC — AWS reports us-east-1 recovery
16:57 UTC — Incident declared resolved

Total outage: 7 hours 10 minutes
RTO achieved: 107 minutes (target: 15 minutes)
RPO achieved: 17 minutes (target: 5 minutes)
Data loss: 12 minutes of transactions (847 orders)
Revenue impact: $742,000
```
