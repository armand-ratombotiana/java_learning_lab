# Architecture Patterns - EXERCISES

## Exercise Set Overview
This exercise set contains 25 exercises covering layered architecture, microservices, event-driven architecture, and CQRS patterns.

---

## Part 1: Layered Architecture Exercises (1-8)

### Exercise 1: Identify Layer Responsibilities
**Difficulty**: Easy

For the following components, identify which layer they belong to:
- `UserController`
- `ProductRepository`
- `PaymentService`
- `OrderMapper`
- `AuthenticationFilter`

**Answer**:
- `UserController` - Presentation Layer
- `ProductRepository` - Data Layer
- `PaymentService` - Business Layer
- `OrderMapper` - Data Layer (DTO conversion)
- `AuthenticationFilter` - Presentation Layer

---

### Exercise 2: Fix Layer Violation
**Difficulty**: Medium

The following code violates layered architecture principles. Identify the violation and fix it:

```java
// ProductController.java
@RestController
public class ProductController {
    @PostMapping("/products")
    public Product createProduct(@RequestBody ProductDTO dto) {
        // Direct database access - WRONG!
        return database.products.insert(dto.toEntity());
    }
}
```

**Answer**: The controller is directly accessing the database, bypassing the service layer. The fix:

```java
@RestController
public class ProductController {
    private final ProductService productService;
    
    @PostMapping("/products")
    public Product createProduct(@RequestBody ProductDTO dto) {
        return productService.createProduct(dto);
    }
}
```

---

### Exercise 3: Add Validation Layer
**Difficulty**: Medium

Add input validation to the business layer for creating a Product:
- Name must not be empty
- Price must be positive
- Category must be specified

**Answer**:

```java
public class ProductServiceImpl implements ProductService {
    @Override
    public Product createProduct(ProductDTO dto) {
        validateProduct(dto);
        return productRepository.save(dto.toEntity());
    }
    
    private void validateProduct(ProductDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new ValidationException("Name is required");
        }
        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Price must be positive");
        }
        if (dto.getCategory() == null || dto.getCategory().trim().isEmpty()) {
            throw new ValidationException("Category is required");
        }
    }
}
```

---

### Exercise 4: Implement DTO Mapping
**Difficulty**: Easy

Create a mapper utility to convert between Product entity and ProductDTO.

**Answer**:

```java
@Component
public class ProductMapper {
    public ProductDTO toDTO(Product entity) {
        ProductDTO dto = new ProductDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setCategory(entity.getCategory());
        return dto;
    }
    
    public Product toEntity(ProductDTO dto) {
        return new Product(dto.getName(), dto.getDescription(), 
                          dto.getPrice(), dto.getCategory());
    }
}
```

---

### Exercise 5: Create Generic Repository
**Difficulty**: Hard

Create a generic repository interface and implementation that can work with any entity type.

**Answer**:

```java
public interface GenericRepository<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void deleteById(ID id);
    boolean existsById(ID id);
}

public class GenericRepositoryImpl<T, ID> implements GenericRepository<T, ID> {
    private final Map<ID, T> storage = new ConcurrentHashMap<>();
    
    @Override
    public T save(T entity) {
        return entity;
    }
    
    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(storage.get(id));
    }
    
    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }
    
    @Override
    public void deleteById(ID id) {
        storage.remove(id);
    }
    
    @Override
    public boolean existsById(ID id) {
        return storage.containsKey(id);
    }
}
```

---

### Exercise 6: Add Transaction Management
**Difficulty**: Medium

Wrap multiple service operations in a single transaction.

**Answer**:

```java
@Service
public class OrderService {
    @Transactional
    public Order createOrder(OrderDTO orderDTO) {
        Product product = productRepository.findById(orderDTO.getProductId())
            .orElseThrow(() -> new ProductNotFoundException(...));
        
        if (product.getStock() < orderDTO.getQuantity()) {
            throw new InsufficientStockException(...);
        }
        
        product.setStock(product.getStock() - orderDTO.getQuantity());
        productRepository.save(product);
        
        Order order = new Order(orderDTO.getCustomerId(), product, orderDTO.getQuantity());
        return orderRepository.save(order);
    }
}
```

---

### Exercise 7: Implement Global Exception Handler
**Difficulty**: Medium

Create a global exception handler that returns proper HTTP status codes.

