package com.learning.lab13;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates reduce, counting, min/max, and primitive stream operations.
 */
public class StreamReduceExample {

    public static void showReduce() {
        System.out.println("=== Reduce & Primitive Streams ===");

        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        int sum = numbers.stream().reduce(0, Integer::sum);
        int product = numbers.stream().reduce(1, (a, b) -> a * b);
        Optional<Integer> max = numbers.stream().reduce(Integer::max);

        System.out.println("Sum: " + sum);
        System.out.println("Product: " + product);
        System.out.println("Max: " + max.orElse(-1));

        long count = numbers.stream().count();
        System.out.println("Count: " + count);

        IntSummaryStatistics stats = numbers.stream()
            .mapToInt(Integer::intValue)
            .summaryStatistics();
        System.out.println("Stats: " + stats);

        OptionalDouble average = numbers.stream()
            .mapToInt(Integer::intValue)
            .average();
        System.out.println("Average: " + average.orElse(0.0));
    }
}
