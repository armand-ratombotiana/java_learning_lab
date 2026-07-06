package com.javaacademy.lab48.structuredconcurrency;

import java.util.concurrent.*;

/**
 * Combines virtual threads with structured concurrency to demonstrate
 * massive parallelism with clean lifecycle management.
 */
public class VirtualThreadStructuredExample {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Virtual + Structured Concurrency ===");
        long result = processMany(1000);
        System.out.println("Sum: " + result);
    }

    static long processMany(int count) throws Exception {
        record TaskResult(int id, long value) {}

        long total = 0;
        int batchSize = 100;
        for (int batch = 0; batch < count / batchSize; batch++) {
            try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
                var futures = new java.util.ArrayList<Future<TaskResult>>();
                for (int i = 0; i < batchSize; i++) {
                    int finalI = batch * batchSize + i;
                    futures.add(scope.fork(() -> {
                        // Simulate IO-bound work
                        Thread.sleep(1);
                        return new TaskResult(finalI, (long) finalI * finalI);
                    }));
                }
                scope.join();
                for (var f : futures) {
                    total += f.resultNow().value();
                }
            }
        }
        return total;
    }
}
