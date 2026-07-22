# Interview Questions: Performance Antipatterns & Debugging

## Company-Specific Focus

### Google
- Excessive object allocation: hidden allocations from autoboxing, string concat
- Inefficient data structures: using ArrayList when HashMap is needed
- Thread contention: excessive synchronization in hot paths

### Microsoft
- Memory leaks: ThreadLocal, classloader leaks, listener registration
- Logging performance: string concatenation in log messages
- I/O performance: blocking I/O in async patterns

### Amazon
- GC pressure: high allocation rate, promotion failures
- Thread pool mismanagement: unbounded queues, core vs max pool size
- Connection pool leaks: unclosed resources, exhausted pools

### Meta
- Reflection overhead: calling Method.invoke() in hot paths
- Serialization bottlenecks: Java serialization is slow and bloated
- Exception overhead: using exceptions for control flow

### Apple
- Object overhead: creating too many small objects
- Unbounded caches: HashMap without size limit causes OOM
- Unclosed resources: file handles, sockets

### Oracle
- Common JVM performance antipatterns: memory leaks, GC tuning misconfiguration
- Thread dump analysis for deadlocks and contention
- Heap dump analysis for memory leak identification
- JFR event analysis for performance bottlenecks

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| (No direct LC problems — antipatterns are debugging concepts) |

## Real Production Scenarios
- **Amazon**: Connection pool leak caused service outage every 48 hours — missing close() in finally block
- **Netflix**: String concatenation in a hot loop caused 30% CPU usage — fixed with StringBuilder
- **LinkedIn**: ThreadLocal leak in a request processing filter caused OOM after 48 hours of uptime

## Interview Patterns & Tips
- **Don't optimize prematurely**: but avoid known antipatterns from the start
- **Profile first**: use JFR, async-profiler to identify actual bottlenecks
- **Common patterns**: excessive allocation, thread contention, memory leaks
- **Connection leaks**: always use try-with-resources

## Deep Dive Questions
- **Memory leak identification**: How to find a memory leak with heap dump analysis?
- **Thread contention**: How to identify thread contention with thread dumps?
- **GC pressure**: How to identify high allocation rate using JFR?
- **False sharing**: How to detect false sharing? (perf c2c on Linux)
- **Bottleneck**: How to identify a CPU bottleneck vs I/O bottleneck?