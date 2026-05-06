# Lab 12: Concurrency Basics

## 📋 Lab Header

| Aspect | Details |
|--------|---------|
| **Difficulty** | Intermediate-Advanced |
| **Estimated Time** | 5 hours |
| **Real-World Context** | Building a multi-threaded download manager |
| **Prerequisites** | Lab 11: Streams API (Advanced) |
| **Learning Type** | Hands-on, Project-based |

## 🎯 Learning Objectives

By completing this lab, you will be able to:

1. **Understand thread basics** and lifecycle
2. **Create and manage threads** effectively
3. **Implement thread synchronization** mechanisms
4. **Avoid race conditions** and deadlocks
5. **Use volatile keyword** for visibility
6. **Build a multi-threaded download manager system**

## 📚 Prerequisites

- Lab 11: Streams API (Advanced) completed
- Understanding of functional programming
- Knowledge of collections
- Familiarity with exceptions

## 🧠 Concept Theory

### 1. Thread Basics

Creating and managing threads:

```java
// Creating thread by extending Thread class
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread running: " + Thread.currentThread().getName());
    }
}

MyThread thread = new MyThread();
thread.start();  // Start thread

// Creating thread by implementing Runnable
class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Runnable running");
    }
}

Thread thread2 = new Thread(new MyRunnable());
thread2.start();

// Using lambda expression
Thread thread3 = new Thread(() -> {
    System.out.println("Lambda thread running");
});
thread3.start();

// Getting thread information
System.out.println("Thread name: " + Thread.currentThread().getName());
System.out.println("Thread ID: " + Thread.currentThread().getId());
System.out.println("Thread priority: " + Thread.currentThread().getPriority());
System.out.println("Is alive: " + thread.isAlive());
```

### 2. Thread Lifecycle

Understanding thread states:

```java
// Thread states: NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED

Thread thread = new Thread(() -> {
    try {
        System.out.println("Thread started");
        Thread.sleep(1000);  // TIMED_WAITING
        System.out.println("Thread resumed");
    } catch (InterruptedException e) {
        System.out.println("Thread interrupted");
    }
});

System.out.println("State: " + thread.getState());  // NEW
thread.start();
System.out.println("State: " + thread.getState());  // RUNNABLE

try {
    thread.join();  // Wait for thread to complete
    System.out.println("Thread completed");
} catch (InterruptedException e) {
    e.printStackTrace();
}

// Checking if thread is alive
if (thread.isAlive()) {
    System.out.println("Thread is still running");
} else {
    System.out.println("Thread has finished");
}
```

### 3. Synchronization Basics

Protecting shared resources:

```java
// Synchronized method
class Counter {
    private int count = 0;
    
    public synchronized void increment() {
        count++;
    }
    
    public synchronized int getCount() {
        return count;
    }
}

// Synchronized block
class SafeCounter {
    private int count = 0;
    private Object lock = new Object();
    
    public void increment() {
        synchronized(lock) {
            count++;
        }
    }
    
    public int getCount() {
        synchronized(lock) {
            return count;
        }
    }
}

// Usage
Counter counter = new Counter();
Thread t1 = new Thread(() -> {
    for (int i = 0; i < 1000; i++) {
        counter.increment();
    }
});
Thread t2 = new Thread(() -> {
    for (int i = 0; i < 1000; i++) {
        counter.increment();
    }
});

t1.start();
t2.start();
t1.join();
t2.join();

System.out.println("Count: " + counter.getCount());  // 2000
```

### 4. Volatile Keyword

Ensuring visibility across threads:

```java
// Without volatile - may not see updates
class Flag {
    private boolean running = true;
    
    public void stop() {
        running = false;
    }
    
    public void run() {
        while (running) {
            // May not see the update to running
        }
    }
}

// With volatile - guaranteed visibility
class VolatileFlag {
    private volatile boolean running = true;
    
    public void stop() {
        running = false;
    }
    
    public void run() {
        while (running) {
            // Will see the update to running
        }
    }
}

// Usage
VolatileFlag flag = new VolatileFlag();
Thread worker = new Thread(flag::run);
worker.start();

Thread.sleep(1000);
flag.stop();
worker.join();
```

### 5. Race Conditions

Understanding and preventing race conditions:

