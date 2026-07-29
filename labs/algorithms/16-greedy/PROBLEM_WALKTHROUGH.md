# LeetCode 45 — Jump Game II — Problem Walkthrough

## Problem Statement

You are given a **0-indexed** array `nums` of length `n`. You are initially at index `0`. Each element `nums[i]` represents the maximum jump length from that position.

Return the **minimum number of jumps** to reach `nums[n - 1]`. The test cases are generated such that you can always reach the last index.

**Constraints:**
- `1 <= nums.length <= 10^4`
- `0 <= nums[i] <= 1000`

**Examples:**
```
Input:  nums = [2, 3, 1, 1, 4]
Output: 2
Explanation: Jump 1 step from index 0 to 1, then 3 steps to last index.

Input:  nums = [2, 3, 0, 1, 4]
Output: 2
Explanation: Jump 1 step to index 1, then 3 steps to last index.
```

---

## Step-by-Step Solution

### Step 1: Greedy BFS Intuition

Think of indices as nodes and jumps as edges in a graph where each node connects to the next `nums[i]` nodes. We need the shortest path from node 0 to node n-1.

A **greedy BFS** approach tracks the "frontier" of the current jump: the farthest index reachable with the current number of jumps. When the frontier reaches or exceeds `n - 1`, we're done.

### Step 2: Algorithm

Maintain:
- `jumps` = number of jumps taken so far.
- `currentEnd` = farthest index reachable with the current number of jumps.
- `farthest` = farthest index reachable overall while exploring the current window.

Iterate `i` from `0` to `n - 2` (don't need to jump from the last index):
1. Update `farthest = max(farthest, i + nums[i])`.
2. If `i == currentEnd`: we've reached the end of the current jump window.
   - Increment `jumps`.
   - Set `currentEnd = farthest`.
   - If `currentEnd >= n - 1` → return `jumps`.

### Step 3: Why Greedy Works

The greedy choice of always extending the farthest reachable index is optimal because:
- All jumps within the current window are equally one jump away.
- Minimizing the number of jumps is equivalent to maximizing the reach per jump.
- There's no benefit to taking a shorter jump in the current window — it would only reduce future options.

---

## Full Compilable Solution

```java
import java.util.Arrays;

/**
 * LeetCode 45 — Jump Game II
 *
 * Greedy BFS approach: minimize jumps by maximizing reach per jump.
 *
 * Time:  O(n)
 * Space: O(1)
 */
public class JumpGameII {

    public int jump(int[] nums) {
        int n = nums.length;
        if (n == 1) return 0;

        int jumps = 0;
        int currentEnd = 0;
        int farthest = 0;

        for (int i = 0; i < n - 1; i++) {
            farthest = Math.max(farthest, i + nums[i]);

            if (i == currentEnd) {
                jumps++;
                currentEnd = farthest;
                if (currentEnd >= n - 1) break;
            }
        }
        return jumps;
    }

    public static void main(String[] args) {
        JumpGameII s = new JumpGameII();

        runTest(s, new int[]{2, 3, 1, 1, 4}, 2);
        runTest(s, new int[]{2, 3, 0, 1, 4}, 2);
        runTest(s, new int[]{0}, 0);               // Already at last
        runTest(s, new int[]{1}, 0);                // Single element
        runTest(s, new int[]{1, 2}, 1);             // One jump
        runTest(s, new int[]{1, 1, 1, 1}, 3);       // Incremental jumps
        runTest(s, new int[]{1, 2, 3, 4, 5}, 3);    // Jump from 0→1→3→4
        runTest(s, new int[]{5, 4, 3, 2, 1, 1, 1}, 2); // First jump takes far
        runTest(s, new int[]{10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 1, 0}, 2);
        runTest(s, new int[]{1, 3, 2, 2, 1, 1, 1}, 3); // 0→1→2→6
    }

    private static void runTest(JumpGameII s, int[] nums, int expected) {
        int result = s.jump(nums);
        String status = result == expected ? "PASS" : "FAIL";
        System.out.printf("%s | jump(%s) = %d (expected %d)%n",
            status, Arrays.toString(nums), result, expected);
    }
}
```

---

## DP Approach (for comparison)

```java
/**
 * O(n^2) DP approach — useful for understanding but not optimal.
 *
 * Time:  O(n^2)
 * Space: O(n)
 */
public class JumpGameIIDP {

    public int jump(int[] nums) {
        int n = nums.length;
        int[] dp = new int[n];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;

        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (j + nums[j] >= i && dp[j] != Integer.MAX_VALUE) {
                    dp[i] = Math.min(dp[i], dp[j] + 1);
                }
            }
        }
        return dp[n - 1];
    }

    public static void main(String[] args) {
        JumpGameIIDP s = new JumpGameIIDP();
        System.out.println(s.jump(new int[]{2,3,1,1,4}) + " (expected: 2)");
    }
}
```

---

## Complexity Analysis

| Version | Time | Space | Notes |
|---------|------|-------|-------|
| Greedy BFS | O(n) | O(1) | Optimal — single pass |
| DP | O(n^2) | O(n) | Useful for proof but too slow for large n |

### Why Greedy Is Optimal

The greedy BFS approach is actually computing the **shortest path in an unweighted DAG** where edges go forward. The BFS layers correspond exactly to the number of jumps:

- **Layer 0:** index 0
- **Layer 1:** all indices reachable from index 0
- **Layer k:** all indices reachable from layer k-1 that aren't in earlier layers

The greedy algorithm tracks the farthest reachable index per layer without explicitly constructing the graph.

---

## Follow-Up: Jump Game I (LeetCode 55)

Check if the last index is reachable (not the minimum jumps).

```java
public class JumpGameI {

    public boolean canJump(int[] nums) {
        int farthest = 0;
        for (int i = 0; i < nums.length; i++) {
            if (i > farthest) return false;
            farthest = Math.max(farthest, i + nums[i]);
        }
        return true;
    }

    public static void main(String[] args) {
        JumpGameI s = new JumpGameI();
        System.out.println(s.canJump(new int[]{2,3,1,1,4}) + " (expected: true)");
        System.out.println(s.canJump(new int[]{3,2,1,0,4}) + " (expected: false)");
    }
}
```

---

## Edge Cases & Test Coverage

| Case | Input | Expected | Notes |
|------|-------|----------|-------|
| Single element | `[0]` | 0 | Already at destination |
| Two elements | `[1, 0]` | 1 | One jump |
| Incremental | `[1,1,1,1]` | 3 | Step by step |
| Big first jump | `[5,1,1,1,1,1]` | 1 | Jump straight to end |
| Zero in middle | `[2,0,0,3]` | - | Always reachable per constraint |
| Long jumps | `[2,3,1,1,4]` | 2 | Standard test |
| All ones | `[1,1,1,1,1]` | 4 | One step at a time |

---

## Key Takeaways

1. **Greedy + BFS** is the optimal pattern for "minimum steps with forward jumps" problems.
2. **BFS layers** correspond to jump count — tracking `currentEnd` and `farthest` effectively computes these layers.
3. The **DP approach** (O(n^2)) reveals the recurrence but is not competitive performance-wise.
4. The same pattern extends to Jump Game I (reachability) and Video Stitching (LeetCode 1024).
5. The problem guarantees reachability, so we don't need to return `-1` — but real implementations should check.