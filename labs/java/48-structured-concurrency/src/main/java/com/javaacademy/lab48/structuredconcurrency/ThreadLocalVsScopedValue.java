package com.javaacademy.lab48.structuredconcurrency;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Benchmarks ThreadLocal vs ScopedValue to demonstrate allocation
 * and lookup performance differences in virtual thread scenarios.
 */
public class ThreadLocalVsScopedValue {

    private static final ThreadLocal<Integer> TL_COUNTER = ThreadLocal.withInitial(() -> 0);
    private static final ScopedValue<Integer> SV_COUNTER = ScopedValue.newInstance();
    private static final int TASKS = 100_000;

    public static void main(String[] args) throws Exception {
        long tlTime = benchmarkThreadLocal();
        long svTime = benchmarkScopedValue();
        System.out.println("ThreadLocal time: " + tlTime + " ms");
        System.out.println("ScopedValue time: " + svTime + " ms");
        System.out.println("Ratio: " + (double) tlTime / svTime);
    }

    static long benchmarkThreadLocal() throws Exception {
        AtomicInteger sink = new AtomicInteger();
        long start = System.currentTimeMillis();
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < TASKS; i++) {
                pool.submit(() -> {
                    TL_COUNTER.set(42);
                    sink.addAndGet(TL_COUNTER.get());
                });
            }
        }
        return System.currentTimeMillis() - start;
    }

    static long benchmarkScopedValue() throws Exception {
        AtomicInteger sink = new AtomicInteger();
        long start = System.currentTimeMillis();
        try (var pool = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < TASKS; i++) {
                ScopedValue.where(SV_COUNTER, 42)
                    .run(() -> pool.submit(() -> {
                        sink.addAndGet(SV_COUNTER.get());
                    }));
            }
        }
        return System.currentTimeMillis() - start;
    }
}
