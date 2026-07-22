# Interview Questions: Parallel Algorithms

## LeetCode Problem Map
No direct LeetCode problems. System design focus: ForkJoinPool, Parallel Streams, MapReduce.

## NeetCode Reference
Not directly covered as a standalone topic. Relevant to system design discussions (Design Uber, Design Netflix).

## Company-Specific Questions
### Google
- Design a parallel MapReduce job for processing petabytes of log data
- How does Google's Borg scheduler parallelize tasks across a cluster?
- Explain work-stealing in Fork/Join framework vs Google's internal thread pools
- Design a parallel sorting algorithm for distributed systems

### Microsoft
- How does the Windows Thread Pool use work-stealing?
- Design a parallel algorithm for Excel's large spreadsheet computation
- Explain ConcurrentBag vs ConcurrentQueue vs ConcurrentStack in .NET (compare to Java)
- How does SQL Server parallelize query execution?

### Meta
- Design a parallel system for processing Facebook's news feed ranking
- How would you parallelize video transcoding across a cluster?
- Explain MapReduce for social graph analytics
- Design a data pipeline that processes billions of events per day in parallel

### Amazon
- How does AWS Lambda scale functions in parallel?
- Design a parallel algorithm for DynamoDB scan operations
- Explain the parallel processing pipeline for Amazon order fulfillment
- How does S3 handle parallel uploads (multipart upload)?

### Apple
- How does Grand Central Dispatch work on iOS/macOS?
- Design a parallel algorithm for image processing on limited hardware
- Explain the performance implications of parallelism on battery life
- How would you parallelize a real-time audio processing pipeline?

### Oracle
- How does Oracle RAC parallelize queries across nodes?
- Design a parallel index build for very large tables
- Explain Oracle's parallel execution servers and degree of parallelism
- How does parallel DML work in Oracle Database?

## Real Production Scenarios
- Scenario 1: Batch processing pipeline - designing a parallel ETL pipeline using ForkJoin to process 10 million daily transactions with exactly-once semantics
- Scenario 2: Real-time analytics - debugging a parallel stream operation that produces incorrect results due to shared mutable state in a lambda expression
- Scenario 3: Distributed computation - implementing a MapReduce-style job to compute PageRank across 100 nodes with fault tolerance and straggler mitigation

## Interview Tips
- Amdahl's Law: speedup = 1 / (1 - P + P/N) where P is parallelizable fraction
- Know Fork/Join work-stealing algorithm (work-first vs help-first scheduling)
- Race conditions, deadlocks, and livelocks are the main parallel pitfalls
- Common edge cases: thread starvation, false sharing, Amdahl's wall, NUMA effects

## Java-Specific Considerations
- `ForkJoinPool.commonPool()` for parallel task execution via `RecursiveTask<T>` and `RecursiveAction`
- `parallelStream()` for declarative parallelism on collections; `spliterator()` controls partitioning
- `CompletableFuture` for asynchronous parallel computation with `allOf()` / `anyOf()` combinators
- `java.util.concurrent` provides: `CountDownLatch`, `CyclicBarrier`, `Semaphore`, `Phaser`
- Pitfall: shared mutable state in parallel streams (non-interference + statelessness required)
- Pitfall: parallel stream performance worse than sequential for small datasets (overhead dominates)
- Pitfall: thread pool saturation leading to deadlock with nested parallel tasks
