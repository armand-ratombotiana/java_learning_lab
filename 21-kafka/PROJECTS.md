# Kafka Module - PROJECTS.md

---

# Mini-Project 1: Producer Implementation (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Beginner  
**Concepts Used**: KafkaProducer, Serializers, Message Keys, Acknowledgments, Idempotent Producers

This mini-project focuses on implementing Kafka producers with various configuration patterns.

---

## Project Structure

```
21-kafka/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── producer/
│   │   ├── BasicProducer.java
│   │   ├── IdempotentProducer.java
│   │   └── TransactionalProducer.java
│   └── model/
│       └── Order.java
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
    <artifactId>kafka-producer-demo</artifactId>
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
import java.util.UUID;

public class Order {
    private String orderId;
    private Long customerId;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
    private String region;
    
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
    
    public Order(Long customerId, List<OrderItem> items, String region) {
        this.orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
        this.customerId = customerId;
        this.items = items;
        this.region = region;
        this.status = "CREATED";
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
    
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
}
```

---

## Step 3: Basic Producer Implementation

```java
// producer/BasicProducer.java
package com.learning.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.model.Order;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Service
public class BasicProducer {
    
    private final ObjectMapper objectMapper;
    private final org.apache.kafka.clients.producer.KafkaProducer<String, String> producer;
    private final String topic;
    
    public BasicProducer(
            ObjectMapper objectMapper,
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
            @Value("${kafka.topic.orders:orders}") String topic) {
        this.objectMapper = objectMapper;
        this.topic = topic;
        
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        props.put(ProducerConfig.LINGER_MS_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        
        this.producer = new org.apache.kafka.clients.producer.KafkaProducer<>(props);
    }
    
    public void sendOrderSync(Order order) {
        try {
            String orderJson = objectMapper.writeValueAsString(order);
            
            ProducerRecord<String, String> record = new ProducerRecord<>(
                topic,
                order.getOrderId(),
                orderJson
            );
            
            Future<RecordMetadata> future = producer.send(record);
            RecordMetadata metadata = future.get(10, TimeUnit.SECONDS);
            
            System.out.println("Order sent successfully:");
            System.out.println("  Order ID: " + order.getOrderId());
            System.out.println("  Partition: " + metadata.partition());
            System.out.println("  Offset: " + metadata.offset());
            
        } catch (JsonProcessingException e) {
            System.err.println("Failed to serialize order: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Failed to send order: " + e.getMessage());
        }
    }
    
    public void sendOrderWithCallback(Order order) {
        try {
            String orderJson = objectMapper.writeValueAsString(order);
            
            ProducerRecord<String, String> record = new ProducerRecord<>(
                topic,
                order.getRegion(),
                orderJson
            );
            
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    System.err.println("Failed to send order: " + exception.getMessage());
                } else {
                    System.out.println("Order delivered:");
                    System.out.println("  Key: " + record.key());
                    System.out.println("  Partition: " + metadata.partition());
                    System.out.println("  Offset: " + metadata.offset());
                }
            });
            
        } catch (JsonProcessingException e) {
            System.err.println("Failed to serialize order: " + e.getMessage());
        }
    }
    
    public void close() {
        producer.close();
    }
}
```

---

## Step 4: Idempotent Producer Implementation

```java
// producer/IdempotentProducer.java
package com.learning.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.model.Order;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class IdempotentProducer {
    
    private final ObjectMapper objectMapper;
    private final org.apache.kafka.clients.producer.KafkaProducer<String, String> producer;
    private final String topic;
    
    public IdempotentProducer(
            ObjectMapper objectMapper,
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
            @Value("${kafka.topic.orders:orders}") String topic) {
        this.objectMapper = objectMapper;
        this.topic = topic;
        
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 100);
        
        this.producer = new org.apache.kafka.clients.producer.KafkaProducer<>(props);
    }
    
    public String sendOrderWithDeduplication(Order order) {
        String producerId = UUID.randomUUID().toString();
        
        try {
            String orderJson = objectMapper.writeValueAsString(order);
            
            ProducerRecord<String, String> record = new ProducerRecord<>(
                topic,
                order.getOrderId(),
                orderJson
            );
            
            record.headers().add("X-Producer-Id", producerId.getBytes());
            record.headers().add("X-Timestamp", String.valueOf(System.currentTimeMillis()).getBytes());
            
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    System.err.println("Failed to send order: " + exception.getMessage());
                } else {
                    System.out.println("Idempotent order delivered:");
                    System.out.println("  Producer ID: " + producerId);
                    System.out.println("  Partition: " + metadata.partition());
                    System.out.println("  Offset: " + metadata.offset());
                }
            });
            
            return producerId;
            
        } catch (JsonProcessingException e) {
            System.err.println("Failed to serialize order: " + e.getMessage());
            return null;
        }
    }
    
    public void sendBatchOrders(Iterable<Order> orders) {
        try {
            for (Order order : orders) {
                String orderJson = objectMapper.writeValueAsString(order);
                
                ProducerRecord<String, String> record = new ProducerRecord<>(
                    topic,
                    order.getOrderId(),
                    orderJson
                );
                
                producer.send(record);
            }
            
            producer.flush();
            System.out.println("Batch of orders sent successfully");
            
        } catch (JsonProcessingException e) {
            System.err.println("Failed to serialize order: " + e.getMessage());
        }
    }
    
    public void close() {
        producer.flush();
        producer.close();
    }
}
```

