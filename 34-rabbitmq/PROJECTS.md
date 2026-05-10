# RabbitMQ Messaging Projects

This module covers RabbitMQ messaging patterns including publishers, consumers, exchanges, queues, and advanced features like dead letter queues, publisher confirms, and clustering for building robust message-driven Java applications.

## Mini-Project: Order Processing System (2-4 Hours)

### Overview

Build an order processing system using RabbitMQ that demonstrates multiple exchange types, message acknowledgment patterns, and error handling with dead letter queues.

### Technology Stack

- Java 21 with Spring Boot 3.x
- Spring AMQP for RabbitMQ integration
- Maven build system

### Project Structure

```
rabbitmq-messaging/
├── pom.xml
└── src/
    └── main/
        ├── java/com/learning/rabbitmq/
        │   ├── OrderProcessingApplication.java
        │   ├── config/
        │   │   └── RabbitMQConfig.java
        │   ├── model/
        │   │   ├── Order.java
        │   │   └── OrderEvent.java
        │   ├── publisher/
        │   │   └── OrderEventPublisher.java
        │   ├── consumer/
        │   │   ├── OrderEventConsumer.java
        │   │   └── InventoryConsumer.java
        │   └── service/
        │       └── OrderService.java
        └── resources/
            └── application.properties
```

### Implementation

**pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.learning</groupId>
        <artifactId>java-learning-parent</artifactId>
        <version>1.0.0</version>
    </parent>
    
    <artifactId>rabbitmq-order-processing</artifactId>
    
    <properties>
        <java.version>21</java.version>
        <spring-boot.version>3.2.0</spring-boot.version>
        <spring-rabbit.version>3.2.0</spring-rabbit.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
            <version>${spring-rabbit.version}</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>${spring-boot.version}</version>
            </plugin>
        </plugins>
    </build>
</project>
```

**Order.java (Model)**

```java
package com.learning.rabbitmq.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Order {
    private String orderId;
    private Long customerId;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    
    public enum OrderStatus {
        PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    }
    
    public static class OrderItem {
        private String productId;
        private String productName;
        private int quantity;
        private BigDecimal unitPrice;
        
        public OrderItem() {}
        
        public OrderItem(String productId, String productName, int quantity, BigDecimal unitPrice) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }
        
        public BigDecimal getSubtotal() {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
        
        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    }
    
    public Order() {
        this.orderId = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }
    
    public BigDecimal calculateTotal() {
        return items.stream()
            .map(OrderItem::getSubtotal)
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
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
```

**OrderEvent.java**

```java
package com.learning.rabbitmq.model;

import java.time.LocalDateTime;

public class OrderEvent {
    private String eventId;
    private String orderId;
    private EventType eventType;
    private Order.OrderStatus previousStatus;
    private Order.OrderStatus newStatus;
    private String payload;
    private LocalDateTime timestamp;
    
    public enum EventType {
        ORDER_CREATED, ORDER_CONFIRMED, ORDER_PROCESSING, ORDER_SHIPPED, 
        ORDER_DELIVERED, ORDER_CANCELLED, PAYMENT_PROCESSED, INVENTORY_RESERVED
    }
    
    public OrderEvent() {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
    }
    
    public OrderEvent(String orderId, EventType eventType, Order.OrderStatus newStatus) {
        this();
        this.orderId = orderId;
        this.eventType = eventType;
        this.newStatus = newStatus;
    }
    
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }
    public Order.OrderStatus getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(Order.OrderStatus previousStatus) { this.previousStatus = previousStatus; }
    public Order.OrderStatus getNewStatus() { return newStatus; }
    public void setNewStatus(Order.OrderStatus newStatus) { this.newStatus = newStatus; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
```

**RabbitMQConfig.java**

```java
package com.learning.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String PAYMENT_EXCHANGE = "payment.exchange";
    public static final String INVENTORY_EXCHANGE = "inventory.exchange";
    public static final String DLX_EXCHANGE = "dlx.exchange";
    
    public static final String ORDER_QUEUE = "order.queue";
    public static final String PAYMENT_QUEUE = "payment.queue";
    public static final String INVENTORY_QUEUE = "inventory.queue";
    public static final String DLQ_QUEUE = "dlq.queue";
    
    public static final String ORDER_DLQ = "order.dlq";
    public static final String PAYMENT_DLQ = "payment.dlq";
    
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE, true, false);
    }
    
    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange(PAYMENT_EXCHANGE, true, false);
    }
    
    @Bean
    public TopicExchange inventoryExchange() {
        return new TopicExchange(INVENTORY_EXCHANGE, true, false);
    }
    
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_EXCHANGE, true, false);
    }
    
    @Bean
    public Queue orderQueue() {
        return QueueBuilder.durable(ORDER_QUEUE)
            .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
            .withArgument("x-dead-letter-routing-key", "dlq")
            .build();
    }
    
    @Bean
    public Queue paymentQueue() {
        return QueueBuilder.durable(PAYMENT_QUEUE)
            .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
            .withArgument("x-dead-letter-routing-key", "dlq")
            .build();
    }
    
    @Bean
    public Queue inventoryQueue() {
        return QueueBuilder.durable(INVENTORY_QUEUE)
            .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
            .withArgument("x-dead-letter-routing-key", "dlq")
            .build();
    }
    
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ_QUEUE).build();
    }
    
    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(orderQueue())
            .to(orderExchange())
            .with("order.created");
    }
    
    @Bean
    public Binding paymentBinding() {
        return BindingBuilder.bind(paymentQueue())
            .to(paymentExchange())
            .with("payment.processed");
    }
    
    @Bean
    public Binding inventoryBinding() {
        return BindingBuilder.bind(inventoryQueue())
            .to(inventoryExchange())
            .with("inventory.#");
    }
    
    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(deadLetterQueue())
            .to(deadLetterExchange())
            .with("dlq");
    }
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
```

**OrderEventPublisher.java**

```java
package com.learning.rabbitmq.publisher;

