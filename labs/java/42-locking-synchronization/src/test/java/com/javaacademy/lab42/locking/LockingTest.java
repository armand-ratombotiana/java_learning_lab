package com.javaacademy.lab42.locking;

import org.junit.jupiter.api.*;

import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;

class LockingTest {

    @Test
    void testAqsLockExclusion() throws Exception {
        AqsLock lock = new AqsLock();
        int[] shared = {0};
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    lock.lock();
                    try { shared[0]++; } finally { lock.unlock(); }
                }
            });
        }
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
        assertEquals(5000, shared[0]);
    }

    @Test
    void testReentrantLockFairness() throws Exception {
        ReentrantLock lock = new ReentrantLock(true);
        assertTrue(lock.isFair());
        lock.lock();
        lock.lock(); // reentrant
        assertEquals(2, lock.getHoldCount());
        lock.unlock();
        lock.unlock();
    }

    @Test
    void testCasCounter() throws Exception {
        CasCounter counter = new CasCounter(0);
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 500; j++) counter.incrementAndGet();
            });
        }
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
        assertEquals(2500, counter.get());
    }

    @Test
    void testLockSupport() {
        Thread t = new Thread(() -> {
            try {
                LockSupportDemo.main(new String[]{});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        t.start();
        try { t.join(3000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        assertFalse(t.isAlive());
    }
}
