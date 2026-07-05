package com.learning.lab13;

import java.util.*;
import java.util.stream.*;

/**
 * Demonstrates parallelStream for parallel processing of collections.
 */
public class ParallelStreamExample {

    public static void showParallelStream() {
        System.out.println("=== parallelStream ===");

        long count = LongStream.rangeClosed(1, 10_000_000)
            .parallel()
            .filter(n -> n % 2 == 0)
            .count();
        System.out.println("Even numbers from 1 to 10M (parallel): " + count);

        List<Integer> numbers = IntStream.rangeClosed(1, 20)
            .boxed()
            .collect(Collectors.toList());

        long serialTime = measureTime(() -> 
            numbers.stream().map(n -> n * 2).collect(Collectors.toList()));
        long parallelTime = measureTime(() -> 
            numbers.parallelStream().map(n -> n * 2).collect(Collectors.toList()));

        System.out.println("Serial time: " + serialTime + "ms");
        System.out.println("Parallel time: " + parallelTime + "ms");

        List<Integer> parallelResult = numbers.parallelStream()
            .filter(n -> n % 3 == 0)
            .map(n -> n * n)
            .sorted()
            .collect(Collectors.toList());
        System.out.println("Parallel pipeline result: " + parallelResult);
    }

    static long measureTime(Runnable task) {
        long start = System.nanoTime();
        task.run();
        return (System.nanoTime() - start) / 1_000_000;
    }
}