import com.learning.rabbitmq.config.RabbitMQConfig;
import com.learning.rabbitmq.model.OrderEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    
    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    public void publishOrderCreated(String orderId, String payload) {
        OrderEvent event = new OrderEvent(
            orderId, 
            OrderEvent.EventType.ORDER_CREATED, 
            com.learning.rabbitmq.model.Order.OrderStatus.PENDING
        );
        event.setPayload(payload);
        
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.ORDER_EXCHANGE,
            "order.created",
            event
        );
        
        System.out.println("Published ORDER_CREATED event for order: " + orderId);
    }
    
    public void publishPaymentProcessed(String orderId, String payload) {
        OrderEvent event = new OrderEvent(
            orderId,
            OrderEvent.EventType.PAYMENT_PROCESSED,
            com.learning.rabbitmq.model.Order.OrderStatus.CONFIRMED
        );
        event.setPayload(payload);
        
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.PAYMENT_EXCHANGE,
            "payment.processed",
            event
        );
        
        System.out.println("Published PAYMENT_PROCESSED event for order: " + orderId);
    }
    
    public void publishInventoryReserved(String orderId, String productId, int quantity) {
        String routingKey = "inventory.reserve." + productId;
        OrderEvent event = new OrderEvent(
            orderId,
            OrderEvent.EventType.INVENTORY_RESERVED,
            com.learning.rabbitmq.model.Order.OrderStatus.PROCESSING
        );
        event.setPayload(productId + ":" + quantity);
        
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.INVENTORY_EXCHANGE,
            routingKey,
            event
        );
        
        System.out.println("Published INVENTORY_RESERVED event for order: " + orderId);
    }
    
    public void publishOrderShipped(String orderId) {
        OrderEvent event = new OrderEvent(
            orderId,
            OrderEvent.EventType.ORDER_SHIPPED,
            com.learning.rabbitmq.model.Order.OrderStatus.SHIPPED
        );
        
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.ORDER_EXCHANGE,
            "order.shipped",
            event
        );
        
        System.out.println("Published ORDER_SHIPPED event for order: " + orderId);
    }
}
```

**OrderEventConsumer.java**

```java
package com.learning.rabbitmq.consumer;

