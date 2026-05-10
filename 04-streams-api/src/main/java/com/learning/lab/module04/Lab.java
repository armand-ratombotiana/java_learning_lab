package com.learning.lab.module04;

import java.util.*;
import java.util.stream.*;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 04: Streams API ===");
        streamCreationDemo();
        intermediateOperationsDemo();
        terminalOperationsDemo();
        reductionDemo();
        parallelStreamsDemo();
    }

    static void streamCreationDemo() {
        System.out.println("\n--- Stream Creation ---");
        List<Integer> list = List.of(1, 2, 3, 4, 5);
        Stream<Integer> stream = list.stream();
        System.out.println("From list: " + stream.collect(Collectors.toList()));

        int[] arr = {1, 2, 3, 4, 5};
        System.out.println("From array: " + Arrays.stream(arr).boxed().collect(Collectors.toList()));

        Stream<String> streamOf = Stream.of("a", "b", "c");
        System.out.println("Stream.of: " + streamOf.collect(Collectors.toList()));

        Stream.iterate(1, n -> n + 1).limit(5).forEach(System.out::print);
        System.out.println();
        System.out.println("Stream.generate: " + Stream.generate(() -> "x").limit(3).collect(Collectors.toList()));
    }

    static void intermediateOperationsDemo() {
        System.out.println("\n--- Intermediate Operations ---");
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        List<Integer> filtered = numbers.stream().filter(n -> n % 2 == 0).collect(Collectors.toList());
        System.out.println("Filter (even): " + filtered);

        List<Integer> mapped = numbers.stream().map(n -> n * 2).collect(Collectors.toList());
        System.out.println("Map (x2): " + mapped);

        List<String> flatMapped = Stream.of(List.of(1, 2), List.of(3, 4))
            .flatMap(List::stream).map(String::valueOf).collect(Collectors.toList());
        System.out.println("FlatMap: " + flatMapped);

        List<Integer> distinct = Stream.of(1, 2, 2, 3, 3, 3).distinct().collect(Collectors.toList());
        System.out.println("Distinct: " + distinct);

        List<Integer> sorted = numbers.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        System.out.println("Sorted (desc): " + sorted);

        List<Integer> skip = numbers.stream().skip(5).collect(Collectors.toList());
        System.out.println("Skip(5): " + skip);

        List<Integer> limit = numbers.stream().limit(5).collect(Collectors.toList());
        System.out.println("Limit(5): " + limit);
    }

    static void terminalOperationsDemo() {
        System.out.println("\n--- Terminal Operations ---");
        List<String> names = List.of("Alice", "Bob", "Charlie", "David");

        long count = names.stream().filter(s -> s.length() > 3).count();
        System.out.println("Count (>3 chars): " + count);

        boolean anyMatch = names.stream().anyMatch(s -> s.startsWith("A"));
        System.out.println("Any match (starts with A): " + anyMatch);

        boolean allMatch = names.stream().allMatch(s -> s.length() > 2);
        System.out.println("All match (len > 2): " + allMatch);

        boolean noneMatch = names.stream().noneMatch(s -> s.isEmpty());
        System.out.println("None match (empty): " + noneMatch);

        Optional<String> findFirst = names.stream().findFirst();
        System.out.println("Find first: " + findFirst.orElse("none"));

        Optional<String> findAny = names.stream().parallel().findAny();
        System.out.println("Find any: " + findAny.orElse("none"));

        names.stream().forEach(System.out::print);
        System.out.println();

        String reduced = names.stream().reduce("", (a, b) -> a + b + ", ");
        System.out.println("Reduce: " + reduced.trim());
    }

    static void reductionDemo() {
        System.out.println("\n--- Reduction ---");
        List<Integer> nums = List.of(1, 2, 3, 4, 5);

        Optional<Integer> sum = nums.stream().reduce(Integer::sum);
        System.out.println("Sum: " + sum.orElse(0));

        Optional<Integer> max = nums.stream().reduce(Integer::max);
        System.out.println("Max: " + max.orElse(0));

        Optional<Integer> min = nums.stream().reduce(Integer::min);
        System.out.println("Min: " + min.orElse(0));

        Integer product = nums.stream().reduce(1, (a, b) -> a * b);
        System.out.println("Product: " + product);

        Map<String, Integer> lengths = List.of("apple", "banana", "cherry").stream()
            .collect(Collectors.toMap(s -> s, String::length));
        System.out.println("ToMap: " + lengths);

        Double average = nums.stream().collect(Collectors.averagingInt(Integer::intValue));
        System.out.println("Average: " + average);

        IntSummaryStatistics stats = nums.stream().collect(Collectors.summarizingInt(Integer::intValue));
        System.out.println("Statistics: " + stats);
    }

    static void parallelStreamsDemo() {
        System.out.println("\n--- Parallel Streams ---");
        List<Integer> numbers = IntStream.range(1, 100).boxed().collect(Collectors.toList());

        long start = System.nanoTime();
        long count = numbers.parallelStream().filter(n -> n % 2 == 0).count();
        long time = System.nanoTime() - start;
        System.out.println("Parallel count: " + count + " in " + time + " ns");
    }
}