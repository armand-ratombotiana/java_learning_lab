# Spring Modulith Projects - Module 68

This module covers Spring Modulith, module boundaries, and event-driven architecture.

## Mini-Project: Order Module with Spring Modulith (2-4 hours)

### Overview
Build a modular Spring Boot application using Spring Modulith with clear module boundaries.

### Project Structure
```
spring-modulith-demo/
├── src/main/java/com/learning/
│   ├── Application.java
│   ├── order/
│   │   ├── Order.java
│   │   ├── OrderController.java
│   │   └── OrderService.java
│   ├── inventory/
│   │   ├── Inventory.java
│   │   └── InventoryService.java
│   └── payments/
│       ├── Payment.java
│       └── PaymentService.java
├── pom.xml
└── run.sh
```

### Implementation
```java
// pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>spring-modulith-demo</artifactId>
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
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.modulith</groupId>
            <artifactId>spring-modulith-starter-core</artifactId>
            <version>1.1.0</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.modulith</groupId>
            <artifactId>spring-modulith-starter-events</artifactId>
            <version>1.1.0</version>
        </dependency>
    </dependencies>
</project>

// Application.java
package com.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.modulith.Modulithic;

@Modulithic
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

// Order Module
package com.learning.order;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String orderNumber;
    private String customerId;
    private String status;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}

@Entity
@Table(name = "order_items")
class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String productId;
    private int quantity;
    private double unitPrice;
    
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    
    public Long getId() { return id; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
}

// OrderService.java
package com.learning.order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    
    private final OrderRepository orderRepository;
    
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    
    @Transactional
    public Order createOrder(String customerId, List<OrderItemData> items) {
        Order order = new Order();
        order.setOrderNumber("ORD-" + System.currentTimeMillis());
        order.setCustomerId(customerId);
        order.setStatus("CREATED");
        
        return orderRepository.save(order);
    }
}

record OrderItemData(String productId, int quantity, double unitPrice) {}

interface OrderRepository extends JpaRepository<Order, Long> {}

// Inventory Module
package com.learning.inventory;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
public class Inventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String productId;
    private int availableQuantity;
    private int reservedQuantity;
    private LocalDateTime lastUpdated;
    
    public boolean reserve(int quantity) {
        if (availableQuantity >= quantity) {
            availableQuantity -= quantity;
            reservedQuantity += quantity;
            lastUpdated = LocalDateTime.now();
            return true;
        }
        return false;
    }
    
    public void release(int quantity) {
        reservedQuantity -= quantity;
        availableQuantity += quantity;
        lastUpdated = LocalDateTime.now();
    }
    
    public Long getId() { return id; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }
    public int getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(int reservedQuantity) { this.reservedQuantity = reservedQuantity; }
}
```

---

## Real-World Project: Event-Driven Modular E-Commerce (8+ hours)

### Overview
Build a modular e-commerce platform using Spring Modulith with event-driven communication between modules.

### Project Structure
```
ecommerce-modulith/
├── src/main/java/com/learning/
│   ├── application/
│   ├── modules/
│   │   ├── order/
│   │   ├── inventory/
│   │   ├── payment/
│   │   ├── shipping/
│   │   └── notifications/
│   └── events/
├── pom.xml
└── docker-compose.yml
```

