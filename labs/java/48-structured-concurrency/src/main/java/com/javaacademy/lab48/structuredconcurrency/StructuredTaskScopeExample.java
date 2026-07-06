package com.javaacademy.lab48.structuredconcurrency;

import java.util.concurrent.*;

/**
 * Demonstrates StructuredTaskScope with ShutdownOnSuccess and ShutdownOnFailure policies.
 * Shows how structured concurrency manages task lifetimes and propagates errors
 * within a bounded scope.
 */
public class StructuredTaskScopeExample {

    public static void main(String[] args) throws Exception {
        System.out.println("=== ShutdownOnSuccess Example ===");
        String result = tryShutdownOnSuccess();
        System.out.println("Result: " + result);

        System.out.println("\n=== ShutdownOnFailure Example ===");
        String combined = tryShutdownOnFailure();
        System.out.println("Combined: " + combined);
    }

    static String tryShutdownOnSuccess() throws Exception {
        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<String>()) {
            Future<String> task1 = scope.fork(() -> {
                Thread.sleep(200);
                return "fast-result";
            });
            Future<String> task2 = scope.fork(() -> {
                Thread.sleep(500);
                return "slow-result";
            });
            scope.join();
            return scope.result();
        }
    }

    static String tryShutdownOnFailure() throws Exception {
        record Pair(String a, String b) {}
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Future<String> task1 = scope.fork(() -> {
                Thread.sleep(100);
                return "part-a";
            });
            Future<String> task2 = scope.fork(() -> {
                Thread.sleep(150);
                return "part-b";
            });
            scope.join();
            scope.throwIfFailed();
            return new Pair(task1.resultNow(), task2.resultNow()).toString();
        }
    }
}
