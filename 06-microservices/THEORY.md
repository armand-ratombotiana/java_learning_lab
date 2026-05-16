# Microservices Theory

## Table of Contents
1. [Microservices Patterns](#microservices-patterns)
2. [Service Discovery](#service-discovery)
3. [API Gateway](#api-gateway)

---

## 1. Microservices Patterns

### 1.1 What are Microservices?

Microservices architecture is an approach where an application is built as a collection of small, independent services. Each service:
- Is independently deployable
- Has its own database (Database per Service)
- Communicates via well-defined APIs
- Is owned by a small team

### 1.2 Service Decomposition

**Decomposition by Business Capability:**
```java
// E-commerce microservices
@Service
public class OrderService {
    public Order createOrder(OrderRequest request) {
        // Create order logic
        return order;
    }
}

@Service
public class PaymentService {
    public PaymentResult processPayment(PaymentRequest request) {
        // Payment processing
        return result;
    }
}

@Service
public class InventoryService {
    public boolean checkAvailability(Long productId, int quantity) {
        // Check inventory
        return available;
    }
}
```

**Domain-Driven Design Bounded Contexts:**
```
┌─────────────────────────────────────────┐
│           E-commerce Domain             │
├──────────────────┬──────────────────────┤
│   Order Context  │   Customer Context    │
│                  │                      │
│  - Orders        │  - Customers         │
│  - OrderItems    │  - Addresses         │
│  - Shipments     │  - Profiles          │
├──────────────────┴──────────────────────┤
│         Product Context                  │
│                                          │
│  - Products                              │
│  - Categories                            │
│  - Inventory                             │
└─────────────────────────────────────────┘
```

### 1.3 Communication Patterns

**Synchronous Communication (REST):**
```java
@RestController
public class OrderController {
    
    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest req) {
        // Call payment service
        PaymentResult payment = restTemplate.postForObject(
            "http://payment-service/api/payments",
            paymentRequest,
            PaymentResult.class
        );
        
        // Call inventory service
        inventoryService.reserveItems(req.getItems());
        
        // Create order
        return order;
    }
}
```

**Asynchronous Communication (Message Queue):**
```java
@Service
public class OrderService {
    
    @Autowired
    private MessageQueue messageQueue;
    
    public void createOrder(Order order) {
        orderRepository.save(order);
        
        // Publish event
        messageQueue.publish("order.created", JsonObject.of(
            "orderId", order.getId(),
            "items", order.getItems()
        ));
    }
}

// Event handler in another service
@Verticle
public class NotificationVerticle extends AbstractVerticle {
    
    @Override
    public void start() {
        vertx.eventBus().consumer("order.created", msg -> {
            JsonObject order = (JsonObject) msg.body();
            sendEmailNotification(order.getLong("orderId"));
        });
    }
}
```

### 1.4 Data Management

**API Composition:**
```java
@Service
public class CustomerOrderService {
    
    public CustomerOrders getCustomerOrders(Long customerId) {
        // Get customer
        Customer customer = customerClient.getCustomer(customerId);
        
        // Get orders
        List<Order> orders = orderClient.getOrders(customerId);
        
        // Combine
        return new CustomerOrders(customer, orders);
    }
}
```

**CQRS (Command Query Responsibility Segregation):**
```java
// Command side - write model
@Service
public class OrderCommandService {
    
    @Transactional
    public Order createOrder(CreateOrderCommand command) {
        Order order = new Order();
        order.setCustomerId(command.getCustomerId());
        order.setItems(command.getItems());
        
        orderRepository.save(order);
        
        // Update read model
        orderViewRepository.save(new OrderView(order));
        
        return order;
    }
}

// Query side - read model
@Service
public class OrderQueryService {
    
    public Optional<OrderView> getOrder(Long id) {
        return orderViewRepository.findById(id);
    }
    
    public List<OrderView> getOrdersByCustomer(Long customerId) {
        return orderViewRepository.findByCustomerId(customerId);
    }
}
```

### 1.5 Saga Pattern

**Orchestration-based Saga:**
```java
@Service
public class OrderSagaOrchestrator {
    
    public void executeOrderSaga(Order order) {
        try {
            // Step 1: Reserve inventory
            inventoryService.reserve(order.getItems());
            
            // Step 2: Process payment
            paymentService.charge(order.getCustomerId(), order.getTotal());
            
            // Step 3: Create order
            order.setStatus(OrderStatus.CREATED);
            orderRepository.save(order);
            
        } catch (Exception e) {
            // Compensate on failure
            compensate(order);
        }
    }
    
    private void compensate(Order order) {
        // Rollback payment
        paymentService.refund(order.getCustomerId(), order.getTotal());
        
        // Release inventory
        inventoryService.release(order.getItems());
    }
}
```

---

## 2. Service Discovery

### 2.1 Service Registry

**Registration:**
```java
@SpringBootApplication
@EnableDiscoveryClient
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}

// Service registration happens automatically with Eureka/Consul
// Or manually:
@Bean
public ServiceRegistry serviceRegistry() {
    return new ServiceRegistry("order-service", "localhost", 8080);
}
```

**Eureka Server Configuration:**
```yaml
# application.yml
server:
  port: 8761

eureka:
  instance:
    hostname: localhost
  client:
    register-with-eureka: false
    fetch-registry: false
  server:
    enable-self-preservation: true
```

**Eureka Client:**
```yaml
spring:
  application:
    name: order-service

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true
    lease-renewal-interval-in-seconds: 30
```

### 2.2 Client-Side Discovery

```java
@Service
public class ProductClient {
    
    @Autowired
    private DiscoveryClient discoveryClient;
    
    public Product getProduct(Long id) {
        // Get all instances
        List<ServiceInstance> instances = discoveryClient
            .getInstances("product-service");
        
        // Load balance
        ServiceInstance selected = loadBalancer.select(instances);
        
        // Call service
        String url = "http://" + selected.getHost() + ":" 
            + selected.getPort() + "/products/" + id;
        
        return restTemplate.getForObject(url, Product.class);
    }
}
```

### 2.3 Server-Side Discovery (Load Balancer)

```java
@Configuration
public class LoadBalancerConfig {
    
    @Bean
    public ServiceInstanceListSupplier serviceInstanceListSupplier(
            ConfigurationBasedServiceInstanceListSupplier supplier) {
        return supplier;
    }
}

// Using Spring Cloud LoadBalancer
@Service
public class ProductService {
    
    @Autowired
    private LoadBalancer loadBalancer;
    
    public Product getProduct(Long id) {
        ServiceInstance instance = loadBalancer.choose("product-service");
        String url = instance.getUri() + "/products/" + id;
        return restTemplate.getForObject(url, Product.class);
    }
}
```

### 2.4 Service Mesh

**Example with Istio (VirtualService):**
```yaml
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: order-service
spec:
  hosts:
  - order-service
  http:
  - route:
    - destination:
        host: order-service
        subset: v1
      weight: 90
    - destination:
        host: order-service
        subset: v2
      weight: 10
```

---

## 3. API Gateway

### 3.1 Gateway Pattern

**Spring Cloud Gateway:**
```java
@Configuration
public class GatewayConfig {
    
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("order_route", r -> r
                .path("/api/orders/**")
                .filters(f -> f
                    .stripPrefix(1)
                    .addRequestHeader("X-Gateway", "true"))
                .uri("lb://order-service"))
            .route("product_route", r -> r
                .path("/api/products/**")
                .uri("lb://product-service"))
            .build();
    }
}
```

### 3.2 Request Filtering

```java
@Component
public class AuthenticationFilter extends GatewayFilter {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, 
                             GatewayFilterChain chain) {
        String authHeader = exchange.getRequest()
            .getHeaders().getFirst("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        return chain.filter(exchange);
    }
}
```

### 3.3 Circuit Breaker Integration

```java
@Configuration
public class GatewayCircuitBreakerConfig {
    
    @Bean
    public RouteLocator circuitBreakerRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("products_circuit", r -> r
                .path("/api/products/**")
                .filters(f -> f
                    .circuitBreaker(c -> c
                        .setName("products")
                        .setFallbackUri("forward:/fallback/products")))
                .uri("lb://product-service"))
            .build();
    }
}
```

### 3.4 Rate Limiting

```java
@Component
public class RateLimitFilter implements GatewayFilter, Ordered {
    
    private final Map<String, AtomicInteger> counters = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS = 100;
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, 
                            GatewayFilterChain chain) {
        String clientId = exchange.getRequest().getRemoteAddress()
            .getAddress().getHostAddress();
        
        int count = counters.computeIfAbsent(clientId, k -> new AtomicInteger())
            .incrementAndGet();
        
        if (count > MAX_REQUESTS) {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return exchange.getResponse().setComplete();
        }
        
        return chain.filter(exchange);
    }
    
    @Override
    public int getOrder() {
        return -100;
    }
}
```

### 3.5 Centralized Configuration

```yaml
# application.yml in config server
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/myorg/config-repo
          default-label: main

# Client application.yml
spring:
  cloud:
    config:
      enabled: true
      uri: http://localhost:8888
      name: order-service
      profile: dev
```

---

## Key Concepts Summary

| Pattern | Description |
|---------|-------------|
| Service Decomposition | Splitting monolith into services by domain |
| API Composition | Aggregating data from multiple services |
| CQRS | Separate read and write models |
| Saga | Distributed transactions |
| Service Discovery | Dynamic service location |
| API Gateway | Single entry point for clients |
| Circuit Breaker | Fault tolerance |
| Service Mesh | Infrastructure layer for service communication |