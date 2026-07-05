package com.learning.lab25;

import java.util.*;

/**
 * Demonstrates Optional best practices and common anti-patterns.
 */
public class OptionalBestPracticesExample {

    public static void showBestPractices() {
        System.out.println("=== Optional Best Practices ===");

        System.out.println("Good: " + findUser(1));
        System.out.println("Not found: " + findUser(999));

        Optional<String> result = getConfig("timeout");
        result.ifPresentOrElse(
            val -> System.out.println("Config found: " + val),
            () -> System.out.println("Config not found, using default")
        );

        System.out.println("Chained result: " 
            + getConfig("host").orElseGet(() -> "localhost"));
    }

    static Optional<String> findUser(int id) {
        Map<Integer, String> users = Map.of(1, "Alice", 2, "Bob");
        return Optional.ofNullable(users.get(id));
    }

    static Optional<String> getConfig(String key) {
        Map<String, String> config = new HashMap<>();
        config.put("host", "example.com");
        config.put("port", "8080");
        return Optional.ofNullable(config.get(key));
    }
}
