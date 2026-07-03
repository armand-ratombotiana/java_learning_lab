# Module 50: Virtual Threads (Project Loom) - Deep Dive

**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-49 (especially Concurrency and Java 21 Features)  
**Estimated Reading Time**: 60 minutes  

---

## 📚 Table of Contents

1. [The Problem with Platform Threads](#problem)
2. [What are Virtual Threads?](#virtual-threads)
3. [How Virtual Threads Work Under the Hood](#mechanics)
4. [Creating and Using Virtual Threads](#usage)
5. [Structured Concurrency](#structured-concurrency)

---

## 1. The Problem with Platform Threads <a name="problem"></a>
Historically, every `java.lang.Thread` was a "Platform Thread," mapped 1:1 to an Operating System (OS) thread. OS threads are expensive:
- They require a large chunk of memory (often 1MB for the call stack).
- Context switching between OS threads is slow (requiring kernel involvement).
- As a result, a typical server can only support a few thousand concurrent threads before running out of memory. This forced developers to adopt complex, difficult-to-debug asynchronous/reactive programming models (like Spring WebFlux) to handle high concurrency.

---

## 2. What are Virtual Threads? <a name="virtual-threads"></a>
Virtual Threads (introduced in Project Loom, standard in Java 21) are lightweight threads managed entirely by the Java Virtual Machine (JVM), not the OS.
- They are incredibly cheap to create (consuming only bytes of memory).
- You can easily run **millions** of virtual threads concurrently on a standard laptop.
- They allow developers to write high-throughput concurrent code in a simple, synchronous, blocking style.

---

## 3. How Virtual Threads Work Under the Hood <a name="mechanics"></a>
Virtual Threads run on top of a small pool of Platform Threads (called "Carrier Threads"), usually sized to the number of CPU cores.
When a Virtual Thread executes a blocking operation (like `Thread.sleep()`, a JDBC database query, or a REST API call), the JVM automatically **unmounts** the Virtual Thread from the Carrier Thread. The Carrier Thread is immediately freed to run a different Virtual Thread. When the blocking operation completes, the original Virtual Thread is **remounted** onto an available Carrier Thread and resumes execution.

---

## 4. Creating and Using Virtual Threads <a name="usage"></a>
Virtual threads are daemon threads by default and have no dedicated thread group.

```java
// Method 1: The Builder API
Thread vThread = Thread.ofVirtual().start(() -> {
    System.out.println("Running in a virtual thread");
});

// Method 2: Executor Service
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    for (int i = 0; i < 100_000; i++) {
        executor.submit(() -> {
            // Simulate blocking I/O
            Thread.sleep(Duration.ofSeconds(1));
            return "Done";
        });
    }
} // The try-with-resources block waits for all tasks to complete
```

---

## 5. Structured Concurrency <a name="structured-concurrency"></a>
Structured Concurrency (currently a preview feature) simplifies multi-threaded code by treating multiple concurrent tasks running in different threads as a single unit of work. It helps with error handling and cancellation.

```java
// Example conceptual flow using StructuredTaskScope
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Supplier<String> user  = scope.fork(() -> fetchUser());
    Supplier<String> order = scope.fork(() -> fetchOrder());

    scope.join();           // Join both subtasks
    scope.throwIfFailed();  // Propagate errors if any subtask failed

    // Both succeeded
    System.out.println(user.get() + " ordered " + order.get());
}
```