package com.learning.concurrency;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Advanced Concurrency Pattern Tests
 * Tests barriers, latches, semaphores, and advanced patterns
 */
@Timeout(30)  // All tests must complete in 30 seconds
public class AdvancedConcurrencyTest {
    
    // ==================== COUNT DOWN LATCH TESTS ====================
    
    @Test
    public void testCountDownLatch_WaitsForAllThreads() throws InterruptedException {
        int threadCount = 5;
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger completedTasks = new AtomicInteger(0);
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    Thread.sleep(100);
                    completedTasks.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        executor.shutdown();
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertEquals(threadCount, completedTasks.get());
    }
    
    @Test
    public void testCountDownLatch_Timeout() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        
        boolean completed = latch.await(100, TimeUnit.MILLISECONDS);
        assertFalse(completed, "Should timeout waiting for latch");
    }
    
    // ==================== CYCLIC BARRIER TESTS ====================
    
    @Test
    public void testCyclicBarrier_SynchronizesThreads() throws InterruptedException, BrokenBarrierException {
        int threadCount = 3;
        CyclicBarrier barrier = new CyclicBarrier(threadCount);
        AtomicInteger barrierReachCount = new AtomicInteger(0);
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    barrier.await();  // Wait for all threads
                    barrierReachCount.incrementAndGet();
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        assertEquals(threadCount, barrierReachCount.get());
    }
    
    @Test
    public void testCyclicBarrier_Reusable() throws InterruptedException, BrokenBarrierException {
        int threadCount = 2;
        int rounds = 3;
        CyclicBarrier barrier = new CyclicBarrier(threadCount);
        AtomicInteger passCount = new AtomicInteger(0);
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int round = 0; round < rounds; round++) {
                        barrier.await();
                        passCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        assertEquals(threadCount * rounds, passCount.get());
    }
    
    // ==================== SEMAPHORE TESTS ====================
    
    @Test
    public void testSemaphore_LimitsAccess() throws InterruptedException {
        int permits = 2;
        Semaphore semaphore = new Semaphore(permits);
        AtomicInteger activeCount = new AtomicInteger(0);
        AtomicInteger maxConcurrent = new AtomicInteger(0);
        
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            executor.submit(() -> {
                try {
                    semaphore.acquire();
                    int active = activeCount.incrementAndGet();
                    maxConcurrent.set(Math.max(maxConcurrent.get(), active));
                    
                    Thread.sleep(100);
                    activeCount.decrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    semaphore.release();
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        assertTrue(maxConcurrent.get() <= permits, "Should never exceed permits");
    }
    
    @Test
    public void testSemaphore_TryAcquire() throws InterruptedException {
        Semaphore semaphore = new Semaphore(1);
        
        assertTrue(semaphore.tryAcquire());
        assertFalse(semaphore.tryAcquire());  // No permits left
        
        semaphore.release();
        assertTrue(semaphore.tryAcquire());  // Permit released and reacquired
    }
    
    // ==================== BLOCKING QUEUE TESTS ====================
    
    @Test
    public void testBlockingQueue_ProducerConsumer() throws InterruptedException {
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(5);
        AtomicInteger producedCount = new AtomicInteger(0);
        AtomicInteger consumedCount = new AtomicInteger(0);
        
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    queue.put(i);
                    producedCount.incrementAndGet();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    queue.take();
                    consumedCount.incrementAndGet();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
        
        assertEquals(10, producedCount.get());
        assertEquals(10, consumedCount.get());
    }
    
    @Test
    public void testBlockingQueue_PollTimeout() throws InterruptedException {
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
        
        Integer value = queue.poll(100, TimeUnit.MILLISECONDS);
        assertNull(value, "Should timeout and return null");
    }
    
    // ==================== COPY ON WRITE LIST TESTS ====================
    
    @Test
    public void testCopyOnWriteArrayList_ThreadSafe() throws InterruptedException {
        CopyOnWriteArrayList<Integer> list = new CopyOnWriteArrayList<>();
        int threadCount = 10;
        int elementsPerThread = 100;
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < elementsPerThread; j++) {
                    list.add(j);
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        assertEquals(threadCount * elementsPerThread, list.size());
    }
    
    // ==================== CONCURRENT HASH MAP TESTS ====================
    
    @Test
    public void testConcurrentHashMap_ThreadSafe() throws InterruptedException {
        ConcurrentHashMap<Integer, Integer> map = new ConcurrentHashMap<>();
        int threadCount = 10;
        int operationsPerThread = 100;
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    int key = threadId * operationsPerThread + j;
                    map.put(key, j);
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        assertEquals(threadCount * operationsPerThread, map.size());
    }
    
    @Test
    public void testConcurrentHashMap_ComputeIfAbsent() throws InterruptedException {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        
        map.putIfAbsent("count", 0);
        map.computeIfPresent("count", (k, v) -> v + 1);
        
        assertEquals(1, map.get("count"));
    }
    
    // ==================== COMPLETABLE FUTURE TESTS ====================
    
    @Test
    public void testCompletableFuture_Async() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(100);
                return 42;
            } catch (InterruptedException e) {
                return 0;
            }
        });
        
        Integer result = future.get();
        assertEquals(42, result);
    }
    
    @Test
    public void testCompletableFuture_ThenApply() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> 10)
            .thenApply(x -> x * 2)
            .thenApply(x -> x + 5);
        
        Integer result = future.get();
        assertEquals(25, result);
    }
    
    @Test
    public void testCompletableFuture_ThenCompose() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> 5)
            .thenCompose(x -> CompletableFuture.supplyAsync(() -> x * 2));
        
        Integer result = future.get();
        assertEquals(10, result);
    }
    
    // ==================== CONDITION VARIABLE TESTS ====================
    
    @Test
    public void testCondition_ProducerConsumer() throws InterruptedException {
        AdvancedConcurrencyDemo.ConditionBoundedBuffer buffer = 
            new AdvancedConcurrencyDemo.ConditionBoundedBuffer(5);
        
        AtomicInteger produced = new AtomicInteger(0);
        AtomicInteger consumed = new AtomicInteger(0);
        
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    buffer.put(i);
                    produced.incrementAndGet();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    buffer.get();
                    consumed.incrementAndGet();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
        
        assertEquals(10, produced.get());
        assertEquals(10, consumed.get());
    }
    
    // ==================== STRESS TESTS ====================
    
    @Test
    public void testConcurrentModification_StressTest() throws InterruptedException {
        ConcurrentHashMap<Integer, Integer> map = new ConcurrentHashMap<>();
        int threadCount = 20;
        int operationsPerThread = 1000;
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    map.put(threadId * operationsPerThread + j, j);
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
        long duration = System.currentTimeMillis() - startTime;
        
        assertEquals(threadCount * operationsPerThread, map.size());
        System.out.println("Stress test completed in " + duration + "ms");
    }
    
    @Test
    public void testConcurrentList_StressTest() throws InterruptedException {
        CopyOnWriteArrayList<Integer> list = new CopyOnWriteArrayList<>();
        int threadCount = 10;
        int operationsPerThread = 500;
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    list.add(j);
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
        
        assertEquals(threadCount * operationsPerThread, list.size());
    }
}
