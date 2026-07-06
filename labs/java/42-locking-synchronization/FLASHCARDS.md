# Flashcards: Locking & Synchronization

**Q: What are the three lock states for Java monitors?**
A: Biased (single-thread), thin (CAS-based), inflated (OS mutex).

**Q: What is the CLH queue in AQS?**
A: A lock-free doubly-linked list of threads waiting to acquire the lock. Each thread spins on its predecessor's status.

**Q: When should you use StampedLock over ReentrantLock?**
A: When reads dominate writes (>90% reads) and you want to avoid read lock overhead with optimistic reads.

**Q: What is the ABA problem in CAS?**
A: When a value changes A→B→A between a thread's read and CAS, the CAS succeeds incorrectly. Mitigated with version stamps.

**Q: How does ReentrantLock support reentrancy?**
A: It tracks the hold count in AQS state. Each lock() increments, each unlock() decrements. Lock is released when count reaches 0.

**Q: What is LockSupport's permit?**
A: A binary semaphore (max 1). park() consumes it (blocks if 0). unpark() provides it (wakes thread if blocked, or remembers if called before park).

**Q: In what order should finalize be called with multiple resources?**
A: In reverse order of acquisition (stack discipline) to avoid deadlock.

**Q: What happens if you forget try-finally with ReentrantLock?**
A: If the critical section throws, the lock is never released, causing deadlock or starvation.