import com.learning.rabbitmq.config.RabbitMQConfig;
import com.learning.rabbitmq.model.OrderEvent;
import com.learning.rabbitmq.publisher.OrderEventPublisher;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class OrderEventConsumer {
    
    private final OrderEventPublisher publisher;
    private final Random random = new Random();
    
    public OrderEventConsumer(OrderEventPublisher publisher) {
        this.publisher = publisher;
    }
    
    @RabbitListener(queues = RabbitMQConfig.ORDER_QUEUE)
    public void handleOrderCreated(OrderEvent event) {
        System.out.println("Received ORDER_CREATED event: " + event.getEventId());
        System.out.println("  Order ID: " + event.getOrderId());
        System.out.println("  Timestamp: " + event.getTimestamp());
        
        if (random.nextInt(10) < 2) {
            System.out.println("  Simulated processing failure - sending to DLQ");
            throw new RuntimeException("Simulated processing failure");
        }
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        publisher.publishPaymentProcessed(
            event.getOrderId(),
            "Payment processed for " + event.getOrderId()
        );
        
        System.out.println("Processed ORDER_CREATED event, published PAYMENT_PROCESSED");
    }
    
    @RabbitListener(queues = RabbitMQConfig.PAYMENT_QUEUE)
    public void handlePaymentProcessed(OrderEvent event) {
        System.out.println("Received PAYMENT_PROCESSED event: " + event.getEventId());
        System.out.println("  Order ID: " + event.getOrderId());
        System.out.println("  New Status: " + event.getNewStatus());
        
        if (random.nextInt(10) < 2) {
            System.out.println("  Simulated payment failure - sending to DLQ");
            throw new RuntimeException("Payment processing failed");
        }
        
        publisher.publishInventoryReserved(
            event.getOrderId(),
            "PROD-" + random.nextInt(100),
            random.nextInt(5) + 1
        );
        
        System.out.println("Processed PAYMENT_PROCESSED event");
    }
}
```

**InventoryConsumer.java**

```java
package com.learning.rabbitmq.consumer;

import com.learning.rabbitmq.config.RabbitMQConfig;
import com.learning.rabbitmq.model.OrderEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryConsumer {
    
    @RabbitListener(queues = RabbitMQConfig.INVENTORY_QUEUE)
    public void handleInventoryReservation(OrderEvent event) {
        System.out.println("Received INVENTORY_RESERVED event: " + event.getEventId());
        System.out.println("  Order ID: " + event.getOrderId());
        System.out.println("  Payload: " + event.getPayload());
        System.out.println("  Routing Key: inventory.reserve.*");
        
        System.out.println("Reserved inventory for order: " + event.getOrderId());
        
        System.out.println("Processed INVENTORY_RESERVED event");
    }
}
```

**OrderService.java**

```java
package com.learning.rabbitmq.service;

import com.learning.rabbitmq.model.Order;
import com.learning.rabbitmq.publisher.OrderEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    
    private final OrderEventPublisher publisher;
    
    public OrderService(OrderEventPublisher publisher) {
        this.publisher = publisher;
    }
    
    public Order createOrder(Long customerId, List<Order.OrderItem> items) {
        Order order = new Order();
        order.setCustomerId(customerId);
        order.setItems(items);
        order.setTotalAmount(order.calculateTotal());
        
        publisher.publishOrderCreated(order.getOrderId(), toJson(order));
        
        return order;
    }
    
    public void processOrder(String orderId) {
        publisher.publishPaymentProcessed(
            orderId,
            "Payment initiated for order: " + orderId
        );
    }
    
    public void shipOrder(String orderId) {
        publisher.publishOrderShipped(orderId);
    }
    
    private String toJson(Order order) {
        return String.format(
            "{\"orderId\":\"%s\",\"customerId\":%d,\"totalAmount\":%s}",
            order.getOrderId(),
            order.getCustomerId(),
            order.getTotalAmount()
        );
    }
}
```

**OrderProcessingApplication.java**

```java
package com.learning.rabbitmq;

import com.learning.rabbitmq.model.Order;
import com.learning.rabbitmq.service.OrderService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.List;

