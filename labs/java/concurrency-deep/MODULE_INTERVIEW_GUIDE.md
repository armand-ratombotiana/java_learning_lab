# Concurrency Deep Dive — Module Interview Guide

## Company-Specific Questions

### Google
- "Walk through the full lifecycle of a thread in the JVM. What happens at each state transition?"
- "How does synchronized work at the bytecode and assembly level? Explain the lock word transitions."
- "Design a lock-free queue. What CAS operations are needed? How do you avoid ABA?"
- "Explain the ForkJoinPool work-stealing algorithm. How does it differ from work-sharing?"

### Amazon
- "Design a distributed counter that survives node failures. How do you handle contention at scale?"
- "How would you implement a thread pool that dynamically scales based on queue depth?"
- "Explain CompletableFuture's internal completion chain. How does thread hop work?"

### Meta
- "Implement a read-write lock. When should readers be preferred vs writers?"
- "How does volatile interact with the CPU cache coherency protocol (MESI/MESIF)?"
- "What is false sharing? How does it affect multi-threaded performance? Show padding techniques."

### Apple
- "How does structured concurrency improve resource management over raw threads?"
- "Implement a cancellation mechanism for a long-running computation without using volatile boolean."

### Microsoft
- "Compare Java threading with C# Task Parallel Library. How does Monito.Enter differ from synchronized?"
- "How would you thread Java code to use Windows I/O Completion Ports?"

### Oracle
- "Explain the AbstractQueuedSynchronizer (AQS) framework. Which JDK classes are built on it?"
- "How is Thread.sleep() implemented at the OS level? What about Object.wait()?"

## LeetCode Problems

| Problem | Concept | Concurrency Insight |
|---------|---------|-------------------|
| 1114 Print in Order | CountDownLatch | Thread ordering without sleeps |
| 1115 Print FooBar Alternately | Semaphore | Alternating execution |
| 1116 Print Zero Even Odd | Condition variables | State-dependent signaling |
| 1117 Building H2O | Barrier | Multi-thread rendezvous |
| 1226 Dining Philosophers | Deadlock prevention | Resource hierarchy, tryLock |
| 1195 Fizz Buzz Multithreaded | Thread coordination | Lock-free with atomics |
| 1242 Web Crawler Multithreaded | ThreadPool | Concurrent BFS with visited set |
| 1279 Traffic Light Controlled Intersection | State machine | Read-write semantics |
| 1188 Design Bounded Blocking Queue | Producer-Consumer | Condition.await/signal |
| 1188 (alternate) | ArrayBlockingQueue | Built-in vs custom |

## FAANG Interview Stories

**Story 1: Google — Lock-Free Stack**
> *"I was asked to implement a concurrent stack. I started with synchronized. Then they asked to make it lock-free using CAS. I built it with AtomicReference for the head. They asked about the ABA problem — I added a versioned AtomicStampedReference. Then they asked about memory reclamation — how does the JVM handle this compared to C++?"* — L5 SWE, Google

**Story 2: Amazon — Thread Pool Design**
> *"Design a thread pool. I gave the standard ExecutorService API. They said 'Now design it for a system with 100K tasks/second where each task is a microsecond of CPU and a millisecond of I/O.' This changed everything — I needed way more threads for I/O, but also better queuing. We discussed virtual threads before they were released."* — Principal Engineer, Amazon

**Story 3: Uber — Lost Update Bug**
> *"Production incident: A counter was losing updates under high concurrency. A developer used volatile int for a counter. They saw ~90% of the expected count. The fix was AtomicInteger (or LongAdder for high contention). The root cause was 'volatile guarantees visibility, not atomicity' — a classic interview topic they'd failed to apply."* — Staff Engineer, Uber

## Senior vs Staff Deep Dive

### Senior-Level
- "Explain ThreadPoolExecutor parameters. What happens when corePoolSize is exceeded? When maxPoolSize is exceeded?"
- "Compare synchronized with ReentrantLock. Give specific scenarios where each is better."
- "How does a Phaser differ from a CyclicBarrier? When would you use Phaser?"

### Staff-Level
- "Design a lock-free hash map. What are the challenges with resize? With concurrent iteration?"
- "Analyze the memory ordering guarantees of StampedLock's optimistic read. When does it fail?"
- "How does the JVM implement Object.wait()/notify()? What is the WaitSet and how does it interact with the EntrySet?"
- "Design a thread pool that works with both platform and virtual threads. How does pinning affect throughput?"

## System Design Connections

| System | Concurrency Pattern |
|--------|-------------------|
| Web server | Thread pool, async I/O, virtual threads |
| Database connection pool | Thread-safe resource management, leak detection |
| Distributed cache | Lock striping, CAS operations |
| Event bus | Producer-consumer, backpressure |
| Rate limiter | Token bucket, atomic counters |
| Task scheduler | Priority queue + worker threads |
| Stream processing | ForkJoinPool, work stealing, backpressure |

## Code Review Scenarios

**Scenario 1**: Using `synchronized(this)` in a class.
```java
// Anti-pattern
public synchronized void add(User u) { ... }
public synchronized int getCount() { ... }
// Issue: external code can synchronize on the same instance and cause deadlock
// Fix: synchronize on a private final lock object
```

**Scenario 2**: `Double-checked locking` without volatile.
- Issue: JIT reordering can publish partially constructed object
- Fix: Make the field `volatile`, or use `Holder` pattern

**Scenario 3**: Thread pool starvation from task dependency.
```java
// Deadlock risk
executor.submit(() -> {
    Future<Data> f = executor.submit(() -> fetchData());
    Data d = f.get();  // Thread waiting for another task in same pool
});
// Fix: Separate pools for different task types, or use async chaining
```

## Debugging Scenarios

**Scenario 1**: Deadlock in production — all threads BLOCKED.
- Capture thread dump: `jstack <pid>` or `jcmd <pid> Thread.print`
- Look for threads holding locks and threads waiting for same locks
- Common cause: lock ordering violation in two-method call chain

**Scenario 2**: Thread leak — threads created but not returned to pool.
- Set `-Djava.util.concurrent.ForkJoinPool.common.threadFactory` to log creation
- Monitor with `Thread.activeCount()` and `ManagementFactory.getThreadMXBean()`
- Check for thread-local memory leaks (map cleared on thread exit)

**Scenario 3**: Live-lock — threads active but no progress.
- Different from deadlock: threads aren't blocked, they're spinning
- Detect with CPU profiling: `async-profiler` shows high CPU in CAS loops
- Fix: exponential backoff in retry loops
