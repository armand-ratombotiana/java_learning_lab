# LeetCode 63 — Unique Paths II

## Problem

A robot is located at the top-left corner of an `m x n` grid. The robot can only move **down** or **right** at any point. Some cells are **obstacles** (marked as `1`) that the robot cannot pass through.

Return the **number of possible unique paths** from the top-left corner to the bottom-right corner, avoiding obstacles.

**Constraints:**
- `1 <= m, n <= 100`
- `obstacleGrid[i][j]` is `0` or `1`

---

## Solution 1: 2D DP Table

```java
import java.util.*;

/**
 * LeetCode 63 — Unique Paths II
 * 2D DP with obstacle handling.
 *
 * Time: O(m * n) | Space: O(m * n)
 */
public class UniquePathsII {

    public int uniquePathsWithObstacles(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        if (grid[0][0] == 1 || grid[m - 1][n - 1] == 1) return 0;

        int[][] dp = new int[m][n];
        dp[0][0] = 1;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) continue;
                if (i > 0) dp[i][j] += dp[i - 1][j];
                if (j > 0) dp[i][j] += dp[i][j - 1];
            }
        }
        return dp[m - 1][n - 1];
    }

    public static void main(String[] args) {
        UniquePathsII s = new UniquePathsII();

        // Test 1: Standard obstacle
        int[][] g1 = {{0,0,0},{0,1,0},{0,0,0}};
        System.out.println("Test 1: " + s.uniquePathsWithObstacles(g1) + " (expected: 2)");

        // Test 2: No obstacles
        int[][] g2 = {{0,0},{0,0}};
        System.out.println("Test 2: " + s.uniquePathsWithObstacles(g2) + " (expected: 2)");

        // Test 3: Start blocked
        int[][] g3 = {{1,0}};
        System.out.println("Test 3: " + s.uniquePathsWithObstacles(g3) + " (expected: 0)");

        // Test 4: End blocked
        int[][] g4 = {{0,0},{0,1}};
        System.out.println("Test 4: " + s.uniquePathsWithObstacles(g4) + " (expected: 0)");

        // Test 5: Single cell
        int[][] g5 = {{0}};
        System.out.println("Test 5: " + s.uniquePathsWithObstacles(g5) + " (expected: 1)");

        // Test 6: Obstacle blocks entire path
        int[][] g6 = {{0,1},{0,0}};
        System.out.println("Test 6: " + s.uniquePathsWithObstacles(g6) + " (expected: 1)");
    }
}
```

---

## Solution 2: Space-Optimized (1D DP)

```java
import java.util.*;

/**
 * LeetCode 63 — Unique Paths II
 * Space-optimized 1D DP.
 *
 * Time: O(m * n) | Space: O(n)
 */
public class UniquePathsIIOptimized {

    public int uniquePathsWithObstacles(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        if (grid[0][0] == 1 || grid[m - 1][n - 1] == 1) return 0;

        int[] dp = new int[n];
        dp[0] = 1;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    dp[j] = 0;
                } else if (j > 0) {
                    dp[j] += dp[j - 1];
                }
            }
        }
        return dp[n - 1];
    }

    public static void main(String[] args) {
        UniquePathsIIOptimized s = new UniquePathsIIOptimized();

        int[][] g1 = {{0,0,0},{0,1,0},{0,0,0}};
        System.out.println("Test 1: " + s.uniquePathsWithObstacles(g1) + " (expected: 2)");

        int[][] g2 = {{0,0,0},{0,0,0},{0,0,0}};
        System.out.println("Test 2: " + s.uniquePathsWithObstacles(g2) + " (expected: 6)");

        int[][] g3 = {{1,0}};
        System.out.println("Test 3: " + s.uniquePathsWithObstacles(g3) + " (expected: 0)");

        int[][] g4 = {{0,1,0},{0,0,0}};
        System.out.println("Test 4: " + s.uniquePathsWithObstacles(g4) + " (expected: 1)");
    }
}
```

---

## Complexity Analysis

| Approach | Time | Space |
|----------|------|-------|
| 2D DP | O(m * n) | O(m * n) |
| 1D DP (Optimized) | O(m * n) | O(n) |

**Key Insight:** `dp[i][j] = dp[i-1][j] + dp[i][j-1]` — paths to cell `(i,j)` come from above or from the left. Obstacles reset the count to 0. The 1D version works because `dp[j]` after row `i` represents `dp[i][j]`, and `dp[j-1]` has already been updated to `dp[i][j-1]` in the current row.
