# Architecture Patterns - REAL WORLD PROJECT

## Project Overview

**Project Name**: E-Commerce Platform Architecture
**Time Estimate**: 20-30 hours
**Difficulty**: Advanced

Build a production-ready e-commerce platform demonstrating all four architecture patterns working together. This is a real-world project simulating a large-scale distributed system.

---

## System Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              API Gateway                                    │
│                         (Kong / Spring Cloud Gateway)                       │
└─────────┬───────────────────────┬─────────────────────────┬─────────────────┘
          │                       │                         │
          ▼                       ▼                         ▼
┌─────────────────┐    ┌─────────────────┐       ┌─────────────────┐
│  User Service   │    │ Product Service │       │  Order Service  │
│   (Microservice)│    │  (Microservice) │       │  (Microservice) │
└────────┬────────┘    └────────┬────────┘       └────────┬────────┘
         │                       │                         │
         └───────────────────────┴─────────────────────────┘
                                 │
                                 ▼
                    ┌────────────────────────┐
                    │    Event Bus (Kafka)   │
                    └────────────┬───────────┘
                                 │
         ┌───────────────────────┼───────────────────────┐
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐       ┌─────────────────┐
│ Notification    │    │   Inventory    │       │    Analytics    │
│    Service      │    │    Service     │       │    Service      │
└─────────────────┘    └─────────────────┘       └─────────────────┘
```

---

## Part 1: Foundation Setup

### 1.1 Project Structure

```bash
ecommerce-platform/
├── docker-compose.yml
├── docker/
│   └── Dockerfile
├── services/
│   ├── api-gateway/
│   ├── service-registry/
│   ├── config-server/
│   ├── user-service/
│   ├── product-service/
│   ├── order-service/
│   ├── inventory-service/
│   ├── notification-service/
│   └── analytics-service/
└── infrastructure/
    ├── kafka/
    ├── postgres/
    ├── redis/
    └── monitoring/
```

### 1.2 Docker Compose Configuration

Create `docker-compose.yml`:

```yaml
version: '3.8'

services:
  # Infrastructure
  postgres:
    image: postgres:14
    environment:
      POSTGRES_USER: ecommerce
      POSTGRES_PASSWORD: ecommerce
      POSTGRES_DB: ecommerce
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  redis:
    image: redis:7
    ports:
      - "6379:6379"
    command: redis-server --appendonly yes
    volumes:
      - redis_data:/data

  zookeeper:
    image: confluentinc/cp-zookeeper:7.4.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000

  kafka:
    image: confluentinc/cp-kafka:7.4.0
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

  # Service Infrastructure
  service-registry:
    image: springcloud/eureka:2.0
    ports:
      - "8761:8761"

  config-server:
    image: springcloud/config-server:2022.0
    ports:
      - "8888:8888"
    environment:
      SPRING_CLOUD_CONFIG_SERVER_GIT_URI: https://github.com/your-org/config-repo

  api-gateway:
    image: springcloud/gateway:2022.0
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: docker

  # Application Services (build from source)
  user-service:
    build: ./services/user-service
    ports:
      - "8081:8081"

  product-service:
    build: ./services/product-service
    ports:
      - "8082:8082"

  order-service:
    build: ./services/order-service
    ports:
      - "8083:8083"

  inventory-service:
    build: ./services/inventory-service
    ports:
      - "8084:8084"

  notification-service:
    build: ./services/notification-service
    ports:
      - "8085:8085"

volumes:
  postgres_data:
  redis_data:
```

---

## Part 2: User Service (Layered + CQRS)

### 2.1 Domain Model

Create `services/user-service/src/main/java/com/ecommerce/user/model/User.java`:

```java
package com.ecommerce.user.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class User {
    private String id;
    private String email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String phone;
    private Address billingAddress;
    private Set<Address> shippingAddresses = new HashSet<>();
    private UserRole role;
    private boolean emailVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum UserRole {
        CUSTOMER, ADMIN, SUPPORT
    }

    public static class Address {
        private String id;
        private String street;
        private String city;
        private String state;
        private String zipCode;
        private String country;
        private boolean isDefault;

        // Getters and Setters
    }

    // Constructors, Getters, Setters
}
```

### 2.2 CQRS Implementation

**Command Side:**

Create `services/user-service/src/main/java/com/ecommerce/user/command/UserCommandHandler.java`:

```java
package com.ecommerce.user.command;

