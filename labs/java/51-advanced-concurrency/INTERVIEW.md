# Interview Questions: Advanced Concurrency Patterns

## Company-Specific Focus

### Google
- Non-blocking algorithms: CAS-based data structures
- VarHandle: atomic operations on object fields
- Producer-Consumer patterns: BlockingQueue variants

### Microsoft
- Advanced concurrency in Java vs .NET
- ConcurrentHashMap: CAS operations, tree bins, resize
- CompletableFuture: composing async operations

### Amazon
- Non-blocking data structures for high throughput
- CompletableFuture for async service composition
- Backpressure patterns in concurrent systems

### Meta
- Lock-free programming: CAS loops, ABA problem
- ConcurrentHashMap internals: Java 8+ improvements
- Disruptor pattern: ring buffer for high throughput

### Apple
- Structured concurrency for coordination
- Scoped values for cross-thread propagation
- Immutable data structures for concurrent access

### Oracle
- java.util.concurrent.atomic: AtomicInteger, AtomicReference, LongAdder
- VarHandle: Java 9+ method handles on object fields
- CompletableFuture pipeline composition
- Phaser, Exchanger, Semaphore

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 1114 Print in Order | Easy | Google, Amazon, Microsoft | Atomic ordering |
| 1115 Print FooBar Alternately | Medium | Amazon, Google | Non-blocking coordination |
| 1116 Print Zero Even Odd | Medium | Google, Microsoft | Lock-free coordination |
| 1242 Web Crawler Multithreaded | Medium | Amazon, Apple | Concurrent worker coordination |
| 1226 The Dining Philosophers | Medium | Google, Amazon | Deadlock-free concurrent access |

## Real Production Scenarios
- **LinkedIn**: LongAdder replacing AtomicLong for a high contention counter improved throughput by 5x
- **Netflix**: CompletableFuture composition for async pipeline of 5 services improved latency by 40%
- **Uber**: ConcurrentHashMap resize caused CPU spike to 100% — resolved by pre-sizing the map

## Interview Patterns & Tips
- **CAS loops**: Atomic classes use compare-and-swap loops for lock-free updates
- **ABA problem**: Use AtomicStampedReference for ABA prevention
- **LongAdder vs AtomicLong**: LongAdder uses striped counters for higher throughput under contention
- **CompletableFuture**: thenApply, thenCompose, thenCombine, allOf, anyOf

## Deep Dive Questions
- **CAS**: How does the JVM implement compareAndSwap? (CMPXCHG on x86)
- **VarHandle**: How does VarHandle differ from reflected Field access?
- **ConcurrentHashMap**: How does the table resize work?
- **ABA**: What is the ABA problem? How is it prevented?
- **CompletableFuture**: How does the async mechanism work under the hood?