---

## Step 5: Transactional Producer Implementation

```java
// producer/TransactionalProducer.java
package com.learning.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.model.Order;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class TransactionalProducer {
    
    private final ObjectMapper objectMapper;
    private final org.apache.kafka.clients.producer.KafkaProducer<String, String> producer;
    private final String ordersTopic;
    private final String inventoryTopic;
    private final String paymentTopic;
    
    public TransactionalProducer(
            ObjectMapper objectMapper,
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
            @Value("${kafka.topic.orders:orders}") String ordersTopic,
            @Value("${kafka.topic.inventory:inventory}") String inventoryTopic,
            @Value("${kafka.topic.payments:payments}") String paymentTopic) {
        this.objectMapper = objectMapper;
        this.ordersTopic = ordersTopic;
        this.inventoryTopic = inventoryTopic;
        this.paymentTopic = paymentTopic;
        
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "order-transactional-producer");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        
        this.producer = new org.apache.kafka.clients.producer.KafkaProducer<>(props);
        this.producer.initTransactions();
    }
    
    public void sendOrderTransactionally(Order order) {
        producer.beginTransaction();
        
        try {
            String orderJson = objectMapper.writeValueAsString(order);
            
            ProducerRecord<String, String> orderRecord = new ProducerRecord<>(
                ordersTopic,
                order.getOrderId(),
                orderJson
            );
            producer.send(orderRecord);
            
            String inventoryJson = objectMapper.writeValueAsString(new InventoryReservation(
                order.getOrderId(),
                order.getItems()
            ));
            ProducerRecord<String, String> inventoryRecord = new ProducerRecord<>(
                inventoryTopic,
                order.getOrderId(),
                inventoryJson
            );
            producer.send(inventoryRecord);
            
            String paymentJson = objectMapper.writeValueAsString(new PaymentRequest(
                "PAY-" + order.getOrderId(),
                order.getOrderId(),
                order.getTotalAmount()
            ));
            ProducerRecord<String, String> paymentRecord = new ProducerRecord<>(
                paymentTopic,
                order.getOrderId(),
                paymentJson
            );
            producer.send(paymentRecord);
            
            producer.commitTransaction();
            System.out.println("Order transaction committed successfully: " + order.getOrderId());
            
        } catch (Exception e) {
            producer.abortTransaction();
            System.err.println("Order transaction aborted: " + e.getMessage());
            throw new RuntimeException("Transaction failed", e);
        }
    }
    
    public static class InventoryReservation {
        private String orderId;
        private List<Order.OrderItem> items;
        
        public InventoryReservation() {}
        
        public InventoryReservation(String orderId, List<Order.OrderItem> items) {
            this.orderId = orderId;
            this.items = items;
        }
        
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        
        public List<Order.OrderItem> getItems() { return items; }
        public void setItems(List<Order.OrderItem> items) { this.items = items; }
    }
    
    public static class PaymentRequest {
        private String paymentId;
        private String orderId;
        private java.math.BigDecimal amount;
        
        public PaymentRequest() {}
        
        public PaymentRequest(String paymentId, String orderId, java.math.BigDecimal amount) {
            this.paymentId = paymentId;
            this.orderId = orderId;
            this.amount = amount;
        }
        
        public String getPaymentId() { return paymentId; }
        public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
        
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        
        public java.math.BigDecimal getAmount() { return amount; }
        public void setAmount(java.math.BigDecimal amount) { this.amount = amount; }
    }
    
    public void close() {
        producer.close();
    }
}
```

---

## Step 6: Main Application

