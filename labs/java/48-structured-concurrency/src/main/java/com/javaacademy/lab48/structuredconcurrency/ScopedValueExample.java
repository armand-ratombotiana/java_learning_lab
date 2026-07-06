package com.javaacademy.lab48.structuredconcurrency;

import java.util.concurrent.StructuredTaskScope;

/**
 * Demonstrates ScopedValue usage within structured concurrency.
 * ScopedValue provides bounded dynamic scoping that works correctly
 * with virtual threads and structured task scopes, unlike ThreadLocal.
 */
public class ScopedValueExample {

    private static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();
    private static final ScopedValue<String> USER_CONTEXT = ScopedValue.newInstance();

    public static void main(String[] args) throws Exception {
        ScopedValue.where(REQUEST_ID, "req-001")
            .where(USER_CONTEXT, "user-admin")
            .run(() -> {
                try {
                    processInScope();
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                }
            });
    }

    static void processInScope() throws Exception {
        System.out.println("Request: " + REQUEST_ID.get());
        System.out.println("User: " + USER_CONTEXT.get());

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            scope.fork(() -> {
                System.out.println("  Subtask sees request: " + REQUEST_ID.get());
                return "ok";
            });
            scope.fork(() -> {
                System.out.println("  Subtask sees user: " + USER_CONTEXT.get());
                return "ok";
            });
            scope.join();
        }
    }
}
