package com.javaacademy.lab51.advancedconcurrency;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

class AdvancedConcurrencyUltraDeepTest {

    @Test
    void atomicIntegerIncrement() {
        AtomicInteger counter = new AtomicInteger(0);
        assertEquals(1, counter.incrementAndGet());
        assertEquals(2, counter.incrementAndGet());
    }

    @Test
    void atomicIntegerCompareAndSet() {
        AtomicInteger ai = new AtomicInteger(10);
        assertTrue(ai.compareAndSet(10, 20));
        assertEquals(20, ai.get());
        assertFalse(ai.compareAndSet(10, 30));
    }

    @Test
    void atomicLongAccumulate() {
        var accumulator = new LongAccumulator(Long::max, 0);
        accumulator.accumulate(10);
        accumulator.accumulate(5);
        accumulator.accumulate(20);
        assertEquals(20, accumulator.get());
    }

    @Test
    void atomicLongAdderSum() {
        var adder = new LongAdder();
        adder.add(10);
        adder.add(20);
        adder.add(30);
        assertEquals(60, adder.sum());
    }

    @Test
    void completableFutureThenCompose() throws Exception {
        CompletableFuture<String> future = CompletableFuture
            .supplyAsync(() -> "hello")
            .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + " world"));
        assertEquals("hello world", future.get(1, TimeUnit.SECONDS));
    }

    @Test
    void countDownLatchCoordinatesThreads() throws InterruptedException {
        var latch = new CountDownLatch(3);
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                latch.countDown();
            }).start();
        }
        assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    void cyclicBarrierSyncPoint() throws InterruptedException {
        var barrier = new CyclicBarrier(2);
        var passed = new boolean[1];
        var t1 = new Thread(() -> {
            try { barrier.await(); passed[0] = true; } catch (Exception e) { }
        });
        t1.start();
        Thread.sleep(100);
        assertFalse(passed[0]);
        barrier.reset();
        t1.join(1000);
    }

    @Test
    void semaphoreLimitsConcurrency() throws InterruptedException {
        var semaphore = new Semaphore(1);
        assertTrue(semaphore.tryAcquire());
        assertFalse(semaphore.tryAcquire());
        semaphore.release();
        assertTrue(semaphore.tryAcquire());
        semaphore.release();
    }
}
