package com.algorithms.backtracking;

import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode 51: N-Queens
 * https://leetcode.com/problems/n-queens/
 *
 * Place N queens on an N x N chessboard so no two queens attack each other.
 *
 * Time Complexity: O(N!)
 * Space Complexity: O(N^2)
 */
public class NQueens {

    public List<List<String>> solveNQueens(int n) {
        List<List<String>> result = new ArrayList<>();
        char[][] board = new char[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                board[i][j] = '.';
        backtrack(result, board, 0, n);
        return result;
    }

    private void backtrack(List<List<String>> result, char[][] board, int row, int n) {
        if (row == n) {
            List<String> solution = new ArrayList<>();
            for (char[] r : board) solution.add(new String(r));
            result.add(solution);
            return;
        }
        for (int col = 0; col < n; col++) {
            if (isValid(board, row, col, n)) {
                board[row][col] = 'Q';
                backtrack(result, board, row + 1, n);
                board[row][col] = '.';
            }
        }
    }

    private boolean isValid(char[][] board, int row, int col, int n) {
        for (int i = 0; i < row; i++) {
            if (board[i][col] == 'Q') return false;
            int diag1 = col - (row - i);
            int diag2 = col + (row - i);
            if (diag1 >= 0 && board[i][diag1] == 'Q') return false;
            if (diag2 < n && board[i][diag2] == 'Q') return false;
        }
        return true;
    }

    public static void main(String[] args) {
        NQueens nq = new NQueens();
        List<List<String>> r1 = nq.solveNQueens(4);
        System.out.println("4-Queens solutions: " + r1.size() + " (expected: 2)");
        for (List<String> sol : r1) {
            for (String row : sol) System.out.println("  " + row);
            System.out.println();
        }
        List<List<String>> r2 = nq.solveNQueens(1);
        System.out.println("1-Queen solutions: " + r2.size() + " (expected: 1)");
        List<List<String>> r3 = nq.solveNQueens(2);
        System.out.println("2-Queens solutions: " + r3.size() + " (expected: 0)");
    }
}
