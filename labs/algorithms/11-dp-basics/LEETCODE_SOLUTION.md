# LeetCode 70 — Climbing Stairs

## Problem

You are climbing a staircase. It takes `n` steps to reach the top. Each time you can either climb 1 or 2 steps. In how many distinct ways can you climb to the top?

**Constraints:**
- `1 <= n <= 45`

---

## Solution 1: Top-Down DP (Memoization)

```java
import java.util.*;

/**
 * LeetCode 70 — Climbing Stairs
 * Top-down DP with memoization.
 *
 * Time: O(n) | Space: O(n)
 */
public class ClimbingStairsMemo {

    public int climbStairs(int n) {
        int[] memo = new int[n + 1];
        return dfs(n, memo);
    }

    private int dfs(int i, int[] memo) {
        if (i <= 1) return 1;
        if (memo[i] != 0) return memo[i];
        memo[i] = dfs(i - 1, memo) + dfs(i - 2, memo);
        return memo[i];
    }

    public static void main(String[] args) {
        ClimbingStairsMemo s = new ClimbingStairsMemo();

        System.out.println("Test 1 (n=2): " + s.climbStairs(2) + " (expected: 2)");
        System.out.println("Test 2 (n=3): " + s.climbStairs(3) + " (expected: 3)");
        System.out.println("Test 3 (n=5): " + s.climbStairs(5) + " (expected: 8)");
        System.out.println("Test 4 (n=1): " + s.climbStairs(1) + " (expected: 1)");
        System.out.println("Test 5 (n=45): " + s.climbStairs(45) + " (expected: 1836311903)");
    }
}
```

---

## Solution 2: Bottom-Up Tabulation

```java
import java.util.*;

/**
 * LeetCode 70 — Climbing Stairs
 * Bottom-up DP with O(n) space.
 *
 * Time: O(n) | Space: O(n)
 */
public class ClimbingStairsTabulation {

    public int climbStairs(int n) {
        if (n <= 1) return 1;
        int[] dp = new int[n + 1];
        dp[0] = 1;
        dp[1] = 1;
        for (int i = 2; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        return dp[n];
    }

    public static void main(String[] args) {
        ClimbingStairsTabulation s = new ClimbingStairsTabulation();

        System.out.println("Test 1 (n=2): " + s.climbStairs(2) + " (expected: 2)");
        System.out.println("Test 2 (n=3): " + s.climbStairs(3) + " (expected: 3)");
        System.out.println("Test 3 (n=5): " + s.climbStairs(5) + " (expected: 8)");
        System.out.println("Test 4 (n=10): " + s.climbStairs(10) + " (expected: 89)");
        System.out.println("Test 5 (n=45): " + s.climbStairs(45) + " (expected: 1836311903)");
    }
}
```

---

## Solution 3: Space-Optimized DP (O(1) Space)

```java
import java.util.*;

/**
 * LeetCode 70 — Climbing Stairs
 * Space-optimized DP — only track the last two values.
 *
 * Time: O(n) | Space: O(1)
 */
public class ClimbingStairsOptimized {

    public int climbStairs(int n) {
        if (n <= 1) return 1;
        int prev = 1, curr = 1;
        for (int i = 2; i <= n; i++) {
            int next = prev + curr;
            prev = curr;
            curr = next;
        }
        return curr;
    }

    public static void main(String[] args) {
        ClimbingStairsOptimized s = new ClimbingStairsOptimized();

        System.out.println("Test 1 (n=2): " + s.climbStairs(2) + " (expected: 2)");
        System.out.println("Test 2 (n=3): " + s.climbStairs(3) + " (expected: 3)");
        System.out.println("Test 3 (n=4): " + s.climbStairs(4) + " (expected: 5)");
        System.out.println("Test 4 (n=10): " + s.climbStairs(10) + " (expected: 89)");
        System.out.println("Test 5 (n=45): " + s.climbStairs(45) + " (expected: 1836311903)");

        // Edge case
        System.out.println("Test 6 (n=1): " + s.climbStairs(1) + " (expected: 1)");
    }
}
```

---

## Complexity Analysis

| Approach | Time | Space |
|----------|------|-------|
| Top-Down Memoization | O(n) | O(n) |
| Bottom-Up Tabulation | O(n) | O(n) |
| Space-Optimized | O(n) | O(1) |

**Key Insight:** This is the Fibonacci sequence in disguise. `dp[i] = dp[i-1] + dp[i-2]` because from step `i` you could have come from `i-1` (one step) or `i-2` (two steps). The space-optimized version achieves O(1) by only keeping the last two computed values since the recurrence relation only depends on `i-1` and `i-2`.
