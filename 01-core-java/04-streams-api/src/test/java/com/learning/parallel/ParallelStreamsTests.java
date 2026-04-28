package com.learning.parallel;

import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.stream.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive tests for parallel streams.
 */
public class ParallelStreamsTests {
    
    @Test
    void testParallelStreamExecution() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);
        List<Integer> result = numbers.parallelStream()
            .map(n -> n * 2)
            .collect(Collectors.toList());
        assertThat(result).contains(2, 4, 6, 8, 10);
    }
    
    @Test
    void testParallelVsSequential() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);
        long seqResult = numbers.stream().filter(n -> n > 2).count();
        long parResult = numbers.parallelStream().filter(n -> n > 2).count();
        assertThat(seqResult).isEqualTo(parResult).isEqualTo(3);
    }
    
    @Test
    void testParallelWithOrdering() {
        List<Integer> result = List.of(5, 3, 1, 4, 2).parallelStream()
            .sorted()
            .collect(Collectors.toList());
        assertThat(result).containsExactly(1, 2, 3, 4, 5);
    }
    
    @Test
    void testParallelWithGrouping() {
        Map<Boolean, List<Integer>> result = List.of(1, 2, 3, 4, 5).parallelStream()
            .collect(Collectors.partitioningBy(n -> n % 2 == 0));
        assertThat(result.get(true)).contains(2, 4);
        assertThat(result.get(false)).contains(1, 3, 5);
    }
    
    @Test
    void testParallelReduce() {
        Integer sum = List.of(1, 2, 3, 4).parallelStream()
            .reduce(0, Integer::sum, Integer::sum);
        assertThat(sum).isEqualTo(10);
    }
    
    @Test
    void testParallelFlatMap() {
        List<Integer> result = List.of(List.of(1, 2), List.of(3, 4)).parallelStream()
            .flatMap(List::stream)
            .collect(Collectors.toList());
        assertThat(result).contains(1, 2, 3, 4);
    }
}
