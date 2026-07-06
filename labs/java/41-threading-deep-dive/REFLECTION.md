# Reflection: Threading Deep Dive

## Key Takeaways
- Thread lifecycle is a finite state machine with six states and well-defined transitions
- ThreadPoolExecutor decouples task submission from execution via a work queue
- ForkJoinPool uses work-stealing to balance load across workers dynamically
- CompletableFuture enables declarative asynchronous pipelines
- StructuredTaskScope brings structured programming principles to concurrency

## Connections to Other Concepts
Threading connects to every aspect of Java performance. The thread pool pattern appears in web servers (Tomcat, Jetty), database connection pools (HikariCP), and reactive frameworks (Project Reactor). ForkJoinPool powers parallel streams and CompletableFuture's default executor.

## Challenges Encountered
- Understanding the difference between BLOCKED and WAITING states
- Internalizing when ThreadPoolExecutor creates vs queues vs rejects
- Distinguishing thenApply from thenCompose (flatMap)
- Recognizing that structured concurrency prevents resource leaks

## Questions to Explore Further
1. How does virtual thread (Project Loom) scheduling differ from platform thread scheduling?
2. What are the performance characteristics of Disruptor compared to ForkJoinPool?
3. How do reactive frameworks (RxJava, Reactor) implement backpressure?
4. What is the relationship between CompletableFuture and the actor model?

## Practical Application
The concepts in this lab apply directly to production systems:
- Right-sizing thread pools prevents latency spikes
- Using ForkJoinPool for divide-and-conquer algorithms improves throughput
- CompletableFuture pipelines make async code readable and maintainable
- StructuredTaskScope prevents orphaned subtasks in complex concurrent operations

## Next Steps
- Study virtual threads and how they differ from platform threads
- Explore the Disruptor library as an alternative to BlockingQueue
- Read the ForkJoinPool source code to understand work-stealing in detail
- Practice converting blocking code to CompletableFuture pipelines
- Implement a structured concurrency scope from scratch
