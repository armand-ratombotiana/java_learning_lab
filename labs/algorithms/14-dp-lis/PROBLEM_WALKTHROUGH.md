# LeetCode 300 — Longest Increasing Subsequence — Problem Walkthrough

## Problem Statement

Given an integer array `nums`, return the **length** of the longest strictly increasing subsequence.

A subsequence is a sequence derived by deleting some or no elements without changing the order of the remaining elements.

**Constraints:**
- `1 <= nums.length <= 2500`
- `-10^4 <= nums[i] <= 10^4`

**Examples:**
```
Input:  nums = [10, 9, 2, 5, 3, 7, 101, 18]
Output: 4
Explanation: LIS is [2, 3, 7, 101] (or [2, 5, 7, 101]).

Input:  nums = [0, 1, 0, 3, 2, 3]
Output: 4
Explanation: LIS is [0, 1, 2, 3].

Input:  nums = [7, 7, 7, 7]
Output: 1
Explanation: Strictly increasing means equal values don't count.
```

---

## Step-by-Step Solution

### Step 1: O(n^2) DP Approach

**State:** `dp[i]` = length of LIS ending at index `i`.

**Recurrence:**
```
dp[i] = 1 + max{ dp[j] } for all j < i and nums[j] < nums[i]
```

**Base:** `dp[i] = 1` (each element alone is an LIS of length 1).

Time: O(n^2) | Space: O(n)

### Step 2: O(n log n) Patience Sorting

**Key Insight:** Maintain an array `tails` where `tails[k]` = the smallest possible tail value of an increasing subsequence of length `k+1`.

For each `num`:
- Find the first index in `tails` where `tails[idx] >= num` (binary search).
- Replace it with `num`.
- If `num` is larger than all tails, append it (subsequence length increases).

At the end, `tails.length` = LIS length.

This works because:
- `tails` is always sorted (strictly increasing).
- Greedy replacement keeps the smallest possible tails for each length, maximizing future growth.

---

## Full Compilable Solution

```java
import java.util.Arrays;

/**
 * LeetCode 300 — Longest Increasing Subsequence
 *
 * O(n log n) using Patience Sorting (binary search on tails array).
 *
 * Time:  O(n log n)
 * Space: O(n)
 */
public class LongestIncreasingSubsequence {

    public int lengthOfLIS(int[] nums) {
        int[] tails = new int[nums.length];
        int size = 0;

        for (int num : nums) {
            int l = 0, r = size;
            while (l < r) {
                int m = l + (r - l) / 2;
                if (tails[m] < num) {
                    l = m + 1;
                } else {
                    r = m;
                }
            }
            tails[l] = num;
            if (l == size) size++;
        }
        return size;
    }

    public static void main(String[] args) {
        LongestIncreasingSubsequence s = new LongestIncreasingSubsequence();

        runTest(s, new int[]{10, 9, 2, 5, 3, 7, 101, 18}, 4);
        runTest(s, new int[]{0, 1, 0, 3, 2, 3}, 4);
        runTest(s, new int[]{7, 7, 7, 7}, 1);
        runTest(s, new int[]{1, 2, 3, 4, 5}, 5);
        runTest(s, new int[]{5, 4, 3, 2, 1}, 1);
        runTest(s, new int[]{1}, 1);
        runTest(s, new int[]{}, 0);
        runTest(s, new int[]{2, 2, 2}, 1);
        runTest(s, new int[]{10, 22, 9, 33, 21, 50, 41, 60, 80}, 6);
        runTest(s, new int[]{-10, -5, 0, 5, 10}, 5);
    }

    private static void runTest(LongestIncreasingSubsequence s, int[] nums, int expected) {
        int result = s.lengthOfLIS(nums);
        String status = result == expected ? "PASS" : "FAIL";
        System.out.printf("%s | lengthOfLIS(%s) = %d (expected %d)%n",
            status, Arrays.toString(nums), result, expected);
    }
}
```

---

## O(n^2) DP Version (for understanding)