**Answer**:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(404, ex.getMessage()));
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(400, ex.getMessage()));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse(500, "Internal server error"));
    }
}
```

---

### Exercise 8: Add Pagination Support
**Difficulty**: Medium

Add pagination support to the getAllProducts method in the service layer.

**Answer**:

```java
public interface ProductService {
    Page<ProductDTO> getAllProducts(Pageable pageable);
}

@Service
public class ProductServiceImpl implements ProductService {
    @Override
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        List<Product> products = productRepository.findAll();
        
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), products.size());
        
        List<ProductDTO> pageContent = products.subList(start, end)
            .stream()
            .map(ProductMapper::toDTO)
            .collect(Collectors.toList());
        
        return new PageImpl<>(pageContent, pageable, products.size());
    }
}
```

---

## Part 2: Microservices Exercises (9-16)

### Exercise 9: Design Service Boundaries
**Difficulty**: Medium

For an e-commerce system, identify the microservices and their boundaries.

**Answer**:
- **Product Service**: Product catalog, inventory
- **Order Service**: Order management, order processing
- **User Service**: User accounts, authentication
- **Payment Service**: Payment processing
- **Shipping Service**: Shipping logistics
- **Notification Service**: Email, SMS, push notifications

---

### Exercise 10: Implement Service Discovery
**Difficulty**: Hard

Create a simple service registry using Consul-style health checks.

**Answer**:

```java
@Service
public class ServiceRegistry {
    private final Map<String, ServiceInstance> services = new ConcurrentHashMap<>();
    
    public void register(String serviceName, String host, int port) {
        String instanceId = UUID.randomUUID().toString();
        services.put(instanceId, new ServiceInstance(serviceName, host, port, true));
    }
    
    public Optional<ServiceInstance> discover(String serviceName) {
        return services.values().stream()
            .filter(s -> s.getServiceName().equals(serviceName) && s.isHealthy())
            .findFirst();
    }
    
    public void healthCheck(String instanceId) {
        ServiceInstance instance = services.get(instanceId);
        if (instance != null) {
            instance.setHealthy(checkHealth(instance));
        }
    }
}
```

---

### Exercise 11: Configure API Gateway
**Difficulty**: Medium

Configure an API Gateway with:
- Rate limiting
- Authentication
- Request routing

**Answer**:

```java
@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("auth-service", r -> r
                .path("/api/auth/**")
                .filters(f -> f.stripPrefix(1))
                .uri("lb://auth-service"))
            .route("product-service", r -> r
                .path("/api/products/**")
                .filters(f -> f
                    .stripPrefix(1)
                    .addRequestHeader("X-Gateway-Time", 
                        String.valueOf(System.currentTimeMillis())))
                .uri("lb://product-service"))
            .build();
    }
    
    @Bean
    public GlobalFilter rateLimitFilter() {
        return (exchange, chain) -> {
            // Rate limiting logic
            return chain.filter(exchange);
        };
    }
}
```

---

### Exercise 12: Implement Circuit Breaker
**Difficulty**: Hard

Implement a circuit breaker pattern to handle service failures.

**Answer**:

```java
public class CircuitBreaker {
    private enum State { CLOSED, OPEN, HALF_OPEN }
    private State state = State.CLOSED;
    private int failureCount = 0;
    private long lastFailureTime;
    private final int threshold = 5;
    private final long resetTimeout = 60000;
    
    public void recordSuccess() {
        failureCount = 0;
        state = State.CLOSED;
    }
    
    public void recordFailure() {
        failureCount++;
        lastFailureTime = System.currentTimeMillis();
        
        if (failureCount >= threshold) {
            state = State.OPEN;
        }
    }
    
    public boolean allowRequest() {
        if (state == State.CLOSED) return true;
        if (state == State.HALF_OPEN) return true;
        
        if (System.currentTimeMillis() - lastFailureTime > resetTimeout) {
            state = State.HALF_OPEN;
            return true;
        }
        
        return false;
    }
}
```

---

### Exercise 13: Distributed Tracing Setup
**Difficulty**: Medium

Configure distributed tracing for microservices using Sleuth.

**Answer**:

```yaml
# application.yml
spring:
  application:
    name: product-service
  sleuth:
    sampler:
      probability: 1.0
  zipkin:
    base-url: http://zipkin-server:9411
    
logging:
  level:
    org.springframework.cloud.sleuth: DEBUG
```

```java
// Add to dependencies in pom.xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-zipkin</artifactId>
</dependency>
```

---

### Exercise 14: Inter-Service Communication
**Difficulty**: Medium

Implement synchronous communication between Order Service and Product Service using RestTemplate.

**Answer**:

```java
@Service
public class ProductClient {
    private final RestTemplate restTemplate;
    private final String productServiceUrl = "http://product-service:8081";
    
