# Pedagogic Guide: Advanced Virtual Threads

## 1. Module Overview
This module introduces the most significant architectural change to Java since Generics (Java 5) or Lambdas (Java 8). It allows developers to "have their cake and eat it too"—writing simple, synchronous, blocking code that achieves the massive scalability of complex reactive frameworks. 

## 2. Learning Paths

### Path A: The Spring Boot / Web Developer (Focus: Migration & Usage)
**Target Audience**: Developers looking to upgrade existing web applications to Java 21+ for performance gains.
*   **Focus**: `DEEP_DIVE.md` (Migration Strategies) and `EDGE_CASES.md` (Thread Pinning, ThreadLocal Exhaustion).
*   **Key Takeaway**: Understanding that migrating to Virtual Threads is usually a 1-line configuration change, *provided* they have audited their codebase and dependencies for `synchronized` blocks and excessive `ThreadLocal` usage.

### Path B: The Systems Architect (Focus: Orchestration & Concurrency Models)
**Target Audience**: Senior developers designing complex microservice orchestrators or high-throughput batch processors.
*   **Focus**: `MINI_PROJECT.md` (Structured Concurrency) and `INTERVIEW_PREP.md` (Scoped Values).
*   **Key Takeaway**: Mastering `StructuredTaskScope` to cleanly manage the lifecycle of thousands of concurrent subtasks, treating them as a single logical unit of work.

## 3. Teaching Strategies

### The "Delivery Driver" Metaphor
To explain Virtual Thread unmounting:
*   **Platform Thread**: A delivery driver (OS Thread) takes a package (Task). They drive to the customer's house, ring the doorbell, and *wait on the porch* for 10 minutes until the customer answers. While waiting, the driver and the delivery truck are completely blocked from doing other work.
*   **Virtual Thread**: The delivery driver (Carrier Thread) takes a package (Virtual Thread). They ring the doorbell. Instead of waiting, they drop the package on the porch (Unmount to Heap), get back in the truck, and deliver 50 other packages. When the customer finally opens the door (I/O completes), the driver swings back by, picks up the signature (Mounts), and finishes the job. 

### The "Orphaned Thread" Demonstration
To explain Structured Concurrency, write a standard `ExecutorService` example where a parent thread spawns 3 child threads. Have the parent thread throw an exception and die immediately.
Show the learner that the 3 child threads keep running in the background, consuming CPU and network resources for a response that will never be used.
Then, show the same code using `StructuredTaskScope.ShutdownOnFailure()`. Show how the parent's failure instantly cancels the children. This visualizes the concept of "treating concurrent tasks as a single unit of work."

## 4. Common Mental Blocks & Clarifications

### Block 1: "If Virtual Threads are so great, why ever use Platform Threads?"
*   **Clarification**: Emphasize the difference between I/O-bound and CPU-bound tasks. Virtual threads only help when a thread is *waiting*. If you are calculating Pi to a million digits (CPU-bound), the Virtual Thread never unmounts. It just hogs the carrier thread. If you spawn 10,000 Virtual Threads to calculate Pi, the JVM spends all its time context-switching between them on the limited carrier threads, making it *slower* than a standard thread pool.

### Block 2: "Why does `synchronized` break Virtual Threads but `ReentrantLock` doesn't?"
*   **Clarification**: Explain that `synchronized` is deeply integrated into the JVM's C++ codebase (Object Monitors) and is tied to the physical OS thread memory address. `ReentrantLock` is just a Java object (using `LockSupport.park()`). The JVM developers rewrote `LockSupport.park()` to be Virtual Thread-aware, allowing it to unmount safely.

### Block 3: "Why shouldn't I pool Virtual Threads to limit database connections?"
*   **Clarification**: This is a very common mistake. Developers use thread pools for two reasons: 1) Threads are expensive to create. 2) To limit concurrency (e.g., only 10 DB connections). 
Virtual threads solve #1 (they are cheap). But if you pool them to solve #2, you break the virtual thread model. Explain that to limit concurrency with Virtual Threads, you should use a `Semaphore(10)`. Spawn 1,000,000 Virtual Threads, but have them all acquire the Semaphore before hitting the DB.

## 5. Assessment Strategy
*   **Formative**: Ask the learner to identify the bug in a code snippet where a Virtual Thread performs an HTTP call inside a `synchronized` method. (Answer: Thread Pinning).
*   **Summative**: The `MINI_PROJECT.md` requires the learner to build a Weather Aggregator using `StructuredTaskScope`. They must demonstrate the Fail-Fast policy working correctly when one of the simulated network calls throws an exception.