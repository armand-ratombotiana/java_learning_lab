# Interview Questions: Java 21 Features

## Beginner Questions

### Q1: What are virtual threads and why were they introduced?
Virtual threads are lightweight threads managed by the JVM rather than the OS. They were introduced to address Java's historical limitation with OS threads: each platform thread consumes ~1 MB of stack space and is limited to a few thousand per server. Virtual threads use ~200 bytes when parked and can scale to millions, enabling simple synchronous code to handle massive concurrency without reactive frameworks.

### Q2: How does pattern matching for switch differ from traditional switch?
Traditional switch works with integral types, enums, and strings, matching values via equality. Pattern matching for switch extends this to match on type (`case String s`), deconstruct records (`case Point(int x, int y)`), add conditions with `when` guards, and support exhaustive matching over sealed hierarchies. It also guarantees null safety through explicit `case null` handling.

### Q3: What new methods does SequencedCollection add to the Java Collections Framework?
`SequencedCollection` adds seven methods: `addFirst()`, `addLast()`, `getFirst()`, `getLast()`, `removeFirst()`, `removeLast()`, and `reversed()`. These provide a uniform API for collections with defined encounter order, eliminating the need for different approaches to access first/last elements across List, Deque, and ordered Set types.

## Intermediate Questions

### Q4: Explain the concept of "pinning" in virtual threads and how to avoid it.
Pinning occurs when a virtual thread cannot be unmounted from its carrier thread. It happens in `synchronized` blocks/methods, native method calls, and `wait()/notify()`. During pinning, the carrier thread is blocked, which reduces scalability. To avoid pinning, replace `synchronized` with `java.util.concurrent.locks.ReentrantLock` and avoid JNI calls in hot paths. Use `-Djdk.tracePinnedThreads=short` to detect pinning.

### Q5: How does the compiler verify exhaustiveness in pattern matching switch expressions?
The compiler analyzes the permitted subtypes of the input type. For sealed types, it knows the full set of permitted subtypes and checks that each one has a corresponding case. For non-sealed types, a `default` case is required. The compiler also checks pattern dominance (a more general pattern cannot precede a more specific one) and handles null via `case null` or throws NullPointerException if null is not handled.

### Q6: What is the relationship between virtual threads and structured concurrency?
Virtual threads provide the lightweight execution substrate, while structured concurrency provides the coordination pattern. Virtual threads make it practical to create a thread per task without resource concerns. Structured concurrency builds on this by managing the lifecycle of related virtual threads as a unit, ensuring proper error propagation, cancellation, and resource cleanup.

## Advanced Questions

### Q7: Describe the internal implementation of virtual threads in the JVM.
Virtual threads are built on JVM-level continuations (`jdk.internal.vm.Continuation`). When a virtual thread blocks on I/O or parks, the JVM captures its stack frames into a heap-allocated continuation via `Continuation.yield()`. The carrier thread is then freed to execute other virtual threads. When the blocking condition resolves, the continuation is restored and execution resumes. The `ForkJoinPool` scheduler uses work-stealing to balance virtual threads across carrier threads.

### Q8: How would you design a custom StringTemplate processor for building JSON safely?
A custom JSON template processor would parse the template fragments to track context (inside string, property name, array). For each embedded expression, it would apply context-appropriate escaping: strings need `\"` escaping, numbers need no quoting, booleans/null are unquoted. Nested objects could trigger recursive processing. The processor would validate JSON structure at composition time, preventing malformed JSON and injection attacks through user-provided values.

### Q9: Compare the structured concurrency approach with CompletableFuture for orchestrating multiple microservice calls.
Structured concurrency (`StructuredTaskScope`) provides: (1) automatic lifecycle management — tasks are cancelled when the scope closes; (2) deterministic error propagation — exceptions in subtasks cause scope-wide cancellation; (3) cleaner stack traces showing the logical execution flow. CompletableFuture provides: (1) more flexible composition (thenCombine, allOf, etc.); (2) no built-in cancellation boundaries; (3) more complex error handling. Structured concurrency is preferred when tasks have a shared lifecycle and failure should propagate. CompletableFuture is better for complex dependency graphs.

### Q10: How do record patterns enable algebraic data type style programming in Java?
Record patterns, combined with sealed classes and pattern matching, provide the three pillars of algebraic data types: (1) product types (records with `record Point(int x, int y)`), (2) sum types (sealed interfaces with `sealed interface Shape permits Circle, Rectangle`), and (3) exhaustive pattern matching (switch that the compiler verifies covers all cases). This enables a functional programming style where data is deconstructed structurally rather than through imperative accessor methods, making code more concise and safer.

## Scenario-Based Questions

### Q11: A production service using virtual threads is experiencing high latency during peak load. What would you investigate?
First, check `jcmd <pid> Thread.vthread_dump` and look for pinned threads using `-Djdk.tracePinnedThreads=short`. If pinning is detected, identify synchronized blocks and replace them. Next, check carrier thread utilization — if all carrier threads are busy (100% CPU), the application might be CPU-bound, where virtual threads provide no benefit. Also check if ThreadLocal is being used excessively, adding memory overhead. Finally, ensure the garbage collector is tuned for the short-lived nature of virtual thread tasks.

### Q12: How would you migrate a legacy application using a fixed thread pool with 200 threads to virtual threads?
Migration steps: (1) Replace `new Thread()`, `Executors.newFixedThreadPool()` etc. with `Executors.newVirtualThreadPerTaskExecutor()`. (2) Replace `synchronized` blocks with `ReentrantLock` to prevent pinning. (3) Replace ThreadLocal with ScopedValue for request-scoped data. (4) Add `-Djdk.tracePinnedThreads=short` to detect remaining pinning. (5) Remove any thread-pool-size tuning (now unnecessary). (6) Test with production-like loads — virtual threads may expose previously hidden data races due to increased parallelism. (7) Monitor heap usage — each virtual thread adds ~200 bytes overhead.
