package com.javaacademy.lab47.profiling;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Contended lock workload that demonstrates lock profiling.
 * Multiple threads compete for the same lock; profile with
 * async-profiler --lock mode or JFR lock events.
 */
public class LockProfilingTarget {

    private static final int THREAD_COUNT = 8;
    private static final int ITERATIONS = 500_000;
    private static final Lock lock = new ReentrantLock();
    private static int sharedCounter = 0;

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[THREAD_COUNT];
        for (int t = 0; t < THREAD_COUNT; t++) {
            threads[t] = new Thread(() -> {
                for (int i = 0; i < ITERATIONS; i++) {
                    lock.lock();
                    try {
                        sharedCounter++;
                        // Simulate short critical section work
                        int dummy = sharedCounter * sharedCounter;
                    } finally {
                        lock.unlock();
                    }
                }
            }, "Worker-" + t);
            threads[t].start();
        }
        for (Thread t : threads) {
            t.join();
        }
        System.out.println("Final counter: " + sharedCounter);
    }
}
