# Company-Specific Concurrency Interview Guide

> How specific companies test concurrency — patterns, depth, and preparation strategies

---

## Google (5-7 Rounds)

### Depth: Very Deep (9/10)

Google interviews probe concurrency at the deepest level — expect JMM minutiae, collection internals, and lock-free algorithms.

### What They Ask

**Round 1-2 (Coding):**
- Design a thread-safe data structure (LRU cache, concurrent hash map, bounded queue)
- Implement a lock-free stack or queue
- Deadlock-free resource allocation

**Round 3-4 (Systems/Design):**
- Design a distributed rate limiter
- Design a highly concurrent job scheduler
- Concurrency architecture for a real-time auction system

**Round 5 (Deep Dive/JMM):**

Common questions:
- "Explain happens-before for: volatile write + volatile read + synchronized block"
- "Can reordering break double-checked locking? Prove it."
- "How does ConcurrentHashMap resize in Java 8? Show the multi-threaded transfer."
- "What happens if you remove `volatile` from DCL singleton?"

**Sample conversation:**
```
Interviewer: "Your ConcurrentHashMap has 16 segments. How many concurrent writers?"
You: "In Java 7, up to 16 — one per segment. In Java 8, it's bucket-level, so up to the number of buckets."
Interviewer: "And map.size()? Is it consistent?"
You: "No — it's an estimate. In Java 8, sumCount() uses CounterCell[] with contention; it's eventually consistent."
```

### Evaluation Criteria

| Criterion | Weight | What They Watch |
|-----------|--------|----------------|
| JMM understanding | 35% | Can you prove your code is correct using happens-before? |
| Lock-free expertise | 30% | Do you know CAS, ABA, memory ordering? |
| Code correctness | 25% | Race conditions, visibility, atomicity |
| Communication | 10% | Can you teach the interviewer? |

### Preparation Strategy

```
Priority 1: JCIP Chapters 3, 5, 11, 15, 16 (Memory Model, Collections, Lock-free)
Priority 2: ConcurrentHashMap source code (Java 8 vs 17)
Priority 3: Treiber stack, Michael-Scott queue implementation
Priority 4: VarHandle, memory barriers (loadLoad, storeStore, etc.)
Priority 5: Virtual threads internals (continuation, pinning)
```

**Key books:** *Java Concurrency in Practice* (Goetz), *The Art of Multiprocessor Programming* (Herlihy)

---

## Amazon (4-5 Rounds)

### Depth: Practical-Deep (8/10)

Amazon focuses on real-world concurrency at scale — thread pool tuning, producer-consumer patterns, and solving operational concurrency problems.

### What They Ask

**Round 1-2 (Coding):**
- Thread-safe singleton (all approaches, evaluate trade-offs)
- Bounded blocking queue with multiple producers/consumers
- Design a connection pool

**Round 2-3 (Design/OOP):**
- "Design a highly concurrent job processing system"
- "How would you architect a system handling 100K requests/sec?"
- "Design ThreadPoolExecutor from scratch"

**Round 3-4 (Bar Raiser — Deep Dive):**

Common questions:
- "You have a service doing 10K req/s with 100ms average latency. How many threads?"
  - *Expected: Calculate Little's Law: N = throughput × latency = 10,000 × 0.1 = 1,000 threads*
- "Your thread pool is rejecting tasks. What do you do?"
- "What happens when a thread pool queue is full? Which handler do you choose?"
- "How do you handle thread pool saturation gracefully?"

**LP (Leadership Principles) concurrency angle:**
- **Dive Deep**: "What happens at the CPU level during a CAS operation?"
- **Deliver Results**: "How would you tune a thread pool for a 20ms SLA?"

### Evaluation Criteria

| Criterion | Weight | What They Watch |
|-----------|--------|----------------|
| Practical tuning | 35% | Can you reason about pool size, queue depth, rejection strategies? |
| Correctness | 30% | Thread safety in concurrent code |
| Scalability thinking | 25% | Can your design handle 10x load? |
| Leadership principles | 10% | Connecting concurrency choices to customer impact |

### Preparation Strategy

