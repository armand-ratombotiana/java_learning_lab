# LeetCode 322 — Coin Change

## Problem

You are given an integer array `coins` representing coins of different denominations and an integer `amount` representing a total amount of money.

Return the **fewest number of coins** needed to make up that amount. If that amount cannot be made up by any combination of the coins, return `-1`.

**Constraints:**
- `1 <= coins.length <= 12`
- `1 <= coins[i] <= 2^31 - 1`
- `0 <= amount <= 10^4`

---

## Solution 1: Top-Down DP (Memoization)

```java
import java.util.*;

/**
 * LeetCode 322 — Coin Change
 * Top-down DP with memoization.
 *
 * Time: O(amount * coins.length) | Space: O(amount)
 */
public class CoinChangeMemo {

    public int coinChange(int[] coins, int amount) {
        int[] memo = new int[amount + 1];
        Arrays.fill(memo, -2);
        return dfs(coins, amount, memo);
    }

    private int dfs(int[] coins, int rem, int[] memo) {
        if (rem == 0) return 0;
        if (rem < 0) return -1;
        if (memo[rem] != -2) return memo[rem];

        int min = Integer.MAX_VALUE;
        for (int coin : coins) {
            int sub = dfs(coins, rem - coin, memo);
            if (sub >= 0) {
                min = Math.min(min, sub + 1);
            }
        }
        memo[rem] = (min == Integer.MAX_VALUE) ? -1 : min;
        return memo[rem];
    }

    public static void main(String[] args) {
        CoinChangeMemo s = new CoinChangeMemo();

        System.out.println("Test 1: " + s.coinChange(new int[]{1,2,5}, 11) + " (expected: 3)");
        System.out.println("Test 2: " + s.coinChange(new int[]{2}, 3) + " (expected: -1)");
        System.out.println("Test 3: " + s.coinChange(new int[]{1}, 0) + " (expected: 0)");
        System.out.println("Test 4: " + s.coinChange(new int[]{1,2,5}, 100) + " (expected: 20)");
        System.out.println("Test 5: " + s.coinChange(new int[]{186,419,83,408}, 6249) + " (expected: 20)");
    }
}
```

---

## Solution 2: Bottom-Up Tabulation (Full Knapsack DP)

```java
import java.util.*;

/**
 * LeetCode 322 — Coin Change
 * Bottom-up DP — unbounded knapsack pattern.
 *
 * Time: O(amount * coins.length) | Space: O(amount)
 */
public class CoinChangeDP {

    public int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        dp[0] = 0;

        for (int i = 1; i <= amount; i++) {
            for (int coin : coins) {
                if (coin <= i) {
                    dp[i] = Math.min(dp[i], dp[i - coin] + 1);
                }
            }
        }
        return dp[amount] > amount ? -1 : dp[amount];
    }

    public static void main(String[] args) {
        CoinChangeDP s = new CoinChangeDP();

        System.out.println("Test 1: " + s.coinChange(new int[]{1,2,5}, 11) + " (expected: 3)");
        System.out.println("Test 2: " + s.coinChange(new int[]{2}, 3) + " (expected: -1)");
        System.out.println("Test 3: " + s.coinChange(new int[]{1}, 0) + " (expected: 0)");
        System.out.println("Test 4: " + s.coinChange(new int[]{1}, 2) + " (expected: 2)");
        System.out.println("Test 5: " + s.coinChange(new int[]{1,5,10,25}, 99) + " (expected: 9)");
        System.out.println("Test 6: " + s.coinChange(new int[]{2,5,10,1}, 27) + " (expected: 4)");
    }
}
```

---

## Complexity Analysis

| Approach | Time | Space |
|----------|------|-------|
| Top-Down Memoization | O(coins * amount) | O(amount) |
| Bottom-Up Tabulation | O(coins * amount) | O(amount) |

**Key Insight:** This is an **unbounded knapsack** problem — each coin can be used an unlimited number of times. The recurrence `dp[i] = min(dp[i], dp[i - coin] + 1)` expresses the minimum coins needed for amount `i`. The array is initialized to `amount + 1` (an impossible large value) because the maximum coins needed is at most `amount` (using all 1s).

**Why Greedy Fails:** For coin set `{1, 3, 4}` and amount `6`, greedy picks 4+1+1 (3 coins), but optimal is 3+3 (2 coins). DP explores all combinations and finds the true minimum.
