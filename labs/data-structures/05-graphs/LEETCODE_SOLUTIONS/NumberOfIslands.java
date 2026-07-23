package com.leetcode.graphs;

/**
 * LeetCode 200: Number of Islands
 * https://leetcode.com/problems/number-of-islands/
 *
 * Given a 2D grid of '1's (land) and '0's (water), count the number of islands.
 * An island is surrounded by water and formed by connecting adjacent lands.
 *
 * Time Complexity: O(M * N)
 * Space Complexity: O(M * N) worst case for recursion stack
 */
public class NumberOfIslands {

    /**
     * Approach 1 (Optimal): DFS
     * When you find a '1', DFS to mark the entire island as visited.
     * Time: O(M * N), Space: O(M * N)
     */
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
        grid[i][j] = '0'; // mark as visited
        dfs(grid, i - 1, j);
        dfs(grid, i + 1, j);
        dfs(grid, i, j - 1);
        dfs(grid, i, j + 1);
    }

    /**
     * Approach 2: BFS using queue
     * Same complexity but iterative.
     */

    public static void main(String[] args) {
        NumberOfIslands noi = new NumberOfIslands();

        char[][] grid1 = {
            { '1', '1', '1', '1', '0' },
            { '1', '1', '0', '1', '0' },
            { '1', '1', '0', '0', '0' },
            { '0', '0', '0', '0', '0' }
        };
        System.out.println("Test 1: " + noi.numIslands(grid1) + " (expected: 1)");

        char[][] grid2 = {
            { '1', '1', '0', '0', '0' },
            { '1', '1', '0', '0', '0' },
            { '0', '0', '1', '0', '0' },
            { '0', '0', '0', '1', '1' }
        };
        System.out.println("Test 2: " + noi.numIslands(grid2) + " (expected: 3)");

        char[][] grid3 = {{}};
        System.out.println("Test 3 (empty): " + noi.numIslands(grid3) + " (expected: 0)");
    }
}