import com.ecommerce.user.event.UserCreatedEvent;
import com.ecommerce.user.event.UserEventStore;
import com.ecommerce.user.model.User;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserCommandHandler {
    private final UserEventStore eventStore;

    public UserCommandHandler(UserEventStore eventStore) {
        this.eventStore = eventStore;
    }

    public String handle(RegisterUserCommand command) {
        validateCommand(command);
        
        String userId = UUID.randomUUID().toString();
        
        UserCreatedEvent event = new UserCreatedEvent(
            userId,
            command.getEmail(),
            command.getPasswordHash(),
            command.getFirstName(),
            command.getLastName(),
            LocalDateTime.now()
        );
        
        eventStore.append(userId, event);
        
        return userId;
    }

    public void handle(UpdateUserCommand command) {
        eventStore.append(command.getUserId(), new UserUpdatedEvent(
            command.getUserId(),
            command.getUpdates(),
            LocalDateTime.now()
        ));
    }

    private void validateCommand(RegisterUserCommand command) {
        if (command.getEmail() == null || !command.getEmail().contains("@")) {
            throw new ValidationException("Invalid email");
        }
        if (command.getPassword() == null || command.getPassword().length() < 8) {
            throw new ValidationException("Password must be at least 8 characters");
        }
    }
}
```

**Query Side:**

Create `services/user-service/src/main/java/com/ecommerce/user/query/UserQueryHandler.java`:

```java
package com.ecommerce.user.query;

import com.ecommerce.user.model.User;
import com.ecommerce.user.projection.UserReadModel;
import com.ecommerce.user.projection.UserProjection;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserQueryHandler {
    private final UserProjection userProjection;

    public UserQueryHandler(UserProjection userProjection) {
        this.userProjection = userProjection;
    }

    public UserView getUserById(String userId) {
        UserReadModel readModel = userProjection.getById(userId);
        return mapToView(readModel);
    }

    public List<UserView> getUsersByRole(User.UserRole role) {
        return userProjection.findByRole(role).stream()
            .map(this::mapToView)
            .toList();
    }

    public UserView getUserByEmail(String email) {
        UserReadModel readModel = userProjection.findByEmail(email);
        return mapToView(readModel);
    }

    private UserView mapToView(UserReadModel model) {
        if (model == null) return null;
        
        UserView view = new UserView();
        view.setUserId(model.getUserId());
        view.setEmail(model.getEmail());
        view.setFullName(model.getFullName());
        view.setRole(model.getRole());
        view.setCreatedAt(model.getCreatedAt());
        return view;
    }
}
```

**Projection:**

Create `services/user-service/src/main/java/com/ecommerce/user/projection/UserProjection.java`:

```java
package com.ecommerce.user.projection;

import com.ecommerce.user.event.*;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserProjection {
    private final ConcurrentHashMap<String, UserReadModel> users = new ConcurrentHashMap<>();

    public void project(String userId, Object event) {
        if (event instanceof UserCreatedEvent created) {
            handleCreated(userId, created);
        } else if (event instanceof UserUpdatedEvent updated) {
            handleUpdated(userId, updated);
        }
    }

    private void handleCreated(String userId, UserCreatedEvent event) {
        UserReadModel model = new UserReadModel();
        model.setUserId(userId);
        model.setEmail(event.getEmail());
        model.setFullName(event.getFirstName() + " " + event.getLastName());
        model.setRole("CUSTOMER");
        model.setCreatedAt(event.getTimestamp());
        users.put(userId, model);
    }

    private void handleUpdated(String userId, UserUpdatedEvent event) {
        users.computeIfPresent(userId, (id, model) -> {
            // Update fields based on event
            return model;
        });
    }

    public UserReadModel getById(String userId) {
        return users.get(userId);
    }

    public UserReadModel findByEmail(String email) {
        return users.values().stream()
            .filter(u -> u.getEmail().equals(email))
            .findFirst()
            .orElse(null);
    }

    public List<UserReadModel> findByRole(String role) {
        return users.values().stream()
            .filter(u -> u.getRole().equals(role))
            .toList();
    }
}
```

---

## Part 3: Order Service (Event-Driven + Microservices)

### 3.1 Order Service Implementation

Create `services/order-service/src/main/java/com/ecommerce/order/OrderService.java`:

```java
package com.ecommerce.order;

