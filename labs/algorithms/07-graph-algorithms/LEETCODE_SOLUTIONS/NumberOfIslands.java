package com.algorithms.graph;

/**
 * LeetCode 200: Number of Islands
 * https://leetcode.com/problems/number-of-islands/
 *
 * Count the number of islands in a 2D grid (1 = land, 0 = water).
 *
 * Time Complexity: O(M * N)
 * Space Complexity: O(M * N) worst case for DFS stack
 */
public class NumberOfIslands {

    public int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0) return 0;
        int count = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == '1') {
                    count++;
                    dfs(grid, i, j);
                }
            }
        }
        return count;
    }

    private void dfs(char[][] grid, int i, int j) {
        if (i < 0 || i >= grid.length || j < 0 || j >= grid[0].length || grid[i][j] != '1') return;
        grid[i][j] = '0';
        dfs(grid, i - 1, j);
        dfs(grid, i + 1, j);
        dfs(grid, i, j - 1);
        dfs(grid, i, j + 1);
    }

    public static void main(String[] args) {
        NumberOfIslands noi = new NumberOfIslands();
        char[][] g1 = {
            { '1', '1', '1', '1', '0' },
            { '1', '1', '0', '1', '0' },
            { '1', '1', '0', '0', '0' },
            { '0', '0', '0', '0', '0' }
        };
        System.out.println("Test 1: " + noi.numIslands(g1) + " (expected: 1)");

        char[][] g2 = {
            { '1', '1', '0', '0', '0' },
            { '1', '1', '0', '0', '0' },
            { '0', '0', '1', '0', '0' },
            { '0', '0', '0', '1', '1' }
        };
        System.out.println("Test 2: " + noi.numIslands(g2) + " (expected: 3)");
    }
}