@SpringBootApplication
public class OrderProcessingApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(OrderProcessingApplication.class, args);
    }
    
    @Bean
    CommandLineRunner demo(OrderService service) {
        return args -> {
            System.out.println("=== RabbitMQ Order Processing Demo ===\n");
            
            List<Order.OrderItem> items = List.of(
                new Order.OrderItem("PROD-001", "Laptop", 1, BigDecimal.valueOf(999.99)),
                new Order.OrderItem("PROD-002", "Mouse", 2, BigDecimal.valueOf(29.99)),
                new Order.OrderItem("PROD-003", "Keyboard", 1, BigDecimal.valueOf(79.99))
            );
            
            Order order = service.createOrder(1001L, items);
            System.out.println("Created order: " + order.getOrderId());
            System.out.println("Total: $" + order.getTotalAmount());
            System.out.println("Events published to RabbitMQ\n");
            
            Thread.sleep(500);
            
            service.processOrder(order.getOrderId());
            System.out.println("Processing order: " + order.getOrderId() + "\n");
            
            Thread.sleep(500);
            
            service.shipOrder(order.getOrderId());
            System.out.println("Shipping order: " + order.getOrderId() + "\n");
            
            System.out.println("=== Demo Complete ===");
            
            System.exit(0);
        };
    }
}
```

**application.properties**

```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
spring.rabbitmq.listener.simple.prefetch=10
spring.rabbitmq.listener.simple.acknowledge-mode=manual
```

### Build and Run

```bash
# Start RabbitMQ
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management

# Build and run
cd 34-rabbitmq/rabbitmq-messaging
mvn clean package -DskipTests
mvn spring-boot:run
```

### Expected Output

```
=== RabbitMQ Order Processing Demo ===

Created order: a1b2c3d4-e5f6-7890-abcd-ef1234567890
Total: $1139.96
Events published to RabbitMQ

Published ORDER_CREATED event for order: a1b2c3d4-e5f6-7890-abcd-ef1234567890
Received ORDER_CREATED event: event-001
  Order ID: a1b2c3d4-e5f6-7890-abcd-ef1234567890
  Timestamp: 2024-01-15T10:30:00
Processed ORDER_CREATED event, published PAYMENT_PROCESSED
Published PAYMENT_PROCESSED event for order: a1b2c3d4-e5f6-7890-abcd-ef1234567890
Received PAYMENT_PROCESSED event: event-002
  Order ID: a1b2c3d4-e5f6-7890-abcd-ef1234567890
  New Status: CONFIRMED
Processed PAYMENT_PROCESSED event

=== Demo Complete ===
```

---

## Real-World Project: Event-Driven Microservices Communication (8+ Hours)

### Overview

Build a comprehensive event-driven microservices architecture using RabbitMQ that demonstrates advanced messaging patterns including publisher confirms, consumer acknowledgments, message TTL, priority queues, and clustering for high availability.

### Key Features

1. **Multi-Exchange Architecture** - Direct, Topic, Fanout exchanges
2. **Guaranteed Delivery** - Publisher confirms and consumer acknowledgments
3. **Dead Letter Handling** - DLX and DLQ for failed messages
4. **Message Priorities** - Priority-based message processing
5. **Request-Reply Pattern** - RPC-style communication
6. **Circuit Breaker** - Resilience with RetryTemplate

### Project Structure

```
rabbitmq-messaging/
├── pom.xml
└── src/
    └── main/
        ├── java/com/learning/rabbitmq/
        │   ├── MicroservicesApplication.java
        │   ├── config/
        │   │   ├── RabbitMQConfig.java
        │   │   └── CircuitBreakerConfig.java
        │   ├── model/
        │   │   ├── Product.java
        │   │   ├── InventoryCheckRequest.java
        │   │   └── InventoryCheckResponse.java
        │   ├── publisher/
        │   │   ├── AsyncEventPublisher.java
        │   │   └── RpcPublisher.java
        │   ├── consumer/
        │   │   ├── InventoryServiceConsumer.java
        │   │   └── NotificationConsumer.java
        │   └── service/
        │       ├── ProductCatalogService.java
        │       └── InventoryService.java
        └── resources/
            └── application.properties
