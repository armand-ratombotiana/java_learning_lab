# LeetCode 198 — House Robber — Problem Walkthrough

## Problem Statement

You are a professional robber planning to rob houses along a street. Each house has a certain amount of money stashed. Adjacent houses have connected security systems — if two adjacent houses are broken into on the same night, the police will be called.

Given an integer array `nums` representing the money at each house, return the **maximum amount** you can rob tonight **without alerting the police**.

**Constraints:**
- `1 <= nums.length <= 100`
- `0 <= nums[i] <= 400`

**Examples:**
```
Input:  nums = [1, 2, 3, 1]
Output: 4
Explanation: Rob house 1 (money=1) then house 3 (money=3). Total = 1+3=4.

Input:  nums = [2, 7, 9, 3, 1]
Output: 12
Explanation: Rob house 1 (2), house 3 (9), house 5 (1). Total = 2+9+1=12.
```

---

## Step-by-Step Solution

### Step 1: Define the State

Let `dp[i]` = maximum amount that can be robbed from the first `i` houses (0-indexed).

### Step 2: Recurrence Relation

At house `i`, we have two choices:
1. **Skip** this house: take `dp[i-1]` (best from previous houses).
2. **Rob** this house: take `nums[i] + dp[i-2]` (cannot rob adjacent).

Therefore:
```
dp[i] = max(dp[i-1], nums[i] + dp[i-2])
```

### Step 3: Base Cases

- `dp[0] = nums[0]` (only one house)
- `dp[1] = max(nums[0], nums[1])` (two houses, pick the richer one)

### Step 4: Space Optimization

Notice that `dp[i]` only depends on `dp[i-1]` and `dp[i-2]`. Instead of an O(n) array, maintain two variables:

```
prev2 = dp[i-2], prev1 = dp[i-1]
curr = max(prev1, nums[i] + prev2)
prev2 = prev1
prev1 = curr
```

This gives **O(1) space**.

---

## Full Compilable Solution

```java
import java.util.Arrays;

/**
 * LeetCode 198 — House Robber
 *
 * Dynamic Programming with O(1) space optimization.
 *
 * Recurrence: dp[i] = max(dp[i-1], nums[i] + dp[i-2])
 *
 * Time:  O(n)
 * Space: O(1)
 */
public class HouseRobber {

    public int rob(int[] nums) {
        int n = nums.length;
        if (n == 1) return nums[0];

        int prev2 = nums[0];
        int prev1 = Math.max(nums[0], nums[1]);

        for (int i = 2; i < n; i++) {
            int curr = Math.max(prev1, nums[i] + prev2);
            prev2 = prev1;
            prev1 = curr;
        }

        return prev1;
    }

    public static void main(String[] args) {
        HouseRobber s = new HouseRobber();

        // Test cases
        runTest(s, new int[]{1, 2, 3, 1}, 4);
        runTest(s, new int[]{2, 7, 9, 3, 1}, 12);
        runTest(s, new int[]{5}, 5);
        runTest(s, new int[]{1, 2}, 2);
        runTest(s, new int[]{2, 1, 1, 2}, 4);  // rob indices 0 and 3
        runTest(s, new int[]{0, 0, 0, 0}, 0);
        runTest(s, new int[]{10, 1, 1, 10}, 20); // rob first and last
        runTest(s, new int[]{1, 3, 1, 3, 100}, 103); // skip, rob, skip, skip, rob
    }

    private static void runTest(HouseRobber s, int[] nums, int expected) {
        int result = s.rob(nums);
        String status = result == expected ? "PASS" : "FAIL";
        System.out.printf("%s | rob(%s) = %d (expected %d)%n",
            status, Arrays.toString(nums), result, expected);
    }
}
```

### Array DP Version (for clarity)

```java
/**
 * Unoptimized DP for understanding.
 * Time: O(n) | Space: O(n)
 */
public class HouseRobberArray {

    public int rob(int[] nums) {
        int n = nums.length;
        if (n == 1) return nums[0];

        int[] dp = new int[n];
        dp[0] = nums[0];
        dp[1] = Math.max(nums[0], nums[1]);

        for (int i = 2; i < n; i++) {
            dp[i] = Math.max(dp[i - 1], nums[i] + dp[i - 2]);
        }
        return dp[n - 1];
    }

    public static void main(String[] args) {
        HouseRobberArray s = new HouseRobberArray();
        System.out.println(s.rob(new int[]{1, 2, 3, 1}) + " (expected: 4)");
        System.out.println(s.rob(new int[]{2, 7, 9, 3, 1}) + " (expected: 12)");
    }
}
```