```java
// ❌ Race condition - not thread-safe
class UnsafeCounter {
    private int count = 0;
    
    public void increment() {
        count++;  // Not atomic: read, modify, write
    }
}

// ✅ Thread-safe - synchronized
class SafeCounter {
    private int count = 0;
    
    public synchronized void increment() {
        count++;
    }
}

// ✅ Thread-safe - atomic operation
class AtomicCounter {
    private java.util.concurrent.atomic.AtomicInteger count = 
        new java.util.concurrent.atomic.AtomicInteger(0);
    
    public void increment() {
        count.incrementAndGet();
    }
}

// Demonstrating race condition
UnsafeCounter unsafe = new UnsafeCounter();
Thread[] threads = new Thread[10];

for (int i = 0; i < 10; i++) {
    threads[i] = new Thread(() -> {
        for (int j = 0; j < 1000; j++) {
            unsafe.increment();
        }
    });
}

for (Thread t : threads) t.start();
for (Thread t : threads) t.join();

System.out.println("Count: " + unsafe.count);  // Less than 10000
```

### 6. Thread Interruption

Interrupting threads gracefully:

```java
// Checking for interruption
Thread worker = new Thread(() -> {
    try {
        while (!Thread.currentThread().isInterrupted()) {
            System.out.println("Working...");
            Thread.sleep(1000);
        }
    } catch (InterruptedException e) {
        System.out.println("Thread interrupted");
        Thread.currentThread().interrupt();  // Restore interrupt status
    }
});

worker.start();

Thread.sleep(3000);
worker.interrupt();  // Request interruption
worker.join();

// Handling InterruptedException
Thread thread = new Thread(() -> {
    try {
        Thread.sleep(5000);
    } catch (InterruptedException e) {
        System.out.println("Sleep interrupted");
        // Handle interruption
    }
});

thread.start();
thread.interrupt();
```

### 7. Thread Communication

Coordinating between threads:

```java
// Using wait and notify
class SharedResource {
    private int value = 0;
    private boolean ready = false;
    
    public synchronized void produce(int val) {
        value = val;
        ready = true;
        notifyAll();  // Wake up waiting threads
    }
    
    public synchronized int consume() {
        while (!ready) {
            try {
                wait();  // Release lock and wait
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        ready = false;
        return value;
    }
}

// Usage
SharedResource resource = new SharedResource();

Thread producer = new Thread(() -> {
    for (int i = 0; i < 5; i++) {
        resource.produce(i);
        System.out.println("Produced: " + i);
    }
});

Thread consumer = new Thread(() -> {
    for (int i = 0; i < 5; i++) {
        int val = resource.consume();
        System.out.println("Consumed: " + val);
    }
});

producer.start();
consumer.start();
producer.join();
consumer.join();
```

### 8. Deadlock Prevention

Avoiding deadlock situations:

```java
// ❌ Potential deadlock
class Account {
    private int balance;
    
    public synchronized void transfer(Account other, int amount) {
        if (this.balance >= amount) {
            this.balance -= amount;
            other.deposit(amount);  // May cause deadlock
        }
    }
    
    public synchronized void deposit(int amount) {
        this.balance += amount;
    }
}

// ✅ Deadlock prevention - consistent lock ordering
class SafeAccount {
    private int balance;
    private static final Object GLOBAL_LOCK = new Object();
    
    public void transfer(SafeAccount other, int amount) {
        SafeAccount first = this;
        SafeAccount second = other;
        
        // Ensure consistent ordering
        if (System.identityHashCode(first) > System.identityHashCode(second)) {
            SafeAccount temp = first;
            first = second;
            second = temp;
        }
        
        synchronized(first) {
            synchronized(second) {
                if (first.balance >= amount) {
                    first.balance -= amount;
                    second.balance += amount;
                }
            }
        }
    }
}
```

### 9. Thread Safety Patterns

Common patterns for thread safety:

```java
// Immutable objects are thread-safe
final class ImmutablePoint {
    private final int x;
    private final int y;
    
    public ImmutablePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
}

// Thread-local storage
class ThreadLocalExample {
    private static ThreadLocal<String> threadLocal = ThreadLocal.withInitial(() -> "default");
    
    public static void main(String[] args) {
        threadLocal.set("value1");
        
        Thread t = new Thread(() -> {
            threadLocal.set("value2");
            System.out.println(threadLocal.get());  // value2
        });
        
        t.start();
        t.join();
        
        System.out.println(threadLocal.get());  // value1
    }
}

// Copy-on-write for thread safety
java.util.concurrent.CopyOnWriteArrayList<String> list = 
    new java.util.concurrent.CopyOnWriteArrayList<>();
```

### 10. Best Practices

Concurrency programming guidelines:

```java
// ❌ Bad: Excessive synchronization
class BadCounter {
    private int count = 0;
    
    public synchronized void increment() {
        count++;
    }
    
    public synchronized int getCount() {
        return count;
    }
    
    public synchronized void reset() {
        count = 0;
    }
}

// ✅ Good: Minimal synchronization
class GoodCounter {
    private int count = 0;
    private final Object lock = new Object();
    
    public void increment() {
        synchronized(lock) {
            count++;
        }
    }
    
    public int getCount() {
        synchronized(lock) {
            return count;
        }
    }
}

// ✅ Good: Use concurrent utilities
java.util.concurrent.atomic.AtomicInteger atomicCount = 
    new java.util.concurrent.atomic.AtomicInteger(0);
atomicCount.incrementAndGet();

// ❌ Bad: Ignoring InterruptedException
Thread thread = new Thread(() -> {
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        // Ignoring exception
    }
});

// ✅ Good: Handling InterruptedException
Thread thread2 = new Thread(() -> {
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
});
```

---

## 💻 Step-by-Step Coding Tasks

### Task 1: Create and Manage Threads

**Objective**: Implement thread creation and lifecycle management

**Acceptance Criteria**:
- [ ] Create threads using Thread class
- [ ] Create threads using Runnable
- [ ] Use lambda expressions
- [ ] Manage thread lifecycle
- [ ] Code compiles without errors

**Instructions**:
1. Create multiple threads
2. Start threads
3. Monitor thread states
4. Wait for completion
5. Handle interruptions

### Task 2: Implement Synchronization

**Objective**: Protect shared resources with synchronization

**Acceptance Criteria**:
- [ ] Synchronized methods
- [ ] Synchronized blocks
- [ ] Volatile variables
- [ ] Thread safety verified
- [ ] No race conditions

**Instructions**:
1. Create shared resource
2. Implement synchronization
3. Create multiple threads
4. Verify thread safety
5. Test with stress

### Task 3: Prevent Deadlocks

**Objective**: Design systems that avoid deadlocks

**Acceptance Criteria**:
- [ ] Consistent lock ordering
- [ ] Timeout mechanisms
- [ ] Deadlock detection
- [ ] Prevention strategies
- [ ] Verified safe

**Instructions**:
1. Identify deadlock scenarios
2. Implement prevention
3. Test with multiple threads
4. Verify no deadlocks
5. Document strategies

---

## 🎨 Mini-Project: Multi-Threaded Download Manager

### Project Overview

**Description**: Create a multi-threaded download manager with progress tracking and error handling.

**Real-World Application**: Download managers, file transfer systems, concurrent processing.

**Learning Value**: Master thread creation, synchronization, and coordination.

### Project Requirements

#### Functional Requirements
- [ ] Download files concurrently
- [ ] Track download progress
- [ ] Handle errors gracefully
- [ ] Pause and resume downloads
- [ ] Generate reports
- [ ] Manage resources

#### Non-Functional Requirements
- [ ] Thread-safe implementation
- [ ] Proper synchronization
- [ ] Comprehensive documentation
- [ ] Unit tests
- [ ] Error handling

### Project Structure

```
multi-threaded-download-manager/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/learning/
│   │           ├── DownloadTask.java
│   │           ├── DownloadManager.java
│   │           ├── DownloadProgress.java
│   │           ├── DownloadReport.java
│   │           └── Main.java
│   └── test/
│       └── java/
│           └── com/learning/
│               ├── DownloadTaskTest.java
│               └── DownloadManagerTest.java
├── pom.xml
└── README.md
```

### Implementation Guide

#### Step 1: Create DownloadProgress Class

