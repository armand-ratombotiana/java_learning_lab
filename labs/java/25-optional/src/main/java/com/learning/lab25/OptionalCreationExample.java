package com.learning.lab25;

import java.util.*;

/**
 * Demonstrates Optional.of(), ofNullable(), and empty() creation patterns.
 */
public class OptionalCreationExample {

    public static void showOptionalCreation() {
        System.out.println("=== Optional Creation ===");

        Optional<String> nonEmpty = Optional.of("Hello");
        Optional<String> nullable = Optional.ofNullable(getValue(true));
        Optional<String> empty = Optional.empty();

        System.out.println("Optional.of: " + nonEmpty);
        System.out.println("Optional.ofNullable (non-null): " + nullable);
        System.out.println("Optional.empty: " + empty);

        System.out.println("isPresent checks:");
        System.out.println("  nonEmpty.isPresent(): " + nonEmpty.isPresent());
        System.out.println("  empty.isPresent(): " + empty.isPresent());
        System.out.println("  nullable.isEmpty(): " + nullable.isEmpty());
    }

    static String getValue(boolean returnValue) {
        return returnValue ? "Some value" : null;
    }
}
