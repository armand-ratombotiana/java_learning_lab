package com.javaacademy.lab42.locking;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Demonstrates ReentrantLock features: fair vs unfair, tryLock, lockInterruptibly.
 * Shows how the internal AQS-based fair queue differs from the default unfair policy.
 */
public class ReentrantLockDemo {

    private static int counter = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("=== ReentrantLock: Fair vs Unfair ===\n");

        // Unfair lock demo
        ReentrantLock unfairLock = new ReentrantLock(false);
        runContention(unfairLock, "Unfair");

        // Fair lock demo
        ReentrantLock fairLock = new ReentrantLock(true);
        runContention(fairLock, "Fair");

        // tryLock demo
        ReentrantLock lock = new ReentrantLock();
        boolean acquired = lock.tryLock();
        System.out.println("tryLock acquired: " + acquired);
        if (acquired) lock.unlock();

        // lockInterruptibly demo
        Thread t = new Thread(() -> {
            lock.lock();
            try {
                Thread.currentThread().interrupt(); // simulate interruption
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                System.out.println("Interrupted during lockInterruptibly");
            } finally {
                if (lock.isHeldByCurrentThread()) lock.unlock();
            }
        });
        t.start();
        t.join();
    }

    static void runContention(ReentrantLock lock, String label) throws Exception {
        counter = 0;
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    lock.lock();
                    try { counter++; } finally { lock.unlock(); }
                }
            });
        }
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
        System.out.println(label + " lock counter: " + counter + " (expected: 5000)");
    }
}
