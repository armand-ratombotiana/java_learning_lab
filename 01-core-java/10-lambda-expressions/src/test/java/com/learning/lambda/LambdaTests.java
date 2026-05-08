package com.learning.lambda;

import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.function.*;

import static org.junit.jupiter.api.Assertions.*;

class LambdaTests {

    @Test
    void testBasicLambda() {
        Runnable r = () -> System.out.println("Hello");
        assertNotNull(r);
    }

    @Test
    void testFunctionInterface() {
        MathOperation add = (a, b) -> a + b;
        assertEquals(15, add.operate(10, 5));

        MathOperation sub = (a, b) -> a - b;
        assertEquals(5, sub.operate(10, 5));
    }

    @Test
    void testBuiltInFunctionalInterfaces() {
        Predicate<String> isEmpty = String::isEmpty;
        assertTrue(isEmpty.test(""));

        Function<String, Integer> length = String::length;
        assertEquals(5, length.apply("Hello"));

        Consumer<String> consumer = s -> { };
        assertNotNull(consumer);

        Supplier<String> supplier = () -> "test";
        assertEquals("test", supplier.get());
    }

    @Test
    void testMethodReferences() {
        Function<String, Integer> parser = Integer::parseInt;
        assertEquals(42, parser.apply("42"));

        Supplier<ArrayList<String>> creator = ArrayList::new;
        assertNotNull(creator.get());
    }

    @Test
    void testStreamOperations() {
        List<Integer> nums = List.of(1, 2, 3, 4, 5);
        List<Integer> doubled = nums.stream()
            .map(n -> n * 2)
            .collect(java.util.stream.Collectors.toList());
        assertEquals(List.of(2, 4, 6, 8, 10), doubled);

        List<Integer> filtered = nums.stream()
            .filter(n -> n > 2)
            .collect(java.util.stream.Collectors.toList());
        assertEquals(List.of(3, 4, 5), filtered);
    }

    @Test
    void testFunctionComposition() {
        Function<Integer, Integer> add5 = n -> n + 5;
        Function<Integer, Integer> square = n -> n * n;

        Function<Integer, Integer> composed = square.compose(add5);
        assertEquals(64, composed.apply(3)); // (3+5)^2 = 64
    }

    @Test
    void testCollectionOperations() {
        List<String> names = List.of("Alice", "Bob", "Charlie");
        List<String> sorted = names.stream()
            .sorted(Comparator.comparingInt(String::length))
            .collect(java.util.stream.Collectors.toList());

        assertEquals("Bob", sorted.get(0)); // shortest
    }
}