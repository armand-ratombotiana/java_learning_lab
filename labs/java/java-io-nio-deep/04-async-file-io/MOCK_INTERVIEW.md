# Mock Interview Transcript: Asynchronous I/O

## Interviewer: Senior SWE, Amazon
## Candidate: Senior Java developer
## Time: 25 minutes
## Focus: AsyncFileChannel, CompletionHandler, scalability

---

**Q1: How does AsynchronousFileChannel differ from FileChannel?**

**Candidate**: `FileChannel` blocks the calling thread during I/O operations. `AsynchronousFileChannel` returns immediately — either as a `Future<Integer>` or via a `CompletionHandler` callback. The actual I/O is performed by a thread pool in the `AsynchronousChannelGroup`. This allows a single thread to initiate many concurrent file I/O operations without blocking.

**Interviewer**: When would you use the Future API vs CompletionHandler?

**Candidate**: Use Future when you want to batch operations and wait for completion in one place — like initiating multiple reads then joining them. Use CompletionHandler for callback-driven workflows where each read's result triggers the next action. Future is simpler but forces blocking at the join point; CompletionHandler enables true non-blocking pipelines.

**Interviewer**: How does the thread pool for async I/O work?

**Candidate**: The `AsynchronousChannelGroup` manages a pool of threads that handle I/O events. When a read is initiated, the OS performs the I/O and notifies the group's threads, which then invoke the `CompletionHandler`. The default pool uses daemon threads. You can provide a custom `ExecutorService` to control pool size and thread factory.

**Interviewer**: Can async I/O improve performance for a single large file read?

**Candidate**: Not necessarily — a single sequential read from an HDD is bottlenecked by disk seek time regardless of thread. Async I/O shines with: (1) many concurrent files (e.g., reading 1000 log files simultaneously), (2) mixed I/O and computation (initiate I/O, compute while waiting), (3) SSDs where parallelism actually improves throughput.

**Interviewer**: How do you propagate context (like MDC or tracing) through async callbacks?

**Candidate**: The CompletionHandler runs on a different thread, so thread-local data is lost. Solutions: (1) capture context in a lambda/closure before calling the async operation, (2) use a custom thread pool with `ThreadFactory` that copies MDC, (3) use `ScopedValue` (Java 21+) which is inherited by child threads and can be rebound in callbacks.
