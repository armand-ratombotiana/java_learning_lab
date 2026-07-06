# Why Locking Exists

Locking exists because shared mutable state is unavoidable in concurrent systems. Without locks, threads would corrupt shared data through interleaved read-modify-write operations. Consider a simple counter: `count++` compiles to three operations (read, increment, write). Without synchronization, two threads can interleave: both read the same value, both increment, both write — one increment is lost.

synchronized exists as Java's built-in mutual exclusion mechanism. It's simple (one keyword) and reliable (guaranteed by the JVM specification). However, it's inflexible: you can't time out, you can't interrupt a thread waiting for a lock, and you can't check if a lock is available without blocking.

ReentrantLock exists because synchronized's rigidity limits concurrency design. ReentrantLock adds: tryLock (non-blocking attempt), lockInterruptibly (interruptible waiting), fair/unfair policies, Condition variables (multiple wait queues per lock), and lock inspection (getHoldCount, isHeldByCurrentThread, hasQueuedThreads).

StampedLock exists because read-write locks still have overhead when reads vastly outnumber writes. Optimistic reads (tryOptimisticRead) don't block at all — they check after reading whether a write occurred. If no write occurred, the read was consistent without any locking. This is ideal for read-heavy workloads with infrequent writes.

LockSupport exists as the primitive underpinning all other locking mechanisms. park/unpark are more flexible than wait/notify because unpark can be called before park, and the permit accumulates (max 1). AQS, ReentrantLock, and Condition all use LockSupport internally.

CAS exists because sometimes all you need is an atomic increment or compare-and-swap. Using a full lock for a single word update is overkill. CAS is the foundation of all atomic classes (AtomicInteger, AtomicReference) and lock-free data structures.
