# Pedagogic Guide: Advanced Thread Pools

## 1. Module Overview
This module is a critical rite of passage for Java developers moving from building functional applications to building *resilient* applications. It shatters the illusion that `Executors.newFixedThreadPool()` is safe for production and forces learners to confront the physical realities of memory limits, CPU context switching, and backpressure.

## 2. Learning Paths

### Path A: The Backend Developer (Focus: Resilience & Backpressure)
**Target Audience**: Developers building REST APIs, microservices, or message queue consumers.
*   **Focus**: `DEEP_DIVE.md` (ThreadPoolExecutor internals, Rejection Policies) and `EDGE_CASES.md` (OOM Traps).
*   **Key Takeaway**: Understanding the exact sequence of task submission (Core -> Queue -> Max -> Reject) and how to use `CallerRunsPolicy` to prevent the system from collapsing under heavy load.

### Path B: The Data/Compute Engineer (Focus: CPU Optimization)
**Target Audience**: Senior developers building batch processors, parallel algorithms, or working with massive datasets.
*   **Focus**: `DEEP_DIVE.md` (ForkJoinPool, Work Stealing) and `MINI_PROJECT.md`.
*   **Key Takeaway**: Mastering the divide-and-conquer paradigm, understanding why `ForkJoinPool` minimizes lock contention, and the absolute necessity of keeping blocking I/O out of the common pool.

## 3. Teaching Strategies

### The "Restaurant Seating" Metaphor
To explain the `ThreadPoolExecutor` submission logic (which is highly unintuitive to beginners):
*   **Core Pool**: The main dining room (e.g., 5 tables).
*   **Queue**: The waiting area bench (e.g., 10 seats).
*   **Max Pool**: The emergency patio seating (e.g., 5 extra tables).
*   **Scenario**: 
    1. First 5 customers arrive -> Seated in the main dining room (Core Pool).
    2. Customer 6 arrives -> The restaurant doesn't immediately open the patio. They ask the customer to sit on the bench (Queue).
    3. Customers 7-15 arrive -> They all sit on the bench. The bench is now full.
    4. Customer 16 arrives -> The bench is full! *Now* the manager opens an emergency patio table (Max Pool) to handle the overflow.
    5. Customer 21 arrives -> Main room full, bench full, patio full. The bouncer turns them away (RejectedExecutionHandler).
This metaphor perfectly explains why the pool doesn't grow to `maxPoolSize` until the queue is completely full.

### The "Supermarket Checkout" Metaphor
To explain `ForkJoinPool` Work Stealing:
In a standard `ThreadPoolExecutor`, there is one massive checkout line and 5 cashiers. Everyone fights to get to the front of the line (lock contention).
In a `ForkJoinPool`, there are 5 cashiers, each with their own line (deque). Customers (tasks) go to a specific line. If Cashier A finishes all their customers, they don't just stand there. They walk over to Cashier B's line, go to the *back* of the line (the tail of the deque), and pull a customer over to their register. This minimizes bumping into Cashier B, who is working at the front of the line (the head of the deque).

## 4. Common Mental Blocks & Clarifications

### Block 1: "Why does `Executors.newFixedThreadPool()` cause OOM?"
*   **Clarification**: Show the source code. Beginners assume "Fixed" means the pool is bounded and safe. Point out the `new LinkedBlockingQueue<Runnable>()` call. Explain that without a capacity argument, the queue accepts 2 billion tasks. If tasks take 1 second to process but arrive at 100 per second, the queue grows infinitely.

### Block 2: "Why did my parallel stream hang?"
*   **Clarification**: This is the Common Pool deadlock. Explain that `parallelStream()` is a shared global resource. If one developer writes a parallel stream that makes an HTTP call (which takes 2 seconds), they have hijacked the entire JVM's common pool for those 2 seconds. Every other parallel stream in the application must wait. Rule: Never block in the common pool.

### Block 3: "Where did my exception go?"
*   **Clarification**: When you `submit()` a task, the thread pool wraps it in a `FutureTask`. If an exception occurs, the `FutureTask` catches it, saves it internally, and terminates cleanly. If the developer never calls `future.get()`, the exception is swallowed forever. Show them how to use `execute()` instead, or how to properly unwrap exceptions from `Future.get()`.

## 5. Assessment Strategy
*   **Formative**: Provide a `ThreadPoolExecutor` configuration (Core: 2, Max: 4, Queue: 2). Ask the learner: "If I submit 5 tasks simultaneously, how many threads will be created, and where will the tasks go?" (Answer: 2 threads created for tasks 1 & 2. Tasks 3 & 4 go to the queue. Task 5 forces a 3rd thread to be created).
*   **Summative**: The `MINI_PROJECT.md` requires the learner to build a custom thread pool and observe the `CallerRunsPolicy` in action, proving they understand how to configure backpressure and avoid the traps of the `Executors` factory methods.