```java
package com.learning;

/**
 * Tracks download progress.
 */
public class DownloadProgress {
    private String fileId;
    private String fileName;
    private long totalSize;
    private long downloadedSize;
    private volatile boolean paused;
    private volatile boolean completed;
    private String status;
    
    /**
     * Constructor for DownloadProgress.
     */
    public DownloadProgress(String fileId, String fileName, long totalSize) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.totalSize = totalSize;
        this.downloadedSize = 0;
        this.paused = false;
        this.completed = false;
        this.status = "Pending";
    }
    
    // Getters
    public String getFileId() { return fileId; }
    public String getFileName() { return fileName; }
    public long getTotalSize() { return totalSize; }
    public long getDownloadedSize() { return downloadedSize; }
    public boolean isPaused() { return paused; }
    public boolean isCompleted() { return completed; }
    public String getStatus() { return status; }
    
    /**
     * Update progress.
     */
    public synchronized void updateProgress(long bytes) {
        downloadedSize += bytes;
        if (downloadedSize >= totalSize) {
            downloadedSize = totalSize;
            completed = true;
            status = "Completed";
        } else {
            status = "Downloading";
        }
    }
    
    /**
     * Get progress percentage.
     */
    public synchronized double getProgressPercentage() {
        return (downloadedSize * 100.0) / totalSize;
    }
    
    /**
     * Pause download.
     */
    public synchronized void pause() {
        paused = true;
        status = "Paused";
    }
    
    /**
     * Resume download.
     */
    public synchronized void resume() {
        paused = false;
        status = "Downloading";
    }
    
    @Override
    public String toString() {
        return String.format("%s: %.2f%% (%d/%d bytes) - %s",
            fileName, getProgressPercentage(), downloadedSize, totalSize, status);
    }
}
```

#### Step 2: Create DownloadTask Class

```java
package com.learning;

/**
 * Represents a download task.
 */
public class DownloadTask implements Runnable {
    private DownloadProgress progress;
    private long simulatedSpeed;  // bytes per iteration
    
    /**
     * Constructor for DownloadTask.
     */
    public DownloadTask(DownloadProgress progress, long simulatedSpeed) {
        this.progress = progress;
        this.simulatedSpeed = simulatedSpeed;
    }
    
    @Override
    public void run() {
        try {
            while (!progress.isCompleted()) {
                synchronized(progress) {
                    if (progress.isPaused()) {
                        progress.wait();  // Wait until resumed
                    }
                }
                
                // Simulate download
                progress.updateProgress(simulatedSpeed);
                Thread.sleep(100);  // Simulate network delay
            }
            
            System.out.println("Download completed: " + progress.getFileName());
        } catch (InterruptedException e) {
            System.out.println("Download interrupted: " + progress.getFileName());
            Thread.currentThread().interrupt();
        }
    }
}
```

#### Step 3: Create DownloadManager Class

```java
package com.learning;

import java.util.*;
import java.util.concurrent.*;

/**
 * Manages multiple downloads.
 */
public class DownloadManager {
    private Map<String, DownloadProgress> downloads;
    private ExecutorService executor;
    private final int maxConcurrentDownloads;
    
    /**
     * Constructor for DownloadManager.
     */
    public DownloadManager(int maxConcurrentDownloads) {
        this.downloads = new ConcurrentHashMap<>();
        this.executor = Executors.newFixedThreadPool(maxConcurrentDownloads);
        this.maxConcurrentDownloads = maxConcurrentDownloads;
    }
    
    /**
     * Start download.
     */
    public void startDownload(String fileId, String fileName, long fileSize, long speed) {
        DownloadProgress progress = new DownloadProgress(fileId, fileName, fileSize);
        downloads.put(fileId, progress);
        
        DownloadTask task = new DownloadTask(progress, speed);
        executor.execute(task);
    }
    
    /**
     * Pause download.
     */
    public void pauseDownload(String fileId) {
        DownloadProgress progress = downloads.get(fileId);
        if (progress != null) {
            progress.pause();
        }
    }
    
    /**
     * Resume download.
     */
    public void resumeDownload(String fileId) {
        DownloadProgress progress = downloads.get(fileId);
        if (progress != null) {
            synchronized(progress) {
                progress.resume();
                progress.notifyAll();
            }
        }
    }
    
    /**
     * Get download progress.
     */
    public DownloadProgress getProgress(String fileId) {
        return downloads.get(fileId);
    }
    
    /**
     * Get all downloads.
     */
    public Collection<DownloadProgress> getAllDownloads() {
        return new ArrayList<>(downloads.values());
    }
    
    /**
     * Display all progress.
     */
    public void displayAllProgress() {
        System.out.println("\n=== Download Progress ===");
        downloads.values().forEach(System.out::println);
        System.out.println("========================\n");
    }
    
    /**
     * Shutdown manager.
     */
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
```

