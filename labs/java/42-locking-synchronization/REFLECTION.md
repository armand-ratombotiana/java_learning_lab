# Reflection: Locking & Synchronization

## Key Takeaways
- Locking is essential for safe concurrent access to shared mutable state
- AQS is the foundation of the java.util.concurrent synchronizer framework
- Different lock types have different performance characteristics for different workloads
- CAS provides lock-free atomic operations with finer granularity
- Understanding memory ordering is crucial for correct lock-free programming

## Connections to Other Concepts
Locking connects to the JVM's memory model (happens-before), JIT compilation (lock elision, coarsening), and OS scheduling (mutexes, context switching). Virtual threads (Project Loom) change the locking landscape by making synchronized blocks a pinning concern.

## Challenges Encountered
- Understanding the CLH queue's lock-free nature
- Distinguishing between biased, thin, and inflated lock states
- Internalizing when optimistic reads fail and fall back to read locks
- Correctly using Unsafe for CAS operations

## Questions to Explore Further
1. How does the Disruptor library achieve single-writer principle for zero contention?
2. What are the implications of virtual threads on locking patterns?
3. How do databases implement optimistic concurrency control vs pessimistic locking?
4. What is the role of transactional memory (HTM) in future Java versions?

## Practical Application
- Use synchronized for simplicity when contention is low
- Use ReentrantLock when you need tryLock, timeouts, or interruptibility
- Use StampedLock when reads dominate writes
- Use CAS for single-word atomic operations
- Always use try-finally with explicit locks
- Profile lock contention before optimizing
