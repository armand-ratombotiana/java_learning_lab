package com.learning.concurrency;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Thread Basics Demonstration
 * Covers fundamental threading concepts, synchronization, and thread lifecycle
 * 
 * Learning Objectives:
 * - Understand Thread creation and lifecycle
 * - Synchronization mechanisms
 * - Thread communication
 * - Common pitfalls and solutions
 * 
 * Companies: Google, Amazon, Meta, Microsoft, Netflix
 * Interview Frequency: Very High
 * Difficulty: Medium-Hard
 */
public class ThreadBasicsDemo {
    
    // ==================== THREAD CREATION ====================
    
    /**
     * Method 1: Extend Thread class
     * Preferred when class hierarchy is not needed
     */
    public static class CounterThread extends Thread {
        private int count;
        
        public CounterThread(String name) {
            super(name);
            this.count = 0;
        }
        
        @Override
        public void run() {
            for (int i = 0; i < 5; i++) {
                count++;
                System.out.println(Thread.currentThread().getName() + ": " + count);
                try {
                    Thread.sleep(100);  // Simulate work
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    
    /**
     * Method 2: Implement Runnable interface
     * Preferred - allows extending another class
     */
    public static class RunnableCounter implements Runnable {
        private int count;
        private String name;
        
        public RunnableCounter(String name) {
            this.name = name;
            this.count = 0;
        }
        
        @Override
        public void run() {
            for (int i = 0; i < 5; i++) {
                count++;
                System.out.println(name + ": " + count);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    
    // ==================== SYNCHRONIZATION ====================
    
    /**
     * Demonstrates synchronized method
     * Thread-safe counter using method lock
     */
    public static class SynchronizedCounter {
        private int value = 0;
        
        public synchronized void increment() {
            value++;
        }
        
        public synchronized int getValue() {
            return value;
        }
        
        // Lock is on 'this' object
        // Only one thread can execute synchronized method at a time
    }
    
    /**
     * Demonstrates synchronized block
     * More granular control over locking
     */
    public static class BlockSynchronizedCounter {
        private int value = 0;
        private final Object lock = new Object();
        
        public void increment() {
            // Lock acquired here
            synchronized (lock) {
                value++;
            }
            // Lock released here
        }
        
        public int getValue() {
            synchronized (lock) {
                return value;
            }
        }
    }
    
    /**
     * Demonstrates race condition
     * Multiple threads modifying shared state without synchronization
     */
    public static class UnsafeCounter {
        private int value = 0;
        
        public void increment() {
            value++;  // NOT thread-safe!
            // Read-Modify-Write is 3 separate operations
            // Another thread can interfere between operations
        }
        
        public int getValue() {
            return value;
        }
    }
    
    // ==================== THREAD-SAFE DATA STRUCTURES ====================
    
    /**
     * AtomicInteger - Atomic operation without explicit synchronization
     * Uses Compare-And-Swap (CAS) under the hood
     */
    public static class AtomicCounter {
        private AtomicInteger value = new AtomicInteger(0);
        
        public void increment() {
            value.incrementAndGet();  // Atomic operation
        }
        
        public int getValue() {
            return value.get();
        }
        
        public void add(int delta) {
            value.addAndGet(delta);
        }
    }
    
    // ==================== LOCKS ====================
    
    /**
     * ReentrantLock - More flexible than synchronized
     * Allows try-lock, fair scheduling, multiple conditions
     */
    public static class ReentrantLockCounter {
        private int value = 0;
        private final ReentrantLock lock = new ReentrantLock();
        
        public void increment() {
            lock.lock();
            try {
                value++;
            } finally {
                lock.unlock();  // Always release in finally block
            }
        }
        
        public int getValue() {
            lock.lock();
            try {
                return value;
            } finally {
                lock.unlock();
            }
        }
        
        // Try-lock with timeout
        public boolean tryIncrementWithTimeout() throws InterruptedException {
            if (lock.tryLock(1, TimeUnit.SECONDS)) {
                try {
                    value++;
                    return true;
                } finally {
                    lock.unlock();
                }
            }
            return false;
        }
    }
    
    /**
     * ReadWriteLock - Multiple readers, exclusive writer
     * Performance benefit when reads > writes
     */
    public static class ReadWriteCounter {
        private int value = 0;
        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        
        public void increment() {
            lock.writeLock().lock();
            try {
                value++;
            } finally {
                lock.writeLock().unlock();
            }
        }
        
        public int getValue() {
            lock.readLock().lock();
            try {
                return value;  // Multiple threads can read simultaneously
            } finally {
                lock.readLock().unlock();
            }
        }
    }
    
    // ==================== THREAD COMMUNICATION ====================
    
    /**
     * Wait and notify for thread communication
     * Threads can communicate through shared objects
     */
    public static class ProducerConsumer {
        private final Object lock = new Object();
        private int value;
        private boolean valueSet = false;
        
        public void produce(int val) throws InterruptedException {
            synchronized (lock) {
                while (valueSet) {
                    lock.wait();  // Wait until consumer takes value
                }
                this.value = val;
                this.valueSet = true;
                lock.notifyAll();  // Wake up waiting consumer
            }
        }
        
        public int consume() throws InterruptedException {
            synchronized (lock) {
                while (!valueSet) {
                    lock.wait();  // Wait until producer sets value
                }
                valueSet = false;
                lock.notifyAll();  // Wake up waiting producer
                return value;
            }
        }
    }
    
    // ==================== THREAD POOLING ====================
    
    /**
     * ExecutorService - Manage thread pools efficiently
     * Pre-created threads for multiple tasks
     */
    public static class ThreadPoolExample {
        private final ExecutorService executor;
        
        public ThreadPoolExample(int customPoolSize) {
            // Create fixed thread pool
            this.executor = Executors.newFixedThreadPool(customPoolSize);
        }
        
        public void submitTask(Runnable task) {
            executor.submit(task);
        }
        
        public void shutdown() {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Callable and Future - Get return value from task
     * Unlike Runnable, Callable<V> can return a value
     */
    public static class FutureExample {
        private final ExecutorService executor = Executors.newFixedThreadPool(2);
        
        public Future<Integer> calculateAsync(int n) {
            return executor.submit(() -> {
                // Callable - can return value or throw exception
                if (n < 0) {
                    throw new IllegalArgumentException("n must be positive");
                }
                Thread.sleep(1000);  // Simulate work
                return n * n;
            });
        }
        
        public void getFutureResult() throws ExecutionException, InterruptedException, TimeoutException {
            Future<Integer> future = calculateAsync(5);
            
            // Block until result is ready
            Integer result = future.get();
            System.out.println("Result: " + result);
            
            // Get with timeout
            Integer resultWithTimeout = future.get(2, TimeUnit.SECONDS);
        }
        
        public void shutdown() {
            executor.shutdown();
        }
    }
    
    // ==================== DEADLOCK PREVENTION ====================
    
    /**
     * Deadlock example - Both threads waiting for each other
     * AVOID THIS PATTERN
     */
    public static class DeadlockExample {
        private final Object lock1 = new Object();
        private final Object lock2 = new Object();
        
        public void methodOne() {
            synchronized (lock1) {  // Thread 1 locks lock1
                synchronized (lock2) {  // Waits for lock2
                    System.out.println("Method One");
                }
            }
        }
        
        public void methodTwo() {
            synchronized (lock2) {  // Thread 2 locks lock2
                synchronized (lock1) {  // Waits for lock1 - DEADLOCK!
                    System.out.println("Method Two");
                }
            }
        }
    }
    
    /**
     * Prevent deadlock by always acquiring locks in same order
     */
    public static class DeadlockFree {
        private final Object lock1 = new Object();
        private final Object lock2 = new Object();
        
        public void methodOne() {
            synchronized (lock1) {
                synchronized (lock2) {  // Always lock1 then lock2
                    System.out.println("Method One");
                }
            }
        }
        
        public void methodTwo() {
            synchronized (lock1) {  // Same order
                synchronized (lock2) {
                    System.out.println("Method Two");
                }
            }
        }
    }
    
    // ==================== VOLATILE KEYWORD ====================
    
    /**
     * volatile - Ensures visibility across threads
     * Each read/write is directly from main memory, not cache
     */
    public static class VolatileFlag {
        private volatile boolean flag = false;
        
        public void setFlag(boolean value) {
            flag = value;  // Visible to all threads immediately
        }
        
        public boolean getFlag() {
            return flag;  // Always reads from main memory
        }
    }
    
    // ==================== THREAD-LOCAL ====================
    
    /**
     * ThreadLocal - Each thread gets its own instance
     * Useful for context information, user sessions, etc.
     */
    public static class ThreadLocalExample {
        private static final ThreadLocal<Integer> threadLocalValue = 
            ThreadLocal.withInitial(() -> 0);
        
        public void setValue(int value) {
            threadLocalValue.set(value);
        }
        
        public int getValue() {
            return threadLocalValue.get();
        }
        
        public void cleanup() {
            threadLocalValue.remove();  // Important: prevent memory leaks
        }
    }
    
    // ==================== MAIN DEMONSTRATION ====================
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Java Concurrency Fundamentals ===\n");
        
        // 1. Thread creation
        System.out.println("1. Thread Creation:");
        CounterThread thread1 = new CounterThread("Thread-1");
        Thread thread2 = new Thread(new RunnableCounter("Thread-2"));
        thread1.start();
        thread2.start();
        thread1.join();  // Wait for completion
        thread2.join();
        System.out.println();
        
        // 2. Synchronized counter
        System.out.println("2. Synchronized Counter (Thread-safe):");
        SynchronizedCounter syncCounter = new SynchronizedCounter();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        for (int i = 0; i < 100; i++) {
            executor.submit(syncCounter::increment);
        }
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("Final value: " + syncCounter.getValue() + " (should be 100)\n");
        
        // 3. Atomic counter
        System.out.println("3. Atomic Counter:");
        AtomicCounter atomicCounter = new AtomicCounter();
        ExecutorService executor2 = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 1000; i++) {
            executor2.submit(atomicCounter::increment);
        }
        executor2.shutdown();
        executor2.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("Final value: " + atomicCounter.getValue() + " (should be 1000)\n");
        
        System.out.println("=== Concurrency concepts demonstrated ===");
    }
}
