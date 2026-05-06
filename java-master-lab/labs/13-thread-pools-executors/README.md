# Lab 13: Thread Pools and Executors

## 📋 Lab Header

| Aspect | Details |
|--------|---------|
| **Difficulty** | Intermediate-Advanced |
| **Estimated Time** | 4 hours |
| **Real-World Context** | Building a task scheduling system |
| **Prerequisites** | Lab 12: Concurrency Basics |
| **Learning Type** | Hands-on, Project-based |

## 🎯 Learning Objectives

By completing this lab, you will be able to:

1. **Understand the Executor framework** and its benefits
2. **Create and configure thread pools** effectively
3. **Use ExecutorService** for task management
4. **Work with Future and Callable** for results
5. **Implement ScheduledExecutor** for delayed tasks
6. **Build a task scheduling system** with thread pools

## 📚 Prerequisites

- Lab 12: Concurrency Basics completed
- Understanding of threads and synchronization
- Knowledge of functional interfaces
- Familiarity with exceptions

## 🧠 Concept Theory

### 1. Executor Framework

Managing threads with executors:

```java
import java.util.concurrent.*;

// Creating executors
Executor executor = Executors.newSingleThreadExecutor();
executor.execute(() -> System.out.println("Task executed"));

// ExecutorService - more control
ExecutorService service = Executors.newFixedThreadPool(3);
service.execute(() -> System.out.println("Task 1"));
service.execute(() -> System.out.println("Task 2"));

// Shutdown
service.shutdown();  // Graceful shutdown
service.awaitTermination(10, TimeUnit.SECONDS);

// Force shutdown
service.shutdownNow();
```

### 2. Thread Pool Types

Different executor configurations:

```java
// Single thread executor
ExecutorService single = Executors.newSingleThreadExecutor();

// Fixed thread pool
ExecutorService fixed = Executors.newFixedThreadPool(5);

// Cached thread pool - creates threads as needed
ExecutorService cached = Executors.newCachedThreadPool();

// Work stealing pool - for parallel tasks
ExecutorService workStealing = Executors.newWorkStealingPool();

// Scheduled executor
ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(2);

// Custom thread pool
ThreadPoolExecutor custom = new ThreadPoolExecutor(
    2,      // Core threads
    5,      // Max threads
    60,     // Keep alive time
    TimeUnit.SECONDS,
    new LinkedBlockingQueue<>()
);
```

### 3. ExecutorService

Submitting and managing tasks:

```java
ExecutorService service = Executors.newFixedThreadPool(3);

// Submit runnable
service.submit(() -> System.out.println("Task executed"));

// Submit callable with result
Future<Integer> future = service.submit(() -> {
    Thread.sleep(1000);
    return 42;
});

// Get result
try {
    Integer result = future.get();  // Blocks until result available
    System.out.println("Result: " + result);
} catch (InterruptedException | ExecutionException e) {
    e.printStackTrace();
}

// Check if done
if (future.isDone()) {
    System.out.println("Task completed");
}

// Cancel task
boolean cancelled = future.cancel(true);  // true = interrupt if running

// Submit multiple tasks
List<Callable<Integer>> tasks = Arrays.asList(
    () -> 1,
    () -> 2,
    () -> 3
);

List<Future<Integer>> futures = service.invokeAll(tasks);

// Get first result
Integer firstResult = service.invokeAny(tasks);

service.shutdown();
```

### 4. Future and Callable

Working with results:

```java
// Callable - returns result
Callable<String> callable = () -> {
    Thread.sleep(1000);
    return "Result";
};

ExecutorService service = Executors.newSingleThreadExecutor();
Future<String> future = service.submit(callable);

// Waiting for result
try {
    String result = future.get();  // Blocks
    System.out.println(result);
} catch (InterruptedException e) {
    System.out.println("Task interrupted");
} catch (ExecutionException e) {
    System.out.println("Task failed: " + e.getCause());
}

// Timeout
try {
    String result = future.get(2, TimeUnit.SECONDS);
} catch (TimeoutException e) {
    System.out.println("Task took too long");
}

// Checking status
if (!future.isDone()) {
    System.out.println("Still running");
}

if (future.isCancelled()) {
    System.out.println("Task was cancelled");
}

service.shutdown();
```

