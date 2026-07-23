# Netflix Interview Guide — Java Academy

## Interview Process for Java Roles

### Rounds & Timeline
- **Phone Screen (45 min):** 1 algorithmic coding problem + system design discussion
- **On-site (4-5 rounds):** 2 coding, 1 system design (deep), 1 behavioral/culture, 1 manager round
- **Timeline:** 2-5 weeks (Netflix moves fast — "Freedom and Responsibility")
- **Java-Specific:** Netflix is a **heavy Java shop** — their entire streaming platform runs on Java (Zuul, Eureka, Hystrix, Archaius). **Java is the default** and they expect Spring Boot & Spring Cloud experience.

### Java-Specific Evaluation
- Netflix invented **Spring Cloud Netflix** (Eureka, Zuul, Hystrix) — knowing these is a huge signal
- They care about **reactive programming** — Project Reactor, WebFlux, reactive streams
- **Resilience patterns** — circuit breakers, bulkheads, retries, fallbacks (Hystrix/Resilience4J)
- **Performance at scale** — 200M+ subscribers, every millisecond matters
- **Microservices architecture** understanding is mandatory

---

## Top Problems by Module

### Module: Reactive Programming

#### Problem: Implement a Reactive Stream Publisher
- **LC:** N/A
- **Difficulty/Frequency:** Hard / Very High
- **Problem Statement:** Implement a minimal reactive streams `Publisher` that backpressures.
- **Interview Walkthrough:** Netflix invented reactive patterns at scale. Implement `Publisher<T>` following Reactive Streams spec. `Subscription` with `request(n)` backs off. Netflix wants to see understanding of backpressure — this is core to their architecture.
- **Solution 1 vs Solution 2:** `BlockingQueue`-based (simple, blocking). Reactive Streams `Publisher` (non-blocking, backpressure-aware). Netflix wants the reactive version — matches Project Reactor.
- **Java Code:**
```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Minimal reactive streams Publisher with backpressure support.
 * Netflix expects Project Reactor fluency and backpressure understanding.
 */
public class RangePublisher implements java.util.concurrent.Flow.Publisher<Integer> {

    private final int start, end;

    public RangePublisher(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public void subscribe(java.util.concurrent.Flow.Subscriber<? super Integer> subscriber) {
        subscriber.onSubscribe(new RangeSubscription(subscriber));
    }

    private class RangeSubscription implements java.util.concurrent.Flow.Subscription {
        private final java.util.concurrent.Flow.Subscriber<? super Integer> subscriber;
        private final AtomicLong requested = new AtomicLong(0);
        private final ReentrantLock lock = new ReentrantLock();
        private int current;
        private boolean cancelled = false;

        RangeSubscription(java.util.concurrent.Flow.Subscriber<? super Integer> subscriber) {
            this.subscriber = subscriber;
            this.current = start;
        }

        /**
         * Backpressure-aware request handling.
         * @param n number of items requested
         */
        public void request(long n) {
            if (n <= 0) {
                subscriber.onError(new IllegalArgumentException("n > 0 required"));
                return;
            }
            requested.addAndGet(n);
            drain();
        }

        public void cancel() {
            cancelled = true;
        }

        private void drain() {
            lock.lock();
            try {
                while (!cancelled && requested.get() > 0 && current <= end) {
                    subscriber.onNext(current);
                    current++;
                    requested.decrementAndGet();
                }
                if (current > end && !cancelled) {
                    subscriber.onComplete();
                }
            } finally {
                lock.unlock();
            }
        }
    }
}
```
- **Company Evaluation Criteria:** Backpressure implementation, thread safety, Reactive Streams spec compliance, `request()` semantics.
- **Follow-ups:** Make it async with `ExecutorService`; implement `Processor` (Publisher + Subscriber); add `onError` recovery.