```
Priority 1: ThreadPoolExecutor, Executors, ThreadFactory — all parameters
Priority 2: BlockingQueue implementations (Array, Linked, Synchronous, Transfer)
Priority 3: Little's Law, Amdahl's Law, Universal Scalability Law
Priority 4: Producer-consumer patterns (multiple queues, priority queues)
Priority 5: Circuit breaker, bulkhead patterns (like Hystrix)
```

**Practice problems:**
- Implement a thread pool with core/max/keepAlive
- Implement a rate limiter (token bucket + sliding window)
- Connection pool with timeout, validation, leak detection

---

## Meta (4 Rounds)

### Depth: Lock-Free Heavy (8/10)

Meta tests lock-free data structures intensively. Expect to implement CAS-based algorithms under time pressure.

### What They Ask

**Round 1-2 (Coding):**
- Implement a lock-free stack (Treiber)
- Implement a lock-free queue (Michael-Scott)
- Non-blocking counter with AtomicReference
- Concurrent LRU cache

**Round 2-3 (Concurrency Design):**
- "Design a concurrent in-memory cache"
- "How would you optimize a contended data structure?"
- "Compare synchronized, ReentrantLock, and CAS for different workloads"

**Round 3-4 (Performance/Optimization):**

Common questions:
- "What is false sharing? How do you detect and fix it?"
  - *Expected: @Contended, cache line padding, alignment*
- "Show me the ABA problem in a lock-free stack. How do you fix it?"
  - *Expected: AtomicStampedReference, double-wide CAS*
- "Compare CAS, FAA (fetch-and-add), and LL/SC semantics"

### Evaluation Criteria

| Criterion | Weight | What They Watch |
|-----------|--------|----------------|
| Lock-free algorithms | 40% | Do you understand CAS, ABA, memory ordering? |
| Performance awareness | 30% | Do you know about cache contention, false sharing? |
| Code correctness | 20% | Are there subtle race conditions? |
| Communication | 10% | Can you explain the algorithm's correctness? |

### Preparation Strategy

```
Priority 1: Treiber stack, Michael-Scott queue — implement from memory
Priority 2: Atomic classes (AtomicReference, AtomicStampedReference, AtomicIntegerFieldUpdater)
Priority 3: False sharing, cache coherence protocols (MESI/MESIF)
Priority 4: VarHandle, Unsafe (limits of each)
Priority 5: Lock-free hash set, skip list basics
```

**Meta-specific pattern:** Expect to write lock-free code on a whiteboard. Practice writing CAS loops without IDE help.

---

## Microsoft (4-5 Rounds)

### Depth: Comparative (7/10)

Microsoft often compares Java concurrency with C# async/await and .NET threading. Expect cross-language questions.

### What They Ask

**Round 1-2 (Coding):**
- Readers-writers problem (all preference variations)
- Producer-consumer with multiple queues
- Implement async equivalent in Java

**Round 2-3 (Concurrency Design):**
- "Compare Java synchronized vs C# lock statement"
- "How does Java volatile compare to C# volatile?"
- "Design a concurrent priority queue"

**Round 3-4 (System Design + Concurrency):**
- "How does async/await compare to CompletableFuture?"
- "Design a real-time collaborative editing system (like Google Docs)"
- "How would you handle conflict resolution in a concurrent system?"

**Sample conversation:**
```
Interviewer: "In C#, you write 'await httpClient.GetStringAsync(url)'. In Java, what's the equivalent?"
You: "CompletableFuture.supplyAsync(() -> client.get(url)).thenApply(response ...). In Java 21+, with virtual threads, it's simply: client.get(url) — no explicit async needed."
Interviewer: "Are they equivalent at the thread level?"
You: "Not exactly. await in C# captures the synchronization context and resumes on the captured context. CompletableFuture resumes on the common ForkJoinPool. Virtual threads resume on any available carrier thread."
```

### Evaluation Criteria

| Criterion | Weight | What They Watch |
|-----------|--------|----------------|
| Cross-platform knowledge | 30% | Can you compare Java/C#/C++ concurrency models? |
| Async patterns | 30% | Do you understand continuations, promises, tasks? |
| Correctness | 25% | Thread safety in shared-memory scenarios |
| Design | 15% | Can you design concurrent systems? |

### Preparation Strategy

