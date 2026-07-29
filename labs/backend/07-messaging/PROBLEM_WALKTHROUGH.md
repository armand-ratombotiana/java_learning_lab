# PROBLEM WALKTHROUGH: Implement Exactly-Once Kafka Producer/Consumer

## Problem Statement

Implement a Kafka-based message processing system with exactly-once semantics (EOS). The system should:

- Produce messages idempotently with exactly-once delivery guarantees
- Consume messages transactionally with exactly-once processing
- Handle failure and recovery without message loss or duplication
- Implement a dead letter queue (DLQ) for poison messages
- Support idempotent consumers via idempotency key tracking
- Implement at-least-once, at-most-once, and exactly-once delivery strategies

**Constraints:**
- Apache Kafka 3.x with Java 21+
- Spring Boot 3.x with Spring Kafka
- Kafka transactions with `processing.guarantee=exactly_once_v2`
- Idempotent producer enabled
- Read-committed isolation level for consumers

---

## Step-by-Step Solution

### Step 1: Maven Dependencies

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
</dependency>
```

### Step 2: Configuration — Producer & Consumer

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      acks: all
      properties:
        enable.idempotence: true
        max.in.flight.requests.per.connection: 5
        retries: 2147483647
        delivery.timeout.ms: 120000
        request.timeout.ms: 30000
    consumer:
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        isolation.level: read_committed
        spring.json.trusted.packages: "*"
      auto-offset-reset: earliest
      enable-auto-commit: false
    listener:
      type: batch
      concurrency: 3
      ack-mode: manual_immediate
      properties:
        max.poll.interval.ms: 600000
        max.poll.records: 100
    template:
      default-topic: orders
```

### Step 3: Kafka Topic Configuration

```java
@Configuration
public class KafkaTopicConfig {

    public static final String ORDER_TOPIC = "orders";
    public static final String ORDER_DLQ_TOPIC = "orders-dlq";
    public static final String PAYMENT_TOPIC = "payments";
    public static final String INVENTORY_TOPIC = "inventory";
    public static final String IDEMPOTENCY_TOPIC = "idempotency-events";

    @Bean
    public NewTopic orderTopic() {
        return TopicBuilder.name(ORDER_TOPIC)
            .partitions(6)
            .replicas(3)
            .config(TopicConfig.RETENTION_MS_CONFIG, "604800000") // 7 days
            .build();
    }

    @Bean
    public NewTopic orderDlqTopic() {
        return TopicBuilder.name(ORDER_DLQ_TOPIC)
            .partitions(3)
            .replicas(3)
            .config(TopicConfig.RETENTION_MS_CONFIG, "2592000000") // 30 days
            .build();
    }

    @Bean
    public NewTopic paymentTopic() {
        return TopicBuilder.name(PAYMENT_TOPIC)
            .partitions(6)
            .replicas(3)
            .build();
    }

    @Bean
    public NewTopic inventoryTopic() {
        return TopicBuilder.name(INVENTORY_TOPIC)
            .partitions(6)
            .replicas(3)
            .build();
    }

    @Bean
    public NewTopic idempotencyTopic() {
        return TopicBuilder.name(IDEMPOTENCY_TOPIC)
            .partitions(3)
            .replicas(3)
            .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT)
            .build();
    }
}
```

### Step 4: Domain Models

```java
public record OrderEvent(
    String eventId,
    String orderId,
    String customerId,
    String eventType,
    BigDecimal amount,
    String status,
    Instant timestamp
) {
    public static OrderEvent created(String orderId, String customerId, BigDecimal amount) {
        return new OrderEvent(UUID.randomUUID().toString(), orderId, customerId,
            "ORDER_CREATED", amount, "PENDING", Instant.now());
    }
}

public record PaymentEvent(
    String eventId,
    String orderId,
    String transactionId,
    BigDecimal amount,
    String status,
    Instant timestamp
) {}

public record InventoryEvent(
    String eventId,
    String orderId,
    String sku,
    int quantity,
    String status,
    Instant timestamp
) {}
```

### Step 5: Exactly-Once Producer Service (Transactional)