```java
// Main.java
package com.learning;

import com.learning.model.Order;
import com.learning.producer.BasicProducer;
import com.learning.producer.IdempotentProducer;
import com.learning.producer.TransactionalProducer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.List;

@SpringBootApplication
public class Main {
    
    private final BasicProducer basicProducer;
    private final IdempotentProducer idempotentProducer;
    private final TransactionalProducer transactionalProducer;
    
    public Main(BasicProducer basicProducer, 
                IdempotentProducer idempotentProducer,
                TransactionalProducer transactionalProducer) {
        this.basicProducer = basicProducer;
        this.idempotentProducer = idempotentProducer;
        this.transactionalProducer = transactionalProducer;
    }
    
    @Bean
    public CommandLineRunner run() {
        return args -> {
            System.out.println("=== Kafka Producer Demo ===\n");
            
            Order order1 = new Order(
                1L,
                List.of(
                    new Order.OrderItem(1L, "Laptop", 1, new BigDecimal("1299.99")),
                    new Order.OrderItem(2L, "Mouse", 2, new BigDecimal("29.99"))
                ),
                "US-EAST"
            );
            
            Order order2 = new Order(
                2L,
                List.of(
                    new Order.OrderItem(3L, "Keyboard", 1, new BigDecimal("99.99")),
                    new Order.OrderItem(4L, "Monitor", 1, new BigDecimal("399.99"))
                ),
                "US-WEST"
            );
            
            System.out.println("--- Basic Producer ---");
            basicProducer.sendOrderSync(order1);
            
            System.out.println("\n--- Idempotent Producer ---");
            idempotentProducer.sendOrderWithDeduplication(order2);
            
            System.out.println("\n--- Transactional Producer ---");
            Order order3 = new Order(
                3L,
                List.of(
                    new Order.OrderItem(5L, "Headphones", 1, new BigDecimal("199.99"))
                ),
                "EU"
            );
            transactionalProducer.sendOrderTransactionally(order3);
            
            System.out.println("\nAll producers completed successfully!");
            
            Thread.sleep(2000);
        };
    }
    
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```

---

## Step 7: Application Properties

```yaml
# src/main/resources/application.yml
spring:
  application:
    name: kafka-producer-demo
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer

kafka:
  topic:
    orders: orders
    inventory: inventory-reservation
    payments: payment-requests
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

# Mini-Project 2: Consumer Implementation (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Beginner  
**Concepts Used**: KafkaConsumer, Consumer Groups, Offsets, Auto/Manual Commit, Rebalance Listeners

This mini-project focuses on implementing Kafka consumers with various configuration patterns.

---

## Project Structure

```
21-kafka/
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── consumer/
│   │   ├── BasicConsumer.java
│   │   ├── ManualCommitConsumer.java
│   │   └── ConsumerWithRebalance.java
│   └── model/
│       └── Order.java
└── src/main/resources/
    └── application.yml
