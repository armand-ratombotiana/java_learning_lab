package com.learning.lab13;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates various ways to create streams and the map/filter/sorted pipeline operations.
 */
public class StreamCreationExample {

    public static void showStreams() {
        System.out.println("=== Stream Creation & Pipelines ===");

        List<String> items = List.of("banana", "apple", "cherry", "date", "grape");

        List<String> result = items.stream()
            .filter(s -> s.length() >= 5)
            .map(String::toUpperCase)
            .sorted()
            .collect(Collectors.toList());
        System.out.println("Filtered, mapped, sorted: " + result);

        Stream<Integer> fromValues = Stream.of(1, 2, 3, 4, 5);
        System.out.println("Stream.of: " + fromValues.collect(Collectors.toList()));

        IntStream range = IntStream.range(1, 6);
        System.out.println("IntStream.range: " + range.boxed().collect(Collectors.toList()));

        Stream<String> fromArray = Arrays.stream(new String[]{"x", "y", "z"});
        System.out.println("Arrays.stream: " + fromArray.collect(Collectors.toList()));

        Stream<Integer> iterate = Stream.iterate(0, n -> n + 2).limit(5);
        System.out.println("Stream.iterate: " + iterate.collect(Collectors.toList()));
    }
}
