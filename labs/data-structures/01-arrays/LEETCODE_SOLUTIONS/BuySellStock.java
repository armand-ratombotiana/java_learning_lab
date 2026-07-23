package com.leetcode.arrays;

/**
 * LeetCode 121: Best Time to Buy and Sell Stock
 * https://leetcode.com/problems/best-time-to-buy-and-sell-stock/
 *
 * You are given an array prices where prices[i] is the price of a given stock on day i.
 * Find the maximum profit you can achieve by buying and selling once.
 *
 * Time Complexity: O(n) - single pass
 * Space Complexity: O(1) - constant extra space
 */
public class BuySellStock {

    /**
     * Approach 1 (Optimal): Single pass
     * Track min price seen so far and max profit achievable.
     * Time: O(n), Space: O(1)
     */
    public int maxProfit(int[] prices) {
        if (prices == null || prices.length < 2) return 0;

        int minPrice = prices[0];
        int maxProfit = 0;

        for (int i = 1; i < prices.length; i++) {
            if (prices[i] < minPrice) {
                minPrice = prices[i];
            } else {
                int profit = prices[i] - minPrice;
                if (profit > maxProfit) {
                    maxProfit = profit;
                }
            }
        }
        return maxProfit;
    }

    /**
     * Approach 2: Brute Force
     * Check every buy/sell pair.
     * Time: O(n^2), Space: O(1)
     */
    public int maxProfitBruteForce(int[] prices) {
        int max = 0;
        for (int i = 0; i < prices.length; i++) {
            for (int j = i + 1; j < prices.length; j++) {
                max = Math.max(max, prices[j] - prices[i]);
            }
        }
        return max;
    }

    public static void main(String[] args) {
        BuySellStock bss = new BuySellStock();

        System.out.println("Test 1: " + bss.maxProfit(new int[] { 7, 1, 5, 3, 6, 4 }) + " (expected: 5)");
        System.out.println("Test 2: " + bss.maxProfit(new int[] { 7, 6, 4, 3, 1 }) + " (expected: 0)");
        System.out.println("Test 3: " + bss.maxProfit(new int[] { 1, 2 }) + " (expected: 1)");
        System.out.println("Test 4: " + bss.maxProfit(new int[] { 3 }) + " (expected: 0)");

        // Verify with brute force
        System.out.println("Test 5 (BF): " + bss.maxProfitBruteForce(new int[] { 7, 1, 5, 3, 6, 4 }) + " (expected: 5)");
    }
}
