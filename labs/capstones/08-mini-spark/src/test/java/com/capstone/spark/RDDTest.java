package com.capstone.spark;

import org.junit.jupiter.api.*;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class RDDTest {
    private RDD<Integer> rdd;

    @BeforeEach
    void setUp() { rdd = new RDD<>(List.of(1, 2, 3, 4, 5)); }

    @Test void testMap() {
        var mapped = rdd.map(x -> x * 2);
        assertEquals(List.of(2, 4, 6, 8, 10), mapped.collect());
    }

    @Test void testFilter() {
        var filtered = rdd.filter(x -> x % 2 == 0);
        assertEquals(List.of(2, 4), filtered.collect());
    }

    @Test void testFlatMap() {
        var flat = rdd.flatMap(x -> List.of(x, x * 10));
        assertEquals(10, flat.count());
    }

    @Test void testReduce() {
        var sum = rdd.reduce(Integer::sum);
        assertTrue(sum.isPresent());
        assertEquals(15, sum.get());
    }

    @Test void testCollect() {
        assertEquals(List.of(1, 2, 3, 4, 5), rdd.collect());
    }

    @Test void testCount() {
        assertEquals(5, rdd.count());
    }

    @Test void testFirst() {
        assertEquals(1, rdd.first());
    }

    @Test void testTake() {
        assertEquals(List.of(1, 2), rdd.take(2));
    }

    @Test void testDistinct() {
        var withDups = new RDD<>(List.of(1, 1, 2, 2, 3));
        assertEquals(List.of(1, 2, 3), withDups.distinct().collect());
    }

    @Test void testUnion() {
        var other = new RDD<>(List.of(6, 7, 8));
        var union = rdd.union(other);
        assertEquals(8, union.count());
    }

    @Test void testEmptyRDD() {
        var empty = new RDD<>(List.of());
        assertEquals(0, empty.count());
        assertTrue(empty.isEmpty());
        assertThrows(NoSuchElementException.class, empty::first);
    }
}
