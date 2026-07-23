# Microsoft Interview Guide — Java Academy

## Interview Process for Java Roles

### Rounds & Timeline
- **Phone Screen (45 min):** 1 coding + culture fit screening
- **On-site (4 rounds):** 2 coding, 1 system design, 1 behavioral ("fit" round — very important at Microsoft)
- **Timeline:** 3-8 weeks (Microsoft is slower due to "loop coordinator" process)
- **Java-Specific:** Microsoft is the most **enterprise-Java-friendly** FAANG. Azure SDK is Java, Teams uses Java, many internal services use Java. **Java is well-regarded and expected.**

### Java-Specific Evaluation
- Microsoft focuses on **design patterns** and **clean architecture** — Dependency Injection, Factory, Strategy
- They love **SOLID principles** — expect to justify every class responsibility
- **Testing** is highly valued — they often ask "how would you unit test this?"
- **Azure integration** — showing awareness of Azure SDK for Java is a plus
- **Records, sealed classes, pattern matching** — Microsoft interviewers know modern Java well

---

## Top Problems by Module

### Module: Object-Oriented Design & Design Patterns

#### Problem: Design a Text Editor with Undo/Redo
- **LC:** N/A (OOP Design)
- **Difficulty/Frequency:** Hard / High
- **Problem Statement:** Design a text editor supporting insert, delete, undo, redo. Use Memento pattern.
- **Interview Walkthrough:** Microsoft OOP design is the most rigorous in FAANG. Use `StringBuilder` for state, `Deque<Memento>` for undo/redo stacks. Microsoft wants to see Command + Memento patterns applied naturally.
- **Solution 1 vs Solution 2:** Single `StringBuilder` with two stacks is clean. Gap buffer (more complex) is what real editors use. Microsoft accepts the stack approach but asks "what's the memory cost of snapshots?"
- **Java Code:**
```java
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Text editor with undo/redo using Memento pattern.
 * Microsoft tests design pattern fluency — expects Command + Memento.
 */
public class TextEditor {

    /**
     * Immutable snapshot of editor state.
     */
    public record Memento(String content) {}

    private final StringBuilder content;
    private final Deque<Memento> undoStack;
    private final Deque<Memento> redoStack;

    public TextEditor() {
        this.content = new StringBuilder();
        this.undoStack = new ArrayDeque<>();
        this.redoStack = new ArrayDeque<>();
    }

    /**
     * Inserts text at given position.
     * @param pos  position to insert at
     * @param text text to insert
     * @throws IndexOutOfBoundsException if position invalid
     */
    public void insert(int pos, String text) {
        saveSnapshot();
        content.insert(pos, text);
        redoStack.clear();
    }

    /**
     * Deletes characters from position with given length.
     * @param pos   start position
     * @param len   number of characters to delete
     */
    public void delete(int pos, int len) {
        saveSnapshot();
        content.delete(pos, Math.min(pos + len, content.length()));
        redoStack.clear();
    }

    /**
     * Undoes last operation.
     * @return true if undo was performed
     */
    public boolean undo() {
        if (undoStack.isEmpty()) return false;
        redoStack.push(new Memento(content.toString()));
        Memento prev = undoStack.pop();
        content.replace(0, content.length(), prev.content());
        return true;
    }

    /**
     * Redoes previously undone operation.
     * @return true if redo was performed
     */
    public boolean redo() {
        if (redoStack.isEmpty()) return false;
        undoStack.push(new Memento(content.toString()));
        Memento next = redoStack.pop();
        content.replace(0, content.length(), next.content());
        return true;
    }

    /**
     * @return current editor content
     */
    public String getContent() {
        return content.toString();
    }

    private void saveSnapshot() {
        undoStack.push(new Memento(content.toString()));
        if (undoStack.size() > 100) {
            undoStack.removeLast(); // limit memory
        }
    }
}
```
- **Company Evaluation Criteria:** Pattern usage (Memento + Command), memory awareness (undo limit), record usage (modern Java).
- **Follow-ups:** Support multiple cursors; make thread-safe; use gap buffer for efficiency.

