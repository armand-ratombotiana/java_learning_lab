package com.learning.lab16;

import org.junit.jupiter.api.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

class ConcurrencyTest {

    @Test
    @DisplayName("SyncExample Counter is thread-safe with synchronized")
    void synchronizedCounter() throws InterruptedException {
        Counter counter = new Counter();
        Thread t1 = new Thread(() -> { for (int i = 0; i < 1000; i++) counter.increment(); });
        Thread t2 = new Thread(() -> { for (int i = 0; i < 1000; i++) counter.increment(); });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        assertEquals(2000, counter.getCount());
    }

    @Test
    @DisplayName("LockCounter is thread-safe with ReentrantLock")
    void lockCounter() throws InterruptedException {
        LockCounter lockCounter = new LockCounter();
        Thread t1 = new Thread(() -> { for (int i = 0; i < 500; i++) lockCounter.increment(); });
        Thread t2 = new Thread(() -> { for (int i = 0; i < 500; i++) lockCounter.increment(); });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        assertEquals(1000, lockCounter.getCount());
    }

    @Test
    @DisplayName("CompletableFuture thenCombine works")
    void completableFutureThenCombine() throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello")
            .thenCombine(CompletableFuture.supplyAsync(() -> "World"), (h, w) -> h + " " + w);
        assertEquals("Hello World", future.get(1, TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("CompletableFuture thenApply pipeline")
    void completableFuturePipeline() throws Exception {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> 42)
            .thenApply(n -> n * 2)
            .thenApply(n -> n + 1);
        assertEquals(85, future.get(1, TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("CompletableFuture exceptionally handles errors")
    void completableFutureErrorHandling() throws Exception {
        CompletableFuture<Integer> future = CompletableFuture
            .supplyAsync(() -> { throw new RuntimeException("fail"); })
            .exceptionally(ex -> -1);
        assertEquals(-1, future.get(1, TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("ExecutorService submits and executes tasks")
    void executorServiceSubmit() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(() -> "done");
        assertEquals("done", future.get(1, TimeUnit.SECONDS));
        executor.shutdown();
        assertTrue(executor.awaitTermination(1, TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("Thread creation with Runnable")
    void threadCreationRunnable() throws InterruptedException {
        var result = new String[1];
        Thread t = new Thread(() -> result[0] = "ran", "test-thread");
        t.start();
        t.join(1000);
        assertEquals("ran", result[0]);
    }

    @Test
    @DisplayName("MyThread extends Thread and runs")
    void myThreadExtendsThread() throws InterruptedException {
        MyThread t = new MyThread();
        t.start();
        t.join(1000);
        assertEquals("worker-2", t.getName());
    }
}
