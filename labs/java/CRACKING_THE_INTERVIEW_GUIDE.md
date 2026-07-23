# Cracking the Java Interview — Comprehensive Guide

## Table of Contents
1. The Java Interview Process at Each FAANG Company
2. How to Approach Coding Problems in Java
3. System Design with Java
4. Behavioral Questions for Java Roles
5. 30/60/90 Day Study Plans
6. Salary Expectations by Company and Seniority Level
7. Recommended Books

---

## 1. The Java Interview Process at Each FAANG Company

### Google
**Focus areas:** JVM internals, concurrency, design patterns, algorithmic thinking

- **Phone screen (45 min):** 1-2 LeetCode medium/hard problems in Java. Expect questions on `HashMap` collisions, `ConcurrentHashMap` segmentation, `Stream` laziness.
- **Onsite (4-5 rounds):**
  - **Coding (2 rounds):** Algorithmic problems. Must handle edge cases in Java (autoboxing pitfalls, `equals`/`hashCode` contracts).
  - **Java deep-dive (1 round):** JVM memory model, GC algorithms (G1, ZGC), class loading, `synchronized` vs `ReentrantLock`, `volatile` semantics.
  - **System design (1 round):** Design distributed systems — map to Java constructs (thread pools → request handling, `CompletableFuture` → async orchestration).
  - **Googliness (1 round):** Behavioral + leadership.

**Key Java topics:**
- JVM: Heap vs stack, Young/Old/Gen, GC roots, Stop-the-world, Safe points
- Concurrency: `java.util.concurrent` classes, `ForkJoinPool`, `Phaser`, `VarHandle`
- Design patterns in Java: Builder, Factory, Strategy, Observer (with `PropertyChangeListener`)

**Study resources:** Effective Java, Java Concurrency in Practice, Google's Java style guide.

### Microsoft
**Focus areas:** Azure Java, collections, threading, system design

- **Phone screen:** 1 coding problem + Azure/Java architecture questions.
- **Onsite (4-5 rounds):**
  - **Coding (2 rounds):** Problems solvable with Java collections (`HashMap`, `TreeMap`, `PriorityQueue`). Multiple approaches expected.
  - **Design (1-2 rounds):** Design Azure services — map to Java: `ThreadPoolExecutor` for scaling, `Reactive` for event-driven, `Records` for DTOs.
  - **Behavioral + Azure depth (1 round).**

**Key Java topics:**
- `HashMap` internals: load factor, treeification, resize
- `ConcurrentHashMap`: lock striping, `computeIfAbsent`
- Threading: `synchronized`, `Lock`, `Condition`, `Phaser`
- Reactive: Project Reactor, WebFlux

### Amazon
**Focus areas:** Performance, distributed Java, reactive, leadership principles

- **Phone screen:** 1 coding problem with Java + 1 LP (Leadership Principle) round.
- **Onsite (4-5 rounds):**
  - **Coding (2 rounds):** Focus on performance — prefer primitives over objects, avoid unnecessary allocation, use `StringBuilder`, choose `ArrayList` vs `LinkedList` by access pattern.
  - **System design (1-2 rounds):** Design for scale — `CompletableFuture` for orchestration, virtual threads for lightweight concurrency, reactive streams for backpressure.
  - **Bar raiser (1 round):** LPs + design + coding.

**Key Java topics:**
- Performance: JMH benchmarking, escape analysis, lock coarsening, biased locking
- Distributed Java: `CompletableFuture.allOf`, `CountDownLatch`, WAL with Java NIO
- Reactive: `Flux`, `Mono`, backpressure strategies

### Meta (Facebook)
**Focus areas:** Streams, lambdas, functional programming, speed

- **Phone screen:** 1-2 coding problems. Expect clean, functional-style Java.
- **Onsite (4 rounds):**
  - **Coding (2 rounds):** Heavy use of `Stream` API, `Optional`, method references. Prefer functional composition over imperative loops.
  - **System design (1 round):** Real-time systems — reactive streams, event-driven architecture.
  - **Behavioral (1 round):** Meta-specific (Move Fast, Be Bold).

**Key Java topics:**
- Streams: `map`, `filter`, `reduce`, `flatMap`, `collect(Collectors.toUnmodifiableList())`
- Lambdas: effectively final, lambda scoping, serialization
- Functional: `Function`, `Predicate`, `Supplier`, `Consumer` composition
- Records + pattern matching for algebraic data types

### Apple
**Focus areas:** Memory management, immutability, Modern Java (17+)

- **Phone screen:** 1 coding problem focused on correctness and immutability.
- **Onsite (4-5 rounds):**
  - **Coding (2 rounds):** Immutable collections (Java 9+ `List.of()`, `Set.of()`), defensive copies, `record` types.
  - **System design (1 round):** High-performance client-side systems — memory footprint, object layout.
  - **JVM low-level (1 round):** JMM, object header, compressed OOPs, memory barriers.
  - **Behavioral (1 round):** Craftsmanship + detail orientation.

