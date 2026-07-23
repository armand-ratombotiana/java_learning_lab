# Amazon Interview Guide — Java Academy

## Interview Process for Java Roles

### Rounds & Timeline
- **Phone Screen (60 min):** 1 coding + LP (Leadership Principles) probing
- **On-site (4-5 rounds):** 2 coding, 1 system design (bar-raiser), 1 OOP design, 1 LP deep-dive
- **Timeline:** 3-6 weeks
- **Java-Specific:** Amazon is the most Java-friendly FAANG. They use Java extensively (AWS SDK, DynamoDB client, internal services). **Yes, you can use Java in all rounds** and they prefer it for the OOP design round.

### Java-Specific Evaluation
- Amazon focuses heavily on **concurrency** and **thread safety** — their services handle millions of requests/sec
- They care deeply about **design patterns** — Singleton, Factory, Builder, Strategy in the OOP round
- **Java concurrency utilities** (`ConcurrentHashMap`, `BlockingQueue`, `ThreadPoolExecutor`) are expected
- They will ask "how do you make this thread-safe?" on every coding question
- **Amazon Leadership Principles** are evaluated in every round — every answer must demonstrate one

---

## Top Problems by Module

### Module: Concurrency

#### Problem: Design Bounded Blocking Queue
- **LC:** 1188
- **Difficulty/Frequency:** Medium / Very High
- **Problem Statement:** Implement a thread-safe bounded blocking queue with `enqueue` and `dequeue`.
- **Interview Walkthrough:** Amazon asks this in almost every SDE-II interview. Start with `synchronized` + `wait/notify`, then upgrade to `ReentrantLock` + `Condition`. Amazon cares about **backpressure** — relate this to SQS queue throttling.
- **Solution 1 vs Solution 2:** `synchronized` is simpler but `ReentrantLock` gives `tryLock()` with timeout — Amazon values this for production systems.
- **Java Code:**
```java
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-safe bounded blocking queue with backpressure.
 * Amazon expects discussion of thread starvation and fairness.
 *
 * LC 1188 — Amazon on-site favorite for SDE-II
 */
public class BoundedBlockingQueue {

    private final Queue<Integer> queue;
    private final int capacity;
    private final ReentrantLock lock = new ReentrantLock(true); // fair
    private final Condition notFull  = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    /**
     * @param capacity max elements in queue
     */
    public BoundedBlockingQueue(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedList<>();
    }

    /**
     * Enqueues with timeout awareness — Amazon wants tryLock discussion.
     * @param element value to enqueue
     * @throws InterruptedException on interruption
     */
    public void enqueue(int element) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            while (queue.size() == capacity) {
                notFull.await();
            }
            queue.offer(element);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Dequeues with fair lock ordering.
     * @return next element
     * @throws InterruptedException on interruption
     */
    public int dequeue() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            while (queue.isEmpty()) {
                notEmpty.await();
            }
            int value = queue.poll();
            notFull.signal();
            return value;
        } finally {
            lock.unlock();
        }
    }

    /**
     * @return current queue size
     */
    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }
}
```
- **Company Evaluation Criteria:** Fairness policy, `lockInterruptibly()` usage, backpressure analogy.
- **Follow-ups:** How does this relate to SQS? Add priority support. Handle poison pills.

