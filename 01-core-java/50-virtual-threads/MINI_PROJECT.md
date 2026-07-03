# Module 50: Virtual Threads (Project Loom) - Mini Project

**Project Name**: The 100,000 Thread Concurrent Downloader  
**Difficulty Level**: Advanced  
**Estimated Time**: 2-3 hours

---

## 🎯 Objective
Experience the raw scalability of Java 21's Virtual Threads by creating an application that attempts to execute 100,000 concurrent blocking I/O tasks. Compare the outcome against traditional Platform Threads.

## 📝 Requirements

### Core Features

1. **The Blocking Task**:
   - Create a method `simulateDownload(int id)`.
   - The method should log "Starting download " + id, sleep for 2000ms using `Thread.sleep()`, and then return a success string.

2. **The Platform Thread Runner (The Crash)**:
   - Create a method `runWithPlatformThreads()`.
   - Attempt to run 100,000 iterations of `simulateDownload` by creating raw `new Thread(Runnable).start()` or using `Executors.newCachedThreadPool()`.
   - *Expectation*: The JVM will either crash with `OutOfMemoryError: unable to create new native thread`, or it will bring your OS to a grinding, unresponsive halt. (Be prepared to kill the process!).

3. **The Virtual Thread Runner (The Success)**:
   - Create a method `runWithVirtualThreads()`.
   - Use a `try-with-resources` block to instantiate `Executors.newVirtualThreadPerTaskExecutor()`.
   - Submit 100,000 iterations of `simulateDownload`.
   - Capture the start time and end time.
   - *Expectation*: The program should finish successfully in just a few seconds (a little over 2000ms), seamlessly multiplexing all 100,000 blocking operations across your computer's CPU cores.

4. **Pinning Experiment (Bonus)**:
   - Inside `simulateDownload`, wrap the `Thread.sleep` inside a `synchronized(this)` block. Run the Virtual Thread test again. Note the massive performance degradation caused by pinning. Fix it by replacing `synchronized` with `ReentrantLock`.

---

## 💡 Solution Blueprint

```java
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;

public class VirtualThreadScaleTest {

    public static void main(String[] args) {
        System.out.println("Starting Virtual Thread Test...");
        Instant start = Instant.now();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 100_000; i++) {
                final int id = i;
                executor.submit(() -> {
                    // Simulating a slow HTTP call
                    Thread.sleep(Duration.ofSeconds(2));
                    return "File " + id + " downloaded";
                });
            }
        } // Block automatically waits for all 100k tasks to finish

        Instant end = Instant.now();
        System.out.println("Completed in " + Duration.between(start, end).toMillis() + " ms");
    }
}
```