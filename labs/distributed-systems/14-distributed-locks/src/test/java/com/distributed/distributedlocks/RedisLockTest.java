package com.distributed.distributedlocks;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

class RedisLockTest {

    @Test
    void testLockAndUnlock() {
        RedisLock lock = new RedisLock();
        assertTrue(lock.tryLock("test", Duration.ofSeconds(10)));
        assertFalse(lock.tryLock("test", Duration.ofSeconds(1)));
        lock.unlock("test");
        assertTrue(lock.tryLock("test", Duration.ofSeconds(10)));
    }

    @Test
    void testConcurrentLocking() throws InterruptedException {
        RedisLock lock = new RedisLock();
        int threads = 10;
        ExecutorService exec = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        ConcurrentHashMap<String, Boolean> results = new ConcurrentHashMap<>();

        for (int i = 0; i < threads; i++) {
            exec.submit(() -> {
                boolean acquired = lock.tryLock("shared", Duration.ofSeconds(5));
                results.put(Thread.currentThread().getName(), acquired);
                latch.countDown();
            });
        }
        latch.await(10, TimeUnit.SECONDS);
        long acquiredCount = results.values().stream().filter(v -> v).count();
        assertEquals(1, acquiredCount);
        exec.shutdown();
    }

    @Test
    void testFencingTokenIncrement() {
        RedisLock lock = new RedisLock();
        assertTrue(lock.tryLock("a", Duration.ofSeconds(10)));
        long t1 = lock.getFencingToken("a");
        lock.unlock("a");
        assertTrue(lock.tryLock("a", Duration.ofSeconds(10)));
        long t2 = lock.getFencingToken("a");
        assertTrue(t2 > t1);
    }

    @Test
    void testLeaseExpiry() throws InterruptedException {
        RedisLock lock = new RedisLock();
        assertTrue(lock.tryLock("exp", Duration.ofMillis(50)));
        Thread.sleep(100);
        assertFalse(lock.isLocked("exp"));
        assertTrue(lock.tryLock("exp", Duration.ofSeconds(10)));
    }
}