```

---

## Step 1: Basic Consumer Implementation

```java
// consumer/BasicConsumer.java
package com.learning.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.model.Order;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class BasicConsumer {
    
    private final ObjectMapper objectMapper;
    private final String topic;
    private final String groupId;
    private KafkaConsumer<String, String> consumer;
    private final AtomicBoolean running = new AtomicBoolean(true);
    
    public BasicConsumer(
            ObjectMapper objectMapper,
            @Value("${kafka.topic.orders:orders}") String topic,
            @Value("${spring.kafka.consumer.group-id:basic-consumer-group}") String groupId) {
        this.objectMapper = objectMapper;
        this.topic = topic;
        this.groupId = groupId;
    }
    
    @PostConstruct
    public void startConsuming() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);
        
        this.consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));
        
        Thread consumerThread = new Thread(this::consumeLoop);
        consumerThread.setDaemon(true);
        consumerThread.start();
        
        System.out.println("Basic consumer started with group: " + groupId);
    }
    
    private void consumeLoop() {
        try {
            while (running.get()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                
                for (ConsumerRecord<String, String> record : records) {
                    processRecord(record);
                }
            }
        } finally {
            consumer.close();
        }
    }
    
    private void processRecord(ConsumerRecord<String, String> record) {
        try {
            Order order = objectMapper.readValue(record.value(), Order.class);
            
            System.out.println("=== Order Received ===");
            System.out.println("Order ID: " + order.getOrderId());
            System.out.println("Customer ID: " + order.getCustomerId());
            System.out.println("Total: $" + order.getTotalAmount());
            System.out.println("Partition: " + record.partition());
            System.out.println("Offset: " + record.offset());
            System.out.println("======================");
            
        } catch (Exception e) {
            System.err.println("Failed to process record: " + e.getMessage());
        }
    }
    
    @PreDestroy
    public void stopConsuming() {
        running.set(false);
        consumer.close();
        System.out.println("Basic consumer stopped");
    }
}
```

---

## Step 2: Manual Commit Consumer Implementation

```java
// consumer/ManualCommitConsumer.java
package com.learning.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.model.Order;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ManualCommitConsumer {
    
    private final ObjectMapper objectMapper;
    private final String topic;
    private final String groupId;
    private KafkaConsumer<String, String> consumer;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final Map<TopicPartition, Long> lastProcessedOffset = new HashMap<>();
    
    public ManualCommitConsumer(
            ObjectMapper objectMapper,
            @Value("${kafka.topic.orders:orders}") String topic,
            @Value("${spring.kafka.consumer.group-id:manual-commit-group}") String groupId) {
        this.objectMapper = objectMapper;
        this.topic = topic;
        this.groupId = groupId;
    }
    
    @PostConstruct
    public void startConsuming() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        
        this.consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));
        
        Thread consumerThread = new Thread(this::consumeLoop);
        consumerThread.setDaemon(true);
        consumerThread.start();
        
        System.out.println("Manual commit consumer started with group: " + groupId);
    }
    
    private void consumeLoop() {
        try {
            while (running.get()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                
                for (ConsumerRecord<String, String> record : records) {
                    processRecordWithManualCommit(record);
                }
                
                if (!records.isEmpty()) {
                    commitOffsets();
                }
            }
        } finally {
            consumer.close();
        }
    }
    
    private void processRecordWithManualCommit(ConsumerRecord<String, String> record) {
        try {
            Order order = objectMapper.readValue(record.value(), Order.class);
            
            System.out.println("Processing order: " + order.getOrderId());
            
            Thread.sleep(100);
            
            TopicPartition partition = new TopicPartition(record.topic(), record.partition());
            lastProcessedOffset.put(partition, record.offset());
            
        } catch (Exception e) {
            System.err.println("Failed to process record: " + e.getMessage());
        }
    }
    
    private void commitOffsets() {
        Map<TopicPartition, OffsetAndMetadata> offsetsToCommit = new HashMap<>();
        
        for (Map.Entry<TopicPartition, Long> entry : lastProcessedOffset.entrySet()) {
            TopicPartition partition = entry.getKey();
            Long offset = entry.getValue();
            
            offsetsToCommit.put(partition, new OffsetAndMetadata(offset + 1));
        }
        
        if (!offsetsToCommit.isEmpty()) {
            consumer.commitSync(offsetsToCommit);
            System.out.println("Committed offsets for " + offsetsToCommit.size() + " partitions");
        }
    }
    
    @PreDestroy
    public void stopConsuming() {
        running.set(false);
        commitOffsets();
        consumer.close();
        System.out.println("Manual commit consumer stopped");
    }
}
```

---

## Step 3: Consumer with Rebalance Listener

```java
// consumer/ConsumerWithRebalance.java
package com.learning.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.model.Order;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ConsumerWithRebalance {
    
    private final ObjectMapper objectMapper;
    private final String topic;
    private final String groupId;
    private KafkaConsumer<String, String> consumer;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final Map<TopicPartition, Long> partitionOffsets = new HashMap<>();
    
    public ConsumerWithRebalance(
            ObjectMapper objectMapper,
            @Value("${kafka.topic.orders:orders}") String topic,
            @Value("${spring.kafka.consumer.group-id:rebalance-group}") String groupId) {
        this.objectMapper = objectMapper;
        this.topic = topic;
        this.groupId = groupId;
    }
    
    @PostConstruct
    public void startConsuming() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        
        this.consumer = new KafkaConsumer<>(props);
        
        ConsumerRebalanceListenerImpl rebalanceListener = new ConsumerRebalanceListenerImpl();
        consumer.subscribe(Collections.singletonList(topic), rebalanceListener);
        
        Thread consumerThread = new Thread(this::consumeLoop);
        consumerThread.setDaemon(true);
        consumerThread.start();
        
        System.out.println("Consumer with rebalance listener started with group: " + groupId);
    }
    
    private void consumeLoop() {
        try {
            while (running.get()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                
                for (ConsumerRecord<String, String> record : records) {
                    processRecord(record);
                }
                
                if (!records.isEmpty()) {
                    consumer.commitSync();
                }
            }
        } finally {
            consumer.close();
        }
    }
    
    private void processRecord(ConsumerRecord<String, String> record) {
        try {
            Order order = objectMapper.readValue(record.value(), Order.class);
            
            TopicPartition partition = new TopicPartition(record.topic(), record.partition());
            partitionOffsets.put(partition, record.offset());
            
            System.out.println("Order processed: " + order.getOrderId() + 
                " from partition " + record.partition());
            
        } catch (Exception e) {
            System.err.println("Failed to process record: " + e.getMessage());
        }
    }
    
    @PreDestroy
    public void stopConsuming() {
        running.set(false);
        consumer.close();
        System.out.println("Consumer with rebalance listener stopped");
    }
    
    private class ConsumerRebalanceListenerImpl implements org.apache.kafka.clients.consumer.ConsumerRebalanceListener {
        
        @Override
        public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
            System.out.println("=== Partitions Revoked ===");
            for (TopicPartition partition : partitions) {
                Long savedOffset = partitionOffsets.get(partition);
                if (savedOffset != null) {
                    System.out.println("Saving offset for partition " + partition.partition() + 
                        ": " + savedOffset);
                }
            }
        }
        
        @Override
        public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
            System.out.println("=== Partitions Assigned ===");
            for (TopicPartition partition : partitions) {
                System.out.println("Assigned to partition: " + partition.partition());
            }
        }
    }
}
```

---

## Build Instructions

```bash
cd 21-kafka
mvn clean compile
mvn spring-boot:run
```

---

# Mini-Project 3: Topic Management (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Beginner  
**Concepts Used**: Topic Creation, Partitions, Replication, Retention Policy, AdminClient

This mini-project focuses on managing Kafka topics programmatically.

---

## Project Structure

```
21-kafka/
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── admin/
│   │   └── TopicManager.java
│   └── config/
│       └── KafkaAdminConfig.java
└── src/main/resources/
    └── application.yml
