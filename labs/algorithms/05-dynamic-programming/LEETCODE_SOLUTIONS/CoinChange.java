package com.algorithms.dp;

import java.util.Arrays;

/**
 * LeetCode 322: Coin Change
 * https://leetcode.com/problems/coin-change/
 *
 * Given coins of different denominations and an amount, find the fewest coins needed.
 *
 * Time Complexity: O(n * amount)
 * Space Complexity: O(amount)
 */
public class CoinChange {

    /**
     * Approach 1 (Optimal): DP (Unbounded Knapsack)
     * dp[i] = min coins to make amount i.
     * dp[i] = min(dp[i], dp[i - coin] + 1) for each coin.
     */
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

    /**
     * Approach 2: Brute force recursion (exponential)
     */

    public static void main(String[] args) {
        CoinChange cc = new CoinChange();
        System.out.println("Test 1: " + cc.coinChange(new int[] { 1, 2, 5 }, 11) + " (expected: 3)");
        System.out.println("Test 2: " + cc.coinChange(new int[] { 2 }, 3) + " (expected: -1)");
        System.out.println("Test 3: " + cc.coinChange(new int[] { 1 }, 0) + " (expected: 0)");
        System.out.println("Test 4: " + cc.coinChange(new int[] { 1, 2, 5, 10 }, 18) + " (expected: 4)");
        System.out.println("Test 5: " + cc.coinChange(new int[] { 186, 419, 83, 408 }, 6249) + " (expected: 20)");
    }
}
