package com.learning.lab.module04.solution;

import java.util.*;
import java.util.stream.*;

public class Test {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== Module 04: Streams API - Comprehensive Tests ===\n");

        testStreamCreation();
        testFilterOperation();
        testMapOperation();
        testFlatMapOperation();
        testDistinctOperation();
        testSortedOperation();
        testLimitSkipOperation();
        testTerminalOperations();
        testCollectors();
        testReduction();
        testOptional();
        testPrimitiveStreams();
        testParallelStreams();

        printSummary();
    }

    private static void testStreamCreation() {
        System.out.println("--- Testing Stream Creation ---");

        test("Stream from List", () -> {
            List<Integer> list = List.of(1, 2, 3);
            List<Integer> result = list.stream().collect(Collectors.toList());
            assert result.equals(list) : "Should match original list";
        });

        test("Stream from Array", () -> {
            Integer[] arr = {1, 2, 3};
            List<Integer> result = Arrays.stream(arr).collect(Collectors.toList());
            assert result.size() == 3 : "Should have 3 elements";
        });

        test("Stream.of", () -> {
            List<String> result = Stream.of("a", "b", "c").collect(Collectors.toList());
            assert result.size() == 3 : "Should have 3 elements";
        });

        test("Stream.iterate", () -> {
            List<Integer> result = Stream.iterate(1, n -> n + 1).limit(5).collect(Collectors.toList());
            assert result.equals(List.of(1, 2, 3, 4, 5)) : "Should be 1-5";
        });

        test("Stream.generate", () -> {
            List<String> result = Stream.generate(() -> "x").limit(3).collect(Collectors.toList());
            assert result.size() == 3 : "Should have 3 elements";
            assert result.stream().allMatch(s -> s.equals("x")) : "All should be x";
        });

        test("Stream.empty", () -> {
            List<String> result = Stream.<String>empty().collect(Collectors.toList());
            assert result.isEmpty() : "Should be empty";
        });

        test("IntStream.range", () -> {
            List<Integer> result = IntStream.range(1, 5).boxed().collect(Collectors.toList());
            assert result.equals(List.of(1, 2, 3, 4)) : "Should be 1-4";
        });

        test("IntStream.rangeClosed", () -> {
            List<Integer> result = IntStream.rangeClosed(1, 5).boxed().collect(Collectors.toList());
            assert result.equals(List.of(1, 2, 3, 4, 5)) : "Should be 1-5";
        });

        System.out.println();
    }

    private static void testFilterOperation() {
        System.out.println("--- Testing Filter Operation ---");

        test("Filter evens", () -> {
            List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6);
            List<Integer> result = numbers.stream().filter(n -> n % 2 == 0).collect(Collectors.toList());
            assert result.equals(List.of(2, 4, 6)) : "Should be even numbers";
        });

        test("Filter by condition", () -> {
            List<String> names = List.of("Alice", "Bob", "Charlie");
            List<String> result = names.stream().filter(s -> s.length() > 4).collect(Collectors.toList());
            assert result.equals(List.of("Alice", "Charlie")) : "Should have longer names";
        });

        test("Filter with null handling", () -> {
            List<String> names = List.of("A", null, "B", null);
            long count = names.stream().filter(Objects::nonNull).count();
            assert count == 2 : "Should have 2 non-null elements";
        });

        test("Chained filters", () -> {
            List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            List<Integer> result = numbers.stream()
                .filter(n -> n > 3)
                .filter(n -> n < 8)
                .filter(n -> n % 2 == 0)
                .collect(Collectors.toList());
            assert result.equals(List.of(4, 6)) : "Should be filtered correctly";
        });

        System.out.println();
    }

    private static void testMapOperation() {
        System.out.println("--- Testing Map Operation ---");

        test("Map to double", () -> {
            List<Integer> numbers = List.of(1, 2, 3);
            List<Integer> result = numbers.stream().map(n -> n * 10).collect(Collectors.toList());
            assert result.equals(List.of(10, 20, 30)) : "Should multiply by 10";
        });

        test("Map to object", () -> {
            List<String> names = List.of("alice", "bob");
            List<String> result = names.stream().map(String::toUpperCase).collect(Collectors.toList());
            assert result.equals(List.of("ALICE", "BOB")) : "Should be uppercase";
        });

        test("Map to new type", () -> {
            List<String> numbers = List.of("1", "2", "3");
            List<Integer> result = numbers.stream().map(Integer::parseInt).collect(Collectors.toList());
            assert result.equals(List.of(1, 2, 3)) : "Should parse to int";
        });

        test("Map with index", () -> {
            List<String> names = List.of("A", "B", "C");
            List<String> result = names.stream()
                .map(s -> s + "_")
                .collect(Collectors.toList());
            assert result.equals(List.of("A_", "B_", "C_")) : "Should add underscore";
        });

        System.out.println();
    }

    private static void testFlatMapOperation() {
        System.out.println("--- Testing FlatMap Operation ---");

        test("FlatMap nested lists", () -> {
            List<List<Integer>> nested = List.of(List.of(1, 2), List.of(3, 4));
            List<Integer> result = nested.stream().flatMap(List::stream).collect(Collectors.toList());
            assert result.equals(List.of(1, 2, 3, 4)) : "Should flatten";
        });

        test("FlatMap arrays", () -> {
            String[][] arrays = {{"a", "b"}, {"c", "d"}};
            List<String> result = Arrays.stream(arrays).flatMap(Arrays::stream).collect(Collectors.toList());
            assert result.equals(List.of("a", "b", "c", "d")) : "Should flatten";
        });

        test("FlatMap to split", () -> {
            List<String> words = List.of("ab", "cd");
            List<String> result = words.stream()
                .flatMap(w -> Arrays.stream(w.split("")))
                .collect(Collectors.toList());
            assert result.equals(List.of("a", "b", "c", "d")) : "Should split characters";
        });

        System.out.println();
    }

    private static void testDistinctOperation() {
        System.out.println("--- Testing Distinct Operation ---");

        test("Distinct removes duplicates", () -> {
            List<Integer> numbers = List.of(1, 2, 2, 3, 3, 3);
            List<Integer> result = numbers.stream().distinct().collect(Collectors.toList());
            assert result.equals(List.of(1, 2, 3)) : "Should remove duplicates";
        });

        test("Distinct with objects", () -> {
            List<String> strings = List.of("a", "b", "A", "b");
            List<String> result = strings.stream().map(String::toLowerCase).distinct().collect(Collectors.toList());
            assert result.equals(List.of("a", "b")) : "Should be lowercase distinct";
        });

        System.out.println();
    }

    private static void testSortedOperation() {
        System.out.println("--- Testing Sorted Operation ---");

        test("Sorted natural order", () -> {
            List<Integer> numbers = List.of(3, 1, 2);
            List<Integer> result = numbers.stream().sorted().collect(Collectors.toList());
            assert result.equals(List.of(1, 2, 3)) : "Should be sorted";
        });

        test("Sorted with comparator", () -> {
            List<Integer> numbers = List.of(3, 1, 2);
            List<Integer> result = numbers.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
            assert result.equals(List.of(3, 2, 1)) : "Should be reverse sorted";
        });

        test("Sorted strings by length", () -> {
            List<String> words = List.of("abc", "a", "ab");
            List<String> result = words.stream().sorted(Comparator.comparingInt(String::length)).collect(Collectors.toList());
            assert result.equals(List.of("a", "ab", "abc")) : "Should be sorted by length";
        });

        System.out.println();
    }

    private static void testLimitSkipOperation() {
        System.out.println("--- Testing Limit & Skip ---");

        test("Limit", () -> {
            List<Integer> numbers = List.of(1, 2, 3, 4, 5);
            List<Integer> result = numbers.stream().limit(3).collect(Collectors.toList());
            assert result.equals(List.of(1, 2, 3)) : "Should have 3 elements";
        });

        test("Skip", () -> {
            List<Integer> numbers = List.of(1, 2, 3, 4, 5);
            List<Integer> result = numbers.stream().skip(2).collect(Collectors.toList());
            assert result.equals(List.of(3, 4, 5)) : "Should skip first 2";
        });

        test("Skip and limit", () -> {
            List<Integer> numbers = List.of(1, 2, 3, 4, 5);
            List<Integer> result = numbers.stream().skip(1).limit(2).collect(Collectors.toList());
            assert result.equals(List.of(2, 3)) : "Should skip 1 limit 2";
        });

        System.out.println();
    }

    private static void testTerminalOperations() {
        System.out.println("--- Testing Terminal Operations ---");

        test("forEach", () -> {
            List<String> results = new ArrayList<>();
            List.of("a", "b", "c").stream().forEach(results::add);
            assert results.size() == 3 : "Should have 3 elements";
        });

        test("count", () -> {
            long count = List.of(1, 2, 3, 4, 5).stream().count();
            assert count == 5 : "Count should be 5";
        });

        test("anyMatch", () -> {
            boolean result = List.of(1, 2, 3).stream().anyMatch(n -> n == 2);
            assert result : "Should match";
        });

        test("allMatch", () -> {
            boolean result = List.of(1, 2, 3).stream().allMatch(n -> n > 0);
            assert result : "All should match";
        });

        test("noneMatch", () -> {
            boolean result = List.of(1, 2, 3).stream().noneMatch(n -> n < 0);
            assert result : "None should match";
        });

        test("findFirst", () -> {
            Optional<Integer> result = List.of(1, 2, 3).stream().findFirst();
            assert result.orElse(0) == 1 : "First should be 1";
        });

        test("findAny", () -> {
            Optional<Integer> result = List.of(1, 2, 3).stream().findAny();
            assert result.isPresent() : "Should find any";
        });

        test("reduce with identity", () -> {
            Integer sum = List.of(1, 2, 3).stream().reduce(0, Integer::sum);
            assert sum == 6 : "Sum should be 6";
        });

        test("reduce without identity", () -> {
            Optional<Integer> sum = List.of(1, 2, 3).stream().reduce(Integer::sum);
            assert sum.orElse(0) == 6 : "Sum should be 6";
        });

        System.out.println();
    }

    private static void testCollectors() {
        System.out.println("--- Testing Collectors ---");

        test("toList", () -> {
            List<Integer> result = Stream.of(1, 2, 3).collect(Collectors.toList());
            assert result.size() == 3 : "Should have 3 elements";
        });

        test("toSet", () -> {
            Set<Integer> result = Stream.of(1, 2, 2, 3).collect(Collectors.toSet());
            assert result.size() == 3 : "Should have 3 unique elements";
        });

        test("toMap", () -> {
            Map<String, Integer> result = Stream.of("a", "bb", "ccc")
                .collect(Collectors.toMap(s -> s, String::length));
            assert result.get("a") == 1 : "a should map to 1";
            assert result.get("bb") == 2 : "bb should map to 2";
        });

        test("joining", () -> {
            String result = Stream.of("a", "b", "c").collect(Collectors.joining(","));
            assert result.equals("a,b,c") : "Should join with comma";
        });

        test("groupingBy", () -> {
            Map<Integer, List<String>> result = Stream.of("a", "bb", "ccc")
                .collect(Collectors.groupingBy(String::length));
            assert result.get(1).contains("a") : "Should group correctly";
        });

        test("partitioningBy", () -> {
            Map<Boolean, List<Integer>> result = Stream.of(1, 2, 3, 4)
                .collect(Collectors.partitioningBy(n -> n % 2 == 0));
            assert result.get(true).equals(List.of(2, 4)) : "Should partition evens";
            assert result.get(false).equals(List.of(1, 3)) : "Should partition odds";
        });

        test("counting", () -> {
            Long count = Stream.of("a", "b", "c").collect(Collectors.counting());
            assert count == 3 : "Count should be 3";
        });

        test("summingInt", () -> {
            int sum = Stream.of(1, 2, 3).collect(Collectors.summingInt(Integer::intValue));
            assert sum == 6 : "Sum should be 6";
        });

        test("averagingInt", () -> {
            double avg = Stream.of(1, 2, 3).collect(Collectors.averagingInt(Integer::intValue));
            assert avg == 2.0 : "Average should be 2";
        });

        System.out.println();
    }

    private static void testReduction() {
        System.out.println("--- Testing Reduction ---");

        test("Sum with reduce", () -> {
            int sum = List.of(1, 2, 3).stream().reduce(0, Integer::sum);
            assert sum == 6 : "Sum should be 6";
        });

        test("Max with reduce", () -> {
            int max = List.of(1, 2, 3).stream().reduce(Integer::max).orElse(0);
            assert max == 3 : "Max should be 3";
        });

        test("Min with reduce", () -> {
            int min = List.of(1, 2, 3).stream().reduce(Integer::min).orElse(0);
            assert min == 1 : "Min should be 1";
        });

        test("Product with reduce", () -> {
            int product = List.of(1, 2, 3, 4).stream().reduce(1, (a, b) -> a * b);
            assert product == 24 : "Product should be 24";
        });

        System.out.println();
    }

    private static void testOptional() {
        System.out.println("--- Testing Optional ---");

        test("Optional.of", () -> {
            Optional<String> opt = Optional.of("test");
            assert opt.get().equals("test") : "Should get value";
        });

        test("Optional.empty", () -> {
            Optional<String> opt = Optional.empty();
            assert !opt.isPresent() : "Should be empty";
        });

        test("Optional.ofNullable", () -> {
            Optional<String> opt = Optional.ofNullable(null);
            assert !opt.isPresent() : "Should be empty";
            Optional<String> opt2 = Optional.ofNullable("value");
            assert opt2.isPresent() : "Should be present";
        });

        test("Optional.orElse", () -> {
            String result = Optional.empty().orElse("default");
            assert result.equals("default") : "Should return default";
        });

        test("Optional.orElseGet", () -> {
            String result = Optional.empty().orElseGet(() -> "generated");
            assert result.equals("generated") : "Should return generated";
        });

        test("Optional.map", () -> {
            Optional<Integer> result = Optional.of("test").map(String::length);
            assert result.orElse(0) == 4 : "Should be length 4";
        });

        test("Optional.filter", () -> {
            Optional<Integer> result = Optional.of(5).filter(n -> n > 3);
            assert result.isPresent() : "Should be present";
            Optional<Integer> filtered = Optional.of(2).filter(n -> n > 3);
            assert !filtered.isPresent() : "Should not be present";
        });

        System.out.println();
    }

    private static void testPrimitiveStreams() {
        System.out.println("--- Testing Primitive Streams ---");

        test("IntStream sum", () -> {
            int sum = IntStream.of(1, 2, 3, 4, 5).sum();
            assert sum == 15 : "Sum should be 15";
        });

        test("IntStream average", () -> {
            double avg = IntStream.of(1, 2, 3).average().orElse(0);
            assert avg == 2.0 : "Average should be 2";
        });

        test("IntStream rangeClosed", () -> {
            long count = IntStream.rangeClosed(1, 5).count();
            assert count == 5 : "Count should be 5";
        });

        test("LongStream", () -> {
            long sum = LongStream.of(100, 200, 300).sum();
            assert sum == 600 : "Sum should be 600";
        });

        test("DoubleStream", () -> {
            double sum = DoubleStream.of(1.1, 2.2, 3.3).sum();
            assert Math.abs(sum - 6.6) < 0.001 : "Sum should be 6.6";
        });

        test("Boxing IntStream to Stream", () -> {
            Stream<Integer> boxed = IntStream.range(1, 4).boxed();
            assert boxed.count() == 3 : "Should have 3 elements";
        });

        System.out.println();
    }

    private static void testParallelStreams() {
        System.out.println("--- Testing Parallel Streams ---");

        test("Parallel stream creation", () -> {
            List<Integer> result = List.of(1, 2, 3).parallelStream().collect(Collectors.toList());
            assert result.size() == 3 : "Should have 3 elements";
        });

        test("Parallel stream operations", () -> {
            long count = IntStream.range(1, 1000).parallel().filter(n -> n % 2 == 0).count();
            assert count == 499 : "Should have 499 even numbers";
        });

        test("Parallel reduce", () -> {
            int sum = IntStream.range(1, 101).parallel().sum();
            assert sum == 5050 : "Sum should be 5050";
        });

        test("Parallel with collect", () -> {
            Set<Integer> result = IntStream.range(1, 10).parallel().boxed().collect(Collectors.toSet());
            assert result.size() == 9 : "Should have 9 elements";
        });

        System.out.println();
    }

    private static void test(String name, Runnable test) {
        try {
            test.run();
            System.out.println("  PASS: " + name);
            passed++;
        } catch (AssertionError | Exception e) {
            System.out.println("  FAIL: " + name + " - " + e.getMessage());
            failed++;
        }
    }

    private static void printSummary() {
        System.out.println("=== Test Summary ===");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Total: " + (passed + failed));
        System.out.println("===================");
    }
}