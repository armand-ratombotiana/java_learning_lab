# Mock Interview: Messaging (Lab 07)

**Role:** Backend Engineer (Senior)  
**Duration:** 45 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** Compare RabbitMQ and Kafka. When would you use each?

**Candidate:**

| Aspect | RabbitMQ | Kafka |
|--------|----------|-------|
| Message model | Queue-based (push) | Log-based (pull) |
| Throughput | ~50K msg/sec | ~1M+ msg/sec |
| Message routing | Complex (exchanges, bindings) | Topics only |
| Message retention | Deleted after ack | Configurable retention |
| Ordering | Queue-level | Partition-level |
| Consumer model | Competing consumers | Consumer groups |
| Use case | Task queues, RPC | Event streaming, data pipelines |

Choose RabbitMQ for: complex routing, RPC, task distribution, when messages need individual processing.
Choose Kafka for: high throughput, event sourcing, log aggregation, replay, long-term retention.

**Interviewer:** How does Spring Cloud Stream simplify messaging?

**Candidate:** Spring Cloud Stream provides a binder abstraction that decouples application code from the specific messaging middleware. You define `@Functional` interfaces (Supplier, Function, Consumer) and the binder connects them to the messaging system. Configuration (topic names, partitioning, consumer groups) is externalized. Switching from Kafka to RabbitMQ requires only changing the binder dependency and properties.

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** Design an order processing pipeline with guaranteed delivery and exactly-once semantics.

**Candidate:** For guaranteed delivery with exactly-once processing:

1. **Producer side:** Use Kafka's transactional API with `idempotence=true`
2. **Consumer side:** Implement idempotent processing with a deduplication store
```java
@Component
public class OrderProcessor {
    private final Set<String> processedIds = ConcurrentHashMap.newKeySet();
    
    @KafkaListener(topics = "orders")
    public void processOrder(OrderEvent event) {
        String idempotencyKey = event.getOrderId() + "_" + event.getVersion();
        if (!processedIds.add(idempotencyKey)) return; // Already processed
        
        processOrderInternal(event);
    }
}
```

3. **Dead letter queue:** Configure `@DltHandler` for failed messages after retries
4. **Transactional outbox:** Write events to a local DB table in the same transaction as business data, then publish via a separate process

**Interviewer:** How do you handle message rebalancing in Kafka consumer groups?

**Candidate:** When consumers join or leave a group, Kafka triggers a rebalance. During rebalance, all consumers in the group stop processing to reassign partitions. Strategies:
1. **Eager rebalance (default):** All partitions revoked, then reassigned — stop-the-world
2. **Cooperative rebalance:** Partitions reassigned gradually. Use `partition.assignment.strategy=CooperativeStickyAssignor`
3. **Static group membership:** `group.instance.id` to prevent rebalance on restart
4. **Session timeout tuning:** Adjust `session.timeout.ms` and `heartbeat.interval.ms`

For stateless processing, eager rebalance is fine. For stateful processing (aggregations), cooperative rebalance minimizes disruption.

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a real-time fraud detection system processing 100K transactions/second using messaging.

**Candidate:** Architecture:

```
Transactions → Kafka Cluster (12 partitions)
                    ↓
         Streaming Processor (Kafka Streams)
           ├─ Aggregate per user (1-min tumbling window)
           ├─ Aggregate per card (5-min sliding window)
           └─ Pattern detection (session window)
                    ↓
         Alert Topic ← Fraud Detection Service (A.I.)
                    ↓
         Action Service → block/flag/allow
```

**Spring Boot implementation:**
```java
@Component
public class FraudDetectionStream {
    
    @Bean
    public KStream<String, Transaction> process(StreamsBuilder builder) {
        KStream<String, Transaction> stream = builder.stream("transactions");
        
        // Per-user velocity check
        stream.groupByKey()
            .windowedBy(TimeWindows.of(Duration.ofMinutes(1)))
            .aggregate(TransactionAggregate::new)
            .toStream()
            .filter((key, agg) -> agg.getTotalAmount() > 10000)
            .to("high-value-alerts");
        
        // Geographic anomaly (different country within minutes)
        stream.groupByKey()
            .windowedBy(SessionWindows.of(Duration.ofMinutes(5)))
            .aggregate(GeoSession::new)
            .toStream()
            .filter((key, session) -> session.distanceDuringWindow() > 1000)
            .to("geo-anomalies");
            
        return stream;
    }
}
```

**Exactly-once semantics** with `processing.guarantee=exactly_once_v2`. State stores backed by RocksDB for aggregates. Alerts published to separate Kafka topic for downstream action processing.

**Interviewer:** What happens if the Kafka broker crashes? How do you ensure no message loss?

**Candidate:** Kafka ensures durability through replication:
- Each partition has N replicas (configurable, typically 3)
- One replica is the leader, others are followers
- Messages are acknowledged based on `acks` setting:
  - `acks=0` (fire and forget — may lose data)
  - `acks=1` (leader writes to log — may lose if leader crashes before replication)
  - `acks=all` (all in-sync replicas acknowledge — no loss)

For zero message loss: `acks=all`, `min.insync.replicas=2`, replication factor 3. On broker crash, a follower in the ISR set is elected as leader. If all ISRs are down, choose between waiting for an ISR (maintain consistency) or electing an out-of-sync replica (maintain availability) — the `unclean.leader.election.enable` setting.

---

## Interviewer Feedback

**Strengths:** Strong architectural design, practical Kafka knowledge, good understanding of consumer groups  
**Areas to Improve:** Could discuss Schema Registry and Avro serialization  
**Verdict:** Strong Hire

---

*Lab 07 MOCK_INTERVIEW.md — Part of Backend Academy Interview Preparation*