```java
/**
 * O(n^2) dynamic programming approach.
 * Good for understanding the recurrence; not optimal for large inputs.
 *
 * Time:  O(n^2)
 * Space: O(n)
 */
public class LISQuadratic {

    public int lengthOfLIS(int[] nums) {
        int n = nums.length;
        int[] dp = new int[n];
        int maxLen = 0;

        for (int i = 0; i < n; i++) {
            dp[i] = 1;
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
            maxLen = Math.max(maxLen, dp[i]);
        }
        return maxLen;
    }

    public static void main(String[] args) {
        LISQuadratic s = new LISQuadratic();
        System.out.println(s.lengthOfLIS(new int[]{10,9,2,5,3,7,101,18}) + " (expected: 4)");
        System.out.println(s.lengthOfLIS(new int[]{0,1,0,3,2,3}) + " (expected: 4)");
    }
}
```

---

## Complexity Analysis

| Version | Time | Space | Notes |
|---------|------|-------|-------|
| O(n^2) DP | O(n^2) | O(n) | Simple, works for n ≤ 2500 |
| Patience Sorting | O(n log n) | O(n) | Optimal for this problem |

### Why Patience Sorting Works

The `tails` array maintains the invariant: `tails[i]` is the minimum possible last element of an increasing subsequence of length `i+1`. Binary search finds where to place each new number:

- If `num` extends the longest existing subsequence → append → length increases.
- If `num` replaces a smaller tail → future subsequences have a better (smaller) ending → more room for growth.

This is a **greedy + binary search** approach — greedy in the replacement strategy, binary search for efficiency.

---

## Follow-Up: Number of LIS (LeetCode 673)

Count the number of longest increasing subsequences.

**Approach:** Augment DP with counts.

```java
public class NumberOfLIS {

    public int findNumberOfLIS(int[] nums) {
        int n = nums.length;
        int[] len = new int[n]; // LIS length ending at i
        int[] cnt = new int[n]; // count of LIS ending at i

        int maxLen = 0, totalCount = 0;
        for (int i = 0; i < n; i++) {
            len[i] = 1; cnt[i] = 1;
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]) {
                    if (len[j] + 1 > len[i]) {
                        len[i] = len[j] + 1;
                        cnt[i] = cnt[j];
                    } else if (len[j] + 1 == len[i]) {
                        cnt[i] += cnt[j];
                    }
                }
            }
            if (len[i] > maxLen) { maxLen = len[i]; totalCount = cnt[i]; }
            else if (len[i] == maxLen) { totalCount += cnt[i]; }
        }
        return totalCount;
    }

    public static void main(String[] args) {
        NumberOfLIS s = new NumberOfLIS();
        System.out.println(s.findNumberOfLIS(new int[]{1,3,5,4,7}) + " (expected: 2)");
        System.out.println(s.findNumberOfLIS(new int[]{2,2,2,2,2}) + " (expected: 5)");
    }
}
```

---

## Edge Cases & Test Coverage

| Case | Input | Expected | Notes |
|------|-------|----------|-------|
| Single element | `[1]` | 1 | Only one |
| Decreasing | `[5,4,3,2,1]` | 1 | Each element alone |
| All equal | `[3,3,3]` | 1 | Strictly increasing — no increases |
| Already increasing | `[1,2,3,4,5]` | 5 | Full array |
| Mixed | `[10,22,9,33,21,50,41,60,80]` | 6 | Classic test |
| Negatives | `[-5,-3,-1,0,2]` | 5 | LIS across negatives |
| Empty | `[]` | 0 | No elements |

---

## Key Takeaways

1. **Patience sorting** achieves O(n log n) — the binary search on `tails` is the key optimization.
2. The **tails array** remains sorted because each replacement or append preserves ordering.
3. This algorithm only computes **length** — recovering the actual LIS requires auxiliary storage.
4. The **O(n^2) DP** approach is simpler and can be augmented for counting / reconstruction.
5. Understanding both approaches is essential: O(n^2) for the recurrence intuition, O(n log n) for performance.