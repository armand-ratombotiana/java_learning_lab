# System Design → Java Concepts Mapping

## How Java Features Map to System Design Patterns

| System Design Problem | Java Solution | Key Classes/APIs | Notes |
|----------------------|---------------|------------------|-------|
| Handling concurrent requests at scale | Thread Pool | `ThreadPoolExecutor`, `Executors` | Tune core/max pool, queue type, rejection policy |
| Lightweight concurrency for IO | Virtual Threads | `Thread.ofVirtual()`, `Executors.newVirtualThreadPerTaskExecutor()` | Java 21+, millions of threads, backed by FJP |
| Async service orchestration | CompletableFuture | `CompletableFuture.supplyAsync()`, `thenApply()`, `thenCompose()`, `allOf()` | Non-blocking, error recovery with `exceptionally()` |
| Backpressure in distributed systems | Reactive Streams | `Publisher`, `Subscriber`, `Subscription`, `Flux`, `Mono` | Project Reactor, RxJava |
| Immutable data transfer | Records | `record`, `with` (withers via builder) | Compact, value-based, equals/hashCode auto |
| In-memory data store / cache | ConcurrentHashMap | `ConcurrentHashMap`, `computeIfAbsent()`, `merge()` | Lock striping, CAS-based operations |
| Batch processing pipeline | Streams | `Stream`, `parallelStream()`, `Collectors.groupingBy()` | Lazy, functional, can parallelize |
| Inter-service communication | HTTP Clients / gRPC | `HttpClient` (Java 11+), `Netty`, gRPC-Java | HTTP/2, reactive, non-blocking |
| Persistent storage / WAL | NIO / Files / MappedByteBuffer | `FileChannel`, `MappedByteBuffer`, `RandomAccessFile` | Memory-mapped files for fast persistence |
| Rate limiting | Semaphore / Token Bucket | `Semaphore`, custom using `ScheduledExecutorService` | Control access to limited resources |
| Circuit breaker | Resilience4j | `CircuitBreaker`, `RateLimiter`, `TimeLimiter` | Fault tolerance in distributed calls |
| Event-driven architecture | Reactive Streams / Event Bus | `Flux`, `Sinks` (Project Reactor), LMAX Disruptor | Event sourcing, CQRS |
| Distributed caching | JCache (JSR 107) | `javax.cache.Cache`, Caffeine, Redis via Jedis | JCache standard, Caffeine for local |
| Service discovery | Consul / Eureka clients | HTTP polling, DNS SRV | Java clients for registry lookup |
| Configuration management | Spring Cloud Config / MicroProfile Config | `@ConfigurationProperties`, `@Inject Config` | Externalized config |
| Serialization / Marshalling | Protobuf / JSON / Kryo | `protobuf-java`, `Jackson`, `Kryo` | Payload efficiency, schema evolution |
| Object pooling | GenericObjectPool | Apache Commons Pool2 | Database connections, expensive objects |
| Coordination / Leader election | ZooKeeper / Curator | `CuratorFramework`, `LeaderLatch` | Distributed locks, service registry |
| Metrics / Observability | Micrometer / JFR | `MeterRegistry`, `jdk.jfr.Event` | Prometheus, Grafana, profiling |

---

## Deep Dive: Thread Pools for Concurrent Request Handling

```java
// Production-grade thread pool for web server
var executor = new ThreadPoolExecutor(
    50,                        // corePoolSize
    200,                       // maxPoolSize
    60, TimeUnit.SECONDS,      // keepAliveTime
    new LinkedBlockingQueue<>(1000),  // workQueue
    new ThreadPoolExecutor.CallerRunsPolicy()  // rejection policy
);

// Virtual threads (Java 21+) — lighter alternative
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    executor.submit(() -> handleRequest(request));
}
```

**Key decisions:**
- **Bounded vs unbounded queue:** Unbounded can cause OOM under load.
- **Rejection policy:** `AbortPolicy` (throws), `CallerRunsPolicy` (throttles), `DiscardPolicy` (silent).
- **Monitoring:** `getActiveCount()`, `getCompletedTaskCount()`, `getLargestPoolSize()`.

---

## Deep Dive: Virtual Threads for IO-Bound Work

Before Java 21:
```java
// Each connection needs an OS thread — limited to ~10k on typical machines
try (var server = new ServerSocket(8080)) {
    while (true) {
        Socket s = server.accept();
        new Thread(() -> handle(s)).start();  // Limited!
    }
}
```

With virtual threads (Java 21+):
```java
try (var server = new ServerSocket(8080);
     var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    while (true) {
        Socket s = server.accept();
        executor.submit(() -> handle(s));  // Millions!
    }
}
```

