package com.learning.intermediate;

import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.stream.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive tests for flatMap operations.
 */
public class FlatMapOperationsTests {
    
    @Test
    void testFlatMapBasic() {
        List<List<Integer>> nested = List.of(List.of(1, 2), List.of(3, 4));
        List<Integer> result = nested.stream()
            .flatMap(List::stream)
            .collect(Collectors.toList());
        assertThat(result).containsExactly(1, 2, 3, 4);
    }
    
    @Test
    void testFlatMapStrings() {
        List<String> words = List.of("Hello", "World");
        List<String> chars = words.stream()
            .flatMap(word -> Arrays.stream(word.split("")))
            .collect(Collectors.toList());
        assertThat(chars).contains("H", "e", "l", "l", "o");
    }
    
    @Test 
    void testFlatMapEmpty() {
        List<List<Integer>> nested = List.of(List.of(), List.of(1), List.of());
        long count = nested.stream().flatMap(List::stream).count();
        assertThat(count).isEqualTo(1);
    }
    
    @Test
    void testFlatMapTransform() {
        List<Integer> numbers = List.of(1, 2, 3);
        List<Integer> result = numbers.stream()
            .flatMapToInt(n -> IntStream.range(0, n))
            .boxed()
            .collect(Collectors.toList());
        assertThat(result).containsExactly(0, 0, 1, 0, 1, 2);
    }
}