---

## Complexity Analysis

| Measure | Value |
|---------|-------|
| Time Complexity | **O(n)** — single pass through the array |
| Space Complexity | **O(1)** — only two variables tracked (O(n) with array DP) |

### Why This Is Optimal

- We must examine each house at least once → O(n) is a lower bound.
- Each house's decision depends only on the previous two values → no need to store the full DP table.

---

## Follow-Up: House Robber II (LeetCode 213)

Houses are arranged in a **circle** (first and last are now adjacent).

**Solution:** Run the linear robber twice:
1. Exclude last house: `rob(nums[0..n-2])`
2. Exclude first house: `rob(nums[1..n-1])`
3. Return `max(result1, result2)`

```java
public class HouseRobberII {

    public int rob(int[] nums) {
        int n = nums.length;
        if (n == 1) return nums[0];
        return Math.max(robLinear(nums, 0, n - 2), robLinear(nums, 1, n - 1));
    }

    private int robLinear(int[] nums, int l, int r) {
        int prev2 = 0, prev1 = 0;
        for (int i = l; i <= r; i++) {
            int curr = Math.max(prev1, nums[i] + prev2);
            prev2 = prev1;
            prev1 = curr;
        }
        return prev1;
    }

    public static void main(String[] args) {
        HouseRobberII s = new HouseRobberII();
        System.out.println(s.rob(new int[]{2, 3, 2}) + " (expected: 3)");
        System.out.println(s.rob(new int[]{1, 2, 3, 1}) + " (expected: 4)");
        System.out.println(s.rob(new int[]{1, 2, 3}) + " (expected: 3)");
    }
}
```

## Follow-Up: House Robber III (LeetCode 337)

Houses arranged in a **binary tree**. Cannot rob parent and child directly.

**Solution:** Tree DP returning `[robThis, skipThis]`:

```java
public class HouseRobberIII {

    public int rob(TreeNode root) {
        int[] result = dfs(root);
        return Math.max(result[0], result[1]);
    }

    private int[] dfs(TreeNode node) {
        if (node == null) return new int[]{0, 0};
        int[] left = dfs(node.left);
        int[] right = dfs(node.right);
        int rob = node.val + left[1] + right[1];
        int skip = Math.max(left[0], left[1]) + Math.max(right[0], right[1]);
        return new int[]{rob, skip};
    }

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int v) { this.val = v; }
    }

    public static void main(String[] args) {
        // Build tree: [3,2,3,null,3,null,1]
        TreeNode n3 = new TreeNode(3);
        TreeNode n2 = new TreeNode(2);
        TreeNode n3b = new TreeNode(3);
        TreeNode n3c = new TreeNode(3);
        TreeNode n1 = new TreeNode(1);
        n3.left = n2; n3.right = n3b;
        n2.right = n3c; n3b.right = n1;
        HouseRobberIII s = new HouseRobberIII();
        System.out.println(s.rob(n3) + " (expected: 7)");
    }
}
```

---

## Edge Cases & Test Coverage

| Case | Input | Expected | Notes |
|------|-------|----------|-------|
| Single house | `[5]` | 5 | Base case n=1 |
| Two houses | `[1, 2]` | 2 | Pick the max |
| All zeros | `[0,0,0]` | 0 | Edge |
| Alternating | `[2,1,1,2]` | 4 | Picking both ends |
| Increasing | `[1,2,3,4,5]` | 9 | Odd indices |
| Descending | `[5,4,3,2,1]` | 9 | Even indices |
| Large values | `[400, 400, 400]` | 800 | Max constraint check |

---

## Key Takeaways

1. **State definition** is the most crucial step in DP — define what `dp[i]` represents clearly.
2. **Space optimization** follows naturally when the recurrence only references 1-2 previous states.
3. The **House Robber family** (I, II, III) demonstrates how the same core DP idea adapts to different data structures (array, circle, tree).
4. Always test edge cases: single element, two elements, all equal values.