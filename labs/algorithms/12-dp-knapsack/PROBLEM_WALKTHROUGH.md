# LeetCode 416 — Partition Equal Subset Sum — Problem Walkthrough

## Problem Statement

Given a **non-empty** array `nums` containing **positive integers**, determine if the array can be partitioned into two subsets such that the sum of elements in both subsets is equal.

**Constraints:**
- `1 <= nums.length <= 200`
- `1 <= nums[i] <= 100`

**Examples:**
```
Input:  nums = [1, 5, 11, 5]
Output: true
Explanation: Subsets [1, 5, 5] and [11] both sum to 11.

Input:  nums = [1, 2, 3, 5]
Output: false
Explanation: No partition yields equal sums.
```

---

## Step-by-Step Solution

### Step 1: Problem Analysis

- Total sum `S` must be even; otherwise return false.
- Target = `S / 2` — we need a subset summing to exactly half the total.
- This is a **0/1 Knapsack** problem: each element is either taken or not.
- Capacity = target, items = nums, values = weights = nums.
- We ask: can we achieve sum = target using some subset?

### Step 2: Define State

Let `dp[i][s]` = true if we can achieve sum `s` using the first `i` elements.

### Step 3: Recurrence

```
dp[i][s] = dp[i-1][s] || dp[i-1][s - nums[i-1]]
          ↑ skip          ↑ take
```

Base: `dp[0][0] = true`, `dp[0][s] = false` for s > 0.

### Step 4: Space Optimization

Since `dp[i]` depends only on `dp[i-1]`, we can use a 1D boolean array and iterate backwards:

```
dp[s] = dp[s] || dp[s - num]   (iterate s from target down to num)
```

---

## Full Compilable Solution

```java
import java.util.Arrays;

/**
 * LeetCode 416 — Partition Equal Subset Sum
 *
 * 0/1 Knapsack DP: can we achieve sum = total/2?
 *
 * Time:  O(n * target) where target = sum/2
 * Space: O(target)
 */
public class PartitionEqualSubsetSum {

    public boolean canPartition(int[] nums) {
        int total = 0;
        for (int v : nums) total += v;
        if ((total & 1) == 1) return false;

        int target = total / 2;
        boolean[] dp = new boolean[target + 1];
        dp[0] = true;

        for (int num : nums) {
            for (int s = target; s >= num; s--) {
                dp[s] = dp[s] || dp[s - num];
            }
        }
        return dp[target];
    }

    public static void main(String[] args) {
        PartitionEqualSubsetSum s = new PartitionEqualSubsetSum();

        runTest(s, new int[]{1, 5, 11, 5}, true);
        runTest(s, new int[]{1, 2, 3, 5}, false);
        runTest(s, new int[]{1, 2, 5}, false);
        runTest(s, new int[]{2, 2, 2, 2}, true);       // each = 4
        runTest(s, new int[]{1}, false);                // total = 1 (odd)
        runTest(s, new int[]{100}, false);              // total = 100 (odd)
        runTest(s, new int[]{1, 1, 1, 1, 1, 1, 1, 1}, true); // total 8, target 4
        runTest(s, new int[]{3, 3, 3, 3, 3}, false);   // total 15, odd
        runTest(s, new int[]{1, 2, 3, 4, 5, 6, 7}, true); // total 28, target 14
        runTest(s, new int[]{10, 20, 30, 40, 50}, false); // total 150, target 75
    }

    private static void runTest(PartitionEqualSubsetSum s, int[] nums, boolean expected) {
        boolean result = s.canPartition(nums);
        String status = result == expected ? "PASS" : "FAIL";
        System.out.printf("%s | canPartition(%s) = %b (expected %b)%n",
            status, Arrays.toString(nums), result, expected);
    }
}
```

### 2D DP Version

```java
/**
 * 2D DP for clarity.
 * Time: O(n * target) | Space: O(n * target)
 */
public class PartitionEqualSubsetSum2D {

    public boolean canPartition(int[] nums) {
        int total = Arrays.stream(nums).sum();
        if ((total & 1) == 1) return false;
        int target = total / 2, n = nums.length;

        boolean[][] dp = new boolean[n + 1][target + 1];
        dp[0][0] = true;

        for (int i = 1; i <= n; i++) {
            for (int s = 0; s <= target; s++) {
                dp[i][s] = dp[i - 1][s];
                if (s >= nums[i - 1])
                    dp[i][s] = dp[i][s] || dp[i - 1][s - nums[i - 1]];
            }
        }
        return dp[n][target];
    }

    public static void main(String[] args) {
        PartitionEqualSubsetSum2D s = new PartitionEqualSubsetSum2D();
        System.out.println(s.canPartition(new int[]{1,5,11,5}) + " (expected: true)");
        System.out.println(s.canPartition(new int[]{1,2,3,5}) + " (expected: false)");
    }
}
```

---

## Complexity Analysis

| Measure | Value |
|---------|-------|
| Time Complexity | **O(n * target)** where target = sum / 2 ≤ 10,000 (since n ≤ 200, nums[i] ≤ 100, max sum = 20,000, target ≤ 10,000) |
| Space Complexity | **O(target)** with 1D optimization, O(n * target) with 2D |

### Optimization Rationale

- The 1D backward-iteration trick is standard for 0/1 knapsack optimization
- It works because each item is used at most once — backward iteration prevents reusing the same item multiple times

---

## Follow-Up: LeetCode 494 — Target Sum

Assign `+` or `-` before each integer to reach a target.

**Approach:** Transform to subset sum: let `P` be the positive subset sum, `N` the negative. Then `P - N = target` and `P + N = sum` → `P = (sum + target) / 2`. Count subsets summing to `P`.

```java
public class TargetSum {

    public int findTargetSumWays(int[] nums, int target) {
        int total = Arrays.stream(nums).sum();
        if (total < Math.abs(target) || (total + target) % 2 == 1) return 0;
        int sum = (total + target) / 2;
        int[] dp = new int[sum + 1];
        dp[0] = 1;
        for (int num : nums) {
            for (int s = sum; s >= num; s--) {
                dp[s] += dp[s - num];
            }
        }
        return dp[sum];
    }

    public static void main(String[] args) {
        TargetSum s = new TargetSum();
        System.out.println(s.findTargetSumWays(new int[]{1,1,1,1,1}, 3) + " (expected: 5)");
        System.out.println(s.findTargetSumWays(new int[]{1}, 1) + " (expected: 1)");
    }
}
```

---

## Edge Cases & Test Coverage

| Case | Input | Expected | Notes |
|------|-------|----------|-------|
| Odd total | `[1, 2]` | false | total=3, odd |
| Single element | `[1]` | false | total=1, odd |
| Single even | `[2]` | false | total=2, target=1, can't make 1 |
| All equal | `[2,2,2,2]` | true | target=4 |
| Large spread | `[1, 100, 50, 49]` | true | target=100 (100 = 100) |
| Unsorted | `[3,1,5,9,12]` | false | total=30, target=15, no subset sums to 15 |

---

## Key Takeaways

1. **Partition equals subset sum** — recognize the 0/1 knapsack pattern.
2. **First check total parity** — a simple O(n) check can eliminate impossible cases instantly.
3. **1D DP with backward iteration** is the classic space optimization for 0/1 knapsack.
4. The **Target Sum** variant shows how to transform "+/-" assignment into a subset sum problem.
5. This problem class (subset sum / partition) appears in many disguises: Last Stone Weight II, Target Sum, Minimum Difference of Subsets.