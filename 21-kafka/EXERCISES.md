# Kafka - Exercises

---

## Exercise Set 1: Kafka Fundamentals

### Exercise 1.1: Topic Management
**Task**: Create and configure Kafka topics programmatically.

```java
public void createTopics() {
    // Create topics with:
    // - orders: 6 partitions, replication factor 1
    // - payments: 3 partitions, cleanup.policy=compact
    // - events: 6 partitions, retention 7 days
}
```

---

### Exercise 1.2: Basic Producer
**Task**: Implement a reliable order producer.

```java
@Service
public class OrderProducer {
    
    public void sendOrder(Order order) {
        // 1. Serialize order to JSON
        // 2. Send to "orders" topic partitioned by customerId
        // 3. Handle send result asynchronously
        // 4. Log success/failure
    }
}
```

---

### Exercise 1.3: Basic Consumer
**Task**: Implement a consumer that processes orders.

```java
@Service
public class OrderConsumer {
    
    @KafkaListener(topics = "orders", groupId = "order-processor")
    public void processOrder(String message) {
        // 1. Deserialize JSON to Order
        // 2. Process the order
        // 3. Update order status
        // 4. Acknowledge processing
    }
}
```

---

## Exercise Set 2: Producer Patterns

### Exercise 2.1: Idempotent Producer
**Task**: Configure producer for exactly-once semantics.

```java
public ProducerFactory<String, String> idempotentProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
    props.put(ProducerConfig.ACKS_CONFIG, "all");
    props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
    props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
    return new DefaultKafkaProducerFactory<>(props);
}
```

---

### Exercise 2.2: Custom Partitioner
**Task**: Implement a sticky partitioner for low latency.

```java
public class UserBasedPartitioner implements Partitioner {
    
    @Override
    public int partition(String topic, Object key, byte[] keyBytes,
                        Object value, byte[] valueBytes, Cluster cluster) {
        // Partition by user ID hash for ordered processing per user
        // Fall back to sticky partitioner for batching
    }
}
```

---

### Exercise 2.3: Batching Producer
**Task**: Implement producer with optimized batching.

```java
public ProducerFactory<String, String> batchedProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
    props.put(ProducerConfig.LINGER_MS_CONFIG, 10);
    props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
    return new DefaultKafkaProducerFactory<>(props);
}
```

---

## Exercise Set 3: Consumer Patterns

### Exercise 3.1: Manual Offset Commit
**Task**: Implement exactly-once consumer processing.

```java
@KafkaListener(topics = "orders", groupId = "exactly-once")
public void processOrderWithAck(ConsumerRecord<String, String> record, 
                                 Acknowledgment ack) {
    try {
        // Process order
        // Update database in transaction
        // Commit offset only after DB commit
        ack.acknowledge();
    } catch (Exception e) {
        // Don't acknowledge - will reprocess
        throw e;
    }
}
```

---

### Exercise 3.2: Consumer with Retry
**Task**: Implement retry with dead letter queue.

```java
@KafkaListener(topics = "orders", groupId = "order-processor")
public void processOrderWithRetry(String message) {
    try {
        processOrder(message);
    } catch (TransientException e) {
        // Retry with exponential backoff
        throw e; // Will be sent to DLQ after retries exhausted
    }
}

// Configure error handler with retry
@Bean
public CommonErrorHandler errorHandler(KafkaTemplate<String, String> template) {
    return new DefaultErrorHandler(new DeadLetterPublishingRecoverer(template),
        new FixedBackOff(1000L, 3));
}
```

---

### Exercise 3.3: Multi-threaded Consumer
**Task**: Process messages with concurrent threads.

```java
@Bean
public ConcurrentKafkaListenerContainerFactory<String, String> 
    concurrentFactory(ConsumerFactory<String, String> factory) {
    
    ConcurrentKafkaListenerContainerFactory<String, String> factory2 = 
        new ConcurrentKafkaListenerContainerFactory<>();
    factory2.setConsumerFactory(factory);
    factory2.setConcurrency(3); // 3 threads per instance
    return factory2;
}
```

---