    public Product getProduct(String productId) {
        return restTemplate.getForObject(
            productServiceUrl + "/api/products/" + productId, 
            Product.class
        );
    }
    
    public boolean checkStock(String productId, int quantity) {
        try {
            String url = String.format("%s/api/products/%s/stock?quantity=%d",
                productServiceUrl, productId, quantity);
            return restTemplate.getForObject(url, Boolean.class);
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        }
    }
}
```

---

### Exercise 15: Configuration Management
**Difficulty**: Medium

Set up a configuration server to manage microservices configuration.

**Answer**:

```java
// ConfigServerApplication.java
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
```

```yaml
# application.yml
server:
  port: 8888
spring:
  application:
    name: config-server
  cloud:
    config:
      server:
        git:
          uri: https://github.com/your-org/config-repo
          default-label: main
```

```yaml
# product-service.yml in git repo
spring:
  datasource:
    url: jdbc:postgresql://db:5432/products
  rabbitmq:
    host: rabbitmq
```

---

### Exercise 16: Service Deployment
**Difficulty**: Hard

Create Docker Compose file for microservices deployment.

**Answer**:

```yaml
version: '3.8'
services:
  eureka:
    image: springcloud/eureka
    ports:
      - "8761:8761"
  
  config-server:
    image: config-server:latest
    ports:
      - "8888:8888"
    depends_on:
      - eureka
  
  product-service:
    image: product-service:latest
    ports:
      - "8081:8081"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      - config-server
  
  order-service:
    image: order-service:latest
    ports:
      - "8082:8082"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      - config-server
  
  api-gateway:
    image: api-gateway:latest
    ports:
      - "8080:8080"
    depends_on:
      - product-service
      - order-service
```

---

## Part 3: Event-Driven Exercises (17-21)

### Exercise 17: Design Event Schema
**Difficulty**: Easy

Design an event schema for an "Order Placed" event.

**Answer**:

```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440000",
  "eventType": "OrderPlaced",
  "timestamp": "2024-01-15T10:30:00Z",
  "aggregateId": "order-12345",
  "payload": {
    "orderId": "order-12345",
    "customerId": "customer-789",
    "items": [
      {
        "productId": "prod-001",
        "quantity": 2,
        "price": 29.99
      }
    ],
    "totalAmount": 59.98,
    "shippingAddress": {
      "street": "123 Main St",
      "city": "New York",
      "zipCode": "10001"
    }
  }
}
```

---

### Exercise 18: Implement Event Publisher
**Difficulty**: Medium

Create an event publisher using Kafka.

**Answer**:

```java
@Component
public class KafkaEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    public void publish(String topic, DomainEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, event.getAggregateId(), message);
        } catch (JsonProcessingException e) {
            throw new EventPublishException("Failed to publish event", e);
        }
    }
}
```

---

### Exercise 19: Event Handler Implementation
**Difficulty**: Medium

Implement an event handler that processes order placed events and updates inventory.

**Answer**:

```java
@Component
public class InventoryEventHandler {
    private final InventoryService inventoryService;
    
    @KafkaListener(topics = "order-events", groupId = "inventory-group")
    public void handleOrderPlaced(ConsumerRecord<String, String> record) {
        OrderPlacedEvent event = objectMapper.readValue(record.value(), OrderPlacedEvent.class);
        
        for (OrderItem item : event.getItems()) {
            inventoryService.reserveStock(
                item.getProductId(), 
                item.getQuantity()
            );
        }
    }
}
```

---

### Exercise 20: Idempotent Event Processing
**Difficulty**: Hard

Implement idempotent event processing to handle duplicate events.

**Answer**:

```java
@Service
public class IdempotentEventHandler {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    
    public void handleEvent(DomainEvent event) {
        String idempotencyKey = "event:" + event.getEventId();
        
        Boolean processed = redisTemplate.opsForValue()
            .getOperation(idempotencyKey);
        
        if (Boolean.TRUE.equals(processed)) {
            return; // Duplicate event, skip processing
        }
        
        processEvent(event);
        
        redisTemplate.opsForValue().set(idempotencyKey, "processed", Duration.ofDays(1));
    }
}
```

---

### Exercise 21: Event Sourcing Implementation
**Difficulty**: Hard

Implement event sourcing for an Order aggregate.

**Answer**:

```java
public class OrderAggregate {
    private String id;
    private OrderStatus status;
    private List<OrderEvent> events = new ArrayList<>();
    