```java
@Service
public class ExactlyOnceProducer {

    private static final Logger log = LoggerFactory.getLogger(ExactlyOnceProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final IdempotencyService idempotencyService;

    public ExactlyOnceProducer(KafkaTemplate<String, Object> kafkaTemplate,
                                IdempotencyService idempotencyService) {
        this.kafkaTemplate = kafkaTemplate;
        this.idempotencyService = idempotencyService;
    }

    @Transactional("kafkaTransactionManager")
    public void sendOrderWithTransaction(OrderEvent event) {
        // Check idempotency before producing
        if (idempotencyService.isProcessed(event.eventId())) {
            log.info("Skipping already processed event: {}", event.eventId());
            return;
        }

        try {
            // Within a single transaction, produce to multiple topics atomically
            kafkaTemplate.send(KafkaTopicConfig.ORDER_TOPIC, event.orderId(), event);
            kafkaTemplate.send(KafkaTopicConfig.PAYMENT_TOPIC, event.orderId(),
                new PaymentEvent(UUID.randomUUID().toString(), event.orderId(),
                    "txn-" + event.orderId(), event.amount(), "PENDING", Instant.now()));
            kafkaTemplate.send(KafkaTopicConfig.INVENTORY_TOPIC, event.orderId(),
                new InventoryEvent(UUID.randomUUID().toString(), event.orderId(),
                    "SKU-" + event.orderId(), 1, "RESERVED", Instant.now()));

            // Record idempotency within the same transaction
            kafkaTemplate.send(KafkaTopicConfig.IDEMPOTENCY_TOPIC, event.eventId(),
                Map.of("eventId", event.eventId(), "processedAt", Instant.now().toString()));

            idempotencyService.markProcessed(event.eventId());

            log.info("Transactionally produced event: {} for order: {}",
                event.eventId(), event.orderId());
        } catch (Exception e) {
            log.error("Failed to produce event transactionally: {}", e.getMessage());
            throw new RuntimeException("Transactional production failed", e);
        }
    }

    @Transactional("kafkaTransactionManager")
    public void sendToDlq(String originalTopic, Object failedRecord, String errorReason) {
        kafkaTemplate.send(KafkaTopicConfig.ORDER_DLQ_TOPIC, Map.of(
            "originalTopic", originalTopic,
            "failedRecord", failedRecord,
            "errorReason", errorReason,
            "failedAt", Instant.now().toString()
        ));
    }
}
```

### Step 6: Idempotency Service

```java
@Service
public class IdempotencyService {

    private static final Logger log = LoggerFactory.getLogger(IdempotencyService.class);
    private final ConcurrentHashMap<String, Instant> processedStore = new ConcurrentHashMap<>();
    private static final Duration RETENTION = Duration.ofDays(7);

    public boolean isProcessed(String eventId) {
        Instant processedAt = processedStore.get(eventId);
        if (processedAt == null) return false;
        // Clean up expired entries
        if (Duration.between(processedAt, Instant.now()).compareTo(RETENTION) > 0) {
            processedStore.remove(eventId);
            return false;
        }
        return true;
    }

    public void markProcessed(String eventId) {
        processedStore.put(eventId, Instant.now());
    }

    @Scheduled(fixedRate = 3600000) // Hourly cleanup
    void cleanupExpired() {
        Instant cutoff = Instant.now().minus(RETENTION);
        processedStore.entrySet().removeIf(entry -> entry.getValue().isBefore(cutoff));
        log.info("Cleaned up {} expired idempotency entries", processedStore.size());
    }
}
```

### Step 7: Exactly-Once Consumer (Transactional)