```

### Implementation

**Advanced RabbitMQConfig.java**

```java
package com.learning.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {
    
    public static final String PRODUCT_EXCHANGE = "product.exchange";
    public static final String INVENTORY_EXCHANGE = "inventory.exchange";
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    public static final String DLX_EXCHANGE = "dlx.exchange";
    public static final String RPC_EXCHANGE = "rpc.exchange";
    
    public static final String PRODUCT_QUEUE = "product.queue";
    public static final String PRODUCT_HIGH_PRIORITY_QUEUE = "product.high.priority.queue";
    public static final String INVENTORY_QUEUE = "inventory.queue";
    public static final String NOTIFICATION_QUEUE = "notification.queue";
    public static final String DLQ_QUEUE = "dlq.queue";
    public static final String RPC_QUEUE = "rpc.queue";
    
    @Bean
    public DirectExchange productExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("alternate-exchange", "product.ae");
        return new DirectExchange(PRODUCT_EXCHANGE, true, false, args);
    }
    
    @Bean
    public FanoutExchange inventoryExchange() {
        return new FanoutExchange(INVENTORY_EXCHANGE, true, false);
    }
    
    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE, true, false);
    }
    
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_EXCHANGE, true, false);
    }
    
    @Bean
    public DirectExchange rpcExchange() {
        return new DirectExchange(RPC_EXCHANGE, true, false);
    }
    
    @Bean
    public Queue productQueue() {
        return QueueBuilder.durable(PRODUCT_QUEUE)
            .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
            .withArgument("x-dead-letter-routing-key", "dlq")
            .withArgument("x-message-ttl", 86400000)
            .build();
    }
    
    @Bean
    public Queue productHighPriorityQueue() {
        return QueueBuilder.durable(PRODUCT_HIGH_PRIORITY_QUEUE)
            .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
            .withArgument("x-max-priority", 10)
            .build();
    }
    
    @Bean
    public Queue inventoryQueue() {
        return QueueBuilder.durable(INVENTORY_QUEUE)
            .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
            .withArgument("x-message-ttl", 300000)
            .build();
    }
    
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE)
            .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
            .build();
    }
    
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ_QUEUE).build();
    }
    
    @Bean
    public Queue rpcQueue() {
        return QueueBuilder.durable(RPC_QUEUE)
            .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
            .build();
    }
    
    @Bean
    public Binding productBinding() {
        return BindingBuilder.bind(productQueue())
            .to(productExchange())
            .with("product.#");
    }
    
    @Bean
    public Binding productHighPriorityBinding() {
        return BindingBuilder.bind(productHighPriorityQueue())
            .to(productExchange())
            .with("product.priority.#");
    }
    
    @Bean
    public Binding inventoryBinding() {
        return BindingBuilder.bind(inventoryQueue())
            .to(inventoryExchange())
            .with("inventory.check");
    }
    
    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue())
            .to(notificationExchange())
            .with("notification.#");
    }
    
    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(deadLetterQueue())
            .to(deadLetterExchange())
            .with("dlq");
    }
    
    @Bean
    public Binding rpcBinding() {
        return BindingBuilder.bind(rpcQueue())
            .to(rpcExchange())
            .with("rpc");
    }
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = 
            new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setPrefetchCount(10);
        factory.setDefaultRequeueRejected(false);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }
    
    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy policy = new ExponentialBackOffPolicy();
        policy.setInitialInterval(1000);
        policy.setMultiplier(2.0);
        policy.setMaxInterval(10000);
        retryTemplate.setBackOffPolicy(policy);
        return retryTemplate;
    }
}
```

**InventoryCheckRequest.java**

```java
package com.learning.rabbitmq.model;

import java.io.Serializable;
import java.util.UUID;

public class InventoryCheckRequest implements Serializable {
    private String requestId;
    private String productId;
    private int quantity;
    private long timestamp;
    private String correlationId;
    
    public InventoryCheckRequest() {
        this.requestId = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
    }
    
    public InventoryCheckRequest(String productId, int quantity) {
        this();
        this.productId = productId;
        this.quantity = quantity;
    }
    
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
}
```

**InventoryCheckResponse.java**

```java
package com.learning.rabbitmq.model;

import java.io.Serializable;
import java.time.Instant;

public class InventoryCheckResponse implements Serializable {
    private String requestId;
    private boolean available;
    private int availableQuantity;
    private String productId;
    private long timestamp;
    private String errorMessage;
    
    public InventoryCheckResponse() {
        this.timestamp = Instant.now().toEpochMilli();
    }
    
    public static InventoryCheckResponse available(String requestId, String productId, int qty) {
        InventoryCheckResponse response = new InventoryCheckResponse();
        response.setRequestId(requestId);
        response.setProductId(productId);
        response.setAvailable(true);
        response.setAvailableQuantity(qty);
        return response;
    }
    