```

---

## Step 1: Kafka Admin Configuration

```java
// config/KafkaAdminConfig.java
package com.learning.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaAdminConfig {
    
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    
    @Bean
    public AdminClient adminClient() {
        Map<String, Object> props = new HashMap<>();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 5000);
        props.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, 5000);
        return AdminClient.create(props);
    }
    
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
            .partitions(3)
            .replicas(1)
            .config("retention.ms", "604800000")
            .build();
    }
    
    @Bean
    public NewTopic analyticsTopic() {
        return TopicBuilder.name("analytics")
            .partitions(5)
            .replicas(1)
            .config("retention.bytes", "1073741824")
            .build();
    }
}
```

---

## Step 2: Topic Manager Implementation

```java
// admin/TopicManager.java
package com.learning.admin;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.TopicCollection;
import org.apache.kafka.common.TopicPartitionInfo;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class TopicManager {
    
    private final AdminClient adminClient;
    
    public TopicManager(AdminClient adminClient) {
        this.adminClient = adminClient;
    }
    
    public void createTopic(String name, int partitions, int replicationFactor) {
        NewTopic newTopic = new NewTopic(name, partitions, (short) replicationFactor);
        
        CreateTopicsResult result = adminClient.createTopics(Collections.singleton(newTopic));
        
        try {
            result.all().get();
            System.out.println("Topic created: " + name);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof TopicExistsException) {
                System.out.println("Topic already exists: " + name);
            } else {
                throw new RuntimeException("Failed to create topic: " + e.getMessage(), e);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while creating topic", e);
        }
    }
    
    public void createTopicWithConfig(String name, Map<String, String> configs) {
        NewTopic newTopic = new NewTopic(name, 3, (short) 1).configs(configs);
        
        CreateTopicsResult result = adminClient.createTopics(Collections.singleton(newTopic));
        
        try {
            result.all().get();
            System.out.println("Topic created with config: " + name);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to create topic: " + e.getMessage(), e);
        }
    }
    
    public void listTopics() {
        ListTopicsResult result = adminClient.listTopics();
        
        try {
            Set<String> topics = result.names().get();
            System.out.println("=== Available Topics ===");
            topics.forEach(topic -> System.out.println("  - " + topic));
            System.out.println("========================");
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to list topics: " + e.getMessage(), e);
        }
    }
    
    public void describeTopic(String topicName) {
        DescribeTopicsResult result = adminClient.describeTopics(Collections.singleton(topicName));
        
        try {
            TopicDescription description = result.topicTopicNames().get().get(topicName);
            
            System.out.println("=== Topic: " + topicName + " ===");
            System.out.println("Topic ID: " + description.topicId());
            System.out.println("Internal: " + description.isInternal());
            System.out.println("Partitions:");
            
            for (TopicPartitionInfo partition : description.partitions()) {
                System.out.println("  Partition " + partition.partition() + ":");
                System.out.println("    Leader: " + partition.leader());
                System.out.println("    Replicas: " + partition.replicas());
                System.out.println("    ISR: " + partition.isr());
            }
            System.out.println("============================");
            
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to describe topic: " + e.getMessage(), e);
        }
    }
    
    public void updateTopicConfig(String topicName, Map<String, String> newConfigs) {
        Map<ConfigResource, Config> configUpdates = new HashMap<>();
        ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, topicName);
        
        Config config = new Config(newConfigs.entrySet().stream()
            .map(e -> new ConfigEntry(e.getKey(), e.getValue()))
            .collect(Collectors.toList()));
        
        configUpdates.put(resource, config);
        
        AlterConfigsResult result = adminClient.alterConfigs(configUpdates);
        
        try {
            result.all().get();
            System.out.println("Topic config updated: " + topicName);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to update topic config: " + e.getMessage(), e);
        }
    }
    
    public void increasePartitions(String topicName, int newPartitionCount) {
        Map<String, NewPartitions> increases = new HashMap<>();
        increases.put(topicName, NewPartitions.increaseTo(newPartitionCount));
        
        CreatePartitionsResult result = adminClient.createPartitions(increases);
        
        try {
            result.all().get();
            System.out.println("Partitions increased for topic: " + topicName);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to increase partitions: " + e.getMessage(), e);
        }
    }
    
    public void deleteTopic(String topicName) {
        DeleteTopicsResult result = adminClient.deleteTopics(Collections.singleton(topicName));
        
        try {
            result.all().get();
            System.out.println("Topic deleted: " + topicName);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to delete topic: " + e.getMessage(), e);
        }
    }
    
    public Map<String, TopicDescription> getAllTopicDescriptions() {
        ListTopicsResult result = adminClient.listTopics();
        
        try {
            return result.topicDescriptions().get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to get topic descriptions: " + e.getMessage(), e);
        }
    }
}
```

---

## Step 3: Main Application

```java
// Main.java
package com.learning;