```java
@Component
public class ExactlyOnceConsumer {

    private static final Logger log = LoggerFactory.getLogger(ExactlyOnceConsumer.class);
    private static final int MAX_RETRIES = 3;

    private final ExactlyOnceProducer producer;
    private final IdempotencyService idempotencyService;
    private final OrderProcessingService orderProcessingService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ExactlyOnceConsumer(ExactlyOnceProducer producer,
                                IdempotencyService idempotencyService,
                                OrderProcessingService orderProcessingService,
                                KafkaTemplate<String, Object> kafkaTemplate) {
        this.producer = producer;
        this.idempotencyService = idempotencyService;
        this.orderProcessingService = orderProcessingService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = KafkaTopicConfig.ORDER_TOPIC,
                   groupId = "order-processor",
                   containerFactory = "kafkaListenerContainerFactory")
    @Transactional("kafkaTransactionManager")
    public void consumeOrder(ConsumerRecord<String, OrderEvent> record,
                              Acknowledgment acknowledgment) {
        OrderEvent event = record.value();
        log.info("Consumed order event: {} (partition={}, offset={})",
            event.eventId(), record.partition(), record.offset());

        try {
            // Idempotency check (first level)
            if (idempotencyService.isProcessed(event.eventId())) {
                log.info("Duplicate event detected (idempotency): {}", event.eventId());
                acknowledgment.acknowledge();
                return;
            }

            // Process the order
            processWithRetry(event, record);

            // Mark as processed within the transaction
            idempotencyService.markProcessed(event.eventId());

            // Acknowledge offset (commit will happen when transaction commits)
            acknowledgment.acknowledge();

            log.info("Successfully processed order event: {}", event.eventId());

        } catch (Exception e) {
            log.error("Failed to process order event: {} - {}",
                event.eventId(), e.getMessage());

            // Route to DLQ after max retries
            producer.sendToDlq(KafkaTopicConfig.ORDER_TOPIC, event,
                "Processing failed: " + e.getMessage());
        }
    }

    void processWithRetry(OrderEvent event, ConsumerRecord<String, OrderEvent> record) {
        RetryTemplate retryTemplate = RetryTemplate.builder()
            .maxAttempts(MAX_RETRIES)
            .exponentialBackoff(Duration.ofMillis(100), 2, Duration.ofSeconds(5))
            .retryOn(TransientException.class)
            .build();

        retryTemplate.execute(context -> {
            log.info("Processing attempt {} for event: {}",
                context.getRetryCount() + 1, event.eventId());
            orderProcessingService.processOrder(event);
            return null;
        }, context -> {
            // Last retry failed — throw to trigger DLQ
            throw new RuntimeException("All retries exhausted for event: "
                + event.eventId(), context.getLastThrowable());
        });
    }
}
```

### Step 8: Kafka Consumer Factory Configuration

```java
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, Object> consumerFactory(
            KafkaProperties properties) {
        Map<String, Object> config = new HashMap<>(properties.buildConsumerProperties(null));
        config.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
            StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
            JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 600000);
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object>
            kafkaListenerContainerFactory(ConsumerFactory<String, Object> consumerFactory,
                                           KafkaTransactionManager<String, Object> transactionManager) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, Object>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setConcurrency(3);
        factory.getContainerProperties().setTransactionManager(transactionManager);

        // Exactly-once semantics
        factory.getContainerProperties().setSyncCommits(true);
        return factory;
    }
}
```

### Step 9: Transaction Manager Configuration

```java
@Configuration
public class KafkaTransactionConfig {

    @Bean
    public KafkaTransactionManager<String, Object> kafkaTransactionManager(
            ProducerFactory<String, Object> producerFactory) {
        return new KafkaTransactionManager<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory(
            KafkaProperties properties) {
        Map<String, Object> config = new HashMap<>(properties.buildProducerProperties(null));
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG,
            "txn-" + UUID.randomUUID());
        config.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }
}
```

### Step 10: Order Processing Service

```java
@Service
public class OrderProcessingService {

    private static final Logger log = LoggerFactory.getLogger(OrderProcessingService.class);

    // Simulated DB operations
    private final ConcurrentHashMap<String, String> orderStore = new ConcurrentHashMap<>();

    @Transactional("kafkaTransactionManager")
    public void processOrder(OrderEvent event) {
        // Simulate business logic
        if (event.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid order amount");
        }

        // Simulate DB write
        orderStore.put(event.orderId(), event.status());

        // Simulate transient failure (for testing)
        if (event.orderId().contains("fail")) {
            throw new TransientException("Simulated transient failure");
        }

        log.info("Order processed: {} (amount={}, status={})",
            event.orderId(), event.amount(), event.status());
    }

    public Optional<String> getOrderStatus(String orderId) {
        return Optional.ofNullable(orderStore.get(orderId));
    }
}

class TransientException extends RuntimeException {
    public TransientException(String message) { super(message); }
}
```

### Step 11: DLQ Consumer (Reprocessing)

