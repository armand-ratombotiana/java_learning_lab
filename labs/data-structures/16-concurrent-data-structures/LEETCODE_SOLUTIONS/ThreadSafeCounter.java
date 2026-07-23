package com.leetcode.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Custom: Thread-Safe Counter
 * Demonstrate thread-safe increment/decrement using AtomicInteger.
 *
 * Time Complexity: O(1)
 * Space Complexity: O(1)
 */
public class ThreadSafeCounter {

    private final AtomicInteger count = new AtomicInteger(0);

    public int increment() {
        return count.incrementAndGet();
    }

    public int decrement() {
        return count.decrementAndGet();
    }

    public int getValue() {
        return count.get();
    }

    /**
     * Approach 2: Synchronized block (alternative to AtomicInteger)
     */
    private int syncCount = 0;

    public synchronized int incrementSync() {
        return ++syncCount;
    }

    public synchronized int decrementSync() {
        return --syncCount;
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadSafeCounter counter = new ThreadSafeCounter();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) counter.increment();
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) counter.increment();
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Final count (expected 2000): " + counter.getValue());
    }
}