**Key Java topics:**
- `record` vs `class` for data carriers
- `final` semantics, unmodifiable collections, `Collections.unmodifiableList()`
- JMM: happens-before, memory barriers, `volatile` vs `AtomicReference`
- Object layout: mark word, klass pointer, field alignment
- GC: ZGC, Shenandoah for low-pause

### Oracle
**Focus areas:** Java Language Specification, JVM, HotSpot internals

- **Phone screen:** Deep JVM trivia, JLS clauses, HotSpot implementation details.
- **Onsite (5-6 rounds):**
  - **JVM internals (2 rounds):** Bytecode, class file format, verifier, constant pool, invokedynamic.
  - **Language spec (1-2 rounds):** Generics (erasure vs reified), type inference, intersection types, varargs.
  - **Concurrency (1 round):** JMM guarantees, `VarHandle`, `LockSupport`, `StampedLock`.
  - **Design + coding (1 round):** Implement JVM features in pseudocode.

**Key Java topics:**
- Class file format: magic number, version, constant pool, access flags, bytecode instructions
- JIT: C1/C2 compilation, OSR, inlining, deoptimization
- GC: G1, ZGC, Shenandoah — how each works at the algorithm level
- `invokedynamic`, method handles, `LambdaMetaFactory`

---

## 2. How to Approach Coding Problems in Java

### Objects vs Primitives
- Use primitives (`int`, `long`, `double`) for performance-critical loops and algorithms.
- Use objects (`Integer`, `Long`) when nullability is needed or when using with Collections/Generics.
- Be aware of autoboxing overhead — prefer `IntStream` over `Stream<Integer>` for numeric operations.

### Streams vs Loops
- **Streams:** Preferred for readability, parallel execution, declarative pipeline (filter-map-reduce).
- **Loops:** Preferred when performance is critical, when you need `break`/`continue`, or when mutating local variables.
- **Rule of thumb:** If the operation fits a pure functional transformation, use streams. If it's complex stateful logic, use loops.

### Collections: Choose Wisely
- **Lookup by key:** `HashMap` (O(1)), `TreeMap` (O(log n)) — tree for sorted/range queries.
- **Iteration order:** `ArrayList` for insertion-order, `LinkedHashMap` for access-order (LRU cache).
- **Concurrency:** `ConcurrentHashMap`, `CopyOnWriteArrayList`, `ConcurrentLinkedQueue`.
- **Immutable:** `List.of()`, `Set.of()`, `Map.of()` — use for defensive returns.

### Concurrency Patterns
- **CountDownLatch:** One-time barrier (wait for N tasks).
- **CyclicBarrier:** Reusable barrier (all threads meet at a point).
- **Phaser:** Flexible dynamic barrier (add/register parties dynamically).
- **CompletableFuture:** Async pipeline with composition (`thenApply`, `thenCompose`, `allOf`).
- **Virtual threads (Java 21+):** For high-throughput IO-bound tasks — each request gets its own virtual thread.

### Error Handling
- Use `Optional` for nullable returns (avoid `null`).
- Use `Result` pattern (sealed interface + record) for success/failure.
- Prefer checked exceptions for recoverable errors, unchecked for programming errors.

---

## 3. System Design with Java

### Thread Pools → Handling Concurrent Requests at Scale
- `ThreadPoolExecutor` with tuned core/max pool sizes, work queue, rejected execution handler.
- Use `Executors.newFixedThreadPool()` for CPU-bound, `newCachedThreadPool()` for IO-bound.
- Monitor pool stats: `getActiveCount()`, `getQueue().size()`, `getCompletedTaskCount()`.

### Virtual Threads → Lightweight Concurrency Pattern
- Java 21+: `Thread.startVirtualThread(runnable)` or `Executors.newVirtualThreadPerTaskExecutor()`.
- Each request or external call runs in its own virtual thread — millions possible.
- Backed by ForkJoinPool, carrier threads.

### Reactive Streams → Backpressure in Distributed Systems
- `Publisher` / `Subscriber` / `Subscription` — Reactive Streams spec.
- Project Reactor: `Flux` (N elements) and `Mono` (0/1 element).
- Backpressure strategies: `BUFFER`, `DROP`, `LATEST`, `ERROR`.
- Use: microservices communication, event-driven architecture.

### Records → Immutable Data Transfer in Microservices
- Compact data carriers: `public record UserRequest(String name, int age) {}`
- Auto-generates constructor, `equals`, `hashCode`, `toString`, accessors.
- Perfect for DTOs, event payloads, API contracts.

### CompletableFuture → Async Service Orchestration
- Chain async calls: `CompletableFuture.supplyAsync(() -> fetchUser(id)).thenApply(user -> enrich(user)).thenAccept(System.out::println)`
- Wait for all: `CompletableFuture.allOf(f1, f2, f3).join()`
- Error recovery: `exceptionally`, `handle`, `whenComplete`.

