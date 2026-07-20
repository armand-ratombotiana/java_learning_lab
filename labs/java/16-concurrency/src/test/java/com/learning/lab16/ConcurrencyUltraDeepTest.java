package com.learning.lab16;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.*;

class ConcurrencyUltraDeepTest {

    @Test
    void completableFutureAllOfJoinsMultiple() throws Exception {
        CompletableFuture<String> f1 = CompletableFuture.completedFuture("A");
        CompletableFuture<String> f2 = CompletableFuture.completedFuture("B");
        CompletableFuture<Void> all = CompletableFuture.allOf(f1, f2);
        assertNull(all.get(1, TimeUnit.SECONDS));
    }

    @Test
    void completableFutureAnyOfReturnsFirst() throws Exception {
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(200); } catch (InterruptedException e) { }
            return "slow";
        });
        CompletableFuture<String> f2 = CompletableFuture.completedFuture("fast");
        Object result = CompletableFuture.anyOf(f1, f2).get(1, TimeUnit.SECONDS);
        assertEquals("fast", result);
    }

    @Test
    void executorServiceInvokeAll() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Callable<Integer> task1 = () -> 1;
        Callable<Integer> task2 = () -> 2;
        var results = executor.invokeAll(List.of(task1, task2));
        assertEquals(2, results.size());
        assertEquals(1, results.get(0).get());
        assertEquals(2, results.get(1).get());
        executor.shutdown();
    }

    @Test
    void threadInterruptionFlag() throws InterruptedException {
        Thread t = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) { }
        });
        t.start();
        t.interrupt();
        t.join(2000);
        assertFalse(t.isAlive());
    }

    @Test
    void newThreadPoolWithRejectionHandler() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            1, 1, 0, TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            new ThreadPoolExecutor.AbortPolicy()
        );
        executor.submit(() -> { try { Thread.sleep(500); } catch (InterruptedException e) { } });
        assertThrows(RejectedExecutionException.class, () ->
            executor.submit(() -> {})
        );
        executor.shutdownNow();
    }
}
