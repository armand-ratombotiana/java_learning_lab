# Why Locking Internals Matter

Understanding locking internals determines whether your concurrent application scales to 64 cores or bottlenecks on lock contention. A poorly chosen lock can reduce throughput below sequential execution.

synchronized's internal lock inflation matters because the JVM optimizes the common case. Most synchronized blocks have no contention (thin lock — CAS on object header). Only when contention arises does the lock inflate to an OS mutex. Knowing this prevents premature optimization — switching to ReentrantLock "for performance" is often counterproductive for uncontended locks.

AQS matters because it's the foundation of most java.util.concurrent synchronizers. Understanding AQS means understanding ReentrantLock, CountDownLatch, Semaphore, and CyclicBarrier. The CLH queue ensures fairness is FIFO; state management determines lock semantics. A custom AQS subclass can implement any synchronizer with predictable performance.

StampedLock's optimistic reads matter for read-dominated workloads. A cache with 99% reads and 1% writes running on 64 cores can use optimistic reads to eliminate all lock contention for readers. The 1% writes briefly invalidate optimistic read results, but 99% of reads proceed without any atomic operations.

LockSupport matters because park/unpark are the building blocks of all Java concurrency. Unlike wait/notify, park/unpark don't require holding a monitor. This enables flexible synchronization patterns. The Blocker mechanism allows thread dumps to show why a thread is parked (e.g., "waiting on ReentrantLock").

CAS matters because it's the cheapest synchronization primitive. A single CAS costs ~10-50 nanoseconds (cache hit) compared to ~1000+ nanoseconds for an OS mutex. Lock-free algorithms built on CAS avoid context switches and priority inversion entirely. The CasCounter in this lab demonstrates the minimal cost of atomic increment compared to synchronized.