import com.learning.admin.TopicManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Main {
    
    private final TopicManager topicManager;
    
    public Main(TopicManager topicManager) {
        this.topicManager = topicManager;
    }
    
    @Bean
    public CommandLineRunner run() {
        return args -> {
            System.out.println("=== Kafka Topic Management Demo ===\n");
            
            topicManager.listTopics();
            
            System.out.println("\n--- Creating a new topic ---");
            topicManager.createTopic("demo-topic", 3, 1);
            
            System.out.println("\n--- Creating topic with retention config ---");
            Map<String, String> config = new HashMap<>();
            config.put("retention.ms", "86400000");
            config.put("cleanup.policy", "delete");
            topicManager.createTopicWithConfig("demo-topic-retention", config);
            
            System.out.println("\n--- Describing topics ---");
            topicManager.describeTopic("orders");
            topicManager.describeTopic("notifications");
            
            System.out.println("\n--- Listing all topics ---");
            topicManager.listTopics();
            
            System.out.println("\nTopic management demo completed!");
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
cd 21-kafka
mvn clean compile
mvn spring-boot:run
```

---

# Mini-Project 4: Stream Processing (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Kafka Streams, KStream, KTable, Stateful Operations, Windowing

This mini-project focuses on implementing Kafka Streams processing applications.

---

## Project Structure

```
21-kafka/
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── streams/
│   │   ├── OrderProcessingStream.java
│   │   ├── AggregationStream.java
│   │   └── WindowedStream.java
│   └── model/
│       └── Order.java
└── src/main/resources/
    └── application.yml
```

---

## Step 1: Order Processing Stream

```java
// streams/OrderProcessingStream.java
package com.learning.streams;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.model.Order;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

@Configuration
public class OrderProcessingStream {
    
    @Value("${kafka.topic.orders:orders}")
    private String ordersTopic;
    
    @Value("${kafka.topic.processed-orders:processed-orders}")
    private String processedOrdersTopic;
    
    @Bean
    public KStream<String, Order> orderProcessingStream(StreamsBuilder builder, ObjectMapper objectMapper) {
        JsonSerde<Order> orderSerde = new JsonSerde<>(Order.class, objectMapper);
        
        KStream<String, Order> ordersStream = builder.stream(
            ordersTopic,
            Consumed.with(Serdes.String(), orderSerde)
        );
        
        KStream<String, Order> filteredOrders = ordersStream
            .filter((key, order) -> order.getTotalAmount().doubleValue() > 0);
        
        KStream<String, Order> validatedOrders = filteredOrders
            .filter((key, order) -> order.getCustomerId() != null);
        
        KStream<String, Order> enrichedOrders = validatedOrders
            .mapValues(order -> {
                order.setStatus("PROCESSED");
                return order;
            });
        
        enrichedOrders
            .peek((key, order) -> System.out.println("Processing order: " + order.getOrderId()))
            .to(processedOrdersTopic);
        
        return enrichedOrders;
    }
}
```

---

## Step 2: Aggregation Stream

```java
// streams/AggregationStream.java
package com.learning.streams;

import com.learning.model.Order;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AggregationStream {
    
    @Value("${kafka.topic.orders:orders}")
    private String ordersTopic;
    
    @Bean
    public KTable<String, OrderCount> orderCountTable(StreamsBuilder builder) {
        KStream<String, Order> ordersStream = builder.stream(ordersTopic);
        
        return ordersStream
            .groupBy((key, order) -> order.getRegion())
            .aggregate(
                () -> new OrderCount(),
                (key, order, count) -> count.increment(order),
                Materialized.as("order-counts-store")
            );
    }
    
    @Bean
    public KTable<String, Double> revenueByRegion(StreamsBuilder builder) {
        KStream<String, Order> ordersStream = builder.stream(ordersTopic);
        
        return ordersStream
            .groupBy((key, order) -> KeyValue.pair(order.getRegion(), order.getTotalAmount().doubleValue()))
            .reduce(Double::sum, Materialized.as("revenue-by-region-store"));
    }
    
    @Bean
    public KTable<String, Long> orderCountByRegion(StreamsBuilder builder) {
        KStream<String, Order> ordersStream = builder.stream(ordersTopic);
        
        return ordersStream
            .groupBy((key, order) -> KeyValue.pair(order.getRegion(), order))
            .count(Materialized.as("order-count-by-region"));
    }
    
    public static class OrderCount {
        private long count = 0;
        private double totalRevenue = 0;
        
        public OrderCount increment(Order order) {
            this.count++;
            this.totalRevenue += order.getTotalAmount().doubleValue();
            return this;
        }
        
        public long getCount() { return count; }
        public double getTotalRevenue() { return totalRevenue; }
    }
}
```

---

## Step 3: Windowed Stream

```java
// streams/WindowedStream.java
package com.learning.streams;

import com.learning.model.Order;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.Windowed;

@Configuration
public class WindowedStream {
    
    @Value("${kafka.topic.orders:orders}")
    private String ordersTopic;
    
    @Bean
    public KTable<Windowed<String>, Double> hourlyRevenueStream(StreamsBuilder builder) {
        KStream<String, Order> ordersStream = builder.stream(ordersTopic);
        
        return ordersStream
            .filter((key, order) -> "PROCESSED".equals(order.getStatus()))
            .groupBy((key, order) -> KeyValue.pair(order.getRegion(), order.getTotalAmount().doubleValue()))
            .windowedBy(TimeWindows.of(Duration.ofHours(1)))
            .reduce(Double::sum, Materialized.as("hourly-revenue-store"));
    }
    
    @Bean
    public KTable<Windowed<String>, Long> orderCountPerHour(StreamsBuilder builder) {
        KStream<String, Order> ordersStream = builder.stream(ordersTopic);
        
        return ordersStream
            .groupBy((key, order) -> KeyValue.pair(order.getRegion(), order))
            .windowedBy(TimeWindows.of(Duration.ofHours(1)).grace(Duration.ofMinutes(5)))
            .count(Materialized.as("order-count-per-hour"));
    }
    
    @Bean
    public KTable<Windowed<String>, Double> sessionRevenue(StreamsBuilder builder) {
        KStream<String, Order> ordersStream = builder.stream(ordersTopic);
        
        return ordersStream
            .groupBy((key, order) -> KeyValue.pair(String.valueOf(order.getCustomerId()), order.getTotalAmount().doubleValue()))
            .windowedBy(SessionWindows.of(Duration.ofMinutes(30)).grace(Duration.ofMinutes(5)))
            .reduce(Double::sum, Materialized.as("session-revenue-store"));
    }
}
```

---

## Build Instructions

```bash
cd 21-kafka
mvn clean compile
mvn spring-boot:run
```

---

# Real-World Project: Event Streaming Platform

## Project Overview

**Duration**: 10+ hours  
**Difficulty**: Advanced  
**Concepts Used**: Multi-topic streams, Exactly-once semantics, Complex joins, State stores, Topology testing

This comprehensive project implements a complete event streaming platform for e-commerce.

---

## Project Structure

```
21-kafka/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── config/
│   │   ├── KafkaConfig.java
│   │   └── StreamsConfig.java
│   ├── streams/
│   │   ├── OrderEnrichmentStream.java
│   │   ├── RealTimeAnalyticsStream.java
│   │   └── InventoryAlertStream.java
│   ├── service/
│   │   └── StreamProcessingService.java
│   └── model/
│       ├── Order.java
│       ├── Customer.java
│       └── EnrichedOrder.java
└── src/main/resources/
    └── application.yml
```

---

## Step 1: Advanced Streams Configuration

```java
// config/StreamsConfig.java
package com.learning.config;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafkaStreams
public class StreamsConfig {
    
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    
    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kafkaStreamsConfiguration() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "ecommerce-streams-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 3);
        props.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp/kafka-streams");
        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);
        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 1);
        return new KafkaStreamsConfiguration(props);
    }
}
```

---

## Step 2: Order Enrichment Stream

```java
// streams/OrderEnrichmentStream.java
package com.learning.streams;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.model.Customer;
import com.learning.model.EnrichedOrder;
import com.learning.model.Order;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

