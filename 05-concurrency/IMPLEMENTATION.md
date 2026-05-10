# Java Concurrency - Implementation Guide

## Module Overview

This module covers Java concurrency fundamentals including Thread, ExecutorService, Callable, Future, synchronization, and concurrent collections.

---

## Part 1: From-Scratch Java Implementation

### 1.1 Custom Thread Implementation

```java
package com.learning.concurrency.implementation;

public class ThreadImplementation {
    
    // Extending Thread class
    static class MyThread extends Thread {
        private String taskName;
        
        public MyThread(String taskName) {
            this.taskName = taskName;
        }
        
        @Override
        public void run() {
            System.out.println("Starting: " + taskName);
            try {
                Thread.sleep(1000); // Simulate work
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Completed: " + taskName);
        }
    }
    
    // Implementing Runnable
    static class Task implements Runnable {
        private int taskId;
        
        public Task(int taskId) {
            this.taskId = taskId;
        }
        
        @Override
        public void run() {
            System.out.println("Task " + taskId + " running on " + 
                    Thread.currentThread().getName());
        }
    }
    
    // Implementing Callable
    static class CalculationTask implements Callable<Integer> {
        private int number;
        
        public CalculationTask(int number) {
            this.number = number;
        }
        
        @Override
        public Integer call() throws Exception {
            int sum = 0;
            for (int i = 1; i <= number; i++) {
                sum += i;
                Thread.sleep(10); // Simulate computation
            }
            return sum;
        }
    }
    
    public void demonstrateThreadCreation() {
        // Method 1: Extend Thread
        Thread thread1 = new MyThread("Task-1");
        thread1.start();
        
        // Method 2: Implement Runnable
        Thread thread2 = new Thread(new Task(2));
        thread2.start();
        
        // Method 3: Lambda (Runnable)
        Thread thread3 = new Thread(() -> 
                System.out.println("Lambda task running"));
        thread3.start();
        
        // Method 4: Implement Callable with Future
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = executor.submit(new CalculationTask(100));
        
        try {
            Integer result = future.get();
            System.out.println("Calculation result: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        executor.shutdown();
    }
    
    public void demonstrateThreadJoin() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            System.out.println("Thread 1 starting");
            sleep(500);
            System.out.println("Thread 1 done");
        });
        
        Thread t2 = new Thread(() -> {
            System.out.println("Thread 2 starting");
            sleep(300);
            System.out.println("Thread 2 done");
        });
        
        t1.start();
        t2.start();
        
        t1.join();
        t2.join();
        
        System.out.println("Both threads completed");
    }
    
    public void demonstrateThreadInterruption() {
        Thread worker = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Working...");
                sleep(100);
            }
            System.out.println("Worker interrupted, exiting");
        });
        
        worker.start();
        
        sleep(500);
        worker.interrupt();
    }
    
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

### 1.2 ExecutorService Implementation

```java
package com.learning.concurrency.implementation;

import java.util.*;
import java.util.concurrent.*;

public class ExecutorServiceDemo {
    
    public void demonstrateFixedThreadPool() {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        
        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("Task " + taskId + " on " + 
                        Thread.currentThread().getName());
                sleep(100);
            });
        }
        
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void demonstrateCachedThreadPool() {
        ExecutorService executor = Executors.newCachedThreadPool();
        
        for (int i = 0; i < 20; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("Task " + taskId);
                return taskId;
            });
        }
        
        executor.shutdown();
    }
    
    public void demonstrateScheduledThreadPool() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        
        // Run once after delay
        scheduler.schedule(() -> 
                System.out.println("One-time task"), 2, TimeUnit.SECONDS);
        
        // Run repeatedly
        scheduler.scheduleAtFixedRate(() -> 
                System.out.println("Repeating task"), 0, 1, TimeUnit.SECONDS);
        
        sleep(5000);
        scheduler.shutdown();
    }
    
    public void demonstrateSingleThreadExecutor() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        
        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("Task " + taskId);
            });
        }
        
        executor.shutdown();
    }
    
    public void demonstrateFutureOperations() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        // SubmitCallable and get result
        Future<Integer> future1 = executor.submit(() -> {
            sleep(500);
            return 100;
        });
        
        // SubmitRunnable (no result)
        Future<?> future2 = executor.submit(() -> {
            System.out.println("Runnable task");
        });
        
        // Check if done
        System.out.println("Future1 done: " + future1.isDone());
        
        // Get with timeout
        Integer result = future1.get(2, TimeUnit.SECONDS);
        System.out.println("Result: " + result);
        
        executor.shutdown();
    }
    
    public void demonstrateInvokeAll() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        List<Callable<String>> tasks = Arrays.asList(
                () -> { sleep(200); return "Task1"; },
                () -> { sleep(100); return "Task2"; },
                () -> { sleep(300); return "Task3"; }
        );
        
        List<Future<String>> futures = executor.invokeAll(tasks);
        
        for (Future<String> future : futures) {
            System.out.println("Result: " + future.get());
        }
        
        executor.shutdown();
    }
    
    public void demonstrateInvokeAny() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        List<Callable<String>> tasks = Arrays.asList(
                () -> { sleep(500); return "First"; },
                () -> { sleep(100); return "Second"; },
                () -> { sleep(300); return "Third"; }
        );
        
        // Returns first completed
        String result = executor.invokeAny(tasks);
        System.out.println("First completed: " + result);
        
        executor.shutdown();
    }
    
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

