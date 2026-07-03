# Module 50: Virtual Threads (Project Loom) - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: Why did Java introduce Virtual Threads when we already had asynchronous programming (CompletableFutures) and Reactive Streams (Project Reactor)?
**Answer**:
Asynchronous and Reactive frameworks were introduced to solve the scaling limitations of Platform Threads (the "Thread-per-request" bottleneck). However, they forced developers to adopt a radically different, complex programming model:
- Code is no longer executed sequentially.
- Control flow statements (`if`, `while`, `try-catch`) cannot be used normally.
- Stack traces become completely unreadable because logic jumps across dozens of worker threads.
Virtual Threads were introduced to give developers the best of both worlds: The massive scalability and non-blocking I/O of Reactive programming, but written in the traditional, easy-to-read, synchronous, blocking, thread-per-request style that Java developers are used to.

### Q2: What is a "Carrier Thread" in the context of Virtual Threads?
**Answer**:
A Carrier Thread is the actual, physical OS Platform Thread that the JVM uses to execute a Virtual Thread. The JVM maintains a small pool of Carrier Threads (typically a `ForkJoinPool` sized to the number of CPU cores). When a Virtual Thread has actual CPU work to do, it is "mounted" onto a Carrier Thread. When the Virtual Thread hits a blocking I/O operation, the JVM saves the Virtual Thread's state to the heap and "unmounts" it, allowing the Carrier Thread to execute a completely different Virtual Thread.

### Q3: What is "Structured Concurrency" in Java?
**Answer**:
Structured Concurrency (introduced alongside Virtual Threads) is an API paradigm that treats multiple concurrent tasks running in different threads as a single logical unit of work.
Historically, if a method spawned two threads via an `ExecutorService`, and the first thread crashed, the second thread would keep running in the background as a "zombie" or "orphan" task. Structured Concurrency introduces `StructuredTaskScope`. It links the lifecycles of sub-tasks to the parent scope, ensuring that if one sub-task fails, the remaining sibling tasks are automatically cancelled and cleaned up, dramatically reducing thread leaks and simplifying error handling.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Refactoring for Structured Concurrency
**Problem**: You have a REST endpoint that needs to fetch User details from API-1 and User Orders from API-2 concurrently. Both take 2 seconds. The current code uses `CompletableFuture`, but it's hard to read and doesn't handle failures cleanly. Rewrite it using Java's new `StructuredTaskScope`.

**Solution**:
```java
// Assumes running with preview features enabled
public UserProfile fetchProfile(String userId) throws InterruptedException, ExecutionException {
    
    // ShutdownOnFailure automatically cancels all remaining forks if one throws an exception
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
        
        // Fork the concurrent tasks
        StructuredTaskScope.Subtask<User> userTask = scope.fork(() -> fetchUser(userId));
        StructuredTaskScope.Subtask<List<Order>> ordersTask = scope.fork(() -> fetchOrders(userId));
        
        // Wait for all tasks to finish (or one to fail)
        scope.join();
        
        // Throw an exception if any task failed
        scope.throwIfFailed();
        
        // Both succeeded safely, construct the result
        return new UserProfile(userTask.get(), ordersTask.get());
    }
}
```