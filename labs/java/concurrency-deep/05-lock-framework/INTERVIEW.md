# Interview Questions: Lock Framework

## Company-Specific Focus

### Google
- ReentrantLock: explicit lock with tryLock, lockInterruptibly, fairness
- ReadWriteLock: multiple readers, single writer
- StampedLock: optimistic read mode for higher throughput

### Microsoft
- ReentrantLock vs C# Monitor: similar reentrant semantics
- tryLock: non-blocking lock attempt with timeout

### Amazon
- Fair vs unfair locks: fairness prevents starvation, but reduces throughput
- Condition: await/signal for thread coordination
- Performance: Lock vs synchronized — when Lock is preferred

### Meta
- ReentrantReadWriteLock: read-heavy workloads
- tryLock pattern: bail out if lock not available
- LockSupport: park/unpark for building custom locks

### Apple
- Condition await/signal: bounded buffer producer-consumer
- Lock vs synchronized: Lock provides more flexibility
- LockInterruptibly: responsive to interruption

### Oracle
- java.util.concurrent.locks package specification
- AbstractQueuedSynchronizer (AQS): framework for building locks
- Lock interface: lock, unlock, tryLock, newCondition

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 1115 Print FooBar Alternately | Medium | Amazon, Google, Microsoft | Lock + Condition coordination |
| 1116 Print Zero Even Odd | Medium | Google, Apple, Amazon | Multi-thread Condition handoff |
| 1226 The Dining Philosophers | Medium | Google, Amazon, Meta | Lock ordering for deadlock prevention |

## Real Production Scenarios
- **Netflix**: ReentrantLock with tryLock for non-blocking cache updates
- **Uber**: ReadWriteLock for configuration store — reads are frequent, writes rare

## Interview Patterns & Tips
- **tryLock**: Non-blocking lock attempt; always call unlock in finally if acquired
- **Condition**: await releases lock, signal wakes waiting thread
- **AQS**: The foundation of Lock framework — CLH queue of waiting threads

## Deep Dive Questions
- **AQS**: How does AbstractQueuedSynchronizer manage wait queues?
- **Fairness**: How is fairness implemented in ReentrantLock?
- **Condition**: How does Condition.await() release and reacquire the lock?
- **LockSupport**: How does park/unpark interact with the OS scheduler?
- **Performance**: When would you choose Lock over synchronized?