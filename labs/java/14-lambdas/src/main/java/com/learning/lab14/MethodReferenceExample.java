package com.learning.lab14;

import java.util.*;
import java.util.function.*;

/**
 * Demonstrates method references: static method, instance method, constructor.
 */
public class MethodReferenceExample {

    public static void showMethodReferences() {
        System.out.println("=== Method References ===");

        List<String> names = Arrays.asList("Charlie", "Alice", "Bob");
        names.sort(String::compareToIgnoreCase);
        System.out.println("Sorted (method ref): " + names);

        List<String> upper = names.stream()
            .map(String::toUpperCase)
            .toList();
        System.out.println("Uppercase: " + upper);

        Supplier<List<String>> listFactory = ArrayList::new;
        List<String> newList = listFactory.get();
        newList.add("created");
        newList.add("via");
        newList.add("constructor ref");
        System.out.println("Constructor reference: " + newList);

        Function<String, Integer> parser = Integer::parseInt;
        System.out.println("parseInt ref: " + parser.apply("123"));
    }
}
