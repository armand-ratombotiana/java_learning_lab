# History of Java Locking

## Java 1.0 (1996): synchronized and volatile
Java included `synchronized` blocks/methods and `volatile` from the start. The monitor model was inherited from C's Threads API and Mesa monitor conventions. `wait/notify/notifyAll` were the only coordination primitives.

## Java 1.5 (2004): JSR 166 — java.util.concurrent
The `java.util.concurrent` package introduced `ReentrantLock`, `ReadWriteLock`, `Condition`, and the `AbstractQueuedSynchronizer` framework. Doug Lea's AQS became the foundation for all subsequent synchronizers. `LockSupport` was introduced as a thin wrapper around `sun.misc.Unsafe.park/unpark`.

## Java 6 (2006): Lock Optimization
Major synchronized optimizations:
- Biased locking: single-thread lock reacquisition via object header bit
- Thin locks: CAS-based, no OS mutex
- Lock coarsening: JIT merges adjacent synchronized blocks
- Lock elision: JIT eliminates locks on non-escaping objects (escape analysis)

## Java 7 (2011): Phaser and TransferQueue
Finer-grained synchronization primitives added.

## Java 8 (2014): StampedLock
`StampedLock` was added (albeit as an internal class before being standardized). It introduced optimistic reads — a fundamentally new locking mode where reads don't block and can detect concurrent writes.

## Java 15 (2021): Biased Locking Disabled
JEP 374 disabled biased locking by default due to the complexity it added to the JVM's locking code and its limited benefit with modern workloads.

## Java 16 (2022): Value-Based Class Warnings
Synchronizing on value-based classes (LocalDate, Optional, etc.) generates warnings.

## Java 21 (2023): Virtual Threads and Pinning
With virtual threads (JEP 444), synchronized blocks pin virtual threads to platform threads. This limitation has renewed focus on ReentrantLock usage in virtual thread environments.
