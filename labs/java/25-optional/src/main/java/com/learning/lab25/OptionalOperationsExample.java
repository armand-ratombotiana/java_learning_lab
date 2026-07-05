package com.learning.lab25;

import java.util.*;

/**
 * Demonstrates map(), flatMap(), filter(), orElse(), orElseGet(), orElseThrow().
 */
public class OptionalOperationsExample {

    public static void showOptionalOperations() {
        System.out.println("=== Optional Operations ===");

        Optional<String> opt = Optional.of("  Java Optional  ");

        String mapped = opt
            .map(String::trim)
            .map(String::toUpperCase)
            .orElse("DEFAULT");
        System.out.println("Mapped result: " + mapped);

        Optional<String> filtered = opt
            .map(String::trim)
            .filter(s -> s.contains("Java"));
        System.out.println("Filtered (contains Java): " + filtered);

        Optional<String> filteredOut = opt
            .map(String::trim)
            .filter(s -> s.contains("Python"));
        System.out.println("Filtered (contains Python): " + filteredOut);

        String withDefault = filteredOut.orElse("Default Value");
        System.out.println("orElse: " + withDefault);

        String withGet = filteredOut.orElseGet(() -> "Generated Default");
        System.out.println("orElseGet: " + withGet);

        try {
            String thrown = filteredOut.orElseThrow(() -> new NoSuchElementException("Value missing"));
        } catch (NoSuchElementException e) {
            System.out.println("orElseThrow: " + e.getMessage());
        }

        Optional<String> name = Optional.ofNullable(null);
        name.ifPresent(n -> System.out.println("ifPresent: " + n));
        name.ifPresentOrElse(
            n -> System.out.println("ifPresentOrElse: " + n),
            () -> System.out.println("ifPresentOrElse: value absent")
        );
    }
}
