# Mock Interview: Kafka Streaming (06-kafka-streaming)

## Scenario: Design a payment processing system with Kafka
Your fintech company needs a payment processing pipeline handling 10K transactions/sec with exactly-once semantics and sub-second latency. Payment events include: payment_id, user_id, merchant_id, amount, currency, timestamp.

### Time: 45 minutes | Difficulty: Hard

---

## Part 1: Kafka Topic Design (15 min)

**Topic design decisions:**

| Parameter | Value | Rationale |
|-----------|-------|-----------|
| Topic name | `payments` | Single topic for all payment events |
| Partitions | 16 | Throughput: 10K msg/s / partition throughput (typically ~5K msg/s) = 2. Need 4x for headroom and parallelism |
| Replication factor | 3 | Fault tolerance, broker failure resilience |
| Retention | 7 days | Replay needed for audits, but not infinite |
| Cleanup policy | `compact,delete` | Compact by payment_id, delete after 7 days |
| Min ISR | 2 | Acks=all requires at least 2 in-sync replicas |

**Partition key:** `payment_id` (ensures ordering guarantee per payment)

**Partition strategy:**
- Hash of payment_id → consistent partition assignment
- Ensures all events for same payment go to same partition
- Needed for exactly-once processing per payment

**Producer configuration:**
```properties
bootstrap.servers=broker1:9092,broker2:9092,broker3:9092
key.serializer=org.apache.kafka.common.serialization.StringSerializer
value.serializer=io.confluent.kafka.serializers.KafkaAvroSerializer
acks=all
enable.idempotence=true
max.in.flight.requests.per.connection=5
retries=2147483647
delivery.timeout.ms=120000
compression.type=zstd
```

---

## Part 2: Exactly-Once Semantics (10 min)

**End-to-end exactly-once configuration:**

**Producer side:**
1. `enable.idempotence=true` - avoids duplicates within producer session
2. `acks=all` - waits for all ISRs to acknowledge
3. Retry with idempotent producer: if producer retries, broker deduplicates via producer ID + sequence number

**Consumer side:**
1. `isolation.level=read_committed` - only read committed messages
2. Process-then-commit: process message, then commit offset (not auto-commit)
3. Idempotent sink: database writes check `payment_id` before insert

**Transaction API (for exactly-once across topics):**
```java
producer.initTransactions();
try {
    producer.beginTransaction();
    producer.send(new ProducerRecord<>("payments", paymentId, payment));
    producer.send(new ProducerRecord<>("payment-audit", paymentId, audit));
    producer.commitTransaction();
} catch (Exception e) {
    producer.abortTransaction();
}
```

**Consumer exactly-once:**
```java
consumer.subscribe(Arrays.asList("payments"));
while (true) {
    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
    for (ConsumerRecord<String, String> record : records) {
        processPayment(record.value());  // Idempotent processing
        consumer.commitSync();  // Commit after successful processing
    }
}
```

**Read-committed vs read-uncommitted:**
- Read-committed: Only see committed transactions (no duplicates, but higher latency)
- Read-uncommitted: See all messages including uncommitted (lower latency, may see duplicates)

---

## Part 3: Kafka Streams Topology (10 min)

**Payment processing topology:**

```
KStream (payments topic)
    │
    ├── flatMapValues (validate payment: amount > 0, valid currency, valid merchant)
    │     ├── Pass → continue
    │     └── Fail → to("payment-dead-letter")
    │
    ├── join with KTable (merchants table) → enrich merchant name, category
    │
    ├── join with KTable (users table) → enrich user tier, risk score
    │
    ├── process (fraud detection)
    │     ├── State store: user_transaction_counts (windowed 1 hour)
    │     ├── State store: user_total_amount (windowed 24 hours)
    │     └── Rule: > 3 transactions in 1 hour → flag for review
    │
    ├── branch (routing)
    │     ├── Low risk (amount < 1000, user trust score > 0.8) → process immediately
    │     ├── Medium risk → manual review queue
    │     └── High risk → block and alert
    │
    └── to KTable (payment-status) → latest status per payment
```