### Collections → In-Memory Data Stores
- `HashMap` / `ConcurrentHashMap` as key-value cache.
- `TreeMap` for sorted views, range queries.
- `LinkedHashMap` for LRU cache (override `removeEldestEntry`).
- `PriorityQueue` for task scheduling / priority processing.

### Streams → Batch Processing Pipelines
- `Stream<Transaction>.filter(t -> t.amount() > 100).map(t -> t.toDTO()).collect(toList())`
- `parallelStream()` for CPU-intensive batch operations.
- `Collectors.groupingBy`, `partitioningBy`, `toMap` for aggregation.

---

## 4. Behavioral Questions for Java Roles

### Tell me about a time you improved Java application performance.
**STAR:** Situation (slow service), Task (reduce p99 latency), Action (profiled with JFR + async-profiler, optimized hot methods, reduced allocations, tuned GC settings), Result (p99 from 500ms to 120ms).

### Describe a complex concurrency bug you fixed.
**STAR:** Situation (production deadlock), Task (identify root cause), Action (analyzed thread dumps, used `jstack`, reproduced with `ThreadMXBean`, fixed lock ordering or replaced with `ReentrantLock` + tryLock), Result (no more deadlocks).

### How do you ensure code quality in Java?
- Static analysis (ErrorProne, SpotBugs, Checkstyle)
- Unit tests (JUnit 5, parametrized tests)
- Mutation testing (Pitest)
- Code reviews focused on concurrency correctness

### Conflict with a team member about design.
- Java-specific: argument over checked vs unchecked exceptions, or streams vs loops.
- Resolution: data-driven benchmark, team alignment on coding standards.

---

## 5. 30/60/90 Day Study Plans

### 30-Day Plan (Foundation)
**Week 1:** Data structures — implement `HashMap`, `ArrayList`, `LinkedList`, `TreeMap` in Java.
**Week 2:** Algorithms — sort, search, graph traversals (DFS/BFS), DP patterns in Java.
**Week 3:** Concurrency — `synchronized`, `Lock`, `CompletableFuture`, `ConcurrentHashMap`.
**Week 4:** Mock interviews — 2-3 Java-focused coding rounds.

### 60-Day Plan (Intermediate)
**Weeks 1-2:** Deep dive — JMM, GC algorithms, class loading, bytecode.
**Weeks 3-4:** System design with Java — thread pools, reactive, distributed patterns.
**Weeks 5-6:** Practice — LeetCode medium/hard in Java, time-boxed.
**Weeks 7-8:** Company-specific research, targeted problems.

### 90-Day Plan (Advanced)
**Months 1-2:** All of 60-day plan + build a Java project (e.g., simple key-value store with NIO, virtual threads).
**Month 3:** Full mock loop — 5 onsite rounds per week. Review JVM performance tuning.

---

## 6. Salary Expectations by Company and Seniority Level

| Company  | SDE I / New Grad | SDE II / Mid | Senior (5-8 yrs) | Staff (8-12 yrs) |
|----------|-----------------|--------------|-------------------|-------------------|
| Google   | $160-190k       | $220-280k    | $350-450k         | $500-700k         |
| Microsoft| $140-170k       | $190-250k    | $300-400k         | $450-600k         |
| Amazon   | $150-180k       | $200-260k    | $320-420k         | $480-650k         |
| Meta     | $170-200k       | $230-300k    | $380-500k         | $550-800k         |
| Apple    | $155-185k       | $210-270k    | $340-440k         | $490-680k         |
| Oracle   | $130-160k       | $175-230k    | $260-350k         | $380-520k         |

*Note: TC = base + bonus + RSU. Varies by location (Bay Area premiums ~20-30%).*

---

## 7. Recommended Books

### Core Java
- **Effective Java (3rd Edition)** — Joshua Bloch. *The* Java bible. 90 items on best practices.
- **Java Concurrency in Practice** — Brian Goetz. The definitive concurrency reference.
- **Modern Java in Action** — Raoul-Gabriel Urma. Java 8-17, streams, optionals, reactive.

### JVM Internals
- **The Java Virtual Machine Specification (Java SE 17 Edition)** — Oracle. The spec itself.
- **Optimizing Java** — Benjamin J Evans. JIT, GC, performance tuning.
- **Java Performance: In-Depth Advice** — Scott Oaks. Practical JVM tuning.

### System Design
- **Designing Data-Intensive Applications** — Martin Kleppmann. Not Java-specific but essential.
- **Building Microservices** — Sam Newman. Service design with Java.

### Interview Prep
- **Cracking the Coding Interview** — Gayle Laakmann McDowell. General approach, Java section.
- **Grokking the Coding Interview** — Educative. Pattern-based problem solving.
- **Ace the Java Coding Interview** — Ranga Karanam. Java-specific problems.

---

*Last updated: July 2026. For the latest interview patterns, cross-reference with `INTERVIEW_CHEATSHEET.md` and module-level interview guides.*