@Configuration
public class OrderEnrichmentStream {
    
    @Value("${kafka.topic.orders:orders}")
    private String ordersTopic;
    
    @Value("${kafka.topic.customers:customers}")
    private String customersTopic;
    
    @Value("${kafka.topic.enriched-orders:enriched-orders}")
    private String enrichedOrdersTopic;
    
    @Bean
    public KafkaStreams orderEnrichmentStream(StreamsBuilder builder, ObjectMapper objectMapper) {
        JsonSerde<Order> orderSerde = new JsonSerde<>(Order.class, objectMapper);
        JsonSerde<Customer> customerSerde = new JsonSerde<>(Customer.class, objectMapper);
        JsonSerde<EnrichedOrder> enrichedOrderSerde = new JsonSerde<>(EnrichedOrder.class, objectMapper);
        
        KStream<String, Order> ordersStream = builder.stream(ordersTopic, 
            Consumed.with(Serdes.String(), orderSerde));
        
        KTable<String, Customer> customersTable = builder.table(customersTopic,
            Consumed.with(Serdes.String(), customerSerde),
            Materialized.as("customers-store"));
        
        KStream<String, EnrichedOrder> enrichedOrders = ordersStream
            .leftJoin(customersTable,
                (order, customer) -> enrichOrder(order, customer),
                Joined.with(Serdes.String(), orderSerde, customerSerde));
        
        enrichedOrders
            .peek((key, order) -> System.out.println("Enriched order: " + order.getOrderId()))
            .to(enrichedOrdersTopic, Produced.with(Serdes.String(), enrichedOrderSerde));
        
        return new KafkaStreams(builder.build(), getStreamsConfig());
    }
    