#### Problem: Transform with Reactive Operator
- **LC:** N/A
- **Difficulty/Frequency:** Medium / High
- **Problem Statement:** Implement a reactive `map()` operator.
- **Interview Walkthrough:** Netflix expects you to know how Reactor's `map()` works internally. Wrapper publisher that transforms each element. Discuss fusion (micro-optimization of operator chains).
- **Solution 1 vs Solution 2:** Simple wrapper (demonstrates concept). Fused operator (performance, complex). Netflix wants the fused approach — they care about every CPU cycle.
- **Java Code:**
```java
import java.util.concurrent.Flow.*;

/**
 * Reactive map operator — transforms each element via function.
 * Netflix cares about operator fusion and reactive pipeline performance.
 */
public class MapPublisher<T, R> implements Publisher<R> {

    private final Publisher<T> source;
    private final java.util.function.Function<? super T, ? extends R> mapper;

    public MapPublisher(Publisher<T> source, java.util.function.Function<? super T, ? extends R> mapper) {
        this.source = source;
        this.mapper = mapper;
    }

    public void subscribe(Subscriber<? super R> subscriber) {
        source.subscribe(new MapSubscriber(subscriber));
    }

    private class MapSubscriber implements Subscriber<T> {
        private final Subscriber<? super R> downstream;
        private Subscription subscription;

        MapSubscriber(Subscriber<? super R> downstream) {
            this.downstream = downstream;
        }

        public void onSubscribe(Subscription s) {
            this.subscription = s;
            downstream.onSubscribe(s);
        }

        public void onNext(T item) {
            try {
                downstream.onNext(mapper.apply(item));
            } catch (Exception e) {
                downstream.onError(e);
            }
        }

        public void onError(Throwable t) { downstream.onError(t); }
        public void onComplete() { downstream.onComplete(); }
    }
}
```
- **Company Evaluation Criteria:** Reactive operator chain understanding, error propagation, subscription sharing.
- **Follow-ups:** Implement `flatMap()` (inner subscription); add backpressure-aware map; discuss conditional subscriber fusion.

---

### Module: Concurrency & Resilience

#### Problem: Design Circuit Breaker
- **LC:** N/A
- **Difficulty/Frequency:** Hard / High
- **Problem Statement:** Implement a circuit breaker with closed/open/half-open states.
- **Interview Walkthrough:** Netflix invented Hystrix (now Resilience4J). Three states: CLOSED (normal), OPEN (failing, reject), HALF_OPEN (test one request). Sliding window for failure rate. Netflix expects this to be thread-safe and configurable.
- **Solution 1 vs Solution 2:** Simple counter (leaky, bursty). Sliding window with ring buffer (precise). Netflix wants the ring buffer — it matches Hystrix's actual implementation.
- **Java Code:**
```java
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Circuit breaker with sliding window — Netflix's Hystrix pattern.
 * States: CLOSED, OPEN, HALF_OPEN. Thread-safe state machine.
 */
public class CircuitBreaker {

    enum State { CLOSED, OPEN, HALF_OPEN }

    private final AtomicReference<State> state = new AtomicReference<>(State.CLOSED);
    private final int threshold;
    private final long windowNanos;
    private final long halfOpenTimeoutNanos;
    private final AtomicLong lastFailureTime = new AtomicLong(0);
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicLong failureWindowStart = new AtomicLong(System.nanoTime());

    /**
     * @param threshold          failures before circuit opens
     * @param windowMs           sliding window duration in milliseconds
     * @param halfOpenTimeoutMs  time before allowing half-open test
     */
    public CircuitBreaker(int threshold, long windowMs, long halfOpenTimeoutMs) {
        this.threshold = threshold;
        this.windowNanos = windowMs * 1_000_000L;
        this.halfOpenTimeoutNanos = halfOpenTimeoutMs * 1_000_000L;
    }

    /**
     * Returns true if call is allowed through the circuit breaker.
     * @return true if allowed
     */
    public boolean allow() {
        State s = state.get();
        if (s == State.CLOSED) {
            return checkWindow();
        } else if (s == State.OPEN) {
            long elapsed = System.nanoTime() - lastFailureTime.get();
            if (elapsed >= halfOpenTimeoutNanos) {
                if (state.compareAndSet(State.OPEN, State.HALF_OPEN)) {
                    return true;
                }
            }
            return false;
        }
        // HALF_OPEN — allow one test request
        return true;
    }

    /**
     * Records a success — resets failure count.
     */
    public void recordSuccess() {
        State s = state.get();
        if (s == State.HALF_OPEN) {
            state.set(State.CLOSED);
        }
        resetWindow();
    }

    /**
     * Records a failure — may trip circuit to OPEN.
     */
    public void recordFailure() {
        lastFailureTime.set(System.nanoTime());
        if (state.get() == State.HALF_OPEN) {
            state.set(State.OPEN);
            return;
        }
        resetWindowIfExpired();
        if (failureCount.incrementAndGet() >= threshold) {
            state.set(State.OPEN);
        }
    }

    private boolean checkWindow() {
        resetWindowIfExpired();
        return failureCount.get() < threshold;
    }

    private void resetWindowIfExpired() {
        long now = System.nanoTime();
        long start = failureWindowStart.get();
        if (now - start > windowNanos) {
            failureWindowStart.set(now);
            failureCount.set(0);
        }
    }

    private void resetWindow() {
        failureWindowStart.set(System.nanoTime());
        failureCount.set(0);
    }
}
```
- **Company Evaluation Criteria:** State machine correctness, thread safety, sliding window logic, Hystrix/Resilience4J awareness.
- **Follow-ups:** Add metrics (success/failure rates); add `CompletableFuture` recovery; implement in Project Reactor with `Mono.defer()`.

