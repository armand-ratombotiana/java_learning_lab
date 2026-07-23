# System Design Cheatsheet — Java Engineer's Perspective

> **System design from a Java lens: frameworks, concurrency models, JVM tuning, database access, caching, observability, and decision trees**

---

## Table of Contents
1. [Java Tech Stack Choices](#java-tech-stack-choices)
2. [Concurrency Models](#concurrency-models)
3. [JVM Tuning in System Design](#jvm-tuning-in-system-design)
4. [Database Access Patterns](#database-access-patterns)
5. [Caching Patterns](#caching-patterns)
6. [Reactive Systems](#reactive-systems)
7. [Microservices](#microservices)
8. [Messaging](#messaging)
9. [Observability](#observability)
10. [Cloud-Native Java](#cloud-native-java)
11. [System Design Response Template](#system-design-response-template)

---

## Java Tech Stack Choices

### Framework Decision Matrix

| Requirement | Spring Boot | Quarkus | Micronaut | Helidon | Vert.x |
|-------------|-------------|---------|-----------|---------|--------|
| Startup time | 3-8 sec | <1 sec (JVM) / <0.1s (native) | <1 sec | <1 sec | <1 sec |
| Memory footprint | ~100MB+ | ~10-30MB | ~20-40MB | ~20-30MB | ~15-30MB |
| Native image (GraalVM) | Supported (Spring 6+) | First-class | First-class | Supported | Plugin |
| Reactive support | WebFlux | Mutiny | Reactive HTTP | SE reactive | Native |
| Virtual threads | Since Spring Boot 3.2 | Since 3.0 | Since 4.0 | Since 4.0 | Experimental |
| Learning curve | Medium | Low | Low | Medium | Medium |
| Kubernetes native | Via Spring Cloud | Yes | Yes | Yes (SE) | Partial |
| Market adoption | Very High | Growing | Growing | Niche | Moderate |
| Best for | Enterprise, large teams | Serverless, cloud-native | Cloud-native, microservices | Lightweight SE | High-throughput IO |

### Decision Tree: Which Stack to Propose?

```
System Requirements:
├── Startup < 1s, memory < 64MB, serverless?
│   ├── Use Quarkus or Micronaut (with GraalVM native)
│   └── Use Helidon SE for minimal footprint
├── Heavy concurrency, high throughput, low latency?
│   ├── Use Vert.x (event-loop, non-blocking IO)
│   └── Use Spring WebFlux + Netty
├── Enterprise, large team, many integrations?
│   └── Use Spring Boot (huge ecosystem, hiring pool)
├── Real-time streaming, WebSockets?
│   ├── Use Vert.x for raw performance
│   └── Use Spring WebFlux for ecosystem
└── REST API with moderate load, team familiar with Spring?
    └── Use Spring Boot (virtual threads in SB 3.2+)
```

### When to Mention in Interviews

> *"For this system, I'd propose Spring Boot with WebFlux for reactive endpoints, running on a virtual-thread-per-request model under Spring Boot 3.2+. The startup penalty is acceptable given the deployment model (long-running services on EKS). For the ingestion pipeline, I'd use Vert.x to handle 100K+ concurrent connections."*

---

## Concurrency Models

| Model | How Java Implements It | Thread Overhead | Best For |
|-------|----------------------|-----------------|----------|
| Thread-per-request | `ThreadPoolExecutor`, `Executors` | ~1MB stack per thread | CPU-bound workloads |
| Event loop | Netty, Vert.x EventLoop, Reactor's Scheduler | Few threads (usually #CPU) | IO-bound, many connections |
| Virtual threads (Project Loom) | `Thread.ofVirtual()`, `Executors.newVirtualThreadPerTaskExecutor()` | ~few KB each | IO-bound, blocking code |
| Work stealing | `ForkJoinPool`, `parallelStream()` | #CPU threads | CPU-bound parallel processing |

### Impact on System Design

```java
// Traditional thread-per-request — design for pool size limits
ExecutorService pool = Executors.newFixedThreadPool(200);
// → Design implication: tune pool size, handle queuing/rejection

// Virtual threads — design for millions of concurrent tasks
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    executor.submit(() -> handle(request));
}
// → Design implication: no pool tuning needed, but pinning is a concern

// Event loop — design for non-blocking everything
Mono<String> result = webClient.get()
    .uri("/api/data")
    .retrieve()
    .bodyToMono(String.class);
// → Design implication: never block in handler, everything reactive
```

### Interview Tip: Mention Virtual Threads Correctly

> *"For IO-bound services, I'd use virtual threads (Java 21+). They eliminate the thread-per-request scalability ceiling. However, I need to be aware of pinning — synchronized blocks and JNI calls can pin virtual threads to carrier threads. I'd use ReentrantLock instead of synchronized to avoid this."*

---

## JVM Tuning in System Design

### GC Choice Decision Table

| GC Algorithm | Throughput | Latency (P99) | Heap Size | When to Propose |
|--------------|------------|---------------|-----------|-----------------|
| G1GC (default) | Good | ~10-100ms | < 64GB | General purpose, low-latency |
| Parallel GC | Best | ~100ms-1s | < 64GB | Batch processing, throughput-oriented |
| ZGC | Moderate | < 1-10ms (sub-millisecond) | 8MB-16TB | Ultra-low latency, large heaps |
| Shenandoah | Moderate | < 1-10ms | < 512GB | Low latency, moderate heap |
| Epsilon GC | N/A (no GC) | N/A | Small, short-lived | Short-lived microservices, serverless |

### JVM Flags to Mention in Design

```java
// For latency-sensitive trading system (128GB heap, <2ms P99)
-XX:+UseZGC -Xms64G -Xmx64G -XX:ConcGCThreads=8
-XX:SoftMaxHeapSize=48G        // GC triggers earlier
-Xlog:gc*:file=gc.log          // GC logging for analysis

// For throughput-oriented batch processor (32GB heap)
-XX:+UseParallelGC -XX:ParallelGCThreads=16
-XX:+UseStringDeduplication    // reduce string duplication in logs

// For container/memory-constrained (1-4GB heap)
-XX:+UseContainerSupport       // detect cgroup limits
-XX:MaxRAMPercentage=75.0      // use 75% of container memory
-Xss256k                        // reduce thread stack (important with many threads)
```

### Heap Sizing Rules of Thumb

- Rule 1: `-Xmx` should not exceed available RAM — leave 20-30% for OS + off-heap
- Rule 2: Metaspace grows unbounded by default — set `-XX:MaxMetaspaceSize`
- Rule 3: Direct memory (for NIO) is outside heap — account for it: `-XX:MaxDirectMemorySize`
- Rule 4: For ~50% of services, default GC (G1GC) is fine — don't over-optimize
- Rule 5: Always configure `-XX:+HeapDumpOnOutOfMemoryError` in production

---

## Database Access Patterns

| Pattern | Java Implementation | Thread Model | When to Use |
|---------|--------------------|--------------|-------------|
| JDBC (blocking) | `Connection`, `PreparedStatement` | Thread-per-connection | Simple queries, low concurrency |
| JPA / Hibernate | `EntityManager`, `@Entity` | Thread-per-request | CRUD, complex object graphs |
| R2DBC (reactive) | `DatabaseClient`, `Flux<T>` | Event-loop | High concurrency, reactive chain |
| jOOQ | DSL-based type-safe SQL | Thread-per-request | Complex SQL, no ORM overhead |
| Spring Data | Repository abstraction | Depends on underlying | Standard CRUD, quick development |

### Design Implications

> *"For a high-throughput API (10K+ RPS), I'd avoid JPA due to its lazy loading and N+1 query risks. Use jOOQ for complex queries or R2DBC for a fully reactive stack. JPA is fine for admin panels and low-traffic CRUD."*

---

## Caching Patterns

### Java Caching Stack

| Cache Type | Java Library | Eviction | Features |
|------------|-------------|----------|----------|
| Local heap cache | Caffeine | Window-TinyLFU | Fastest, fine-grained TTL, async refresh |
| Local heap cache | Guava Cache | LRU | Legacy, simpler API |
| Local off-heap | MapDB / Chronicle Map | Configurable | Off-heap, persists to disk |
| Distributed | Redis via Jedis/Lettuce | Various | Shared across JVMs, persistence |
| Distributed | Hazelcast | Various | In-memory data grid, distributed |
| JCache (JSR 107) | Caffeine, Hazelcast, etc. | Standard | Portable across providers |

### Caffeine Usage Pattern (Interview-Ready)

```java
Cache<String, Result> cache = Caffeine.newBuilder()
    .maximumSize(10_000)
    .expireAfterWrite(5, TimeUnit.MINUTES)
    .refreshAfterWrite(1, TimeUnit.MINUTES)  // async refresh, never blocks
    .recordStats()                            // hit rate metrics
    .build(key -> loadFromDatabase(key));     // auto-load function

// For high hit rates, prefer local cache (Caffeine) + Redis for invalidation
// For shared state across JVMs, use Redis directly
```

---

## Reactive Systems

### Project Reactor / WebFlux Patterns

```java
// Backpressure strategies — know when to use each
Flux<Data> stream = source.getDataStream();

// Safe for bounded buffers, risky for unbounded
stream.onBackpressureBuffer(1000).subscribe();

// Drop when consumer can't keep up (preferred for real-time)
stream.onBackpressureDrop().subscribe();

// Latest value only (like debounce for speed)
stream.onBackpressureLatest().subscribe();

// Error when consumer can't keep up
stream.onBackpressureError().subscribe();
```

### When to Propose Reactive in Design

| Scenario | Recommend Reactive? | Why |
|----------|-------------------|-----|
| High concurrency (10K+ connections) | Yes | Event loop scales better |
| Simple CRUD API | No | Virtual threads are simpler |
| Complex orchestration (multiple downstream calls) | Maybe | `Mono.zip()` is clean; debugging is hard |
| Team is new to Java | No | Virtual threads + blocking is easier |
| Streaming data / real-time | Yes | Backpressure is built-in |

---

## Microservices

### Java Microservice Components

| Concern | Java Implementation | Notes |
|---------|--------------------|-------|
| Service discovery | Eureka (Spring Cloud), Consul | Eureka is AP (availability + partition tolerance) |
| Circuit breaker | Resilience4j | Replaces Hystrix (Netflix deprecated) |
| API gateway | Spring Cloud Gateway, Zuul | Gateway uses WebFlux; Zuul is blocking |
| Configuration | Spring Cloud Config, Consul | Externalized, refreshable at runtime |
| Load balancing | Spring Cloud LoadBalancer | Replaces Ribbon (Netflix deprecated) |
| Retry | Resilience4j Retry | Exponential backoff, jitter |
| Tracing | Micrometer Tracing + Zipkin | Distributed trace propagation |

### Resilience4j Patterns

```java
// Circuit breaker — protect downstream services
CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("userService");
Supplier<String> decorated = CircuitBreaker.decorateSupplier(circuitBreaker, 
    () -> restTemplate.getForObject(url, String.class));

// Retry with exponential backoff
Retry retry = Retry.custom()
    .maxAttempts(3)
    .waitDuration(Duration.ofMillis(500))
    .retryOnResult(result -> result == null)
    .build();

// Rate limiter — control request rate
RateLimiter rateLimiter = RateLimiter.custom()
    .limitForPeriod(100)
    .limitRefreshPeriod(Duration.ofSeconds(1))
    .build();
```

---

## Messaging

### Java Messaging Comparison

| Technology | Java Client | Consumer Model | Delivery | Best For |
|------------|-------------|----------------|----------|----------|
| Kafka | Kafka Client, Spring Kafka | Poll-based, consumer groups | At-least-once, exactly-once | Event streaming, log aggregation |
| RabbitMQ | RabbitMQ Java Client | Push-based, AMQP | At-most-once, at-least-once | Task queues, RPC |
| JMS | Jakarta JMS | Connection-based | Transactional | Legacy enterprise integration |
| Pulsar | Pulsar Client | Consumer + Reader | Exactly-once geo-replicated | Multi-region, high-throughput |
| AWS SQS | AWS SDK Java | Poll-based (long polling) | At-least-once | Simple queuing, AWS-native |

### Java Integration Pattern

```java
// Kafka consumer with manual offset management
@KafkaListener(topics = "orders", groupId = "order-processor")
public void onOrder(ConsumerRecord<String, Order> record, 
                     Acknowledgment acknowledgment) {
    try {
        processOrder(record.value());
        acknowledgment.acknowledge();  // manual offset commit
    } catch (Exception e) {
        // log and retry — don't ack, will reprocess
    }
}

// RabbitMQ with RPC pattern
AmqpTemplate template = rabbitTemplate;
Message response = template.sendAndReceive(routingKey, message);
```

---

## Observability

### Java Observability Stack

| Concern | Java Implementation | Metrics Export |
|---------|--------------------|----------------|
| Metrics | Micrometer (MeterRegistry) | Prometheus, Graphite, Datadog |
| Tracing | Micrometer Tracing / OpenTelemetry | Zipkin, Jaeger, Cloud Trace |
| Logging | Logback / Log4j 2 + MDC | JSON format, structured logging |
| Profiling | JFR (JDK Flight Recorder) | JDK Mission Control, async-profiler |
| Health checks | Spring Actuator / MicroProfile Health | `/health`, `/ready`, `/live` |

### Interview-Ready Observability Template

```java
// Micrometer — use it in every system design
MeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

// Timer for latency tracking
Timer.Sample sample = Timer.start(registry);
try {
    handleRequest();
} finally {
    sample.stop(Timer.builder("request.latency")
        .tag("endpoint", path)
        .register(registry));
}

// Counter for error tracking
Counter.builder("request.errors")
    .tag("endpoint", path)
    .register(registry)
    .increment();
```

---

## Cloud-Native Java

### GraalVM Native Images

| Aspect | JVM | Native Image |
|--------|-----|--------------|
| Startup | 3-8 seconds | 0.01-0.3 seconds |
| Memory | 100-300MB | 10-50MB |
| Peak performance | Immediate (JIT warmup) | Instant (AOT compiled) |
| Reflection | Full support | Requires configuration |
| Proxies | Dynamic | Build-time proxy definitions |
| Serialization | JVM handles | Requires serialization config |

### Kubernetes-Aware Java Patterns

```java
// Graceful shutdown — handle SIGTERM
@PreDestroy
public void shutdown() {
    executor.shutdown();
    try { executor.awaitTermination(30, TimeUnit.SECONDS); } 
    catch (InterruptedException e) { Thread.currentThread().interrupt(); }
}

// Health probes via Spring Actuator
// /actuator/health/liveness — JVM alive check
// /actuator/health/readiness — service ready to serve traffic

// Pod disruption budgets: at least 2 replicas always running
// Resource requests/limits: set -Xmx to 70% of container memory limit
```

---

## System Design Response Template

### "Design X in Java" — Reusable Structure

```
1. REQUIREMENTS CLARIFICATION
   - Functional: what does the system do?
   - Non-functional: latency, throughput, consistency, durability
   - Scale: QPS, data volume, read/write ratio

2. DATA MODEL (Java-First)
   - Define records/POJOs for domain objects
   - `record User(String id, String name, Instant createdAt)`
   - Serialization format: JSON (Jackson/JSON-B) vs Protobuf vs Avro

3. API DESIGN
   - REST endpoints (Spring Boot annotations) or gRPC services
   - `@RestController`, `@PostMapping`, `@Validated`
   - Request/response DTOs as Java records

4. STORAGE LAYER
   - Database choice + Java client (JDBC, R2DBC, JPA, jOOQ)
   - Connection pooling: HikariCP (tune maxPoolSize = 10-50 per instance)
   - Migration tool: Flyway / Liquibase

5. COMPUTE & CONCURRENCY
   - Execution model: virtual threads (Java 21+) vs reactive (WebFlux/Vert.x)
   - Thread pool sizing: N_threads = N_CPU * (1 + W/C) for traditional
   - Async orchestration: CompletableFuture / Mono / Flux

6. CACHING
   - Local: Caffeine (Window-TinyLFU, max entries, TTL)
   - Distributed: Redis (Jedis/Lettuce/Redisson) or Hazelcast
   - Cache-aside: `cache.computeIfAbsent(key, db::load)`

7. RESILIENCE
   - Circuit breaker: Resilience4j
   - Retry: Exponential backoff + jitter
   - Bulkhead: Thread pool isolation per downstream dependency

8. OBSERVABILITY
   - Metrics: Micrometer + Prometheus
   - Tracing: OpenTelemetry / Jaeger
   - Logging: Structured JSON (Logback), MDC for trace IDs
   - Profiling: JFR enabled in production, async-profiler for incidents

9. JVM CONFIGURATION
   - GC: G1GC default, ZGC for <10ms latency, Parallel for batch
   - Heap: -Xmx = 70% of container limit
   - Flags: -XX:+UseContainerSupport, -XX:MaxRAMPercentage=75

10. DEPLOYMENT
    - Container: Docker multi-stage (distroless base for native)
    - Orchestration: Kubernetes with liveness/readiness probes
    - CI/CD: Maven/Gradle + SpotBugs + JUnit + ArchUnit
```

> *"For this design, I'd use Spring Boot 3.2+ with virtual threads for the web layer, Caffeine for local caching, and Redis for distributed state. The database layer uses R2DBC for reactive access with connection pooling via HikariCP. Resilience is handled by Resilience4j circuit breakers. JVM tuning focuses on ZGC for sub-10ms latencies with a 16GB heap. Observability uses Micrometer + Prometheus for metrics and OpenTelemetry for distributed tracing."*
