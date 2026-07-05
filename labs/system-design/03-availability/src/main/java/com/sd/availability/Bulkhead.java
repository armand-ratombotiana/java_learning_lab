package com.sd.availability;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class Bulkhead {

    public static class BulkheadPool {
        private final String name;
        private final Semaphore semaphore;
        private final int maxConcurrent;
        private final AtomicInteger activeCount = new AtomicInteger(0);
        private final AtomicInteger rejectedCount = new AtomicInteger(0);

        public BulkheadPool(String name, int maxConcurrent) {
            this.name = name;
            this.maxConcurrent = maxConcurrent;
            this.semaphore = new Semaphore(maxConcurrent);
        }

        public boolean tryAcquire() {
            boolean acquired = semaphore.tryAcquire();
            if (acquired) {
                activeCount.incrementAndGet();
                System.out.println("[" + name + "] Acquired (" + activeCount.get() + "/" + maxConcurrent + ")");
            } else {
                rejectedCount.incrementAndGet();
                System.out.println("[" + name + "] REJECTED (" + activeCount.get() + "/" + maxConcurrent + ")");
            }
            return acquired;
        }

        public void release() {
            semaphore.release();
            activeCount.decrementAndGet();
        }

        public int getActiveCount() { return activeCount.get(); }
        public int getRejectedCount() { return rejectedCount.get(); }
    }

    public static class BulkheadExecutor {
        private final Map<String, BulkheadPool> pools = new ConcurrentHashMap<>();

        public BulkheadExecutor addPool(String name, int maxConcurrent) {
            pools.put(name, new BulkheadPool(name, maxConcurrent));
            return this;
        }

        public void execute(String poolName, Runnable task) {
            BulkheadPool pool = pools.get(poolName);
            if (pool == null) {
                throw new IllegalArgumentException("Unknown pool: " + poolName);
            }
            if (pool.tryAcquire()) {
                try {
                    task.run();
                } finally {
                    pool.release();
                }
            } else {
                System.out.println("  [Bulkhead] Task rejected for pool " + poolName);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        BulkheadExecutor executor = new BulkheadExecutor();
        executor.addPool("api-calls", 3);
        executor.addPool("db-queries", 2);

        System.out.println("=== Bulkhead Pattern ===");
        CountDownLatch latch = new CountDownLatch(6);

        for (int i = 0; i < 6; i++) {
            final int id = i;
            new Thread(() -> {
                executor.execute("api-calls", () -> {
                    System.out.println("  Processing API call " + id);
                    try { Thread.sleep(500); } catch (InterruptedException e) {}
                    latch.countDown();
                });
            }).start();
        }

        latch.await(3, TimeUnit.SECONDS);
        System.out.println("\nDone - API pool rejected count: "
            + executor.pools.get("api-calls").getRejectedCount());
    }
}