## Exercise Set 4: Kafka Streams

### Exercise 4.1: Word Count Stream
**Task**: Implement streaming word count.

```java
public KafkaStreams wordCountStream(StreamsBuilder builder) {
    KStream<String, String> textLines = builder.stream("text-input");
    
    KTable<String, Long> wordCounts = textLines
        .flatMapValues(textLine -> Arrays.asList(textLine.toLowerCase().split("\\W+")))
        .groupBy((key, word) -> KeyValue.pair(word, word))
        .count(Materialized.as("counts-store"));
    
    wordCounts.toStream().to("word-counts-output");
    
    return new KafkaStreams(builder.build(), streamsConfig());
}
```

---

### Exercise 4.2: Windowed Aggregation
**Task**: Count orders per minute per customer.

```java
public KafkaStreams orderCountByMinute(StreamsBuilder builder) {
    KStream<String, Order> orders = builder.stream("orders");
    
    orders
        .filter((key, order) -> order.getAmount().compareTo(BigDecimal.ZERO) > 0)
        .groupBy((key, order) -> KeyValue.pair(
            order.getCustomerId() + "-" + order.getTimestamp().getMinute(),
            order))
        .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(1)))
        .count(Materialized.as("order-counts"))
        .toStream()
        .foreach((key, count) -> 
            System.out.println(key.key() + ": " + count + " orders"));
}
```

---

### Exercise 4.3: Stream Join
**Task**: Join orders with customer information.

```java
public KafkaStreams orderEnrichmentStream(StreamsBuilder builder) {
    KStream<String, Order> orders = builder.stream("orders");
    KTable<String, Customer> customers = builder.table("customers");
    
    orders
        .leftJoin(customers,
            (order, customer) -> enrichOrder(order, customer))
        .to("enriched-orders");
}
```

---

## Exercise Set 5: Schema Registry

### Exercise 5.1: Avro Producer
**Task**: Serialize orders using Avro with Schema Registry.

```java
@Bean
public ProducerFactory<String, Order> avroProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put("schema.registry.url", "http://localhost:8081");
    props.put("value.subject.name.strategy", TopicNameStrategy.class);
    return new DefaultKafkaProducerFactory<>(props);
}
```

---

### Exercise 5.2: Schema Evolution
**Task**: Add a new field with backward compatibility.

```java
// Original schema
{
  "type": "record",
  "name": "Order",
  "fields": [
    {"name": "orderId", "type": "string"},
    {"name": "amount", "type": "double"}
  ]
}

// New schema (backward compatible)
{
  "type": "record",
  "name": "Order",
  "fields": [
    {"name": "orderId", "type": "string"},
    {"name": "amount", "type": "double"},
    {"name": "currency", "type": "string", "default": "USD"}
  ]
}
```

---

## Challenge Problems

### Challenge 1: Exactly-Once E-Commerce
**Difficulty**: Advanced
**Task**: Build an e-commerce system with exactly-once processing.

Requirements:
- Orders published once
- Inventory decremented once
- Payment processed once
- All operations idempotent or transactional

---

### Challenge 2: Real-Time Analytics
**Difficulty**: Advanced
**Task**: Build real-time analytics dashboard.

Requirements:
- Orders per minute
- Revenue per hour
- Top products by category
- Customer activity streaks

---

### Challenge 3: Event Sourcing with Kafka
**Difficulty**: Expert
**Task**: Implement event sourcing using Kafka as event store.

Requirements:
- Append-only event log
- Replay events to rebuild state
- Event versioning and upcasting
- Snapshots for performance

---

## Solutions Guidance

For each exercise:
1. Start with understanding Kafka semantics
2. Implement incrementally, testing each step
3. Use docker-compose for development environment
4. Monitor offsets and consumer lag

---

## Time Estimates

| Exercise | Estimated Time |
|----------|---------------|
| Set 1 | 1-2 hours |
| Set 2 | 2-3 hours |
| Set 3 | 2-3 hours |
| Set 4 | 3-4 hours |
| Set 5 | 2-3 hours |
| Challenges | 8+ hours each |

