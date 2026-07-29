# Circuit Breaker & Resilience Patterns — Deep Interview Guide

## Table of Contents
1. [Circuit Breaker Fundamentals](#circuit-breaker-fundamentals)
2. [Circuit Breaker States & Transition Logic](#circuit-breaker-states--transition-logic)
3. [Bulkhead Pattern](#bulkhead-pattern)
4. [Rate Limiter](#rate-limiter)
5. [Retry & Timeout](#retry--timeout)
6. [Cache-Aside Pattern](#cache-aside-pattern)
7. [Resilience4j vs Hystrix](#resilience4j-vs-hystrix)
8. [Java Code Examples](#java-code-examples)
9. [15+ Interview Questions](#15-interview-questions)

---

## Circuit Breaker Fundamentals

The Circuit Breaker pattern prevents cascading failures by detecting when a downstream service is failing and stopping requests to it until it recovers.

### Core Concept

```
Normal Operation (Circuit Closed)
Client ──→ Service
    ↓
200 OK

Failure Threshold Exceeded (Circuit Open)
Client ──→ Circuit Breaker ──→ FALLBACK (no request to service)
    ↓
Fallback response

Recovery Attempt (Circuit Half-Open)
Client ──→ Circuit Breaker ──→ Service (probe request)
    ↓
200 OK → Close
    OR
500 Error → Open
```

---

## Circuit Breaker States & Transition Logic

### State Machine

```
         ┌──────────────────────────────┐
         │                              │
         ▼                              │
    ┌─────────┐    failure ≥ threshold    ┌─────────┐
    │  CLOSED  │ ────────────────────────► │   OPEN  │
    │ (normal) │                           │(blocked)│
    └─────────┘                           └─────────┘
         ▲                                    │
         │         timeout elapsed             │
         │    ┌──────────────────────────────┐ │
         │    │                              │ │
         │    ▼                              │ │
         │ ┌──────────┐   success            │ │
         └─┤HALF-OPEN │◄─────────────────────┘ │
           │  (probe) │── failure ─────────────┘
           └──────────┘
```

### Transition Parameters

| Parameter | Description | Typical Value |
|-----------|-------------|---------------|
| **slidingWindowSize** | Number of calls to evaluate | 10, 20, 100 |
| **minimumNumberOfCalls** | Min calls before evaluating | 5, 10 |
| **failureRateThreshold** | % failure to open circuit | 50, 60, 75 |
| **slowCallRateThreshold** | % slow calls to treat as failure | 50, 100 |
| **slowCallDurationThreshold** | Duration considered slow | 1s, 5s |
| **waitDurationInOpenState** | Time before half-open | 5s, 10s, 60s |
| **permittedNumberOfCallsInHalfOpenState** | Probes in half-open | 2, 3, 5 |
| **writableStackTraceEnabled** | Preserve full stack traces | false (for perf) |

### Resilience4j Configuration

```java
CircuitBreakerConfig config = CircuitBreakerConfig.custom()
    .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
    .slidingWindowSize(10)
    .minimumNumberOfCalls(5)
    .failureRateThreshold(50)
    .slowCallRateThreshold(100)
    .slowCallDurationThreshold(Duration.ofSeconds(2))
    .waitDurationInOpenState(Duration.ofSeconds(10))
    .permittedNumberOfCallsInHalfOpenState(3)
    .recordExceptions(IOException.class, TimeoutException.class)
    .ignoreExceptions(BusinessException.class)
    .build();

CircuitBreaker circuitBreaker = CircuitBreakerRegistry.of(config)
    .circuitBreaker("paymentService", config);
```

### Event Monitoring

```java
circuitBreaker.getEventPublisher()
    .onSuccess(e -> log.info("CB success: {}", e.getElapsedDuration()))
    .onError(e -> log.warn("CB error: {}", e.getThrowable().getMessage()))
    .onStateTransition(e -> log.warn("CB state change: {} → {}",
        e.getOldState(), e.getNewState()))
    .onCallNotPermitted(e -> log.warn("CB call blocked!"))
    .onFailureRateExceeded(e -> log.error("CB failure rate exceeded: {}%",
        e.getFailureRate()));
```

---

## Bulkhead Pattern

Isolates resources so failure in one part doesn't take down the entire system.

### Types

#### 1. Semaphore Bulkhead (Thread-based)

```java
BulkheadConfig config = BulkheadConfig.custom()
    .maxConcurrentCalls(10)
    .maxWaitDuration(Duration.ofMillis(500))
    .build();

Bulkhead bulkhead = BulkheadRegistry.of(config)
    .bulkhead("orderService", config);
```

#### 2. ThreadPool Bulkhead (Separate thread pool)

```java
ThreadPoolBulkheadConfig config = ThreadPoolBulkheadConfig.custom()
    .maxThreadPoolSize(10)
    .coreThreadPoolSize(4)
    .queueCapacity(20)
    .keepAliveDuration(Duration.ofMinutes(1))
    .build();

ThreadPoolBulkhead bulkhead = ThreadPoolBulkheadRegistry.of(config)
    .bulkhead("paymentService", config);
```

### Visual

```
Semaphore Bulkhead                ThreadPool Bulkhead
┌────────────────────┐           ┌────────────────────┐
│ Permits: 10/10     │           │ Thread Pool        │
│ Request A ──► OK   │           │ [ T1 ][ T2 ][ T3 ] │
│ Request B ──► OK   │           │ [ T4 ][ __ ][ __ ] │
│ Request C ──► WAIT │           │ Queue: [R5][R6]    │
│ Request D ──► DENY │           │ Full → Reject      │
└────────────────────┘           └────────────────────┘
```

### Use Cases

| Strategy | When to Use | Example |
|----------|-------------|---------|
| **Semaphore** | Fast operations, non-blocking | Cache lookups, simple calculations |
| **ThreadPool** | Blocking I/O, variable latency | Database queries, HTTP calls |

---

## Rate Limiter

Controls the rate of requests to prevent overload.

### Algorithms

| Algorithm | Description | Strength |
|-----------|-------------|----------|
| **Token Bucket** | Tokens added at fixed rate, consumed per request | Allows bursts up to capacity |
| **Sliding Window** | Count requests in a rolling time window | Smooth, prevents edge spikes |
| **Leaky Bucket** | Requests processed at fixed rate, excess queued/dropped | Very smooth, no bursts |

### Resilience4j RateLimiter

```java
RateLimiterConfig config = RateLimiterConfig.custom()
    .limitForPeriod(10)          // requests per period
    .limitRefreshPeriod(Duration.ofSeconds(1))  // period duration
    .timeoutDuration(Duration.ofMillis(500))    // wait for permit
    .build();

RateLimiter rateLimiter = RateLimiterRegistry.of(config)
    .rateLimiter("api", config);
```

### Token Bucket Internals

```
Token Bucket: capacity=20, refill=10/sec
Time 0:  [||||||||||||||||||||] 20 tokens available
Time 0.5: [||||||||||          ] 10 consumed, 10 remain + 5 refilled = 15
Time 1.0: [||||||||||||||||||  ] 18 available
Time 1.5: [|||||||||           ] spike: 20 requests, 11 refilled = 9 remain
```

---

## Retry & Timeout

### Retry with Exponential Backoff

```java
RetryConfig config = RetryConfig.custom()
    .maxAttempts(3)
    .waitDuration(Duration.ofMillis(500))
    .intervalFunction(IntervalFunction.ofExponentialBackoff(
        Duration.ofMillis(100),  // initial interval
        2,                        // multiplier
        Duration.ofSeconds(10)   // max interval
    ))
    .retryExceptions(IOException.class, TimeoutException.class)
    .retryOnResult(result -> result.status() == 429)  // retry on rate limit
    .build();

Retry retry = RetryRegistry.of(config).retry("dbQuery", config);
```

### Timeout

```java
TimeLimiterConfig config = TimeLimiterConfig.custom()
    .timeoutDuration(Duration.ofSeconds(2))
    .cancelRunningFuture(true)
    .build();

TimeLimiter timeLimiter = TimeLimiterRegistry.of(config)
    .timeLimiter("paymentService", config);
```

### Combined: Retry + CircuitBreaker + TimeLimiter

```java
// Order: TimeLimiter → Retry → CircuitBreaker → Bulkhead
Supplier<PaymentResponse> decorated = Decorators.ofSupplier(
        () -> paymentService.process(request))
    .withTimeLimiter(timeLimiter)
    .withRetry(retry, Executors.newScheduledThreadPool(1))
    .withCircuitBreaker(circuitBreaker)
    .withBulkhead(bulkhead)
    .decorate();

// Execute with fallback
CompletableFuture<PaymentResponse> future = CompletableFuture
    .supplyAsync(decorated)
    .exceptionally(t -> new PaymentResponse("FALLBACK", t.getMessage()));
```

---

## Cache-Aside Pattern

Application checks cache before calling the service, then populates cache on miss.

```java
@Service
public class CachedProductService {

    private final CacheManager cacheManager;
    private final ProductServiceClient client;

    public CachedProductService(CacheManager cacheManager, ProductServiceClient client) {
        this.cacheManager = cacheManager;
        this.client = client;
    }

    @CircuitBreaker(name = "productService")
    public Product getProduct(String id) {
        Cache cache = cacheManager.getCache("products");
        Product product = cache.get(id, Product.class);
        if (product != null) {
            return product; // Cache hit
        }
        // Cache miss — call service with resilience
        product = client.getProduct(id);
        cache.put(id, product);
        return product;
    }
}
```

---

## Resilience4j vs Hystrix

| Feature | Resilience4j | Hystrix (Deprecated) |
|---------|-------------|---------------------|
| **Status** | Active, maintained | Archived, no updates |
| **Java version** | Java 8+ | Java 7+ |
| **Spring Boot** | 2.x/3.x via starter | Via spring-cloud-netflix |
| **Circuit Breaker** | Full sliding window (count/time-based) | Fixed-size window |
| **Bulkhead** | Semaphore + ThreadPool | Only semaphore |
| **Rate Limiter** | Built-in (token bucket) | Not available |
| **Retry** | Built-in with backoff | No built-in retry |
| **Time Limiter** | Separate module | Not available |
| **Cache** | Built-in | Not available |
| **Thread model** | Decorator (no thread overhead) | Thread-per-command |
| **Reactive** | Yes (Reactor, RxJava) | No |
| **Metrics** | Micrometer, Prometheus | Hystrix Dashboard |
| **Fault tolerance** | Modular, choose what you need | Monolithic, circuit-breaker required |
| **Performance** | ~10µs overhead per call | ~100µs per call (thread overhead) |

### Migration from Hystrix to Resilience4j

| Hystrix | Resilience4j |
|---------|--------------|
| `@HystrixCommand` | `@CircuitBreaker(name = "...")` |
| `@HystrixProperty` | `application.yml` configuration |
| `HystrixCommand` | `Decorators.ofSupplier(...)` |
| `HystrixObservableCommand` | Reactor `Mono.fromSupplier(...)` |
| `HystrixDashboard` | Micrometer + Grafana |
| `HystrixThreadPool` | `ThreadPoolBulkhead` |

---

## Java Code Examples

### 1. Complete Resilience4j Configuration

```java
@Configuration
@EnableAspectJAutoProxy
public class Resilience4jConfig {

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> circuitBreakerFactory() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
            .circuitBreakerConfig(CircuitBreakerConfig.custom()
                .slidingWindowType(COUNT_BASED)
                .slidingWindowSize(20)
                .minimumNumberOfCalls(5)
                .failureRateThreshold(60)
                .slowCallRateThreshold(80)
                .slowCallDurationThreshold(Duration.ofSeconds(3))
                .waitDurationInOpenState(Duration.ofSeconds(15))
                .permittedNumberOfCallsInHalfOpenState(3)
                .recordExceptions(IOException.class, HttpServerErrorException.class)
                .ignoreExceptions(BusinessValidationException.class)
                .build())
            .timeLimiterConfig(TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(2))
                .build())
            .build());

        // Service-specific configurations
        factory.configure(builder -> builder
            .circuitBreakerConfig(CircuitBreakerConfig.custom()
                .slidingWindowSize(10)
                .failureRateThreshold(30) // More sensitive for payment
                .build())
            .build(), "paymentService");
    }

    @Bean
    public Customizer<Resilience4jBulkheadProvider> bulkheadProvider() {
        return provider -> provider.configureDefault(id -> new Resilience4JConfigBuilder(id)
            .bulkheadConfig(BulkheadConfig.custom()
                .maxConcurrentCalls(20)
                .maxWaitDuration(Duration.ofMillis(200))
                .build())
            .build());
    }

    @Bean
    public Customizer<Resilience4jRetryFactory> retryFactory() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
            .retryConfig(RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(200))
                .intervalFunction(IntervalFunction.ofExponentialBackoff(
                    Duration.ofMillis(100), 2))
                .retryExceptions(IOException.class, TimeoutException.class)
                .retryOnResult(result -> result instanceof PaymentResponse r
                    && "RETRY_LATER".equals(r.status()))
                .build())
            .build());
    }
}
```

### 2. Decorators API (Programmatic)

```java
@Service
public class ResilientPaymentService {

    private static final Logger log = LoggerFactory.getLogger(ResilientPaymentService.class);

    private final CircuitBreaker circuitBreaker;
    private final Bulkhead bulkhead;
    private final Retry retry;
    private final TimeLimiter timeLimiter;
    private final RateLimiter rateLimiter;

    public ResilientPaymentService(
            CircuitBreakerRegistry cbRegistry,
            BulkheadRegistry bhRegistry,
            RetryRegistry retryRegistry,
            TimeLimiterRegistry tlRegistry,
            RateLimiterRegistry rlRegistry) {
        this.circuitBreaker = cbRegistry.circuitBreaker("paymentService");
        this.bulkhead = bhRegistry.bulkhead("paymentService");
        this.retry = retryRegistry.retry("paymentService");
        this.timeLimiter = tlRegistry.timeLimiter("paymentService");
        this.rateLimiter = rlRegistry.rateLimiter("paymentService");
    }

    public PaymentResponse process(PaymentRequest request) {
        Supplier<PaymentResponse> supplier = Decorators.ofSupplier(
                () -> callPaymentGateway(request))
            .withCircuitBreaker(circuitBreaker)
            .withBulkhead(bulkhead)
            .withRetry(retry, Executors.newScheduledThreadPool(1))
            .withTimeLimiter(timeLimiter, Executors.newSingleThreadExecutor())
            .withRateLimiter(rateLimiter)
            .decorate();

        // Try with fallback
        return Try.ofSupplier(supplier)
            .recover(throwable -> {
                log.error("All resilience mechanisms exhausted: {}", throwable.getMessage());
                return new PaymentResponse(request.orderId(), "FALLBACK",
                    "Service temporarily unavailable");
            })
            .get();
    }

    private PaymentResponse callPaymentGateway(PaymentRequest request) {
        // Simulated external call
        return paymentGateway.charge(request);
    }
}
```

### 3. Spring Annotations Approach

```java
@Service
public class OrderService {

    @CircuitBreaker(name = "orderService", fallbackMethod = "getOrdersFallback")
    @Retry(name = "orderService")
    @Bulkhead(name = "orderService", type = Bulkhead.Type.THREADPOOL)
    public List<Order> getOrders(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public List<Order> getOrdersFallback(String customerId, Throwable t) {
        log.warn("Fallback for getOrders({}): {}", customerId, t.getMessage());
        return List.of(); // Empty list is better than error
    }

    @CircuitBreaker(name = "paymentService", fallbackMethod = "processPaymentFallback")
    @TimeLimiter(name = "paymentService")
    @RateLimiter(name = "paymentService")
    public CompletableFuture<PaymentResult> processPayment(Payment payment) {
        return CompletableFuture.supplyAsync(() -> {
            return paymentGateway.process(payment);
        });
    }

    public CompletableFuture<PaymentResult> processPaymentFallback(
            Payment payment, Throwable t) {
        log.error("Payment failed for {}: {}", payment.id(), t.getMessage());
        return CompletableFuture.completedFuture(
            new PaymentResult(payment.id(), "FAILED", t.getMessage()));
    }
}
```

### 4. Reactive Resilience4j (WebFlux)

```java
@Service
public class ReactiveProductService {

    private final WebClient webClient;

    public ReactiveProductService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    public Mono<Product> getProduct(String id) {
        return webClient.get()
            .uri("http://product-service/api/products/{id}", id)
            .retrieve()
            .bodyToMono(Product.class)
            .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
            .transformDeferred(RetryOperator.of(retry))
            .transformDeferred(TimeLimiterOperator.of(timeLimiter))
            .transformDeferred(BulkheadOperator.of(bulkhead))
            .onErrorResume(t -> {
                log.error("Failed to fetch product {}: {}", id, t.getMessage());
                return Mono.just(new Product(id, "Unavailable", BigDecimal.ZERO));
            });
    }

    // With RateLimiter for reactive
    public Mono<Product> getProductWithRateLimit(String id) {
        return Mono.fromRunnable(() -> rateLimiter.acquirePermit())
            .then(getProduct(id));
    }
}
```

### 5. Custom Circuit Breaker Registry with Events

```java
@Component
public class CircuitBreakerHealthMonitor {

    private static final Logger log = LoggerFactory.getLogger(CircuitBreakerHealthMonitor.class);
    private final Map<String, CircuitBreaker.Metrics> metrics = new ConcurrentHashMap<>();

    public CircuitBreakerHealthMonitor(CircuitBreakerRegistry registry) {
        registry.getAllCircuitBreakers().forEach(cb -> {
            cb.getEventPublisher()
                .onStateTransition(this::onStateTransition)
                .onFailureRateExceeded(this::onFailureRateExceeded)
                .onCallNotPermitted(this::onCallNotPermitted);
        });
    }

    private void onStateTransition(CircuitBreakerOnStateTransitionEvent event) {
        log.warn("[CB:{}] State transition: {} → {}",
            event.getCircuitBreakerName(),
            event.getOldState(),
            event.getNewState());

        if (event.getNewState() == CircuitBreaker.State.OPEN) {
            // Send alert to monitoring system
            alertService.sendAlert("CB_OPEN",
                "Circuit breaker opened for: " + event.getCircuitBreakerName());
        }
    }

    private void onFailureRateExceeded(CircuitBreakerOnFailureRateExceededEvent event) {
        log.error("[CB:{}] Failure rate exceeded: {}%",
            event.getCircuitBreakerName(), event.getFailureRate());
    }

    private void onCallNotPermitted(CircuitBreakerOnCallNotPermittedEvent event) {
        log.warn("[CB:{}] Call blocked — circuit is OPEN",
            event.getCircuitBreakerName());
    }

    @EventListener
    public void handleCustomEvent(CircuitBreakerEvent event) {
        // Custom metrics collection
        metrics.put(event.getCircuitBreakerName(),
            event.getCircuitBreaker().getMetrics());
    }

    @Bean
    public HealthIndicator circuitBreakerHealthIndicator() {
        return () -> {
            Map<String, Object> details = new LinkedHashMap<>();
            boolean allClosed = true;
            for (Map.Entry<String, CircuitBreaker.Metrics> entry : metrics.entrySet()) {
                CircuitBreaker.State state = registry.circuitBreaker(entry.getKey()).getState();
                details.put(entry.getKey(), Map.of(
                    "state", state,
                    "failureRate", entry.getValue().getFailureRate(),
                    "bufferedCalls", entry.getValue().getNumberOfBufferedCalls(),
                    "notPermittedCalls", entry.getValue().getNumberOfNotPermittedCalls()
                ));
                if (state != CircuitBreaker.State.CLOSED) allClosed = false;
            }
            return Health.status(allClosed ? Status.UP : Status.DEGRADED)
                .withDetails(details)
                .build();
        };
    }
}
```

### 6. Decorator with Custom Fallback Chain

```java
@Service
public class MultiLevelFallbackService {

    public Product getProductWithFallbackChain(String id) {
        // Level 1: Primary cache
        // Level 2: Primary service with CB
        // Level 3: Secondary service with CB
        // Level 4: Stale cache
        // Level 5: Default response

        return Try.ofSupplier(
                Decorators.ofSupplier(() -> primaryService.getProduct(id))
                    .withCircuitBreaker(primaryCB)
                    .withRetry(primaryRetry)
                    .withTimeLimiter(primaryTL)
                    .decorate())
            .recover(t1 -> Try.ofSupplier(
                    Decorators.ofSupplier(() -> secondaryService.getProduct(id))
                        .withCircuitBreaker(secondaryCB)
                        .decorate())
                .recover(t2 -> Try.ofSupplier(
                        () -> staleCache.get(id))
                    .recover(t3 -> new Product(id, "Default Product", BigDecimal.ZERO))
                    .get())
                .get())
            .get();
    }
}
```

### 7. Bulkhead with Custom ThreadPool

```java
@Configuration
public class BulkheadConfig {

    @Bean
    public ThreadPoolBulkhead orderBulkhead() {
        return ThreadPoolBulkheadConfig.custom()
            .maxThreadPoolSize(5)
            .coreThreadPoolSize(3)
            .queueCapacity(10)
            .keepAliveDuration(Duration.ofSeconds(30))
            .writableStackTraceEnabled(false)
            .build()
            .threadPoolBulkheadRegistry()
            .bulkhead("orderService");
    }

    @Bean(name = "paymentExecutor")
    public ExecutorService paymentExecutor() {
        return Executors.newThreadPerTaskExecutor(
            Thread.ofVirtual()
                .name("payment-vt-", 0)
                .factory());
    }
}

@Service
public class BulkheadService {

    private final ThreadPoolBulkhead bulkhead;
    private final ExecutorService executor;

    public CompletableFuture<Order> processOrder(OrderRequest request) {
        return CompletableFuture.supplyAsync(
            () -> bulkhead.executeSupplier(() -> orderService.process(request)),
            executor
        );
    }
}
```

---

## 15+ Interview Questions

### Basic

1. **What problem does the circuit breaker pattern solve?** — Prevents cascading failures in distributed systems. Stops calling a failing service, allowing it time to recover. Provides graceful degradation via fallbacks.

2. **Explain the three states of a circuit breaker.** — Closed: normal operation, requests pass through. Open: requests fail fast without calling the service. Half-Open: limited probe requests to test recovery.

3. **What is the bulkhead pattern?** — Isolates resources (threads, connections) into pools so failure in one pool doesn't exhaust resources for other pools. Like ship bulkheads preventing flooding the entire ship.

### Intermediate

4. **How does Resilience4j determine when to open a circuit breaker?** — Sliding window (count or time-based) tracks outcomes. When failure rate exceeds threshold within the window, circuit opens. Minimum call count required before evaluating.

5. **Explain the difference between semaphore and thread pool bulkhead.** — Semaphore: limits concurrent calls, same thread executes. ThreadPool: separate thread pool, queues excess requests, provides isolation of thread context.

6. **How does the retry pattern interact with circuit breaker?** — Retry executes before circuit breaker evaluates. Multiple failures from retries contribute to failure count. Order: TimeLimiter → Retry → CircuitBreaker → Bulkhead.

7. **What is the token bucket algorithm for rate limiting?** — Tokens added at fixed rate. Each request consumes tokens. Bucket capacity allows bursts. When bucket empty, requests wait or fail. Allows controlled bursting.

8. **How do you implement a fallback method?** — In annotations: `fallbackMethod = "methodName"`. Fallback receives the same parameters + `Throwable`. Can return default value, cached data, or degraded response.

### Advanced

9. **Design a resilience strategy for a payment gateway integration.** — CircuitBreaker (low threshold, payment failures are critical), Retry (3x with backoff, only on 5xx/429), Bulkhead (dedicated pool, don't block other operations), TimeLimiter (2s timeout), Fallback (queue for manual processing).

10. **How do you observe circuit breaker state in production?** — Micrometer metrics: `resilience4j.circuitbreaker.state`, `resilience4j.circuitbreaker.calls`. Event publisher: onStateTransition, onFailureRateExceeded. Health indicator: expose state via /actuator/health.

11. **Explain how to test a circuit breaker in integration tests.** — Wiremock to simulate failures. Call service until threshold exceeded. Assert circuit opens. Verify fallback called. Wait for waitDuration. Verify half-open probes. Test recovery.

12. **How does Resilience4j compare to Hystrix for performance?** — Resilience4j ~10µs overhead (no thread pool). Hystrix ~100µs (thread-per-command). Resilience4j is modular, Hystrix is monolithic. Resilience4j has more features (rate limiter, retry, cache).

13. **Design a rate limiting strategy for a multi-tenant API.** — Per-tenant key resolver. Token bucket per tenant. Different limits per tier (free: 10/min, pro: 1000/min). Redis backend for distributed rate limiting across instances.

14. **How do you implement a cache-aside pattern with circuit breaker?** — Check cache first. On miss, call service via circuit breaker. On success, populate cache. On circuit open, try stale cache. Circuit breaker protects the cache population path.

15. **What happens when multiple circuit breakers are nested (e.g., gateway → service → database)?** — Each level has its own guard. Gateway CB protects from slow services. Service CB protects from DB failures. DB connection pool acts as bulkhead. Cascading opens should be graceful.

16. **How do you handle half-open state probe requests?** — Limited number of requests (e.g., 3). If all succeed, close circuit. If any fails, reopen. Probe requests should be real user requests, not synthetic. Consider isolating probes to critical paths.

17. **Explain the Decorators API in Resilience4j.** — Builder pattern: `Decorators.ofSupplier(supplier).withCircuitBreaker().withRetry().withBulkhead().decorate()`. Returns a `Supplier` that chains all resilience mechanisms. Supports checked exceptions via `Try.ofSupplier()`.

18. **How do you configure Resilience4j in a Spring Boot application?** — `resilience4j.circuitbreaker.instances.serviceName.*` in YAML. `@CircuitBreaker`, `@Retry`, `@Bulkhead`, `@RateLimiter`, `@TimeLimiter` annotations. Aspect-oriented, integrates with Spring's expression language.