### Implementation
```java
// Events
package com.learning.events;

public interface DomainEvent {}

record OrderCreatedEvent(String orderId, String customerId, List<OrderItemEvent> items) 
    implements DomainEvent {
    record OrderItemEvent(String productId, int quantity) {}
}

record InventoryReservedEvent(String orderId, String productId, int quantity) 
    implements DomainEvent {}

record PaymentProcessedEvent(String orderId, String transactionId, double amount) 
    implements DomainEvent {}

record ShippingCreatedEvent(String orderId, String shipmentId) implements DomainEvent {}

// Module: Order
package com.learning.order;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderEventHandler {
    
    private final OrderRepository orderRepository;
    
    public OrderEventHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    
    @ApplicationModuleListener
    @Transactional
    public void onInventoryReserved(InventoryReservedEvent event) {
        orderRepository.findById(Long.parseLong(event.orderId()))
            .ifPresent(order -> {
                order.setStatus("INVENTORY_RESERVED");
                orderRepository.save(order);
            });
    }
    
    @ApplicationModuleListener
    @Transactional
    public void onPaymentProcessed(PaymentProcessedEvent event) {
        orderRepository.findById(Long.parseLong(event.orderId()))
            .ifPresent(order -> {
                order.setStatus("PAYMENT_COMPLETED");
                orderRepository.save(order);
            });
    }
}

@Service
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;
    
    public OrderService(OrderRepository orderRepository, EventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }
    
    @Transactional
    public Order createOrder(String customerId, List<OrderItemData> items) {
        Order order = new Order();
        order.setOrderNumber("ORD-" + System.currentTimeMillis());
        order.setCustomerId(customerId);
        order.setStatus("CREATED");
        
        Order saved = orderRepository.save(order);
        
        var orderItems = items.stream()
            .map(item -> new OrderCreatedEvent.OrderItemEvent(item.productId(), item.quantity()))
            .toList();
        
        eventPublisher.publish(new OrderCreatedEvent(saved.getId().toString(), 
            customerId, orderItems));
        
        return saved;
    }
}

interface EventPublisher {
    void publish(DomainEvent event);
}

// Module: Inventory
package com.learning.inventory;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {
    
    private final InventoryRepository inventoryRepository;
    private final EventPublisher eventPublisher;
    
    public InventoryService(InventoryRepository inventoryRepository, EventPublisher eventPublisher) {
        this.inventoryRepository = inventoryRepository;
        this.eventPublisher = eventPublisher;
    }
    
    @ApplicationModuleListener
    @Transactional
    public void onOrderCreated(OrderCreatedEvent event) {
        for (var item : event.items()) {
            boolean reserved = inventoryRepository.reserve(item.productId(), item.quantity());
            
            eventPublisher.publish(new InventoryReservedEvent(
                event.orderId(), item.productId(), item.quantity()
            ));
        }
    }
    
    public boolean reserve(String productId, int quantity) {
        return inventoryRepository.findByProductId(productId)
            .map(inv -> inv.reserve(quantity))
            .orElse(false);
    }
}

// Module: Payment
package com.learning.payment;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final EventPublisher eventPublisher;
    
    public PaymentService(PaymentRepository paymentRepository, EventPublisher eventPublisher) {
        this.paymentRepository = paymentRepository;
        this.eventPublisher = eventPublisher;
    }
    
    @ApplicationModuleListener
    @Transactional
    public void onInventoryReserved(InventoryReservedEvent event) {
        Payment payment = new Payment();
        payment.setOrderId(event.orderId());
        payment.setAmount(calculateTotal(event.quantity()));
        payment.setStatus("PROCESSED");
        
        paymentRepository.save(payment);
        
        eventPublisher.publish(new PaymentProcessedEvent(
            event.orderId(), "TXN-" + System.currentTimeMillis(), payment.getAmount()
        ));
    }
    
    private double calculateTotal(int quantity) {
        return quantity * 10.0;
    }
}

// Module: Shipping
package com.learning.shipping;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShippingService {
    
    private final ShippingRepository shippingRepository;
    private final EventPublisher eventPublisher;
    
    public ShippingService(ShippingRepository shippingRepository, EventPublisher eventPublisher) {
        this.shippingRepository = shippingRepository;
        this.eventPublisher = eventPublisher;
    }
    
    @ApplicationModuleListener
    @Transactional
    public void onPaymentProcessed(PaymentProcessedEvent event) {
        Shipment shipment = new Shipment();
        shipment.setOrderId(event.orderId());
        shipment.setTrackingNumber("TRACK-" + System.currentTimeMillis());
        shipment.setStatus("CREATED");
        
        shippingRepository.save(shipment);
        
        eventPublisher.publish(new ShippingCreatedEvent(
            event.orderId(), shipment.getTrackingNumber()
        ));
    }
}

// Module: Notifications
package com.learning.notifications;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    
    @ApplicationModuleListener
    public void onOrderCreated(OrderCreatedEvent event) {
        System.out.println("Notification: Order " + event.orderId() + " created");
    }
    
    @ApplicationModuleListener
    public void onShippingCreated(ShippingCreatedEvent event) {
        System.out.println("Notification: Order " + event.orderId() + " shipped. " +
            "Tracking: " + event.shipmentId());
    }
}
```

### Build and Run
```bash
mvn clean compile
java -jar target/spring-modulith-demo-1.0.0.jar
```

### Learning Outcomes
- Design modular applications with Spring Modulith
- Define module boundaries
- Implement event-driven communication
- Handle cross-module transactions
- Configure module dependencies
- Build maintainable architectures