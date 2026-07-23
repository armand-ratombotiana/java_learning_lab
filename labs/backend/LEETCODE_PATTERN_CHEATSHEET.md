# Backend Academy — LeetCode Pattern Cheatsheet

<div align="center">

![LeetCode](https://img.shields.io/badge/LeetCode-FFA116?style=for-the-badge&logo=leetcode&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Backend Patterns](https://img.shields.io/badge/Backend_Patterns-6DB33F?style=for-the-badge)

**Backend-relevant LeetCode patterns with Java templates, problem references, and company frequency**

</div>

---

## Table of Contents

1. [Concurrency Patterns](#1-concurrency-patterns)
2. [Database/API Patterns](#2-databaseapi-patterns)
3. [Design Patterns (Backend Context)](#3-design-patterns-backend-context)
4. [Data Processing Patterns](#4-data-processing-patterns)
5. [System Design Problems on LeetCode](#5-system-design-problems-on-leetcode)
6. [Java-Specific Backend Patterns](#6-java-specific-backend-patterns)

---

## 1. Concurrency Patterns

### 1.1 Producer-Consumer

**Backend Context:** Message queue consumers, batch processors, async loggers

**Java Code Template:**
```java
class BoundedBuffer<T> {
    private final Queue<T> queue = new LinkedList<>();
    private final int capacity;
    private final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public BoundedBuffer(int capacity) { this.capacity = capacity; }

    public void produce(T item) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == capacity) notFull.await();
            queue.offer(item);
            notEmpty.signal();
        } finally { lock.unlock(); }
    }

    public T consume() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) notEmpty.await();
            T item = queue.poll();
            notFull.signal();
            return item;
        } finally { lock.unlock(); }
    }
}
```

**LeetCode Problems:** 1188 (Design Bounded Blocking Queue)
**Company Frequency:** Google (high), Amazon (high), Microsoft (medium)
**Backend Lab:** Lab 07 (Messaging — Kafka consumer groups, RabbitMQ queues)

### 1.2 Reader-Writer (ReadWriteLock)

**Backend Context:** Cache implementations, configuration stores, shared state

**Java Code Template:**
```java
class ReadWriteDataStore<K, V> {
    private final Map<K, V> store = new HashMap<>();
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public V read(K key) {
        rwLock.readLock().lock();
        try { return store.get(key); }
        finally { rwLock.readLock().unlock(); }
    }

    public void write(K key, V value) {
        rwLock.writeLock().lock();
        try { store.put(key, value); }
        finally { rwLock.writeLock().unlock(); }
    }
}
```

**LeetCode Problems:** 1242 (Web Crawler Multithreaded)
**Company Frequency:** Google (medium), Uber (high), Netflix (medium)
**Backend Lab:** Lab 13 (Caching — Redis read replicas vs write master)

### 1.3 Dining Philosophers (Deadlock Prevention)

**Backend Context:** Distributed lock management, resource ordering

**Java Code Template:**
```java
class DiningPhilosophers {
    private final Object[] forks = new Object[5];
    public DiningPhilosophers() {
        for (int i = 0; i < 5; i++) forks[i] = new Object();
    }

    public void philosopher(int id, Runnable eat) throws InterruptedException {
        int left = id, right = (id + 1) % 5;
        // Always pick up lower-numbered fork first to prevent deadlock
        Object first = forks[Math.min(left, right)];
        Object second = forks[Math.max(left, right)];
        synchronized (first) { synchronized (second) { eat.run(); } }
    }
}
```

**LeetCode Problems:** 1226 (Dining Philosophers)
**Company Frequency:** Google (high), Microsoft (medium)
**Backend Lab:** Lab 24 (Backend Performance — deadlock detection in distributed systems)

### 1.4 Thread Pool Patterns

**Backend Context:** Task execution, parallel processing, async operations

**Java Code Template:**
```java
class BackendThreadPool {
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
        4,                          // corePoolSize
        16,                         // maxPoolSize
        60, TimeUnit.SECONDS,       // keepAliveTime
        new LinkedBlockingQueue<>(100),  // workQueue
        new ThreadPoolExecutor.CallerRunsPolicy()  // rejection
    );

    public <T> CompletableFuture<T> submit(Callable<T> task) {
        return CompletableFuture.supplyAsync(() -> {
            try { return task.call(); } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor);
    }
}
```

**LeetCode Problems:** 1114 (Print in Order), 1115 (Print FooBar Alternately), 1116 (Print Zero Even Odd)
**Company Frequency:** Meta (medium), Amazon (high), Google (medium)
**Backend Lab:** Lab 15 (WebFlux/Reactive — async processing, Schedulers)

### 1.5 CompletableFuture Patterns

**Backend Context:** Reactive microservices, async API orchestration

**Java Code Template:**
```java
class AsyncApiOrchestrator {
    public CompletableFuture<OrderResponse> processOrder(OrderRequest req) {
        return validateInventory(req.inventoryId())
            .thenCompose(available -> {
                if (!available) return CompletableFuture.failedFuture(...);
                return chargePayment(req.paymentId());
            })
            .thenCompose(payment -> scheduleShipping(req.address()))
            .thenApply(shipment -> new OrderResponse(shipment.trackingId()))
            .exceptionally(ex -> new OrderResponse("FAILED", ex.getMessage()))
            .orTimeout(5, TimeUnit.SECONDS);
    }
}
```

**LeetCode Problems:** 1654 (Minimum Jumps, for async style), 1655 (Distribute Repeating Integers)
**Company Frequency:** Netflix (high), Uber (high), Stripe (medium)
**Backend Lab:** Lab 15 (WebFlux/Reactive — Mono/Flux, asynchronous orchestration)

---

## 2. Database/API Patterns

### 2.1 SQL Problem Patterns

**Backend Context:** JPA repository queries, data aggregation, pagination

**Key SQL patterns for backend interviews:**
| Pattern | SQL | LeetCode Problem | Company Freq |
|---------|-----|-----------------|--------------|
| Top N per group | `ROW_NUMBER() OVER (PARTITION BY ...)` | 185 (Department Top 3 Salaries) | Amazon, Google |
| Cumulative sum | `SUM() OVER (ORDER BY ...)` | 534 (Game Play Analysis III) | Meta, Amazon |
| Date range joins | `BETWEEN` or `JOIN ON` date intervals | 180 (Consecutive Numbers) | Microsoft |
| Self-joins | `JOIN table AS t2 ON ...` | 181 (Employees Earning More) | Amazon |
| Window functions | `LAG()`, `LEAD()`, `RANK()` | 177 (Nth Highest Salary) | Google |
| Pivoting | Conditional aggregation with `CASE` | 618 (Students Report By Geography) | Stripe |

**Java code template for SQL in Spring Boot:**
```java
@Query("""
    SELECT d.name AS department, e.name AS employee, e.salary
    FROM Employee e JOIN e.department d
    WHERE (SELECT COUNT(DISTINCT e2.salary)
           FROM Employee e2
           WHERE e2.department.id = e.department.id
             AND e2.salary > e.salary) < 3
    ORDER BY d.name, e.salary DESC
""")
List<Object[]> findTopThreeSalariesPerDepartment();
```

**Backend Lab:** Lab 04 (Spring Data JPA — queries, pagination, projections)

### 2.2 API Design Patterns

**Backend Context:** RESTful API design, pagination, rate limiting

**Java Code Template — Custom Pagination:**
```java
record PaginatedResponse<T>(List<T> data, int page, int size, long total, int totalPages) {
    public static <T> PaginatedResponse<T> from(Page<T> page) {
        return new PaginatedResponse<>(
            page.getContent(), page.getNumber(), page.getSize(),
            page.getTotalElements(), page.getTotalPages()
        );
    }
}
```

**LeetCode Problems:**
| Problem | Backend Pattern | Company |
|---------|----------------|---------|
| 160 (Intersection of Two Linked Lists) | Cursor-based pagination | Any |
| 146 (LRU Cache) | API caching layer | Google, Amazon |
| 355 (Design Twitter) | Timeline API design | Meta, Google |
| 380 (Insert Delete GetRandom O(1)) | In-memory data API | Google |
| 981 (Time Based Key-Value Store) | Versioned API design | DoorDash, Google |

**Backend Lab:** Lab 02 (REST APIs — pagination, HATEOAS), Lab 17 (API Versioning)

### 2.3 Caching Problem Patterns

**Backend Context:** Redis-like caching, TTL, eviction policies

**Java Code Template — LRU Cache:**
```java
class LRUCache {
    private final int capacity;
    private final LinkedHashMap<Integer, Integer> cache;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new LinkedHashMap<>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                return size() > capacity;
            }
        };
    }

    public int get(int key) { return cache.getOrDefault(key, -1); }
    public void put(int key, int value) { cache.put(key, value); }
}
```

**LeetCode Problems:** 146 (LRU Cache), 460 (LFU Cache), 1429 (First Unique Number)
**Company Frequency:** Google (very high), Amazon (high), Microsoft (high), Meta (high)
**Backend Lab:** Lab 13 (Caching — Redis cache-aside, write-through, eviction policies)

---

## 3. Design Patterns (Backend Context)

### 3.1 Singleton (Bean Management)

**LeetCode:** 251 (Flatten 2D Vector) — single iterator instance
**Company:** Google (medium), Amazon (medium)

**Backend Context:**
```java
@Component
public class CacheManager { // Singleton by default in Spring
    private final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T get(String key) { return (T) cache.get(key); }
    public void put(String key, Object value) { cache.put(key, value); }
}
```

**Backend Lab:** Lab 01 (Spring Boot Basics — bean scopes, singleton)

### 3.2 Factory Pattern (Bean Creation)

**LeetCode:** 895 (Maximum Frequency Stack) — multi-level data structure factory
**Company:** Uber (high), DoorDash (medium)

**Backend Context:**
```java
@Component
public class NotificationFactory {
    private final Map<NotificationType, NotificationSender> senders;

    public NotificationFactory(List<NotificationSender> senderList) {
        this.senders = senderList.stream()
            .collect(Collectors.toMap(NotificationSender::type, Function.identity()));
    }

    public NotificationSender getSender(NotificationType type) {
        return senders.get(type);
    }
}
```

**Backend Lab:** Lab 06 (Security — filter chain factory), Lab 07 (Messaging — connection factories)

### 3.3 Strategy Pattern (Dynamic Algorithms)

**LeetCode:** 588 (Design In-Memory File System) — strategy-per-operation
**Company:** Google (medium), Meta (medium)

**Backend Context:**
```java
@Component
public class PricingStrategyProvider {
    private final Map<String, PricingStrategy> strategies;

    public PricingStrategyProvider(List<PricingStrategy> strategyList) {
        this.strategies = strategyList.stream()
            .collect(Collectors.toMap(PricingStrategy::region, Function.identity()));
    }

    public PricingStrategy forRegion(String region) {
        return strategies.getOrDefault(region, new DefaultPricing());
    }
}
```

**Backend Lab:** Lab 03 (Spring MVC — strategy pattern for request handling)

### 3.4 Observer Pattern (Event-Driven)

**LeetCode:** 1472 (Design Browser History) — observable state
**Company:** Google (medium), Uber (high)

**Backend Context:**
```java
@Component
public class OrderEventPublisher {
    private final ApplicationEventPublisher publisher;

    public void orderPlaced(Order order) {
        publisher.publishEvent(new OrderPlacedEvent(this, order));
    }
}

@Component
public class InventoryUpdater {
    @EventListener
    public void handleOrderPlaced(OrderPlacedEvent event) {
        // Update inventory
    }
}
```

**Backend Lab:** Lab 07 (Messaging — pub/sub, Kafka event-driven)

---

## 4. Data Processing Patterns

### 4.1 File Processing

**Java Code Template — CSV Processor:**
```java
public class CsvBatchProcessor {
    public void processLargeFile(Path file, int batchSize) {
        try (Stream<String> lines = Files.lines(file)) {
            Iterator<String> iterator = lines.iterator();
            List<String> batch = new ArrayList<>(batchSize);
            while (iterator.hasNext()) {
                batch.add(iterator.next());
                if (batch.size() == batchSize) {
                    processBatch(List.copyOf(batch));
                    batch.clear();
                }
            }
            if (!batch.isEmpty()) processBatch(batch);
        }
    }
}
```

**LeetCode Problems:** 186 (Reverse Words in a String II), 71 (Simplify Path), 388 (Longest Absolute File Path)
**Company Frequency:** Google (medium), Microsoft (medium), Amazon (low)
**Backend Lab:** Lab 18 (File/Batch Processing — Spring Batch, chunk-oriented processing)

### 4.2 Streaming Data Processing

**Java Code Template — Sliding Window:**
```java
class SlidingWindowCounter {
    private final TreeMap<Long, Integer> timestamps = new TreeMap<>();
    private final long windowMs;

    public SlidingWindowCounter(long windowMs) { this.windowMs = windowMs; }

    public synchronized void increment() {
        timestamps.merge(System.currentTimeMillis(), 1, Integer::sum);
        evictOld();
    }

    public synchronized int count() {
        evictOld();
        return timestamps.values().stream().mapToInt(Integer::intValue).sum();
    }

    private void evictOld() {
        long cutoff = System.currentTimeMillis() - windowMs;
        timestamps.headMap(cutoff, false).clear();
    }
}
```

**LeetCode Problems:** 480 (Sliding Window Median), 239 (Sliding Window Maximum), 295 (Find Median from Data Stream)
**Company Frequency:** Amazon (high), Google (high), Meta (high)
**Backend Lab:** Lab 24 (Backend Performance — real-time stream processing)

### 4.3 Batch Processing

**Java Code Template — Chunk Processor:**
```java
class ChunkProcessor<T> {
    public void process(List<T> allItems, int chunkSize, Consumer<List<T>> processor) {
        for (int i = 0; i < allItems.size(); i += chunkSize) {
            int end = Math.min(i + chunkSize, allItems.size());
            processor.accept(allItems.subList(i, end));
        }
    }
}
```

**LeetCode Problems:** 56 (Merge Intervals), 57 (Insert Interval), 986 (Interval List Intersections)
**Company Frequency:** Uber (medium), Google (medium), Amazon (high)
**Backend Lab:** Lab 18 (File/Batch Processing — Spring Batch chunk-oriented steps)

---

## 5. System Design Problems on LeetCode

### 5.1 LRU Cache — Lab 13 (Caching)

| Aspect | Details |
|--------|---------|
| **LeetCode** | 146 (LRU Cache) |
| **Company** | Google, Amazon, Microsoft, Meta — **very high** |
| **Backend Context** | Redis cache eviction, HTTP cache headers, browser cache |
| **Java approach** | LinkedHashMap (O(1)), or custom Node + HashMap + doubly linked list |
| **Follow-up** | Implement LFU (LC 460), implement TTL-based cache |

### 5.2 Design Twitter — Lab 07 (Messaging) + Lab 23 (CQRS/Axon)

| Aspect | Details |
|--------|---------|
| **LeetCode** | 355 (Design Twitter) |
| **Company** | Meta, Google, Amazon — **high** |
| **Backend Context** | Timeline fanout, news feed generation, social graph |
| **Java approach** | HashMap + PriorityQueue for merging timelines |
| **Follow-up** | Add retweets, likes, media; handle celebrities (fanout-on-read vs fanout-on-write) |

### 5.3 Design HashMap — Lab 04 (Spring Data JPA)

| Aspect | Details |
|--------|---------|
| **LeetCode** | 706 (Design HashMap) |
| **Company** | Microsoft, Google, Amazon — **medium** |
| **Backend Context** | Database indexing, hash partitioning, hash-based caching |
| **Java approach** | Array of buckets, LinkedList collisions, load factor resizing |
| **Follow-up** | Thread-safe version (ConcurrentHashMap), open addressing |

### 5.4 Design Parking System — Lab 21 (Multi-Tenancy)

| Aspect | Details |
|--------|---------|
| **LeetCode** | 1603 (Design Parking System) |
| **Company** | Amazon, Uber — **low** |
| **Backend Context** | Resource allocation, tenant isolation, capacity management |
| **Java approach** | Simple counters with atomic increment |
| **Follow-up** | Multi-level parking floors, dynamic pricing, reservation system |

### 5.5 Design File System — Lab 18 (File/Batch Processing)

| Aspect | Details |
|--------|---------|
| **LeetCode** | 588 (Design In-Memory File System) |
| **Company** | Google, Microsoft — **medium** |
| **Backend Context** | S3 object storage, file hierarchy, batch file processing |
| **Java approach** | Trie-like tree with FileNode (name, isFile, content, children) |
| **Follow-up** | Add search by content, versioning, access control |

### 5.6 Design Circular Queue — Lab 07 (Messaging)

| Aspect | Details |
|--------|---------|
| **LeetCode** | 622 (Design Circular Queue) |
| **Company** | Microsoft, Amazon — **medium** |
| **Backend Context** | Ring buffer for Kafka/Log, bounded message queues |
| **Java approach** | Array with head/tail pointers, modulo arithmetic |
| **Follow-up** | Thread-safe, blocking (producer-consumer), bounded vs unbounded |

### 5.7 Design Log Storage System — Lab 24 (Backend Performance)

| Aspect | Details |
|--------|---------|
| **LeetCode** | 635 (Design Log Storage System) |
| **Company** | Google — **medium** |
| **Backend Context** | Distributed logging, time-series data, monitoring |
| **Java approach** | TreeMap with granularity timestamp, range queries |
| **Follow-up** | Sharding by time range, retention policies, thread-safe writes |

### 5.8 Design In-Memory Database Lab 04 (Spring Data JPA)

| Aspect | Details |
|--------|---------|
| **LeetCode** | Design approaches to 981, 981 (Time Map) |
| **Company** | DoorDash, Uber, Google — **high** |
| **Backend Context** | Time-series DB, versioned data, audit logging |
| **Java approach** | HashMap<K, TreeMap<Timestamp, V>> |
| **Follow-up** | Concurrent writes, snapshot isolation, multi-key indexing |

---

## 6. Java-Specific Backend Patterns

### 6.1 Spring Boot — Dependency Injection

**LeetCode-style problem:** "Design a Dependency Injection Container"

```java
class SimpleDIContainer {
    private final Map<Class<?>, Object> singletons = new ConcurrentHashMap<>();
    private final Map<Class<?>, Class<?>> bindings = new ConcurrentHashMap<>();

    public <T> void bind(Class<T> iface, Class<? extends T> impl) {
        bindings.put(iface, impl);
    }

    @SuppressWarnings("unchecked")
    public <T> T resolve(Class<T> type) {
        return (T) singletons.computeIfAbsent(type, this::instantiate);
    }

    private Object instantiate(Class<?> type) {
        Class<?> impl = bindings.getOrDefault(type, type);
        Constructor<?>[] ctors = impl.getConstructors();
        // Simple: resolve first constructor's parameters
        for (Constructor<?> ctor : ctors) {
            Class<?>[] paramTypes = ctor.getParameterTypes();
            Object[] params = Arrays.stream(paramTypes)
                .map(this::resolve).toArray();
            try { return ctor.newInstance(params); } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("No injectable constructor");
    }
}
```

**Company Frequency:** Google (high), Amazon (high), Microsoft (medium)
**Backend Lab:** Lab 01 (Spring Boot Basics — DI, IoC container)

### 6.2 JPA — Lazy Loading / Proxy

**LeetCode-style problem:** "Implement a Lazy Loading Proxy"

```java
class LazyLoader<T> {
    private volatile T value;
    private final Supplier<T> supplier;

    public LazyLoader(Supplier<T> supplier) { this.supplier = supplier; }

    public T get() {
        T result = value;
        if (result == null) {
            synchronized (this) {
                result = value;
                if (result == null) {
                    result = supplier.get();
                    value = result;
                }
            }
        }
        return result;
    }
}
```

**Company Frequency:** Amazon (medium), Microsoft (medium)
**Backend Lab:** Lab 04 (Spring Data JPA — lazy loading, fetch strategies, N+1 problem)

### 6.3 Security — JWT / Rate Limiter

**LeetCode-style problem:** "Design a Token Bucket Rate Limiter"

```java
class TokenBucket {
    private final long capacity;
    private final long refillRate;          // tokens per second
    private double tokens;
    private long lastRefill;

    public TokenBucket(long capacity, long refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.tokens = capacity;
        this.lastRefill = System.currentTimeMillis();
    }

    public synchronized boolean allowRequest() {
        refill();
        if (tokens >= 1) { tokens -= 1; return true; }
        return false;
    }

    private void refill() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastRefill;
        tokens = Math.min(capacity, tokens + elapsed * refillRate / 1000.0);
        lastRefill = now;
    }
}
```

**LeetCode Problems:** None directly, but concept used in many system design problems
**Company Frequency:** Google, Amazon, Stripe — **very high**
**Backend Lab:** Lab 06 (Security — JWT, OAuth2, rate limiting)

### 6.4 Reactive — Publisher/Subscriber

**LeetCode-style problem:** "Implement a Simple Pub/Sub System"

```java
class SimpleEventBus {
    private final Map<Class<?>, List<Consumer<?>>> handlers = new ConcurrentHashMap<>();

    public <T> void subscribe(Class<T> eventType, Consumer<T> handler) {
        handlers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                .add(handler);
    }

    @SuppressWarnings("unchecked")
    public <T> void publish(T event) {
        List<Consumer<?>> consumers = handlers.get(event.getClass());
        if (consumers != null) consumers.forEach(c -> ((Consumer<T>) c).accept(event));
    }
}
```

**Company Frequency:** Netflix (high), Uber (medium), Google (medium)
**Backend Lab:** Lab 15 (WebFlux/Reactive — Mono/Flux, backpressure)

### 6.5 Circuit Breaker

**LeetCode-style problem:** "Implement a Circuit Breaker"

```java
class CircuitBreaker {
    enum State { CLOSED, OPEN, HALF_OPEN }

    private State state = State.CLOSED;
    private int failureCount = 0;
    private final int threshold;
    private final long timeoutMs;
    private long lastFailureTime;

    public CircuitBreaker(int threshold, long timeoutMs) {
        this.threshold = threshold;
        this.timeoutMs = timeoutMs;
    }

    public synchronized <T> T execute(Supplier<T> operation) {
        if (state == State.OPEN) {
            if (System.currentTimeMillis() - lastFailureTime > timeoutMs) {
                state = State.HALF_OPEN;
            } else throw new RuntimeException("Circuit breaker is OPEN");
        }
        try {
            T result = operation.get();
            if (state == State.HALF_OPEN) state = State.CLOSED;
            failureCount = 0;
            return result;
        } catch (Exception e) {
            failureCount++;
            lastFailureTime = System.currentTimeMillis();
            if (failureCount >= threshold) state = State.OPEN;
            throw e;
        }
    }
}
```

**Company Frequency:** Netflix (very high), Amazon (high), Google (high)
**Backend Lab:** Lab 16 (Spring Cloud — Resilience4J circuit breaker)

---

## Pattern Frequency Matrix

| Pattern | LeetCode Problem | Google | Amazon | Meta | Microsoft | Netflix | Uber | Stripe |
|---------|-----------------|--------|--------|------|-----------|---------|------|--------|
| LRU Cache | 146 | VH | H | H | H | M | H | M |
| Producer-Consumer | 1188 | H | H | M | M | H | M | M |
| Token Bucket | (Concept) | VH | H | M | M | H | VH | VH |
| Sliding Window | 239, 480 | H | H | VH | M | M | H | M |
| Design Twitter | 355 | H | M | VH | L | L | M | L |
| Time Map | 981 | H | M | M | M | L | H | H |
| Circuit Breaker | (Concept) | M | H | L | M | VH | H | M |
| Dining Philosophers | 1226 | VH | L | L | M | L | L | L |
| Reader-Writer | 1242 | H | M | L | L | M | M | L |
| File System | 588 | H | M | L | H | M | L | L |

*VH = Very High, H = High, M = Medium, L = Low*

---

## Backend Engineer's LeetCode Study Plan

### Must-Solve (40 problems for backend roles)
| Priority | Problem | Reason for Backend |
|----------|---------|-------------------|
| P0 | 146 LRU Cache | Caching in every system design |
| P0 | 355 Design Twitter | Feed generation, fanout pattern |
| P0 | 1188 Bounded Blocking Queue | Concurrency, message queues |
| P0 | 981 Time Based KV Store | Time-series data, versioned DB |
| P0 | 706 Design HashMap | Hash-based indexing |
| P0 | 460 LFU Cache | Cache eviction policies |
| P0 | 622 Design Circular Queue | Ring buffer, Kafka/Log |
| P1 | 1226 Dining Philosophers | Deadlock prevention |
| P1 | 1242 Web Crawler | Multi-threaded crawling |
| P1 | 588 In-Memory File System | File storage, S3 patterns |
| P1 | 160 Intersection of Two Lists | Cursor-based pagination |
| P1 | 239 Sliding Window Max | Stream processing |
| P1 | 295 Find Median from Data Stream | Real-time analytics |
| P1 | 895 Maximum Frequency Stack | Cache frequency patterns |
| P2 | 635 Design Log Storage | Logging/monitoring |
| P2 | 1429 First Unique Number | Data stream uniqueness |
| P2 | 1472 Design Browser History | Observer pattern |
| P2 | 1603 Design Parking System | Resource allocation |

### Practice Strategy
- **Weeks 1-2:** All P0 problems (8 problems, 2-3 per solution approach)
- **Weeks 3-4:** P1 problems + reimplement P0 from memory
- **Weeks 5-6:** P2 problems + timed mock coding interviews
- **Weekly:** Pick one "system design" LeetCode problem and correlate it with a backend lab

---

<div align="center">

**LeetCode is not about memorizing — it's about recognizing patterns and mapping them to backend concepts.**

</div>