---

### Module: Collections & Streams at Scale

#### Problem: Design Consistent Hash Ring (for caching)
- **LC:** N/A
- **Difficulty/Frequency:** Medium / High
- **Problem Statement:** Implement consistent hashing for cache node distribution.
- **Interview Walkthrough:** Netflix uses consistent hashing for their content delivery. `TreeMap<Long, String>` ring. MD5 hash. 150 virtual nodes per real node. Discuss rebalancing when nodes fail.
- **Solution 1 vs Solution 2:** Simple modulo (terrible at scale). Consistent hashing with virtual nodes (Netflix standard). Netflix wants the virtual node version — discuss how Netflix handles cache node failures.
- **Java Code:**
```java
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Consistent hashing ring for cache node distribution.
 * Netflix uses this for content delivery cache routing.
 */
public class ConsistentHashRing<T> {

    private final TreeMap<Long, T> ring;
    private final int virtualNodes;
    private final MessageDigest md;

    public ConsistentHashRing(int virtualNodes) {
        this.ring = new TreeMap<>();
        this.virtualNodes = virtualNodes;
        try { this.md = MessageDigest.getInstance("MD5"); }
        catch (NoSuchAlgorithmException e) { throw new RuntimeException(e); }
    }

    /**
     * Adds a physical node with virtual replicas.
     * @param node node to add
     */
    public void addNode(T node) {
        for (int i = 0; i < virtualNodes; i++) {
            long h = hash(node.toString() + "#" + i);
            ring.put(h, node);
        }
    }

    /**
     * Removes a physical node and all its replicas.
     * @param node node to remove
     */
    public void removeNode(T node) {
        for (int i = 0; i < virtualNodes; i++) {
            long h = hash(node.toString() + "#" + i);
            ring.remove(h);
        }
    }

    /**
     * Routes key to responsible cache node.
     * @param key lookup key
     * @return responsible node
     */
    public T getNode(String key) {
        if (ring.isEmpty()) return null;
        long h = hash(key);
        Map.Entry<Long, T> entry = ring.ceilingEntry(h);
        if (entry == null) entry = ring.firstEntry();
        return entry.getValue();
    }

    private long hash(String input) {
        md.update(input.getBytes());
        return ByteBuffer.wrap(md.digest()).getLong();
    }
}
```
- **Company Evaluation Criteria:** Ring implementation, virtual node distribution, hash quality, routing correctness.
- **Follow-ups:** Add replication (assign key to N nodes); handle node weight differences; add gossip protocol.

---

### Module: Performance & Profiling

