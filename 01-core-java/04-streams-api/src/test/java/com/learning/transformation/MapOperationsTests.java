package com.learning.transformation;

import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.stream.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive tests for map operations.
 */
public class MapOperationsTests {
    
    @Test
    void testMapBasic() {
        List<Integer> numbers = List.of(1, 2, 3);
        List<Integer> squared = numbers.stream()
            .map(n -> n * n)
            .collect(Collectors.toList());
        assertThat(squared).containsExactly(1, 4, 9);
    }
    
    @Test
    void testMapToString() {
        List<Integer> numbers = List.of(1, 2, 3);
        List<String> strings = numbers.stream()
            .map(Object::toString)
            .collect(Collectors.toList());
        assertThat(strings).containsExactly("1", "2", "3");
    }
    
    @Test
    void testMapChained() {
        List<String> words = List.of("Java", "Streams");
        List<String> result = words.stream()
            .map(String::toUpperCase)
            .map(s -> s + "!")
            .collect(Collectors.toList());
        assertThat(result).containsExactly("JAVA!", "STREAMS!");
    }
    
    @Test
    void testMapToInt() {
        List<String> numbers = List.of("1", "2", "3");
        int sum = numbers.stream()
            .mapToInt(Integer::parseInt)
            .sum();
        assertThat(sum).isEqualTo(6);
    }
    
    @Test
    void testMapWithFilter() {
        List<Integer> numbers = List.of(1, 2, 3, 4);
        List<Integer> result = numbers.stream()
            .filter(n -> n > 2)
            .map(n -> n * 10)
            .collect(Collectors.toList());
        assertThat(result).containsExactly(30, 40);
    }
}