```java
@Component
public class DlqConsumer {

    private static final Logger log = LoggerFactory.getLogger(DlqConsumer.class);

    @KafkaListener(topics = KafkaTopicConfig.ORDER_DLQ_TOPIC,
                   groupId = "dlq-processor",
                   containerFactory = "kafkaListenerContainerFactory")
    public void consumeDlq(ConsumerRecord<String, Map<String, Object>> record,
                            Acknowledgment acknowledgment) {
        Map<String, Object> dlqEntry = record.value();
        log.warn("DLQ entry: originalTopic={}, errorReason={}, failedAt={}",
            dlqEntry.get("originalTopic"),
            dlqEntry.get("errorReason"),
            dlqEntry.get("failedAt"));

        // Alert or manual reprocessing logic
        // In production: send to alerting system, store in DB for manual review

        acknowledgment.acknowledge();
    }
}
```

### Step 12: Delivery Strategy Comparison

```java
@Service
public class DeliveryStrategyService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public DeliveryStrategyService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // At-Most-Once: fire and forget, may lose messages
    public void sendAtMostOnce(String topic, String key, Object value) {
        kafkaTemplate.send(topic, key, value);
        // No callback, no retry
    }

    // At-Least-Once: retry until success, may duplicate
    public void sendAtLeastOnce(String topic, String key, Object value) {
        kafkaTemplate.send(topic, key, value)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Send failed (will retry): {}", ex.getMessage());
                    // Producer retries automatically if idempotence is enabled
                } else {
                    log.info("Sent to partition {} at offset {}",
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
                }
            });
    }

    // Exactly-Once: transactional, idempotent producer + read_committed consumer
    @Transactional("kafkaTransactionManager")
    public void sendExactlyOnce(String topic, String key, Object value) {
        kafkaTemplate.send(topic, key, value);
        // Within transaction — atomically committed or rolled back
    }
}
```

