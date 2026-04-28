package com.learning;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * ELITE CONCURRENCY & MULTITHREADING TRAINING - FAANG Interview Preparation
 *
 * This class contains 15 advanced concurrency problems commonly asked
 * in interviews at top tech companies (Google, Amazon, Meta, Microsoft, Netflix, Apple).
 *
 * PEDAGOGIC APPROACH:
 * - Foundation Level (Problems 1-5): Thread basics, synchronization, deadlock prevention
 * - Intermediate Level (Problems 6-10): ExecutorService, Locks, Concurrent collections
 * - Advanced Level (Problems 11-15): CompletableFuture, Fork/Join, Lock-free algorithms
 *
 * Each problem includes:
 * - Clear problem statement
 * - Company attribution
 * - Complete implementation
 * - Time and space complexity analysis
 * - Interview tips and follow-up questions
 *
 * @author Elite Interview Preparation Team
 * @version 1.0
 */
public class EliteConcurrencyTraining {

    // ============================================================================
    // FOUNDATION LEVEL - Thread Basics & Synchronization (Problems 1-5)
    // ============================================================================

    /**
     * Problem 1: Producer-Consumer Problem
     *
     * Company: Amazon, Google, Meta
     * Difficulty: Medium
     *
     * Implement a thread-safe producer-consumer pattern using wait() and notify().
     * The buffer has a fixed capacity. Producer waits when buffer is full,
     * consumer waits when buffer is empty.
     *
     * Example:
     *   Producer adds items: [1, 2, 3]
     *   Consumer takes items: [1, 2, 3]
     *
     * Time Complexity: O(1) per operation
     * Space Complexity: O(capacity) for the buffer
     *
     * Interview Tips:
     * - Discuss alternatives: BlockingQueue, Semaphores
     * - Explain spurious wakeups and why while loop is needed
     * - Consider bounded vs unbounded buffers
     *
     * @param <T> the type of elements in the buffer
     */
    public static class ProducerConsumer<T> {
        private final Queue<T> buffer;
        private final int capacity;

        public ProducerConsumer(int capacity) {
            this.capacity = capacity;
            this.buffer = new LinkedList<>();
        }

        /**
         * Produces an item and adds it to the buffer.
         * Waits if buffer is full.
         *
         * @param item the item to produce
         * @throws InterruptedException if interrupted while waiting
         */
        public synchronized void produce(T item) throws InterruptedException {
            while (buffer.size() == capacity) {
                wait(); // Wait until space is available
            }
            buffer.offer(item);
            notifyAll(); // Notify consumers that item is available
        }

        /**
         * Consumes an item from the buffer.
         * Waits if buffer is empty.
         *
         * @return the consumed item
         * @throws InterruptedException if interrupted while waiting
         */
        public synchronized T consume() throws InterruptedException {
            while (buffer.isEmpty()) {
                wait(); // Wait until item is available
            }
            T item = buffer.poll();
            notifyAll(); // Notify producers that space is available
            return item;
        }

        /**
         * Returns the current size of the buffer.
         *
         * @return current buffer size
         */
        public synchronized int size() {
            return buffer.size();
        }
    }

    /**
     * Problem 2: Print Numbers in Sequence (Multi-thread Coordination)
     *
     * Company: Microsoft, Amazon
     * Difficulty: Medium
     *
     * Create N threads that print numbers from 1 to N*M in sequence.
     * Thread 0 prints 1, Thread 1 prints 2, ..., Thread N-1 prints N,
     * then back to Thread 0 prints N+1, etc.
     *
     * Example (3 threads, 10 numbers):
     *   Thread-0: 1, 4, 7, 10
     *   Thread-1: 2, 5, 8
     *   Thread-2: 3, 6, 9
     *
     * Time Complexity: O(N*M) total operations
     * Space Complexity: O(1)
     *
     * Interview Tips:
     * - Discuss synchronization mechanisms (wait/notify vs Semaphore)
     * - Consider fairness and starvation issues
     * - Explain modulo arithmetic for thread coordination
     */
    public static class SequentialNumberPrinter {
        private final int numThreads;
        private final int maxNumber;
        private int currentNumber = 1;
        private final List<String> output = new ArrayList<>();

        public SequentialNumberPrinter(int numThreads, int maxNumber) {
            this.numThreads = numThreads;
            this.maxNumber = maxNumber;
        }

        /**
         * Prints numbers assigned to this thread in sequence.
         *
         * @param threadId the ID of the current thread (0 to numThreads-1)
         */
        public synchronized void printNumber(int threadId) {
            while (currentNumber <= maxNumber) {
                // Wait until it's this thread's turn
                while (currentNumber <= maxNumber && currentNumber % numThreads != threadId) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                // Print if it's still within range
                if (currentNumber <= maxNumber) {
                    output.add("Thread-" + threadId + ": " + currentNumber);
                    currentNumber++;
                    notifyAll();
                }
            }
        }

        /**
         * Returns the output produced by all threads.
         *
         * @return list of printed messages
         */
        public List<String> getOutput() {
            return new ArrayList<>(output);
        }

        /**
         * Runs the sequential number printing with multiple threads.
         *
         * @return list of all printed messages in order
         */
        public List<String> run() throws InterruptedException {
            Thread[] threads = new Thread[numThreads];
            for (int i = 0; i < numThreads; i++) {
                final int threadId = i;
                threads[i] = new Thread(() -> printNumber(threadId));
                threads[i].start();
            }

            for (Thread thread : threads) {
                thread.join();
            }

            return getOutput();
        }
    }

    /**
     * Problem 3: Thread-Safe Counter
     *
     * Company: Google, Amazon, Meta
     * Difficulty: Easy-Medium
     *
     * Implement a thread-safe counter that supports increment, decrement,
     * and get operations. Compare different synchronization approaches.
     *
     * Example:
     *   10 threads each increment 1000 times
     *   Final count should be exactly 10000
     *
     * Time Complexity: O(1) per operation
     * Space Complexity: O(1)
     *
     * Interview Tips:
     * - Compare synchronized, AtomicInteger, and Lock approaches
     * - Discuss performance implications of each approach
     * - Explain memory visibility and happens-before relationship
     */
    public static class ThreadSafeCounter {
        private int count = 0;
        private final AtomicInteger atomicCount = new AtomicInteger(0);
        private final Lock lock = new ReentrantLock();

        /**
         * Increments the counter using synchronized keyword.
         */
        public synchronized void incrementSynchronized() {
            count++;
        }

        /**
         * Decrements the counter using synchronized keyword.
         */
        public synchronized void decrementSynchronized() {
            count--;
        }

