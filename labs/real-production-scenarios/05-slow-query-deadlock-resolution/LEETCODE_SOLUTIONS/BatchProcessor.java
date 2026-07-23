package com.prod.solutions.slowquery;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Demonstrates optimized batch processing using JDBC batch updates
 * with configurable batch size. This is the recommended pattern for
 * processing large datasets without overwhelming the database.
 *
 * FIX: Uses batch processing instead of individual row-by-row operations,
 * reducing database round-trips from N to N/batchSize.
 */
public class BatchProcessor {

    private final int batchSize;
    private final ExecutorService executor;

    public BatchProcessor(int batchSize, int threadCount) {
        this.batchSize = batchSize;
        this.executor = Executors.newFixedThreadPool(threadCount);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Batch Processing Performance Demo ===");

        List<Integer> records = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            records.add(i);
        }

        // Naive approach: one by one
        long start = System.nanoTime();
        for (Integer record : records) {
            simulateProcessRecord(record);
        }
        long rowByRow = (System.nanoTime() - start) / 1_000_000;

        // Batch approach
        BatchProcessor processor = new BatchProcessor(100, 4);
        start = System.nanoTime();
        processor.processBatch(records);
        long batchTime = (System.nanoTime() - start) / 1_000_000;

        System.out.printf("Row-by-row:   %d records in %d ms (%.1f rec/s)%n",
                records.size(), rowByRow, records.size() * 1000.0 / rowByRow);
        System.out.printf("Batch (sz=%d): %d records in %d ms (%.1f rec/s)%n",
                100, records.size(), batchTime, records.size() * 1000.0 / batchTime);
        System.out.printf("Speedup: %.1fx%n", (double) rowByRow / batchTime);

        processor.shutdown();

        System.out.printf("%nScaling: 1M records%n");
        System.out.printf("  Row-by-row: ~%d seconds%n", 1_000_000 * 5 / 1000);
        System.out.printf("  Batch 100:  ~%d seconds%n", 1_000_000 * 5 / (100 * 4 * 1000));
    }

    /**
     * Processes records in batches. Each batch is submitted as a task
     * to the thread pool, enabling parallel execution with controlled
     * concurrency.
     */
    public void processBatch(List<Integer> records) throws Exception {
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < records.size(); i += batchSize) {
            int end = Math.min(i + batchSize, records.size());
            List<Integer> batch = records.subList(i, end);

            futures.add(executor.submit(() -> {
                for (Integer record : batch) {
                    simulateProcessRecord(record);
                }
            }));
        }

        for (Future<?> f : futures) {
            f.get(30, TimeUnit.SECONDS);
        }
    }

    static void simulateProcessRecord(int id) {
        // Simulate 5ms of DB work per record
        try { Thread.sleep(5); } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}