#### Problem: Design Parking Lot (Microsoft variant)
- **LC:** 1603
- **Difficulty/Frequency:** Medium / Medium
- **Problem Statement:** Design parking lot with hourly pricing, different spot sizes.
- **Interview Walkthrough:** Microsoft adds **strategy pattern for pricing** and **factory for spot creation**. They care about extensibility — "add electric vehicle charging spots" without modifying existing code.
- **Solution 1 vs Solution 2:** Simple inheritance (Amazon version) vs Strategy + Factory (Microsoft version). Microsoft explicitly wants the patterns version.
- **Java Code:**
```java
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

/**
 * Parking lot with Strategy pattern for pricing.
 * Microsoft expects design patterns — Factory, Strategy, Builder.
 */
public class MicrosoftParkingLot {

    enum VehicleType { MOTORCYCLE, CAR, TRUCK, EV }

    record Vehicle(String plate, VehicleType type) {}

    record ParkingTicket(String id, Vehicle vehicle, LocalDateTime entryTime, String spotId) {}

    interface PricingStrategy {
        double calculateCost(Duration duration);
    }

    static class HourlyPricing implements PricingStrategy {
        private final double ratePerHour;
        HourlyPricing(double ratePerHour) { this.ratePerHour = ratePerHour; }
        public double calculateCost(Duration duration) {
            long hours = Math.max(1, duration.toHours());
            return hours * ratePerHour;
        }
    }

    static class ProgressivePricing implements PricingStrategy {
        public double calculateCost(Duration duration) {
            long hours = duration.toHours();
            if (hours <= 2) return hours * 5.0;
            if (hours <= 6) return 10.0 + (hours - 2) * 4.0;
            return 26.0 + (hours - 6) * 3.0;
        }
    }

    static class ParkingSpotFactory {
        private static final Map<VehicleType, Function<String, ParkingSpot>> creators = Map.of(
            VehicleType.MOTORCYCLE, id -> new ParkingSpot(id, 1),
            VehicleType.CAR,        id -> new ParkingSpot(id, 2),
            VehicleType.TRUCK,      id -> new ParkingSpot(id, 3),
            VehicleType.EV,         id -> new EVChargingSpot(id, 2)
        );

        static ParkingSpot create(String id, VehicleType type) {
            return creators.getOrDefault(type, i -> new ParkingSpot(i, 2)).apply(id);
        }
    }

    static class ParkingSpot {
        final String id;
        final int size;
        Vehicle current;
        ParkingSpot(String id, int size) { this.id = id; this.size = size; }
        boolean canFit(Vehicle v) { return current == null && size >= spotSizeFor(v); }
        void park(Vehicle v) { this.current = v; }
        Vehicle leave() { Vehicle v = current; current = null; return v; }
        private int spotSizeFor(Vehicle v) { return switch (v.type()) {
            case MOTORCYCLE -> 1; case CAR, EV -> 2; case TRUCK -> 3;
        };}
    }

    static class EVChargingSpot extends ParkingSpot {
        EVChargingSpot(String id, int size) { super(id, size); }
    }

    private final Map<String, ParkingSpot> spots;
    private final Map<String, ParkingTicket> active;
    private final PricingStrategy pricing;

    public MicrosoftParkingLot(int spotsPerType, PricingStrategy pricing) {
        this.pricing = pricing;
        this.spots = new HashMap<>();
        this.active = new HashMap<>();
        int id = 0;
        for (VehicleType t : VehicleType.values()) {
            for (int i = 0; i < spotsPerType; i++) {
                ParkingSpot s = ParkingSpotFactory.create(t.name() + "-" + (id++), t);
                spots.put(s.id, s);
            }
        }
    }

    public ParkingTicket enter(Vehicle v) {
        ParkingSpot spot = spots.values().stream()
            .filter(s -> s.canFit(v)).findFirst().orElseThrow(() -> new RuntimeException("Full"));
        spot.park(v);
        ParkingTicket ticket = new ParkingTicket(
            UUID.randomUUID().toString(), v, LocalDateTime.now(), spot.id);
        active.put(ticket.id(), ticket);
        return ticket;
    }

    public double exit(String ticketId) {
        ParkingTicket t = active.remove(ticketId);
        if (t == null) throw new IllegalArgumentException("Invalid ticket");
        ParkingSpot spot = spots.get(t.spotId());
        spot.leave();
        return pricing.calculateCost(Duration.between(t.entryTime(), LocalDateTime.now()));
    }
}
```
- **Company Evaluation Criteria:** Factory pattern, Strategy pattern, record types, separation of concerns, extensibility.
- **Follow-ups:** Add valet parking; add reservation system; add peak-hour pricing.