### 5. ScheduledExecutor

Scheduling delayed and periodic tasks:

```java
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

// Schedule once after delay
scheduler.schedule(
    () -> System.out.println("Delayed task"),
    2,
    TimeUnit.SECONDS
);

// Schedule with result
ScheduledFuture<Integer> future = scheduler.schedule(
    () -> 42,
    1,
    TimeUnit.SECONDS
);

// Schedule periodic task
scheduler.scheduleAtFixedRate(
    () -> System.out.println("Periodic task"),
    0,      // Initial delay
    1,      // Period
    TimeUnit.SECONDS
);

// Schedule with fixed delay between executions
scheduler.scheduleWithFixedDelay(
    () -> System.out.println("Task"),
    0,      // Initial delay
    1,      // Delay between executions
    TimeUnit.SECONDS
);

// Shutdown
scheduler.shutdown();
```

### 6. Thread Pool Configuration

Tuning thread pools:

```java
// Core threads - always kept alive
// Max threads - maximum pool size
// Keep alive time - how long to keep idle threads

ThreadPoolExecutor executor = new ThreadPoolExecutor(
    2,                          // Core threads
    5,                          // Max threads
    60,                         // Keep alive time
    TimeUnit.SECONDS,
    new LinkedBlockingQueue<>(100),  // Queue
    Executors.defaultThreadFactory(),
    new ThreadPoolExecutor.AbortPolicy()  // Rejection policy
);

// Rejection policies
// AbortPolicy - throws exception
// CallerRunsPolicy - caller thread executes task
// DiscardPolicy - discards task
// DiscardOldestPolicy - discards oldest task

// Monitoring
System.out.println("Active threads: " + executor.getActiveCount());
System.out.println("Pool size: " + executor.getPoolSize());
System.out.println("Queue size: " + executor.getQueue().size());
System.out.println("Completed tasks: " + executor.getCompletedTaskCount());

executor.shutdown();
```

### 7. CompletableFuture

Advanced async operations:

```java
// Create completed future
CompletableFuture<Integer> future = CompletableFuture.completedFuture(42);

// Async computation
CompletableFuture<Integer> async = CompletableFuture.supplyAsync(() -> {
    return 42;
});

// Chaining operations
CompletableFuture<String> chain = async
    .thenApply(n -> "Number: " + n)
    .thenApply(String::toUpperCase);

// Combining futures
CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(() -> 10);
CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(() -> 20);

CompletableFuture<Integer> combined = f1.thenCombine(f2, (a, b) -> a + b);

// Handling exceptions
CompletableFuture<Integer> withError = CompletableFuture.supplyAsync(() -> {
    throw new RuntimeException("Error");
}).exceptionally(e -> {
    System.out.println("Error: " + e.getMessage());
    return 0;
});

// Waiting for result
try {
    Integer result = combined.get();
    System.out.println(result);
} catch (InterruptedException | ExecutionException e) {
    e.printStackTrace();
}
```

### 8. Error Handling

Managing exceptions in thread pools:

```java
ExecutorService service = Executors.newFixedThreadPool(2);

// Handling exceptions from Callable
Future<Integer> future = service.submit(() -> {
    if (Math.random() > 0.5) {
        throw new RuntimeException("Random error");
    }
    return 42;
});

try {
    Integer result = future.get();
} catch (ExecutionException e) {
    System.out.println("Task failed: " + e.getCause().getMessage());
} catch (InterruptedException e) {
    System.out.println("Task interrupted");
}

// Uncaught exception handler
Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
    System.out.println("Uncaught exception in " + thread.getName());
    exception.printStackTrace();
});

service.shutdown();
```

### 9. Performance Optimization

Optimizing thread pool usage:

```java
// ❌ Bad: Creating new thread for each task
for (int i = 0; i < 1000; i++) {
    new Thread(() -> doWork()).start();
}

// ✅ Good: Using thread pool
ExecutorService service = Executors.newFixedThreadPool(10);
for (int i = 0; i < 1000; i++) {
    service.submit(() -> doWork());
}
service.shutdown();

// ❌ Bad: Blocking on get()
List<Future<Integer>> futures = new ArrayList<>();
for (int i = 0; i < 100; i++) {
    futures.add(service.submit(() -> expensiveOperation()));
}
for (Future<Integer> future : futures) {
    Integer result = future.get();  // Blocks
}

// ✅ Good: Using invokeAll
List<Callable<Integer>> tasks = new ArrayList<>();
for (int i = 0; i < 100; i++) {
    tasks.add(() -> expensiveOperation());
}
List<Future<Integer>> futures = service.invokeAll(tasks);

// ✅ Good: Using CompletableFuture
List<CompletableFuture<Integer>> completables = new ArrayList<>();
for (int i = 0; i < 100; i++) {
    completables.add(CompletableFuture.supplyAsync(() -> expensiveOperation()));
}
CompletableFuture.allOf(completables.toArray(new CompletableFuture[0])).join();
```

### 10. Best Practices

Thread pool programming guidelines:

```java
// ❌ Bad: Not shutting down executor
ExecutorService service = Executors.newFixedThreadPool(5);
service.submit(() -> System.out.println("Task"));
// Missing shutdown

// ✅ Good: Proper shutdown
ExecutorService service = Executors.newFixedThreadPool(5);
try {
    service.submit(() -> System.out.println("Task"));
} finally {
    service.shutdown();
    try {
        if (!service.awaitTermination(10, TimeUnit.SECONDS)) {
            service.shutdownNow();
        }
    } catch (InterruptedException e) {
        service.shutdownNow();
        Thread.currentThread().interrupt();
    }
}

// ❌ Bad: Unbounded queue
ExecutorService service = new ThreadPoolExecutor(
    2, 5, 60, TimeUnit.SECONDS,
    new LinkedBlockingQueue<>()  // Unbounded
);

// ✅ Good: Bounded queue
ExecutorService service = new ThreadPoolExecutor(
    2, 5, 60, TimeUnit.SECONDS,
    new LinkedBlockingQueue<>(100)  // Bounded
);

// ✅ Good: Using try-with-resources
try (ExecutorService service = Executors.newFixedThreadPool(5)) {
    service.submit(() -> System.out.println("Task"));
} // Automatically shutdown
```

---

## 💻 Step-by-Step Coding Tasks

### Task 1: Create Thread Pools

**Objective**: Implement different types of thread pools

**Acceptance Criteria**:
- [ ] Fixed thread pool
- [ ] Cached thread pool
- [ ] Scheduled executor
- [ ] Custom configuration
- [ ] Code compiles without errors

**Instructions**:
1. Create different executor types
2. Submit tasks
3. Monitor execution
4. Shutdown properly
5. Test with multiple tasks

### Task 2: Use Future and Callable

**Objective**: Work with results from tasks

**Acceptance Criteria**:
- [ ] Callable implementation
- [ ] Future retrieval
- [ ] Exception handling
- [ ] Timeout handling
- [ ] Correct results

**Instructions**:
1. Create callable tasks
2. Submit to executor
3. Retrieve results
4. Handle exceptions
5. Test timeouts

### Task 3: Schedule Tasks

**Objective**: Implement scheduled task execution

**Acceptance Criteria**:
- [ ] Delayed execution
- [ ] Periodic execution
- [ ] Fixed rate scheduling
- [ ] Fixed delay scheduling
- [ ] Proper cancellation

**Instructions**:
1. Create scheduled executor
2. Schedule delayed tasks
3. Schedule periodic tasks
4. Monitor execution
5. Cancel tasks

---

## 🎨 Mini-Project: Task Scheduling System

### Project Overview

**Description**: Create a comprehensive task scheduling system using thread pools and executors.

**Real-World Application**: Job schedulers, task runners, background processing systems.

**Learning Value**: Master executor framework and task scheduling patterns.

### Project Requirements

#### Functional Requirements
- [ ] Schedule tasks
- [ ] Execute tasks concurrently
- [ ] Track task status
- [ ] Handle results
- [ ] Generate reports
- [ ] Manage resources

#### Non-Functional Requirements
- [ ] Clean code structure
- [ ] Proper encapsulation
- [ ] Comprehensive documentation
- [ ] Unit tests
- [ ] Error handling

### Project Structure