    private EnrichedOrder enrichOrder(Order order, Customer customer) {
        EnrichedOrder enriched = new EnrichedOrder();
        enriched.setOrderId(order.getOrderId());
        enriched.setCustomerId(order.getCustomerId());
        enriched.setTotalAmount(order.getTotalAmount());
        enriched.setStatus(order.getStatus());
        enriched.setRegion(order.getRegion());
        enriched.setCreatedAt(order.getCreatedAt());
        
        if (customer != null) {
            enriched.setCustomerName(customer.getName());
            enriched.setCustomerEmail(customer.getEmail());
            enriched.setCustomerTier(customer.getTier());
        }
        
        return enriched;
    }
    
    private org.apache.kafka.streams.KafkaStreams getStreamsConfig() {
        return new org.apache.kafka.streams.KafkaStreams(
            new org.apache.kafka.streams.StreamsBuilder().build(),
            new java.util.Properties() {{
                put(org.apache.kafka.streams.StreamsConfig.APPLICATION_ID_CONFIG, "order-enrichment");
                put(org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            }}
        );
    }
}
```

---

## Step 3: Real-Time Analytics Stream

```java
// streams/RealTimeAnalyticsStream.java
package com.learning.streams;

import com.learning.model.EnrichedOrder;
import com.learning.model.Order;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.time.Duration;
import java.time.Windowed;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class RealTimeAnalyticsStream {
    
    @Value("${kafka.topic.enriched-orders:enriched-orders}")
    private String enrichedOrdersTopic;
    
    @Value("${kafka.topic.order-stats:order-stats}")
    private String orderStatsTopic;
    
    private final Map<String, AtomicInteger> revenueAlerts = new HashMap<>();
    
    @Bean
    public KafkaStreams realTimeAnalyticsStream(StreamsBuilder builder, ObjectMapper objectMapper) {
        JsonSerde<EnrichedOrder> enrichedOrderSerde = new JsonSerde<>(EnrichedOrder.class, objectMapper);
        
        KStream<String, EnrichedOrder> ordersStream = builder.stream(enrichedOrdersTopic,
            Consumed.with(Serdes.String(), enrichedOrderSerde));
        
        KTable<Windowed<String>, OrderStats> hourlyStats = ordersStream
            .filter((key, order) -> order.getStatus().equals("PROCESSED"))
            .groupBy((key, order) -> KeyValue.pair(order.getRegion(), order))
            .windowedBy(TimeWindows.of(Duration.ofHours(1)))
            .aggregate(
                () -> new OrderStats(),
                (key, order, stats) -> stats.add(order),
                Materialized.as("hourly-stats-store")
            );
        
        hourlyStats
            .toStream()
            .filter((key, stats) -> stats.getOrderCount() > 10)
            .foreach((key, stats) -> System.out.println(
                "Hour " + key.window().startTime() + ": " + key.key() + 
                " had " + stats.getOrderCount() + " orders, $" + stats.getTotalRevenue()));
        
        KTable<Windowed<String>, Long> orderCounts = ordersStream
            .groupBy((key, order) -> KeyValue.pair(order.getRegion(), order))
            .windowedBy(TimeWindows.of(Duration.ofMinutes(5)))
            .count();
        
        orderCounts
            .toStream()
            .filter((key, count) -> count >= 5)
            .foreach((key, count) -> System.out.println("Alert: High order volume in " + key.key()));
        
        return new KafkaStreams(builder.build(), getStreamsProperties());
    }
    
    private Properties getStreamsProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "realtime-analytics");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);
        return props;
    }
    
    public static class OrderStats {
        private long orderCount = 0;
        private double totalRevenue = 0;
        private double avgOrderValue = 0;
        
        public OrderStats add(EnrichedOrder order) {
            this.orderCount++;
            this.totalRevenue += order.getTotalAmount().doubleValue();
            this.avgOrderValue = totalRevenue / orderCount;
            return this;
        }
        
        public long getOrderCount() { return orderCount; }
        public double getTotalRevenue() { return totalRevenue; }
        public double getAvgOrderValue() { return avgOrderValue; }
    }
}
```

---

## Build Instructions

```bash
# Start Kafka with KRaft mode for exactly-once
docker-compose up -d

cd 21-kafka
mvn clean compile
mvn spring-boot:run
```

---

## Build Instructions (All Projects)

```bash
# Start Kafka
docker run -p 9092:9092 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 confluentinc/cp-kafka

cd 21-kafka
mvn clean compile
mvn spring-boot:run

# Monitor topics
kafka-topics.sh --list --bootstrap-server localhost:9092
kafka-console-consumer.sh --topic orders --from-beginning --bootstrap-server localhost:9092
```