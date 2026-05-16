# Microservices Exercises

## Exercise 1: Service Decomposition

**Problem:** Design a microservices architecture for an e-commerce system.

**Requirements:**
1. Identify bounded contexts
2. Define service boundaries
3. Create service interfaces
4. Handle inter-service communication

**Solution:**

```java
// Service definitions

// 1. Product Service API
public interface ProductService {
    Product getProduct(Long id);
    List<Product> searchProducts(String query);
    boolean checkStock(Long productId, int quantity);
    void reserveStock(Long productId, int quantity);
    void releaseStock(Long productId, quantity);
}

// 2. Order Service API
public interface OrderService {
    Order createOrder(CreateOrderRequest request);
    Order getOrder(Long id);
    void updateOrderStatus(Long id, OrderStatus status);
    List<Order> getCustomerOrders(Long customerId);
}

// 3. Payment Service API
public interface PaymentService {
    PaymentResult processPayment(PaymentRequest request);
    PaymentResult refund(Long paymentId);
    Payment getPayment(Long id);
}

// 4. Shipping Service API
public interface ShippingService {
    ShippingLabel createLabel(Long orderId);
    TrackingInfo getTracking(String trackingNumber);
    void updateStatus(String trackingNumber, ShippingStatus status);
}

// Service implementation examples

// ProductServiceImpl.java
@Service
public class ProductServiceImpl implements ProductService {
    
    @Override
    public Product getProduct(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
    }
    
    @Override
    public List<Product> searchProducts(String query) {
        return productRepository.findByNameContaining(query);
    }
    
    @Override
    public boolean checkStock(Long productId, int quantity) {
        Product product = getProduct(productId);
        return product.getStock() >= quantity;
    }
    
    @Override
    public void reserveStock(Long productId, int quantity) {
        productRepository.decrementStock(productId, quantity);
    }
    
    @Override
    public void releaseStock(Long productId, int quantity) {
        productRepository.incrementStock(productId, quantity);
    }
}

// OrderServiceImpl.java with saga
@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired private ProductServiceClient productClient;
    @Autowired private PaymentServiceClient paymentClient;
    @Autowired private ShippingServiceClient shippingClient;
    
    @Override
    public Order createOrder(CreateOrderRequest request) {
        // Validate inventory first
        for (OrderItem item : request.getItems()) {
            if (!productClient.checkStock(item.getProductId(), item.getQuantity())) {
                throw new InsufficientStockException(item.getProductId());
            }
        }
        
        // Create order in PENDING state
        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setItems(request.getItems());
        order.setStatus(OrderStatus.PENDING);
        
        try {
            // Reserve inventory
            for (OrderItem item : request.getItems()) {
                productClient.reserveStock(item.getProductId(), item.getQuantity());
            }
            
            // Process payment
            PaymentResult payment = paymentClient.processPayment(
                request.getCustomerId(), order.getTotal());
            
            order.setPaymentId(payment.getPaymentId());
            order.setStatus(OrderStatus.CONFIRMED);
            
            // Create shipping label
            shippingClient.createLabel(order.getId());
            
        } catch (Exception e) {
            // Compensate: release inventory, cancel payment
            compensate(order);
            throw new OrderCreationException("Failed to create order", e);
        }
        
        return orderRepository.save(order);
    }
    
    private void compensate(Order order) {
        for (OrderItem item : order.getItems()) {
            productClient.releaseStock(item.getProductId(), item.getQuantity());
        }
        if (order.getPaymentId() != null) {
            paymentClient.refund(order.getPaymentId());
        }
    }
}
```

---

## Exercise 2: Service Discovery Implementation

**Problem:** Implement client-side service discovery with Eureka.

**Requirements:**
1. Configure Eureka client
2. Implement service lookup
3. Add load balancing
4. Handle service unavailability

**Solution:**

