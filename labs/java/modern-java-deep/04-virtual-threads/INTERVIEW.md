# Interview Questions: Virtual Threads Deep Dive

## Company-Specific Focus

### Google
- Virtual threads: lightweight threads managed by JVM, not OS
- Continuation: JVM manages virtual thread state as a continuation
- Mount/Unmount: virtual threads mount to carrier threads for execution

### Microsoft
- Virtual threads vs C# Task: both provide lightweight concurrency
- Async patterns: virtual threads enable synchronous-style code without blocking

### Amazon
- Virtual threads for HTTP services: thousands of concurrent connections
- Scoped values + virtual threads: context propagation without ThreadLocal leaks
- Memory: virtual threads use significantly less memory than platform threads

### Meta
- Pinning: synchronized blocks and native calls pin virtual threads to carrier threads
- Carrier threads: ForkJoinPool of platform threads that execute virtual threads
- Throughput: handle more concurrent requests with fewer resources

### Apple
- Virtual threads on ARM64: full support in JDK 21+
- Structured concurrency: manages virtual threads lifecycle

### Oracle
- JEP 425: Virtual Threads (Preview)
- JEP 444: Virtual Threads (Final in JDK 21)
- Continuation: JVM internal mechanism for yielding and resuming
- Scheduler: default ForkJoinPool carrier thread pool

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 1114 Print in Order | Easy | Google, Amazon, Apple | Virtual thread coordination |
| 1242 Web Crawler Multithreaded | Medium | Amazon, Google | Many concurrent virtual threads |
| 1226 The Dining Philosophers | Medium | Google, Amazon, Microsoft | Virtual thread synchronization |

## Real Production Scenarios
- **Amazon**: Migrating 2000-thread pool to virtual threads — P99 latency dropped 60%, memory dropped 80%
- **LinkedIn**: Virtual threads for I/O-heavy data pipeline — doubled throughput with same hardware

## Interview Patterns & Tips
- **Million threads**: create millions of virtual threads without OOM
- **Pinning**: synchronized blocks pin virtual threads — use ReentrantLock instead
- **Pooling**: don't pool virtual threads — they're cheap to create
- **No thread per request**: virtual threads make the thread-per-request model efficient again

## Deep Dive Questions
- **Continuation**: How does the JVM implement continuations?
- **Mount/Unmount**: How does the JVM manage mounting/unmounting virtual threads?
- **Carrier thread**: What happens when a virtual thread is pinned?
- **Scheduler**: How does the ForkJoinPool scheduler work for virtual threads?
- **Memory**: What is the memory footprint of an idle virtual thread vs platform thread?