#### Step 4: Create DownloadReport Class

```java
package com.learning;

import java.util.*;

/**
 * Generates download reports.
 */
public class DownloadReport {
    private DownloadManager manager;
    
    /**
     * Constructor for DownloadReport.
     */
    public DownloadReport(DownloadManager manager) {
        this.manager = manager;
    }
    
    /**
     * Generate summary report.
     */
    public void generateSummaryReport() {
        System.out.println("\n========== DOWNLOAD SUMMARY ==========");
        
        Collection<DownloadProgress> downloads = manager.getAllDownloads();
        long totalSize = 0;
        long totalDownloaded = 0;
        int completed = 0;
        
        for (DownloadProgress progress : downloads) {
            totalSize += progress.getTotalSize();
            totalDownloaded += progress.getDownloadedSize();
            if (progress.isCompleted()) {
                completed++;
            }
        }
        
        System.out.println("Total Downloads: " + downloads.size());
        System.out.println("Completed: " + completed);
        System.out.println("In Progress: " + (downloads.size() - completed));
        System.out.println("Total Size: " + formatBytes(totalSize));
        System.out.println("Downloaded: " + formatBytes(totalDownloaded));
        System.out.println("Overall Progress: " + String.format("%.2f%%", 
            (totalDownloaded * 100.0) / totalSize));
        System.out.println("=====================================\n");
    }
    
    /**
     * Format bytes to human readable.
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.2f %sB", bytes / Math.pow(1024, exp), pre);
    }
}
```

#### Step 5: Create Main Class

```java
package com.learning;

/**
 * Main entry point for Multi-Threaded Download Manager.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Create download manager
        DownloadManager manager = new DownloadManager(3);
        
        // Start downloads
        manager.startDownload("F1", "file1.zip", 1000000, 50000);
        manager.startDownload("F2", "file2.iso", 2000000, 40000);
        manager.startDownload("F3", "file3.tar", 1500000, 45000);
        manager.startDownload("F4", "file4.exe", 800000, 35000);
        
        // Monitor progress
        DownloadReport report = new DownloadReport(manager);
        
        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000);
            manager.displayAllProgress();
            report.generateSummaryReport();
            
            // Pause one download
            if (i == 3) {
                manager.pauseDownload("F2");
                System.out.println("Paused F2");
            }
            
            // Resume download
            if (i == 5) {
                manager.resumeDownload("F2");
                System.out.println("Resumed F2");
            }
        }
        
        // Wait for completion
        manager.shutdown();
        System.out.println("All downloads completed!");
    }
}
```

#### Step 6: Create Unit Tests

```java
package com.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DownloadProgress.
 */
public class DownloadProgressTest {
    
    private DownloadProgress progress;
    
    @BeforeEach
    void setUp() {
        progress = new DownloadProgress("F1", "test.zip", 1000);
    }
    
    @Test
    void testProgressUpdate() {
        progress.updateProgress(100);
        assertEquals(100, progress.getDownloadedSize());
        assertEquals(10.0, progress.getProgressPercentage());
    }
    
    @Test
    void testCompletion() {
        progress.updateProgress(1000);
        assertTrue(progress.isCompleted());
        assertEquals("Completed", progress.getStatus());
    }
    
    @Test
    void testPauseResume() {
        progress.pause();
        assertTrue(progress.isPaused());
        assertEquals("Paused", progress.getStatus());
        
        progress.resume();
        assertFalse(progress.isPaused());
        assertEquals("Downloading", progress.getStatus());
    }
}

/**
 * Unit tests for DownloadManager.
 */
public class DownloadManagerTest {
    
    private DownloadManager manager;
    
    @BeforeEach
    void setUp() {
        manager = new DownloadManager(2);
    }
    
    @Test
    void testStartDownload() {
        manager.startDownload("F1", "test.zip", 1000, 100);
        assertNotNull(manager.getProgress("F1"));
    }
    
    @Test
    void testPauseResume() {
        manager.startDownload("F1", "test.zip", 1000, 100);
        manager.pauseDownload("F1");
        assertTrue(manager.getProgress("F1").isPaused());
        
        manager.resumeDownload("F1");
        assertFalse(manager.getProgress("F1").isPaused());
    }
}
```

