package com.javaacademy.lab52.performanceantipatterns;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Demonstrates contention on heavily accessed fields.
 * Compares synchronized, ReentrantLock, and AtomicLong throughput
 * under high thread contention to show performance degradation
 * from excessive synchronization.
 */
public class ContentionExample {

    private static final int THREADS = 8;
    private static final int ITERATIONS = 5_000_000;

    private long syncCounter = 0;
    private final ReentrantLock reentrantLock = new ReentrantLock();
    private long lockCounter = 0;
    private final AtomicLong atomicCounter = new AtomicLong(0);

    public static void main(String[] args) throws Exception {
        var demo = new ContentionExample();

        System.out.println("=== Contention Benchmark ===");
        System.out.println("Threads: " + THREADS + ", iterations: " + ITERATIONS);

        long syncTime = demo.benchmarkSync();
        long lockTime = demo.benchmarkLock();
        long atomicTime = demo.benchmarkAtomic();

        System.out.println("synchronized: " + syncTime + " ms");
        System.out.println("ReentrantLock: " + lockTime + " ms");
        System.out.println("AtomicLong: " + atomicTime + " ms");
    }

    long benchmarkSync() throws Exception {
        long start = System.nanoTime();
        Thread[] threads = new Thread[THREADS];
        for (int t = 0; t < THREADS; t++) {
            threads[t] = new Thread(() -> {
                for (int i = 0; i < ITERATIONS; i++) {
                    incrementSync();
                }
            }, "sync-thread-" + t);
            threads[t].start();
        }
        for (Thread t : threads) t.join();
        return (System.nanoTime() - start) / 1_000_000;
    }

    synchronized void incrementSync() { syncCounter++; }

    long benchmarkLock() throws Exception {
        long start = System.nanoTime();
        Thread[] threads = new Thread[THREADS];
        for (int t = 0; t < THREADS; t++) {
            threads[t] = new Thread(() -> {
                for (int i = 0; i < ITERATIONS; i++) {
                    reentrantLock.lock();
                    try { lockCounter++; } finally { reentrantLock.unlock(); }
                }
            }, "lock-thread-" + t);
            threads[t].start();
        }
        for (Thread t : threads) t.join();
        return (System.nanoTime() - start) / 1_000_000;
    }

    long benchmarkAtomic() throws Exception {
        long start = System.nanoTime();
        Thread[] threads = new Thread[THREADS];
        for (int t = 0; t < THREADS; t++) {
            threads[t] = new Thread(() -> {
                for (int i = 0; i < ITERATIONS; i++) {
                    atomicCounter.incrementAndGet();
                }
            }, "atomic-thread-" + t);
            threads[t].start();
        }
        for (Thread t : threads) t.join();
        return (System.nanoTime() - start) / 1_000_000;
    }
}