---

### Module: Concurrency

#### Problem: Design a Thread-Safe HashMap
- **LC:** N/A
- **Difficulty/Frequency:** Hard / Medium
- **Problem Statement:** Implement a thread-safe HashMap from scratch.
- **Interview Walkthrough:** Microsoft asks this to test concurrent programming depth. Start with `synchronized` on all methods (coarse). Then segment locks like `ConcurrentHashMap`. Microsoft wants to discuss CAS + synchronized on bins.
- **Solution 1 vs Solution 2:** Coarse-grained locking (simple, low throughput). Striped locking with `ReentrantLock[]` segments (better). Microsoft wants the striped version — they use this pattern in Azure SDK.
- **Java Code:**
```java
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-safe HashMap with striped locking.
 * Microsoft's internally preferred approach — similar to ConcurrentHashMap JDK 7.
 */
public class ThreadSafeHashMap<K, V> {

    private static class Entry<K, V> {
        final K key;
        V value;
        Entry<K, V> next;
        Entry(K key, V value) { this.key = key; this.value = value; }
    }

    private final Entry<K, V>[] table;
    private final ReentrantLock[] locks;
    private final int concurrencyLevel;

    @SuppressWarnings("unchecked")
    public ThreadSafeHashMap(int capacity, int concurrencyLevel) {
        this.table = new Entry[capacity];
        this.concurrencyLevel = concurrencyLevel;
        this.locks = new ReentrantLock[concurrencyLevel];
        for (int i = 0; i < concurrencyLevel; i++) {
            locks[i] = new ReentrantLock();
        }
    }

    /**
     * Thread-safe put with striped locking.
     * @param key   key
     * @param value value
     */
    public void put(K key, V value) {
        int hash = key.hashCode() & 0x7FFFFFFF;
        int idx = hash % table.length;
        int lockIdx = idx % concurrencyLevel;
        locks[lockIdx].lock();
        try {
            Entry<K, V> head = table[idx];
            Entry<K, V> curr = head;
            while (curr != null) {
                if (curr.key.equals(key)) {
                    curr.value = value;
                    return;
                }
                curr = curr.next;
            }
            Entry<K, V> newEntry = new Entry<>(key, value);
            newEntry.next = head;
            table[idx] = newEntry;
        } finally {
            locks[lockIdx].unlock();
        }
    }

    /**
     * Thread-safe get with striped locking.
     * @param key key
     * @return value or null
     */
    public V get(K key) {
        int hash = key.hashCode() & 0x7FFFFFFF;
        int idx = hash % table.length;
        int lockIdx = idx % concurrencyLevel;
        locks[lockIdx].lock();
        try {
            Entry<K, V> curr = table[idx];
            while (curr != null) {
                if (curr.key.equals(key)) return curr.value;
                curr = curr.next;
            }
            return null;
        } finally {
            locks[lockIdx].unlock();
        }
    }

    /**
     * @return all keys (snapshot, not live)
     */
    public List<K> keys() {
        List<K> result = new ArrayList<>();
        for (Entry<K, V> entry : table) {
            Entry<K, V> curr = entry;
            while (curr != null) {
                result.add(curr.key);
                curr = curr.next;
            }
        }
        return result;
    }
}
```
- **Company Evaluation Criteria:** Lock striping, hash distribution, CAS vs locks discussion, resizing awareness.
- **Follow-ups:** Add resize; use CAS for reads (lock-free); compare with ConcurrentHashMap JDK 8+.