```
task-scheduling-system/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/learning/
│   │           ├── Task.java
│   │           ├── TaskScheduler.java
│   │           ├── TaskResult.java
│   │           ├── TaskReport.java
│   │           └── Main.java
│   └── test/
│       └── java/
│           └── com/learning/
│               ├── TaskSchedulerTest.java
│               └── TaskResultTest.java
├── pom.xml
└── README.md
```

### Implementation Guide

#### Step 1: Create Task Class

```java
package com.learning;

import java.util.concurrent.Callable;
import java.time.LocalDateTime;

/**
 * Represents a scheduled task.
 */
public class Task implements Callable<TaskResult> {
    private String taskId;
    private String taskName;
    private Callable<String> action;
    private int priority;
    private LocalDateTime createdAt;
    
    /**
     * Constructor for Task.
     */
    public Task(String taskId, String taskName, Callable<String> action, int priority) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.action = action;
        this.priority = priority;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters
    public String getTaskId() { return taskId; }
    public String getTaskName() { return taskName; }
    public int getPriority() { return priority; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    @Override
    public TaskResult call() throws Exception {
        long startTime = System.currentTimeMillis();
        try {
            String result = action.call();
            long executionTime = System.currentTimeMillis() - startTime;
            return new TaskResult(taskId, taskName, result, executionTime, true);
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            return new TaskResult(taskId, taskName, "Error: " + e.getMessage(), 
                                executionTime, false);
        }
    }
    
    @Override
    public String toString() {
        return "Task{" +
                "id='" + taskId + '\'' +
                ", name='" + taskName + '\'' +
                ", priority=" + priority +
                '}';
    }
}
```

#### Step 2: Create TaskResult Class

```java
package com.learning;

/**
 * Represents task execution result.
 */
public class TaskResult {
    private String taskId;
    private String taskName;
    private String result;
    private long executionTime;
    private boolean success;
    
    /**
     * Constructor for TaskResult.
     */
    public TaskResult(String taskId, String taskName, String result, 
                      long executionTime, boolean success) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.result = result;
        this.executionTime = executionTime;
        this.success = success;
    }
    
    // Getters
    public String getTaskId() { return taskId; }
    public String getTaskName() { return taskName; }
    public String getResult() { return result; }
    public long getExecutionTime() { return executionTime; }
    public boolean isSuccess() { return success; }
    
    @Override
    public String toString() {
        return "TaskResult{" +
                "taskId='" + taskId + '\'' +
                ", taskName='" + taskName + '\'' +
                ", result='" + result + '\'' +
                ", executionTime=" + executionTime + "ms" +
                ", success=" + success +
                '}';
    }
}
```

#### Step 3: Create TaskScheduler Class

```java
package com.learning;

import java.util.*;
import java.util.concurrent.*;

/**
 * Schedules and executes tasks.
 */
public class TaskScheduler {
    private ExecutorService executor;
    private ScheduledExecutorService scheduler;
    private Map<String, Future<TaskResult>> futures;
    private List<TaskResult> results;
    
    /**
     * Constructor for TaskScheduler.
     */
    public TaskScheduler(int threadPoolSize) {
        this.executor = Executors.newFixedThreadPool(threadPoolSize);
        this.scheduler = Executors.newScheduledThreadPool(2);
        this.futures = new ConcurrentHashMap<>();
        this.results = Collections.synchronizedList(new ArrayList<>());
    }
    
    /**
     * Submit task for execution.
     */
    public void submitTask(Task task) {
        Future<TaskResult> future = executor.submit(task);
        futures.put(task.getTaskId(), future);
    }
    
    /**
     * Submit multiple tasks.
     */
    public void submitTasks(List<Task> tasks) {
        tasks.forEach(this::submitTask);
    }
    
    /**
     * Schedule task with delay.
     */
    public void scheduleTask(Task task, long delay, TimeUnit unit) {
        scheduler.schedule(() -> {
            Future<TaskResult> future = executor.submit(task);
            futures.put(task.getTaskId(), future);
        }, delay, unit);
    }
    
    /**
     * Schedule periodic task.
     */
    public void schedulePeriodicTask(Task task, long initialDelay, 
                                     long period, TimeUnit unit) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                TaskResult result = task.call();
                results.add(result);
            } catch (Exception e) {
                System.out.println("Periodic task error: " + e.getMessage());
            }
        }, initialDelay, period, unit);
    }
    
    /**
     * Get task result.
     */
    public TaskResult getResult(String taskId) {
        Future<TaskResult> future = futures.get(taskId);
        if (future == null) return null;
        
        try {
            if (future.isDone()) {
                TaskResult result = future.get();
                results.add(result);
                return result;
            }
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Error getting result: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Wait for all tasks.
     */
    public void waitForAll() {
        for (Future<TaskResult> future : futures.values()) {
            try {
                TaskResult result = future.get();
                results.add(result);
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    
    /**
     * Get all results.
     */
    public List<TaskResult> getAllResults() {
        return new ArrayList<>(results);
    }
    
    /**
     * Get successful results.
     */
    public List<TaskResult> getSuccessfulResults() {
        return results.stream()
            .filter(TaskResult::isSuccess)
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Get failed results.
     */
    public List<TaskResult> getFailedResults() {
        return results.stream()
            .filter(r -> !r.isSuccess())
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Shutdown scheduler.
     */
    public void shutdown() {
        executor.shutdown();
        scheduler.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
            if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
```

