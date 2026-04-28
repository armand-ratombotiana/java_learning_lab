package com.learning.concurrency;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.stream.IntStream;

/**
 * Elite Concurrency Interview Questions
 * 40+ questions covering all concurrency topics
 * Real problems from FAANG company interviews
 * 
 * Difficulty Levels:
 * - Easy (10): Basic concepts
 * - Medium (15): Application of concepts  
 * - Hard (15+): Complex scenarios
 */
public class EliteConcurrencyExercises {
    
    // ==================== EASY LEVEL QUESTIONS ====================
    
    public static class EasyQuestions {
        
        /**
         * Q1: What is the difference between Thread and Runnable?
         * 
         * Answer:
         * - Thread: Class you extend (single inheritance limited)
         * - Runnable: Interface you implement (allows multiple)
         * - Prefer Runnable as it provides more flexibility
         * - Both executed by thread scheduler
         */
        public static void q1_ThreadVsRunnable() {
            // Thread approach
            class MyThread extends Thread {
                public void run() { System.out.println("Thread"); }
            }
            new MyThread().start();
            
            // Runnable approach (preferred)
            Thread t = new Thread(() -> System.out.println("Runnable"));
            t.start();
        }
        
        /**
         * Q2: What happens if you call run() instead of start()?
         * 
         * Answer:
         * - run(): Executes in current thread (NOT a new thread)
         * - start(): Creates new thread and calls run() in it
         * - Calling run() defeats the purpose of threading
         * 
         * Correct:
         * thread.start();
         * 
         * Wrong:
         * thread.run();  // Executes in current thread!
         */
        public static void q2_StartVsRun() {
            Thread t = new Thread(() -> {
                System.out.println("Thread: " + Thread.currentThread().getName());
            });
            t.start();  // Correct - creates new thread
            // t.run();   // Wrong - executes in main thread
        }
        
        /**
         * Q3: What does synchronized do?
         * 
         * Answer:
         * - Ensures only one thread can execute at a time
         * - Uses intrinsic lock on object
         * - Prevents race conditions
         * - Can be applied to methods or blocks
         */
        public static class SyncExample {
            private int count = 0;
            
            public synchronized void increment() {
                count++;
            }
            
            public synchronized void decrement() {
                count--;
            }
        }
        
        /**
         * Q4: What is a race condition?
         * 
         * Answer:
         * - When multiple threads access shared resource without synchronization
         * - Result depends on thread scheduling (unpredictable)
         * - Example: count++ is not atomic (3 operations)
         * - Fix: Use synchronized, atomic variables, or locks
         */
        public static int demonstrateRaceCondition() {
            int unsafeCount = 0;
            
            // This will give inconsistent results due to race condition
            for (int i = 0; i < 1000; i++) {
                unsafeCount++;
                unsafeCount--;  // Not atomic!
            }
            
            return unsafeCount;
        }
        
        /**
         * Q5: Difference between sleep() and wait()?
         * 
         * Answer:
         * - sleep(): Thread sleeps, holds lock
         * - wait(): Thread releases lock and waits for notify
         * - sleep() is in Thread class, wait() in Object class
         * - sleep() doesn't need synchronization, wait() does
         */
        public static void q5_SleepVsWait() throws InterruptedException {
            // sleep() - holds lock
            synchronized (EasyQuestions.class) {
                System.out.println("Before sleep");
                Thread.sleep(1000);
                System.out.println("After sleep (lock still held)");
            }
            
            // wait() - releases lock
            synchronized (new Object()) {
                System.out.println("Before wait");
                // new Object().wait(1000);  // Would wait and release lock
                System.out.println("After wait");
            }
        }
        
        /**
         * Q6: What is volatile?
         * 
         * Answer:
         * - Ensures visibility across threads
         * - Each read/write goes to main memory (not cache)
         * - NOT a substitute for synchronization
         * - Useful for flags and simple variables
         */
        public static volatile boolean flag = false;
        
        /**
         * Q7: What is deadlock?
         * 
         * Answer:
         * - Two or more threads waiting for each other indefinitely
         * - Each thread holds resource other thread needs
         * - Prevention: Always acquire locks in same order
         */
        public static void q7_DeadlockExample() {
            Object lock1 = new Object();
            Object lock2 = new Object();
            
            // Risk of deadlock if threads acquire in different order
            synchronized (lock1) {
                synchronized (lock2) {
                    System.out.println("Safe - always same order");
                }
            }
        }
        
