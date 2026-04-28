package com.learning.basics;

import java.util.*;
import java.util.stream.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.*;

/**
 * Test class for filter operations.
 * Tests filter operation with various predicates and edge cases.
 */
class FilterOperationsTests {
    
    private List<Integer> numbers;
    
    @BeforeEach
    void setUp() {
        numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }
    
    @Test
    void testSimpleFilter() {
        List<Integer> result = numbers.stream()
            .filter(n -> n > 5).toList();
        assertThat(result).containsExactly(6, 7, 8, 9, 10);
    }
    
    @Test
    void testFilterAll() {
        List<Integer> result = numbers.stream()
            .filter(n -> n > 0).toList();
        assertThat(result).hasSize(10);
    }
    
    @Test
    void testFilterNone() {
        List<Integer> result = numbers.stream()
            .filter(n -> n > 100).toList();
        assertThat(result).isEmpty();
    }
    
    @Test
    void testChainedFilters() {
        List<Integer> result = numbers.stream()
            .filter(n -> n > 2)
            .filter(n -> n < 8)
            .filter(n -> n % 2 == 0).toList();
        assertThat(result).containsExactly(4, 6);
    }
    
    @Test
    void testFilterWithNull() {
        List<String> strings = new ArrayList<>(Arrays.asList("A", null, "B", "C"));
        assertThatThrownBy(() -> strings.stream()
            .filter(s -> s.length() > 0).count())
            .isInstanceOf(NullPointerException.class);
    }
    
    @Test
    void testFilterDistinct() {
        List<Integer> duplicates = List.of(1, 2, 2, 3, 3, 3);
        List<Integer> result = duplicates.stream()
            .filter(n -> n > 1).toList();
        assertThat(result).containsExactly(2, 2, 3, 3, 3);
    }
    
    @Test
    void testFilterEmpty() {
        List<Integer> empty = List.of();
        List<Integer> result = empty.stream()
            .filter(n -> n > 0).toList();
        assertThat(result).isEmpty();
    }
    
    @Test
    void testFilterEvenNumbers() {
        List<Integer> result = numbers.stream()
            .filter(n -> n % 2 == 0).toList();
        assertThat(result).containsExactly(2, 4, 6, 8, 10);
    }
    
    @Test
    void testFilterStringLength() {
        List<String> words = List.of("a", "bb", "ccc", "dddd");
        List<String> result = words.stream()
            .filter(s -> s.length() > 2).toList();
        assertThat(result).containsExactly("ccc", "dddd");
    }
    
    @Test
    void testFilterWithComplexPredicate() {
        List<Integer> result = numbers.stream()
            .filter(n -> n > 3 && n < 8 && n % 2 == 0).toList();
        assertThat(result).containsExactly(4, 6);
    }
    
    @Test
    void testNegateFilter() {
        List<Integer> result = numbers.stream()
            .filter(((java.util.function.Predicate<Integer>) (n -> n > 5)).negate())
            .toList();
        assertThat(result).containsExactly(1, 2, 3, 4, 5);
    }
}
