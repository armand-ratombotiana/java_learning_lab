# Locking Theory

## The Need for Locking
When multiple threads access shared mutable state, race conditions occur. A race condition is when the outcome depends on the interleaving of thread execution. Locking provides mutual exclusion — only one thread at a time can execute a critical section.

## Java's Memory Model (JMM)
The JMM defines when one thread's writes become visible to another thread. Without proper synchronization, the JVM, JIT compiler, and CPU may reorder instructions, making writes invisible. The `happens-before` relationship guarantees ordering:
- Monitor lock: unlock happens-before subsequent lock
- Volatile: write happens-before subsequent read
- Thread start: start() happens-before first action in new thread
- Thread join: last action in thread happens-before join() returns

## Monitor Locks (synchronized)
Every Java object has an associated monitor. The synchronized keyword compiles to `monitorenter`/`monitorexit` bytecodes. The JVM implements monitors with three tiers:
1. **Biased locking** (Java 6-15, removed in 21): Single thread repeatedly acquires the lock with minimal overhead
2. **Thin lock** (CAS-based): Lightweight lock using CAS on the object header
3. **Inflated lock** (OS mutex): Heavyweight lock using OS synchronization primitives

## AbstractQueuedSynchronizer (AQS)
AQS is the framework behind ReentrantLock, CountDownLatch, Semaphore, and other synchronizers. It maintains:
- A `volatile int state` representing lock ownership count
- A CLH (Craig, Landin, Hagersten) queue of waiting threads
- CAS-based state transitions
- LockSupport.park/unpark for thread blocking

## CAS (Compare-And-Swap)
CAS is an atomic CPU instruction that updates a memory location only if it holds an expected value. CAS avoids the overhead of locks for simple operations. Java's `Unsafe.compareAndSwapInt` exposes this. The ABA problem (value changes A→B→A) is mitigated by using version stamps or atomic markable references.
