# Kafka Module - PROJECTS.md

---

# Mini-Project: Event Streaming System

## Project Overview

**Duration**: 4-5 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Kafka Producer, Kafka Consumer, Topics, Partitions, Consumer Groups, Serializers

This mini-project demonstrates Kafka fundamentals with a message streaming system for order processing.

---

## Project Structure

```
21-kafka/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── producer/
│   │   ├── OrderProducer.java
│   │   └── EventProducer.java
│   ├── consumer/
│   │   ├── OrderConsumer.java
│   │   └── NotificationConsumer.java
│   ├── model/
│   │   ├── Order.java
│   │   └── Notification.java
│   ├── service/
│   │   └── OrderService.java
│   └── config/
│       └── KafkaConfig.java
└── src/main/resources/
    └── application.yml
```

---

## Step 1: POM.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>kafka-demo</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>17</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.30</version>
        </dependency>
    </dependencies>
</project>
```

---

## Step 2: Model Classes

```java
// model/Order.java
package com.learning.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private String orderId;
    private Long customerId;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
    
    public static class OrderItem {
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal price;
        
        public OrderItem() {}
        
        public OrderItem(Long productId, String productName, Integer quantity, BigDecimal price) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
        }
        
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
    }
    
    public Order() {}
    
    public Order(String orderId, Long customerId, List<OrderItem> items, String status) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = items;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.totalAmount = items.stream()
            .map(i -> i.getPrice().multiply(new BigDecimal(i.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
}
```

```java
// model/Notification.java
package com.learning.model;

import java.time.LocalDateTime;

public class Notification {
    private String notificationId;
    private String type;
    private String recipient;
    private String subject;
    private String message;
    private LocalDateTime timestamp;
    
    public Notification() {}
    
    public Notification(String type, String recipient, String subject, String message) {
        this.notificationId = "NOTIF-" + System.currentTimeMillis();
        this.type = type;
        this.recipient = recipient;
        this.subject = subject;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
    
    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
}
```

---

## Step 3: Kafka Configuration

```java
// config/KafkaConfig.java
package com.learning.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;
    
    @Bean
    public NewTopic ordersTopic() {
        return TopicBuilder.name("orders")
            .partitions(3)
            .replicas(1)
            .build();
    }
    
    @Bean
    public NewTopic notificationsTopic() {
        return TopicBuilder.name("notifications")
            .partitions(2)
            .replicas(1)
            .build();
    }
    
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new DefaultKafkaConsumerFactory<>(props);
    }
    
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
```

---

## Step 4: Producers

```java
// producer/OrderProducer.java
package com.learning.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.model.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
public class OrderProducer {
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    public OrderProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
    
    public void sendOrder(Order order) {
        try {
            String orderJson = objectMapper.writeValueAsString(order);
            
            CompletableFuture<SendResult<String, String>> future = 
                kafkaTemplate.send("orders", order.getOrderId(), orderJson);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    System.out.println("Sent order [" + order.getOrderId() + 
                        "] to partition " + result.getRecordMetadata().partition() + 
                        " with offset " + result.getRecordMetadata().offset());
                } else {
                    System.out.println("Unable to send order [" + order.getOrderId() + 
                        "] due to: " + ex.getMessage());
                }
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize order", e);
        }
    }
    
    public void sendOrderStatusUpdate(String orderId, String status) {
        try {
            String message = objectMapper.writeValueAsString(Map.of(
                "orderId", orderId,
                "status", status,
                "timestamp", System.currentTimeMillis()
            ));
            kafkaTemplate.send("order-status", orderId, message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize status", e);
        }
    }
}
```

---

## Step 5: Consumers

```java
// consumer/OrderConsumer.java
package com.learning.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.model.Order;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {
    
    private final ObjectMapper objectMapper;
    private final NotificationProducer notificationProducer;
    
    public OrderConsumer(ObjectMapper objectMapper, NotificationProducer notificationProducer) {
        this.objectMapper = objectMapper;
        this.notificationProducer = notificationProducer;
    }
    
    @KafkaListener(topics = "orders", groupId = "order-processing-group")
    public void processOrder(String message) {
        try {
            Order order = objectMapper.readValue(message, Order.class);
            
            System.out.println("Processing order: " + order.getOrderId());
            System.out.println("Customer: " + order.getCustomerId());
            System.out.println("Total: $" + order.getTotalAmount());
            
            order.setStatus("PROCESSED");
            
            notificationProducer.sendNotification(
                "ORDER_CONFIRMATION",
                "customer" + order.getCustomerId() + "@example.com",
                "Order Confirmation",
                "Your order " + order.getOrderId() + " has been processed"
            );
            
            System.out.println("Order processed successfully");
            
        } catch (Exception e) {
            System.err.println("Error processing order: " + e.getMessage());
        }
    }
    
    @KafkaListener(topics = "orders", groupId = "analytics-group")
    public void analyzeOrder(String message) {
        try {
            Order order = objectMapper.readValue(message, Order.class);
            
            System.out.println("[Analytics] Order received: " + order.getOrderId());
            System.out.println("[Analytics] Amount: $" + order.getTotalAmount());
            
        } catch (Exception e) {
            System.err.println("Error in analytics: " + e.getMessage());
        }
    }
}
```

```java
// producer/NotificationProducer.java
package com.learning.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.model.Notification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducer {
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    public NotificationProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
    
    public void sendNotification(String type, String recipient, String subject, String message) {
        try {
            Notification notification = new Notification(type, recipient, subject, message);
            String notificationJson = objectMapper.writeValueAsString(notification);
            
            kafkaTemplate.send("notifications", notification.getRecipient(), notificationJson);
            System.out.println("Notification sent: " + notification.getNotificationId());
            
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }
    }
}
```

```java
// consumer/NotificationConsumer.java
package com.learning.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.model.Notification;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {
    
    private final ObjectMapper objectMapper;
    
    public NotificationConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @KafkaListener(topics = "notifications", groupId = "notification-group")
    public void handleNotification(String message) {
        try {
            Notification notification = objectMapper.readValue(message, Notification.class);
            
            System.out.println("=== Notification ===");
            System.out.println("Type: " + notification.getType());
            System.out.println("To: " + notification.getRecipient());
            System.out.println("Subject: " + notification.getSubject());
            System.out.println("Message: " + notification.getMessage());
            System.out.println("Time: " + notification.getTimestamp());
            System.out.println("===================");
            
        } catch (Exception e) {
            System.err.println("Error handling notification: " + e.getMessage());
        }
    }
}
```

---

## Step 6: Application Properties

```yaml
# src/main/resources/application.yml
spring:
  application:
    name: kafka-demo
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: kafka-demo-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer

server:
  port: 8080
```

---

## Step 7: Main Application

```java
// Main.java
package com.learning;

import com.learning.model.Order;
import com.learning.producer.OrderProducer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.math.BigDecimal;
import java.util.List;

@SpringBootApplication
public class Main {
    
    private final OrderProducer orderProducer;
    
    public Main(OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
    }
    
    @Bean
    public CommandLineRunner run() {
        return args -> {
            System.out.println("=== Kafka Demo Application ===\n");
            
            Order order1 = new Order(
                "ORD-001",
                1L,
                List.of(
                    new Order.OrderItem(1L, "Laptop", 1, new BigDecimal("1299.99")),
                    new Order.OrderItem(2L, "Mouse", 2, new BigDecimal("29.99"))
                ),
                "CREATED"
            );
            
            Order order2 = new Order(
                "ORD-002",
                2L,
                List.of(
                    new Order.OrderItem(3L, "Keyboard", 1, new BigDecimal("99.99"))
                ),
                "CREATED"
            );
            
            Order order3 = new Order(
                "ORD-003",
                1L,
                List.of(
                    new Order.OrderItem(1L, "Laptop", 1, new BigDecimal("1299.99")),
                    new Order.OrderItem(4L, "Monitor", 1, new BigDecimal("399.99"))
                ),
                "CREATED"
            );
            
            orderProducer.sendOrder(order1);
            Thread.sleep(1000);
            
            orderProducer.sendOrder(order2);
            Thread.sleep(1000);
            
            orderProducer.sendOrder(order3);
            
            System.out.println("\nAll orders sent to Kafka!");
            System.out.println("Check Kafka consumers for processing");
            
            Thread.sleep(5000);
        };
    }
    
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```

---

## Build Instructions

```bash
# Start Kafka (using Docker)
docker run -p 9092:9092 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 confluentinc/cp-kafka

cd 21-kafka
mvn clean compile
mvn spring-boot:run
```

---

# Real-World Project: E-Commerce Event Streaming Platform

## Project Overview

**Duration**: 15+ hours  
**Difficulty**: Advanced  
**Concepts Used**: Kafka Streams, Exactly-once Semantics, Schema Registry, Event Sourcing, CQRS, Windowed Operations

This comprehensive project implements a complete event streaming platform for e-commerce with real-time analytics, order processing, and inventory management.

---

## Project Structure

```
21-kafka/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── config/
│   │   ├── KafkaConfig.java
│   │   └── SchemaRegistryConfig.java
│   ├── streams/
│   │   ├── OrderAggregationStream.java
│   │   └── InventoryAlertStream.java
│   ├── producer/
│   │   ├── OrderProducer.java
│   │   ├── InventoryProducer.java
│   │   └── PaymentProducer.java
│   ├── consumer/
│   │   ├── OrderConsumer.java
│   │   ├── InventoryConsumer.java
│   │   └── AnalyticsConsumer.java
│   ├── model/
│   │   ├── Order.java
│   │   ├── Payment.java
│   │   ├── Inventory.java
│   │   └── AnalyticsEvent.java
│   └── service/
│       ├── OrderProcessingService.java
│       └── AnalyticsService.java
└── src/main/resources/
    └── application.yml
```

---

## POM.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>ecommerce-kafka</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>17</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.kafka</groupId>
            <artifactId>kafka-streams</artifactId>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
        <dependency>
            <groupId>io.confluent</groupId>
            <artifactId>kafka-avro-serializer</artifactId>
            <version>7.5.0</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.30</version>
        </dependency>
    </dependencies>
</project>
```

---

## Kafka Streams Implementation

```java
// streams/OrderAggregationStream.java
package com.learning.streams;

import com.learning.model.AnalyticsEvent;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

@Configuration
public class OrderAggregationStream {
    
    @Bean
    public KafkaStreams orderAggregationStream(StreamsBuilder streamsBuilder) {
        StoreBuilder<KeyValueStore<String, Long>> countsStore = Stores.keyValueStoreBuilder(
            Stores.persistentKeyValueStore("order-counts"),
            Serdes.String(),
            Serdes.Long()
        );
        
        streamsBuilder.addStateStore(countsStore);
        
        KStream<String, String> ordersStream = streamsBuilder.stream("orders");
        
        KTable<String, Long> orderCounts = ordersStream
            .mapValues((key, value) -> {
                System.out.println("Processing order: " + key);
                return value;
            })
            .groupBy((key, value) -> "all-orders")
            .count(Materialized.as("order-counts"));
        
        orderCounts.toStream().foreach((key, count) -> 
            System.out.println("Total orders processed: " + count));
        
        return new KafkaStreams(streamsBuilder.build(), getStreamsConfig());
    }
    
    private KafkaStreams.StreamsConfig getStreamsConfig() {
        return new KafkaStreams.StreamsConfig(java.util.Map.of(
            "application.id", "order-aggregation-app",
            "bootstrap.servers", "localhost:9092",
            "default.key.serde", Serdes.String().getClass().getName(),
            "default.value.serde", Serdes.String().getClass().getName(),
            "commit.interval.ms", "5000"
        ));
    }
}
```

---

## Enhanced Model Classes

```java
// model/Payment.java
package com.learning.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {
    private String paymentId;
    private String orderId;
    private String customerId;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
    private String transactionId;
    private LocalDateTime timestamp;
    
    public Payment() {}
    
    public Payment(String paymentId, String orderId, String customerId, 
                   BigDecimal amount, String paymentMethod) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = "PENDING";
        this.timestamp = LocalDateTime.now();
    }
    
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
}
```

```java
// model/Inventory.java
package com.learning.model;

public class Inventory {
    private String productId;
    private String productName;
    private Integer availableQuantity;
    private Integer reservedQuantity;
    private Integer reorderLevel;
    private String location;
    
    public Inventory() {}
    
    public Inventory(String productId, String productName, Integer availableQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = 0;
        this.reorderLevel = 10;
    }
    
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public Integer getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(Integer availableQuantity) { this.availableQuantity = availableQuantity; }
    
    public Integer getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(Integer reservedQuantity) { this.reservedQuantity = reservedQuantity; }
    
    public Integer getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(Integer reorderLevel) { this.reorderLevel = reorderLevel; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public boolean needsReorder() {
        return availableQuantity <= reorderLevel;
    }
}
```

```java
// model/AnalyticsEvent.java
package com.learning.model;

import java.time.LocalDateTime;

public class AnalyticsEvent {
    private String eventId;
    private String eventType;
    private String category;
    private Double value;
    private String customerId;
    private String productId;
    private LocalDateTime timestamp;
    
    public AnalyticsEvent() {}
    
    public AnalyticsEvent(String eventType, String category, Double value, String customerId) {
        this.eventId = "EVT-" + System.currentTimeMillis();
        this.eventType = eventType;
        this.category = category;
        this.value = value;
        this.customerId = customerId;
        this.timestamp = LocalDateTime.now();
    }
    
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
    
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
}
```

---

## Services

```java
// service/OrderProcessingService.java
package com.learning.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.model.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class OrderProcessingService {
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    public OrderProcessingService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
    
    public void processOrder(Order order) {
        try {
            String orderJson = objectMapper.writeValueAsString(order);
            kafkaTemplate.send("orders", order.getOrderId(), orderJson);
            
            kafkaTemplate.send("inventory-reservation", order.getOrderId(), orderJson);
            
            kafkaTemplate.send("payment-request", order.getOrderId(), objectMapper.writeValueAsString(
                new Payment(
                    "PAY-" + System.currentTimeMillis(),
                    order.getOrderId(),
                    "CUST-" + order.getCustomerId(),
                    order.getTotalAmount(),
                    "CREDIT_CARD"
                )
            ));
            
            System.out.println("Order " + order.getOrderId() + " dispatched to multiple topics");
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to process order", e);
        }
    }
    
    public void handlePaymentSuccess(String orderId) {
        try {
            String statusUpdate = objectMapper.writeValueAsString(Map.of(
                "orderId", orderId,
                "status", "PAID",
                "timestamp", System.currentTimeMillis()
            ));
            
            kafkaTemplate.send("order-status-updates", orderId, statusUpdate);
            
            String analyticsEvent = objectMapper.writeValueAsString(
                new AnalyticsEvent("ORDER_COMPLETED", "revenue", 100.0, "CUST-1")
            );
            kafkaTemplate.send("analytics-events", orderId, analyticsEvent);
            
        } catch (Exception e) {
            System.err.println("Failed to handle payment success: " + e.getMessage());
        }
    }
}
```

```java
// service/AnalyticsService.java
package com.learning.service;

import com.learning.model.AnalyticsEvent;
import com.learning.model.Order;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AnalyticsService {
    
    private final Map<String, Double> revenueByHour = new ConcurrentHashMap<>();
    private final Map<String, Integer> ordersByCategory = new ConcurrentHashMap<>();
    private final Map<String, Integer> customerOrderCount = new ConcurrentHashMap<>();
    
    @KafkaListener(topics = "analytics-events", groupId = "analytics-processor")
    public void processAnalyticsEvent(String message) {
        try {
            System.out.println("Processing analytics event: " + message);
            
        } catch (Exception e) {
            System.err.println("Error processing analytics: " + e.getMessage());
        }
    }
    
    public void trackOrderCompleted(Order order) {
        String hourKey = java.time.LocalDateTime.now().getHour() + ":00";
        revenueByHour.merge(hourKey, order.getTotalAmount().doubleValue(), Double::sum);
        
        ordersByCategory.merge("ALL", 1, Integer::sum);
        
        String customerKey = "CUST-" + order.getCustomerId();
        customerOrderCount.merge(customerKey, 1, Integer::sum);
    }
    
    public Map<String, Double> getHourlyRevenue() {
        return revenueByHour;
    }
    
    public Map<String, Integer> getOrdersByCategory() {
        return ordersByCategory;
    }
    
    public void printAnalytics() {
        System.out.println("\n=== Analytics Dashboard ===");
        System.out.println("Hourly Revenue:");
        revenueByHour.forEach((hour, revenue) -> 
            System.out.println("  " + hour + ": $" + revenue));
        
        System.out.println("\nTotal Orders by Category:");
        ordersByCategory.forEach((category, count) -> 
            System.out.println("  " + category + ": " + count));
        
        System.out.println("\nCustomer Order Counts:");
        customerOrderCount.forEach((customer, count) -> 
            System.out.println("  " + customer + ": " + count + " orders"));
    }
}
```

---

## Main Application

```java
// Main.java
package com.learning;

import com.learning.model.*;
import com.learning.service.OrderProcessingService;
import com.learning.service.AnalyticsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.math.BigDecimal;
import java.util.List;

@SpringBootApplication
public class Main {
    
    private final OrderProcessingService orderProcessingService;
    private final AnalyticsService analyticsService;
    
    public Main(OrderProcessingService orderProcessingService, AnalyticsService analyticsService) {
        this.orderProcessingService = orderProcessingService;
        this.analyticsService = analyticsService;
    }
    
    @Bean
    public CommandLineRunner run() {
        return args -> {
            System.out.println("=== E-Commerce Kafka Platform ===\n");
            
            for (int i = 1; i <= 5; i++) {
                Order order = new Order(
                    "ORD-" + System.currentTimeMillis() + "-" + i,
                    (long) i,
                    List.of(
                        new Order.OrderItem((long) i, "Product " + i, 1, new BigDecimal("99.99"))
                    ),
                    "CREATED"
                );
                
                orderProcessingService.processOrder(order);
                Thread.sleep(500);
            }
            
            Thread.sleep(2000);
            
            orderProcessingService.handlePaymentSuccess("ORD-123");
            
            analyticsService.printAnalytics();
            
            System.out.println("\n=== System Running ===");
            System.out.println("Waiting for consumer processing...");
            
            Thread.sleep(10000);
        };
    }
    
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```

---

## Build Instructions

```bash
# Start Kafka with more advanced setup
docker-compose up -d

cd 21-kafka
mvn clean compile
mvn spring-boot:run

# Monitor topics
kafka-topics.sh --list --bootstrap-server localhost:9092
kafka-console-consumer.sh --topic orders --from-beginning --bootstrap-server localhost:9092
```

This comprehensive project demonstrates enterprise-grade Kafka patterns including streams processing, multiple consumer groups, event-driven architecture, and real-time analytics.

---

# Production Patterns: Advanced Streaming

## Exactly-Once Processing with Transactions

```java
// streams/ExactlyOnceStream.java
package com.learning.streams;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import java.util.Properties;

public class ExactlyOnceStream {
    
    public KafkaStreams buildExactlyOnceStream(StreamsBuilder builder) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "exactly-once-app");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "exactly-once-producer");
        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, 
            StreamsConfig.EXACTLY_ONCE_V2);
        
        KStream<String, Order> ordersStream = builder.stream("orders-input");
        
        ordersStream
            .filter((key, order) -> order.getStatus().equals("PAID"))
            .peek((key, order) -> System.out.println("Processing: " + key))
            .to("orders-output");
        
        return new KafkaStreams(builder.build(), props);
    }
}
```

## Windowed Aggregation with Late Events

```java
// streams/WindowedRevenueStream.java
package com.learning.streams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import java.time.Duration;

public class WindowedRevenueStream {
    
    public KafkaStreams buildHourlyRevenueStream(StreamsBuilder builder) {
        KStream<String, Order> ordersStream = builder.stream("orders");
        
        KTable<Windowed<String>, Double> hourlyRevenue = ordersStream
            .filter((key, order) -> order.getStatus().equals("COMPLETED"))
            .groupBy((key, order) -> KeyValue.pair(
                String.valueOf(order.getTimestamp().getHour()),
                order.getTotalAmount().doubleValue()))
            .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofHours(1))
                .grace(Duration.ofMinutes(15)))
            .reduce(Double::sum, Materialized.as("hourly-revenue-store"));
        
        hourlyRevenue
            .toStream()
            .filter((key, value) -> value != null && value > 0)
            .foreach((key, value) -> 
                System.out.printf("Hour %s: Revenue $%.2f%n", key.key(), value));
        
        return new KafkaStreams(builder.build(), getStreamsConfig());
    }
}
```

## Join Stream with Table

```java
// streams/OrderEnrichmentStream.java
package com.learning.streams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import java.time.Duration;

public class OrderEnrichmentStream {
    
    public KafkaStreams buildEnrichmentStream(StreamsBuilder builder) {
        KStream<String, Order> ordersStream = builder.stream("orders");
        KTable<String, Customer> customersTable = builder.table("customers");
        
        KStream<String, EnrichedOrder> enrichedOrders = ordersStream
            .leftJoin(customersTable,
                (order, customer) -> enrichOrder(order, customer),
                Joined.with(
                    Serdes.String(),
                    new OrderSerde(),
                    new CustomerSerde()));
        
        enrichedOrders
            .filter((key, order) -> order.getCustomer() != null)
            .to("enriched-orders");
        
        return new KafkaStreams(builder.build(), getStreamsConfig());
    }
    
    private EnrichedOrder enrichOrder(Order order, Customer customer) {
        EnrichedOrder enriched = new EnrichedOrder();
        enriched.setOrderId(order.getOrderId());
        enriched.setItems(order.getItems());
        enriched.setTotalAmount(order.getTotalAmount());
        
        if (customer != null) {
            CustomerInfo info = new CustomerInfo();
            info.setName(customer.getName());
            info.setEmail(customer.getEmail());
            info.setTier(customer.getTier());
            enriched.setCustomer(info);
        }
        
        return enriched;
    }
}
```

## Stateful Count with Session Windows

```java
// streams/SessionAnalysisStream.java
package com.learning.streams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import java.time.Duration;

public class SessionAnalysisStream {
    
    public KafkaStreams buildSessionStream(StreamsBuilder builder) {
        KStream<String, UserEvent> events = builder.stream("user-events");
        
        KTable<Windowed<String>, Long> sessionCounts = events
            .groupBy((key, event) -> KeyValue.pair(event.getUserId(), event))
            .windowedBy(SessionWindows.of(Duration.ofMinutes(30))
                .grace(Duration.ofMinutes(5)))
            .count(Materialized.as("session-counts"));
        
        sessionCounts
            .toStream()
            .foreach((key, count) -> 
                System.out.printf("User %s session: %d events%n", key.key(), count));
        
        return new KafkaStreams(builder.build(), getStreamsConfig());
    }
}
```

## Table-to-Table Join

```java
// streams/ProductPriceStream.java
package com.learning.streams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import java.math.BigDecimal;

public class ProductPriceStream {
    
    public KafkaStreams buildPriceUpdateStream(StreamsBuilder builder) {
        KTable<String, ProductPrice> currentPrices = builder.table("product-prices");
        KTable<String, ProductPrice> historicalPrices = builder.table("historical-prices");
        
        KTable<String, PriceChange> priceChanges = currentPrices
            .outerJoin(historicalPrices,
                (current, historical) -> calculatePriceChange(current, historical))
            .filter((key, change) -> change != null && change.getChangePercent() > 5);
        
        priceChanges
            .toStream()
            .filter((key, change) -> change.getChangePercent() > 10)
            .to("price-alerts");
        
        return new KafkaStreams(builder.build(), getStreamsConfig());
    }
    
    private PriceChange calculatePriceChange(ProductPrice current, ProductPrice historical) {
        if (current == null) return null;
        
        double changePercent = 0;
        if (historical != null) {
            BigDecimal currPrice = current.getPrice();
            BigDecimal histPrice = historical.getPrice();
            changePercent = currPrice.subtract(histPrice)
                .divide(histPrice, 4, BigDecimal.ROUND_HALF_UP)
                .doubleValue() * 100;
        }
        
        return new PriceChange(current.getProductId(), current.getPrice(), 
            historical != null ? historical.getPrice() : null, changePercent);
    }
}
```

## Branch/Merge Stream

```java
// streams/BranchingStream.java
package com.learning.streams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import java.util.Arrays;

public class BranchingStream {
    
    public KafkaStreams buildBranchingStream(StreamsBuilder builder) {
        KStream<String, Order> ordersStream = builder.stream("orders");
        
        KStream<String, Order>[] branches = ordersStream.branch(
            (key, order) -> order.getTotalAmount().compareTo(new BigDecimal("1000")) > 0,
            (key, order) -> order.getTotalAmount().compareTo(new BigDecimal("500")) > 0,
            (key, order) -> true
        );
        
        branches[0].mapValues(order -> "HIGH_VALUE:" + order.getOrderId())
            .to("high-value-orders");
        
        branches[1].mapValues(order -> "MEDIUM_VALUE:" + order.getOrderId())
            .to("medium-value-orders");
        
        branches[2].mapValues(order -> "STANDARD:" + order.getOrderId())
            .to("standard-orders");
        
        return new KafkaStreams(builder.build(), getStreamsConfig());
    }
}
```

## Global KTable for Enrichment

```java
// streams/GlobalEnrichmentStream.java
package com.learning.streams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import java.util.HashMap;
import java.util.Map;

public class GlobalEnrichmentStream {
    
    public KafkaStreams buildGlobalLookupStream(StreamsBuilder builder) {
        KStream<String, Order> ordersStream = builder.stream("orders");
        
        GlobalKTable<String, ProductInfo> productLookup = 
            builder.globalTable("products",
                Materialized.as("product-lookup-store"));
        
        KStream<String, EnrichedOrder> enrichedOrders = ordersStream
            .leftJoin(productLookup,
                (orderId, order) -> order.getItems().stream()
                    .map(Order.OrderItem::getProductId)
                    .findFirst()
                    .orElse("UNKNOWN"),
                (order, productInfo) -> enrichWithProduct(order, productInfo));
        
        enrichedOrders.to("enriched-orders");
        
        return new KafkaStreams(builder.build(), getStreamsConfig());
    }
    
    private EnrichedOrder enrichWithProduct(Order order, ProductInfo info) {
        EnrichedOrder enriched = new EnrichedOrder();
        enriched.setOrderId(order.getOrderId());
        
        if (info != null) {
            enriched.setPrimaryProductName(info.getName());
            enriched.setPrimaryCategory(info.getCategory());
        }
        
        return enriched;
    }
}
```

## Custom Processor with State Store

```java
// streams/CustomProcessorStream.java
package com.learning.streams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;

public class CustomProcessorStream {
    
    public KafkaStreams buildCustomProcessorStream(StreamsBuilder builder) {
        StoreBuilder<KeyValueStore<String, Long>> countsStore = 
            Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore("counts"),
                Serdes.String(),
                Serdes.Long());
        
        builder.addStateStore(countsStore);
        
        KStream<String, Event> events = builder.stream("events");
        
        events.process(() -> new Processor<String, Event>() {
            private ProcessorContext context;
            private KeyValueStore<String, Long> countsStore;
            
            @Override
            public void init(ProcessorContext context) {
                this.context = context;
                this.countsStore = context.getStateStore("counts");
            }
            
            @Override
            public void process(String key, Event event) {
                String type = event.getType();
                Long count = countsStore.get(type);
                count = (count == null) ? 1L : count + 1L;
                countsStore.put(type, count);
                
                context.forward(key, new EventCount(type, count));
            }
            
            @Override
            public void close() {}
        }, Named.as("event-counter"));
        
        return new KafkaStreams(builder.build(), getStreamsConfig());
    }
}
```