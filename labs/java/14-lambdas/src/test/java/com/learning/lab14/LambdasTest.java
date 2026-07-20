package com.learning.lab14;

import org.junit.jupiter.api.*;
import java.util.*;
import java.util.function.*;
import static org.junit.jupiter.api.Assertions.*;

class LambdasTest {

    @Test
    @DisplayName("LambdaSyntax sorting by length")
    void sortByLength() {
        Comparator<String> byLength = (a, b) -> Integer.compare(a.length(), b.length());
        List<String> words = new ArrayList<>(List.of("java", "lambda", "functional", "code"));
        words.sort(byLength);
        assertEquals(List.of("code", "java", "lambda", "functional"), words);
    }

    @Test
    @DisplayName("BiFunction lambda adds numbers")
    void biFunctionAdd() {
        BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
        assertEquals(8, add.apply(5, 3));
    }

    @Test
    @DisplayName("Predicate filters even numbers")
    void predicateFilterEven() {
        Predicate<Integer> isEven = n -> n % 2 == 0;
        List<Integer> nums = Arrays.asList(1, 2, 3, 4, 5, 6);
        List<Integer> evens = nums.stream().filter(isEven).toList();
        assertEquals(List.of(2, 4, 6), evens);
    }

    @Test
    @DisplayName("Function computes string length")
    void functionStringLength() {
        Function<String, Integer> lengthFunc = String::length;
        assertEquals(5, lengthFunc.apply("Hello"));
    }

    @Test
    @DisplayName("Consumer collects items into list")
    void consumerCollect() {
        List<String> collected = new ArrayList<>();
        Consumer<String> collector = collected::add;
        collector.accept("item1");
        collector.accept("item2");
        assertEquals(List.of("item1", "item2"), collected);
    }

    @Test
    @DisplayName("Supplier provides values")
    void supplierProvides() {
        Supplier<Double> piSupplier = () -> Math.PI;
        assertEquals(Math.PI, piSupplier.get(), 1e-9);
    }

    @Test
    @DisplayName("Method reference sorts case-insensitively")
    void methodReferenceSort() {
        List<String> names = new ArrayList<>(List.of("Charlie", "Alice", "Bob"));
        names.sort(String::compareToIgnoreCase);
        assertEquals(List.of("Alice", "Bob", "Charlie"), names);
    }

    @Test
    @DisplayName("Constructor reference creates new list")
    void constructorReference() {
        Supplier<List<String>> listFactory = ArrayList::new;
        List<String> newList = listFactory.get();
        newList.add("created");
        assertTrue(newList.contains("created"));
    }

    @Test
    @DisplayName("Variable capture with effectively final variables")
    void variableCapture() {
        String prefix = "Value: ";
        int multiplier = 2;
        Function<Integer, String> fn = n -> prefix + (n * multiplier);
        assertEquals("Value: 10", fn.apply(5));
    }

    @Test
    @DisplayName("Array-based variable capture allows mutation")
    void arrayVariableCapture() {
        int[] counter = {0};
        Runnable incrementor = () -> counter[0]++;
        incrementor.run();
        incrementor.run();
        incrementor.run();
        assertEquals(3, counter[0]);
    }
}
