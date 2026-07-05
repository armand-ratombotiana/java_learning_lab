package com.learning.lab15;

import java.util.*;

/**
 * Demonstrates Optional chaining with map, flatMap, filter, orElse.
 */
public class OptionalChainingExample {

    public static void showOptionalChaining() {
        System.out.println("=== Optional Chaining ===");

        Optional<String> optional = Optional.of("  Hello Functional  ");

        String result = optional
            .map(String::trim)
            .map(String::toUpperCase)
            .filter(s -> s.startsWith("H"))
            .orElse("DEFAULT");
        System.out.println("Chained optional result: " + result);

        Optional<String> empty = Optional.empty();
        String fallback = empty
            .map(String::trim)
            .orElse("Fallback Value");
        System.out.println("Empty optional fallback: " + fallback);

        Optional<String> name = Optional.ofNullable(getName(false));
        int length = name
            .filter(n -> n.length() > 2)
            .map(String::length)
            .orElse(-1);
        System.out.println("Name length or -1: " + length);
    }

    static String getName(boolean returnNull) {
        return returnNull ? null : "Al";
    }
}
