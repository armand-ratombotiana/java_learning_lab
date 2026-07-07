package com.javaacademy.lab48.structuredconcurrency;

import java.util.concurrent.StructuredTaskScope;

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
            try (StructuredTaskScope<TaskResult, Void> scope = StructuredTaskScope.open(
                    StructuredTaskScope.Joiner.awaitAllSuccessfulOrThrow())) {
                var subtasks = new java.util.ArrayList<StructuredTaskScope.Subtask<TaskResult>>();
                for (int i = 0; i < batchSize; i++) {
                    int finalI = batch * batchSize + i;
                    subtasks.add(scope.fork(() -> {
                        Thread.sleep(1);
                        return new TaskResult(finalI, (long) finalI * finalI);
                    }));
                }
                scope.join();
                for (var s : subtasks) {
                    total += s.get().value();
                }
            }
        }
        return total;
    }
}