```
Priority 1: C# async/await, Task, Task<T>, Parallel.ForEach
Priority 2: Continuations, promise monad patterns
Priority 3: Readers-writers (all 3 preference variants)
Priority 4: Concurrent collections (Java vs .NET equivalents)
Priority 5: LMAX Disruptor pattern (Microsoft Azure uses similar patterns)
```

---

## Oracle (5 Rounds)

### Depth: JVM-Level Deep (10/10)

Oracle tests concurrency at the JVM level — expect VarHandle, memory barriers, concurrent GC interaction, and JCStress.

### What They Ask

**Round 1-2 (JMM):**
- "Explain the happens-before edges for all synchronizers"
- "How does the JIT use memory barriers? Show examples."
- "What is sequential consistency? Where is it violated?"

**Round 2-3 (JVM Internals):**
- "How does VarHandle.compareAndSet work at the JVM level?"
- "Explain biased locking — why was it removed?"
- "How does the GC interact with concurrent data structures?"

**Round 3-4 (Lock-Free):**
- "Implement a wait-free queue (not lock-free)"
- "How does VarHandle differ from Unsafe?"
- "Design a lock-free hash table"

**Round 5 (Research/Advanced):**
- "What transactional memory proposals exist for Java?"
- "How would you implement software transactional memory for the JVM?"

### Sample conversation:
```
Interviewer: "How does the JIT emit memory barriers for volatile?"
You: "The JIT uses the cmpxchg instruction on x86 (which implies a full barrier), or explicit mfence/lfence on weaker architectures. For StoreLoad, it inserts a full barrier. For StoreStore/LoadLoad, x86 doesn't need them due to TSO. On ARM, it inserts dmb instructions."

Interviewer: "Show me the JCStress test for this."
You: [writes JCStress test class with @Actor, @Outcome annotations]
```

### Evaluation Criteria

| Criterion | Weight | What They Watch |
|-----------|--------|----------------|
| JVM internals | 40% | Do you know how concurrency is implemented at the VM level? |
| Lock-free theory | 30% | Can you prove correctness of wait-free algorithms? |
| Tooling | 20% | Do you know JCStress, jstack, perf, async-profiler? |
| Research knowledge | 10% | Are you aware of current JVM concurrency research? |

### Preparation Strategy

```
Priority 1: VarHandle (full API), Unsafe (historical context)
Priority 2: JCStress — write tests to prove happens-before edges
Priority 3: AQS (AbstractQueuedSynchronizer) internals (CLH queue, state management)
Priority 4: CPU cache coherence: MESI/MOESI, write buffer, store forwarding
Priority 5: Transactional memory (HTM on Intel, STM research)
```

**Resources:** *JVM Anatomy Park* (Shipilëv), *The JSR-133 Cookbook*, OpenJDK source code walkthroughs

---

## Netflix (4 Rounds)

### Depth: Reactive-Heavy (7/10)

Netflix focuses on reactive concurrency, bulkheading, and Hystrix patterns.

### What They Ask

**Round 1-2 (Reactive Programming):**
- "Explain Hystrix thread pool isolation"
- "How does RxJava's Scheduler work?"
- "What is backpressure and how does Hystrix handle it?"

**Round 2-3 (Concurrency Design):**
- "Design a bulkhead pattern using thread pools"
- "How would you migrate from RxJava to virtual threads?"
- "What is the thread-per-core model and its advantages?"
- "Design a circuit breaker"

**Round 3-4 (Production Concurrency):**
- "How do you debug thread pool exhaustion in production?"
- "Describe a production incident involving concurrency"
- "Thread pool per dependency vs shared thread pool — trade-offs"

### Sample conversation:
```
Candidate: "For our new service, I'd recommend virtual threads over reactive."
Interviewer: "Why? We've used RxJava for years."
Candidate: "Because this service has complex business logic with multiple sequential I/O calls. With virtual threads, we write straightforward code, debug it easily, and get similar throughput. Reactive would require wrapping everything in Mono/FlatMap chains. We keep reactive for our streaming pipelines where backpressure is critical."
Interviewer: "What about the Hystrix-style bulkhead?"
Candidate: "StructuredTaskScope.ShutdownOnFailure provides natural bulkheading — each downstream call gets its own scope. We can also use Semaphore-based bulkheads for non-I/O resources."
```

### Evaluation Criteria

