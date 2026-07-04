# Messaging - CODE DEEP DIVE

## Table of Contents
1. [Kafka Producer with Idempotency](#kafka-producer)
2. [Kafka Consumer with Exactly-Once](#kafka-consumer)
3. [RabbitMQ Integration](#rabbitmq)
4. [Event Sourcing with Messaging](#event-sourcing)

---

## 1. Kafka Producer with Idempotency <a name="kafka-producer"></a>

### Idempotent Producer Configuration
```java
@Configuration
public class IdempotentKafkaConfig {

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // Idempotent producer
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);

        // Performance tuning
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
```

### Transactional Outbox Pattern
```java
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafka;

    @Transactional
    public Order createOrder(Order order) {
        // 1. Save to database
        Order saved = orderRepository.save(order);

        // 2. Save to outbox (same transaction)
        OutboxEvent event = new OutboxEvent();
        event.setAggregateId(saved.getId());
        event.setEventType("ORDER_CREATED");
        event.setPayload(objectMapper.writeValueAsString(saved));
        outboxRepository.save(event);

        return saved;
    }
}

@Component
public class OutboxRelay {
    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafka;

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void relayOutbox() {
        List<OutboxEvent> events = outboxRepository.findTop100ByProcessedFalseOrderByCreatedAt();
        for (OutboxEvent event : events) {
            kafka.send("order-events", event.getAggregateId(), event.getPayload());
            event.setProcessed(true);
            outboxRepository.save(event);
        }
    }
}
```

### Custom Partition Strategy
```java
@Component
public class CustomerIdPartitioner implements Partitioner {
    private final int numberOfPartitions = 12;

    @Override
    public int partition(String topic, Object key, byte[] keyBytes,
                         Object value, byte[] valueBytes, Cluster cluster) {
        if (key instanceof String) {
            String customerId = (String) key;
            // Ensure same customer always goes to same partition (ordering)
            return Math.abs(customerId.hashCode()) % numberOfPartitions;
        }
        return 0;
    }

    @Override
    public void close() {}

    @Override
    public void configure(Map<String, ?> configs) {}
}
```

---

## 2. Kafka Consumer with Exactly-Once <a name="kafka-consumer"></a>

### Consumer with Manual Offset Management
```java
@Component
public class ExactlyOnceConsumer {
    private final OrderService orderService;
    private final ProcessedEventTracker tracker;

    @KafkaListener(topics = "order-events", groupId = "order-processors")
    public void consume(ConsumerRecord<String, Order> record, Acknowledgment ack) {
        String eventId = extractEventId(record);
        String orderId = record.key();

        // Check for duplicate
        if (tracker.isProcessed(eventId)) {
            ack.acknowledge();  // skip duplicate
            return;
        }

        try {
            // Process within database transaction
            orderService.processOrder(orderId, record.value());

            // Mark as processed (same transaction)
            tracker.markProcessed(eventId);

            ack.acknowledge();
        } catch (Exception e) {
            // Log and send to DLQ
            sendToDeadLetter(record, e);
            ack.acknowledge();  // or don't, depending on strategy
        }
    }

    private String extractEventId(ConsumerRecord<String, Order> record) {
        return record.headers().lastHeader("eventId") != null
            ? new String(record.headers().lastHeader("eventId").value())
            : record.key() + ":" + record.offset();
    }
}
```

### Batch Consumer
```java
@Component
public class BatchOrderConsumer {

    @KafkaListener(topics = "order-events", groupId = "batch-processors")
    public void consumeBatch(List<Order> orders, Acknowledgment ack) {
        if (orders.isEmpty()) return;

        try {
            orderService.processBatch(orders);
            ack.acknowledge();
        } catch (Exception e) {
            // Process one by one to isolate failures
            for (Order order : orders) {
                try {
                    orderService.processOrder(order);
                } catch (Exception ex) {
                    kafkaTemplate.send("order-events-dlq", order);
                }
            }
            ack.acknowledge();
        }
    }
}
```

### Reactive Kafka (Project Reactor + Kafka)
```java
@Component
public class ReactiveOrderProcessor {
    private final ReactiveKafkaReceiver<String, Order> receiver;

    public ReactiveOrderProcessor(ReactiveKafkaReceiver<String, Order> receiver) {
        this.receiver = receiver;
    }

    public void start() {
        receiver.receive()
            .flatMap(record -> processOrder(record.value())
                .then(Mono.fromRunnable(record.receiverOffset()::acknowledge))
                .onErrorResume(e -> {
                    log.error("Failed to process order", e);
                    return sendToDlq(record);
                })
            )
            .subscribe();
    }
}
```

---

## 3. RabbitMQ Integration <a name="rabbitmq"></a>

### RabbitMQ Configuration
```java
@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue orderQueue() {
        return QueueBuilder.durable("order.queue")
            .deadLetterExchange("dlx.exchange")
            .deadLetterRoutingKey("order.dlq")
            .ttl(30000)  // 30s TTL
            .maxLength(100000)
            .build();
    }

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange("order.exchange");
    }

    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(orderQueue())
            .to(orderExchange())
            .with("order.#");  // order.created, order.shipped, etc.
    }

    // Dead letter configuration
    @Bean
    public Queue deadLetterQueue() {
        return new Queue("order.dlq");
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("dlx.exchange");
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
            .to(deadLetterExchange())
            .with("order.dlq");
    }
}
```

### RabbitMQ Producer with Confirms
```java
@Service
public class OrderMessagePublisher {
    private final RabbitTemplate rabbitTemplate;

    public OrderMessagePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.error("Message send failed: {}", cause);
            }
        });
    }

    public void publishOrderCreated(Order order) {
        CorrelationData cd = new CorrelationData(order.getId());
        rabbitTemplate.convertAndSend("order.exchange", "order.created", order, cd);
    }
}
```

### RabbitMQ Consumer with Retry
```java
@Component
public class OrderMessageConsumer {
    private static final int MAX_RETRIES = 3;

    @RabbitListener(queues = "order.queue")
    public void handleOrder(Order order, Message message, Channel channel) {
        try {
            processOrder(order);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            long retryCount = getRetryCount(message);
            if (retryCount < MAX_RETRIES) {
                // Retry: reject and requeue
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),
                    false, true);
            } else {
                // Send to DLQ
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),
                    false, false);
            }
        }
    }

    private long getRetryCount(Message message) {
        Long count = message.getMessageProperties()
            .getHeader("x-retry-count");
        return count == null ? 0 : count;
    }
}
```

---

## 4. Event Sourcing with Messaging <a name="event-sourcing"></a>

### Event Store Implementation
```java
@Component
public class KafkaEventStore {
    private final KafkaTemplate<String, Object> kafka;
    private static final String STORE_TOPIC = "event-store";

    public KafkaEventStore(KafkaTemplate<String, Object> kafka) {
        this.kafka = kafka;
    }

    public void saveEvent(String aggregateId, Object event) {
        kafka.send(STORE_TOPIC, aggregateId, event);
    }

    public List<Object> getEvents(String aggregateId) {
        // In production, read from compacted topic or database
        // This demonstrates the concept
        return List.of();
    }
}

// Order aggregate that applies events
public class OrderAggregate {
    private String id;
    private String status;
    private BigDecimal total;
    private List<Object> changes = new ArrayList<>();

    public OrderAggregate(List<Object> pastEvents) {
        pastEvents.forEach(this::apply);
    }

    public void createOrder(String customerId, BigDecimal total) {
        applyChange(new OrderCreatedEvent(id, customerId, total));
    }

    public void confirmOrder() {
        applyChange(new OrderConfirmedEvent(id));
    }

    private void applyChange(Object event) {
        apply(event);
        changes.add(event);
    }

    private void apply(Object event) {
        if (event instanceof OrderCreatedEvent) {
            OrderCreatedEvent e = (OrderCreatedEvent) event;
            this.id = e.getOrderId();
            this.status = "CREATED";
            this.total = e.getTotal();
        } else if (event instanceof OrderConfirmedEvent) {
            this.status = "CONFIRMED";
        }
    }

    public List<Object> getUncommittedChanges() {
        return changes;
    }
}
```

---

## Summary

This deep dive covered:
1. **Kafka Producer**: Idempotent configuration, transactional outbox, custom partitioning
2. **Kafka Consumer**: Exactly-once processing, batch consumption, reactive streams
3. **RabbitMQ**: Exchange/queue binding, publisher confirms, retry with DLQ
4. **Event Sourcing**: Event store on Kafka, aggregate reconstruction from events
