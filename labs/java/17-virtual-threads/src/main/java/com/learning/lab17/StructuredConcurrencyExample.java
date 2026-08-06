package com.learning.lab17;

import java.util.concurrent.*;

/**
 * Demonstrates structured concurrency with virtual threads (ScopedValue concepts).
 * Java 21 preview: StructuredTaskScope for managing task lifetimes.
 */
public class StructuredConcurrencyExample {

    public static void showStructuredConcurrency() throws Exception {
        System.out.println("=== Structured Concurrency ===");

        try (var scope = StructuredTaskScope.open(StructuredTaskScope.Joiner.<String>awaitAllSuccessfulOrThrow())) {
            StructuredTaskScope.Subtask<String> user = scope.fork(() -> fetchUser());
            StructuredTaskScope.Subtask<String> order = scope.fork(() -> fetchOrder());

            scope.join();

            System.out.println("  User: " + user.get());
            System.out.println("  Order: " + order.get());
        }

        System.out.println("Structured scope completed");
    }

    static String fetchUser() throws InterruptedException {
        Thread.sleep(100);
        return "Alice (fetched via virtual thread)";
    }

    static String fetchOrder() throws InterruptedException {
        Thread.sleep(150);
        return "Order #12345";
    }
}
