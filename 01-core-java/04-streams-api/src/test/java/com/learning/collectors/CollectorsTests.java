package com.learning.collectors;

import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.stream.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive tests for Collectors framework.
 */
public class CollectorsTests {
    
    @Test
    void testCollectToList() {
        List<Integer> result = List.of(1, 2, 3).stream()
            .collect(Collectors.toList());
        assertThat(result).containsExactly(1, 2, 3);
    }
    
    @Test
    void testCollectToSet() {
        Set<Integer> result = List.of(1, 1, 2, 3).stream()
            .collect(Collectors.toSet());
        assertThat(result).hasSize(3).contains(1, 2, 3);
    }
    
    @Test
    void testCollectToMap() {
        Map<String, Integer> result = List.of("a", "bb", "ccc").stream()
            .collect(Collectors.toMap(k -> k, String::length));
        assertThat(result).containsEntry("a", 1).containsEntry("bb", 2);
    }
    
    @Test
    void testCollectJoining() {
        String result = List.of("a", "b", "c").stream()
            .collect(Collectors.joining(", "));
        assertThat(result).isEqualTo("a, b, c");
    }
    
    @Test
    void testCollectGroupingBy() {
        Map<Integer, List<String>> result = List.of("a", "bb", "ccc", "dd").stream()
            .collect(Collectors.groupingBy(String::length));
        assertThat(result).containsEntry(1, List.of("a"))
                         .containsEntry(2, List.of("bb", "dd"));
    }
    
    @Test
    void testCollectPartitioningBy() {
        Map<Boolean, List<Integer>> result = List.of(1, 2, 3, 4).stream()
            .collect(Collectors.partitioningBy(n -> n % 2 == 0));
        assertThat(result.get(true)).containsExactly(2, 4);
        assertThat(result.get(false)).containsExactly(1, 3);
    }
    
    @Test
    void testCollectSummarizing() {
        IntSummaryStatistics stats = List.of(1, 2, 3, 4, 5).stream()
            .collect(Collectors.summarizingInt(Integer::intValue));
        assertThat(stats.getSum()).isEqualTo(15);
        assertThat(stats.getAverage()).isEqualTo(3.0);
    }
    
    @Test
    void testCollectAveraging() {
        Double avg = List.of(1, 2, 3, 4).stream()
            .collect(Collectors.averagingInt(Integer::intValue));
        assertThat(avg).isEqualTo(2.5);
    }
    
    @Test
    void testCollectCounting() {
        Long count = List.of(1, 2, 3).stream()
            .collect(Collectors.counting());
        assertThat(count).isEqualTo(3);
    }
    
    @Test
    void testCollectMapping() {
        List<Integer> lengths = List.of("a", "bb", "ccc").stream()
            .collect(Collectors.mapping(String::length, Collectors.toList()));
        assertThat(lengths).containsExactly(1, 2, 3);
    }
}
