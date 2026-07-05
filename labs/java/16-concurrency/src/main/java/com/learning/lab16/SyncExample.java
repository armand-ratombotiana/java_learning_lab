package com.learning.lab16;

import java.util.concurrent.locks.*;

/**
 * Demonstrates synchronized blocks and ReentrantLock for thread safety.
 */
public class SyncExample {

    public static void showSynchronization() throws InterruptedException {
        System.out.println("=== Synchronization ===");

        Counter counter = new Counter();
        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment();
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Synchronized counter: " + counter.getCount());

        LockCounter lockCounter = new LockCounter();
        Runnable lockTask = () -> {
            for (int i = 0; i < 1000; i++) {
                lockCounter.increment();
            }
        };

        Thread t3 = new Thread(lockTask);
        Thread t4 = new Thread(lockTask);
        t3.start();
        t4.start();
        t3.join();
        t4.join();

        System.out.println("ReentrantLock counter: " + lockCounter.getCount());
    }
}

class Counter {
    private int count = 0;

    public synchronized void increment() {
        count++;
    }

    public synchronized int getCount() {
        return count;
    }
}

class LockCounter {
    private final Lock lock = new ReentrantLock();
    private int count = 0;

    public void increment() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }

    public int getCount() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }
}
