# Thread Pools Code Deep Dive

This lab demonstrates how to configure a custom `ThreadPoolExecutor` and observe its dynamic scaling and rejection behavior.

## 💻 Pure Java Implementation

```java file="labs/java/concurrency/thread-pools/SOLUTION/CustomThreadPoolDemo.java"
package java.concurrency.threadpools;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A demonstration of ThreadPoolExecutor configuration and behavior.
 */
public class CustomThreadPoolDemo {

    public static void main(String[] args) throws InterruptedException {
        // Configuration
        int corePoolSize = 2;
        int maxPoolSize = 5;
        int queueCapacity = 10;
        long keepAliveTime = 1; // 1 second for idle threads to die

        // Create a custom executor
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueCapacity),
                new SimpleThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy() // Producer runs task if pool full
        );

        System.out.println("--- Starting Task Submission ---");
        
        // Submit 20 tasks to see the lifecycle
        for (int i = 1; i <= 20; i++) {
            int taskId = i;
            executor.submit(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + " executing Task " + taskId);
                    Thread.sleep(500); // Simulate work
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            
            printStats(executor);
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
    }

    private static void printStats(ThreadPoolExecutor e) {
        System.out.printf("[STATS] Active: %d | Pool: %d | Queue: %d%n",
                e.getActiveCount(), e.getPoolSize(), e.getQueue().size());
    }

    static class SimpleThreadFactory implements ThreadFactory {
        private final AtomicInteger counter = new AtomicInteger(1);
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Worker-" + counter.getAndIncrement());
        }
    }
}
```

## 🔍 Key Takeaways
1. **The Scaling Trigger**: If you run this code, watch the stats. 
   - Task 1-2: `Pool Size` grows to 2 (Core Size).
   - Task 3-12: `Pool Size` stays at 2, but `Queue` fills up to 10.
   - Task 13-15: `Pool Size` grows from 2 up to 5 (Max Size).
   - Task 16-20: The `CallerRunsPolicy` kicks in. You will see `main` thread executing tasks!
2. **Graceful Shutdown**: Always call `shutdown()` or `shutdownNow()`. If you don't, the worker threads (which are non-daemon by default) will keep the JVM alive forever, even after `main` finishes.
3. **Thread Factory**: Using a custom `ThreadFactory` is a best practice. It allows you to give meaningful names to your threads, which is vital for debugging stack traces and using profilers.