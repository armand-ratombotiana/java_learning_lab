# Performance of Locking

## Lock Acquisition Cost (approximate)
- Biased lock (single thread): 1-2 ns
- Thin lock (CAS, no contention): 5-10 ns
- Inflated lock (OS mutex): 100-1000 ns
- ReentrantLock (unfair, no contention): 10-20 ns
- ReentrantLock (fair, no contention): 20-40 ns
- StampedLock optimistic read: 1-2 ns
- StampedLock read lock: 20-30 ns
- CAS (single thread): 5-10 ns

## Contention Scaling
Under contention, lock performance degrades:
- CAS: degrades quadratically with thread count (O(K²))
- synchronized (thin → inflated): cross-over at ~2-4 threads
- ReentrantLock unfair: better than fair under contention (no context switch for barging)
- ReentrantLock fair: FIFO guarantees cause maximum slowdown under contention

## False Sharing
When two threads modify different fields on the same cache line (64 bytes), each write invalidates the other's cache. This can cause 10-100x slowdown. Mitigations:
- Pad objects to 64 bytes (fields at offset 0, 64, 128)
- Use `@Contended` annotation (Java 8+, requires -XX:-RestrictContended)
- Align striping to cache lines

## Read-Write Lock Scalability
For read-heavy workloads with ReentrantReadWriteLock:
- 100% reads: scales to many cores (all readers proceed)
- Mixed: writer acquisition blocks all readers, causing latency spikes
- Cross-over: when writes exceed ~10%, read-write lock performance approaches exclusive lock

## StampedLock Optimistic Read Performance
Optimistic reads are essentially free (just version check). Performance degradation happens when:
- Validation failure rate > 10% (too many writes)
- Each failed validation falls back to read lock (doubles latency)

## Lock Elimination
The JIT can eliminate locks on objects that don't escape the method (escape analysis). Local `StringBuffer` allocations inside a method lose their locks because the StringBuffer is never visible to other threads.
