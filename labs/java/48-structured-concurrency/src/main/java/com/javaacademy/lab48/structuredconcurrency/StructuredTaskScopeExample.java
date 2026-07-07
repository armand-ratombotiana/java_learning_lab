package com.javaacademy.lab48.structuredconcurrency;

import java.util.concurrent.StructuredTaskScope;

public class StructuredTaskScopeExample {

    public static void main(String[] args) throws Exception {
        System.out.println("=== anySuccessfulResultOrThrow Example ===");
        String result = tryAnySuccessfulResultOrThrow();
        System.out.println("Result: " + result);

        System.out.println("\n=== awaitAllSuccessfulOrThrow Example ===");
        String combined = tryAwaitAllSuccessfulOrThrow();
        System.out.println("Combined: " + combined);
    }

    static String tryAnySuccessfulResultOrThrow() throws Exception {
        try (StructuredTaskScope<String, String> scope = StructuredTaskScope.open(
                StructuredTaskScope.Joiner.anySuccessfulResultOrThrow())) {
            scope.fork(() -> {
                Thread.sleep(200);
                return "fast-result";
            });
            scope.fork(() -> {
                Thread.sleep(500);
                return "slow-result";
            });
            return scope.join();
        }
    }

    static String tryAwaitAllSuccessfulOrThrow() throws Exception {
        record Pair(String a, String b) {}
        try (StructuredTaskScope<String, Void> scope = StructuredTaskScope.open(
                StructuredTaskScope.Joiner.awaitAllSuccessfulOrThrow())) {
            StructuredTaskScope.Subtask<String> task1 = scope.fork(() -> {
                Thread.sleep(100);
                return "part-a";
            });
            StructuredTaskScope.Subtask<String> task2 = scope.fork(() -> {
                Thread.sleep(150);
                return "part-b";
            });
            scope.join();
            return new Pair(task1.get(), task2.get()).toString();
        }
    }
}
