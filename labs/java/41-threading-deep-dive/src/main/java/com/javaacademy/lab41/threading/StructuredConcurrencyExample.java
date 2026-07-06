package com.javaacademy.lab41.threading;

import java.util.concurrent.*;

/**
 * Demonstrates StructuredTaskScope (Preview in Java 21) for structured concurrency.
 * ShutdownOnSuccess: returns first successful result, cancels remaining.
 * ShutdownOnFailure: waits for all, fails fast on any failure.
 */
public class StructuredConcurrencyExample {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Structured Concurrency (JEP 428/453) ===\n");
        shutdownOnSuccessDemo();
        shutdownOnFailureDemo();
    }

    static void shutdownOnSuccessDemo() throws Exception {
        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<String>()) {
            Future<String> user = scope.fork(() -> fetchUser(200));
            Future<String> config = scope.fork(() -> fetchConfig(100));
            scope.join();
            String result = scope.result();
            System.out.println("ShutdownOnSuccess result: " + result);
            System.out.println("User done: " + user.isDone() + ", Config done: " + config.isDone());
        }
    }

    static void shutdownOnFailureDemo() throws Exception {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Future<String> task1 = scope.fork(() -> fetchUser(150));
            Future<String> task2 = scope.fork(() -> fetchConfig(50));
            scope.join();
            scope.throwIfFailed(e -> new RuntimeException("Task failed", e));
            System.out.println("ShutdownOnFailure: " + task1.resultNow() + ", " + task2.resultNow());
        }
    }

    static String fetchUser(long delay) throws InterruptedException {
        Thread.sleep(delay);
        return "User(name=Alice)";
    }

    static String fetchConfig(long delay) throws InterruptedException {
        Thread.sleep(delay);
        return "Config(theme=dark)";
    }
}
