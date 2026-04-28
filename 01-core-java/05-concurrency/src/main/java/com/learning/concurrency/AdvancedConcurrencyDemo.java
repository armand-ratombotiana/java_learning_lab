package com.learning.concurrency;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

/**
 * Advanced Concurrency Patterns
 * Covers: Thread pools, barriers, latches, semaphores, and design patterns
 * 
 * Companies: Google, Amazon, Meta, Microsoft, Netflix
 * Interview Difficulty: Hard
 */
public class AdvancedConcurrencyDemo {
    
    // ==================== SYNCHRONIZATION BARRIERS ====================
    
    /**
     * CountDownLatch - Wait for multiple threads to complete
     * One-time use barrier (cannot be reset)
     */
    public static class CountDownLatchExample {
        public static void demonstrateCountDownLatch() throws InterruptedException {
            int workerCount = 3;
            CountDownLatch latch = new CountDownLatch(workerCount);
            
            // Start workers
            for (int i = 0; i < workerCount; i++) {
                new Thread(() -> {
                    try {
                        System.out.println(Thread.currentThread().getName() + " doing work");
                        Thread.sleep(1000);
                        System.out.println(Thread.currentThread().getName() + " finished");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        latch.countDown();  // Signal completion
                    }
                }).start();
            }
            
            System.out.println("Main thread waiting for workers...");
            latch.await();  // Wait until count reaches 0
            System.out.println("All workers completed!");
        }
    }
    
