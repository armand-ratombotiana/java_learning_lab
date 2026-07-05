package com.algo.lab10;

import java.util.*;

/**
 * Backtracking algorithms.
 *
 * N-Queens: O(n!) time, O(n) space
 * Sudoku Solver: O(9^(n*n)) time, O(n*n) space
 * Subset Sum: O(2^n) time, O(n) space
 * Hamiltonian Cycle: O(n!) time, O(n) space
 */
public class BacktrackingAlgorithms {

    private BacktrackingAlgorithms() {}

    public static List<List<String>> solveNQueens(int n) {
        List<List<String>> results = new ArrayList<>();
        char[][] board = new char[n][n];
        for (char[] row : board) Arrays.fill(row, '.');
        solveNQueens(board, 0, results);
        return results;
    }

    private static void solveNQueens(char[][] board, int col, List<List<String>> results) {
        int n = board.length;
        if (col == n) {
            List<String> solution = new ArrayList<>();
            for (char[] row : board) solution.add(new String(row));
            results.add(solution);
            return;
        }
        for (int row = 0; row < n; row++) {
            if (isSafe(board, row, col)) {
                board[row][col] = 'Q';
                solveNQueens(board, col + 1, results);
                board[row][col] = '.';
            }
        }
    }

    private static boolean isSafe(char[][] board, int row, int col) {
        int n = board.length;
        for (int i = 0; i < col; i++) if (board[row][i] == 'Q') return false;
        for (int i = row, j = col; i >= 0 && j >= 0; i--, j--) if (board[i][j] == 'Q') return false;
        for (int i = row, j = col; i < n && j >= 0; i++, j--) if (board[i][j] == 'Q') return false;
        return true;
    }

    public static boolean solveSudoku(int[][] board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == 0) {
                    for (int num = 1; num <= 9; num++) {
                        if (isSudokuSafe(board, row, col, num)) {
                            board[row][col] = num;
                            if (solveSudoku(board)) return true;
                            board[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isSudokuSafe(int[][] board, int row, int col, int num) {
        for (int x = 0; x < 9; x++) {
            if (board[row][x] == num) return false;
            if (board[x][col] == num) return false;
        }
        int boxRow = row - row % 3, boxCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[boxRow + i][boxCol + j] == num) return false;
            }
        }
        return true;
    }

    public static List<List<Integer>> subsetSum(int[] arr, int target) {
        List<List<Integer>> results = new ArrayList<>();
        subsetSum(arr, target, 0, new ArrayList<>(), results);
        return results;
    }

    private static void subsetSum(int[] arr, int remaining, int idx, List<Integer> current, List<List<Integer>> results) {
        if (remaining == 0) {
            results.add(new ArrayList<>(current));
            return;
        }
        if (idx >= arr.length || remaining < 0) return;
        subsetSum(arr, remaining, idx + 1, current, results);
        current.add(arr[idx]);
        subsetSum(arr, remaining - arr[idx], idx + 1, current, results);
        current.remove(current.size() - 1);
    }

    public static List<Integer> hamiltonianCycle(int[][] graph) {
        int n = graph.length;
        List<Integer> path = new ArrayList<>(Collections.nCopies(n, -1));
        path.set(0, 0);
        if (hamiltonianUtil(graph, path, 1)) {
            return path;
        }
        return List.of();
    }

    private static boolean hamiltonianUtil(int[][] graph, List<Integer> path, int pos) {
        int n = graph.length;
        if (pos == n) {
            return graph[path.get(pos - 1)][path.get(0)] == 1;
        }
        for (int v = 1; v < n; v++) {
            if (isSafe(v, graph, path, pos)) {
                path.set(pos, v);
                if (hamiltonianUtil(graph, path, pos + 1)) return true;
                path.set(pos, -1);
            }
        }
        return false;
    }

    private static boolean isSafe(int v, int[][] graph, List<Integer> path, int pos) {
        if (graph[path.get(pos - 1)][v] == 0) return false;
        for (int i = 0; i < pos; i++) if (path.get(i) == v) return false;
        return true;
    }
}