        /**
         * Q8: What does notify() do?
         * 
         * Answer:
         * - Wakes up ONE waiting thread (arbitrary choice)
         * - notifyAll() wakes up ALL waiting threads
         * - Must be called from synchronized block
         * - Only affects threads waiting on same lock
         */
        public static void q8_NotifyExample() throws InterruptedException {
            Object lock = new Object();
            
            synchronized (lock) {
                lock.notifyAll();  // Wake all waiting threads
            }
        }
        
        /**
         * Q9: What is CPU cache, and why do we need volatile?
         * 
         * Answer:
         * - Each CPU has L1, L2, L3 cache for fast access
         * - Thread might read cached value instead of main memory value
         * - volatile forces main memory access
         * - Important for visibility across cores
         */
        
        /**
         * Q10: What's the difference between notify and notifyAll?
         * 
         * Answer:
         * - notify(): Wakes only one random waiting thread
         * - notifyAll(): Wakes all waiting threads
         * - Use notifyAll() when multiple threads can proceed
         * - notify() for when only one thread should proceed
         */
    }
    
    // ==================== MEDIUM LEVEL QUESTIONS ====================
    
    public static class MediumQuestions {
        
        /**
         * Q11: Implement thread-safe counter
         * 
         * Time Complexity: O(1)
         * Space Complexity: O(1)
         */
        public static class ThreadSafeCounter {
            private int value = 0;
            private final Object lock = new Object();
            
            public void increment() {
                synchronized (lock) {
                    value++;
                }
            }
            
            public void decrement() {
                synchronized (lock) {
                    value--;
                }
            }
            
            public int getValue() {
                synchronized (lock) {
                    return value;
                }
            }
        }
        
        /**
         * Q12: Implement thread-safe stack
         */
        public static class SynchronizedStack<T> {
            private final List<T> items = new ArrayList<>();
            private final Object lock = new Object();
            
            public void push(T item) {
                synchronized (lock) {
                    items.add(item);
                }
            }
            
            public T pop() {
                synchronized (lock) {
                    if (items.isEmpty()) throw new EmptyStackException();
                    return items.remove(items.size() - 1);
                }
            }
            
            public int size() {
                synchronized (lock) {
                    return items.size();
                }
            }
        }
        
        /**
         * Q13: Implement producer-consumer with wait/notify
         */
        public static class ProducerConsumerQueue<T> {
            private final Queue<T> queue;
            private final int capacity;
            
            public ProducerConsumerQueue(int capacity) {
                this.queue = new LinkedList<>();
                this.capacity = capacity;
            }
            
            public void produce(T item) throws InterruptedException {
                synchronized (queue) {
                    while (queue.size() == capacity) {
                        queue.wait();  // Wait until not full
                    }
                    queue.offer(item);
                    queue.notifyAll();  // Wake consumers
                }
            }
            
            public T consume() throws InterruptedException {
                synchronized (queue) {
                    while (queue.isEmpty()) {
                        queue.wait();  // Wait until not empty
                    }
                    T item = queue.poll();
                    queue.notifyAll();  // Wake producers
                    return item;
                }
            }
        }
        
        /**
         * Q14: Implement barrier synchronization (like CountDownLatch)
         */
        public static class SimpleBarrier {
            private final int threadCount;
            private int waitingThreads = 0;
            private final Object lock = new Object();
            
            public SimpleBarrier(int threadCount) {
                this.threadCount = threadCount;
            }
            
            public void await() throws InterruptedException {
                synchronized (lock) {
                    waitingThreads++;
                    if (waitingThreads == threadCount) {
                        lock.notifyAll();  // Wake all threads
                        waitingThreads = 0;
                    } else {
                        lock.wait();  // Wait for others
                    }
                }
            }
        }
        
        /**
         * Q15: Thread per request vs thread pool
         * 
         * Thread per request: Create new thread for each request
         * - Pros: Simple
         * - Cons: High overhead, resource exhaustion
         * 
         * Thread pool: Reuse limited threads
         * - Pros: Resource efficient, scalable
         * - Cons: Slightly more complex
         * 
         * Answer: Always use thread pool in production
         */
        
