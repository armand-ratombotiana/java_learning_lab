# Messaging - COMMON MISTAKES

## 1. Non-Idempotent Consumers
```java
// WRONG: Processing an event twice creates duplicate
public void handlePayment(PaymentEvent event) {
    account.balance += event.getAmount();  // double-processed = double credit
}

// RIGHT: Check idempotency
public void handlePayment(PaymentEvent event) {
    if (processedEvents.contains(event.getId())) return;
    account.balance += event.getAmount();
    processedEvents.add(event.getId());
}
```

## 2. Auto-Commit Without Monitoring
```java
// WRONG: Auto-commit, no offset control
props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 5000);

// RIGHT: Manual commit after successful processing
@KafkaListener(...)
public void consume(Order order, Acknowledgment ack) {
    process(order);
    ack.acknowledge();
}
```

## 3. Ignoring Message Ordering
Messages to the same partition are ordered. Use same key for related messages.

## 4. Too Many Partitions
Each partition requires file handles and memory. More partitions = more rebalancing overhead.

## 5. No Dead Letter Queue
Failed messages are lost forever. Always configure DLQ.

## 6. Synchronous Blocking in Consumers
```java
// WRONG: Blocking in consumer thread
public void consume(Order order) {
    externalApi.call(order);  // blocking 30s → consumer paused
}

// RIGHT: Offload to thread pool
public void consume(Order order) {
    executorService.submit(() -> externalApi.call(order));
}
```

## 7. Message Too Large
Kafka default max message size: 1MB. Exceeding this requires configuration changes.

## 8. Ignoring Schema Evolution
Messages with new fields break old consumers. Use schema registry (Avro, Protobuf).

## 9. Not Configuring Replication
Single-broker Kafka loses data on broker failure. Always use RF >= 3 in production.

## 10. Consumer Group Name Changes
Changing consumer group name restarts consumption from the beginning (or latest).
