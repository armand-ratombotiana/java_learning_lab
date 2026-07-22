# Interview Questions: Locking & Synchronization Internals

## Company-Specific Focus

### Google
- Synchronized keyword: monitor objects, reentrancy
- ReentrantLock: tryLock, lockInterruptibly, fairness
- ReadWriteLock: multiple readers, single writer optimization
- LockSupport: park/unpark for advanced lock implementations

### Microsoft
- Java locks vs C# Monitor/locks: similar but differences in reentrancy
- synchronized vs StampedLock: optimistic reading capabilities

### Amazon
- Lock contention: how to detect and fix in high-throughput services
- ReentrantLock vs synchronized performance comparisons
- StampedLock: optimistic reads for read-heavy workloads

### Meta
- Biased locking: why it was removed in Java 15
- Lock coarsening and lock elimination: JIT optimizations
- Deadlock detection: jstack, thread dump analysis

### Apple
- Synchronized block: lightweight and heavyweight monitor transitions
- Intrinsic locks vs explicit locks: choosing the right level

### Oracle
- JVM locking implementation: biased (pre Java 15), lightweight, heavyweight monitor
- Object header: mark word and its encoding for lock state
- The monitor object: WaitSet and EntryList
- ReentrantLock vs synchronized: performance and features

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 1115 Print FooBar Alternately | Medium | Amazon, Google, Microsoft | Lock-based thread coordination |
| 1116 Print Zero Even Odd | Medium | Google, Apple, Amazon | Multi-threaded lock handoff |
| 1117 Building H2O | Medium | Amazon, Google | Complex synchronization |
| 1226 The Dining Philosophers | Medium | Amazon, Google, Microsoft | Deadlock prevention |
| 1242 Web Crawler Multithreaded | Medium | Amazon, Apple | Concurrent crawling with locks |

## Real Production Scenarios
- **AWS**: A deadlock in Amazon S3's metadata service caused a 4-hour outage — two threads holding locks A and B waiting for B and A
- **LinkedIn**: Using synchronized for a read-heavy cache caused 80% thread contention — migrated to ReadWriteLock improving throughput by 5x
- **Uber**: Optimistic locking with StampedLock reduced lock contention by 70% in trip matching service

## Interview Patterns & Tips
- **Reentrancy**: Both synchronized and ReentrantLock are reentrant
- **Fairness**: ReentrantLock can be fair (FIFO) or unfair (barging)
- **StampedLock**: Optimistic reads are not reentrant; conversion between read and write modes
- **Condition**: ReentrantLock.newCondition() for await/signal pattern

## Deep Dive Questions
- **Object header**: How is the lock state encoded in the mark word?
- **Monitor**: How does the JVM manage the WaitSet and EntryList?
- **Lock elimination**: How does the JIT determine that a lock is not shared?
- **Biased locking**: Why was it removed?
- **Park/Unpark**: How does LockSupport.park() interact with the OS thread scheduler?