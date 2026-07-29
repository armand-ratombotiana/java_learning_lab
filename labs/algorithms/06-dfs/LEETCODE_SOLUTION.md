# LeetCode 200 — Number of Islands

## Problem

Given an `m x n` 2D binary grid `grid` where `'1'` represents land and `'0'` represents water, return the number of islands.

An **island** is surrounded by water and formed by connecting adjacent cells horizontally or vertically.

**Constraints:**
- `m, n <= 300`

---

## Solution 1: DFS (Flood Fill)

```java
/**
 * LeetCode 200 — Number of Islands
 *
 * DFS flooding approach: visit each land cell and sink connected land.
 *
 * Time: O(m * n) | Space: O(m * n) worst-case recursion
 */
public class NumberOfIslandsDFS {

    public int numIslands(char[][] grid) {
        int m = grid.length, n = grid[0].length;
        int count = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == '1') {
                    count++;
                    dfs(grid, i, j);
                }
            }
        }
        return count;
    }

    private void dfs(char[][] grid, int i, int j) {
        if (i < 0 || i >= grid.length || j < 0 || j >= grid[0].length || grid[i][j] != '1')
            return;
        grid[i][j] = '0'; // sink
        dfs(grid, i - 1, j);
        dfs(grid, i + 1, j);
        dfs(grid, i, j - 1);
        dfs(grid, i, j + 1);
    }

    public static void main(String[] args) {
        NumberOfIslandsDFS s = new NumberOfIslandsDFS();

        char[][] t1 = {
            {'1','1','1','1','0'},
            {'1','1','0','1','0'},
            {'1','1','0','0','0'},
            {'0','0','0','0','0'}
        };
        System.out.println("Test 1: " + s.numIslands(t1) + " (expected: 1)");

        char[][] t2 = {
            {'1','1','0','0','0'},
            {'1','1','0','0','0'},
            {'0','0','1','0','0'},
            {'0','0','0','1','1'}
        };
        System.out.println("Test 2: " + s.numIslands(t2) + " (expected: 3)");

        char[][] t3 = {{'1'}};
        System.out.println("Test 3: " + s.numIslands(t3) + " (expected: 1)");

        char[][] t4 = {{'0'}};
        System.out.println("Test 4: " + s.numIslands(t4) + " (expected: 0)");
    }
}
```

---

## Solution 2: Union-Find (Disjoint Set Union)

```java
/**
 * LeetCode 200 — Number of Islands
 *
 * Union-Find alternative: connect adjacent land cells and count roots.
 *
 * Time: O(m * n * α(m*n)) | Space: O(m * n)
 */
public class NumberOfIslandsUnionFind {

    public int numIslands(char[][] grid) {
        int m = grid.length, n = grid[0].length;
        UnionFind uf = new UnionFind(m * n);
        int ones = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == '1') {
                    ones++;
                    int idx = i * n + j;
                    // Union with top and left neighbors only
                    if (i > 0 && grid[i - 1][j] == '1')
                        uf.union(idx, (i - 1) * n + j);
                    if (j > 0 && grid[i][j - 1] == '1')
                        uf.union(idx, i * n + (j - 1));
                }
            }
        }

        return uf.getCount(ones);
    }

    static class UnionFind {
        int[] parent;
        int[] rank;

        UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) parent[i] = i;
        }

        int find(int x) {
            if (parent[x] != x)
                parent[x] = find(parent[x]);
            return parent[x];
        }

        void union(int x, int y) {
            int rx = find(x), ry = find(y);
            if (rx == ry) return;
            if (rank[rx] < rank[ry]) {
                parent[rx] = ry;
            } else if (rank[rx] > rank[ry]) {
                parent[ry] = rx;
            } else {
                parent[ry] = rx;
                rank[rx]++;
            }
        }

        int getCount(int totalOnes) {
            int components = 0;
            for (int i = 0; i < parent.length; i++)
                if (parent[i] == i) components++;
            // Subtract water cells that are their own parent
            return components - (parent.length - totalOnes);
        }
    }

    public static void main(String[] args) {
        NumberOfIslandsUnionFind s = new NumberOfIslandsUnionFind();

        char[][] t1 = {
            {'1','1','1','1','0'},
            {'1','1','0','1','0'},
            {'1','1','0','0','0'},
            {'0','0','0','0','0'}
        };
        System.out.println("Test 1: " + s.numIslands(t1) + " (expected: 1)");

        char[][] t2 = {
            {'1','1','0','0','0'},
            {'1','1','0','0','0'},
            {'0','0','1','0','0'},
            {'0','0','0','1','1'}
        };
        System.out.println("Test 2: " + s.numIslands(t2) + " (expected: 3)");
    }
}
```

## Complexity Comparison

| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| DFS | O(m * n) | O(m * n) | Simple, recursive, may overflow stack |
| Union-Find | O(m * n * α(m*n)) | O(m * n) | Iterative, near-constant amortized union |

Both are optimal O(m * n). DFS is simpler; Union-Find avoids recursion depth issues.