**State stores:**
```java
// Windowed state for fraud detection
StoreBuilder<WindowStore<String, Long>> transactionCountStore =
    Stores.windowStoreBuilder(
        Stores.persistentWindowStore("txn-count-store", 
            Duration.ofHours(24),  // retain 24 hours
            Duration.ofMinutes(10), // window size
            false),  // not key-value
        Serdes.String(),
        Serdes.Long());
```

**Consumer group configuration:**
```properties
group.id=payment-processor-v2
enable.auto.commit=false
auto.offset.reset=earliest
max.poll.records=500
max.poll.interval.ms=300000
session.timeout.ms=45000
heartbeat.interval.ms=15000
partition.assignment.strategy=org.apache.kafka.clients.consumer.CooperativeStickyAssignor
```

---

## Part 4: Failure Handling & Monitoring (10 min)

**Consumer crash recovery:**
1. Consumer leaves group (heartbeat timeout)
2. Rebalancing triggered (cooperative sticky: rebalances only affected partitions)
3. Remaining consumers reassume partitions
4. New consumer picks up from last committed offset
5. Process remaining messages from that offset (exactly-once with idempotent sink)

**Dead letter queue design:**
```java
// Failed messages route to DLQ
KStream<String, Payment> validPayments = payments.flatMapValues((key, payment) -> {
    try {
        validatePayment(payment);
        return Collections.singletonList(payment);
    } catch (ValidationException e) {
        // Send to DLQ with error info
        dlqTopicProducer.send(new ProducerRecord<>("payment-dlq", payment.paymentId,
            new PaymentError(payment, e.getMessage(), System.currentTimeMillis())));
        return Collections.emptyList();
    }
});
```

**DLQ monitoring:**
- DLQ topic partition count = number of consumers processing DLQ
- Error types: validation_error, enrichment_error, fraud_check_error
- Alert when DLQ size > 1000 messages in 5 minutes
- Manual replay: re-process messages from DLQ after fixing the issue

**Kafka monitoring metrics:**
```properties
# Consumer lag (most important)
kafka.consumer:type=consumer-fetch-manager-metrics,topic=payments
# Under-replicated partitions (cluster health)
kafka.server:type=ReplicaManager,name=UnderReplicatedPartitions
# Request metrics
kafka.network:type=RequestMetrics,name=TotalTimeMs,request=Produce
# Partition metrics
kafka.log:type=Log,name=LogEndOffset,topic=payments
```

**Consumer lag alert thresholds:**
- Warning: lag > 10,000 (30 seconds at 10K msg/s)
- Critical: lag > 100,000 (5 minutes behind)
- Auto-scaling: add consumers if lag persists > 1 minute

---

## Follow-up Questions

**Idempotent producer:**
- Producer assigns Producer ID (PID) + sequence number to each message
- Broker dedup: if same (PID, partition, sequence) received, returns success without writing
- Guarantees no duplicates within producer session
- Combined with `acks=all` and retries = idempotence

**Cooperative vs eager rebalancing:**
| Strategy | Behavior | Impact |
|----------|----------|--------|
| Eager (RangeAssignor) | Stop all consumers, rebalance, restart | Stop-the-world, lag spike |
| Cooperative Sticky | Rebalance a few partitions at a time | Minimal disruption |
| Choose | Cooperative sticky for large consumer groups | Less lag, smoother operation |

**Consumer rebalancing triggers:**
1. Consumer joins/leaves group
2. Topic partition count changes
3. Subscription pattern changes
4. Session timeout (heartbeat not received)

**Kafka monitoring dashboard:**
- Topics: partition count, replication factor, leader distribution
- Producers: request rate, error rate, compression ratio
- Consumers: consumer lag per partition, rebalance rate, processing time
- Brokers: disk usage, network I/O, GC pause time