import com.ecommerce.order.model.Order;
import com.ecommerce.order.client.InventoryClient;
import com.ecommerce.order.client.ProductClient;
import com.ecommerce.order.event.OrderEventPublisher;
import com.ecommerce.order.event.OrderCreatedEvent;
import com.ecommerce.order.event.OrderStatusChangedEvent;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final ProductClient productClient;
    private final OrderEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository,
                        InventoryClient inventoryClient,
                        ProductClient productClient,
                        OrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.inventoryClient = inventoryClient;
        this.productClient = productClient;
        this.eventPublisher = eventPublisher;
    }

    public Order createOrder(String customerId, List<OrderItemRequest> items) {
        // Check inventory availability
        for (OrderItemRequest item : items) {
            boolean available = inventoryClient.checkAvailability(item.getProductId(), item.getQuantity());
            if (!available) {
                throw new InsufficientInventoryException("Product not available: " + item.getProductId());
            }
        }

        // Create order
        Order order = new Order();
        order.setId(java.util.UUID.randomUUID().toString());
        order.setCustomerId(customerId);
        order.setItems(items.stream().map(this::mapToOrderItem).toList());
        order.setTotalAmount(calculateTotal(items));
        order.setStatus(Order.OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Reserve inventory
        for (OrderItemRequest item : items) {
            inventoryClient.reserveStock(item.getProductId(), item.getQuantity());
        }

        // Publish event
        eventPublisher.publish(new OrderCreatedEvent(
            savedOrder.getId(),
            customerId,
            savedOrder.getTotalAmount(),
            savedOrder.getItems().size()
        ));

        return savedOrder;
    }

    public Order confirmOrder(String orderId) {
        Order order = getOrder(orderId);
        
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new IllegalStateException("Order cannot be confirmed in status: " + order.getStatus());
        }

        order.setStatus(Order.OrderStatus.CONFIRMED);
        Order updated = orderRepository.save(order);

        eventPublisher.publish(new OrderStatusChangedEvent(
            orderId,
            "PENDING",
            "CONFIRMED",
            LocalDateTime.now()
        ));

        return updated;
    }

    private BigDecimal calculateTotal(List<OrderItemRequest> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest item : items) {
            var product = productClient.getProduct(item.getProductId());
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return total;
    }

    private Order.OrderItem mapToOrderItem(OrderItemRequest request) {
        var product = productClient.getProduct(request.getProductId());
        
        Order.OrderItem item = new Order.OrderItem();
        item.setProductId(request.getProductId());
        item.setQuantity(request.getQuantity());
        item.setUnitPrice(product.getPrice());
        return item;
    }
}
```

### 3.2 Event Publisher with Kafka

Create `services/order-service/src/main/java/com/ecommerce/order/event/KafkaOrderEventPublisher.java`:

```java
package com.ecommerce.order.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaOrderEventPublisher implements OrderEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaOrderEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                   ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(OrderEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("ecommerce.orders", event.getOrderId(), message);
        } catch (JsonProcessingException e) {
            throw new EventPublishException("Failed to serialize event", e);
        }
    }
}
```

---

## Part 4: Inventory Service (Event-Driven)

### 4.1 Inventory Management

Create `services/inventory-service/src/main/java/com/ecommerce/inventory/InventoryService.java`:

```java
package com.ecommerce.inventory;

