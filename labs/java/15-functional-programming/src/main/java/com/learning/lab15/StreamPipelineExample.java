package com.learning.lab15;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates complex stream pipelines, immutability, and pure functions.
 */
public class StreamPipelineExample {

    public static void showStreamPipelines() {
        System.out.println("=== Stream Pipelines & Immutability ===");

        var result = Stream.of("apple", "banana", "avocado", "cherry", "apricot")
            .filter(s -> s.startsWith("a"))
            .map(String::toUpperCase)
            .sorted()
            .collect(Collectors.toList());
        System.out.println("Pipeline result: " + result);

        var transactions = List.of(
            new Transaction("Alice", 100),
            new Transaction("Bob", 200),
            new Transaction("Alice", 50),
            new Transaction("Charlie", 300)
        );

        var summary = transactions.stream()
            .collect(Collectors.groupingBy(
                Transaction::user,
                Collectors.summingInt(Transaction::amount)
            ));
        System.out.println("Transaction summary: " + summary);

        var topUsers = summary.entrySet().stream()
            .filter(e -> e.getValue() > 100)
            .map(Map.Entry::getKey)
            .sorted()
            .collect(Collectors.toList());
        System.out.println("Top users (>100): " + topUsers);
    }
}

record Transaction(String user, int amount) {}
