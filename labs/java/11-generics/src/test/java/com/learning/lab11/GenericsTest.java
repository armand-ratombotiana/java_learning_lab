package com.learning.lab11;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class GenericsTest {

    @Test
    @DisplayName("GenericMethodExample.getFirst returns first element")
    void getFirstReturnsFirstElement() {
        Integer[] nums = {10, 20, 30};
        assertEquals(10, GenericMethodExample.getFirst(nums));
        String[] names = {"Alice", "Bob"};
        assertEquals("Alice", GenericMethodExample.getFirst(names));
    }

    @Test
    @DisplayName("GenericMethodExample.getFirst returns null for empty array")
    void getFirstEmptyArray() {
        String[] empty = {};
        assertNull(GenericMethodExample.getFirst(empty));
    }

    @Test
    @DisplayName("GenericMethodExample.average computes correct average")
    void averageComputesCorrectly() {
        List<Integer> ints = List.of(2, 4, 6);
        assertEquals(4.0, GenericMethodExample.average(ints), 1e-9);
        List<Double> doubles = List.of(1.0, 2.0, 3.0);
        assertEquals(2.0, GenericMethodExample.average(doubles), 1e-9);
    }

    @Test
    @DisplayName("GenericMethodExample.max returns maximum")
    void maxReturnsMaximum() {
        assertEquals(20, GenericMethodExample.max(10, 20, 15));
        assertEquals("cherry", GenericMethodExample.max("apple", "banana", "cherry"));
    }

    @Test
    @DisplayName("WildcardExample.sum computes sum of numbers")
    void sumComputesTotal() {
        List<Integer> ints = List.of(1, 2, 3);
        assertEquals(6.0, WildcardExample.sum(ints), 1e-9);
        List<Double> doubles = List.of(1.5, 2.5);
        assertEquals(4.0, WildcardExample.sum(doubles), 1e-9);
    }

    @Test
    @DisplayName("GenericClass Box stores and retrieves values")
    void boxStoresAndRetrieves() {
        Box<String> stringBox = new Box<>("test");
        assertEquals("test", stringBox.get());
        stringBox.set("updated");
        assertEquals("updated", stringBox.get());
    }

    @Test
    @DisplayName("GenericClass Pair stores key-value pairs")
    void pairStoresKeyValue() {
        Pair<String, Integer> pair = new Pair<>("age", 30);
        assertEquals("age", pair.getKey());
        assertEquals(30, pair.getValue());
    }

    @Test
    @DisplayName("GenericInterface Transformer works with lambdas")
    void transformerWorks() {
        Transformer<String, Integer> lengthFn = s -> s.length();
        assertEquals(5, lengthFn.transform("Hello"));
        Transformer<Integer, String> toString = Object::toString;
        assertEquals("42", toString.transform(42));
    }
}
