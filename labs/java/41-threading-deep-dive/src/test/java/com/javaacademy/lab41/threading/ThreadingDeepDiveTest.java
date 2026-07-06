package com.javaacademy.lab41.threading;

import org.junit.jupiter.api.*;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class ThreadingDeepDiveTest {

    @Test
    void testThreadLifecycleStates() {
        Thread t = new Thread(() -> {});
        assertEquals(Thread.State.NEW, t.getState());
        t.start();
        try { t.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        assertEquals(Thread.State.TERMINATED, t.getState());
    }

    @Test
    void testCompletableFutureThenApply() throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> 10)
            .thenApply(n -> n * 3)
            .thenApply(n -> "Value: " + n);
        assertEquals("Value: 30", future.get());
    }

    @Test
    void testCompletableFutureAllOf() throws Exception {
        CompletableFuture<String> f1 = CompletableFuture.completedFuture("A");
        CompletableFuture<String> f2 = CompletableFuture.completedFuture("B");
        CompletableFuture<Void> all = CompletableFuture.allOf(f1, f2);
        assertNull(all.join());
    }

    @Test
    void testForkJoinSum() {
        int[] data = new int[50_000];
        for (int i = 0; i < data.length; i++) data[i] = i + 1;
        ForkJoinPoolDemo.SumTask task = new ForkJoinPoolDemo.SumTask(data, 0, data.length);
        long result = ForkJoinPool.commonPool().invoke(task);
        assertEquals((long) data.length * (data.length + 1) / 2, result);
    }

    @Test
    void testRejectionHandler() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            1, 1, 0, TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            new ThreadPoolExecutor.AbortPolicy()
        );
        executor.submit(() -> { try { Thread.sleep(500); } catch (InterruptedException e) { } });
        assertThrows(RejectedExecutionException.class, () ->
            executor.submit(() -> System.out.println("should reject"))
        );
        executor.shutdownNow();
    }
}