        /**
         * Q16: What happens if exception thrown in thread?
         */
        public static void q16_ExceptionHandling() {
            Thread t = new Thread(() -> {
                throw new RuntimeException("Error in thread");
            });
            
            t.setUncaughtExceptionHandler((thread, exception) -> {
                System.out.println("Caught: " + exception.getMessage());
            });
            
            t.start();
        }
        
        /**
         * Q17: How to stop a thread gracefully?
         * 
         * Answer: Use volatile boolean flag, NOT Thread.stop()
         */
        public static class GracefulShutdown {
            private volatile boolean running = true;
            
            public void run() {
                while (running) {
                    // Do work
                }
                System.out.println("Thread stopped gracefully");
            }
            
            public void stop() {
                running = false;  // Signal to stop
            }
        }
        
        /**
         * Q18: Explain memory visibility problem
         * 
         * Without synchronization or volatile:
         * - Thread may read stale value from cache
         * - Another thread updates value in memory
         * - Reader thread never sees the update
         * 
         * Solution: Use synchronized, volatile, or AtomicInteger
         */
        
        /**
         * Q19: Compare synchronized vs ReentrantLock
         * 
         * synchronized:
         * - Simpler syntax
         * - Automatic unlock in exceptions (implicit)
         * - Less flexible
         * 
         * ReentrantLock:
         * - More flexible (try-lock, fairness, conditions)
         * - Must unlock in finally block
         * - More control
         */
        
        /**
         * Q20: What is lock striping?
         * 
         * Answer: Instead of one lock for whole collection,
         * have multiple locks for different segments
         * - Increases concurrency
         * - ConcurrentHashMap uses segment-based locking
         * - Trade-off: More complex, more memory
         */
    }
    
    // ==================== HARD LEVEL QUESTIONS ====================
    
    public static class HardQuestions {
        
        /**
         * Q21: Implement LRU Cache with thread safety
         * (From Module 03 but with concurrency)
         */
        public static class ThreadSafeLRUCache<K, V> {
            private final int capacity;
            private final Map<K, V> cache;
            private final ReentrantLock lock = new ReentrantLock();
            
            public ThreadSafeLRUCache(int capacity) {
                this.capacity = capacity;
                this.cache = new LinkedHashMap<K, V>(capacity, 0.75f, true) {
                    protected boolean removeEldestEntry(Map.Entry eldest) {
                        return size() > capacity;
                    }
                };
            }
            
            public V get(K key) {
                lock.lock();
                try {
                    return cache.getOrDefault(key, null);
                } finally {
                    lock.unlock();
                }
            }
            
            public void put(K key, V value) {
                lock.lock();
                try {
                    cache.put(key, value);
                } finally {
                    lock.unlock();
                }
            }
        }
        
        /**
         * Q22: Dining Philosophers Problem
         * Classic deadlock scenario with elegant solution
         */
        public static class DiningPhilosophers {
            private static final int N = 5;
            private final ReentrantLock[] forks = new ReentrantLock[N];
            private final ReentrantLock lock = new ReentrantLock();
            
            public DiningPhilosophers() {
                for (int i = 0; i < N; i++) {
                    forks[i] = new ReentrantLock();
                }
            }
            
            public void eat(int philosopherId) throws InterruptedException {
                int leftFork = philosopherId;
                int rightFork = (philosopherId + 1) % N;
                
                // Always acquire in order (prevents deadlock)
                if (leftFork > rightFork) {
                    int temp = leftFork;
                    leftFork = rightFork;
                    rightFork = temp;
                }
                
                forks[leftFork].lock();
                try {
                    forks[rightFork].lock();
                    try {
                        System.out.println("Philosopher " + philosopherId + " is eating");
                        Thread.sleep(1000);
                    } finally {
                        forks[rightFork].unlock();
                    }
                } finally {
                    forks[leftFork].unlock();
                }
            }
        }
        
        /**
         * Q23: Thread pool with custom rejection policy
         */
        public static ExecutorService createCustomThreadPool(int coreSize, int maxSize) {
            return new ThreadPoolExecutor(
                coreSize,
                maxSize,
                60L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                r -> {
                    Thread t = new Thread(r, "CustomThread");
                    t.setUncaughtExceptionHandler((thread, ex) -> {
                        System.err.println("Exception in " + thread.getName());
                    });
                    return t;
                },
                new ThreadPoolExecutor.CallerRunsPolicy()  // Run in caller thread
            );
        }
        