| Criterion | Weight | What They Watch |
|-----------|--------|----------------|
| Reactive understanding | 35% | Do you really understand reactive? Or just know the API? |
| Resilience patterns | 30% | Bulkhead, circuit breaker, retry, fallback |
| Production mindset | 25% | Can you debug concurrency issues in production? |
| Migration judgment | 10% | When to keep reactive vs move to virtual threads |

### Preparation Strategy

```
Priority 1: Hystrix architecture — thread pool isolation vs semaphore isolation
Priority 2: RxJava/RxScala — Schedulers, Observable, Flowable, backpressure strategies
Priority 3: Project Reactor — Mono, Flux, Schedulers, ParallelFlux
Priority 4: Resilience4j — circuit breaker, rate limiter, bulkhead, retry
Priority 5: Concurrency debugging — async-profiler, HDR histogram, thread dump analysis
```

---

## Apple (4 Rounds)

### Depth: Systems-Level (7/10)

Apple focuses on GCD (Grand Central Dispatch) vs Java concurrency, lock-free performance, and power-aware concurrency.

### What They Ask

**Round 1-2 (Coding):**
- Lock-free data structures
- Concurrent dispatch patterns (serial queues vs concurrent queues)
- Power-efficient synchronization

**Round 2-3 (Comparative):**
- "Compare GCD dispatch queues with Java's ExecutorService"
- "How would you implement GCD's dispatch_sync in Java?"
- "What is dispatch barrier and its Java equivalent?"

**Round 3-4 (Performance):**
- "How does power-aware scheduling differ between iOS/server Java?"
- "Design a power-efficient concurrent cache"
- "How does CPU frequency scaling affect lock-free performance?"

### Evaluation Criteria

| Criterion | Weight | What They Watch |
|-----------|--------|----------------|
| Multi-platform knowledge | 30% | Java + Swift/Objective-C concurrency |
| Performance optimization | 30% | Power-aware, cache-friendly concurrency |
| Lock-free algorithms | 25% | Can you implement GC-friendly lock-free structures? |
| System-level thinking | 15% | How hardware affects concurrent performance |

### Preparation Strategy

```
Priority 1: GCD — dispatch queues, groups, semaphores, barriers, sources
Priority 2: RunLoop, CFRunLoop, how Cocoa concurrency works
Priority 3: Power-aware programming (race to idle, CPU sleep states)
Priority 4: Cache-friendly data structures (false sharing awareness)
Priority 5: Java concurrent migration patterns (how to map GCD patterns to Java)
```

---

## Quick Reference: Company-Specific Focus Areas

| Company | Concurrency Depth | Key Focus | Prep Time |
|---------|------------------|-----------|-----------|
| Google | 9/10 | JMM, lock-free, ConcurrentHashMap internals | 60-80 hrs |
| Amazon | 8/10 | Thread pools, producer-consumer, tuning | 40-60 hrs |
| Meta | 8/10 | Lock-free algorithms, CAS, ABA | 50-70 hrs |
| Microsoft | 7/10 | Async/await comparison, readers-writers | 30-50 hrs |
| Oracle | 10/10 | JVM internals, VarHandle, AQS, JCStress | 80-100 hrs |
| Netflix | 7/10 | Reactive, Hystrix, bulkhead, backpressure | 40-60 hrs |
| Apple | 7/10 | GCD comparison, power-aware, lock-free | 30-50 hrs |

---

## General Preparation Timeline

**4-8 weeks before interviews:**

| Week | Focus | Exercises |
|------|-------|-----------|
| 1 | Foundations | JMM, happens-before, volatile, synchronized |
| 2 | Collections | ConcurrentHashMap, CopyOnWriteArrayList, BlockingQueue |
| 3 | Executors | ThreadPoolExecutor, ForkJoinPool, CompletableFuture |
| 4 | Lock-free | Treiber stack, Michael-Scott queue, CAS patterns |
| 5 | Virtual Threads | VT internals, pinning, StructuredTaskScope, ScopedValues |
| 6 | Company-specific | Target company's focus areas |
| 7 | Mock interviews | Whiteboard coding, timed problems |
| 8 | Review | Weak areas, JCStress tests, production debugging |

---

*Note: Interview patterns evolve. Check recent Glassdoor/Levels.fyi reports for the latest question trends before your interview cycle.*
