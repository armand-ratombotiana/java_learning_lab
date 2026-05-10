# Message-Oriented Systems Projects - Module 8

This module covers message-oriented systems, JMS (Java Message Service), message brokers, and asynchronous communication patterns in Java applications.

## Mini-Project: JMS Point-to-Point Messaging (2-4 hours)

### Overview
Implement a point-to-point messaging system using Apache ActiveMQ and JMS to demonstrate reliable message delivery between producer and consumer services.

### Project Structure
```
jms-p2p-demo/
├── pom.xml
├── src/main/java/com/learning/messaging/
│   ├── JmsProducerApplication.java
│   ├── JmsConsumerApplication.java
│   ├── OrderMessage.java
│   └── OrderProcessor.java
├── src/main/resources/
│   └── application.properties
└── docker-compose.yml
```

### Implementation

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>jms-p2p-demo</artifactId>
    <version>1.0.0</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>21</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-activemq</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
    </dependencies>
</project>
```

#### OrderMessage.java
```java
package com.learning.messaging;

import java.io.Serializable;
import java.time.Instant;

public class OrderMessage implements Serializable {
    
    private String orderId;
    private String customerId;
    private String productId;
    private int quantity;
    private double price;
    private String status;
    private Instant timestamp;
    
    public OrderMessage() {
        this.timestamp = Instant.now();
    }
    
    public OrderMessage(String orderId, String customerId, String productId, 
                        int quantity, double price) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.status = "PENDING";
        this.timestamp = Instant.now();
    }
    
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    
    @Override
    public String toString() {
        return "OrderMessage{orderId='" + orderId + "', customerId='" + 
               customerId + "', productId='" + productId + "', quantity=" + 
               quantity + ", price=" + price + ", status='" + status + "'}";
    }
}
```

#### JmsProducerApplication.java
```java
package com.learning.messaging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@SpringBootApplication
public class JmsProducerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(JmsProducerApplication.class, args);
    }
}

@RestController
@RequestMapping("/api/orders")
class OrderProducerController {
    
    private final JmsTemplate jmsTemplate;
    
    public OrderProducerController(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }
    
    @PostMapping
    public String sendOrder(@RequestBody OrderMessage order) {
        if (order.getOrderId() == null) {
            order.setOrderId(UUID.randomUUID().toString());
        }
        
        jmsTemplate.convertAndSend("order-queue", order);
        System.out.println("Sent order: " + order);
        
        return order.getOrderId();
    }
    
    @PostMapping("/batch")
    public int sendBatchOrders(@RequestBody OrderMessage[] orders) {
        int count = 0;
        for (OrderMessage order : orders) {
            if (order.getOrderId() == null) {
                order.setOrderId(UUID.randomUUID().toString());
            }
            jmsTemplate.convertAndSend("order-queue", order);
            count++;
        }
        System.out.println("Sent " + count + " orders");
        return count;
    }
}
```

#### JmsConsumerApplication.java
```java
package com.learning.messaging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.time.Instant;

@SpringBootApplication
public class JmsConsumerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(JmsConsumerApplication.class, args);
    }
}

@Component
class OrderProcessor {
    
    @JmsListener(destination = "order-queue")
    public void processOrder(OrderMessage order) {
        System.out.println("Received order: " + order);
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        order.setStatus("PROCESSED");
        System.out.println("Processed order: " + order);
    }
}

@Component
class OrderStatusController {
    
    @JmsListener(destination = "order-status-queue")
    public void getOrderStatus(String orderId) {
        System.out.println("Checking status for order: " + orderId);
    }
}
```

#### application.properties
```properties
spring.activemq.broker-url=tcp://localhost:61616
spring.activemq.user=admin
spring.activemq.password=admin
spring.activemq.in-memory=false

server.port=8080
```

#### docker-compose.yml
```yaml
version: '3.8'
services:
  activemq:
    image: apache/activemq-classic:5.18.0
    ports:
      - "61616:61616"
      - "8161:8161"
    environment:
      ACTIVEMQ_USER: admin
      ACTIVEMQ_PASSWORD: admin
    volumes:
      - activemq-data:/data
      - activemq-log:/log

volumes:
  activemq-data:
  activemq-log:
```

### Build and Run Instructions
```bash
# Start ActiveMQ
docker-compose up -d

# Build the project
cd jms-p2p-demo
mvn clean package -DskipTests

# Run producer (terminal 1)
java -jar target/jms-p2p-demo-1.0.0.jar --server.port=8081

# Run consumer (terminal 2)
java -jar target/jms-p2p-demo-1.0.0.jar --server.port=8082

# Test the producer
curl -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId":"C001","productId":"P001","quantity":2,"price":99.99}'

# Send batch orders
curl -X POST http://localhost:8081/api/orders/batch \
  -H "Content-Type: application/json" \
  -d '[{"customerId":"C002","productId":"P002","quantity":1,"price":49.99},{"customerId":"C003","productId":"P003","quantity":3,"price":29.99}]'

# Check ActiveMQ web console
# http://localhost:8161 (admin/admin)
```

---

## Real-World Project: Event-Driven Order Processing System (8+ hours)

### Overview
Build a complete event-driven order processing system using Apache Kafka for high-throughput messaging, with multiple consumer groups, exactly-once semantics, and event sourcing patterns.

### Architecture
- **Message Broker**: Apache Kafka with multiple partitions
- **Producers**: REST API for order submission, inventory service
- **Consumers**: Order fulfillment, notification service, analytics
- **Database**: PostgreSQL for order persistence
- **Monitoring**: Prometheus and Grafana for metrics

### Project Structure
```
order-processing-system/
├── order-api/
│   ├── pom.xml
│   ├── src/main/java/com/learning/order/
│   │   ├── OrderApiApplication.java
│   │   ├── controller/OrderController.java
│   │   ├── model/Order.java
│   │   ├── service/OrderProducerService.java
│   │   └── config/KafkaProducerConfig.java
│   └── src/main/resources/application.yml
├── order-processor/
│   ├── pom.xml
│   ├── src/main/java/com/learning/processor/
│   │   ├── OrderProcessorApplication.java
│   │   ├── service/OrderProcessingService.java
│   │   ├── service/InventoryService.java
│   │   ├── service/NotificationService.java
│   │   ├── listener/OrderEventListener.java
│   │   └── config/KafkaConsumerConfig.java
│   └── src/main/resources/application.yml
├── inventory-service/
│   ├── pom.xml
│   └── src/main/java/com/learning/inventory/
├── notification-service/
│   ├── pom.xml
│   └── src/main/java/com/learning/notification/
├── docker-compose.yml
└── kubernetes/
    ├── deployment.yaml
    └── service.yaml
```

### Implementation

#### Order API - pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>order-api</artifactId>
    <version>1.0.0</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>21</java.version>
        <spring-kafka.version>3.1.0</spring-kafka.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.datatype</groupId>
            <artifactId>jackson-datatype-jsr310</artifactId>
        </dependency>
    </dependencies>
</project>
```

#### Order Model
```java
package com.learning.order.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String orderId;
    
    @Column(nullable = false)
    private String customerId;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    private BigDecimal totalAmount;
    
    private Instant createdAt;
    private Instant updatedAt;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> items;
    
    public Order() {
        this.createdAt = Instant.now();
        this.status = OrderStatus.CREATED;
    }
    
    public enum OrderStatus {
        CREATED, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}

@Entity
@Table(name = "order_items")
class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    
    private String productId;
    private int quantity;
    private BigDecimal unitPrice;
    
    public OrderItem() {}
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}
```

#### Order Producer Service
```java
package com.learning.order.service;

import com.learning.order.model.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.CompletableFuture;

@Service
public class OrderProducerService {
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String ORDER_EVENTS_TOPIC = "order-events";
    
    public OrderProducerService(KafkaTemplate<String, String> kafkaTemplate,
                                ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
    
    public CompletableFuture<SendResult<String, String>> sendOrderEvent(Order order, 
                                                                        String eventType) {
        try {
            OrderEvent event = new OrderEvent(
                eventType,
                order.getOrderId(),
                order.getCustomerId(),
                order.getStatus().name(),
                order.getTotalAmount().toString(),
                System.currentTimeMillis()
            );
            
            String eventJson = objectMapper.writeValueAsString(event);
            
            return kafkaTemplate.send(ORDER_EVENTS_TOPIC, order.getOrderId(), eventJson);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize order event", e);
        }
    }
    
    public record OrderEvent(
        String eventType,
        String orderId,
        String customerId,
        String status,
        String totalAmount,
        long timestamp
    ) {}
}
```