```java
// Eureka Server Configuration
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}

// application.yml for Eureka Server
/*
server:
  port: 8761
eureka:
  instance:
    hostname: localhost
  client:
    register-with-eureka: false
    fetch-registry: false
*/

// Service discovery client
@Service
public class ServiceDiscoveryClient {
    
    @Autowired
    private DiscoveryClient discoveryClient;
    
    @Autowired
    private LoadBalancer loadBalancer;
    
    public String getServiceUrl(String serviceName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        
        if (instances.isEmpty()) {
            throw new ServiceUnavailableException(serviceName);
        }
        
        ServiceInstance selected = loadBalancer.select(instances);
        return selected.getUri().toString();
    }
    
    public <T> T executeRequest(String serviceName, 
                                Function<String, T> request) {
        String baseUrl = getServiceUrl(serviceName);
        return request.apply(baseUrl);
    }
}

// Load balancer with retry
@Component
public class RandomLoadBalancer implements ServiceInstanceChooser {
    
    @Override
    public ServiceInstance choose(String serviceId) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
        
        if (instances.isEmpty()) {
            throw new ServiceUnavailableException("No instances for: " + serviceId);
        }
        
        // Random selection
        int index = ThreadLocalRandom.current().nextInt(instances.size());
        return instances.get(index);
    }
}

// REST client with service discovery
@Service
public class ProductServiceClient {
    
    @Autowired
    private ServiceDiscoveryClient discoveryClient;
    
    @Autowired
    private RestTemplate restTemplate;
    
    public Product getProduct(Long productId) {
        return discoveryClient.executeRequest("product-service", url -> 
            restTemplate.getForObject(url + "/api/products/" + productId, Product.class));
    }
    
    public List<Product> searchProducts(String query) {
        return discoveryClient.executeRequest("product-service", url -> 
            restTemplate.getForObject(url + "/api/products/search?q=" + query, 
                new ParameterizedTypeReference<List<Product>>() {}));
    }
}

// Configuration
@Configuration
public class AppConfig {
    
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

---

## Exercise 3: API Gateway Implementation

**Problem:** Create an API gateway with routing, authentication, and rate limiting.

**Solution:**

```java
// Spring Cloud Gateway Configuration
@Configuration
public class GatewayConfiguration {
    
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("product_route", r -> r
                .path("/api/products/**")
                .filters(f -> f
                    .stripPrefix(1)
                    .addRequestHeader("X-Gateway", "true")
                    .circuitBreaker(c -> c.setName("products").setFallbackUri("/fallback")))
                .uri("lb://product-service"))
            
            .route("order_route", r -> r
                .path("/api/orders/**")
                .filters(f -> f
                    .stripPrefix(1)
                    .addRequestHeader("X-Service", "order"))
                .uri("lb://order-service"))
            
            .route("customer_route", r -> r
                .path("/api/customers/**")
                .filters(f -> f.stripPrefix(1))
                .uri("lb://customer-service"))
            
            .route("fallback_route", r -> r
                .path("/fallback/**")
                .filters(f -> f.setStatus(200)
                    .modifyResponseBody("{}", "{\"message\":\"Service unavailable\"}"))
                .uri("forward:/fallback"))
            .build();
    }
}

// Authentication filter
@Component
public class AuthenticationFilter extends AbstractGatewayFilter {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, 
                            GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // Skip auth for health endpoint
        if (request.getURI().getPath().equals("/health")) {
            return chain.filter(exchange);
        }
        
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        String token = authHeader.substring(7);
        
        return validateToken(token)
            .flatMap(valid -> {
                if (valid) {
                    return chain.filter(exchange);
                }
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            });
    }
    
    private Mono<Boolean> validateToken(String token) {
        // Validate JWT token
        return Mono.just(tokenValidator.validate(token));
    }
}

// Rate limiting filter
@Component
public class RateLimitFilter extends AbstractGatewayFilter {
    
    private final Map<String, RateLimitBucket> buckets = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS = 100;
    private static final Duration WINDOW = Duration.ofMinutes(1);
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, 
                            GatewayFilterChain chain) {
        String clientId = getClientId(exchange);
        RateLimitBucket bucket = buckets.computeIfAbsent(clientId, 
            k -> new RateLimitBucket(MAX_REQUESTS, WINDOW));
        
        if (!bucket.tryConsume()) {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            exchange.getResponse().getHeaders().add("X-RateLimit-Retry-After", "60");
            return exchange.getResponse().setComplete();
        }
        
        exchange.getResponse().getHeaders().add("X-RateLimit-Remaining", 
            String.valueOf(bucket.availablePermits()));
        
        return chain.filter(exchange);
    }
    
    private String getClientId(ServerWebExchange exchange) {
        String auth = exchange.getRequest().getHeaders().getFirst("X-Client-Id");
        return auth != null ? auth : 
            exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
    }
}

// Rate limit bucket
public class RateLimitBucket {
    private final int maxPermits;
    private final Duration window;
    private final AtomicInteger permits;
    private volatile Instant windowStart;
    
    public RateLimitBucket(int maxPermits, Duration window) {
        this.maxPermits = maxPermits;
        this.window = window;
        this.permits = new AtomicInteger(maxPermits);
        this.windowStart = Instant.now();
    }
    
    public synchronized boolean tryConsume() {
        if (Instant.now().isAfter(windowStart.plus(window))) {
            permits.set(maxPermits);
            windowStart = Instant.now();
        }
        return permits.decrementAndGet() >= 0;
    }
    
    public int availablePermits() {
        return permits.get();
    }
}
```

---

## Exercise 4: Distributed Tracing

**Problem:** Implement distributed tracing with Spring Cloud Sleuth.

**Solution:**

```java
// Add dependencies: spring-cloud-starter-sleuth, spring-cloud-sleuth-zipkin

// Configuration
spring:
  sleuth:
    sampler:
      probability: 1.0
  zipkin:
    base-url: http://localhost:9411
    sender:
      type: web

// Service A - initiating trace
@Service
public class OrderService {
    
    @Autowired
    private Tracer tracer;
    
    @Autowired
    private RestTemplate restTemplate;
    
