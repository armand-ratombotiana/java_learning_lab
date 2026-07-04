# Messaging - STEP BY STEP

## Setting Up Kafka Producer

### Step 1: Add Dependencies
```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

### Step 2: Configure Producer
```yaml
spring.kafka.producer:
  bootstrap-servers: localhost:9092
  key-serializer: org.apache.kafka.common.serialization.StringSerializer
  value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
  properties:
    acks: all
    retries: 3
    enable.idempotence: true
```

### Step 3: Create KafkaTemplate Bean
```java
@Bean
public KafkaTemplate<String, Order> kafkaTemplate(ProducerFactory<String, Order> pf) {
    return new KafkaTemplate<>(pf);
}
```

### Step 4: Send Messages
```java
@Service
public class OrderPublisher {
    public void publish(Order order) {
        kafkaTemplate.send("orders", order.getId(), order);
    }
}
```

## Setting Up Kafka Consumer

### Step 1: Configure Consumer
```yaml
spring.kafka.consumer:
  bootstrap-servers: localhost:9092
  group-id: order-group
  key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
  value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
  properties:
    spring.json.trusted.packages: "*"
    auto.offset.reset: earliest
    enable.auto.commit: false
```

### Step 2: Create Listener
```java
@Component
public class OrderConsumer {
    @KafkaListener(topics = "orders", groupId = "order-group")
    public void onOrder(Order order, Acknowledgment ack) {
        processOrder(order);
        ack.acknowledge();
    }
}
```

## Implementing Message Retry

### Step 1: Add Retry Template
```java
@Bean
public RetryTemplate retryTemplate() {
    return RetryTemplate.builder()
        .maxAttempts(3)
        .exponentialBackoff(1000, 2, 10000)
        .retryOn(TransientException.class)
        .build();
}
```

### Step 2: Use in Consumer
```java
@KafkaListener(topics = "orders")
public void consume(Order order, Acknowledgment ack) {
    retryTemplate.execute(ctx -> {
        processOrder(order);
        return null;
    }, ctx -> {
        sendToDlq(order);
        return null;
    });
    ack.acknowledge();
}
```

## Creating a Dead Letter Queue

### Step 1: Configure DLQ Topic
```yaml
spring.kafka.template:
  default-topic: orders-dlq
```

### Step 2: Send Failed Messages
```java
@KafkaListener(topics = "orders")
public void consume(Order order, Acknowledgment ack) {
    try {
        processOrder(order);
        ack.acknowledge();
    } catch (Exception e) {
        kafkaTemplate.send("orders-dlq", order);
        ack.acknowledge();  // or don't acknowledge for retry
    }
}
```