---

### Module: Modern Java (Records, Sealed, Pattern Matching)

#### Problem: Design a Shape Hierarchy with Sealed Classes
- **LC:** N/A
- **Difficulty/Frequency:** Medium / Medium
- **Problem Statement:** Model a shape hierarchy that can be extended only by known types. Compute area using pattern matching.
- **Interview Walkthrough:** Microsoft introduced sealed classes in Java 17 and expects you to use them. `sealed interface Shape permits Circle, Rectangle, Triangle`. Use `switch` expression with pattern matching.
- **Solution 1 vs Solution 2:** Abstract class (pre-Java 17). Sealed interface + record + pattern matching (Java 17+). Microsoft wants the modern version — shows you stay current.
- **Java Code:**
```java
/**
 * Sealed interface shape hierarchy with pattern matching.
 * Microsoft expects modern Java — records, sealed, pattern matching instanceof.
 */
public sealed interface Shape
    permits Shape.Circle, Shape.Rectangle, Shape.Triangle {

    record Circle(double radius) implements Shape {}
    record Rectangle(double width, double height) implements Shape {}
    record Triangle(double base, double height) implements Shape {}

    /**
     * Computes area using pattern matching switch (Java 21).
     * @param shape any Shape implementation
     * @return area of the shape
     */
    static double area(Shape shape) {
        return switch (shape) {
            case Circle c      -> Math.PI * c.radius() * c.radius();
            case Rectangle r   -> r.width() * r.height();
            case Triangle t    -> 0.5 * t.base() * t.height();
        };
    }

    /**
     * Computes perimeter using pattern matching.
     * @param shape any Shape implementation
     * @return perimeter of the shape
     */
    static double perimeter(Shape shape) {
        return switch (shape) {
            case Circle c      -> 2 * Math.PI * c.radius();
            case Rectangle r   -> 2 * (r.width() + r.height());
            case Triangle t    -> t.base() + t.height() + 
                Math.sqrt(t.base() * t.base() + t.height() * t.height());
        };
    }
}
```
- **Company Evaluation Criteria:** Sealed class usage, pattern matching exhaustiveness, record usage, modern Java fluency.
- **Follow-ups:** Add new shape without modifying sealed interface (impossible — that's the point); use visitor pattern instead.

---

### Module: Testing & Quality

#### Problem: Design a Rate Limiter with tests
- **LC:** N/A
- **Difficulty/Frequency:** Medium / High
- **Problem Statement:** Implement a sliding window rate limiter. Microsoft asks "show me how you test it."
- **Interview Walkthrough:** Microsoft cares deeply about testability. Write the rate limiter, then write JUnit 5 parameterized tests. Discuss mocking time with `Clock` injection.
- **Solution 1 vs Solution 2:** Fixed window (simpler, bursty at boundaries). Sliding window log (precise, more memory). Microsoft prefers sliding window with `TreeMap` timestamp log.
- **Java Code:**
```java
import java.time.Clock;
import java.time.Instant;
import java.util.TreeMap;

/**
 * Sliding window rate limiter with injectable Clock for testability.
 * Microsoft cares about testability — always inject external dependencies.
 */
public class RateLimiter {

    private final int maxRequests;
    private final long windowMillis;
    private final TreeMap<Long, Integer> requestLog;
    private final Clock clock;

    /**
     * @param maxRequests  max requests allowed per window
     * @param windowMillis time window in milliseconds
     * @param clock        injectable clock (use Clock.systemUTC() in prod)
     */
    public RateLimiter(int maxRequests, long windowMillis, Clock clock) {
        this.maxRequests = maxRequests;
        this.windowMillis = windowMillis;
        this.clock = clock;
        this.requestLog = new TreeMap<>();
    }

    /**
     * Allows request if under limit for current window.
     * @return true if allowed, false if rate-limited
     */
    public synchronized boolean allow() {
        long now = clock.millis();
        long windowStart = now - windowMillis;
        requestLog.headMap(windowStart, true).clear();
        int currentCount = requestLog.values().stream().mapToInt(Integer::intValue).sum();
        if (currentCount >= maxRequests) return false;
        requestLog.merge(now, 1, Integer::sum);
        return true;
    }
}
```
- **Company Evaluation Criteria:** Testability (Clock injection), thread safety, sliding window algorithm, TreeMap usage.
- **Follow-ups:** Write JUnit 5 parameterized test with fixed clock; distributed rate limiter with Redis.

---

### Module: Networking & I/O

#### Problem: Web Crawler Multithreaded
- **LC:** 1242
- **Difficulty/Frequency:** Medium / Medium
- **Problem Statement:** Crawl a web URL hierarchy using threads.
- **Interview Walkthrough:** Microsoft Azure team asks this. `ExecutorService` + `CompletableFuture`. Use `ConcurrentHashMap` for visited set. `BlockingQueue` for URL frontier.
- **Solution 1 vs Solution 2:** Single-threaded queue (slow). `ExecutorService` with `CompletionService` (fast, concurrent). Microsoft wants the concurrent version with proper error handling.
- **Java Code:**
```java
import java.util.*;
import java.util.concurrent.*;

/**
 * Multithreaded web crawler using ExecutorService.
 * Microsoft Azure team asks this for Java networking roles.
 *
 * LC 1242 — Microsoft networking interview question
 */
public class WebCrawler {

    private final ExecutorService executor;
    private final Set<String> visited;
    private final ConcurrentLinkedQueue<String> frontier;

    /**
     * @param maxThreads maximum concurrent crawl threads
     */
    public WebCrawler(int maxThreads) {
        this.executor = Executors.newFixedThreadPool(maxThreads);
        this.visited = ConcurrentHashMap.newKeySet();
        this.frontier = new ConcurrentLinkedQueue<>();
    }

    /**
     * Starts crawling from given URL.
     * @param startUrl seed URL
     * @return list of all visited URLs
     * @throws InterruptedException if interrupted
     */
    public List<String> crawl(String startUrl) throws InterruptedException {
        frontier.offer(startUrl);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        while (!frontier.isEmpty()) {
            String url = frontier.poll();
            if (url == null || !visited.add(url)) continue;

            String finalUrl = url;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                List<String> links = fetchLinks(finalUrl);
                frontier.addAll(links);
            }, executor);

            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();
        return new ArrayList<>(visited);
    }

    /**
     * Mock URL fetching — returns sub-URLs.
     * @param url URL to fetch
     * @return list of discovered links
     */
    private List<String> fetchLinks(String url) {
        // In real implementation: use java.net.http.HttpClient
        return List.of(); // stub
    }
}
```
- **Company Evaluation Criteria:** ExecutorService usage, thread safety of visited set, CompletableFuture composition, error handling.
- **Follow-ups:** Handle malformed URLs; respect robots.txt; add politeness delay.

---

## JVM/Concurrency Deep Dive Questions

Microsoft asks enterprise-focused JVM questions:

1. **How does G1GC handle large heap (>100GB)?** — Microsoft Bing team runs huge JVMs. Walk through region sizing, mixed GC, `-XX:G1HeapRegionSize`, `-XX:MaxGCPauseMillis` tuning.
2. **Explain the Java memory model's happens-before for synchronized + volatile** — Microsoft's Azure SDK uses this pattern extensively. Give concrete example with `synchronized` increment.
3. **How would you monitor a JVM in production?** — Microsoft uses Prometheus + Grafana for JVM metrics. Walk through `-XX:+PrintGCDetails`, `jcmd`, JMX MBeans, Flight Recorder.
4. **What are the risks of virtual threads (Java 21)?** — Microsoft has production experience with Project Loom. Discuss: pinning with synchronized, thread-local memory growth, pool sizing.
5. **Explain how ClassLoader parent delegation works** — Walk through Bootstrap -> Extension -> Application class loaders. Why does Tomcat break this with WebappClassLoader?

## System Design with Java

1. **Design Azure Blob Storage client** — Java SDK pattern: Builder for client creation, `CompletableFuture` for async operations, reactive streams for large blobs. Discuss request pipeline with retry policy.
2. **Design a distributed key-value store** — Consistent hashing in Java using `TreeMap`. Virtual nodes for load distribution. `ConcurrentHashMap` for local cache layer.
3. **Design an API gateway** — Java `HttpServer` (com.sun.net.httpserver) or Spring Cloud Gateway. Rate limiting (sliding window), authentication filter chain, circuit breaker with `CompletableFuture` timeouts.

## Behavioral Questions (STAR)

Microsoft's "fit" round is the most important at the company.

1. **"Tell me about a time you influenced without authority."** — *S: Team resisted adopting Java 21 records. T: Show value without mandate. A: Wrote internal blog with performance benchmarks of records vs POJOs, demo'd at lunch-and-learn. R: 3 teams adopted records, saving ~5000 lines of boilerplate.*
2. **"Tell me about a time you had to learn a new technology quickly."** — *S: Needed to integrate with Azure SDK for Java. T: Ship in 2 weeks. A: Studied Azure SDK patterns, built prototype over weekend, pair-programmed with Azure specialist. R: Shipped on time, became team's Azure expert.*
3. **"Tell me about a time you delivered a project with ambiguity."** — *S: No clear requirements for caching layer. T: Design caching strategy. A: Researched team's access patterns, built A/B test comparing Caffeine vs Redis. R: Caffeine was 3x faster for workload, saved $12k/month in Redis costs.*
4. **"Tell me about growth mindset."** — *S: First time using reactive Java. T: Build producer-consumer pipeline. A: Took Coursera course, built prototype, refactored 3 times based on code reviews. R: Pipeline handles 100k events/sec.*
5. **"Tell me about a time you made an unpopular decision."** — *S: Deprecated team's custom caching library for Caffeine. T: Convince team to migrate. A: Measured 40% latency improvement, migration was 2 days of work. R: Team adopted unanimously after seeing numbers.*

## Study Plan

| Priority | Labs | Focus |
|----------|------|-------|
| P0 | 06-oop-basics, 07-inheritance, 08-polymorphism, 09-abstraction-interfaces | OOP & design patterns |
| P0 | 21-java-21-features, 22-records, 23-sealed-classes, 24-pattern-matching | Modern Java |
| P1 | 16-concurrency, 41-threading-deep-dive, 42-locking-synchronization | Concurrency |
| P1 | 31-testing, 34-logging | Testing & quality |
| P2 | 32-networking, 18-io-nio | Networking |
| P2 | 45-gc-deep-dive, 46-jvm-tuning | JVM tuning |

**Preparation Path:** Solve pattern-heavy OOP problems (parking lot, text editor, elevator system). Master Java records, sealed classes, pattern matching. Study Azure SDK for Java patterns. Write parameterized JUnit 5 tests. Practice STAR stories with "Growth Mindset" and "Customer Obsession."

## Tips

- **Microsoft is the most process-oriented FAANG** — Expect deep dives into software engineering and testing
- **Design patterns are non-negotiable** — Know Factory, Builder, Strategy, Memento, Command, Observer
- **Testability matters** — If your code doesn't have dependency injection, expect pushback
- **Modern Java is expected** — Use records, sealed classes, pattern matching, text blocks
- **Azure knowledge is a huge plus** — If you mention Azure SDK patterns, interviewers will be impressed
- **The "ASAP" (Ask, Solve, Ask) method works** — Microsoft interviewers like candidates who ask clarifying questions before coding
- **Behavioral ("fit") round is pass/fail** — More candidates fail the behavioral than the technical rounds at Microsoft
- **Know Microsoft's culture** — "Growth Mindset" and "We are one company" are frequently evaluated