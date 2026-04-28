package com.learning.concurrency;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import com.learning.concurrency.EliteConcurrencyTraining.*;

/**
 * Comprehensive test suite for EliteConcurrencyTraining.
 *
 * Tests all 15 elite concurrency problems with:
 * - Thread safety verification
 * - Concurrent execution tests
 * - Edge case handling
 * - Performance validation
 *
 * @author Elite Interview Preparation Team
 * @version 1.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EliteConcurrencyTrainingTest {

    // ============================================================================
    // FOUNDATION LEVEL TESTS (Problems 1-5)
    // ============================================================================

    @Test
    @Order(1)
    @DisplayName("Problem 1: Producer-Consumer - Basic Operations")
    void testProducerConsumer_BasicOperations() throws InterruptedException {
        ProducerConsumerQueue<Integer> queue = new ProducerConsumerQueue<>(5);

        queue.produce(1);
        queue.produce(2);
        queue.produce(3);

        assertEquals(1, queue.consume());
        assertEquals(2, queue.consume());
        assertEquals(3, queue.consume());
    }

    @Test
    @Order(2)
    @DisplayName("Problem 1: Producer-Consumer - Concurrent Access")
    void testProducerConsumer_ConcurrentAccess() throws InterruptedException {
        ProducerConsumerQueue<Integer> queue = new ProducerConsumerQueue<>(100);
        int numProducers = 5;
        int numConsumers = 5;
        int itemsPerProducer = 20;

        CountDownLatch latch = new CountDownLatch(numProducers + numConsumers);
        List<Integer> consumed = new CopyOnWriteArrayList<>();

        // Start producers
        for (int i = 0; i < numProducers; i++) {
            final int producerId = i;
            new Thread(() -> {
                try {
                    for (int j = 0; j < itemsPerProducer; j++) {
                        queue.produce(producerId * 100 + j);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        // Start consumers
        for (int i = 0; i < numConsumers; i++) {
            new Thread(() -> {
                try {
                    for (int j = 0; j < itemsPerProducer; j++) {
                        consumed.add(queue.consume());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertEquals(numProducers * itemsPerProducer, consumed.size());
    }

    @Test
    @Order(3)
    @DisplayName("Problem 2: Sequential Printer - Thread Coordination")
    void testSequentialPrinter() throws InterruptedException {
        SequentialPrinter printer = new SequentialPrinter(3);
        int maxNumber = 9;
        CountDownLatch latch = new CountDownLatch(3);

        for (int i = 0; i < 3; i++) {
            final int threadId = i;
            new Thread(() -> {
                printer.printNumber(threadId, maxNumber);
                latch.countDown();
            }).start();
        }

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    @Order(4)
    @DisplayName("Problem 3: Thread-Safe Counter - Basic Operations")
    void testThreadSafeCounter_BasicOperations() {
        ThreadSafeCounter counter = new ThreadSafeCounter();

        assertEquals(0, counter.get());
        counter.increment();
        assertEquals(1, counter.get());
        counter.decrement();
        assertEquals(0, counter.get());
        assertEquals(1, counter.incrementAndGet());
    }

    @Test
    @Order(5)
    @DisplayName("Problem 3: Thread-Safe Counter - Concurrent Increments")
    void testThreadSafeCounter_ConcurrentIncrements() throws InterruptedException {
        ThreadSafeCounter counter = new ThreadSafeCounter();
        int numThreads = 10;
        int incrementsPerThread = 1000;

        CountDownLatch latch = new CountDownLatch(numThreads);

        for (int i = 0; i < numThreads; i++) {
            new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counter.increment();
                }
                latch.countDown();
            }).start();
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertEquals(numThreads * incrementsPerThread, counter.get());
    }

    @Test
    @Order(6)
    @DisplayName("Problem 4: Deadlock Prevention - Basic Acquisition")
    void testDeadlockPrevention_BasicAcquisition() throws InterruptedException {
        DeadlockFreeResourceManager manager = new DeadlockFreeResourceManager(5);

        List<Integer> resources = Arrays.asList(0, 1, 2);
        boolean acquired = manager.acquireResources(resources, 1, TimeUnit.SECONDS);

        assertTrue(acquired);
        manager.releaseResources(resources);
    }

    @Test
    @Order(7)
    @DisplayName("Problem 4: Deadlock Prevention - Concurrent Acquisition")
    void testDeadlockPrevention_ConcurrentAcquisition() throws InterruptedException {
        DeadlockFreeResourceManager manager = new DeadlockFreeResourceManager(3);
        int numThreads = 10;
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < numThreads; i++) {
            new Thread(() -> {
                try {
                    List<Integer> resources = Arrays.asList(0, 1, 2);
                    if (manager.acquireResources(resources, 100, TimeUnit.MILLISECONDS)) {
                        successCount.incrementAndGet();
                        Thread.sleep(10);
                        manager.releaseResources(resources);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertTrue(successCount.get() > 0, "At least some threads should succeed");
    }

    @Test
    @Order(8)
    @DisplayName("Problem 5: Thread Pool - Basic Execution")
    void testThreadPool_BasicExecution() throws InterruptedException {
        SimpleThreadPool pool = new SimpleThreadPool(2, 10);
        AtomicInteger counter = new AtomicInteger(0);

        for (int i = 0; i < 5; i++) {
            pool.submit(counter::incrementAndGet);
        }

        Thread.sleep(500);
        pool.shutdown();

        assertEquals(5, counter.get());
    }

    @Test
    @Order(9)
    @DisplayName("Problem 5: Thread Pool - Reject After Shutdown")
    void testThreadPool_RejectAfterShutdown() throws InterruptedException {
        SimpleThreadPool pool = new SimpleThreadPool(2, 10);
        pool.shutdown();

        assertThrows(IllegalStateException.class, () -> {
            pool.submit(() -> {});
        });
    }

    // ============================================================================
    // INTERMEDIATE LEVEL TESTS (Problems 6-10)
    // ============================================================================

    @Test
    @Order(10)
    @DisplayName("Problem 6: Rate Limiter - Basic Acquire")
    void testRateLimiter_BasicAcquire() throws InterruptedException {
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(10, 2);

        // Give scheduler a moment to start
        Thread.sleep(100);

        assertTrue(limiter.tryAcquire());
        assertTrue(limiter.tryAcquire(5));

        limiter.shutdown();
    }

    @Test
    @Order(11)
    @DisplayName("Problem 6: Rate Limiter - Exhaust Tokens")
    void testRateLimiter_ExhaustTokens() throws InterruptedException {
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(5, 1);

        // Give scheduler a moment to start
        Thread.sleep(100);

        // Acquire all tokens
        for (int i = 0; i < 5; i++) {
            assertTrue(limiter.tryAcquire(), "Should acquire token " + (i + 1));
        }

        // Should fail when exhausted
        assertFalse(limiter.tryAcquire());

        limiter.shutdown();
    }

    @Test
    @Order(12)
    @DisplayName("Problem 7: Blocking Queue - Basic Put/Take")
    void testBlockingQueue_BasicPutTake() throws InterruptedException {
        CustomBlockingQueue<Integer> queue = new CustomBlockingQueue<>(5);

        queue.put(1);
        queue.put(2);
        queue.put(3);

        assertEquals(3, queue.size());
        assertEquals(1, queue.take());
        assertEquals(2, queue.take());
        assertEquals(1, queue.size());
    }

    @Test
    @Order(13)
    @DisplayName("Problem 7: Blocking Queue - Concurrent Put/Take")
    void testBlockingQueue_ConcurrentPutTake() throws InterruptedException {
        CustomBlockingQueue<Integer> queue = new CustomBlockingQueue<>(10);
        int numItems = 100;
        CountDownLatch latch = new CountDownLatch(2);
        List<Integer> taken = new CopyOnWriteArrayList<>();

        // Producer thread
        new Thread(() -> {
            try {
                for (int i = 0; i < numItems; i++) {
                    queue.put(i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
        }).start();

        // Consumer thread
        new Thread(() -> {
            try {
                for (int i = 0; i < numItems; i++) {
                    taken.add(queue.take());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
        }).start();

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertEquals(numItems, taken.size());
    }

    @Test
    @Order(14)
    @DisplayName("Problem 8: Read-Write Cache - Basic Operations")
    void testReadWriteCache_BasicOperations() {
        ReadWriteCache<String, Integer> cache = new ReadWriteCache<>();

        assertNull(cache.get("key1"));
        cache.put("key1", 100);
        assertEquals(100, cache.get("key1"));
        cache.remove("key1");
        assertNull(cache.get("key1"));
    }

    @Test
    @Order(15)
    @DisplayName("Problem 8: Read-Write Cache - Concurrent Reads")
    void testReadWriteCache_ConcurrentReads() throws InterruptedException {
        ReadWriteCache<String, Integer> cache = new ReadWriteCache<>();
        cache.put("shared", 42);

        int numReaders = 10;
        CountDownLatch latch = new CountDownLatch(numReaders);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < numReaders; i++) {
            new Thread(() -> {
                Integer value = cache.get("shared");
                if (value != null && value == 42) {
                    successCount.incrementAndGet();
                }
                latch.countDown();
            }).start();
        }

        assertTrue(latch.await(2, TimeUnit.SECONDS));
        assertEquals(numReaders, successCount.get());
    }

    @Test
    @Order(16)
    @DisplayName("Problem 9: Dining Philosophers - No Deadlock")
    void testDiningPhilosophers_NoDeadlock() throws InterruptedException {
        DiningPhilosophers dp = new DiningPhilosophers(5);
        CountDownLatch latch = new CountDownLatch(5);
        AtomicInteger eatCount = new AtomicInteger(0);

        for (int i = 0; i < 5; i++) {
            final int philosopherId = i;
            new Thread(() -> {
                try {
                    dp.wantsToEat(
                        philosopherId,
                        () -> {}, // pickLeftFork
                        () -> {}, // pickRightFork
                        () -> eatCount.incrementAndGet(), // eat
                        () -> {}, // putLeftFork
                        () -> {}  // putRightFork
                    );
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertEquals(5, eatCount.get());
    }

    @Test
    @Order(17)
    @DisplayName("Problem 10: Concurrent HashMap - Basic Operations")
    void testConcurrentHashMap_BasicOperations() {
        SimpleConcurrentHashMap<String, Integer> map = new SimpleConcurrentHashMap<>();

        assertNull(map.get("key1"));
        map.put("key1", 100);
        assertEquals(100, map.get("key1"));
        map.remove("key1");
        assertNull(map.get("key1"));
    }

    @Test
    @Order(18)
    @DisplayName("Problem 10: Concurrent HashMap - Concurrent Puts")
    void testConcurrentHashMap_ConcurrentPuts() throws InterruptedException {
        SimpleConcurrentHashMap<Integer, Integer> map = new SimpleConcurrentHashMap<>();
        int numThreads = 10;
        int itemsPerThread = 100;
        CountDownLatch latch = new CountDownLatch(numThreads);

        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            new Thread(() -> {
                for (int j = 0; j < itemsPerThread; j++) {
                    map.put(threadId * 1000 + j, j);
                }
                latch.countDown();
            }).start();
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS));

        // Verify all entries are present
        for (int i = 0; i < numThreads; i++) {
            for (int j = 0; j < itemsPerThread; j++) {
                Integer value = map.get(i * 1000 + j);
                assertNotNull(value);
                assertEquals(j, value);
            }
        }
    }

    // ============================================================================
    // ADVANCED LEVEL TESTS (Problems 11-15)
    // ============================================================================

    @Test
    @Order(19)
    @DisplayName("Problem 11: Parallel Merge Sort - Small Array")
    void testParallelMergeSort_SmallArray() {
        int[] array = {5, 2, 8, 1, 9, 3, 7};
        ParallelMergeSort.sort(array);

        assertArrayEquals(new int[]{1, 2, 3, 5, 7, 8, 9}, array);
    }

    @Test
    @Order(20)
    @DisplayName("Problem 11: Parallel Merge Sort - Large Array")
    void testParallelMergeSort_LargeArray() {
        int[] array = new int[10000];
        Random rand = new Random(42);
        for (int i = 0; i < array.length; i++) {
            array[i] = rand.nextInt(10000);
        }

        ParallelMergeSort.sort(array);

        for (int i = 1; i < array.length; i++) {
            assertTrue(array[i - 1] <= array[i], "Array should be sorted");
        }
    }

    @Test
    @Order(21)
    @DisplayName("Problem 12: CompletableFuture Pipeline - Success Case")
    void testCompletableFuture_SuccessCase() throws Exception {
        DataPipeline pipeline = new DataPipeline();
        String result = pipeline.processData("input").get(2, TimeUnit.SECONDS);

        assertNotNull(result);
        assertTrue(result.contains("Enriched"));
    }

    @Test
    @Order(22)
    @DisplayName("Problem 12: CompletableFuture Pipeline - Error Handling")
    void testCompletableFuture_ErrorHandling() throws Exception {
        DataPipeline pipeline = new DataPipeline();
        String result = pipeline.processData(null).get(2, TimeUnit.SECONDS);

        assertNotNull(result);
        assertTrue(result.contains("Error") || result.contains("Enriched"));
    }

    @Test
    @Order(23)
    @DisplayName("Problem 13: Web Crawler - Basic Crawl")
    void testWebCrawler_BasicCrawl() throws InterruptedException {
        WebCrawler crawler = new WebCrawler(2, 2);
        Set<String> urls = crawler.crawl("http://example.com");

        assertNotNull(urls);
        assertTrue(urls.contains("http://example.com"));
        assertTrue(urls.size() > 1, "Should have crawled multiple pages");
    }

    @Test
    @Order(24)
    @DisplayName("Problem 14: Lock-Free Stack - Basic Push/Pop")
    void testLockFreeStack_BasicPushPop() {
        LockFreeStack<Integer> stack = new LockFreeStack<>();

        assertTrue(stack.isEmpty());
        stack.push(1);
        stack.push(2);
        stack.push(3);

        assertEquals(3, stack.pop());
        assertEquals(2, stack.pop());
        assertEquals(1, stack.pop());
        assertTrue(stack.isEmpty());
    }

    @Test
    @Order(25)
    @DisplayName("Problem 14: Lock-Free Stack - Concurrent Push/Pop")
    void testLockFreeStack_ConcurrentPushPop() throws InterruptedException {
        LockFreeStack<Integer> stack = new LockFreeStack<>();
        int numThreads = 10;
        int itemsPerThread = 100;
        CountDownLatch latch = new CountDownLatch(numThreads * 2);

        // Push threads
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            new Thread(() -> {
                for (int j = 0; j < itemsPerThread; j++) {
                    stack.push(threadId * 1000 + j);
                }
                latch.countDown();
            }).start();
        }

        // Pop threads
        List<Integer> popped = new CopyOnWriteArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            new Thread(() -> {
                for (int j = 0; j < itemsPerThread; j++) {
                    Integer value = stack.pop();
                    if (value != null) {
                        popped.add(value);
                    }
                }
                latch.countDown();
            }).start();
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertEquals(numThreads * itemsPerThread, popped.size());
    }

    @Test
    @Order(26)
    @DisplayName("Problem 15: Striped Counter - Basic Increment")
    void testStripedCounter_BasicIncrement() {
        StripedCounter counter = new StripedCounter();

        assertEquals(0, counter.sum());
        counter.increment();
        counter.increment();
        counter.increment();
        assertEquals(3, counter.sum());
    }

    @Test
    @Order(27)
    @DisplayName("Problem 15: Striped Counter - Concurrent Increments")
    void testStripedCounter_ConcurrentIncrements() throws InterruptedException {
        StripedCounter counter = new StripedCounter();
        int numThreads = 20;
        int incrementsPerThread = 1000;
        CountDownLatch latch = new CountDownLatch(numThreads);

        for (int i = 0; i < numThreads; i++) {
            new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counter.increment();
                }
                latch.countDown();
            }).start();
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertEquals(numThreads * incrementsPerThread, counter.sum());
    }

    // ============================================================================
    // PERFORMANCE TESTS
    // ============================================================================

    @Test
    @Order(28)
    @DisplayName("Performance: Rate Limiter - High Load")
    void testPerformance_RateLimiter() {
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(1000, 100);

        long startTime = System.currentTimeMillis();
        int successCount = 0;
        for (int i = 0; i < 1000; i++) {
            if (limiter.tryAcquire()) {
                successCount++;
            }
        }
        long endTime = System.currentTimeMillis();

        assertTrue(successCount > 0);
        assertTrue(endTime - startTime < 5000, "Should complete quickly");

        limiter.shutdown();
    }

    @Test
    @Order(29)
    @DisplayName("Performance: Parallel Merge Sort vs Sequential")
    void testPerformance_ParallelVsSequential() {
        int size = 100000;
        int[] array1 = new int[size];
        int[] array2 = new int[size];
        Random rand = new Random(42);

        for (int i = 0; i < size; i++) {
            int value = rand.nextInt(size);
            array1[i] = value;
            array2[i] = value;
        }

        // Parallel sort
        long start = System.nanoTime();
        ParallelMergeSort.sort(array1);
        long parallelTime = System.nanoTime() - start;

        // Sequential sort
        start = System.nanoTime();
        Arrays.sort(array2);
        long sequentialTime = System.nanoTime() - start;

        System.out.println("Parallel: " + parallelTime / 1_000_000 + "ms");
        System.out.println("Sequential: " + sequentialTime / 1_000_000 + "ms");

        assertArrayEquals(array1, array2, "Both should produce same sorted array");
    }

    @Test
    @Order(30)
    @DisplayName("Integration: Multiple Patterns Combined")
    void testIntegration_CombinedPatterns() throws InterruptedException {
        // Use multiple concurrency patterns together
        ProducerConsumerQueue<Integer> queue = new ProducerConsumerQueue<>(10);
        ThreadSafeCounter counter = new ThreadSafeCounter();
        ReadWriteCache<String, Integer> cache = new ReadWriteCache<>();

        CountDownLatch latch = new CountDownLatch(3);

        // Producer
        new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    queue.produce(i);
                    counter.increment();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
        }).start();

        // Consumer
        new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    Integer value = queue.consume();
                    cache.put("key" + value, value);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
        }).start();

        // Reader
        new Thread(() -> {
            try {
                Thread.sleep(500);
                for (int i = 0; i < 10; i++) {
                    cache.get("key" + i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
        }).start();

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertEquals(10, counter.get());
        assertEquals(10, cache.size());
    }
}
