package com.javaacademy.lab42.locking;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * Custom exclusive lock built on AbstractQueuedSynchronizer (AQS).
 * Demonstrates how ReentrantLock works internally using a CLH queue.
 */
public class AqsLock {

    private final Sync sync = new Sync();

    private static class Sync extends AbstractQueuedSynchronizer {
        @Override
        protected boolean tryAcquire(int acquires) {
            if (compareAndSetState(0, acquires)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int releases) {
            if (getState() == 0) throw new IllegalMonitorStateException();
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        @Override
        protected boolean isHeldExclusively() {
            return getState() != 0 && getExclusiveOwnerThread() == Thread.currentThread();
        }
    }

    public void lock() { sync.acquire(1); }
    public void unlock() { sync.release(1); }
    public boolean tryLock() { return sync.tryAcquire(1); }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Custom AQS-based Lock ===\n");
        AqsLock lock = new AqsLock();
        lock.lock();
        try {
            System.out.println("Critical section protected by AQS lock");
        } finally {
            lock.unlock();
        }
    }
}