#### Problem: Design a High-Performance Logger
- **LC:** N/A
- **Difficulty/Frequency:** Medium / High
- **Problem Statement:** Design an async, low-latency logger for high-throughput streaming.
- **Interview Walkthrough:** Netflix's streaming pipeline logs millions of events/sec. Use `RingBuffer` (Disruptor pattern) for lockless logging. `LongAdder` for metrics. Netflix expects you to know LMAX Disruptor.
- **Solution 1 vs Solution 2:** `Logback` (sync, blocking). RingBuffer + async consumer (Netflix's approach). They want the lockless version — they use Disruptor internally.
- **Java Code:**
```java
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.locks.LockSupport;

/**
 * High-throughput logger using ring buffer (lockless).
 * Netflix patterns — LMAX Disruptor influence for streaming telemetry.
 */
public class AsyncLogger {

    private static final int RING_SIZE = 1024;
    private final String[] ring = new String[RING_SIZE];
    private final AtomicLongArray sequence = new AtomicLongArray(RING_SIZE);
    private volatile int writeIdx = 0;
    private Thread consumer;
    private volatile boolean running = true;

    public AsyncLogger(String filePath) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(filePath, true));
        consumer = Thread.startVirtualThread(() -> {
            int readIdx = 0;
            while (running) {
                String msg = ring[readIdx];
                if (msg != null) {
                    writer.println(msg);
                    ring[readIdx] = null;
                    readIdx = (readIdx + 1) % RING_SIZE;
                } else {
                    Thread.yield();
                }
            }
            writer.close();
        });
    }

    /**
     * Non-blocking log write — returns immediately.
     * @param message log message
     */
    public void log(String message) {
        int idx = writeIdx;
        // Spin-wait if ring buffer full (bounded wait)
        while (ring[idx] != null) {
            LockSupport.parkNanos(1);
        }
        ring[idx] = message;
        writeIdx = (idx + 1) % RING_SIZE;
    }

    /**
     * Flushes and stops consumer thread.
     */
    public void shutdown() {
        running = false;
        try { consumer.join(1000); } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```
- **Company Evaluation Criteria:** Ring buffer design, lockless algorithm, backpressure, virtual thread usage.
- **Follow-ups:** Use `VarHandle` for volatile access; implement multi-producer with CAS slots; add batch flushing.

---

### Module: Networking & Microservices

#### Problem: Design a Service Discovery Client
- **LC:** N/A
- **Difficulty/Frequency:** Medium / High
- **Problem Statement:** Implement a service discovery client (like Eureka) with heartbeats and caching.
- **Interview Walkthrough:** Netflix invented Eureka. Client cache with `ConcurrentHashMap<String, List<String>>`. Heartbeat with `ScheduledExecutorService`. Round-robin or load-aware routing.
- **Solution 1 vs Solution 2:** Simple DNS lookup (static). Eureka-style client-side discovery (dynamic, Netflix's standard). Netflix wants the Eureka pattern.
- **Java Code:**
```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service discovery client (Eureka pattern) with heartbeat + caching.
 * Netflix invented Eureka — this is their core pattern.
 */
public class ServiceDiscoveryClient {

    private final Map<String, List<String>> registry = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> counters = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public ServiceDiscoveryClient() {
        scheduler.scheduleAtFixedRate(this::heartbeat, 5, 30, TimeUnit.SECONDS);
    }

    /**
     * Registers a service instance.
     * @param serviceName logical service name
     * @param address     host:port address
     */
    public void register(String serviceName, String address) {
        registry.compute(serviceName, (k, v) -> {
            List<String> list = v == null ? new ArrayList<>() : new ArrayList<>(v);
            if (!list.contains(address)) list.add(address);
            return Collections.unmodifiableList(list);
        });
        counters.putIfAbsent(serviceName, new AtomicInteger(0));
    }

    /**
     * Discovers next available instance (round-robin).
     * @param serviceName logical service name
     * @return address or null
     */
    public String discover(String serviceName) {
        List<String> instances = registry.get(serviceName);
        if (instances == null || instances.isEmpty()) return null;
        int idx = counters.getOrDefault(serviceName, new AtomicInteger())
            .getAndIncrement() % instances.size();
        return instances.get(Math.abs(idx));
    }

    /**
     * Deregisters a service instance.
     * @param serviceName logical service name
     * @param address     address to remove
     */
    public void deregister(String serviceName, String address) {
        registry.computeIfPresent(serviceName, (k, v) -> {
            List<String> list = new ArrayList<>(v);
            list.remove(address);
            return list.isEmpty() ? null : Collections.unmodifiableList(list);
        });
    }

    private void heartbeat() {
        // In production: send heartbeat to Eureka server
        // If server unreachable, use cached registry
    }

    /**
     * Graceful shutdown.
     */
    public void shutdown() {
        scheduler.shutdown();
    }
}
```
- **Company Evaluation Criteria:** Client-side discovery, round-robin routing, heartbeat scheduling, thread safety.
- **Follow-ups:** Add load-aware routing; add caching with TTL; implement with virtual threads for heartbeat.

---

### Module: Modern Java Features

#### Problem: Pattern Matching for JSON Processing
- **LC:** N/A
- **Difficulty/Frequency:** Medium / Medium
- **Problem Statement:** Parse a nested JSON structure using Java 21 pattern matching.
- **Interview Walkthrough:** Netflix processes millions of events. Show sealed JSON types, pattern matching `switch`, record patterns. Netflix wants modern Java — they migrated to Java 21 for pattern matching.
- **Solution 1 vs Solution 2:** `if-else instanceof` (verbose). Pattern matching switch (idiomatic). Netflix wants the modern version.
- **Java Code:**
```java
import java.util.List;
import java.util.Map;

/**
 * JSON-like structure processing with Java 21 pattern matching.
 * Netflix migrated to Java 21 for this exact feature.
 */
public sealed interface JsonValue
    permits JsonValue.JsonNull, JsonValue.JsonString, JsonValue.JsonNumber,
            JsonValue.JsonArray, JsonValue.JsonObject {

    record JsonNull() implements JsonValue {}
    record JsonString(String value) implements JsonValue {}
    record JsonNumber(double value) implements JsonValue {}
    record JsonArray(List<JsonValue> elements) implements JsonValue {}
    record JsonObject(Map<String, JsonValue> fields) implements JsonValue {}

    /**
     * Serializes any JsonValue to string using pattern matching switch.
     * @param json input value
     * @return JSON string
     */
    static String serialize(JsonValue json) {
        return switch (json) {
            case JsonNull _             -> "null";
            case JsonString(var s)      -> "\"" + s + "\"";
            case JsonNumber(var n)      -> n == (long) n ? String.valueOf((long) n) : String.valueOf(n);
            case JsonArray(var elems)   -> {
                String joined = elems.stream()
                    .map(JsonValue::serialize)
                    .reduce((a, b) -> a + "," + b)
                    .orElse("");
                yield "[" + joined + "]";
            }
            case JsonObject(var fields) -> {
                String joined = fields.entrySet().stream()
                    .map(e -> "\"" + e.getKey() + "\":" + serialize(e.getValue()))
                    .reduce((a, b) -> a + "," + b)
                    .orElse("");
                yield "{" + joined + "}";
            }
        };
    }
}
```
- **Company Evaluation Criteria:** Sealed types, record patterns, switch exhaustiveness, JSON structure modeling.
- **Follow-ups:** Add pretty-printing with indentation; use `DEFAULT` for error handling; implement parser.

---

## JVM/Concurrency Deep Dive Questions

Netflix asks performance-at-scale JVM questions:

1. **How do you reduce GC pause in a streaming application?** — Netflix runs low-latency streaming. Discuss: `-XX:+UseZGC`, direct buffers for media data, object pooling, escape analysis.
2. **Explain how virtual threads interact with synchronized blocks** — "Our virtual thread pinned when using synchronized. Why?" Walk through carrier thread pinning, JEP 444 limitations, `ReentrantLock` as alternative.
3. **How does Netflix tune G1GC for 200M+ subscribers?** — `-XX:G1HeapRegionSize=32M`, `-XX:MaxGCPauseMillis=5`, `-XX:+UnlockExperimentalVMOptions -XX:G1NewSizePercent=5`, concurrent threads.
4. **What happens in the JVM when a `CompletableFuture` completes?** — Walk through `complete()` -> `postComplete()` -> `tryFire()` -> `ForkJoinPool.commonPool()` execution chain. Netflix uses this extensively in their async pipeline.
5. **Explain the Disruptor pattern and how it outperforms BlockingQueue** — Cache line padding (`@Contended`), ring buffer pre-allocation, barrier-based sequencing, CAS on producer.

## System Design with Java

1. **Design Netflix's recommendation service** — Reactive pipeline with Project Reactor: `Flux<Event>` -> `flatMap` -> ML scoring. Discuss backpressure for ML inference requests, caching with `CacheMono`.
2. **Design a streaming content delivery system** — Java NIO for content chunking, `ByteBuffer` pooling, virtual threads for concurrent chunk downloads. Discuss adaptive bitrate switching with `CompletableFuture` composition.
3. **Design Netflix's Zuul API Gateway** — Java `Filter` chain pattern, reactive with `WebFlux`, `ConcurrentHashMap` for route table, circuit breaker per route. Discuss request collapsing with `Disruptor`.

## Behavioral Questions (STAR)

Netflix's culture is unique: "Freedom and Responsibility."

1. **"Tell me about a time you made a high-impact decision independently." (Freedom & Responsibility)** — *S: Chose to deprecate Shared caches. T: Reduce operational cost. A: Independently designed migration plan, executed over 2 weeks, informed team after completion. R: 30% cache cost reduction, zero downtime.*
2. **"Tell me about a time you had to be radically candid." (Radical Candor)** — *S: Senior dev's code had unsafe concurrency. T: Address without damaging relationship. A: Private conversation, showed JMM violation in their code. R: Senior dev appreciated it, became collaborator on concurrency best practices.*
3. **"Tell me about a time you maintained high performance under pressure." (High Performance)** — *S: Super Bowl streaming event. T: Zero latency degradation. A: Pre-warmed JVMs, tuned G1GC, ran Chaos Engineering experiments for 2 weeks. R: 2M concurrent streams with 50ms p99 latency.*
4. **"Tell me about a time you solved an ambiguous problem." (Curiosity)** — *S: Unknown cause of intermittent latency. T: Identify root cause. A: Built custom JFR event recording with `jcmd`, correlated GC events with latency spikes. R: Found G1GC mixed GC causing 200ms pauses, reconfigured.*
5. **"Tell me about a time you failed and learned." (Personal Growth)** — *S: Deployed without circuit breaker, cascading failure brought down service. T: Prevent recurrence. A: Implemented Resilience4J, wrote chaos tests, presented postmortem. R: Zero cascading failures in 8 months.*

## Study Plan

| Priority | Labs | Focus |
|----------|------|-------|
| P0 | 36-reactive-programming, 37-performance-profiling | Reactive & performance |
| P0 | 16-concurrency, 41-threading-deep-dive, 48-structured-concurrency | Concurrency |
| P1 | 32-networking, 18-io-nio | Networking & microservices |
| P1 | 21-java-21-features, 22-records, 24-pattern-matching | Modern Java |
| P2 | 38-memory-model, 45-gc-deep-dive | JVM memory |
| P2 | 47-profiling-observability, 52-performance-antipatterns | Advanced profiling |

**Preparation Path:** Learn Project Reactor (`Mono`, `Flux`, operators). Implement circuit breaker + bulkhead. Read Hystrix/Resilience4J source. Practice Java 21 pattern matching with sealed types. Build a reactive REST client with WebClient. Study Netflix Tech Blog for engineering patterns.

## Tips

- **Netflix is the most streaming-FAANG** — They don't care about LeetCode grinding as much as system design and reactive patterns
- **Project Reactor fluency is expected** — Know `Mono`, `Flux`, `flatMap`, `concatMap`, `transformDeferred`, backpressure operators
- **Resilience is a first-class concern** — Discuss circuit breakers, bulkheads, retries, fallbacks in every design answer
- **"Freedom and Responsibility" is real** — Expect behavioral questions about independent decision-making with high accountability
- **Microservices patterns are tested** — Know Eureka, Zuul, Hystrix, Ribbon, Archaius (even if Netflix now uses other solutions)
- **Performance at extreme scale** — Every answer should consider 200M+ subscriber load
- **Spring Boot + Cloud Netflix is a plus** — Unlike other FAANGs, Netflix actually uses Spring
- **Read Netflix Tech Blog** — Mentioning a specific Netflix engineering blog post is a strong signal
- **Java 21 virtual threads + reactive** — Understanding how virtual threads complement (not replace) reactive programming sets you apart