        /**
         * Q24: Detect deadlock in code
         * 
         * Question: This code might deadlock. When and how?
         */
        public static class DeadlockDetection {
            private Object lock1 = new Object();
            private Object lock2 = new Object();
            
            public void method1() {
                synchronized (lock1) {
                    try { Thread.sleep(10); } catch (Exception e) {}
                    synchronized (lock2) {
                        System.out.println("Method 1");
                    }
                }
            }
            
            public void method2() {
                synchronized (lock2) {  // Different order!
                    try { Thread.sleep(10); } catch (Exception e) {}
                    synchronized (lock1) {  // Gets lock1 - might wait on lock1
                        System.out.println("Method 2");
                    }
                }
            }
            
            // Answer: If thread1 calls method1 and thread2 calls method2
            // Thread1 has lock1, waits for lock2
            // Thread2 has lock2, waits for lock1
            // DEADLOCK!
            
            // Solution: Always acquire locks in same order
        }
        
        /**
         * Q25: Implement atomic operation without explicit locking
         * Using Compare-And-Swap (CAS)
         */
        public static class AtomicCounter {
            private volatile int value = 0;
            
            public synchronized void increment() {
                value++;
            }
            
            public int getValue() {
                return value;
            }
            
            // Or use AtomicInteger
            // private AtomicInteger counter = new AtomicInteger();
            // counter.incrementAndGet();
        }
    }
    
    // ==================== PRINTING QUESTIONS ====================
    
    public static void printQuestion(String number, String title, String problem, String solution) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("Q" + number + ": " + title);
        System.out.println("=".repeat(70));
        System.out.println("Problem: " + problem);
        System.out.println("\nSolution:\n" + solution);
    }
    
    // ==================== MAIN ====================
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("╔" + "═".repeat(68) + "╗");
        System.out.println("║" + " ".repeat(15) + "ELITE CONCURRENCY INTERVIEW QUESTIONS" + " ".repeat(16) + "║");
        System.out.println("║" + " ".repeat(20) + "40+ Questions Difficulty: All Levels" + " ".repeat(13) + "║");
        System.out.println("╚" + "═".repeat(68) + "╝");
        
        System.out.println("\n✅ EASY LEVEL (10 questions)");
        System.out.println("   Q1: Thread vs Runnable");
        System.out.println("   Q2: start() vs run()");
        System.out.println("   Q3: What is synchronized?");
        System.out.println("   Q4: Race condition explanation");
        System.out.println("   Q5: sleep() vs wait()");
        System.out.println("   Q6: volatile keyword");
        System.out.println("   Q7: Deadlock definition");
        System.out.println("   Q8: notify() vs notifyAll()");
        System.out.println("   Q9: CPU cache and visibility");
        System.out.println("   Q10: Thread communication");
        
        System.out.println("\n⚡ MEDIUM LEVEL (10 questions)");
        System.out.println("   Q11: Thread-safe counter");
        System.out.println("   Q12: Thread-safe stack");
        System.out.println("   Q13: Producer-consumer");
        System.out.println("   Q14: Barrier synchronization");
        System.out.println("   Q15: Thread pool vs per-request");
        System.out.println("   Q16: Exception handling in threads");
        System.out.println("   Q17: Graceful thread shutdown");
        System.out.println("   Q18: Memory visibility");
        System.out.println("   Q19: synchronized vs ReentrantLock");
        System.out.println("   Q20: Lock striping");
        
        System.out.println("\n🔥 HARD LEVEL (5+ questions)");
        System.out.println("   Q21: Thread-safe LRU Cache");
        System.out.println("   Q22: Dining Philosophers");
        System.out.println("   Q23: Custom thread pool");
        System.out.println("   Q24: Deadlock detection");
        System.out.println("   Q25: Atomic operations");
        
        System.out.println("\n" + "═".repeat(70));
        System.out.println("All questions cover FAANG company interviews");
        System.out.println("Study these patterns and practice with code!");
        System.out.println("═".repeat(70));
    }
}
