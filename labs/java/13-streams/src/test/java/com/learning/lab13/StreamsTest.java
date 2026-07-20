package com.learning.lab13;

import org.junit.jupiter.api.*;
import java.util.*;
import java.util.stream.*;
import static org.junit.jupiter.api.Assertions.*;

class StreamsTest {

    @Test
    @DisplayName("StreamReduceExample sum reduces correctly")
    void reduceSum() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);
        int sum = numbers.stream().reduce(0, Integer::sum);
        assertEquals(15, sum);
    }

    @Test
    @DisplayName("StreamReduceExample product reduces correctly")
    void reduceProduct() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);
        int product = numbers.stream().reduce(1, (a, b) -> a * b);
        assertEquals(120, product);
    }

    @Test
    @DisplayName("StreamReduceExample count works")
    void streamCount() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);
        assertEquals(5, numbers.stream().count());
    }

    @Test
    @DisplayName("Stream pipeline filter-map-sorted-collect")
    void streamPipeline() {
        var result = Stream.of("banana", "apple", "cherry", "date", "grape")
            .filter(s -> s.length() >= 5)
            .map(String::toUpperCase)
            .sorted()
            .collect(Collectors.toList());
        assertEquals(List.of("APPLE", "BANANA", "CHERRY", "GRAPE"), result);
    }

    @Test
    @DisplayName("Stream.iterate generates expected sequence")
    void streamIterate() {
        var evens = Stream.iterate(0, n -> n + 2).limit(5).collect(Collectors.toList());
        assertEquals(List.of(0, 2, 4, 6, 8), evens);
    }

    @Test
    @DisplayName("Collectors groupingBy groups correctly")
    void groupingByCollector() {
        var people = List.of(
            new Person("Alice", "IT", 50000),
            new Person("Bob", "HR", 45000),
            new Person("Charlie", "IT", 60000)
        );
        var byDept = people.stream().collect(Collectors.groupingBy(Person::dept));
        assertEquals(2, byDept.get("IT").size());
        assertEquals(1, byDept.get("HR").size());
    }

    @Test
    @DisplayName("Collectors partitioningBy partitions correctly")
    void partitioningByCollector() {
        var people = List.of(
            new Person("Alice", "IT", 50000),
            new Person("Bob", "HR", 45000),
            new Person("Charlie", "IT", 55000)
        );
        var highEarners = people.stream()
            .collect(Collectors.partitioningBy(p -> p.salary() > 50000));
        assertTrue(highEarners.get(true).stream().anyMatch(p -> p.name().equals("Charlie")));
    }

    @Test
    @DisplayName("Collectors joining concatenates strings")
    void joiningCollector() {
        var people = List.of(
            new Person("Alice", "IT", 50000),
            new Person("Bob", "HR", 45000)
        );
        String joined = people.stream().map(Person::name).collect(Collectors.joining(", "));
        assertEquals("Alice, Bob", joined);
    }

    @Test
    @DisplayName("ParallelStream count works correctly")
    void parallelStreamCount() {
        long count = LongStream.rangeClosed(1, 1000).parallel().filter(n -> n % 2 == 0).count();
        assertEquals(500, count);
    }

    @Test
    @DisplayName("Parallel stream filter-map-sorted produces correct result")
    void parallelStreamPipeline() {
        var result = IntStream.rangeClosed(1, 20).boxed()
            .parallelStream()
            .filter(n -> n % 3 == 0)
            .map(n -> n * n)
            .sorted()
            .collect(Collectors.toList());
        assertEquals(List.of(9, 36, 81, 144, 225, 324), result);
    }
}