    /**
     * CyclicBarrier - Reusable synchronization for multiple threads
     * All threads wait at barrier point before continuing
     */
    public static class CyclicBarrierExample {
        public static void demonstrateCyclicBarrier() throws InterruptedException {
            int threadCount = 3;
            CyclicBarrier barrier = new CyclicBarrier(threadCount, () -> {
                System.out.println("All threads reached barrier!");
            });
            
            for (int i = 0; i < threadCount; i++) {
                final int threadNum = i;
                new Thread(() -> {
                    try {
                        System.out.println("Thread " + threadNum + " started");
                        Thread.sleep(1000 * (threadNum + 1));  // Different sleep times
                        System.out.println("Thread " + threadNum + " reached barrier");
                        barrier.await();  // Wait for all threads
                        System.out.println("Thread " + threadNum + " continuing");
                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            }
            
            Thread.sleep(5000);  // Let threads finish
        }
    }
    
    /**
     * Semaphore - Control access to limited resource
     * Like having N permits - acquire reduces count, release increases it
     */
    public static class SemaphoreExample {
        public static class ResourcePool {
            private final Semaphore semaphore;
            private final List<String> resources;
            
            public ResourcePool(int poolSize) {
                this.semaphore = new Semaphore(poolSize);
                this.resources = new ArrayList<>();
                for (int i = 0; i < poolSize; i++) {
                    resources.add("Resource-" + i);
                }
            }
            
            public String acquireResource() throws InterruptedException {
                semaphore.acquire();  // Wait for available resource
                synchronized (resources) {
                    return resources.remove(0);
                }
            }
            
            public void releaseResource(String resource) {
                synchronized (resources) {
                    resources.add(resource);
                }
                semaphore.release();  // Make resource available again
            }
        }
    }
    
    // ==================== THREAD-SAFE COLLECTIONS ====================
    
    /**
     * Compare different thread-safe collection options
     */
    public static class ThreadSafeCollectionsExample {
        
        // Option 1: Collections.synchronizedList
        public static List<Integer> createSynchronizedList() {
            return Collections.synchronizedList(new ArrayList<>());
        }
        
        // Option 2: CopyOnWriteArrayList (for high-read scenarios)
        public static List<Integer> createCopyOnWriteList() {
            return new CopyOnWriteArrayList<>();
        }
        
        // Option 3: ConcurrentHashMap (better than Collections.synchronizedMap)
        public static Map<String, Integer> createConcurrentMap() {
            return new ConcurrentHashMap<>();
        }
        
        // Option 4: BlockingQueue (for producer-consumer)
        public static BlockingQueue<Integer> createBlockingQueue() {
            return new LinkedBlockingQueue<>();
        }
    }
    
    /**
     * Producer-Consumer with BlockingQueue
     */
    public static class ProducerConsumerPattern {
        private final BlockingQueue<Integer> queue;
        
        public ProducerConsumerPattern(int capacity) {
            this.queue = new LinkedBlockingQueue<>(capacity);
        }
        
        public class Producer implements Runnable {
            private int count = 0;
            
            @Override
            public void run() {
                try {
                    while (true) {
                        queue.put(count++);  // Blocks if queue full
                        System.out.println("Produced: " + count);
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        public class Consumer implements Runnable {
            @Override
            public void run() {
                try {
                    while (true) {
                        Integer value = queue.take();  // Blocks if queue empty
                        System.out.println("Consumed: " + value);
                        Thread.sleep(200);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    
    // ==================== FUTURE & CALLABLE ====================
    
    /**
     * CompletableFuture - More powerful than Future
     * Allows chaining async operations
     */
    public static class CompletableFutureExample {
        
        public static CompletableFuture<Integer> calculateAsync(int value) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(1000);
                    return value * 2;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return 0;
                }
            });
        }
        
        public static void demonstrateChaining() throws ExecutionException, InterruptedException {
            CompletableFuture<Integer> result = calculateAsync(5)
                .thenApply(x -> x + 10)           // Transform result
                .thenApply(x -> x * 2)            // Chain another operation
                .thenApply(x -> {
                    System.out.println("Final result: " + x);
                    return x;
                });
            
            result.get();  // Wait for completion
        }
        
        public static void demonstrateCompose() throws ExecutionException, InterruptedException {
            CompletableFuture<Integer> future = calculateAsync(5)
                .thenCompose(x -> calculateAsync(x + 10));  // Chain async operations
            
            System.out.println("Result: " + future.get());
        }
    }
    
    // ==================== THREAD POOL PATTERNS ====================
    
    /**
     * Different executor service configurations
     */
    public static class ExecutorServicesComparison {
        
        // 1. FixedThreadPool - Fixed number of threads
        public static ExecutorService createFixedPool(int size) {
            return Executors.newFixedThreadPool(size);
        }
        
        // 2. CachedThreadPool - Creates threads as needed
        public static ExecutorService createCachedPool() {
            return Executors.newCachedThreadPool();
        }
        
        // 3. ScheduledThreadPool - For scheduled tasks
        public static ScheduledExecutorService createScheduledPool(int size) {
            return Executors.newScheduledThreadPool(size);
        }
        
        // 4. SingleThreadExecutor - Exactly one thread
        public static ExecutorService createSingleThreadPool() {
            return Executors.newSingleThreadExecutor();
        }
        
        // 5. Custom ThreadPoolExecutor - Full control
        public static ExecutorService createCustomPool() {
            return new ThreadPoolExecutor(
                2,                          // Core threads
                4,                          // Max threads
                60,                         // Keep-alive time
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()  // Rejection policy
            );
        }
    }
    
    // ==================== CONDITION VARIABLES ====================
    
    /**
     * Condition - More flexible than wait/notify
     * Allows multiple condition objects with single lock
     */
    public static class ConditionBoundedBuffer {
        private final int[] items;
        private int putIdx = 0;
        private int getIdx = 0;
        private int count = 0;
        
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition notFull = lock.newCondition();
        private final Condition notEmpty = lock.newCondition();
        
        public ConditionBoundedBuffer(int capacity) {
            this.items = new int[capacity];
        }
        
        public void put(int item) throws InterruptedException {
            lock.lock();
            try {
                while (count == items.length) {
                    notFull.await();  // Wait until buffer not full
                }
                items[putIdx] = item;
                putIdx = (putIdx + 1) % items.length;
                ++count;
                notEmpty.signalAll();  // Wake up waiting consumers
            } finally {
                lock.unlock();
            }
        }
        
        public int get() throws InterruptedException {
            lock.lock();
            try {
                while (count == 0) {
                    notEmpty.await();  // Wait until buffer not empty
                }
                int item = items[getIdx];
                getIdx = (getIdx + 1) % items.length;
                --count;
                notFull.signalAll();  // Wake up waiting producers
                return item;
            } finally {
                lock.unlock();
            }
        }
    }
    
    // ==================== THREAD SAFETY PATTERNS ====================
    
    /**
     * Immutability - Thread-safe by design
     * If object is immutable, no synchronization needed
     */
    public static final class ImmutablePoint {
        private final int x;
        private final int y;
        
        public ImmutablePoint(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public int getX() { return x; }
        public int getY() { return y; }
        
        // No setters - cannot modify
    }
    
    /**
     * Thread-safe lazily initialized singleton
     */
    public static class ThreadSafeLazySingleton {
        private static volatile ThreadSafeLazySingleton instance;
        
        public static ThreadSafeLazySingleton getInstance() {
            if (instance == null) {
                synchronized (ThreadSafeLazySingleton.class) {
                    if (instance == null) {
                        instance = new ThreadSafeLazySingleton();
                    }
                }
            }
            return instance;
        }
    }
    
    // ==================== MAIN DEMONSTRATION ====================
    
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        System.out.println("=== Advanced Concurrency Patterns ===\n");
        
        // 1. CountDownLatch
        System.out.println("1. CountDownLatch Example:");
        CountDownLatchExample.demonstrateCountDownLatch();
        System.out.println();
        
        // 2. CyclicBarrier
        System.out.println("2. CyclicBarrier Example:");
        CyclicBarrierExample.demonstrateCyclicBarrier();
        System.out.println();
        
        // 3. Semaphore
        System.out.println("3. Semaphore Resource Pool Example:");
        SemaphoreExample.ResourcePool pool = new SemaphoreExample.ResourcePool(2);
        ExecutorService executor = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 4; i++) {
            final int taskNum = i;
            executor.submit(() -> {
                try {
                    String resource = pool.acquireResource();
                    System.out.println("Task " + taskNum + " acquired " + resource);
                    Thread.sleep(1000);
                    System.out.println("Task " + taskNum + " releasing " + resource);
                    pool.releaseResource(resource);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println();
        
        System.out.println("=== Advanced patterns demonstrated ===");
    }
}
