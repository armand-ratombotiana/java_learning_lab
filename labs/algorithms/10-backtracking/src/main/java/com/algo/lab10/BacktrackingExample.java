package com.algo.lab10;

import java.util.List;

public class BacktrackingExample {
    public static void main(String[] args) {
        System.out.println("=== Backtracking Demo ===\n");

        System.out.println("--- N-Queens (4x4) ---");
        List<List<String>> solutions = BacktrackingAlgorithms.solveNQueens(4);
        System.out.println("Number of solutions: " + solutions.size());
        for (List<String> solution : solutions) {
            solution.forEach(System.out::println);
            System.out.println();
        }

        System.out.println("--- Sudoku Solver ---");
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
        boolean solved = BacktrackingAlgorithms.solveSudoku(board);
        System.out.println("Solved: " + solved);
        if (solved) {
            for (int[] row : board) {
                for (int v : row) System.out.print(v + " ");
                System.out.println();
            }
        }

        System.out.println("\n--- Subset Sum (target=10) ---");
        int[] arr = {1, 2, 3, 4, 5, 6, 7};
        List<List<Integer>> subsets = BacktrackingAlgorithms.subsetSum(arr, 10);
        System.out.println("Subsets that sum to 10:");
        subsets.forEach(System.out::println);

        System.out.println("\n--- Hamiltonian Cycle ---");
        int[][] graph = {
            {0, 1, 0, 1, 0},
            {1, 0, 1, 1, 1},
            {0, 1, 0, 0, 1},
            {1, 1, 0, 0, 1},
            {0, 1, 1, 1, 0}
        };
        List<Integer> path = BacktrackingAlgorithms.hamiltonianCycle(graph);
        System.out.println("Hamiltonian cycle: " + (path.isEmpty() ? "None" : path));
    }
}