import com.ecommerce.inventory.model.InventoryTransaction;
import com.ecommerce.inventory.event.InventoryEventPublisher;
import com.ecommerce.inventory.event.StockReservedEvent;
import com.ecommerce.inventory.event.StockReleasedEvent;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryService {
    private final InventoryRepository repository;
    private final InventoryEventPublisher eventPublisher;

    public InventoryService(InventoryRepository repository,
                           InventoryEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    public boolean checkAvailability(String productId, int quantity) {
        return repository.findByProductId(productId)
            .map(inventory -> inventory.getAvailableQuantity() >= quantity)
            .orElse(false);
    }

    public void reserveStock(String productId, int quantity, String orderId) {
        Inventory inventory = repository.findByProductId(productId)
            .orElseThrow(() -> new InventoryNotFoundException(productId));

        if (inventory.getAvailableQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product: " + productId);
        }

        inventory.reserve(quantity);
        repository.save(inventory);

        eventPublisher.publish(new StockReservedEvent(
            productId,
            orderId,
            quantity,
            LocalDateTime.now()
        ));
    }

    public void releaseStock(String productId, int quantity, String orderId) {
        Inventory inventory = repository.findByProductId(productId)
            .orElseThrow(() -> new InventoryNotFoundException(productId));

        inventory.release(quantity);
        repository.save(inventory);

        eventPublisher.publish(new StockReleasedEvent(
            productId,
            orderId,
            quantity,
            LocalDateTime.now()
        ));
    }

    public void confirmReservation(String productId, int quantity) {
        Inventory inventory = repository.findByProductId(productId)
            .orElseThrow(() -> new InventoryNotFoundException(productId));

        inventory.confirmReservation(quantity);
        repository.save(inventory);
    }
}
```

---

## Part 5: API Gateway Configuration

### 5.1 Gateway Routes

Create `services/api-gateway/src/main/resources/application.yml`:

```yaml
server:
  port: 8080

spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/api/users/**
          filters:
            - StripPrefix=1
            - CircuitBreaker=users

        - id: product-service
          uri: lb://product-service
          predicates:
            - Path=/api/products/**
          filters:
            - StripPrefix=1
            - CircuitBreaker=products

        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/api/orders/**
          filters:
            - StripPrefix=1
            - RequestRateLimiter=orders

        - id: inventory-service
          uri: lb://inventory-service
          predicates:
            - Path=/api/inventory/**
          filters:
            - StripPrefix=1

      globalcors:
        cors-configurations:
          '[/**]':
            allowed-origins: "*"
            allowed-methods:
              - GET
              - POST
              - PUT
              - DELETE
              - OPTIONS
            allowed-headers: "*"

  resilience4j:
    circuitbreaker:
      instances:
        users:
          sliding-window-size: 10
          failure-rate-threshold: 50
          wait-duration-in-open-state: 10s
        products:
          sliding-window-size: 10
          failure-rate-threshold: 50

  ratelimiter:
    instances:
      orders:
        redis-rate-limiter:
          replenish-rate: 100
          burst-capacity: 200
```

---

## Part 6: Consumer Services

### 6.1 Notification Service

Create `services/notification-service/src/main/java/com/ecommerce/notification/NotificationService.java`:

```java
package com.ecommerce.notification;

import com.ecommerce.notification.event.OrderCreatedEvent;
import com.ecommerce.notification.event.StockReservedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @KafkaListener(topics = "ecommerce.orders", groupId = "notification-group")
    public void handleOrderCreated(OrderCreatedEvent event) {
        System.out.println("Sending order confirmation for: " + event.getOrderId());
        sendEmail(event.getCustomerId(), "Order Confirmed", 
            "Your order " + event.getOrderId() + " has been placed.");
    }

    @KafkaListener(topics = "ecommerce.inventory", groupId = "notification-group")
    public void handleStockReserved(StockReservedEvent event) {
        System.out.println("Stock reserved: " + event.getProductId());
        
        if (event.getQuantity() < 5) {
            sendEmail("admin@ecommerce.com", "Low Stock Alert",
                "Product " + event.getProductId() + " has only " + event.getQuantity() + " units left.");
        }
    }

    private void sendEmail(String to, String subject, String body) {
        // Email sending logic
        System.out.println("Email to: " + to + ", Subject: " + subject);
    }
}
```

---

## Part 7: Monitoring and Observability

### 7.1 Metrics Configuration

Create `services/product-service/src/main/resources/application.yml`:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
  metrics:
    tags:
      application: ${spring.application.name}

spring:
  application:
    name: product-service
  zipkin:
    base-url: http://zipkin:9411
    sampler:
      probability: 1.0
  sleuth:
    sampler:
      probability: 1.0
```

---

## Part 8: Testing

### 8.1 Integration Test

Create `services/order-service/src/test/java/com/ecommerce/order/OrderServiceIntegrationTest.java`:

```java
package com.ecommerce.order;

import com.ecommerce.order.model.Order;
import com.ecommerce.order.client.InventoryClient;
import com.ecommerce.order.client.ProductClient;
import com.ecommerce.order.event.OrderEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceIntegrationTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private InventoryClient inventoryClient;

    @Mock
    private ProductClient productClient;

    @Mock
    private OrderEventPublisher eventPublisher;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_success() {
        // Given
        String customerId = "customer-123";
        List<OrderItemRequest> items = List.of(
            new OrderItemRequest("product-1", 2)
        );

        when(inventoryClient.checkAvailability("product-1", 2)).thenReturn(true);
        
        Product mockProduct = new Product();
        mockProduct.setPrice(new java.math.BigDecimal("29.99"));
        when(productClient.getProduct("product-1")).thenReturn(mockProduct);
        
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        // When
        Order order = orderService.createOrder(customerId, items);

        // Then
        assertNotNull(order);
        assertEquals(Order.OrderStatus.PENDING, order.getStatus());
        verify(inventoryClient).reserveStock("product-1", 2);
        verify(eventPublisher).publish(any(OrderCreatedEvent.class));
    }

    @Test
    void createOrder_insufficientInventory() {
        // Given
        when(inventoryClient.checkAvailability("product-1", 2)).thenReturn(false);

        // When/Then
        assertThrows(InsufficientInventoryException.class, () -> 
            orderService.createOrder("customer-123", List.of(new OrderItemRequest("product-1", 2)))
        );
    }
}
```

---

## Project Deliverables Checklist

### Architecture Patterns Demonstrated
- [x] Layered Architecture in each service (Controller → Service → Repository)
- [x] Microservices with service discovery and API Gateway
- [x] Event-Driven with Kafka for inter-service communication
- [x] CQRS in User Service with read model projections

### Key Components Built
- [x] User Service with registration and authentication
- [x] Product Service with catalog management
- [x] Order Service with full order lifecycle
- [x] Inventory Service with stock management
- [x] Notification Service for async notifications

### Infrastructure
- [x] Docker Compose for all services
- [x] Kafka for event streaming
- [x] PostgreSQL for persistence
- [x] Redis for caching
- [x] Service Registry (Eureka)
- [x] API Gateway

### Quality Attributes
- [x] Circuit breakers for resilience
- [x] Rate limiting on API Gateway
- [x] Distributed tracing with Zipkin
- [x] Metrics with Prometheus
- [x] Health checks and monitoring

---

## Running the Project

```bash
# Start infrastructure
docker-compose up -d postgres redis zookeeper kafka

# Start service infrastructure
docker-compose up -d service-registry config-server

# Start application services
docker-compose up -d user-service product-service order-service inventory-service

# Start API Gateway
docker-compose up -d api-gateway

# Check health
curl http://localhost:8080/actuator/health
```

---

## Extension Opportunities

1. **Add Payment Service**: Integrate Stripe/PayPal for payment processing
2. **Add Search**: Implement Elasticsearch for product search
3. **Add Analytics**: Real-time dashboard with real-time event processing
4. **Add GraphQL**: Alternative API for complex queries
5. **Add CI/CD**: GitHub Actions for automated deployment