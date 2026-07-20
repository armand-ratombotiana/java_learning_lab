package com.capstone.spark;

import org.junit.jupiter.api.*;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PairRDDTest {
    @Test void testReduceByKey() {
        var data = Map.<String, List<Integer>>of("a", List.of(1, 2, 3), "b", List.of(4, 5));
        var pairRDD = new PairRDD<>(data, 2);
        var reduced = pairRDD.reduceByKey(Integer::sum);
        var result = reduced.collectAsMap();
        assertEquals(6, result.get("a").get(0).intValue());
        assertEquals(9, result.get("b").get(0).intValue());
    }

    @Test void testSortByKey() {
        var data = Map.<String, List<Integer>>of("c", List.of(1), "a", List.of(2), "b", List.of(3));
        var pairRDD = new PairRDD<>(data, 1);
        var sorted = pairRDD.sortByKey();
        var keys = sorted.keys();
        assertEquals(List.of("a", "b", "c"), keys.collect());
    }

    @Test void testToRDD() {
        var data = Map.<String, List<Integer>>of("x", List.of(1));
        var pairRDD = new PairRDD<>(data, 1);
        var rdd = pairRDD.toRDD();
        assertEquals(1, rdd.count());
    }

    @Test void testJoin() {
        var left = Map.<String, List<Integer>>of("k", List.of(1, 2));
        var right = Map.<String, List<String>>of("k", List.of("a", "b"));
        var leftRDD = new PairRDD<>(left, 1);
        var rightRDD = new PairRDD<>(right, 1);
        var joined = leftRDD.join(rightRDD);
        assertEquals(4, joined.count());
    }

    @Test void testValues() {
        var data = Map.<String, List<Integer>>of("a", List.of(1, 2), "b", List.of(3));
        var pairRDD = new PairRDD<>(data, 1);
        var values = pairRDD.values();
        assertEquals(List.of(1, 2, 3), values.collect());
    }
}