    public Order createOrder(OrderRequest request) {
        // Add custom span data
        Span span = tracer.currentSpan()
            .tag("order.customerId", String.valueOf(request.getCustomerId()));
        
        // Call service B
        String response = restTemplate.postForObject(
            "http://inventory-service/api/reserve",
            request,
            String.class);
        
        Order order = new Order();
        order.setTraceId(tracer.currentSpan().context().traceId());
        
        return order;
    }
}

// Service B - receiving trace
@Service
public class InventoryService {
    
    @Autowired
    private Tracer tracer;
    
    public void reserveInventory(OrderRequest request) {
        // Trace automatically continues from Service A
        // Add additional tags
        tracer.currentSpan().tag("inventory.items", 
            String.valueOf(request.getItems().size()));
        
        // Business logic
    }
}

// Service C - async event processing
@Service
public class NotificationService {
    
    @Autowired
    private Tracer tracer;
    
    @StreamListener(Sink.INPUT)
    public void handleOrderEvent(@Payload OrderEvent event) {
        // Trace continues through message
        tracer.currentSpan().tag("notification.type", event.getType());
        
        // Send notification
    }
}

// Custom trace data
@Component
class TraceDataEnhancer implements TraceDataEnhancer {
    
    @Override
    public Span.Builder enhance(Span.Builder spanBuilder, 
                                Map<String, Object> map) {
        spanBuilder.tag("application.name", "my-app");
        return spanBuilder;
    }
}
```

---

## Exercise 5: Circuit Breaker Implementation

**Problem:** Implement fault tolerance patterns with circuit breaker.

**Solution:**

```java
// Product Service with Circuit Breaker
@Service
public class ProductServiceClient {
    
    @Autowired
    private CircuitBreakerFactory circuitBreakerFactory;
    
    @Autowired
    private RestTemplate restTemplate;
    
    private CircuitBreaker circuitBreaker = circuitBreakerFactory.create("product");
    
    public Product getProduct(Long productId) {
        return circuitBreaker.run(
            () -> restTemplate.getForObject(
                "http://product-service/api/products/" + productId, 
                Product.class),
            throwable -> {
                // Fallback
                return getCachedProduct(productId);
            }
        );
    }
    
    private Product getCachedProduct(Long productId) {
        // Return from cache or default
        Product cached = cache.get(productId);
        if (cached == null) {
            cached = new Product();
            cached.setId(productId);
            cached.setName("Default Product");
            cached.setAvailable(false);
        }
        return cached;
    }
}

// Resilience4j Configuration
@Configuration
public class ResilienceConfig {
    
    @Bean
    public CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
            .failureRateThreshold(50)
            .slidingWindowSize(10)
            .minimumNumberOfCalls(5)
            .permittedNumberOfCallsInHalfOpenState(3)
            .automaticTransitionFromOpenToHalfOpenEnabled(true)
            .waitDurationInOpenState(Duration.ofSeconds(10))
            .build();
    }
    
    @Bean
    public CircuitBreakerFactory circuitBreakerFactory(CircuitBreakerConfig config) {
        return new Resilience4JCircuitBreakerFactory(config, new ArrayList<>());
    }
}

// Retry with circuit breaker
@Configuration
public class RetryConfig {
    
    @Bean
    public RetryRegistry retryRegistry() {
        return RetryRegistry.ofDefaults();
    }
    
    @Bean
    public Retry retry(RetryRegistry registry) {
        return registry.retry("product", config -> 
            config.maxAttempts(3)
                .waitDuration(Duration.ofMillis(500))
                .retryExceptions(IOException.class, TimeoutException.class));
    }
}

// Time limiting
@Configuration
public class TimeLimiterConfig {
    
    @Bean
    public TimeLimiterRegistry timeLimiterRegistry() {
        return TimeLimiterRegistry.ofDefaults();
    }
    
    @Bean
    public TimeLimiter timeLimiter(TimeLimiterRegistry registry) {
        return registry.timeLimiter("product", config ->
            config.timeoutDuration(Duration.ofSeconds(2))
                .cancelRunningFuture(true));
    }
}

// Bulkhead pattern
@Configuration
public class BulkheadConfig {
    
    @Bean
    public BulkheadRegistry bulkheadRegistry() {
        return BulkheadRegistry.ofDefaults();
    }
    
    @Bean
    public Bulkhead bulkhead(BulkheadRegistry registry) {
        return registry.bulkhead("product", config ->
            config.maxConcurrentCalls(10)
                .maxWaitDuration(Duration.ofMillis(500)));
    }
}
```

---

## Summary

| Exercise | Key Concepts |
|----------|-------------|
| 1 | Domain decomposition, bounded contexts, saga pattern |
| 2 | Eureka, client-side discovery, load balancing |
| 3 | Gateway routing, filters, rate limiting |
| 4 | Distributed tracing, Sleuth, Zipkin |
| 5 | Circuit breaker, retry, bulkhead, time limiter |