    public static InventoryCheckResponse unavailable(String requestId, String productId) {
        InventoryCheckResponse response = new InventoryCheckResponse();
        response.setRequestId(requestId);
        response.setProductId(productId);
        response.setAvailable(false);
        response.setErrorMessage("Insufficient inventory");
        return response;
    }
    
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
```

**AsyncEventPublisher.java**

```java
package com.learning.rabbitmq.publisher;

import com.learning.rabbitmq.config.RabbitMQConfig;
import com.learning.rabbitmq.model.Product;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AsyncEventPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    private final ConcurrentHashMap<String, ConfirmCallback> pendingConfirms = new ConcurrentHashMap<>();
    
    public interface ConfirmCallback {
        void onConfirm(UUID correlationId, boolean ack);
    }
    
    public AsyncEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            String corrId = correlationData.getId();
            ConfirmCallback callback = pendingConfirms.remove(corrId);
            if (callback != null) {
                callback.onConfirm(UUID.fromString(corrId), ack);
            }
            
            if (!ack) {
                System.out.println("Message not acknowledged: " + corrId + ", cause: " + cause);
            }
        });
    }
    
    public void publishProductUpdate(Product product, ConfirmCallback callback) {
        String correlationId = UUID.randomUUID().toString();
        
        if (callback != null) {
            pendingConfirms.put(correlationId, callback);
        }
        
        CorrelationData correlationData = new CorrelationData(correlationId);
        
        String routingKey = "product.update." + product.getCategory();
        
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.PRODUCT_EXCHANGE,
            routingKey,
            product,
            correlationData
        );
        
        System.out.println("Published product update: " + product.getId() + 
            " with correlation: " + correlationId);
    }
    
    public void publishProductUpdateWithPriority(Product product, int priority) {
        String correlationId = UUID.randomUUID().toString();
        CorrelationData correlationData = new CorrelationData(correlationId);
        
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.PRODUCT_EXCHANGE,
            "product.priority.update",
            product,
            message -> {
                message.getMessageProperties().setPriority(priority);
                return message;
            },
            correlationData
        );
        
        System.out.println("Published priority product update: " + product.getId());
    }
    
    public void publishInventoryCheck(String productId, int quantity) {
        var request = new com.learning.rabbitmq.model.InventoryCheckRequest(productId, quantity);
        
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.INVENTORY_EXCHANGE,
            "inventory.check",
            request
        );
        
        System.out.println("Published inventory check request for product: " + productId);
    }
    
    public void publishNotification(String type, String message) {
        var notification = new java.util.HashMap<String, String>();
        notification.put("type", type);
        notification.put("message", message);
        notification.put("timestamp", String.valueOf(System.currentTimeMillis()));
        
        String routingKey = "notification." + type.toLowerCase();
        
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.NOTIFICATION_EXCHANGE,
            routingKey,
            notification
        );
        
        System.out.println("Published notification: " + type);
    }
}
```

**RpcPublisher.java**

```java
package com.learning.rabbitmq.publisher;

import com.learning.rabbitmq.config.RabbitMQConfig;
import com.learning.rabbitmq.model.InventoryCheckRequest;
import com.learning.rabbitmq.model.InventoryCheckResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class RpcPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    
    public RpcPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    public CompletableFuture<InventoryCheckResponse> checkAvailabilityAsync(
            String productId, int quantity) {
        
        InventoryCheckRequest request = new InventoryCheckRequest(productId, quantity);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                InventoryCheckResponse response = rabbitTemplate.convertSendAndReceive(
                    RabbitMQConfig.RPC_EXCHANGE,
                    "rpc",
                    request,
                    InventoryCheckResponse.class,
                    5000
                );
                return response;
            } catch (Exception e) {
                throw new RuntimeException("RPC call failed: " + e.getMessage(), e);
            }
        });
    }
    
    public InventoryCheckResponse checkAvailabilitySync(String productId, int quantity) 
            throws TimeoutException, InterruptedException {
        
        InventoryCheckRequest request = new InventoryCheckRequest(productId, quantity);
        
        InventoryCheckResponse response = rabbitTemplate.convertSendAndReceive(
            RabbitMQConfig.RPC_EXCHANGE,
            "rpc",
            request,
            InventoryCheckResponse.class,
            5000
        );
        
        if (response == null) {
            throw new TimeoutException("RPC call timed out");
        }
        
        return response;
    }
}
```

**InventoryServiceConsumer.java**

```java
package com.learning.rabbitmq.consumer;