#### Step 4: Create TaskReport Class

```java
package com.learning;

import java.util.*;

/**
 * Generates task execution reports.
 */
public class TaskReport {
    private TaskScheduler scheduler;
    
    /**
     * Constructor for TaskReport.
     */
    public TaskReport(TaskScheduler scheduler) {
        this.scheduler = scheduler;
    }
    
    /**
     * Generate summary report.
     */
    public void generateSummaryReport() {
        System.out.println("\n========== TASK SUMMARY ==========");
        
        List<TaskResult> results = scheduler.getAllResults();
        List<TaskResult> successful = scheduler.getSuccessfulResults();
        List<TaskResult> failed = scheduler.getFailedResults();
        
        System.out.println("Total Tasks: " + results.size());
        System.out.println("Successful: " + successful.size());
        System.out.println("Failed: " + failed.size());
        
        if (!results.isEmpty()) {
            long totalTime = results.stream()
                .mapToLong(TaskResult::getExecutionTime)
                .sum();
            double avgTime = results.stream()
                .mapToLong(TaskResult::getExecutionTime)
                .average()
                .orElse(0);
            
            System.out.println("Total Time: " + totalTime + "ms");
            System.out.println("Average Time: " + String.format("%.2f", avgTime) + "ms");
        }
        System.out.println("=================================\n");
    }
    
    /**
     * Generate detailed report.
     */
    public void generateDetailedReport() {
        System.out.println("\n========== DETAILED REPORT ==========");
        scheduler.getAllResults().forEach(System.out::println);
        System.out.println("====================================\n");
    }
}
```

#### Step 5: Create Main Class

```java
package com.learning;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Main entry point for Task Scheduling System.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Create scheduler
        TaskScheduler scheduler = new TaskScheduler(3);
        
        // Create tasks
        List<Task> tasks = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            final int taskNum = i;
            Task task = new Task(
                "TASK_" + i,
                "Task " + i,
                () -> {
                    Thread.sleep(500);
                    return "Result " + taskNum;
                },
                i
            );
            tasks.add(task);
        }
        
        // Submit tasks
        System.out.println("Submitting tasks...");
        scheduler.submitTasks(tasks);
        
        // Wait for completion
        scheduler.waitForAll();
        
        // Generate reports
        TaskReport report = new TaskReport(scheduler);
        report.generateSummaryReport();
        report.generateDetailedReport();
        
        // Shutdown
        scheduler.shutdown();
        System.out.println("All tasks completed!");
    }
}
```

#### Step 6: Create Unit Tests