#### Order Controller
```java
package com.learning.order.controller;

import com.learning.order.model.Order;
import com.learning.order.service.OrderProducerService;
import com.learning.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private final OrderService orderService;
    private final OrderProducerService producerService;
    
    public OrderController(OrderService orderService, 
                          OrderProducerService producerService) {
        this.orderService = orderService;
        this.producerService = producerService;
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(
            @Valid @RequestBody OrderRequest request) {
        
        Order order = orderService.createOrder(request);
        producerService.sendOrderEvent(order, "ORDER_CREATED");
        
        return ResponseEntity.ok(Map.of(
            "orderId", order.getOrderId(),
            "status", order.getStatus().name(),
            "message", "Order created successfully"
        ));
    }
    
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable String orderId) {
        return orderService.getOrderByOrderId(orderId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{orderId}/confirm")
    public ResponseEntity<Map<String, String>> confirmOrder(@PathVariable String orderId) {
        return orderService.getOrderByOrderId(orderId)
            .map(order -> {
                order.setStatus(Order.OrderStatus.CONFIRMED);
                orderService.save(order);
                producerService.sendOrderEvent(order, "ORDER_CONFIRMED");
                return ResponseEntity.ok(Map.of("status", "CONFIRMED"));
            })
            .orElse(ResponseEntity.notFound().build());
    }
}

record OrderRequest(String customerId, List<OrderItemRequest> items) {}

record OrderItemRequest(String productId, int quantity) {}
```

#### Kafka Consumer Configuration
```java
package com.learning.processor.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {
    
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;
    
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, 
                  StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, 
                  StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        return new DefaultKafkaConsumerFactory<>(props);
    }
    
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> 
            kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3);
        return factory;
    }
}
```

#### Order Event Listener
```java
package com.learning.processor.listener;

import com.learning.processor.service.OrderProcessingService;
import com.learning.order.service.OrderProducerService.OrderEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class OrderEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderEventListener.class);
    
    private final ObjectMapper objectMapper;
    private final OrderProcessingService processingService;
    
    public OrderEventListener(ObjectMapper objectMapper, 
                             OrderProcessingService processingService) {
        this.objectMapper = objectMapper;
        this.processingService = processingService;
    }
    
    @KafkaListener(topics = "order-events", groupId = "order-processor-group")
    public void handleOrderEvent(String message) {
        try {
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
            logger.info("Received order event: {}", event);
            
            switch (event.eventType()) {
                case "ORDER_CREATED" -> processingService.processNewOrder(event);
                case "ORDER_CONFIRMED" -> processingService.handleConfirmation(event);
                default -> logger.warn("Unknown event type: {}", event.eventType());
            }
        } catch (Exception e) {
            logger.error("Failed to process message: {}", message, e);
        }
    }
}
```

#### Processing Service
```java
package com.learning.processor.service;

import com.learning.order.service.OrderProducerService.OrderEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.UUID;

@Service
public class OrderProcessingService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderProcessingService.class);
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final InventoryService inventoryService;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;
    
    public OrderProcessingService(KafkaTemplate<String, String> kafkaTemplate,
                                  InventoryService inventoryService,
                                  NotificationService notificationService,
                                  ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.inventoryService = inventoryService;
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }
    
    public void processNewOrder(OrderEvent event) {
        logger.info("Processing new order: {}", event.orderId());
        
        boolean inventoryReserved = inventoryService.reserveInventory(
            event.orderId(), event.customerId());
        
        if (inventoryReserved) {
            notificationService.sendOrderConfirmation(event.customerId(), event.orderId());
            publishOrderProcessedEvent(event);
        } else {
            publishOrderFailedEvent(event, "Inventory reservation failed");
        }
    }
    
    public void handleConfirmation(OrderEvent event) {
        logger.info("Handling order confirmation: {}", event.orderId());
        inventoryService.confirmReservation(event.orderId());
        notificationService.sendShippingNotification(event.customerId(), event.orderId());
    }
    
    private void publishOrderProcessedEvent(OrderEvent event) {
        try {
            String message = objectMapper.writeValueAsString(Map.of(
                "eventType", "ORDER_PROCESSED",
                "orderId", event.orderId(),
                "timestamp", System.currentTimeMillis()
            ));
            kafkaTemplate.send("order-status-updates", event.orderId(), message);
        } catch (Exception e) {
            logger.error("Failed to publish processed event", e);
        }
    }
    
    private void publishOrderFailedEvent(OrderEvent event, String reason) {
        try {
            String message = objectMapper.writeValueAsString(Map.of(
                "eventType", "ORDER_FAILED",
                "orderId", event.orderId(),
                "reason", reason,
                "timestamp", System.currentTimeMillis()
            ));
            kafkaTemplate.send("order-status-updates", event.orderId(), message);
        } catch (Exception e) {
            logger.error("Failed to publish failed event", e);
        }
    }
}
```