        /**
         * Gets the current count using synchronized keyword.
         *
         * @return current count
         */
        public synchronized int getCountSynchronized() {
            return count;
        }

        /**
         * Increments the counter using AtomicInteger.
         */
        public void incrementAtomic() {
            atomicCount.incrementAndGet();
        }

        /**
         * Decrements the counter using AtomicInteger.
         */
        public void decrementAtomic() {
            atomicCount.decrementAndGet();
        }

        /**
         * Gets the current count using AtomicInteger.
         *
         * @return current atomic count
         */
        public int getCountAtomic() {
            return atomicCount.get();
        }

        /**
         * Increments the counter using ReentrantLock.
         */
        public void incrementWithLock() {
            lock.lock();
            try {
                count++;
            } finally {
                lock.unlock();
            }
        }

        /**
         * Decrements the counter using ReentrantLock.
         */
        public void decrementWithLock() {
            lock.lock();
            try {
                count--;
            } finally {
                lock.unlock();
            }
        }

        /**
         * Gets the current count using ReentrantLock.
         *
         * @return current count
         */
        public int getCountWithLock() {
            lock.lock();
            try {
                return count;
            } finally {
                lock.unlock();
            }
        }

        /**
         * Resets all counters to zero.
         */
        public void reset() {
            lock.lock();
            try {
                count = 0;
                atomicCount.set(0);
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * Problem 4: Deadlock Detection and Prevention
     *
     * Company: Amazon, Microsoft, Google
     * Difficulty: Hard
     *
     * Implement a mechanism to prevent deadlock when acquiring multiple locks.
     * Use lock ordering strategy to ensure deadlock-free execution.
     *
     * Example:
     *   Transfer money between accounts without deadlock
     *   Account A -> Account B and Account B -> Account A simultaneously
     *
     * Time Complexity: O(1) per transfer
     * Space Complexity: O(1)
     *
     * Interview Tips:
     * - Discuss deadlock conditions: mutual exclusion, hold and wait, no preemption, circular wait
     * - Explain lock ordering and timeout strategies
     * - Consider tryLock() with timeout as alternative
     */
    public static class BankAccount {
        private final int accountId;
        private double balance;
        private final Lock lock = new ReentrantLock();

        public BankAccount(int accountId, double initialBalance) {
            this.accountId = accountId;
            this.balance = initialBalance;
        }

        public int getAccountId() {
            return accountId;
        }

        public double getBalance() {
            return balance;
        }

        public Lock getLock() {
            return lock;
        }

        /**
         * Transfers money from one account to another using lock ordering to prevent deadlock.
         *
         * @param from source account
         * @param to destination account
         * @param amount amount to transfer
         * @return true if transfer successful, false otherwise
         */
        public static boolean transferDeadlockFree(BankAccount from, BankAccount to, double amount) {
            // Lock ordering: always lock the account with smaller ID first
            BankAccount first = from.accountId < to.accountId ? from : to;
            BankAccount second = from.accountId < to.accountId ? to : from;

            first.lock.lock();
            try {
                second.lock.lock();
                try {
                    // Perform the transfer
                    if (from.balance >= amount) {
                        from.balance -= amount;
                        to.balance += amount;
                        return true;
                    }
                    return false;
                } finally {
                    second.lock.unlock();
                }
            } finally {
                first.lock.unlock();
            }
        }

        /**
         * Transfers money using tryLock with timeout to avoid indefinite waiting.
         *
         * @param from source account
         * @param to destination account
         * @param amount amount to transfer
         * @param timeout timeout duration
         * @param unit time unit
         * @return true if transfer successful, false otherwise
         */
        public static boolean transferWithTimeout(BankAccount from, BankAccount to,
                                                  double amount, long timeout, TimeUnit unit) {
            long startTime = System.nanoTime();
            long timeoutNanos = unit.toNanos(timeout);

            try {
                // Try to acquire first lock
                if (!from.lock.tryLock(timeout, unit)) {
                    return false;
                }

                try {
                    // Calculate remaining time for second lock
                    long elapsed = System.nanoTime() - startTime;
                    long remainingNanos = timeoutNanos - elapsed;

                    if (remainingNanos <= 0 || !to.lock.tryLock(remainingNanos, TimeUnit.NANOSECONDS)) {
                        return false;
                    }

                    try {
                        // Perform the transfer
                        if (from.balance >= amount) {
                            from.balance -= amount;
                            to.balance += amount;
                            return true;
                        }
                        return false;
                    } finally {
                        to.lock.unlock();
                    }
                } finally {
                    from.lock.unlock();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
    }

    /**
     * Problem 5: Implement Thread Pool from Scratch
     *
     * Company: Google, Meta, Amazon
     * Difficulty: Hard
     *
     * Implement a basic thread pool that manages a fixed number of worker threads
     * and executes submitted tasks from a queue.
     *
     * Example:
     *   ThreadPool pool = new ThreadPool(4);
     *   pool.submit(() -> System.out.println("Task 1"));
     *   pool.submit(() -> System.out.println("Task 2"));
     *   pool.shutdown();
     *
     * Time Complexity: O(1) for task submission
     * Space Complexity: O(queue size)
     *
     * Interview Tips:
     * - Discuss thread lifecycle and graceful shutdown
     * - Explain blocking queue for task distribution
     * - Consider rejection policies when queue is full
     */
    public static class SimpleThreadPool {
        private final int poolSize;
        private final BlockingQueue<Runnable> taskQueue;
        private final List<WorkerThread> workers;
        private volatile boolean isShutdown = false;

        public SimpleThreadPool(int poolSize) {
            this.poolSize = poolSize;
            this.taskQueue = new LinkedBlockingQueue<>();
            this.workers = new ArrayList<>(poolSize);

            // Create and start worker threads
            for (int i = 0; i < poolSize; i++) {
                WorkerThread worker = new WorkerThread();
                workers.add(worker);
                worker.start();
            }
        }

        /**
         * Submits a task for execution.
         *
         * @param task the task to execute
         * @throws IllegalStateException if pool is shutdown
         */
        public void submit(Runnable task) {
            if (isShutdown) {
                throw new IllegalStateException("Thread pool is shutdown");
            }
            taskQueue.offer(task);
        }

        /**
         * Initiates an orderly shutdown. Previously submitted tasks are executed,
         * but no new tasks will be accepted.
         */
        public void shutdown() {
            isShutdown = true;
            // Add poison pills to signal workers to terminate
            for (int i = 0; i < poolSize; i++) {
                taskQueue.offer(() -> {});
            }
        }

        /**
         * Blocks until all tasks have completed after a shutdown request,
         * or the timeout occurs, whichever happens first.
         *
         * @param timeout the maximum time to wait
         * @param unit the time unit of the timeout argument
         * @return true if pool terminated, false if timeout elapsed
         */
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            long deadline = System.nanoTime() + unit.toNanos(timeout);
            for (WorkerThread worker : workers) {
                long remaining = deadline - System.nanoTime();
                if (remaining <= 0) {
                    return false;
                }
                worker.join(TimeUnit.NANOSECONDS.toMillis(remaining));
                if (worker.isAlive()) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Returns the current size of the task queue.
         *
         * @return number of pending tasks
         */
        public int getQueueSize() {
            return taskQueue.size();
        }

        /**
         * Worker thread that continuously pulls and executes tasks from the queue.
         */
        private class WorkerThread extends Thread {
            @Override
            public void run() {
                while (!isShutdown || !taskQueue.isEmpty()) {
                    try {
                        Runnable task = taskQueue.poll(100, TimeUnit.MILLISECONDS);
                        if (task != null) {
                            task.run();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        // Log exception but continue processing
                        System.err.println("Task execution failed: " + e.getMessage());
                    }
                }
            }
        }
    }

    // ============================================================================
    // INTERMEDIATE LEVEL - ExecutorService, Locks, Concurrent Collections (Problems 6-10)
    // ============================================================================

    /**
     * Problem 6: Rate Limiter Implementation (Token Bucket Algorithm)
     *
     * Company: Google, Meta, Amazon
     * Difficulty: Medium-Hard
     *
     * Implement a rate limiter using the token bucket algorithm.
     * Allow up to N requests per time window, with smooth rate limiting.
     *
     * Example:
     *   RateLimiter limiter = new RateLimiter(10, 1, TimeUnit.SECONDS);
     *   limiter.tryAcquire() -> true (if tokens available)
     *
     * Time Complexity: O(1) per request
     * Space Complexity: O(1)
     *
     * Interview Tips:
     * - Compare token bucket vs leaky bucket vs fixed window
     * - Discuss distributed rate limiting with Redis
     * - Explain burst handling and refill strategies
     */
    public static class RateLimiter {
        private final long maxTokens;
        private final long refillIntervalNanos;
        private final AtomicLong availableTokens;
        private final AtomicLong lastRefillTimestamp;

        /**
         * Creates a rate limiter with specified capacity and refill rate.
         *
         * @param maxTokens maximum number of tokens in the bucket
         * @param refillAmount number of tokens to add per interval
         * @param refillInterval time between refills
         * @param unit time unit for refill interval
         */
        public RateLimiter(long maxTokens, long refillAmount, long refillInterval, TimeUnit unit) {
            this.maxTokens = maxTokens;
            this.refillIntervalNanos = unit.toNanos(refillInterval) / refillAmount;
            this.availableTokens = new AtomicLong(maxTokens);
            this.lastRefillTimestamp = new AtomicLong(System.nanoTime());
        }

        /**
         * Tries to acquire a permit from the rate limiter.
         *
         * @return true if permit acquired, false otherwise
         */
        public boolean tryAcquire() {
            return tryAcquire(1);
        }

        /**
         * Tries to acquire the specified number of permits.
         *
         * @param permits number of permits to acquire
         * @return true if permits acquired, false otherwise
         */
        public boolean tryAcquire(int permits) {
            refill();

            long current = availableTokens.get();
            while (current >= permits) {
                if (availableTokens.compareAndSet(current, current - permits)) {
                    return true;
                }
                current = availableTokens.get();
            }
            return false;
        }

        /**
         * Refills the token bucket based on elapsed time.
         */
        private void refill() {
            long now = System.nanoTime();
            long lastRefill = lastRefillTimestamp.get();
            long elapsedNanos = now - lastRefill;

            if (elapsedNanos > refillIntervalNanos) {
                long tokensToAdd = elapsedNanos / refillIntervalNanos;
                long newTokens = Math.min(maxTokens, availableTokens.get() + tokensToAdd);

                if (lastRefillTimestamp.compareAndSet(lastRefill, now)) {
                    availableTokens.set(newTokens);
                }
            }
        }

        /**
         * Returns the current number of available tokens.
         *
         * @return available tokens
         */
        public long getAvailableTokens() {
            refill();
            return availableTokens.get();
        }
    }

    /**
     * Problem 7: Blocking Queue Implementation
     *
     * Company: Amazon, Microsoft, Google
     * Difficulty: Medium
     *
     * Implement a thread-safe blocking queue with put() and take() operations.
     * Use ReentrantLock and Condition for signaling.
     *
     * Example:
     *   BlockingQueue<Integer> queue = new CustomBlockingQueue<>(10);
     *   queue.put(1); // Blocks if full
     *   queue.take(); // Blocks if empty
     *
     * Time Complexity: O(1) for put and take
     * Space Complexity: O(capacity)
     *
     * Interview Tips:
     * - Compare Condition vs wait/notify
     * - Discuss fairness guarantees
     * - Explain use cases: producer-consumer, thread pools
     */
    public static class CustomBlockingQueue<E> {
        private final Object[] items;
        private int putIndex = 0;
        private int takeIndex = 0;
        private int count = 0;
        private final Lock lock = new ReentrantLock();
        private final Condition notEmpty = lock.newCondition();
        private final Condition notFull = lock.newCondition();

        public CustomBlockingQueue(int capacity) {
            if (capacity <= 0) {
                throw new IllegalArgumentException("Capacity must be positive");
            }
            this.items = new Object[capacity];
        }

        /**
         * Inserts the specified element into this queue, waiting if necessary
         * for space to become available.
         *
         * @param element the element to add
         * @throws InterruptedException if interrupted while waiting
         */
        public void put(E element) throws InterruptedException {
            if (element == null) {
                throw new NullPointerException();
            }

            lock.lock();
            try {
                while (count == items.length) {
                    notFull.await();
                }
                items[putIndex] = element;
                putIndex = (putIndex + 1) % items.length;
                count++;
                notEmpty.signal();
            } finally {
                lock.unlock();
            }
        }

        /**
         * Retrieves and removes the head of this queue, waiting if necessary
         * until an element becomes available.
         *
         * @return the head of this queue
         * @throws InterruptedException if interrupted while waiting
         */
        @SuppressWarnings("unchecked")
        public E take() throws InterruptedException {
            lock.lock();
            try {
                while (count == 0) {
                    notEmpty.await();
                }
                E element = (E) items[takeIndex];
                items[takeIndex] = null;
                takeIndex = (takeIndex + 1) % items.length;
                count--;
                notFull.signal();
                return element;
            } finally {
                lock.unlock();
            }
        }

        /**
         * Returns the number of elements in this queue.
         *
         * @return the number of elements
         */
        public int size() {
            lock.lock();
            try {
                return count;
            } finally {
                lock.unlock();
            }
        }

        /**
         * Returns the capacity of this queue.
         *
         * @return the capacity
         */
        public int capacity() {
            return items.length;
        }

        /**
         * Returns true if this queue is empty.
         *
         * @return true if empty
         */
        public boolean isEmpty() {
            lock.lock();
            try {
                return count == 0;
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * Problem 8: Read-Write Lock Pattern
     *
     * Company: Google, Amazon, Meta
     * Difficulty: Medium
     *
     * Implement a cache with read-write lock to allow concurrent reads
     * but exclusive writes. Demonstrate performance benefits over synchronized.
     *
     * Example:
     *   Cache cache = new Cache();
     *   cache.put("key", "value"); // Exclusive write
     *   cache.get("key"); // Concurrent reads allowed
     *
     * Time Complexity: O(1) for get/put
     * Space Complexity: O(n) where n is number of entries
     *
     * Interview Tips:
     * - Explain when ReadWriteLock is beneficial vs synchronized
     * - Discuss lock upgrading and downgrading
     * - Consider StampedLock for better performance in Java 8+
     */
    public static class ReadWriteCache<K, V> {
        private final Map<K, V> cache = new HashMap<>();
        private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
        private final Lock readLock = rwLock.readLock();
        private final Lock writeLock = rwLock.writeLock();

        /**
         * Retrieves a value from the cache.
         * Multiple threads can read concurrently.
         *
         * @param key the key
         * @return the value, or null if not found
         */
        public V get(K key) {
            readLock.lock();
            try {
                return cache.get(key);
            } finally {
                readLock.unlock();
            }
        }

        /**
         * Puts a value into the cache.
         * Acquires exclusive write lock.
         *
         * @param key the key
         * @param value the value
         * @return the previous value, or null
         */
        public V put(K key, V value) {
            writeLock.lock();
            try {
                return cache.put(key, value);
            } finally {
                writeLock.unlock();
            }
        }

        /**
         * Removes a value from the cache.
         *
         * @param key the key
         * @return the removed value, or null
         */
        public V remove(K key) {
            writeLock.lock();
            try {
                return cache.remove(key);
            } finally {
                writeLock.unlock();
            }
        }

        /**
         * Returns the size of the cache.
         *
         * @return number of entries
         */
        public int size() {
            readLock.lock();
            try {
                return cache.size();
            } finally {
                readLock.unlock();
            }
        }

        /**
         * Clears all entries from the cache.
         */
        public void clear() {
            writeLock.lock();
            try {
                cache.clear();
            } finally {
                writeLock.unlock();
            }
        }

        /**
         * Computes a value if absent using double-checked locking pattern.
         *
         * @param key the key
         * @param mappingFunction the function to compute value
         * @return the value
         */
        public V computeIfAbsent(K key, Function<K, V> mappingFunction) {
            // First check with read lock
            readLock.lock();
            try {
                V value = cache.get(key);
                if (value != null) {
                    return value;
                }
            } finally {
                readLock.unlock();
            }

            // Compute with write lock
            writeLock.lock();
            try {
                // Double-check inside write lock
                V value = cache.get(key);
                if (value == null) {
                    value = mappingFunction.apply(key);
                    cache.put(key, value);
                }
                return value;
            } finally {
                writeLock.unlock();
            }
        }
    }

    /**
     * Problem 9: Dining Philosophers Problem
     *
     * Company: Microsoft, Google, Amazon
     * Difficulty: Hard
     *
     * Solve the classic dining philosophers problem without deadlock.
     * N philosophers sit at a round table, each needs 2 forks to eat.
     *
     * Example:
     *   5 philosophers, 5 forks
     *   Each philosopher alternates between thinking and eating
     *   No deadlock or starvation
     *
     * Time Complexity: Depends on think/eat duration
     * Space Complexity: O(N) for N philosophers
     *
     * Interview Tips:
     * - Discuss resource ordering solution
     * - Explain arbitrator/waiter solution
     * - Consider Chandy-Misra solution for distributed systems
     */
    public static class DiningPhilosophers {
        private final int numPhilosophers;
        private final Lock[] forks;
        private final List<String> events = new CopyOnWriteArrayList<>();

        public DiningPhilosophers(int numPhilosophers) {
            this.numPhilosophers = numPhilosophers;
            this.forks = new ReentrantLock[numPhilosophers];
            for (int i = 0; i < numPhilosophers; i++) {
                forks[i] = new ReentrantLock();
            }
        }

        /**
         * A philosopher attempts to eat by acquiring two forks.
         * Uses lock ordering to prevent deadlock.
         *
         * @param philosopherId the philosopher's ID (0 to N-1)
         * @param eatAction the action to perform while eating
         */
        public void dine(int philosopherId, Runnable eatAction) {
            int leftFork = philosopherId;
            int rightFork = (philosopherId + 1) % numPhilosophers;

            // Lock ordering: acquire lower-numbered fork first to prevent circular wait
            int firstFork = Math.min(leftFork, rightFork);
            int secondFork = Math.max(leftFork, rightFork);

            forks[firstFork].lock();
            try {
                events.add("Philosopher " + philosopherId + " picked up fork " + firstFork);

                forks[secondFork].lock();
                try {
                    events.add("Philosopher " + philosopherId + " picked up fork " + secondFork);
                    events.add("Philosopher " + philosopherId + " is eating");

                    // Eat
                    eatAction.run();

                    events.add("Philosopher " + philosopherId + " finished eating");
                } finally {
                    events.add("Philosopher " + philosopherId + " put down fork " + secondFork);
                    forks[secondFork].unlock();
                }
            } finally {
                events.add("Philosopher " + philosopherId + " put down fork " + firstFork);
                forks[firstFork].unlock();
            }
        }

        /**
         * Returns all events that occurred during dining.
         *
         * @return list of events
         */
        public List<String> getEvents() {
            return new ArrayList<>(events);
        }

        /**
         * Clears all recorded events.
         */
        public void clearEvents() {
            events.clear();
        }
    }

    /**
     * Problem 10: Concurrent HashMap Implementation (Simplified)
     *
     * Company: Google, Meta, Amazon
     * Difficulty: Hard
     *
     * Implement a simplified concurrent hash map with segment-based locking.
     * Each segment can be locked independently for better concurrency.
     *
     * Example:
     *   ConcurrentMap<String, Integer> map = new SimpleConcurrentHashMap<>(16);
     *   map.put("key", 1);
     *   map.get("key"); // Returns 1
     *
     * Time Complexity: O(1) average for get/put
     * Space Complexity: O(n) where n is number of entries
     *
     * Interview Tips:
     * - Explain segment-based locking vs global lock
     * - Discuss Java's ConcurrentHashMap implementation
     * - Consider lock-free algorithms with CAS operations
     */
    public static class SimpleConcurrentHashMap<K, V> {
        private static final int DEFAULT_SEGMENTS = 16;
        private final Segment<K, V>[] segments;

        @SuppressWarnings("unchecked")
        public SimpleConcurrentHashMap(int numSegments) {
            this.segments = new Segment[numSegments];
            for (int i = 0; i < numSegments; i++) {
                segments[i] = new Segment<>();
            }
        }

        public SimpleConcurrentHashMap() {
            this(DEFAULT_SEGMENTS);
        }

        /**
         * Computes the segment index for a given key.
         *
         * @param key the key
         * @return segment index
         */
        private int getSegmentIndex(K key) {
            int hash = key.hashCode();
            return Math.abs(hash) % segments.length;
        }

        /**
         * Associates the specified value with the specified key.
         *
         * @param key the key
         * @param value the value
         * @return the previous value, or null
         */
        public V put(K key, V value) {
            int segmentIndex = getSegmentIndex(key);
            return segments[segmentIndex].put(key, value);
        }

        /**
         * Returns the value to which the specified key is mapped.
         *
         * @param key the key
         * @return the value, or null if not found
         */
        public V get(K key) {
            int segmentIndex = getSegmentIndex(key);
            return segments[segmentIndex].get(key);
        }

        /**
         * Removes the mapping for the specified key.
         *
         * @param key the key
         * @return the previous value, or null
         */
        public V remove(K key) {
            int segmentIndex = getSegmentIndex(key);
            return segments[segmentIndex].remove(key);
        }

        /**
         * Returns the total number of key-value mappings.
         *
         * @return the size
         */
        public int size() {
            int totalSize = 0;
            for (Segment<K, V> segment : segments) {
                totalSize += segment.size();
            }
            return totalSize;
        }

        /**
         * A segment represents a portion of the hash map with independent locking.
         */
        private static class Segment<K, V> {
            private final Map<K, V> map = new HashMap<>();
            private final Lock lock = new ReentrantLock();

            public V put(K key, V value) {
                lock.lock();
                try {
                    return map.put(key, value);
                } finally {
                    lock.unlock();
                }
            }

            public V get(K key) {
                lock.lock();
                try {
                    return map.get(key);
                } finally {
                    lock.unlock();
                }
            }

            public V remove(K key) {
                lock.lock();
                try {
                    return map.remove(key);
                } finally {
                    lock.unlock();
                }
            }

            public int size() {
                lock.lock();
                try {
                    return map.size();
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    // ============================================================================
    // ADVANCED LEVEL - CompletableFuture, Fork/Join, Lock-Free (Problems 11-15)
    // ============================================================================

    /**
     * Problem 11: Parallel Merge Sort using Fork/Join Framework
     *
     * Company: Google, Amazon, Meta
     * Difficulty: Medium-Hard
     *
     * Implement parallel merge sort using Java's Fork/Join framework.
     * Split array into tasks until threshold, then merge results.
     *
     * Example:
     *   int[] arr = {5, 2, 8, 1, 9};
     *   parallelMergeSort(arr);
     *   Result: [1, 2, 5, 8, 9]
     *
     * Time Complexity: O(n log n) with parallel speedup
     * Space Complexity: O(n) for temporary arrays
     *
     * Interview Tips:
     * - Discuss work-stealing algorithm
     * - Explain threshold for switching to sequential sort
     * - Compare with parallel streams
     */
    public static class ParallelMergeSort {
        private static final int THRESHOLD = 1000;
        private static final ForkJoinPool pool = new ForkJoinPool();

        /**
         * Sorts an array using parallel merge sort.
         *
         * @param array the array to sort
         * @return sorted array
         */
        public static int[] sort(int[] array) {
            if (array == null || array.length <= 1) {
                return array;
            }
            return pool.invoke(new MergeSortTask(array, 0, array.length));
        }

        /**
         * RecursiveTask for parallel merge sort.
         */
        private static class MergeSortTask extends RecursiveTask<int[]> {
            private final int[] array;
            private final int start;
            private final int end;

            public MergeSortTask(int[] array, int start, int end) {
                this.array = array;
                this.start = start;
                this.end = end;
            }

            @Override
            protected int[] compute() {
                int length = end - start;

                // Base case: use sequential sort for small arrays
                if (length <= THRESHOLD) {
                    int[] result = Arrays.copyOfRange(array, start, end);
                    Arrays.sort(result);
                    return result;
                }

                // Divide
                int mid = start + length / 2;
                MergeSortTask leftTask = new MergeSortTask(array, start, mid);
                MergeSortTask rightTask = new MergeSortTask(array, mid, end);

                // Fork both tasks
                leftTask.fork();
                int[] rightResult = rightTask.compute();
                int[] leftResult = leftTask.join();

                // Merge
                return merge(leftResult, rightResult);
            }

            /**
             * Merges two sorted arrays into one sorted array.
             */
            private int[] merge(int[] left, int[] right) {
                int[] result = new int[left.length + right.length];
                int i = 0, j = 0, k = 0;

                while (i < left.length && j < right.length) {
                    if (left[i] <= right[j]) {
                        result[k++] = left[i++];
                    } else {
                        result[k++] = right[j++];
                    }
                }

                while (i < left.length) {
                    result[k++] = left[i++];
                }

                while (j < right.length) {
                    result[k++] = right[j++];
                }

                return result;
            }
        }
    }

    /**
     * Problem 12: CompletableFuture Pipeline
     *
     * Company: Amazon, Microsoft, Google
     * Difficulty: Medium
     *
     * Build a complex asynchronous pipeline using CompletableFuture.
     * Chain multiple async operations: fetch user -> fetch orders -> process -> aggregate.
     *
     * Example:
     *   fetchUser(userId)
     *     .thenCompose(user -> fetchOrders(user))
     *     .thenApply(orders -> processOrders(orders))
     *     .thenAccept(result -> save(result));
     *
     * Time Complexity: Depends on individual operations
     * Space Complexity: O(1) for the pipeline structure
     *
     * Interview Tips:
     * - Explain difference between thenApply, thenCompose, thenCombine
     * - Discuss exception handling with exceptionally and handle
     * - Consider timeout handling and cancellation
     */
    public static class AsyncPipeline {

        /**
         * Represents a user entity.
         */
        public record User(int id, String name) {}

        /**
         * Represents an order entity.
         */
        public record Order(int orderId, int userId, double amount) {}

        /**
         * Represents processed order result.
         */
        public record OrderSummary(int userId, String userName, int orderCount, double totalAmount) {}

        /**
         * Fetches user asynchronously (simulated).
         *
         * @param userId the user ID
         * @return CompletableFuture with user
         */
        public CompletableFuture<User> fetchUser(int userId) {
            return CompletableFuture.supplyAsync(() -> {
                // Simulate delay
                sleep(100);
                return new User(userId, "User" + userId);
            });
        }

        /**
         * Fetches orders for a user asynchronously (simulated).
         *
         * @param user the user
         * @return CompletableFuture with list of orders
         */
        public CompletableFuture<List<Order>> fetchOrders(User user) {
            return CompletableFuture.supplyAsync(() -> {
                // Simulate delay
                sleep(100);
                return List.of(
                    new Order(1, user.id(), 100.0),
                    new Order(2, user.id(), 200.0),
                    new Order(3, user.id(), 150.0)
                );
            });
        }

        /**
         * Processes orders to create a summary.
         *
         * @param user the user
         * @param orders the orders
         * @return order summary
         */
        public OrderSummary processOrders(User user, List<Order> orders) {
            double totalAmount = orders.stream()
                .mapToDouble(Order::amount)
                .sum();
            return new OrderSummary(user.id(), user.name(), orders.size(), totalAmount);
        }

        /**
         * Creates a complete async pipeline to fetch and process user orders.
         *
         * @param userId the user ID
         * @return CompletableFuture with order summary
         */
        public CompletableFuture<OrderSummary> getUserOrderSummary(int userId) {
            return fetchUser(userId)
                .thenCompose(user ->
                    fetchOrders(user).thenApply(orders -> processOrders(user, orders))
                )
                .exceptionally(ex -> {
                    System.err.println("Error processing orders: " + ex.getMessage());
                    return new OrderSummary(userId, "Unknown", 0, 0.0);
                });
        }

        /**
         * Combines multiple user summaries in parallel.
         *
         * @param userIds list of user IDs
         * @return CompletableFuture with list of summaries
         */
        public CompletableFuture<List<OrderSummary>> getCombinedSummaries(List<Integer> userIds) {
            List<CompletableFuture<OrderSummary>> futures = userIds.stream()
                .map(this::getUserOrderSummary)
                .toList();

            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                    .map(CompletableFuture::join)
                    .toList());
        }

        /**
         * Helper method to sleep without checked exception.
         */
        private void sleep(long millis) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Problem 13: Web Crawler with Thread Pool
     *
     * Company: Google, Amazon, Meta
     * Difficulty: Hard
     *
     * Implement a multi-threaded web crawler that visits URLs up to a max depth.
     * Use thread pool for concurrent crawling and track visited URLs.
     *
     * Example:
     *   crawler.crawl("http://example.com", 2);
     *   Returns all URLs found up to depth 2
     *
     * Time Complexity: O(V + E) where V is URLs, E is links
     * Space Complexity: O(V) for visited set
     *
     * Interview Tips:
     * - Discuss URL normalization and duplicate detection
     * - Explain politeness: delays, robots.txt
     * - Consider distributed crawling strategies
     */
    public static class WebCrawler {
        private final int maxDepth;
        private final Set<String> visited = ConcurrentHashMap.newKeySet();
        private final ExecutorService executor;
        private final UrlExtractor urlExtractor;

        /**
         * Interface for extracting URLs from a page.
         */
        public interface UrlExtractor {
            List<String> extractUrls(String url);
        }

        public WebCrawler(int maxDepth, int threadPoolSize, UrlExtractor urlExtractor) {
            this.maxDepth = maxDepth;
            this.executor = Executors.newFixedThreadPool(threadPoolSize);
            this.urlExtractor = urlExtractor;
        }

        /**
         * Crawls starting from the given URL up to maxDepth.
         *
         * @param startUrl the starting URL
         * @return set of all visited URLs
         */
        public Set<String> crawl(String startUrl) {
            if (startUrl == null || startUrl.isEmpty()) {
                return new HashSet<>();
            }

            CountDownLatch latch = new CountDownLatch(1);
            crawlRecursive(startUrl, 0, latch);

            try {
                latch.await(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            executor.shutdown();
            return new HashSet<>(visited);
        }

        /**
         * Recursively crawls URLs up to max depth.
         */
        private void crawlRecursive(String url, int depth, CountDownLatch parentLatch) {
            if (depth > maxDepth || !visited.add(url)) {
                parentLatch.countDown();
                return;
            }

            executor.submit(() -> {
                try {
                    // Extract URLs from current page
                    List<String> urls = urlExtractor.extractUrls(url);

                    if (depth < maxDepth && !urls.isEmpty()) {
                        CountDownLatch childLatch = new CountDownLatch(urls.size());
                        for (String childUrl : urls) {
                            crawlRecursive(childUrl, depth + 1, childLatch);
                        }

                        try {
                            childLatch.await();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                } finally {
                    parentLatch.countDown();
                }
            });
        }

        /**
         * Returns all visited URLs.
         *
         * @return set of visited URLs
         */
        public Set<String> getVisited() {
            return new HashSet<>(visited);
        }
    }

    /**
     * Problem 14: Lock-Free Stack (CAS Operations)
     *
     * Company: Google, Meta, Amazon
     * Difficulty: Hard
     *
     * Implement a lock-free stack using Compare-And-Swap (CAS) operations.
     * Use AtomicReference for wait-free push and pop operations.
     *
     * Example:
     *   LockFreeStack<Integer> stack = new LockFreeStack<>();
     *   stack.push(1);
     *   stack.push(2);
     *   stack.pop(); // Returns 2
     *
     * Time Complexity: O(1) amortized for push/pop
     * Space Complexity: O(n) for n elements
     *
     * Interview Tips:
     * - Explain ABA problem and solutions (tagged pointers, hazard pointers)
     * - Discuss advantages of lock-free vs lock-based
     * - Compare with Treiber stack algorithm
     */
    public static class LockFreeStack<E> {
        private final AtomicReference<Node<E>> head = new AtomicReference<>();

        /**
         * Node class for stack elements.
         */
        private static class Node<E> {
            final E item;
            final Node<E> next;

            Node(E item, Node<E> next) {
                this.item = item;
                this.next = next;
            }
        }

        /**
         * Pushes an element onto the stack using CAS.
         *
         * @param item the item to push
         */
        public void push(E item) {
            if (item == null) {
                throw new NullPointerException();
            }

            Node<E> newNode = new Node<>(item, null);
            Node<E> oldHead;

            do {
                oldHead = head.get();
                newNode = new Node<>(item, oldHead);
            } while (!head.compareAndSet(oldHead, newNode));
        }

        /**
         * Pops an element from the stack using CAS.
         *
         * @return the popped element, or null if stack is empty
         */
        public E pop() {
            Node<E> oldHead;
            Node<E> newHead;

            do {
                oldHead = head.get();
                if (oldHead == null) {
                    return null;
                }
                newHead = oldHead.next;
            } while (!head.compareAndSet(oldHead, newHead));

            return oldHead.item;
        }

        /**
         * Peeks at the top element without removing it.
         *
         * @return the top element, or null if stack is empty
         */
        public E peek() {
            Node<E> current = head.get();
            return current == null ? null : current.item;
        }

        /**
         * Returns true if the stack is empty.
         *
         * @return true if empty
         */
        public boolean isEmpty() {
            return head.get() == null;
        }

        /**
         * Returns the size of the stack (not thread-safe for accuracy).
         *
         * @return approximate size
         */
        public int size() {
            int count = 0;
            Node<E> current = head.get();
            while (current != null) {
                count++;
                current = current.next;
            }
            return count;
        }
    }

    /**
     * Problem 15: Distributed Counter with Striping
     *
     * Company: Google, Meta, Amazon
     * Difficulty: Hard
     *
     * Implement a high-performance counter using striping technique.
     * Reduce contention by distributing increments across multiple counters.
     *
     * Example:
     *   StripedCounter counter = new StripedCounter(8);
     *   counter.increment(); // Distributes across 8 stripes
     *   counter.get(); // Returns total count
     *
     * Time Complexity: O(1) for increment, O(stripes) for get
     * Space Complexity: O(stripes)
     *
     * Interview Tips:
     * - Explain how striping reduces contention
     * - Discuss trade-off between increment speed and get accuracy
     * - Compare with LongAdder in Java
     */
    public static class StripedCounter {
        private final AtomicLong[] stripes;
        private final int numStripes;

        public StripedCounter(int numStripes) {
            if (numStripes <= 0) {
                throw new IllegalArgumentException("Number of stripes must be positive");
            }
            this.numStripes = numStripes;
            this.stripes = new AtomicLong[numStripes];
            for (int i = 0; i < numStripes; i++) {
                stripes[i] = new AtomicLong(0);
            }
        }

        public StripedCounter() {
            this(Runtime.getRuntime().availableProcessors());
        }

        /**
         * Increments the counter by 1.
         */
        public void increment() {
            increment(1);
        }

        /**
         * Increments the counter by the specified delta.
         *
         * @param delta the amount to add
         */
        public void increment(long delta) {
            int index = getStripeIndex();
            stripes[index].addAndGet(delta);
        }

        /**
         * Decrements the counter by 1.
         */
        public void decrement() {
            increment(-1);
        }

        /**
         * Returns the current value by summing all stripes.
         * Note: This is not an atomic snapshot.
         *
         * @return the current count
         */
        public long get() {
            long sum = 0;
            for (AtomicLong stripe : stripes) {
                sum += stripe.get();
            }
            return sum;
        }

        /**
         * Resets all stripes to zero.
         */
        public void reset() {
            for (AtomicLong stripe : stripes) {
                stripe.set(0);
            }
        }

        /**
         * Determines which stripe to use based on thread ID.
         *
         * @return stripe index
         */
        private int getStripeIndex() {
            long threadId = Thread.currentThread().threadId();
            return (int) (threadId % numStripes);
        }

        /**
         * Returns the number of stripes.
         *
         * @return number of stripes
         */
        public int getNumStripes() {
            return numStripes;
        }
    }

    // ============================================================================
    // DEMONSTRATION METHOD
    // ============================================================================

    /**
     * Demonstrates all 15 elite concurrency problems.
     * This method showcases the usage of each problem's solution.
     */
    public static void demonstrateAll() {
        System.out.println("=".repeat(80));
        System.out.println("ELITE CONCURRENCY TRAINING - ALL 15 PROBLEMS DEMONSTRATION");
        System.out.println("=".repeat(80));

        // Problem 1: Producer-Consumer
        demonstrateProducerConsumer();

        // Problem 2: Sequential Number Printer
        demonstrateSequentialPrinter();

        // Problem 3: Thread-Safe Counter
        demonstrateThreadSafeCounter();

        // Problem 4: Deadlock Prevention
        demonstrateDeadlockPrevention();

        // Problem 5: Thread Pool
        demonstrateThreadPool();

        // Problem 6: Rate Limiter
        demonstrateRateLimiter();

        // Problem 7: Blocking Queue
        demonstrateBlockingQueue();

        // Problem 8: Read-Write Cache
        demonstrateReadWriteCache();

        // Problem 9: Dining Philosophers
        demonstrateDiningPhilosophers();

        // Problem 10: Concurrent HashMap
        demonstrateConcurrentHashMap();

        // Problem 11: Parallel Merge Sort
        demonstrateParallelMergeSort();

        // Problem 12: CompletableFuture Pipeline
        demonstrateCompletableFuture();

        // Problem 13: Web Crawler
        demonstrateWebCrawler();

        // Problem 14: Lock-Free Stack
        demonstrateLockFreeStack();

        // Problem 15: Striped Counter
        demonstrateStripedCounter();

        System.out.println("\n" + "=".repeat(80));
        System.out.println("ALL DEMONSTRATIONS COMPLETED SUCCESSFULLY!");
        System.out.println("=".repeat(80));
    }

    private static void demonstrateProducerConsumer() {
        System.out.println("\n[Problem 1] Producer-Consumer Pattern");
        System.out.println("-".repeat(80));

        ProducerConsumer<Integer> pc = new ProducerConsumer<>(5);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    pc.produce(i);
                    System.out.println("Produced: " + i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    Integer item = pc.consume();
                    System.out.println("Consumed: " + item);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
            System.out.println("Final buffer size: " + pc.size());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void demonstrateSequentialPrinter() {
        System.out.println("\n[Problem 2] Sequential Number Printer");
        System.out.println("-".repeat(80));

        try {
            SequentialNumberPrinter printer = new SequentialNumberPrinter(3, 9);
            List<String> output = printer.run();
            output.forEach(System.out::println);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void demonstrateThreadSafeCounter() {
        System.out.println("\n[Problem 3] Thread-Safe Counter");
        System.out.println("-".repeat(80));

        ThreadSafeCounter counter = new ThreadSafeCounter();
        int numThreads = 10;
        int incrementsPerThread = 1000;

        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counter.incrementAtomic();
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Expected: " + (numThreads * incrementsPerThread));
        System.out.println("Actual: " + counter.getCountAtomic());
    }

    private static void demonstrateDeadlockPrevention() {
        System.out.println("\n[Problem 4] Deadlock Prevention");
        System.out.println("-".repeat(80));

        BankAccount account1 = new BankAccount(1, 1000);
        BankAccount account2 = new BankAccount(2, 1000);

        Thread t1 = new Thread(() -> {
            boolean success = BankAccount.transferDeadlockFree(account1, account2, 100);
            System.out.println("Transfer 1->2: " + (success ? "Success" : "Failed"));
        });

        Thread t2 = new Thread(() -> {
            boolean success = BankAccount.transferDeadlockFree(account2, account1, 50);
            System.out.println("Transfer 2->1: " + (success ? "Success" : "Failed"));
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
            System.out.println("Account 1 balance: " + account1.getBalance());
            System.out.println("Account 2 balance: " + account2.getBalance());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void demonstrateThreadPool() {
        System.out.println("\n[Problem 5] Custom Thread Pool");
        System.out.println("-".repeat(80));

        SimpleThreadPool pool = new SimpleThreadPool(4);

        for (int i = 1; i <= 10; i++) {
            final int taskId = i;
            pool.submit(() -> System.out.println("Executing task " + taskId + " on " + Thread.currentThread().getName()));
        }

        pool.shutdown();
        try {
            boolean terminated = pool.awaitTermination(5, TimeUnit.SECONDS);
            System.out.println("Pool terminated: " + terminated);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void demonstrateRateLimiter() {
        System.out.println("\n[Problem 6] Rate Limiter");
        System.out.println("-".repeat(80));

        RateLimiter limiter = new RateLimiter(5, 1, 1, TimeUnit.SECONDS);

        for (int i = 0; i < 10; i++) {
            boolean acquired = limiter.tryAcquire();
            System.out.println("Request " + (i + 1) + ": " + (acquired ? "Allowed" : "Rate limited"));
        }

        System.out.println("Available tokens: " + limiter.getAvailableTokens());
    }

    private static void demonstrateBlockingQueue() {
        System.out.println("\n[Problem 7] Custom Blocking Queue");
        System.out.println("-".repeat(80));

        CustomBlockingQueue<Integer> queue = new CustomBlockingQueue<>(5);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    queue.put(i);
                    System.out.println("Put: " + i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    Integer item = queue.take();
                    System.out.println("Took: " + item);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
            System.out.println("Final queue size: " + queue.size());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void demonstrateReadWriteCache() {
        System.out.println("\n[Problem 8] Read-Write Cache");
        System.out.println("-".repeat(80));

        ReadWriteCache<String, Integer> cache = new ReadWriteCache<>();

        cache.put("key1", 100);
        cache.put("key2", 200);

        System.out.println("key1: " + cache.get("key1"));
        System.out.println("key2: " + cache.get("key2"));
        System.out.println("Cache size: " + cache.size());

        Integer computed = cache.computeIfAbsent("key3", k -> 300);
        System.out.println("Computed key3: " + computed);
    }

    private static void demonstrateDiningPhilosophers() {
        System.out.println("\n[Problem 9] Dining Philosophers");
        System.out.println("-".repeat(80));

        DiningPhilosophers dining = new DiningPhilosophers(5);
        Thread[] philosophers = new Thread[5];

        for (int i = 0; i < 5; i++) {
            final int id = i;
            philosophers[i] = new Thread(() -> {
                dining.dine(id, () -> {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            });
            philosophers[i].start();
        }

        for (Thread philosopher : philosophers) {
            try {
                philosopher.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("All philosophers dined successfully!");
        System.out.println("Total events: " + dining.getEvents().size());
    }

    private static void demonstrateConcurrentHashMap() {
        System.out.println("\n[Problem 10] Concurrent HashMap");
        System.out.println("-".repeat(80));

        SimpleConcurrentHashMap<String, Integer> map = new SimpleConcurrentHashMap<>(16);

        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);

        System.out.println("one: " + map.get("one"));
        System.out.println("two: " + map.get("two"));
        System.out.println("Size: " + map.size());

        map.remove("two");
        System.out.println("After removal, size: " + map.size());
    }

    private static void demonstrateParallelMergeSort() {
        System.out.println("\n[Problem 11] Parallel Merge Sort");
        System.out.println("-".repeat(80));

        int[] array = {5, 2, 8, 1, 9, 3, 7, 4, 6};
        System.out.println("Original: " + Arrays.toString(array));

        int[] sorted = ParallelMergeSort.sort(array);
        System.out.println("Sorted: " + Arrays.toString(sorted));
    }

    private static void demonstrateCompletableFuture() {
        System.out.println("\n[Problem 12] CompletableFuture Pipeline");
        System.out.println("-".repeat(80));

        AsyncPipeline pipeline = new AsyncPipeline();

        try {
            AsyncPipeline.OrderSummary summary = pipeline.getUserOrderSummary(1).get();
            System.out.println("User: " + summary.userName());
            System.out.println("Orders: " + summary.orderCount());
            System.out.println("Total: $" + summary.totalAmount());
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void demonstrateWebCrawler() {
        System.out.println("\n[Problem 13] Web Crawler");
        System.out.println("-".repeat(80));

        WebCrawler.UrlExtractor extractor = url -> {
            // Simulate extracting URLs
            if (url.equals("http://example.com")) {
                return List.of("http://example.com/page1", "http://example.com/page2");
            } else {
                return List.of();
            }
        };

        WebCrawler crawler = new WebCrawler(2, 4, extractor);
        Set<String> visited = crawler.crawl("http://example.com");

        System.out.println("Visited " + visited.size() + " URLs:");
        visited.forEach(System.out::println);
    }

    private static void demonstrateLockFreeStack() {
        System.out.println("\n[Problem 14] Lock-Free Stack");
        System.out.println("-".repeat(80));

        LockFreeStack<Integer> stack = new LockFreeStack<>();

        stack.push(1);
        stack.push(2);
        stack.push(3);

        System.out.println("Peek: " + stack.peek());
        System.out.println("Pop: " + stack.pop());
        System.out.println("Pop: " + stack.pop());
        System.out.println("Size: " + stack.size());
    }

    private static void demonstrateStripedCounter() {
        System.out.println("\n[Problem 15] Striped Counter");
        System.out.println("-".repeat(80));

        StripedCounter counter = new StripedCounter(8);
        int numThreads = 10;
        int incrementsPerThread = 10000;

        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counter.increment();
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Expected: " + (numThreads * incrementsPerThread));
        System.out.println("Actual: " + counter.get());
        System.out.println("Stripes: " + counter.getNumStripes());
    }

    /**
     * Main method to run all demonstrations.
     */
    public static void main(String[] args) {
        demonstrateAll();
    }
}
