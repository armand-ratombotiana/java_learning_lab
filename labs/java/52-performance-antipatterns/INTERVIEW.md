# Java Performance Antipatterns & Debugging — Interview Questions

## Question 1: Core Concepts
**Q:** Explain performance antipatterns and debugging and why it matters in modern Java development.
**A:** This topic covers how modern Java applications manage concurrency, memory, and performance. It matters because applications increasingly need to leverage multi-core hardware, manage large data sets efficiently, and provide reliable service under load.

## Question 2: Structured Concurrency
**Q:** How does structured concurrency differ from traditional concurrency models?
**A:** Structured concurrency binds task lifetimes to code blocks, ensuring that all subtasks complete before the scope exits. This guarantees proper cleanup and error propagation. Traditional models rely on manual lifecycle management with Future and ExecutorService, which can lead to thread leaks and lost errors.

## Question 3: JFR Profiling
**Q:** How would you use JFR to diagnose a performance issue?
**A:** Enable JFR with appropriate event settings, capture a recording during the performance issue, then analyze the recording. Key events to examine include GC events, lock contention events, allocation events, CPU sampling, and thread sleeps. JFR provides low-overhead continuous recording suitable for production use.

## Question 4: Off-Heap Memory
**Q:** When would you use off-heap memory in a Java application?
**A:** Off-heap memory is useful for large caches, network buffers, memory-mapped files, and data that needs to be shared with native code. Benefits include reduced GC pressure, potential for larger allocations, and direct I/O. Trade-offs include manual memory management and serialization overhead.

## Question 5: False Sharing
**Q:** What is false sharing and how do you prevent it?
**A:** False sharing occurs when multiple threads modify variables on the same cache line, causing cache coherence traffic even though they access different variables. Prevention strategies include padding data structures to align variables on separate cache lines, using @Contended annotation, or restructuring data access patterns.

## Question 6: Disruptor Pattern
**Q:** Explain the Disruptor pattern and its advantages.
**A:** The Disruptor is a ring-buffer based event processing architecture that eliminates lock contention through careful memory layout, sequence barriers, and batch processing. Advantages include extremely low latency, predictable performance, and zero GC during steady-state operation.

## Question 7: Performance Antipatterns
**Q:** What are the most common Java performance antipatterns?
**A:** Common antipatterns include boxing overhead in hot paths, string concatenation with '+' in loops, ThreadLocal leaks in thread pools, excessive synchronization, finalizer usage, classloader leaks, and unbounded thread creation.