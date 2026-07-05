package com.learning.lab24;

/**
 * Demonstrates guarded patterns with when clauses and record pattern nesting.
 */
public class GuardedPatternsExample {

    public static void showGuardedPatterns() {
        System.out.println("=== Guarded Patterns ===");

        Object[] data = {
            new Transaction("Alice", 5000),
            new Transaction("Bob", 100),
            new Transaction("Charlie", 15000),
            "Not a transaction",
            null
        };

        for (Object obj : data) {
            processTransaction(obj);
        }
    }

    static void processTransaction(Object obj) {
        String result = switch (obj) {
            case null -> "Null input";
            case Transaction t when t.amount() > 10000 -> 
                "HIGH VALUE: " + t.user() + " - $" + t.amount() + " (requires approval)";
            case Transaction t when t.amount() > 1000 -> 
                "Medium: " + t.user() + " - $" + t.amount();
            case Transaction t -> 
                "Low: " + t.user() + " - $" + t.amount();
            default -> "Not a transaction: " + obj.getClass().getSimpleName();
        };
        System.out.println("  " + result);
    }
}

record Transaction(String user, int amount) {}
