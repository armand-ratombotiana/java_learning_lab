package com.javalab.02;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.LongAdder;

public final class ComputeIfAbsentExample {

    private static final int THREADS = 16;
    private static final int ITERATIONS = 10_000;

    private ComputeIfAbsentExample() {
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Safe: computeIfAbsent ===");
        runSafe();

        System.out.println("\n=== Unsafe: check-then-act ===");
        runUnsafe();
    }

    private static void runSafe() throws InterruptedException {
        ConcurrentHashMap<String, LongAdder> map = new ConcurrentHashMap<>();
        CountDownLatch latch = new CountDownLatch(THREADS);
        ExecutorService exec = Executors.newFixedThreadPool(THREADS);

        for (int t = 0; t < THREADS; t++) {
            exec.submit(() -> {
                try {
                    for (int i = 0; i < ITERATIONS; i++) {
                        LongAdder counter = map.computeIfAbsent("key-" + (i % 100),
                                k -> new LongAdder());
                        counter.increment();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        exec.shutdown();

        long total = map.values().stream().mapToLong(LongAdder::sum).sum();
        long expected = (long) THREADS * ITERATIONS;
        System.out.printf("Expected total: %d, Actual total: %d, Match: %b%n",
                expected, total, total == expected);
    }

    private static void runUnsafe() throws InterruptedException {
        ConcurrentHashMap<String, LongAdder> map = new ConcurrentHashMap<>();
        CountDownLatch latch = new CountDownLatch(THREADS);
        ExecutorService exec = Executors.newFixedThreadPool(THREADS);

        for (int t = 0; t < THREADS; t++) {
            exec.submit(() -> {
                try {
                    for (int i = 0; i < ITERATIONS; i++) {
                        String key = "key-" + (i % 100);
                        LongAdder counter = map.get(key);
                        if (counter == null) {
                            counter = new LongAdder();
                            LongAdder existing = map.putIfAbsent(key, counter);
                            if (existing != null) {
                                counter = existing;
                            }
                        }
                        counter.increment();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        exec.shutdown();

        long total = map.values().stream().mapToLong(LongAdder::sum).sum();
        long expected = (long) THREADS * ITERATIONS;
        System.out.printf("Expected total: %d, Actual total: %d, Match: %b%n",
                expected, total, total == expected);
    }
}
