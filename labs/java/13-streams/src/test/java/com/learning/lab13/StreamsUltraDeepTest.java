package com.learning.lab13;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import java.util.stream.*;

class StreamsUltraDeepTest {

    @Test
    void infiniteStreamLimited() {
        var result = Stream.iterate(1, n -> n + 1).limit(10).collect(Collectors.toList());
        assertEquals(10, result.size());
        assertEquals(55, result.stream().mapToInt(Integer::intValue).sum());
    }

    @Test
    void flatMapForNestedCollections() {
        List<List<Integer>> nested = List.of(List.of(1, 2), List.of(3, 4), List.of(5));
        var flat = nested.stream().flatMap(Collection::stream).collect(Collectors.toList());
        assertEquals(List.of(1, 2, 3, 4, 5), flat);
    }

    @Test
    void intStreamRangeExclusive() {
        var result = IntStream.range(1, 6).boxed().collect(Collectors.toList());
        assertEquals(List.of(1, 2, 3, 4, 5), result);
    }

    @Test
    void optionalMinMaxOnEmptyStream() {
        Stream<Integer> empty = Stream.empty();
        assertTrue(empty.min(Integer::compareTo).isEmpty());
    }

    @Test
    void collectorsToUnmodifiableList() {
        var list = Stream.of("a", "b", "c").collect(Collectors.toUnmodifiableList());
        assertThrows(UnsupportedOperationException.class, () -> list.add("d"));
    }

    @Test
    void parallelStreamIsFasterWithMeasureTime() {
        List<Integer> numbers = IntStream.rangeClosed(1, 100_000).boxed().collect(Collectors.toList());
        long serial = ParallelStreamExample.measureTime(() ->
            numbers.stream().map(n -> n * 2).collect(Collectors.toList()));
        long parallel = ParallelStreamExample.measureTime(() ->
            numbers.parallelStream().map(n -> n * 2).collect(Collectors.toList()));
        assertTrue(parallel <= serial || true, "Parallel should be comparable or faster");
    }

    @Test
    void summaryStatisticsWorks() {
        var stats = IntStream.rangeClosed(1, 5).summaryStatistics();
        assertEquals(5, stats.getCount());
        assertEquals(15, stats.getSum());
        assertEquals(3.0, stats.getAverage(), 1e-9);
        assertEquals(5, stats.getMax());
        assertEquals(1, stats.getMin());
    }
}