### 1.3 Synchronization Implementation

```java
package com.learning.concurrency.implementation;

import java.util.concurrent.locks.*;

public class SynchronizationDemo {
    
    // Synchronized method
    static class Counter {
        private int count = 0;
        
        public synchronized void increment() {
            count++;
        }
        
        public synchronized int getCount() {
            return count;
        }
    }
    
    // Synchronized block
    static class SafeCounter {
        private int count = 0;
        private final Object lock = new Object();
        
        public void increment() {
            synchronized (lock) {
                count++;
            }
        }
        
        public int getCount() {
            synchronized (lock) {
                return count;
            }
        }
    }
    
    // ReentrantLock
    static class LockCounter {
        private int count = 0;
        private final ReentrantLock lock = new ReentrantLock();
        
        public void increment() {
            lock.lock();
            try {
                count++;
            } finally {
                lock.unlock();
            }
        }
        
        public int getCount() {
            lock.lock();
            try {
                return count;
            } finally {
                lock.unlock();
            }
        }
        
        public boolean tryLockExample() {
            if (lock.tryLock()) {
                try {
                    return true;
                } finally {
                    lock.unlock();
                }
            }
            return false;
        }
    }
    
    // ReadWriteLock
    static class ReadWriteCache {
        private final Map<String, String> cache = new HashMap<>();
        private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
        
        public String read(String key) {
            rwLock.readLock().lock();
            try {
                return cache.get(key);
            } finally {
                rwLock.readLock().unlock();
            }
        }
        
        public void write(String key, String value) {
            rwLock.writeLock().lock();
            try {
                cache.put(key, value);
            } finally {
                rwLock.writeLock().unlock();
            }
        }
    }
    
    // Condition variables
    static class BoundedBuffer<T> {
        private final Object[] items;
        private int count, putIndex, takeIndex;
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition notFull = lock.newCondition();
        private final Condition notEmpty = lock.newCondition();
        
        public BoundedBuffer(int capacity) {
            items = new Object[capacity];
        }
        
        public void put(T item) throws InterruptedException {
            lock.lock();
            try {
                while (count == items.length) {
                    notFull.await();
                }
                items[putIndex] = item;
                putIndex = (putIndex + 1) % items.length;
                count++;
                notEmpty.signal();
            } finally {
                lock.unlock();
            }
        }
        
        public T take() throws InterruptedException {
            lock.lock();
            try {
                while (count == 0) {
                    notEmpty.await();
                }
                @SuppressWarnings("unchecked")
                T item = (T) items[takeIndex];
                takeIndex = (takeIndex + 1) % items.length;
                count--;
                notFull.signal();
                return item;
            } finally {
                lock.unlock();
            }
        }
    }
    
    public void demonstrateSynchronization() throws InterruptedException {
        Counter counter = new Counter();
        
        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment();
            }
        };
        
        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        
        t1.start();
        t2.start();
        
        t1.join();
        t2.join();
        
        System.out.println("Final count: " + counter.getCount());
    }
    
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

### 1.4 Concurrent Collections

```java
package com.learning.concurrency.implementation;

import java.util.concurrent.*;

public class ConcurrentCollectionsDemo {
    
    public void demonstrateConcurrentMap() {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        
        // Atomic operations
        map.put("key1", 1);
        map.putIfAbsent("key1", 2); // Won't replace
        map.computeIfAbsent("key2", k -> 10); // Compute if absent
        
        // Thread-safe get and put
        map.get("key1");
        map.put("key3", 3);
        
        // Bulk operations
        map.forEach((k, v) -> System.out.println(k + ":" + v));
        
        // Atomic update
        map.merge("key1", 1, (old, newVal) -> old + newVal);
    }
    
