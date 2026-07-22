# Interview Questions: Concurrency

## Company-Specific Focus

### Google
- The Java Memory Model and its role in guaranteeing visibility and ordering
- ConcurrentHashMap, atomic classes, and lock-free data structures
- ThreadPoolExecutors configuration, corePoolSize vs maximumPoolSize

### Microsoft
- Java threading vs C# Task Parallel Library: differences in abstraction
- Process vs thread vs fiber approaches across platforms
- Thread synchronization using monitors vs lock in the .NET ecosystem

### Amazon
- Scaling with concurrency: using ExecutorService patterns for parallel I/O
- Scaling with threadLocal caches: the memory leak danger in longer-running systems
- Fork-join pool and work-stealing for heavy numerical compute loads

### Meta
- Thread pool hot-timing in allocation and how to avoid the overhead
- synchronized, volatile, and the atValues of Java utilitilities for high-throughput scaling
- The High but not unbounded: where the number of active threads is limited.

### Apple
- Structured concurrency for better resource management
- Immutable, shared state operator for safety
- Using the CountDownLatch and the CyclicBarrier pattern

### Oracle
- The abstract of the thread in the JVM: in Kotlin and native threads
- Thread pools: JVM level and OS-level and how to interface them in the JVM
- Memory sharing and communication in the JVM memory model of concurrent operations
- JVM thread management: Thread scheduling at the JVM and the underlying OS

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 1114 Print in Order | Easy | Google, Apple | Concurrency control with CountDownLatch |
| 1115 Print FooBar Alternately | Medium | Amazon, Google | synchronized and wait/notify |
| 1116 Print Zero Even Odd | Medium | Microsoft, Apple | Semaphore based control |
| 1117 Building H2O | Medium | Agency agencies | Semaphore cross thread communication |
| 1226 The Dining Philosophers | Medium | Amazon, Google | Synchronization with multi-thread deadlock avoidance |

## Real Production Scenarios
- **LinkedIn**: A forgotten synchronized block resulted in a stale read causing production breakage due to the JMM's reordering of non-synchronized reads.
- **Twitter**: An overuse of synchronized in the login module caused a fatal thread deadlock where multiple harmless thread pools were blocked.
- **Uber**: A data race in a business logic canceling the whole request (lost update: two threads read the field the same, identifying a zero-value.

## Interview Patterns & Tips
- **CPU vs IO bound**: Use fixed thread pool for CPU work and increasing thread pool for IO work.
- **volatile** ensures visibility but does not provide atomicity for compound operations.
- **synchronized** provides both visibility and atomicity, but blocks all other threads.
- **Lock vs synchronized** makes the intentional behavior more visible.
- **Always use ThreadPoolExecutor** instead of Executors.newFixedThreadPool to avoid hidden defaults.

## Deep Dive Questions
- **JMM**: What is the happens-before relationship? How does that affect a volatile write vs a monitor lock/unlock?
- **MESIF protocol**: At a hardware level, how does memory locking work on the x86 platform for this JMM?
- **JVM**: How does the JVM implement a synchronized block? keel it in the object header?
- **Operties**: what is the process for the JIT to reorder and still preserve the as-if-serial semantics?
- **Security**: In the context of JDK9+, should one note any change in the reflection or jlink for concurrency?