import com.learning.rabbitmq.config.RabbitMQConfig;
import com.learning.rabbitmq.model.InventoryCheckRequest;
import com.learning.rabbitmq.model.InventoryCheckResponse;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InventoryServiceConsumer {
    
    private final RabbitTemplate rabbitTemplate;
    private final Map<String, Integer> inventory = new ConcurrentHashMap<>();
    private final Random random = new Random();
    
    public InventoryServiceConsumer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        
        inventory.put("PROD-001", 100);
        inventory.put("PROD-002", 50);
        inventory.put("PROD-003", 25);
        inventory.put("PROD-004", 0);
        inventory.put("PROD-005", 200);
    }
    
    @RabbitListener(queues = RabbitMQConfig.INVENTORY_QUEUE)
    public void handleInventoryCheck(InventoryCheckRequest request) {
        System.out.println("Received inventory check: " + request.getRequestId());
        System.out.println("  Product: " + request.getProductId());
        System.out.println("  Requested quantity: " + request.getQuantity());
        
        int available = inventory.getOrDefault(request.getProductId(), 0);
        
        InventoryCheckResponse response;
        if (available >= request.getQuantity()) {
            response = InventoryCheckResponse.available(
                request.getRequestId(),
                request.getProductId(),
                available
            );
        } else {
            response = InventoryCheckResponse.unavailable(
                request.getRequestId(),
                request.getProductId()
            );
        }
        
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.INVENTORY_EXCHANGE,
            "inventory.response",
            response
        );
        
        System.out.println("Sent inventory response: available=" + response.isAvailable());
    }
    
    @RabbitListener(queues = RabbitMQConfig.RPC_QUEUE)
    public InventoryCheckResponse handleRpcRequest(InventoryCheckRequest request) {
        System.out.println("Received RPC request: " + request.getRequestId());
        
        int available = inventory.getOrDefault(request.getProductId(), 0);
        
        if (available >= request.getQuantity()) {
            inventory.put(request.getProductId(), available - request.getQuantity());
            return InventoryCheckResponse.available(
                request.getRequestId(),
                request.getProductId(),
                available - request.getQuantity()
            );
        } else {
            return InventoryCheckResponse.unavailable(
                request.getRequestId(),
                request.getProductId()
            );
        }
    }
}
```

**NotificationConsumer.java**

```java
package com.learning.rabbitmq.consumer;

import com.learning.rabbitmq.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotificationConsumer {
    
    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleNotification(Map<String, String> notification) {
        String type = notification.get("type");
        String message = notification.get("message");
        String timestamp = notification.get("timestamp");
        
        System.out.println("Received notification [" + type + "]: " + message);
        
        switch (type) {
            case "email" -> System.out.println("  Sending email notification...");
            case "sms" -> System.out.println("  Sending SMS notification...");
            case "push" -> System.out.println("  Sending push notification...");
            default -> System.out.println("  Unknown notification type");
        }
        
        System.out.println("  Timestamp: " + timestamp);
    }
}
```

### Build and Run

```bash
cd 34-rabbitmq/rabbitmq-messaging
mvn clean package -DskipTests
mvn spring-boot:run
```

### API Endpoints

```
# Check inventory via RPC
GET /api/inventory/check?productId=PROD-001&quantity=10

# Publish product update
POST /api/products/update
{
  "id": "PROD-001",
  "name": "Updated Product",
  "category": "electronics",
  "price": 99.99
}

# Publish notification
POST /api/notifications/send?type=email&message=Test

# Get inventory status
GET /api/inventory/status
```

### Learning Outcomes

After completing these projects, you will understand:

1. **Exchange Types** - Direct, Topic, Fanout, and Headers exchanges
2. **Message Acknowledgment** - Manual ACK/NACK and requeuing
3. **Dead Letter Queues** - DLX routing for failed messages
4. **Publisher Confirms** - Guaranteed delivery with correlation
5. **Priority Queues** - Message prioritization
6. **RPC Pattern** - Request-reply with RabbitMQ
7. **Circuit Breaker** - Resilience with RetryTemplate

### References

- RabbitMQ Tutorials: https://www.rabbitmq.com/tutorials
- Spring AMQP Documentation: https://docs.spring.io/spring-amqp/reference/