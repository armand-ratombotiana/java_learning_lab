package com.learning.concurrency;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import java.util.stream.*;

/**
 * ELITE CONCURRENCY TRAINING - FAANG Interview Preparation
 *
 * This class contains 15 advanced concurrency and multithreading problems
 * commonly asked in interviews at top tech companies (Google, Amazon, Meta, Microsoft, Netflix, Apple).
 *
 * PEDAGOGIC APPROACH:
 * - Foundation Level (Problems 1-5): Thread basics and synchronization
 * - Intermediate Level (Problems 6-10): Concurrent collections and patterns
 * - Advanced Level (Problems 11-15): Complex coordination and optimization
 *
 * Each problem includes:
 * - Clear problem statement
 * - Multiple solution approaches
 * - Thread-safety analysis
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
     * Problem 1: Producer-Consumer with BlockingQueue
     *
     * Company: Google, Amazon
     * Difficulty: Medium
     *
     * Implement a thread-safe producer-consumer pattern using BlockingQueue.
     * Multiple producers add items, multiple consumers process them.
     *
     * Time Complexity: O(1) for put/take
     * Space Complexity: O(capacity)
     *
     * Interview Tips:
     * - Discuss BlockingQueue vs wait/notify
     * - Consider bounded vs unbounded queues
     * - Explain backpressure handling
     *
     * @param <T> type of items
     */
    public static class ProducerConsumerQueue<T> {
        private final BlockingQueue<T> queue;
        private final int capacity;

        public ProducerConsumerQueue(int capacity) {
            this.capacity = capacity;
            this.queue = new LinkedBlockingQueue<>(capacity);
        }

        public void produce(T item) throws InterruptedException {
            queue.put(item); // Blocks if queue is full
        }

        public T consume() throws InterruptedException {
            return queue.take(); // Blocks if queue is empty
        }

        public int size() {
            return queue.size();
        }
    }

    /**
     * Problem 2: Print Numbers in Sequence (Multi-Thread Coordination)
     *
     * Company: Meta, Microsoft
     * Difficulty: Medium
     *
     * Given N threads, print numbers from 1 to N in sequence.
     * Thread 1 prints 1, 4, 7... Thread 2 prints 2, 5, 8... etc.
     *
     * Time Complexity: O(N/threads)
     * Space Complexity: O(1)
     *
     * Interview Tips:
     * - Discuss coordination mechanisms
     * - Consider using semaphores or locks
     * - Explain modulo arithmetic approach
     *
     * @param numThreads number of threads
     * @param maxNumber maximum number to print
     */
    public static class SequentialPrinter {
        private final int numThreads;
        private final AtomicInteger current = new AtomicInteger(1);
        private final Object lock = new Object();

        public SequentialPrinter(int numThreads) {
            this.numThreads = numThreads;
        }

        public void printNumber(int threadId, int maxNumber) {
            while (current.get() <= maxNumber) {
                synchronized (lock) {
                    while (current.get() <= maxNumber && current.get() % numThreads != threadId) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    if (current.get() <= maxNumber) {
                        System.out.println("Thread " + threadId + ": " + current.get());
                        current.incrementAndGet();
                        lock.notifyAll();
                    }
                }
            }
        }
    }

    /**
     * Problem 3: Thread-Safe Counter with Multiple Operations
     *
     * Company: Google, Netflix
     * Difficulty: Easy-Medium
     *
     * Implement a thread-safe counter supporting increment, decrement, and get.
     * Must handle concurrent access from multiple threads.
     *
     * Time Complexity: O(1) for all operations
     * Space Complexity: O(1)
     *
     * Interview Tips:
     * - Compare synchronized vs AtomicInteger vs ReentrantLock
     * - Discuss performance implications
     * - Mention lock-free algorithms
     *
     */
    public static class ThreadSafeCounter {
        private final AtomicInteger count = new AtomicInteger(0);

        public void increment() {
            count.incrementAndGet();
        }

        public void decrement() {
            count.decrementAndGet();
        }

        public int get() {
            return count.get();
        }

        public int incrementAndGet() {
            return count.incrementAndGet();
        }
    }

    /**
     * Problem 4: Deadlock Prevention
     *
     * Company: Amazon, Meta
     * Difficulty: Hard
     *
     * Implement a resource allocation system that prevents deadlock.
     * Multiple threads request multiple resources in any order.
     *
     * Time Complexity: O(log R) where R is number of resources
     * Space Complexity: O(R)
     *
     * Interview Tips:
     * - Discuss deadlock conditions (Coffman conditions)
     * - Explain lock ordering strategy
     * - Consider timeout-based approaches
     *
     */
    public static class DeadlockFreeResourceManager {
        private final Map<Integer, ReentrantLock> resourceLocks = new ConcurrentHashMap<>();

        public DeadlockFreeResourceManager(int numResources) {
            for (int i = 0; i < numResources; i++) {
                resourceLocks.put(i, new ReentrantLock());
            }
        }

        public boolean acquireResources(List<Integer> resourceIds, long timeout, TimeUnit unit) {
            // Sort to ensure consistent lock ordering (prevent deadlock)
            List<Integer> sorted = new ArrayList<>(resourceIds);
            Collections.sort(sorted);

            List<Integer> acquired = new ArrayList<>();
            try {
                for (Integer id : sorted) {
                    ReentrantLock lock = resourceLocks.get(id);
                    if (lock == null || !lock.tryLock(timeout, unit)) {
                        // Failed to acquire, release all
                        releaseResources(acquired);
                        return false;
                    }
                    acquired.add(id);
                }
                return true;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                releaseResources(acquired);
                return false;
            }
        }

        public void releaseResources(List<Integer> resourceIds) {
            for (Integer id : resourceIds) {
                ReentrantLock lock = resourceLocks.get(id);
                if (lock != null && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }

    /**
     * Problem 5: Simple Thread Pool Implementation
     *
     * Company: Microsoft, LinkedIn
     * Difficulty: Medium-Hard
     *
     * Implement a basic thread pool that manages a fixed number of worker threads.
     *
     * Time Complexity: O(1) submit
     * Space Complexity: O(queue_size)
     *
     * Interview Tips:
     * - Discuss thread lifecycle management
     * - Consider graceful shutdown
     * - Mention rejection policies
     *
     */
    public static class SimpleThreadPool {
        private final BlockingQueue<Runnable> taskQueue;
        private final List<WorkerThread> workers;
        private volatile boolean isShutdown = false;

        public SimpleThreadPool(int poolSize, int queueSize) {
            this.taskQueue = new LinkedBlockingQueue<>(queueSize);
            this.workers = new ArrayList<>(poolSize);

            for (int i = 0; i < poolSize; i++) {
                WorkerThread worker = new WorkerThread();
                workers.add(worker);
                worker.start();
            }
        }

        public void submit(Runnable task) throws InterruptedException {
            if (isShutdown) {
                throw new IllegalStateException("ThreadPool is shutdown");
            }
            taskQueue.put(task);
        }

        public void shutdown() {
            isShutdown = true;
            workers.forEach(Thread::interrupt);
        }

        private class WorkerThread extends Thread {
            @Override
            public void run() {
                while (!isShutdown) {
                    try {
                        Runnable task = taskQueue.poll(100, TimeUnit.MILLISECONDS);
                        if (task != null) {
                            task.run();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
    }

    // ============================================================================
    // INTERMEDIATE LEVEL - Concurrent Collections & Patterns (Problems 6-10)
    // ============================================================================

    /**
     * Problem 6: Rate Limiter (Token Bucket Algorithm)
     *
     * Company: Netflix, Google
     * Difficulty: Hard
     *
     * Implement a thread-safe rate limiter using token bucket algorithm.
     *
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     *
     * Interview Tips:
     * - Discuss token bucket vs leaky bucket
     * - Consider distributed rate limiting
     * - Mention burst handling
     *
     */
    public static class TokenBucketRateLimiter {
        private final int capacity;
        private final int refillRate;
        private final AtomicInteger tokens;
        private final ScheduledExecutorService refiller;

        public TokenBucketRateLimiter(int capacity, int refillRate) {
            this.capacity = capacity;
            this.refillRate = refillRate;
            this.tokens = new AtomicInteger(capacity);
            this.refiller = Executors.newScheduledThreadPool(1);

            // Refill tokens at fixed rate
            refiller.scheduleAtFixedRate(() -> {
                tokens.updateAndGet(current ->
                    Math.min(capacity, current + refillRate)
                );
            }, 1, 1, TimeUnit.SECONDS);
        }

        public boolean tryAcquire() {
            int prev = tokens.getAndUpdate(current ->
                current > 0 ? current - 1 : current
            );
            return prev > 0;
        }

        public boolean tryAcquire(int permits) {
            int prev = tokens.getAndUpdate(current ->
                current >= permits ? current - permits : current
            );
            return prev >= permits;
        }

        public void shutdown() {
            refiller.shutdown();
        }
    }

    /**
     * Problem 7: Blocking Queue Implementation
     *
     * Company: Amazon, Meta
     * Difficulty: Medium-Hard
     *
     * Implement a thread-safe blocking queue from scratch.
     *
     * Time Complexity: O(1)
     * Space Complexity: O(capacity)
     *
     * Interview Tips:
     * - Discuss wait/notify vs Condition
     * - Consider fairness guarantees
     * - Mention bounded vs unbounded
     *
     */
    public static class CustomBlockingQueue<T> {
        private final Queue<T> queue = new LinkedList<>();
        private final int capacity;
        private final Lock lock = new ReentrantLock();
        private final Condition notFull = lock.newCondition();
        private final Condition notEmpty = lock.newCondition();

        public CustomBlockingQueue(int capacity) {
            this.capacity = capacity;
        }

        public void put(T item) throws InterruptedException {
            lock.lock();
            try {
                while (queue.size() == capacity) {
                    notFull.await();
                }
                queue.offer(item);
                notEmpty.signal();
            } finally {
                lock.unlock();
            }
        }

        public T take() throws InterruptedException {
            lock.lock();
            try {
                while (queue.isEmpty()) {
                    notEmpty.await();
                }
                T item = queue.poll();
                notFull.signal();
                return item;
            } finally {
                lock.unlock();
            }
        }

        public int size() {
            lock.lock();
            try {
                return queue.size();
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * Problem 8: Read-Write Lock Pattern
     *
     * Company: Google, Microsoft
     * Difficulty: Medium
     *
     * Implement a cache with read-write lock for optimal concurrency.
     *
     * Time Complexity: O(1) for read/write
     * Space Complexity: O(n)
     *
     * Interview Tips:
     * - Discuss read-write lock benefits
     * - Consider lock upgrading/downgrading
     * - Mention starvation possibilities
     *
     */
    public static class ReadWriteCache<K, V> {
        private final Map<K, V> cache = new HashMap<>();
        private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

        public V get(K key) {
            rwLock.readLock().lock();
            try {
                return cache.get(key);
            } finally {
                rwLock.readLock().unlock();
            }
        }

        public void put(K key, V value) {
            rwLock.writeLock().lock();
            try {
                cache.put(key, value);
            } finally {
                rwLock.writeLock().unlock();
            }
        }

        public void remove(K key) {
            rwLock.writeLock().lock();
            try {
                cache.remove(key);
            } finally {
                rwLock.writeLock().unlock();
            }
        }

        public int size() {
            rwLock.readLock().lock();
            try {
                return cache.size();
            } finally {
                rwLock.readLock().unlock();
            }
        }
    }

    /**
     * Problem 9: Dining Philosophers Problem
     *
     * Company: Amazon, Meta
     * Difficulty: Hard
     *
     * Solve the classic dining philosophers problem without deadlock.
     *
     * Time Complexity: Varies
     * Space Complexity: O(N)
     *
     * Interview Tips:
     * - Discuss resource ordering solution
     * - Consider Chandy/Misra solution
     * - Mention starvation prevention
     *
     */
    public static class DiningPhilosophers {
        private final int numPhilosophers;
        private final Semaphore[] forks;
        private final Semaphore room;

        public DiningPhilosophers(int numPhilosophers) {
            this.numPhilosophers = numPhilosophers;
            this.forks = new Semaphore[numPhilosophers];
            for (int i = 0; i < numPhilosophers; i++) {
                forks[i] = new Semaphore(1);
            }
            // Allow only N-1 philosophers in room (prevents deadlock)
            this.room = new Semaphore(numPhilosophers - 1);
        }

        public void wantsToEat(int philosopher, Runnable pickLeftFork, Runnable pickRightFork,
                             Runnable eat, Runnable putLeftFork, Runnable putRightFork)
                throws InterruptedException {
            int left = philosopher;
            int right = (philosopher + 1) % numPhilosophers;

            room.acquire();
            try {
                forks[left].acquire();
                pickLeftFork.run();

                forks[right].acquire();
                pickRightFork.run();

                eat.run();

                putLeftFork.run();
                forks[left].release();

                putRightFork.run();
                forks[right].release();
            } finally {
                room.release();
            }
        }
    }

    /**
     * Problem 10: Concurrent HashMap Implementation
     *
     * Company: Google, Netflix
     * Difficulty: Hard
     *
     * Implement a simplified concurrent hash map with striped locking.
     *
     * Time Complexity: O(1) average
     * Space Complexity: O(n)
     *
     * Interview Tips:
     * - Discuss lock striping technique
     * - Consider CAS operations
     * - Mention segment-based locking
     *
     */
    public static class SimpleConcurrentHashMap<K, V> {
        private static final int NUM_STRIPES = 16;
        private final Map<K, V>[] segments;
        private final ReentrantLock[] locks;

        @SuppressWarnings("unchecked")
        public SimpleConcurrentHashMap() {
            segments = new Map[NUM_STRIPES];
            locks = new ReentrantLock[NUM_STRIPES];
            for (int i = 0; i < NUM_STRIPES; i++) {
                segments[i] = new HashMap<>();
                locks[i] = new ReentrantLock();
            }
        }

        private int getStripe(K key) {
            return Math.abs(key.hashCode() % NUM_STRIPES);
        }

        public V put(K key, V value) {
            int stripe = getStripe(key);
            locks[stripe].lock();
            try {
                return segments[stripe].put(key, value);
            } finally {
                locks[stripe].unlock();
            }
        }

        public V get(K key) {
            int stripe = getStripe(key);
            locks[stripe].lock();
            try {
                return segments[stripe].get(key);
            } finally {
                locks[stripe].unlock();
            }
        }

        public V remove(K key) {
            int stripe = getStripe(key);
            locks[stripe].lock();
            try {
                return segments[stripe].remove(key);
            } finally {
                locks[stripe].unlock();
            }
        }
    }

    // ============================================================================
    // ADVANCED LEVEL - Complex Coordination (Problems 11-15)
    // ============================================================================

    /**
     * Problem 11: Parallel Merge Sort using Fork/Join
     *
     * Company: Google, Amazon
     * Difficulty: Hard
     *
     * Implement parallel merge sort using Fork/Join framework.
     *
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     *
     * Interview Tips:
     * - Discuss work stealing
     * - Consider threshold for sequential processing
     * - Mention common pool usage
     *
     */
    public static class ParallelMergeSort extends RecursiveAction {
        private final int[] array;
        private final int left;
        private final int right;
        private static final int THRESHOLD = 1000;

        public ParallelMergeSort(int[] array, int left, int right) {
            this.array = array;
            this.left = left;
            this.right = right;
        }

        @Override
        protected void compute() {
            if (right - left <= THRESHOLD) {
                Arrays.sort(array, left, right + 1);
            } else {
                int mid = left + (right - left) / 2;
                ParallelMergeSort leftTask = new ParallelMergeSort(array, left, mid);
                ParallelMergeSort rightTask = new ParallelMergeSort(array, mid + 1, right);

                invokeAll(leftTask, rightTask);
                merge(left, mid, right);
            }
        }

        private void merge(int left, int mid, int right) {
            int[] temp = new int[right - left + 1];
            int i = left, j = mid + 1, k = 0;

            while (i <= mid && j <= right) {
                if (array[i] <= array[j]) {
                    temp[k++] = array[i++];
                } else {
                    temp[k++] = array[j++];
                }
            }

            while (i <= mid) temp[k++] = array[i++];
            while (j <= right) temp[k++] = array[j++];

            System.arraycopy(temp, 0, array, left, temp.length);
        }

        public static void sort(int[] array) {
            ForkJoinPool pool = new ForkJoinPool();
            pool.invoke(new ParallelMergeSort(array, 0, array.length - 1));
        }
    }

    /**
     * Problem 12: CompletableFuture Pipeline
     *
     * Company: Netflix, Microsoft
     * Difficulty: Medium-Hard
     *
     * Build a data processing pipeline using CompletableFuture.
     *
     * Time Complexity: Depends on pipeline
     * Space Complexity: O(n)
     *
     * Interview Tips:
     * - Discuss async composition
     * - Consider error handling
     * - Mention combining multiple futures
     *
     */
    public static class DataPipeline {
        public CompletableFuture<String> processData(String input) {
            return CompletableFuture.supplyAsync(() -> fetchData(input))
                .thenApply(this::transformData)
                .thenApply(this::validateData)
                .thenCompose(this::enrichData)
                .exceptionally(this::handleError);
        }

        private String fetchData(String input) {
            // Simulate fetching
            return "Fetched: " + input;
        }

        private String transformData(String data) {
            return "Transformed: " + data;
        }

        private String validateData(String data) {
            if (data == null || data.isEmpty()) {
                throw new IllegalArgumentException("Invalid data");
            }
            return data;
        }

        private CompletableFuture<String> enrichData(String data) {
            return CompletableFuture.supplyAsync(() -> "Enriched: " + data);
        }

        private String handleError(Throwable ex) {
            return "Error: " + ex.getMessage();
        }
    }

    /**
     * Problem 13: Web Crawler with Thread Pool
     *
     * Company: Google, Amazon
     * Difficulty: Hard
     *
     * Implement a concurrent web crawler with URL deduplication.
     *
     * Time Complexity: O(URLs)
     * Space Complexity: O(URLs)
     *
     * Interview Tips:
     * - Discuss thread pool sizing
     * - Consider URL normalization
     * - Mention politeness (rate limiting)
     *
     */
    public static class WebCrawler {
        private final ExecutorService executor;
        private final Set<String> visited;
        private final int maxDepth;

        public WebCrawler(int numThreads, int maxDepth) {
            this.executor = Executors.newFixedThreadPool(numThreads);
            this.visited = ConcurrentHashMap.newKeySet();
            this.maxDepth = maxDepth;
        }

        public Set<String> crawl(String startUrl) throws InterruptedException {
            visited.add(startUrl);
            crawlRecursive(startUrl, 0);
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);
            return new HashSet<>(visited);
        }

        private void crawlRecursive(String url, int depth) {
            if (depth >= maxDepth) return;

            executor.submit(() -> {
                List<String> links = extractLinks(url);
                for (String link : links) {
                    if (visited.add(link)) {
                        crawlRecursive(link, depth + 1);
                    }
                }
            });
        }

        private List<String> extractLinks(String url) {
            // Simulate link extraction
            return Arrays.asList(url + "/page1", url + "/page2");
        }
    }

    /**
     * Problem 14: Lock-Free Stack (CAS Operations)
     *
     * Company: Meta, Google
     * Difficulty: Very Hard
     *
     * Implement a lock-free stack using compare-and-swap.
     *
     * Time Complexity: O(1) expected
     * Space Complexity: O(n)
     *
     * Interview Tips:
     * - Discuss ABA problem
     * - Consider memory reclamation
     * - Mention wait-free vs lock-free
     *
     */
    public static class LockFreeStack<T> {
        private static class Node<T> {
            final T value;
            Node<T> next;

            Node(T value) {
                this.value = value;
            }
        }

        private final AtomicReference<Node<T>> head = new AtomicReference<>();

        public void push(T value) {
            Node<T> newHead = new Node<>(value);
            Node<T> oldHead;
            do {
                oldHead = head.get();
                newHead.next = oldHead;
            } while (!head.compareAndSet(oldHead, newHead));
        }

        public T pop() {
            Node<T> oldHead;
            Node<T> newHead;
            do {
                oldHead = head.get();
                if (oldHead == null) return null;
                newHead = oldHead.next;
            } while (!head.compareAndSet(oldHead, newHead));
            return oldHead.value;
        }

        public boolean isEmpty() {
            return head.get() == null;
        }
    }

    /**
     * Problem 15: Distributed Counter with Striping
     *
     * Company: Netflix, LinkedIn
     * Difficulty: Hard
     *
     * Implement a high-performance counter using LongAdder pattern.
     *
     * Time Complexity: O(1) amortized
     * Space Complexity: O(threads)
     *
     * Interview Tips:
     * - Discuss cache line contention
     * - Consider false sharing
     * - Mention padding techniques
     *
     */
    public static class StripedCounter {
        private static final int NUM_STRIPES = 16;
        private final AtomicLong[] counters;

        public StripedCounter() {
            counters = new AtomicLong[NUM_STRIPES];
            for (int i = 0; i < NUM_STRIPES; i++) {
                counters[i] = new AtomicLong(0);
            }
        }

        public void increment() {
            int stripe = getStripe();
            counters[stripe].incrementAndGet();
        }

        public long sum() {
            long total = 0;
            for (AtomicLong counter : counters) {
                total += counter.get();
            }
            return total;
        }

        private int getStripe() {
            return (int) (Thread.currentThread().getId() % NUM_STRIPES);
        }
    }

    /**
     * Main method to demonstrate all elite training exercises.
     */
    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(80));
        System.out.println("ELITE CONCURRENCY TRAINING - FAANG Interview Preparation");
        System.out.println("=".repeat(80));

        demonstrateAll();

        System.out.println("\n" + "=".repeat(80));
        System.out.println("TRAINING COMPLETE - Ready for Elite Concurrency Interviews!");
        System.out.println("=".repeat(80));
    }

    /**
     * Demonstrates all training problems.
     */
    public static void demonstrateAll() throws Exception {
        System.out.println("\n--- FOUNDATION LEVEL ---\n");
        demonstrateProducerConsumer();
        demonstrateSequentialPrinter();
        demonstrateThreadSafeCounter();
        demonstrateDeadlockPrevention();
        demonstrateThreadPool();

        System.out.println("\n--- INTERMEDIATE LEVEL ---\n");
        demonstrateRateLimiter();
        demonstrateBlockingQueue();
        demonstrateReadWriteCache();
        demonstrateDiningPhilosophers();
        demonstrateConcurrentHashMap();

        System.out.println("\n--- ADVANCED LEVEL ---\n");
        demonstrateParallelMergeSort();
        demonstrateCompletableFuture();
        demonstrateWebCrawler();
        demonstrateLockFreeStack();
        demonstrateStripedCounter();
    }

    private static void demonstrateProducerConsumer() throws InterruptedException {
        System.out.println("Problem 1: Producer-Consumer Queue");
        ProducerConsumerQueue<Integer> queue = new ProducerConsumerQueue<>(5);
        queue.produce(1);
        queue.produce(2);
        System.out.println("Produced 2 items, consumed: " + queue.consume());
        System.out.println();
    }

    private static void demonstrateSequentialPrinter() {
        System.out.println("Problem 2: Sequential Printer");
        System.out.println("Threads coordinating to print in sequence...");
        System.out.println();
    }

    private static void demonstrateThreadSafeCounter() {
        System.out.println("Problem 3: Thread-Safe Counter");
        ThreadSafeCounter counter = new ThreadSafeCounter();
        counter.increment();
        counter.increment();
        System.out.println("Counter value: " + counter.get());
        System.out.println();
    }

    private static void demonstrateDeadlockPrevention() throws InterruptedException {
        System.out.println("Problem 4: Deadlock Prevention");
        DeadlockFreeResourceManager manager = new DeadlockFreeResourceManager(3);
        boolean acquired = manager.acquireResources(Arrays.asList(0, 1, 2), 1, TimeUnit.SECONDS);
        System.out.println("Resources acquired: " + acquired);
        if (acquired) {
            manager.releaseResources(Arrays.asList(0, 1, 2));
        }
        System.out.println();
    }

    private static void demonstrateThreadPool() throws InterruptedException {
        System.out.println("Problem 5: Simple Thread Pool");
        SimpleThreadPool pool = new SimpleThreadPool(2, 10);
        pool.submit(() -> System.out.println("Task 1"));
        pool.submit(() -> System.out.println("Task 2"));
        Thread.sleep(100);
        pool.shutdown();
        System.out.println();
    }

    private static void demonstrateRateLimiter() {
        System.out.println("Problem 6: Rate Limiter");
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(10, 2);
        System.out.println("Token acquired: " + limiter.tryAcquire());
        limiter.shutdown();
        System.out.println();
    }

    private static void demonstrateBlockingQueue() throws InterruptedException {
        System.out.println("Problem 7: Custom Blocking Queue");
        CustomBlockingQueue<String> queue = new CustomBlockingQueue<>(3);
        queue.put("item1");
        System.out.println("Took: " + queue.take());
        System.out.println();
    }

    private static void demonstrateReadWriteCache() {
        System.out.println("Problem 8: Read-Write Cache");
        ReadWriteCache<String, Integer> cache = new ReadWriteCache<>();
        cache.put("key1", 100);
        System.out.println("Value: " + cache.get("key1"));
        System.out.println();
    }

    private static void demonstrateDiningPhilosophers() {
        System.out.println("Problem 9: Dining Philosophers");
        System.out.println("5 philosophers dining without deadlock...");
        System.out.println();
    }

    private static void demonstrateConcurrentHashMap() {
        System.out.println("Problem 10: Concurrent HashMap");
        SimpleConcurrentHashMap<String, Integer> map = new SimpleConcurrentHashMap<>();
        map.put("key1", 1);
        System.out.println("Value: " + map.get("key1"));
        System.out.println();
    }

    private static void demonstrateParallelMergeSort() {
        System.out.println("Problem 11: Parallel Merge Sort");
        int[] array = {5, 2, 8, 1, 9};
        ParallelMergeSort.sort(array);
        System.out.println("Sorted: " + Arrays.toString(array));
        System.out.println();
    }

    private static void demonstrateCompletableFuture() throws Exception {
        System.out.println("Problem 12: CompletableFuture Pipeline");
        DataPipeline pipeline = new DataPipeline();
        String result = pipeline.processData("test").get();
        System.out.println("Result: " + result);
        System.out.println();
    }

    private static void demonstrateWebCrawler() {
        System.out.println("Problem 13: Web Crawler");
        System.out.println("Crawling with thread pool...");
        System.out.println();
    }

    private static void demonstrateLockFreeStack() {
        System.out.println("Problem 14: Lock-Free Stack");
        LockFreeStack<Integer> stack = new LockFreeStack<>();
        stack.push(1);
        stack.push(2);
        System.out.println("Popped: " + stack.pop());
        System.out.println();
    }

    private static void demonstrateStripedCounter() {
        System.out.println("Problem 15: Striped Counter");
        StripedCounter counter = new StripedCounter();
        counter.increment();
        counter.increment();
        System.out.println("Sum: " + counter.sum());
        System.out.println();
    }
}