#### Inventory Service
```java
package com.learning.processor.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class InventoryService {
    
    private final ConcurrentMap<String, Inventory> inventory = new ConcurrentHashMap<>();
    
    public InventoryService() {
        inventory.put("P001", new Inventory("P001", 100));
        inventory.put("P002", new Inventory("P002", 50));
        inventory.put("P003", new Inventory("P003", 200));
    }
    
    public boolean reserveInventory(String orderId, String customerId) {
        logger.info("Reserving inventory for order: {}", orderId);
        
        for (var entry : inventory.entrySet()) {
            Inventory inv = entry.getValue();
            if (inv.availableQuantity > 0) {
                inv.availableQuantity--;
                logger.info("Reserved 1 unit of {} for order {}", 
                           entry.getKey(), orderId);
            }
        }
        return true;
    }
    
    public void confirmReservation(String orderId) {
        logger.info("Confirming inventory reservation for order: {}", orderId);
    }
    
    public void releaseReservation(String orderId) {
        logger.info("Releasing inventory reservation for order: {}", orderId);
    }
    
    private static final org.slf4j.Logger logger = 
        org.slf4j.LoggerFactory.getLogger(InventoryService.class);
    
    private static class Inventory {
        String productId;
        int availableQuantity;
        
        Inventory(String productId, int availableQuantity) {
            this.productId = productId;
            this.availableQuantity = availableQuantity;
        }
    }
}
```

#### Notification Service
```java
package com.learning.processor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    public void sendOrderConfirmation(String customerId, String orderId) {
        logger.info("Sending order confirmation email to customer: {} for order: {}", 
                   customerId, orderId);
    }
    
    public void sendShippingNotification(String customerId, String orderId) {
        logger.info("Sending shipping notification to customer: {} for order: {}", 
                   customerId, orderId);
    }
    
    public void sendOrderCancelledNotification(String customerId, String orderId) {
        logger.info("Sending order cancellation to customer: {} for order: {}", 
                   customerId, orderId);
    }
}
```

#### docker-compose.yml
```yaml
version: '3.8'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"
  
  kafka:
    image: confluentinc/cp-kafka:7.5.0
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
  
  order-api:
    build: ./order-api
    ports:
      - "8080:8080"
    environment:
      SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/orders
  
  order-processor:
    build: ./order-processor
    environment:
      SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
  
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: orders
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
  
  prometheus:
    image: prometheus:latest
    ports:
      - "9091:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml

volumes:
  postgres-data:
```

### Build and Run Instructions
```bash
# Start all services
docker-compose up -d

# Build all modules
mvn clean package

# Run order API
java -jar order-api/target/order-api-1.0.0.jar

# Run order processor
java -jar order-processor/target/order-processor-1.0.0.jar

# Create order
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId":"C001","items":[{"productId":"P001","quantity":2}]}'

# Check Kafka topics
docker exec -it <kafka-container> kafka-topics --list --bootstrap-server localhost:9092

# Monitor with Prometheus
# http://localhost:9091

# View logs
docker-compose logs -f
```

### Learning Outcomes
- Implement Kafka producers with exactly-once semantics
- Configure Kafka consumers with consumer groups
- Handle message ordering and partitioning
- Build event-driven architectures with CQRS patterns
- Implement saga patterns for distributed transactions
- Monitor Kafka metrics and troubleshoot issues