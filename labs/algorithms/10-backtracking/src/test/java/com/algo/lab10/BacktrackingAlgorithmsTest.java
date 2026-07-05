package com.algo.lab10;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class BacktrackingAlgorithmsTest {

    @Test
    void testNQueens4() {
        List<List<String>> solutions = BacktrackingAlgorithms.solveNQueens(4);
        assertEquals(2, solutions.size());
    }

    @Test
    void testNQueens1() {
        List<List<String>> solutions = BacktrackingAlgorithms.solveNQueens(1);
        assertEquals(1, solutions.size());
        assertEquals("Q", solutions.get(0).get(0));
    }

    @Test
    void testNQueens8() {
        List<List<String>> solutions = BacktrackingAlgorithms.solveNQueens(8);
        assertEquals(92, solutions.size());
    }

    @Test
    void testSudokuSolver() {
        int[][] board = {
            {5, 3, 0, 0, 7, 0, 0, 0, 0},
            {6, 0, 0, 1, 9, 5, 0, 0, 0},
            {0, 9, 8, 0, 0, 0, 0, 6, 0},
            {8, 0, 0, 0, 6, 0, 0, 0, 3},
            {4, 0, 0, 8, 0, 3, 0, 0, 1},
            {7, 0, 0, 0, 2, 0, 0, 0, 6},
            {0, 6, 0, 0, 0, 0, 2, 8, 0},
            {0, 0, 0, 4, 1, 9, 0, 0, 5},
            {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };
        assertTrue(BacktrackingAlgorithms.solveSudoku(board));
        for (int[] row : board) {
            for (int v : row) assertTrue(v >= 1 && v <= 9);
        }
    }

    @Test
    void testSudokuAlreadySolved() {
        int[][] board = {
            {5, 3, 4, 6, 7, 8, 9, 1, 2},
            {6, 7, 2, 1, 9, 5, 3, 4, 8},
            {1, 9, 8, 3, 4, 2, 5, 6, 7},
            {8, 5, 9, 7, 6, 1, 4, 2, 3},
            {4, 2, 6, 8, 5, 3, 7, 9, 1},
            {7, 1, 3, 9, 2, 4, 8, 5, 6},
            {9, 6, 1, 5, 3, 7, 2, 8, 4},
            {2, 8, 7, 4, 1, 9, 6, 3, 5},
            {3, 4, 5, 2, 8, 6, 1, 7, 9}
        };
        assertTrue(BacktrackingAlgorithms.solveSudoku(board));
    }

    @Test
    void testSubsetSum() {
        List<List<Integer>> results = BacktrackingAlgorithms.subsetSum(
            new int[]{1, 2, 3, 4, 5, 6, 7}, 10);
        assertFalse(results.isEmpty());
        for (List<Integer> subset : results) {
            assertEquals(10, subset.stream().mapToInt(Integer::intValue).sum());
        }
    }

    @Test
    void testSubsetSumNoSolution() {
        List<List<Integer>> results = BacktrackingAlgorithms.subsetSum(
            new int[]{2, 4, 6, 8}, 5);
        assertTrue(results.isEmpty());
    }

    @Test
    void testSubsetSumEmpty() {
        List<List<Integer>> results = BacktrackingAlgorithms.subsetSum(new int[]{}, 0);
        assertEquals(1, results.size());
        assertTrue(results.get(0).isEmpty());
    }

    @Test
    void testHamiltonianCycle() {
        int[][] graph = {
            {0, 1, 0, 1, 0},
            {1, 0, 1, 1, 1},
            {0, 1, 0, 0, 1},
            {1, 1, 0, 0, 1},
            {0, 1, 1, 1, 0}
        };
        List<Integer> path = BacktrackingAlgorithms.hamiltonianCycle(graph);
        assertFalse(path.isEmpty());
        assertEquals(5, path.size());
        assertTrue(path.stream().allMatch(v -> v >= 0 && v < 5));
    }

    @Test
    void testHamiltonianCycleNoSolution() {
        int[][] graph = {
            {0, 1, 0, 0},
            {1, 0, 0, 0},
            {0, 0, 0, 1},
            {0, 0, 1, 0}
        };
        List<Integer> path = BacktrackingAlgorithms.hamiltonianCycle(graph);
        assertTrue(path.isEmpty());
    }
}