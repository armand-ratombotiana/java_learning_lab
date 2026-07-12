# Virtual Threads Code Deep Dive

This lab demonstrates how to create Virtual Threads and showcases their massive throughput advantage over Platform (OS) Threads.

## 💻 Pure Java Implementation

```java file="labs/java/concurrency/virtual-threads/SOLUTION/VirtualThreadDemo.java"
package java.concurrency.virtualthreads;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A demonstration of Virtual Threads vs Platform Threads.
 */
public class VirtualThreadDemo {

    public static void main(String[] args) {
        int numberOfTasks = 10_000;
        
        System.out.println("Starting 10,000 Platform Threads...");
        runTasks(Executors.newCachedThreadPool(), numberOfTasks, "Platform Threads");

        System.out.println("\nStarting 10,000 Virtual Threads...");
        runTasks(Executors.newVirtualThreadPerTaskExecutor(), numberOfTasks, "Virtual Threads");
    }

    private static void runTasks(ExecutorService executor, int numberOfTasks, String type) {
        AtomicInteger completedTasks = new AtomicInteger(0);
        Instant start = Instant.now();

        try (executor) {
            for (int i = 0; i < numberOfTasks; i++) {
                executor.submit(() -> {
                    try {
                        // Simulate a blocking I/O operation (e.g., a database query)
                        Thread.sleep(Duration.ofSeconds(1));
                        completedTasks.incrementAndGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        } // Executor service auto-closes and waits for termination here

        Instant end = Instant.now();
        System.out.printf("%s completed %d tasks in %d ms.%n", 
            type, completedTasks.get(), Duration.between(start, end).toMillis());
    }
}
```

## 🔍 Key Takeaways
1. **The API is Identical**: Notice that the code inside the lambda `() -> { Thread.sleep(...) }` is exactly the same for both executors. Virtual Threads use the exact same `java.lang.Thread` API as Platform Threads. This means existing synchronous code can be ported to Virtual Threads with almost zero effort.
2. **The Performance Difference**: If you run this code, the Platform Threads will likely take several seconds (or crash your JVM with an `OutOfMemoryError` if you increase the task count to 100,000). The Virtual Threads will complete in almost exactly 1 second. Why? Because the JVM spawns 10,000 Virtual Threads instantly, they all hit `Thread.sleep` and unmount, and then 1 second later they all wake up and finish.
3. **No Thread Pools for Virtual Threads**: Notice we use `Executors.newVirtualThreadPerTaskExecutor()`. We *never* pool Virtual Threads. They are so cheap that we simply create a new one for every single task and throw it away when it's done.