    public void demonstrateBlockingQueue() throws InterruptedException {
        BlockingQueue<String> queue = new LinkedBlockingQueue<>(5);
        
        // Producer
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    queue.put("item" + i);
                    System.out.println("Produced: item" + i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // Consumer
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    String item = queue.take();
                    System.out.println("Consumed: " + item);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        producer.start();
        consumer.start();
        
        producer.join();
        consumer.join();
    }
    
    public void demonstrateConcurrentLinkedQueue() {
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        
        queue.offer("first");
        queue.offer("second");
        
        System.out.println("Poll: " + queue.poll());
        System.out.println("Peek: " + queue.peek());
        System.out.println("Size: " + queue.size());
    }
    
    public void demonstrateConcurrentSkipListSet() {
        ConcurrentSkipListSet<Integer> set = new ConcurrentSkipListSet<>();
        
        set.add(3);
        set.add(1);
        set.add(2);
        
        System.out.println("Set: " + set);
        System.out.println("First: " + set.first());
        System.out.println("Last: " + set.lower(2));
    }
    
    public void demonstrateAtomicClasses() {
        AtomicInteger atomicInt = new AtomicInteger(0);
        
        // Atomic operations
        atomicInt.incrementAndGet();
        atomicInt.getAndAdd(10);
        atomicInt.compareAndSet(11, 20);
        
        System.out.println("AtomicInteger: " + atomicInt.get());
        
        AtomicReference<String> atomicRef = new AtomicReference<>("initial");
        atomicRef.compareAndSet("initial", "updated");
        System.out.println("AtomicReference: " + atomicRef.get());
        
        AtomicLongArray array = new AtomicLongArray(5);
        array.set(0, 100);
        System.out.println("Array[0]: " + array.get(0));
    }
}
```

---

## Part 2: Production Variant with Spring Boot

### 2.1 Async Service with CompletableFuture

```java
package com.learning.concurrency.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncService {
    
    @Async("taskExecutor")
    public CompletableFuture<String> asyncMethod() {
        return CompletableFuture.supplyAsync(() -> {
            // Simulate async work
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Async result";
        });
    }
    
    @Async("taskExecutor")
    public CompletableFuture<Result> processData(Data data) {
        return CompletableFuture.supplyAsync(() -> {
            // Process data
            Result result = new Result();
            result.setStatus("COMPLETED");
            return result;
        });
    }
    
    public CompletableFuture<String> composeFutures() {
        return CompletableFuture.supplyAsync(() -> "Step1")
                .thenApply(s -> s + " -> Step2")
                .thenApply(s -> s + " -> Step3");
    }
    
    public CompletableFuture<String> combineFutures() {
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "Result1");
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> "Result2");
        
        return f1.thenCombine(f2, (r1, r2) -> r1 + " + " + r2);
    }
    
    public static class Result {
        private String status;
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
    
    public static class Data {
        private String content;
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}
```

### 2.2 Thread Pool Configuration

```java
package com.learning.concurrency.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;

@Configuration
public class ExecutorConfig {
    
    @Bean("taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }
    
    @Bean("scheduledExecutor")
    public Executor scheduledExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setThreadNamePrefix("scheduled-");
        executor.initialize();
        return executor;
    }
}
```

---

## Part 3: Step-by-Step Code Explanation

### 3.1 Thread Creation

**Step 1: Extend Thread**
- Override run() method
- Call start() to begin execution
- Cannot return result directly

**Step 2: Implement Runnable**
- No return value
- Can be passed to Thread or Executor
- Preferred for most use cases

**Step 3: Implement Callable**
- Can return result via Future
- Can throw checked exceptions
- Used with ExecutorService

### 3.2 Executor Types

**Step 1: Fixed Thread Pool**
- Maintains fixed number of threads
- Reuses threads for tasks
- Good for CPU-bound tasks

**Step 2: Cached Thread Pool**
- Creates new threads as needed
- Reuses idle threads
- Good for many short tasks

**Step 3: Scheduled Thread Pool**
- For delayed/repeated tasks
- schedule(), scheduleAtFixedRate()

### 3.3 Synchronization

**Step 1: synchronized**
- Intrinsic lock
- Automatic acquisition/release
- Can be on method or block

**Step 2: ReentrantLock**
- Explicit lock
- tryLock(), lockInterruptibly()
- More control than synchronized

**Step 3: ReadWriteLock**
- Multiple readers
- Exclusive writers
- Better read-heavy scenarios

---

## Part 4: Key Concepts Demonstrated

| Concept | Implementation | Use Case |
|---------|---------------|----------|
| **Thread** | Thread, Runnable, Callable | Basic concurrency |
| **Executor** | Fixed, Cached, Scheduled thread pools | Thread management |
| **Future** | Future, CompletableFuture | Async results |
| **Synchronized** | synchronized methods/blocks | Thread safety |
| **Locks** | ReentrantLock, ReadWriteLock | Fine-grained control |
| **Conditions** | Condition variables | Complex coordination |
| **Concurrent Collections** | ConcurrentHashMap, BlockingQueue | Thread-safe data structures |
| **Atomic** | AtomicInteger, AtomicReference | Lock-free updates |

---

## Key Takeaways

1. Use ExecutorService instead of raw Thread
2. Prefer concurrent collections over synchronized ones
3. Use CompletableFuture for complex async workflows
4. Avoid shared mutable state when possible
5. Consider virtual threads for high-concurrency scenarios (Java 21+)