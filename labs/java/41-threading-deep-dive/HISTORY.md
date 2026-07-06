# History of Java Threading

## Java 1.0 (1996): Threads and synchronized
Java included threads from the start with `java.lang.Thread` and `synchronized` blocks/methods. The thread model was based on "green threads" (user-level threads scheduled by the JVM) on Solaris, and native OS threads on Windows. The `wait/notify` mechanism provided basic coordination.

## Java 1.2 (1998): Deprecation of Thread.stop/suspend
The original `Thread.stop()`, `suspend()`, and `resume()` methods were deprecated due to inherent deadlock and data corruption risks. `stop()` released all monitors, potentially leaving shared data in inconsistent states.

## Java 5 (2004): java.util.concurrent (JSR 166)
Doug Lea's concurrency framework was integrated. This was a transformative release:
- `ThreadPoolExecutor`, `ScheduledThreadPoolExecutor`
- `ReentrantLock`, `ReadWriteLock`, `Condition`
- `AtomicInteger`, `AtomicLong`, `AtomicReference`
- `ConcurrentHashMap`, `CopyOnWriteArrayList`
- `BlockingQueue` implementations
- `Future` and `Callable` interfaces
- `Semaphore`, `CountDownLatch`, `CyclicBarrier`, `Exchanger`

## Java 7 (2011): ForkJoinPool (JSR 166y)
`ForkJoinPool` and `RecursiveTask/RecursiveAction` were added, introducing work-stealing to Java. Parallel array operations and the `Phaser` synchronizer were also added.

## Java 8 (2014): CompletableFuture and Parallel Streams
`CompletableFuture` (JSR 166e) brought composable asynchronous programming. Parallel streams used ForkJoinPool.commonPool() as their default execution engine.

## Java 19 (2022): Virtual Threads (Preview)
JEP 425 introduced virtual threads as preview. Virtual threads are lightweight threads managed by the JDK rather than the OS, enabling millions of concurrent threads.

## Java 21 (2023): StructuredTaskScope (Preview)
JEP 428 introduced StructuredTaskScope for structured concurrency. Virtual threads were finalized. This marked a shift from unstructured thread-per-request to structured concurrency models.