**Trade-offs:**
- Virtual threads are NOT faster for CPU-bound work.
- Pinning: `synchronized` block or native call pins the carrier thread.
- Use `ReentrantLock` instead of `synchronized` in virtual threads.

---

## Deep Dive: Reactive Streams for Backpressure

```java
// Publisher: produces data
Flux<Integer> publisher = Flux.range(1, 100_000_000);

// Subscriber with backpressure
publisher
    .buffer(1000)                    // Batch into chunks
    .onBackpressureBuffer(10000)     // Buffer up to 10k items
    .subscribe(batch -> processBatch(batch));

// Connection to distributed system (Kafka-like)
Flux<Event> events = Flux.from(kafkaReceiver.receive())
    .flatMap(msg -> process(msg)
        .thenReturn(msg)
        .doOnSuccess(msg -> msg.receiverOffset().acknowledge()))
    .onBackpressureLatest();
```

**Backpressure strategies:**
- `BUFFER` — buffer items (risk OOM)
- `DROP` — drop new items if downstream can't keep up
- `LATEST` — keep only the latest, drop older
- `ERROR` — throw `OverflowException`

---

## Deep Dive: Records for Microservices DTOs

```java
// Before records: boilerplate DTO
public class OrderRequest {
    private final String orderId;
    private final String customerId;
    private final List<OrderItem> items;
    // constructor, getters, equals, hashCode, toString — 50+ lines
}

// With records: 1 line
public record OrderRequest(String orderId, String customerId, List<OrderItem> items) {}

// With validation via compact constructor
public record ValidatedOrder(String orderId, String customerId) {
    public ValidatedOrder {
        Objects.requireNonNull(orderId);
        Objects.requireNonNull(customerId);
        if (orderId.isBlank()) throw new IllegalArgumentException("Empty orderId");
    }
}
```

---

## Deep Dive: CompletableFuture for Async Orchestration

```java
CompletableFuture<User> userFuture = CompletableFuture.supplyAsync(() -> fetchUser(id));
CompletableFuture<Account> accountFuture = CompletableFuture.supplyAsync(() -> fetchAccount(id));

// Wait for all parallel tasks
CompletableFuture.allOf(userFuture, accountFuture).join();

// Combine results
User user = userFuture.get();
Account account = accountFuture.get();

// Chain with error recovery (using thenApply instead of get)
CompletableFuture<Response> response = userFuture
    .thenCombine(accountFuture, (u, a) -> buildResponse(u, a))
    .exceptionally(ex -> {
        log.error("Failed to build response", ex);
        return fallbackResponse();
    })
    .orTimeout(5, TimeUnit.SECONDS);
```

---

## Deep Dive: Collections as In-Memory Data Stores

### LRU Cache with LinkedHashMap
```java
class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int maxCapacity;
    
    LRUCache(int max) {
        super(max, 0.75f, true);  // access-order = true
        this.maxCapacity = max;
    }
    
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxCapacity;
    }
}
```

### Concurrent Multi-Level Cache
```java
class TwoLevelCache<K, V> {
    private final Cache<K, V> local = Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .build();
    
    private final ConcurrentHashMap<K, V> remote = new ConcurrentHashMap<>();
    
    V get(K key) {
        V val = local.getIfPresent(key);
        if (val != null) return val;
        val = remote.get(key);
        if (val != null) local.put(key, val);
        return val;
    }
}
```

---

## Deep Dive: Streams for Batch Processing

```java
// ETL pipeline with streams
record Transaction(String id, double amount, String currency) {}

List<ReportRow> report = transactions.stream()
    .filter(t -> "USD".equals(t.currency()))
    .filter(t -> t.amount() > 0)
    .map(t -> new ReportRow(t.id(), t.amount() * exchangeRate(t.currency())))
    .sorted(Comparator.comparing(ReportRow::amount).reversed())
    .limit(100)
    .collect(Collectors.toList());

// Parallel for CPU-intensive transforms
List<EnrichedRow> result = data.parallelStream()
    .map(this::expensiveEnrichment)  // CPU bound
    .collect(Collectors.toUnmodifiableList());

// Grouping for analytics
Map<String, DoubleSummaryStatistics> stats = transactions.stream()
    .collect(Collectors.groupingBy(
        Transaction::currency,
        Collectors.summarizingDouble(Transaction::amount)
    ));
```

---

*Last updated: July 2026. For interview-specific guidance, see `CRACKING_THE_INTERVIEW_GUIDE.md`.*