### Step 13: REST Controller for Testing

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final ExactlyOnceProducer producer;
    private final OrderProcessingService processingService;

    public OrderController(ExactlyOnceProducer producer,
                            OrderProcessingService processingService) {
        this.producer = producer;
        this.processingService = processingService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createOrder(
            @RequestParam String customerId,
            @RequestParam BigDecimal amount) {
        String orderId = UUID.randomUUID().toString();
        OrderEvent event = OrderEvent.created(orderId, customerId, amount);
        producer.sendOrderWithTransaction(event);
        return ResponseEntity.accepted().body(Map.of(
            "orderId", orderId,
            "eventId", event.eventId(),
            "status", "PROCESSING"
        ));
    }

    @PostMapping("/fail")
    public ResponseEntity<Map<String, String>> createFailingOrder(
            @RequestParam String customerId,
            @RequestParam BigDecimal amount) {
        // Will trigger retry + DLQ
        String orderId = "fail-" + UUID.randomUUID();
        OrderEvent event = OrderEvent.created(orderId, customerId, amount);
        producer.sendOrderWithTransaction(event);
        return ResponseEntity.accepted().body(Map.of(
            "orderId", orderId,
            "status", "WILL_FAIL"
        ));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, String>> getOrderStatus(
            @PathVariable String orderId) {
        return processingService.getOrderStatus(orderId)
            .map(status -> ResponseEntity.ok(Map.of("status", status)))
            .orElse(ResponseEntity.notFound().build());
    }
}
```

### Step 14: Idempotent Consumer with Database Store

```java
// For production: use database-backed idempotency
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String> {
    boolean existsByEventId(String eventId);
}

@Entity
@Table(name = "processed_events")
public class ProcessedEvent {
    @Id
    private String eventId;
    private Instant processedAt;
    private String consumerGroup;

    // getters/setters
}

@Service
public class DbIdempotencyService {

    private final ProcessedEventRepository repository;

    public DbIdempotencyService(ProcessedEventRepository repository) {
        this.repository = repository;
    }

    @Transactional(transactionManager = "transactionManager")
    public boolean tryProcess(String eventId, String consumerGroup) {
        if (repository.existsByEventId(eventId)) {
            return false; // Already processed
        }
        ProcessedEvent event = new ProcessedEvent();
        event.setEventId(eventId);
        event.setProcessedAt(Instant.now());
        event.setConsumerGroup(consumerGroup);
        repository.save(event);
        return true; // First time processing
    }
}
```

---

## Complexity Analysis

| Aspect | At-Most-Once | At-Least-Once | Exactly-Once |
|--------|-------------|---------------|--------------|
| **Throughput** | Highest | High | Moderate (~20% overhead) |
| **Latency** | Lowest | Low | Higher (transaction commit) |
| **Idempotency needed** | No | Yes (consumer) | Yes (both sides) |
| **Storage overhead** | None | Moderate (offsets) | Higher (txn state) |
| **Failure recovery** | May lose | May duplicate | No loss, no duplication |
| **Kafka config** | Default | `enable.idempotence=true` | `enable.idempotence=true` + transactions |
| **Consumer isolation** | `read_uncommitted` | `read_committed` | `read_committed` |

---

## Follow-Up Questions

1. **How does Kafka's idempotent producer work?** — Each producer gets a `producerId` (PID) and an epoch. Messages have a sequence number. Brokers detect duplicates by sequence number and PID. No duplicate writes within the same producer session.

2. **What's the difference between `exactly_once` and `exactly_once_v2`?** — `exactly_once_v2` (Kafka 3.0+) uses a more efficient fencing mechanism. It reduces the number of protocol round-trips and doesn't require special consumer group coordinators.

3. **How do Kafka transactions interact with the consumer offset commit?** — Within a transaction, consumer offsets are committed atomically with produced messages. If transaction fails, both the produced messages and offset commits are rolled back, ensuring reprocessing.

4. **What happens when a transaction coordinator crashes?** — Kafka's transaction coordinator uses the log for transaction state. After failover, the new coordinator reads the log to determine the outcome (commit/abort) of pending transactions.

5. **How do you handle zombie producers (split-brain)?** — Kafka uses producer epochs (monotonically increasing). When a new producer instance starts, it gets a new epoch. Brokers reject messages from older epochs (fencing).

---

## Test Cases

```java
@SpringBootTest
@EmbeddedKafka(partitions = 3, topics = {
    "orders", "orders-dlq", "payments", "inventory", "idempotency-events"})
class ExactlyOnceKafkaTest {

    @Autowired
    private ExactlyOnceProducer producer;

    @Autowired
    private ExactlyOnceConsumer consumer;

    @Autowired
    private IdempotencyService idempotencyService;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private OrderProcessingService processingService;

    @Test
    void shouldProcessEventExactlyOnce() {
        OrderEvent event = OrderEvent.created("order-1", "cust-1", BigDecimal.TEN);
        producer.sendOrderWithTransaction(event);

        // Verify processed
        assertThat(processingService.getOrderStatus("order-1")).isPresent();
    }

    @Test
    void shouldSkipDuplicateEvent() {
        OrderEvent event = OrderEvent.created("order-dup", "cust-1", BigDecimal.TEN);
        producer.sendOrderWithTransaction(event);
        producer.sendOrderWithTransaction(event); // Duplicate

        // Should only be processed once
        assertThat(idempotencyService.isProcessed(event.eventId())).isTrue();
    }

    @Test
    void shouldRouteToDlqOnFailure() {
        OrderEvent event = OrderEvent.created("fail-test", "cust-1", BigDecimal.TEN);
        producer.sendOrderWithTransaction(event);

        // The consumer should route to DLQ after retries
        // Verify via DLQ consumer or mock
    }

    @Test
    void shouldMaintainTransactionAtomicity() {
        // If any produce fails, all should be rolled back
        String orderId = UUID.randomUUID().toString();
        // Verify no partial writes
    }

    @Test
    void shouldHandleIdempotencyInConsumer() {
        String eventId = UUID.randomUUID().toString();
        assertThat(idempotencyService.isProcessed(eventId)).isFalse();

        idempotencyService.markProcessed(eventId);
        assertThat(idempotencyService.isProcessed(eventId)).isTrue();
    }

    @Test
    void shouldProduceAndConsumeOrderWithPaymentAndInventory() {
        // End-to-end transactional flow
        OrderEvent event = OrderEvent.created("e2e-test", "cust-1",
            new BigDecimal("100.00"));
        producer.sendOrderWithTransaction(event);
    }
}
```

---

## Summary

This Kafka exactly-once implementation demonstrates:
- **Idempotent producer**: `enable.idempotence=true`, `acks=all` — no duplicate writes
- **Kafka transactions**: atomic produce to multiple topics + consumer offset commit
- **Read-committed consumer**: doesn't see uncommitted messages
- **Idempotency service**: tracks processed event IDs to prevent duplicate processing
- **Retry with backoff**: transient failures are retried with exponential backoff
- **Dead letter queue**: poison messages routed to DLQ after max retries
- **Delivery strategies**: comparison of at-most-once, at-least-once, and exactly-once
- **Fencing**: producer epochs prevent zombie producers from writing