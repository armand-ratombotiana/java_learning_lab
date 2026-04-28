package com.learning.terminal;

import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.stream.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive tests for terminal operations.
 */
public class TerminalOperationsTests {
    
    @Test
    void testForEach() {
        List<Integer> numbers = List.of(1, 2, 3);
        List<Integer> collected = new ArrayList<>();
        numbers.stream().forEach(collected::add);
        assertThat(collected).containsExactly(1, 2, 3);
    }
    
    @Test
    void testCollectToList() {
        List<Integer> result = List.of(1, 2, 3).stream()
            .map(n -> n * 2)
            .collect(Collectors.toList());
        assertThat(result).containsExactly(2, 4, 6);
    }
    
    @Test
    void testCollectToSet() {
        Set<Integer> result = List.of(1, 1, 2, 2, 3).stream()
            .collect(Collectors.toSet());
        assertThat(result).contains(1, 2, 3).hasSize(3);
    }
    
    @Test
    void testAnyMatch() {
        boolean hasEven = List.of(1, 3, 5, 6).stream()
            .anyMatch(n -> n % 2 == 0);
        assertThat(hasEven).isTrue();
    }
    
    @Test
    void testAllMatch() {
        boolean allEven = List.of(2, 4, 6).stream()
            .allMatch(n -> n % 2 == 0);
        assertThat(allEven).isTrue();
    }
    
    @Test
    void testNoneMatch() {
        boolean noNegative = List.of(1, 2, 3).stream()
            .noneMatch(n -> n < 0);
        assertThat(noNegative).isTrue();
    }
    
    @Test
    void testFindFirst() {
        Optional<Integer> first = List.of(1, 2, 3).stream()
            .findFirst();
        assertThat(first).contains(1);
    }
    
    @Test
    void testFindAny() {
        Optional<Integer> any = List.of(1, 2, 3).stream()
            .findAny();
        assertThat(any).isNotEmpty();
    }
    
    @Test
    void testCount() {
        long count = List.of(1, 2, 3, 4, 5).stream()
            .filter(n -> n > 2)
            .count();
        assertThat(count).isEqualTo(3);
    }
    
    @Test
    void testReduce() {
        Optional<Integer> sum = List.of(1, 2, 3, 4).stream()
            .reduce((a, b) -> a + b);
        assertThat(sum).contains(10);
    }
}
