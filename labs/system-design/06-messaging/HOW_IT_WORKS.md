# Messaging - HOW IT WORKS

## Kafka Producer

### Basic Producer
```java
@Configuration
public class KafkaProducerConfig {
    @Bean
    public ProducerFactory<String, Order> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all");  // strongest durability
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, Order> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
```

### Sending Messages
```java
@Service
public class OrderEventPublisher {
    private final KafkaTemplate<String, Order> kafka;

    public OrderEventPublisher(KafkaTemplate<String, Order> kafka) {
        this.kafka = kafka;
    }

    public void orderCreated(Order order) {
        kafka.send("order-events", order.getCustomerId(), order);
    }

    public void orderShipped(Order order) {
        kafka.send("order-events", order.getCustomerId(),
            new ShipmentEvent(order.getId(), order.getStatus()));
    }
}
```

## Kafka Consumer

### Basic Consumer
```java
@Configuration
public class KafkaConsumerConfig {
    @Bean
    public ConsumerFactory<String, Order> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-processors");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }
}
```

### Consuming Messages
```java
@Component
public class OrderEventConsumer {
    private final OrderService orderService;
    private final PaymentService paymentService;

    public OrderEventConsumer(OrderService orderService, PaymentService paymentService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = "order-events", groupId = "order-processors")
    public void processOrder(Order order, Acknowledgment ack) {
        try {
            orderService.validate(order);
            paymentService.processPayment(order);
            ack.acknowledge();  // manual offset commit
        } catch (Exception e) {
            // send to dead letter queue
            kafkaTemplate.send("order-events-dlq", order);
        }
    }
}
```

## RabbitMQ

### Producer
```java
@Autowired
private RabbitTemplate rabbitTemplate;

public void sendOrder(Order order) {
    rabbitTemplate.convertAndSend("order.exchange", "order.created", order);
}
```

### Consumer
```java
@RabbitListener(queues = "order.queue")
public void receiveOrder(Order order) {
    // process order
}
```

## Dead Letter Queue
```java
@KafkaListener(topics = "order-events-dlq")
public void processDeadLetter(Order order) {
    log.error("Processing failed order: {}", order.getId());
    // Manual inspection or alert
    notificationService.alertTeam("Order processing failed", order);
}
```
