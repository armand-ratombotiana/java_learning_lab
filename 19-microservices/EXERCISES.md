# Microservices - Exercises

---

## Exercise Set 1: Service Discovery

### Exercise 1.1: Eureka Server Setup
**Task**: Configure and start a Eureka service registry.

```yaml
# application.yml for Eureka Server
server:
  port: 8761

eureka:
  instance:
    hostname: localhost
  client:
    register-with-eureka: false
    fetch-registry: false
```

Questions:
- Why disable self-registration in production?
- How would you configure for multiple Eureka instances?

---

### Exercise 1.2: Register a Service
**Task**: Register a new service with Eureka.

```java
@SpringBootApplication
@EnableDiscoveryClient
public class InventoryService {
    public static void main(String[] args) {
        SpringApplication.run(InventoryService.class, args);
    }
}
```

Configuration:
```yaml
spring:
  application:
    name: inventory-service

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

---

### Exercise 1.3: Service Discovery Client
**Task**: Use DiscoveryClient to find services programmatically.

```java
public List<String> findServiceInstances(String serviceName) {
    // Use DiscoveryClient
    // Return list of instance URLs
    // Handle case when service not found
}
```

---

## Exercise Set 2: API Gateway

### Exercise 2.1: Basic Routing
**Task**: Configure routes to different services.

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: product-service
          uri: lb://product-service
          predicates:
            - Path=/products/**
        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/orders/**
```

---

### Exercise 2.2: Request Modification
**Task**: Add headers and modify requests.

```java
public RouteLocator customRoutes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("add-header", r -> r
            .path("/api/**")
            .filters(f -> f
                .addRequestHeader("X-Service", "gateway")
                .addRequestHeader("X-Correlation-Id", UUID.randomUUID().toString()))
            .uri("lb://product-service"))
        .build();
}
```

---

### Exercise 2.3: Rate Limiting
**Task**: Implement rate limiting on the gateway.

```yaml
spring:
  cloud:
    gateway:
      default-filters:
        - name: RequestRateLimiter
          args:
            redis-rate-replier.amount: 10
            redis-rate-replier.burst-capacity: 20
```

---

## Exercise Set 3: Service Communication

### Exercise 3.1: OpenFeign Client
**Task**: Create a Feign client to call product service.

```java
@FeignClient(name = "product-service")
public interface ProductClient {
    
    @GetMapping("/api/products/{id}")
    Product getProductById(@PathVariable("id") Long id);
    
    @GetMapping("/api/products")
    List<Product> getAllProducts();
    
    @PostMapping("/api/products")
    Product createProduct(@RequestBody Product product);
}
```

---

### Exercise 3.2: Feign with Fallback
**Task**: Add fallback when service is unavailable.

```java
@FeignClient(name = "product-service", fallback = ProductClientFallback.class)
public interface ProductClient {
    @GetMapping("/api/products/{id}")
    Product getProductById(@PathVariable("id") Long id);
}

@Component
public class ProductClientFallback implements ProductClient {
    @Override
    public Product getProductById(Long id) {
        return new Product(-1L, "Fallback Product", BigDecimal.ZERO);
    }
}
```

---

### Exercise 3.3: Request/Response Logging
**Task**: Add logging to Feign clients.

```java
@Configuration
public class FeignLoggingConfig {
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
```

---

## Exercise Set 4: Resilience Patterns

### Exercise 4.1: Circuit Breaker
**Task**: Implement circuit breaker for payment service.

```java
@Service
public class PaymentService {
    
    @CircuitBreaker(name = "payment", fallbackMethod = "paymentFallback")
    public PaymentResult processPayment(Long orderId, BigDecimal amount) {
        // Call payment gateway
    }
    
    public PaymentResult paymentFallback(Long orderId, BigDecimal amount, Throwable t) {
        return PaymentResult.pending(orderId);
    }
}
```

Configuration:
```yaml
resilience4j:
  circuitbreaker:
    instances:
      payment:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 60s
```

---

### Exercise 4.2: Retry Policy
**Task**: Add retry with exponential backoff.

```java
@Retry(name = "inventoryService")
public Inventory checkAvailability(Long productId, Integer quantity) {
    // Call inventory service
}

@Configuration
public class RetryConfig {
    @Bean
    public RetryRegistry retryRegistry() {
        RetryConfig config = RetryConfig.custom()
            .maxAttempts(3)
            .waitDuration(Duration.ofSeconds(2))
            .retryExceptions(Exception.class)
            .build();
        
        return RetryRegistry.of(config);
    }
}
```

---

### Exercise 4.3: Bulkhead Isolation
**Task**: Implement bulkhead pattern for thread isolation.

```java
@Bulkhead(name = "externalApi")
public ExternalResponse callExternalApi(Request request) {
    // Call external API with limited concurrency
}

@Configuration
public class BulkheadConfig {
    @Bean
    public BulkheadRegistry bulkheadRegistry() {
        BulkheadConfig config = BulkheadConfig.custom()
            .maxConcurrentCalls(10)
            .maxWaitDuration(Duration.ofMillis(100))
            .build();
        
        return BulkheadRegistry.of(config);
    }
}
```

---

## Exercise Set 5: Kubernetes Deployment

### Exercise 5.1: Docker Configuration
**Task**: Create Dockerfile for a microservice.

```dockerfile
FROM eclipse-temurin:17-jre-alpine
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8081
```

Build and run:
```bash
docker build -t product-service:1.0 .
docker run -p 8081:8081 product-service:1.0
```

---

### Exercise 5.2: Kubernetes Deployment
**Task**: Deploy services to Kubernetes.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: product-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: product-service
  template:
    metadata:
      labels:
        app: product-service
    spec:
      containers:
        - name: product-service
          image: product-service:1.0
          ports:
            - containerPort: 8081
          readinessProbe:
            httpGet:
              path: /actuator/health
              port: 8081
            initialDelaySeconds: 30
```

---

### Exercise 5.3: Service and Ingress
**Task**: Configure Kubernetes Service and Ingress.

```yaml
apiVersion: v1
kind: Service
metadata:
  name: product-service
spec:
  selector:
    app: product-service
  ports:
    - port: 80
      targetPort: 8081
  type: ClusterIP
---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: api-gateway
spec:
  rules:
    - host: api.example.com
      http:
        paths:
          - path: /products
            pathType: Prefix
            backend:
              service:
                name: product-service
                port:
                  number: 80
```

---

## Challenge Problems

### Challenge 1: Distributed Tracing
**Difficulty**: Advanced
**Task**: Implement distributed tracing across all services.

Requirements:
- Propagate trace ID through all service calls
- Use OpenTelemetry or Zipkin
- Visualize trace in Jaeger UI

---

### Challenge 2: Blue-Green Deployment
**Difficulty**: Advanced
**Task**: Implement blue-green deployment strategy.

Requirements:
- Deploy new version alongside existing
- Route traffic using service mesh or load balancer
- Implement one-click rollback

---

### Challenge 3: Chaos Engineering
**Difficulty**: Expert
**Task**: Test system resilience with chaos experiments.

Requirements:
- Kill random pods
- Introduce network latency
- Verify system recovers gracefully

---

## Solutions Guidance

For each exercise:
1. Start with understanding the architecture
2. Implement incrementally
3. Test each component independently
4. Add monitoring and observability

---

## Time Estimates

| Exercise | Estimated Time |
|----------|---------------|
| Set 1 | 2-3 hours |
| Set 2 | 2-3 hours |
| Set 3 | 2-3 hours |
| Set 4 | 3-4 hours |
| Set 5 | 4-5 hours |
| Challenges | 8+ hours each |

