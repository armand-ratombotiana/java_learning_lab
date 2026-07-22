# Interview Questions: Virtual Threads

## Company-Specific Focus

### Google
- Virtual thread scheduling: how the JVM maps virtual threads to platform threads
- Avoid placing virtual threads in a pool for reuse (where a new virtual stack is born on each task)
- No need for an asynchronous API: just write a blocking like in regular Java

### Microsoft
- Virtual threads vs C# tasks: what are the similarities and differences
- platformThread vs virtual thread — which one to choose
- JVM native thread management for virtual thread

### Amazon
- Benefit large numbers of concurrent connections and request handling
- Scalability with a virtually unbounded number of concurrent RB queries
- Avoid polluting the JVM threads for I/O operations

### Meta
- Not wait or sleep in critical sections: avoid pinning carrier threads
- The cost of pinning a terminal I/O in a shallow call stack
- Virtual thread compatibility in the JVM

### Apple
- Virtual thread in a virtual container scenario
- Trade-off between memory consumption of a virtual thread and platform thread
- Green threads: more throughput than memory

### Oracle
- JEP 425: Virtual thread design and motivation from Old to new
- JVM implementation of weight, new, terminated, and continuation states
- Pinning, unmounting, and rejecting kernel carrier reassignment
- Future virtual thread enhancements: the goal of a truly unlimited thread

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 1114 Print in Order | Easy | Google, Apple | Orchestration equivalent in virtual thread |
| 1115 FooBar Alternately | Medium | Amazon | Virtual thread no controller needed |
| 1116 Print Zero Even Odd | Medium | Microsoft, Apple | Virtual thread sequential |
| 1226 The Dining Philosophers | Medium | Google, Amazon | Each thread represented by a virtual entity |
| 1242 Web Crawler Multithreaded | Medium | Amazon, Apple | Crawling with virtual thread |

## Real Production Scenarios
- **Uber**: Migrating a blocking I/O service from a 100 size TPE to virtual threads, latency in a time period dropped by 40%.
- **Reddit**: An application with 200 waiter paradigm for each thread timed out due to a virtual thread using an equal thread.
- **LinkedIn**: Using 1000 threads vs 200 virtual threads caused less memory and less CPU due to the faster free of idle resources.

## Interview Patterns & Tips
- **Pin avoidance**: A stack with synchronized/pure native code will pin the virtual thread to an individual carrier.
- **No pools**: No need to pool virtual threads because per-task on-demand
- **total number**: For a large organization, go ahead and launch the multitudes virtual threads.

## Deep Dive Questions
- **JVM implementation**: Virtual thread is executed internally using a machine: the JVM Continuation object
- **Pinning**: What is a pinning condition and what happens with a Virtual Thread during the pinning period?
- **Stack**: In the execution of a Virtual Thread walk, what is the data structure and how does the JVM store it in the heap?
- **Performance**: How does the execution differ from using kernel thread?
- **Limit**: What are the JVM level limitations of virtual threads? Max number of simultaneous, max nesting sequences?