package com.algo.lab14;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class ApproximationAlgorithmsTest {

    @Test
    void testVertexCoverApprox() {
        int[][] graph = {
            {0, 1, 1, 0, 0},
            {1, 0, 1, 1, 0},
            {1, 1, 0, 0, 1},
            {0, 1, 0, 0, 1},
            {0, 0, 1, 1, 0}
        };
        Set<Integer> cover = ApproximationAlgorithms.vertexCoverApprox(graph);
        assertFalse(cover.isEmpty());
        for (int u = 0; u < graph.length; u++) {
            for (int v = 0; v < graph.length; v++) {
                if (graph[u][v] == 1) {
                    assertTrue(cover.contains(u) || cover.contains(v));
                }
            }
        }
    }

    @Test
    void testVertexCoverApproxSimple() {
        int[][] graph = {{0, 1}, {1, 0}};
        Set<Integer> cover = ApproximationAlgorithms.vertexCoverApprox(graph);
        assertEquals(2, cover.size());
    }

    @Test
    void testTspApprox() {
        int[][] graph = {
            {0, 10, 15, 20},
            {10, 0, 35, 25},
            {15, 35, 0, 30},
            {20, 25, 30, 0}
        };
        List<Integer> tour = ApproximationAlgorithms.tspApprox(graph);
        assertFalse(tour.isEmpty());
        assertEquals(5, tour.size());
        assertEquals(0, tour.get(0).intValue());
        assertEquals(0, tour.get(tour.size() - 1).intValue());
    }

    @Test
    void testSetCoverGreedy() {
        Set<Integer> universe = new HashSet<>(Set.of(1, 2, 3, 4, 5));
        List<Set<Integer>> sets = new ArrayList<>();
        sets.add(new HashSet<>(Set.of(1, 2)));
        sets.add(new HashSet<>(Set.of(2, 3, 4)));
        sets.add(new HashSet<>(Set.of(4, 5)));
        Set<Integer> cover = ApproximationAlgorithms.setCoverGreedy(universe, sets);
        Set<Integer> covered = new HashSet<>();
        for (int idx : cover) covered.addAll(sets.get(idx));
        assertEquals(universe, covered);
    }

    @Test
    void testSetCoverGreedyAllElements() {
        Set<Integer> universe = new HashSet<>(Set.of(1, 2, 3));
        List<Set<Integer>> sets = new ArrayList<>();
        sets.add(new HashSet<>(Set.of(1, 2, 3)));
        Set<Integer> cover = ApproximationAlgorithms.setCoverGreedy(universe, sets);
        assertEquals(1, cover.size());
    }
}