### Running the Project

```bash
# Compile
mvn clean compile

# Run tests
mvn test

# Run the application
mvn exec:java -Dexec.mainClass="com.learning.Main"
```

---

## 📝 Exercises

### Exercise 1: Thread-Safe Counter

**Objective**: Implement a thread-safe counter

**Task Description**:
Create counter that handles concurrent increments safely

**Acceptance Criteria**:
- [ ] Thread-safe implementation
- [ ] Correct results
- [ ] No race conditions
- [ ] Performance acceptable
- [ ] Tests pass

### Exercise 2: Producer-Consumer Pattern

**Objective**: Implement producer-consumer pattern

**Task Description**:
Create system with producers and consumers sharing queue

**Acceptance Criteria**:
- [ ] Producers add items
- [ ] Consumers remove items
- [ ] Thread-safe queue
- [ ] No deadlocks
- [ ] Correct ordering

### Exercise 3: Thread Pool Task Executor

**Objective**: Create custom thread pool

**Task Description**:
Implement thread pool for executing tasks

**Acceptance Criteria**:
- [ ] Thread pool created
- [ ] Task execution
- [ ] Queue management
- [ ] Proper shutdown
- [ ] Error handling

---

## 🧪 Quiz

### Question 1: What is a race condition?

A) When threads run at same speed  
B) When multiple threads access shared resource unsafely  
C) When threads compete for CPU  
D) When threads are interrupted  

**Answer**: B) When multiple threads access shared resource unsafely

### Question 2: What does synchronized do?

A) Runs code in parallel  
B) Ensures only one thread accesses code at a time  
C) Speeds up execution  
D) Prevents interruption  

**Answer**: B) Ensures only one thread accesses code at a time

### Question 3: What is the volatile keyword for?

A) Making variables mutable  
B) Ensuring visibility across threads  
C) Preventing synchronization  
D) Improving performance  

**Answer**: B) Ensuring visibility across threads

### Question 4: What causes a deadlock?

A) Too many threads  
B) Circular wait for locks  
C) Slow execution  
D) Memory issues  

**Answer**: B) Circular wait for locks

### Question 5: What does Thread.join() do?

A) Starts a thread  
B) Stops a thread  
C) Waits for thread to complete  
D) Pauses a thread  

**Answer**: C) Waits for thread to complete

---

## 🚀 Advanced Challenge

### Challenge: Complete Thread Pool System

**Difficulty**: Advanced

**Objective**: Build comprehensive thread pool with advanced features

**Requirements**:
- [ ] Custom thread pool
- [ ] Task queue management
- [ ] Worker threads
- [ ] Graceful shutdown
- [ ] Monitoring and metrics
- [ ] Error handling

---

## 🏆 Best Practices

### Concurrency Programming

1. **Synchronization**
   - Minimize synchronized blocks
   - Use appropriate locks
   - Avoid nested locks

2. **Thread Safety**
   - Immutable objects
   - Thread-local storage
   - Atomic operations

3. **Deadlock Prevention**
   - Consistent lock ordering
   - Timeout mechanisms
   - Resource management

---

## 🔗 Next Steps

**Next Lab**: [Lab 13: Thread Pools and Executors](../13-thread-pools-executors/README.md)

---

## ✅ Completion Checklist

- [ ] Completed all coding tasks
- [ ] Built download manager
- [ ] Solved all exercises
- [ ] Passed quiz (80%+)
- [ ] Attempted advanced challenge
- [ ] Reviewed best practices

---

**Congratulations on completing Lab 12! 🎉**

You've mastered thread basics and synchronization. Ready for thread pools? Move on to [Lab 13: Thread Pools and Executors](../13-thread-pools-executors/README.md).