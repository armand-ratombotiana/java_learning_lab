package com.javaacademy.lab42.locking;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.locks.*;

class LockingUltraDeepTest {

    @Test
    void reentrantLockCanLockMultipleTimes() {
        var lock = new ReentrantLock();
        lock.lock();
        assertTrue(lock.isHeldByCurrentThread());
        assertEquals(1, lock.getHoldCount());
        lock.lock();
        assertEquals(2, lock.getHoldCount());
        lock.unlock();
        lock.unlock();
        assertFalse(lock.isHeldByCurrentThread());
    }

    @Test
    void readWriteLockAllowsConcurrentReads() {
        var rwLock = new ReentrantReadWriteLock();
        var readLock = rwLock.readLock();
        var readLock2 = rwLock.readLock();
        readLock.lock();
        readLock2.lock();
        assertEquals(2, rwLock.getReadLockCount());
        readLock.unlock();
        readLock2.unlock();
    }

    @Test
    void tryLockReturnsFalseWhenLockHeld() throws InterruptedException {
        var lock = new ReentrantLock();
        var acquired = new boolean[1];
        var t1 = new Thread(() -> {
            lock.lock();
            try { Thread.sleep(500); } catch (InterruptedException e) { }
            lock.unlock();
        });
        t1.start();
        Thread.sleep(100);
        assertFalse(lock.tryLock());
        t1.join();
    }

    @Test
    void conditionAwaitSignal() throws InterruptedException {
        var lock = new ReentrantLock();
        var condition = lock.newCondition();
        var signaled = new boolean[1];
        var t1 = new Thread(() -> {
            lock.lock();
            try {
                condition.await();
                signaled[0] = true;
            } catch (InterruptedException e) { }
            lock.unlock();
        });
        t1.start();
        Thread.sleep(100);
        lock.lock();
        condition.signal();
        lock.unlock();
        t1.join(2000);
        assertTrue(signaled[0]);
    }

    @Test
    void stampLockOptimisticRead() {
        var lock = new StampedLock();
        long stamp = lock.tryOptimisticRead();
        assertTrue(lock.validate(stamp));
        long writeStamp = lock.writeLock();
        assertFalse(lock.validate(stamp));
        lock.unlockWrite(writeStamp);
    }
}
