package com.learning.basics;

import java.util.*;
import java.util.stream.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.*;

/**
 * Test class for stream creation and sources.
 * Tests various ways to create streams from different sources.
 */
class StreamCreationTests {
    
    private Stream<Integer> stream;
    
    @BeforeEach
    void setUp() {
    }
    
    @Test
    void testCreateStreamFromList() {
        List<Integer> list = List.of(1, 2, 3, 4, 5);
        assertThat(list.stream())
            .isNotNull();
    }
    
    @Test
    void testCreateStreamFromArray() {
        int[] array = {1, 2, 3, 4, 5};
        assertThat(Arrays.stream(array).boxed().toList())
            .containsExactly(1, 2, 3, 4, 5);
    }
    
    @Test
    void testCreateStreamFromSet() {
        Set<String> set = Set.of("A", "B", "C");
        assertThat(set.stream().count())
            .isEqualTo(3);
    }
    
    @Test
    void testCreateStreamFromMap() {
        Map<String, Integer> map = Map.of("A", 1, "B", 2);
        assertThat(map.keySet().stream().count())
            .isEqualTo(2);
    }
    
    @Test
    void testStreamOf() {
        List<String> result = Stream.of("X", "Y", "Z").toList();
        assertThat(result).containsExactly("X", "Y", "Z");
    }
    
    @Test
    void testEmptyStream() {
        long count = Stream.empty().count();
        assertThat(count).isEqualTo(0);
    }
    
    @Test
    void testStreamGenerate() {
        List<String> result = Stream.generate(() -> "Item")
            .limit(3).toList();
        assertThat(result).hasSize(3)
            .allMatch(s -> s.equals("Item"));
    }
    
    @Test
    void testStreamIterate() {
        List<Integer> result = Stream.iterate(1, n -> n + 1)
            .limit(4).toList();
        assertThat(result).containsExactly(1, 2, 3, 4);
    }
    
    @Test
    void testIntStreamRange() {
        List<Integer> result = IntStream.range(1, 5).boxed().toList();
        assertThat(result).containsExactly(1, 2, 3, 4);
    }
    
    @Test
    void testIntStreamRangeClosed() {
        List<Integer> result = IntStream.rangeClosed(1, 5).boxed().toList();
        assertThat(result).containsExactly(1, 2, 3, 4, 5);
    }
    
    @Test
    void testStreamChaining() {
        List<Integer> result = List.of(1, 2, 3).stream()
            .flatMap(x -> Stream.of(x, x * 2)).toList();
        assertThat(result).containsExactly(1, 2, 2, 4, 3, 6);
    }
    
    @Test
    void testStreamNullHandling() {
        Set<String> set = Set.of("A", "B");
        assertThatNoException().isThrownBy(() -> set.stream().count());
    }
}