    public void applyEvent(OrderEvent event) {
        switch (event.getType()) {
            case ORDER_CREATED:
                applyCreated(event);
                break;
            case ORDER_CONFIRMED:
                applyConfirmed(event);
                break;
            case ORDER_CANCELLED:
                applyCancelled(event);
                break;
        }
        events.add(event);
    }
    
    public void confirmOrder() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot confirm order in status: " + status);
        }
        applyEvent(new OrderConfirmedEvent(id, LocalDateTime.now()));
    }
    
    public List<OrderEvent> getEvents() {
        return new ArrayList<>(events);
    }
}
```

---

## Part 4: CQRS Exercises (22-25)

### Exercise 22: Design Read Model
**Difficulty**: Medium

Design a read model for an order that optimizes for different query patterns:
- List all orders for a customer
- Get order details with items
- Orders by status

**Answer**:

```java
@Entity
@Table(name = "order_read_model")
public class OrderReadModel {
    @Id
    private String orderId;
    private String customerId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "orderId")
    private List<OrderItemReadModel> items;
    
    @Index
    public List<OrderReadModel> findByCustomerId(String customerId) {...}
    
    @Index
    public List<OrderReadModel> findByStatus(OrderStatus status) {...}
}
```

---

### Exercise 23: Implement Projection
**Difficulty**: Hard

Create a projection that builds the read model from events.

**Answer**:

```java
@Component
public class OrderProjection {
    private final OrderReadRepository readRepository;
    
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        OrderReadModel readModel = new OrderReadModel();
        readModel.setOrderId(event.getOrderId());
        readModel.setCustomerId(event.getCustomerId());
        readModel.setStatus(OrderStatus.CREATED);
        readModel.setTotalAmount(calculateTotal(event.getItems()));
        readModel.setCreatedAt(event.getTimestamp());
        
        readRepository.save(readModel);
    }
    
    @EventListener
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        readRepository.findById(event.getOrderId())
            .ifPresent(readModel -> {
                readModel.setStatus(event.getNewStatus());
                readRepository.save(readModel);
            });
    }
}
```

---

### Exercise 24: CQRS Command Handler
**Difficulty**: Medium

Implement a command handler for creating an order in CQRS style.

**Answer**:

```java
@Service
public class OrderCommandHandler {
    private final EventStore eventStore;
    private final EventPublisher eventPublisher;
    
    public void handle(CreateOrderCommand command) {
        validateCommand(command);
        
        OrderCreatedEvent event = new OrderCreatedEvent(
            UUID.randomUUID().toString(),
            command.getCustomerId(),
            command.getItems(),
            LocalDateTime.now()
        );
        
        eventStore.save(event.getAggregateId(), event);
        eventPublisher.publish(event);
    }
    
    private void validateCommand(CreateOrderCommand command) {
        if (command.getItems() == null || command.getItems().isEmpty()) {
            throw new ValidationException("Order must have items");
        }
    }
}
```

---

### Exercise 25: Synchronous vs Asynchronous Updates
**Difficulty**: Hard

Compare synchronous and asynchronous read model updates and implement both.

**Answer**:

```java
// Synchronous Update
@Service
public class SynchronousOrderCommandHandler {
    private final EventStore eventStore;
    private final OrderProjection projection;
    
    public void handle(CreateOrderCommand command) {
        // ... create event
        eventStore.save(aggregateId, event);
        
        // Update read model immediately (synchronous)
        projection.project(event);
    }
}

// Asynchronous Update
@Service
public class AsynchronousOrderCommandHandler {
    private final EventStore eventStore;
    private final ApplicationEventPublisher eventPublisher;
    
    public void handle(CreateOrderCommand command) {
        // ... create event
        eventStore.save(aggregateId, event);
        
        // Publish event for async processing
        eventPublisher.publishEvent(new DomainEventWrapper(event));
    }
}

@Component
public class AsyncProjectionListener {
    @Async
    @EventListener
    public void onEvent(DomainEventWrapper wrapper) {
        projection.project(wrapper.getEvent());
    }
}
```

---

## Summary

These 25 exercises cover:

- **Layered Architecture** (1-8): Layer responsibilities, DTOs, repositories, transactions, exception handling
- **Microservices** (9-16): Service boundaries, discovery, API gateway, circuit breaker, distributed tracing, communication
- **Event-Driven** (17-21): Event schemas, publishers, handlers, idempotency, event sourcing
- **CQRS** (22-25): Read models, projections, command handlers, sync vs async updates