```java
package com.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TaskScheduler.
 */
public class TaskSchedulerTest {
    
    private TaskScheduler scheduler;
    
    @BeforeEach
    void setUp() {
        scheduler = new TaskScheduler(2);
    }
    
    @Test
    void testSubmitTask() throws InterruptedException {
        Task task = new Task("T1", "Test", () -> "Result", 1);
        scheduler.submitTask(task);
        scheduler.waitForAll();
        
        assertEquals(1, scheduler.getAllResults().size());
    }
    
    @Test
    void testMultipleTasks() throws InterruptedException {
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            tasks.add(new Task("T" + i, "Task " + i, () -> "Result", 1));
        }
        
        scheduler.submitTasks(tasks);
        scheduler.waitForAll();
        
        assertEquals(3, scheduler.getAllResults().size());
    }
    
    @Test
    void testSuccessfulResults() throws InterruptedException {
        Task task = new Task("T1", "Test", () -> "Success", 1);
        scheduler.submitTask(task);
        scheduler.waitForAll();
        
        assertEquals(1, scheduler.getSuccessfulResults().size());
    }
    
    @Test
    void testFailedResults() throws InterruptedException {
        Task task = new Task("T1", "Test", () -> {
            throw new RuntimeException("Error");
        }, 1);
        scheduler.submitTask(task);
        scheduler.waitForAll();
        
        assertEquals(1, scheduler.getFailedResults().size());
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

### Exercise 1: Batch Task Processing

**Objective**: Process batch of tasks with thread pool

**Task Description**:
Create system to process large batch of tasks efficiently

**Acceptance Criteria**:
- [ ] Batch submission
- [ ] Progress tracking
- [ ] Result collection
- [ ] Error handling
- [ ] Performance metrics

### Exercise 2: Scheduled Report Generation

**Objective**: Generate reports on schedule

**Task Description**:
Create system that generates reports periodically

**Acceptance Criteria**:
- [ ] Scheduled execution
- [ ] Report generation
- [ ] File output
- [ ] Error handling
- [ ] Proper cleanup

### Exercise 3: Async API Client

**Objective**: Build async API client with thread pool

**Task Description**:
Create client that makes async API calls

**Acceptance Criteria**:
- [ ] Async requests
- [ ] Result handling
- [ ] Error handling
- [ ] Timeout support
- [ ] Resource management

---

## 🧪 Quiz

### Question 1: What is an ExecutorService?

A) A service that executes code  
B) A framework for managing threads  
C) A type of thread  
D) A synchronization mechanism  

**Answer**: B) A framework for managing threads

### Question 2: What does Future.get() do?

A) Gets a future value  
B) Blocks until result is available  
C) Cancels the task  
D) Checks if task is done  

**Answer**: B) Blocks until result is available

### Question 3: What is a Callable?

A) A method that can be called  
B) An interface for tasks that return results  
C) A type of thread  
D) A synchronization tool  

**Answer**: B) An interface for tasks that return results

### Question 4: When should you use ScheduledExecutor?

A) For immediate execution  
B) For delayed or periodic tasks  
C) For synchronization  
D) For thread creation  

**Answer**: B) For delayed or periodic tasks

### Question 5: What happens if you don't shutdown ExecutorService?

A) Nothing  
B) Threads continue running  
C) Memory leak  
D) Program crashes  

**Answer**: B) Threads continue running

---

## 🚀 Advanced Challenge

### Challenge: Complete Job Scheduler

**Difficulty**: Advanced

**Objective**: Build comprehensive job scheduling system

**Requirements**:
- [ ] Task scheduling
- [ ] Dependency management
- [ ] Error recovery
- [ ] Monitoring
- [ ] Persistence
- [ ] Scalability

---

## 🏆 Best Practices

### Executor Programming

1. **Thread Pool Sizing**
   - CPU-bound: number of cores
   - I/O-bound: more threads
   - Monitor and adjust

2. **Task Management**
   - Use appropriate executor type
   - Handle exceptions
   - Proper shutdown

3. **Resource Management**
   - Always shutdown
   - Use try-with-resources
   - Monitor queue size

---

## 🔗 Next Steps

**Next Lab**: [Lab 14: Concurrent Collections](../14-concurrent-collections/README.md)

---

## ✅ Completion Checklist

- [ ] Completed all coding tasks
- [ ] Built task scheduling system
- [ ] Solved all exercises
- [ ] Passed quiz (80%+)
- [ ] Attempted advanced challenge
- [ ] Reviewed best practices

---

**Congratulations on completing Lab 13! 🎉**

You've mastered thread pools and executors. Ready for concurrent collections? Move on to [Lab 14: Concurrent Collections](../14-concurrent-collections/README.md).