#### Problem: The Dining Philosophers
- **LC:** 1226
- **Difficulty/Frequency:** Medium / Medium
- **Problem Statement:** Five philosophers, five forks. Prevent deadlock and starvation.
- **Interview Walkthrough:** Amazon uses this to test deadlock prevention. Start with global lock (terrible). Then resource hierarchy (better). Amazon's preferred: `ReentrantLock` with `tryLock()` + timeout — this mirrors their optimistic concurrency patterns in DynamoDB.
- **Solution 1 vs Solution 2:** Global lock prevents deadlock but kills concurrency. Ordered fork pick-up (Dijkstra's solution) is good. Amazon wants `tryLock()` because it mirrors DynamoDB's conditional updates.
- **Java Code:**
```java
import java.util.concurrent.locks.ReentrantLock;

/**
 * Dining Philosophers with deadlock-free eating using tryLock.
 * Amazon wants this because it mirrors DynamoDB's optimistic locking.
 *
 * LC 1226 — Amazon tests deadlock prevention strategies
 */
public class DiningPhilosophers {

    private final ReentrantLock[] forks;

    /**
     * Creates 5 forks — one per philosopher.
     */
    public DiningPhilosophers() {
        forks = new ReentrantLock[5];
        for (int i = 0; i < 5; i++) {
            forks[i] = new ReentrantLock();
        }
    }

    /**
     * Philosopher picks up left then right fork using tryLock.
     * Falls back to releasing both if either unavailable — no deadlock.
     *
     * @param philosopher 0-based philosopher index
     * @param pickLeft     runnable for picking left fork
     * @param pickRight    runnable for picking right fork
     * @param eat          runnable for eating
     * @param putLeft      runnable for putting left fork
     * @param putRight     runnable for putting right fork
     * @throws InterruptedException on interruption
     */
    public void wantsToEat(
        int philosopher,
        Runnable pickLeft,
        Runnable pickRight,
        Runnable eat,
        Runnable putLeft,
        Runnable putRight
    ) throws InterruptedException {
        int left  = philosopher;
        int right = (philosopher + 1) % 5;

        while (true) {
            if (forks[left].tryLock()) {
                try {
                    if (forks[right].tryLock()) {
                        try {
                            pickLeft.run();
                            pickRight.run();
                            eat.run();
                            putLeft.run();
                            putRight.run();
                            return;
                        } finally {
                            forks[right].unlock();
                        }
                    }
                } finally {
                    if (forks[left].isHeldByCurrentThread()) {
                        forks[left].unlock();
                    }
                }
            }
            // Optional: sleep before retry to avoid busy-waiting
            Thread.sleep(1);
        }
    }
}
```
- **Company Evaluation Criteria:** Deadlock avoidance, resource ordering, retry strategy.
- **Follow-ups:** What about livelock? How does this relate to DynamoDB's conditional updates?

---

### Module: Object-Oriented Design

#### Problem: Design a Parking Lot
- **LC:** 1603 (design-parking-system)
- **Difficulty/Frequency:** Medium / Very High
- **Problem Statement:** Design a parking lot with multiple levels, spot types, and ticketing.
- **Interview Walkthrough:** Amazon OOP design is the hardest in FAANG. They expect `abstract class Vehicle`, `ParkingSpot` with size, `Level` managing spots, `ParkingLot` facade. Use enums, inheritance, composition.
- **Solution 1 vs Solution 2:** Simple parking garage vs multi-level with EV charging, handicapped, compact. Amazon wants the extensible version — discuss how you'd add valet or hourly pricing.
- **Java Code:**
```java
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Parking lot design with OOP patterns — Amazon OOP round.
 * Demonstrates inheritance, composition, enum usage, strategy pattern for pricing.
 */
public class ParkingLot {

    enum VehicleType { MOTORCYCLE, CAR, TRUCK }
    enum SpotSize   { SMALL, COMPACT, LARGE }

    static abstract class Vehicle {
        final String licensePlate;
        final VehicleType type;
        Vehicle(String plate, VehicleType type) {
            this.licensePlate = plate;
            this.type = type;
        }
    }

    static class Car extends Vehicle {
        Car(String plate) { super(plate, VehicleType.CAR); }
    }

    static class Truck extends Vehicle {
        Truck(String plate) { super(plate, VehicleType.TRUCK); }
    }

    static class ParkingSpot {
        final String id;
        final SpotSize size;
        Vehicle parked;

        ParkingSpot(String id, SpotSize size) {
            this.id = id;
            this.size = size;
        }

        boolean canFit(Vehicle v) {
            return parked == null && size.ordinal() >= getRequiredSize(v).ordinal();
        }

        boolean park(Vehicle v) {
            if (!canFit(v)) return false;
            parked = v;
            return true;
        }

        Vehicle leave() {
            Vehicle v = parked;
            parked = null;
            return v;
        }

        private SpotSize getRequiredSize(Vehicle v) {
            return switch (v.type) {
                case MOTORCYCLE -> SpotSize.SMALL;
                case CAR        -> SpotSize.COMPACT;
                case TRUCK      -> SpotSize.LARGE;
            };
        }
    }

    static class Level {
        final int floor;
        final Map<SpotSize, PriorityQueue<ParkingSpot>> available = new HashMap<>();
        final Map<String, ParkingSpot> used = new HashMap<>();

        Level(int floor, int spotsPerSize) {
            this.floor = floor;
            for (SpotSize s : SpotSize.values()) {
                available.put(s, new PriorityQueue<>((a, b) -> a.id.compareTo(b.id)));
                for (int i = 0; i < spotsPerSize; i++) {
                    available.get(s).offer(new ParkingSpot(floor + "-" + s + "-" + i, s));
                }
            }
        }

        boolean park(Vehicle v) {
            SpotSize need = getSizeFor(v);
            for (SpotSize s : SpotSize.values()) {
                if (s.ordinal() >= need.ordinal() && !available.get(s).isEmpty()) {
                    ParkingSpot spot = available.get(s).poll();
                    spot.park(v);
                    used.put(v.licensePlate, spot);
                    return true;
                }
            }
            return false;
        }

        Vehicle leave(String plate) {
            ParkingSpot spot = used.remove(plate);
            if (spot == null) return null;
            Vehicle v = spot.leave();
            available.get(spot.size).offer(spot);
            return v;
        }

        private SpotSize getSizeFor(Vehicle v) {
            return switch (v.type) {
                case MOTORCYCLE -> SpotSize.SMALL;
                case CAR        -> SpotSize.COMPACT;
                case TRUCK      -> SpotSize.LARGE;
            };
        }
    }

    private final Level[] levels;

    public ParkingLot(int numLevels, int spotsPerLevel) {
        levels = new Level[numLevels];
        for (int i = 0; i < numLevels; i++) {
            levels[i] = new Level(i, spotsPerLevel);
        }
    }

    public boolean park(Vehicle v) {
        for (Level l : levels) {
            if (l.park(v)) return true;
        }
        return false;
    }

    public Vehicle leave(String plate) {
        for (Level l : levels) {
            Vehicle v = l.leave(plate);
            if (v != null) return v;
        }
        return null;
    }
}
```
- **Company Evaluation Criteria:** Inheritance hierarchy, enum usage for extensibility, composition over inheritance, separation of concerns.
- **Follow-ups:** Add pricing strategy; add valet service; how do you handle peak hours?

#### Problem: Design Amazon Locker
- **System Design & OOP**
- **Difficulty/Frequency:** Medium / High
- **Problem Statement:** Design a package locker system like Amazon Hub.
- **Interview Walkthrough:** `Locker` has size and location. `Package` has dimensions. `LockerService` assigns nearest available locker. Use `Strategy` pattern for assignment. Amazon loves this because it's their actual system.
- **Java Code:**
```java
import java.time.LocalDateTime;
import java.util.*;

/**
 * Amazon Locker (Hub) system — OOP design with strategy pattern.
 * Amazon's own interview question based on their product.
 */
public class LockerSystem {

    enum LockerSize { SMALL, MEDIUM, LARGE }

    static class Package {
        final String id;
        final LockerSize size;
        final String customerId;
        LocalDateTime placedAt;

        Package(String id, LockerSize size, String customerId) {
            this.id = id;
            this.size = size;
            this.customerId = customerId;
        }
    }

    static class Locker {
        final String lockerId;
        final LockerSize size;
        final String location;
        Package current;
        LocalDateTime expiresAt;

        Locker(String lockerId, LockerSize size, String location) {
            this.lockerId = lockerId;
            this.size = size;
            this.location = location;
        }

        boolean isAvailable() { return current == null; }

        boolean assign(Package pkg) {
            if (!isAvailable() || size.ordinal() < pkg.size.ordinal()) return false;
            this.current = pkg;
            this.expiresAt = LocalDateTime.now().plusDays(3);
            return true;
        }

        Package pickup() {
            Package pkg = current;
            current = null;
            expiresAt = null;
            return pkg;
        }
    }

    static class LockerAssignmentStrategy {
        Locker findBest(List<Locker> candidates, Package pkg) {
            return candidates.stream()
                .filter(Locker::isAvailable)
                .filter(l -> l.size.ordinal() >= pkg.size.ordinal())
                .min(Comparator.comparingInt(l -> l.size.ordinal()))
                .orElse(null);
        }
    }

    private final List<Locker> lockers;
    private final LockerAssignmentStrategy strategy;

    public LockerSystem(List<Locker> lockers, LockerAssignmentStrategy strategy) {
        this.lockers = lockers;
        this.strategy = strategy;
    }

    public Locker assignLocker(Package pkg) {
        Locker best = strategy.findBest(lockers, pkg);
        if (best != null) best.assign(pkg);
        return best;
    }

    public Package pickup(String lockerId) {
        return lockers.stream()
            .filter(l -> l.lockerId.equals(lockerId))
            .findFirst()
            .map(Locker::pickup)
            .orElse(null);
    }
}
```
- **Company Evaluation Criteria:** OOP design, strategy pattern, real-world thinking (3-day expiry, size fitting).
- **Follow-ups:** Handle overflow; notification system; return logistics.

---

### Module: Collections & Streams

#### Problem: Top K Frequent Words
- **LC:** 692
- **Difficulty/Frequency:** Medium / High
- **Problem Statement:** Return top K most frequent words in order of frequency (tie-break by lexicographical order).
- **Interview Walkthrough:** Amazon asks this for data-heavy roles. `HashMap` for counts, then `PriorityQueue` with custom comparator. Or Java streams with `groupingBy` + `sorted`.
- **Solution 1 vs Solution 2:** Min-heap (O(n log k)) is interview standard. Java Stream solution is elegant but less efficient. Amazon expects the heap version — they want to see you choose the right collection.
- **Java Code:**
```java
import java.util.*;
import java.util.stream.*;

/**
 * Returns top K frequent words using min-heap.
 * Amazon tests comparator writing and PriorityQueue usage.
 *
 * LC 692 — Amazon SDE interview standard
 */
public class TopKFrequentWords {

    /**
     * Returns top K frequent words from array.
     * @param words input word array
     * @param k number of top words to return
     * @return list of K most frequent words (tie-break: lexicographical)
     */
    public List<String> topKFrequent(String[] words, int k) {
        Map<String, Long> freq = Arrays.stream(words)
            .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        PriorityQueue<String> heap = new PriorityQueue<>(
            (a, b) -> {
                int cmp = Long.compare(freq.get(a), freq.get(b));
                return cmp != 0 ? cmp : b.compareTo(a); // reverse alpha for min-heap
            }
        );

        for (String word : freq.keySet()) {
            heap.offer(word);
            if (heap.size() > k) heap.poll();
        }

        List<String> result = new ArrayList<>();
        while (!heap.isEmpty()) result.add(heap.poll());
        Collections.reverse(result);
        return result;
    }
}
```
- **Company Evaluation Criteria:** Correct comparator (tie-break), min-heap efficiency, stream fluency.
- **Follow-ups:** What if data doesn't fit in memory? Use external sort + map-reduce.

---

### Module: Recursion & Backtracking

#### Problem: Generate Parentheses
- **LC:** 22
- **Difficulty/Frequency:** Medium / High
- **Problem Statement:** Generate all well-formed parentheses pairs for n pairs.
- **Interview Walkthrough:** Amazon loves this for recursion + pruning. Start with `String` concatenation, then discuss `StringBuilder` backtracking. Amazon interviewers probe "how does the JVM handle this recursion?"
- **Java Code:**
```java
import java.util.ArrayList;
import java.util.List;

/**
 * Generates all valid parentheses combinations using backtracking.
 * Amazon tests recursion depth understanding and JVM stack limits.
 *
 * LC 22 — Amazon recursion interview question
 */
public class GenerateParentheses {

    /**
     * @param n number of parenthesis pairs
     * @return list of all valid combinations
     */
    public List<String> generateParenthesis(int n) {
        List<String> result = new ArrayList<>();
        backtrack(result, new StringBuilder(), 0, 0, n);
        return result;
    }

    private void backtrack(List<String> result, StringBuilder sb, int open, int close, int max) {
        if (sb.length() == max * 2) {
            result.add(sb.toString());
            return;
        }
        if (open < max) {
            sb.append('(');
            backtrack(result, sb, open + 1, close, max);
            sb.deleteCharAt(sb.length() - 1);
        }
        if (close < open) {
            sb.append(')');
            backtrack(result, sb, open, close + 1, max);
            sb.deleteCharAt(sb.length() - 1);
        }
    }
}
```
- **Company Evaluation Criteria:** Branching logic, StringBuilder mutation avoids O(n^2) copying, recursion termination.
- **Follow-ups:** Iterative solution using stack; what is max n before stack overflow?

---

### Module: Performance & Profiling

#### Problem: Design Logger Rate Limiter
- **LC:** 359
- **Difficulty/Frequency:** Easy / Medium
- **Problem Statement:** Design a logger that throttles messages — same message printed at most once every 10 seconds.
- **Interview Walkthrough:** Amazon asks this for high-throughput systems. `HashMap<String, Integer>` tracking last printed timestamp. Amazon extends this to distributed rate limiting with Redis.
- **Solution 1 vs Solution 2:** HashMap (simple, memory grows). Sliding window with `ConcurrentHashMap` (thread-safe). Amazon wants the concurrent version.
- **Java Code:**
```java
import java.util.concurrent.ConcurrentHashMap;

/**
 * Logger rate limiter — throttles messages to once per 10 seconds.
 * Amazon extends to distributed scenario with Redis discussion.
 *
 * LC 359 — Amazon system design warm-up
 */
public class Logger {

    private final ConcurrentHashMap<String, Integer> lastPrinted;
    private static final int LIMIT_MS = 10000;

    /** Initialize logger with empty history. */
    public Logger() {
        this.lastPrinted = new ConcurrentHashMap<>();
    }

    /**
     * Returns true if message should be printed, false if throttled.
     * @param timestamp current timestamp in seconds
     * @param message   log message
     * @return true if message should be logged
     */
    public boolean shouldPrintMessage(int timestamp, String message) {
        Integer last = lastPrinted.get(message);
        if (last == null || timestamp - last >= LIMIT_MS / 1000) {
            lastPrinted.put(message, timestamp);
            return true;
        }
        return false;
    }
}
```
- **Company Evaluation Criteria:** Thread safety, memory management, time window logic.
- **Follow-ups:** Distributed version with Redis; what if messages arrive out of order?

---

## JVM/Concurrency Deep Dive Questions

Amazon's Java questions focus on **production concurrency**:

1. **How does ConcurrentHashMap achieve thread safety?** — Walk through JDK 8+ implementation: `CAS` on inserts, `synchronized` on bin for collisions, `size()` uses `LongAdder`-like counter. Amazon expects you to know "why using `synchronized` instead of `ReentrantLock` in JDK 8?"
2. **What happens when ThreadPoolExecutor queue is full?** — Walk through `corePoolSize`, `maxPoolSize`, `workQueue`, `RejectedExecutionHandler`. Amazon will ask "what happens at AWS scale when a 'submit' call blocks?"
3. **Explain the Java Memory Model** — "We had a bug where `volatile` didn't fix it. Why?" Tests understanding of happens-before for non-volatile fields read after volatile.
4. **How would you debug a JVM that uses 300% CPU?** — `jstack`, `jcmd`, `async-profiler`, thread dump analysis. Amazon expects you to know AWS's own thread analysis tools.
5. **What are the dangers of `Thread.stop()`?** — Amazon actually found a JVM bug with this in production.

## System Design with Java

1. **Design Amazon Cart service** — High-concurrency, eventually consistent. Java `ConcurrentHashMap` for cart state, DynamoDB for persistence. Discuss `StampedLock` for read-optimistic access patterns.
2. **Design URL shortener (like TinyURL)** — Java `HashMap` or `Redis` for mapping. Discuss base-62 encoding, thread-safe ID generation using `AtomicLong`.
3. **Design Amazon Recommendation Engine** — Java stream pipelines for filtering/cross-referencing. Discuss `ForkJoinPool` for parallel computation.

## Behavioral Questions (STAR)

Amazon's **Leadership Principles** are embedded in every round. Every answer must cite an LP.

1. **"Tell me about a time you delivered results under tight deadline." (Deliver Results)** — *S: Database migration needed in 2 weeks due to EOL. T: Zero downtime migration. A: Used dual-write pattern with `ThreadPoolExecutor` — wrote to both DBs, backfilled using `CompletableFuture` batch jobs. R: Migrated 2TB in 10 days with 99.99% uptime.*
2. **"Tell me about a time you deeply understood a problem before acting." (Dive Deep)** — *S: Production JVM OOM errors were "mysterious." T: Find root cause. A: Analyzed heap dumps with Eclipse MAT, found `ArrayList` leak in producer thread. Tuned `-Xmx` and replaced with bounded `ArrayBlockingQueue`. R: Zero OOM for 3 months.*
3. **"Tell me about disagreeing and committing." (Have Backbone)** — *S: Lead wanted `synchronized` for new service. A: Demonstrated with `jmh` benchmark that `ConcurrentHashMap.computeIfAbsent` was 5x faster. R: Team adopted concurrent collections.*
4. **"Tell me about a time you improved a process." (Invent and Simplify)** — *S: CI tests took 45 minutes. A: Parallelized test execution with JUnit Platform + `ForkJoinPool`. R: 12-minute CI, saved 30 dev-hours/week.*
5. **"Tell me about thinking big." (Think Big)** — *S: Proposed migrating monolith to microservices. A: Designed Java 21 virtual thread-based event bus for async communication. R: 2x throughput, paved path for 10 new services.*

## Study Plan

| Priority | Labs | Focus |
|----------|------|-------|
| P0 | 16-concurrency, 41-threading-deep-dive, 42-locking-synchronization, 48-structured-concurrency | Concurrency |
| P0 | 06-oop-basics, 07-inheritance, 08-polymorphism, 09-abstraction-interfaces | OOP design |
| P1 | 12-collections, 13-streams, 14-lambdas | Collections & streams |
| P1 | 37-performance-profiling, 47-profiling-observability | Performance |
| P2 | 38-memory-model, 45-gc-deep-dive | JVM memory |
| P2 | 31-testing | Testing (less focus) |

**Preparation Path:** Solve LC 1188, 1226, 692, 22, 359 in Java. Re-implement `ConcurrentHashMap` API. Build one OOP design (parking lot, Amazon locker). Practice STAR with LP citations — every response must explicitly name a Leadership Principle.

## Tips

- **Amazon is the most Java-friendly FAANG** — Use Java confidently, especially for OOP design rounds
- **Every answer = STAR + LP** — Explicitly say "This demonstrates Customer Obsession" or "This relates to Dive Deep"
- **Thread safety is assumed** — If your solution isn't thread-safe, your interview is over
- **Fair locks vs non-fair** — Know the difference. Amazon will ask about fairness when discussing `BlockingQueue`
- **You can ask about trade-offs** — "Should I use a fair or non-fair lock here?" shows maturity
- **Bar-raiser round is real** — The bar-raiser doesn't care about your solution; they evaluate whether you meet Amazon's hiring bar. They often ask hardest concurrency question
- **Use AWS analogies** — Mentioning "this is like DynamoDB conditional updates" or "similar to SQS semantics" is a strong signal
- **Prepare for "how would you deploy this?"** — Amazon interviewers frequently ask about production deployment, monitoring, and rollback strategy