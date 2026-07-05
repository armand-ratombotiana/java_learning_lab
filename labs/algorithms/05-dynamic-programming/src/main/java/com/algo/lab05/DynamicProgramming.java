package com.algo.lab05;

/**
 * Dynamic programming algorithms.
 *
 * Fibonacci (DP): O(n) time, O(1) space
 * 0/1 Knapsack: O(n*W) time, O(n*W) space
 * Longest Common Subsequence (LCS): O(m*n) time, O(m*n) space
 * Longest Increasing Subsequence (LIS): O(n log n) time, O(n) space
 * Edit Distance (Levenshtein): O(m*n) time, O(m*n) space
 */
public class DynamicProgramming {

    private DynamicProgramming() {}

    public static long fibonacciDP(int n) {
        if (n < 0) throw new IllegalArgumentException("n must be non-negative");
        if (n <= 1) return n;
        long prev = 0, curr = 1;
        for (int i = 2; i <= n; i++) {
            long next = prev + curr;
            prev = curr;
            curr = next;
        }
        return curr;
    }

    public static int knapSack(int[] weights, int[] values, int capacity) {
        int n = values.length;
        int[][] dp = new int[n + 1][capacity + 1];
        for (int i = 1; i <= n; i++) {
            int w = weights[i - 1];
            int v = values[i - 1];
            for (int cap = 1; cap <= capacity; cap++) {
                if (w <= cap) {
                    dp[i][cap] = Math.max(v + dp[i - 1][cap - w], dp[i - 1][cap]);
                } else {
                    dp[i][cap] = dp[i - 1][cap];
                }
            }
        }
        return dp[n][capacity];
    }

    public static int longestCommonSubsequence(String a, String b) {
        int m = a.length(), n = b.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[m][n];
    }

    public static int longestIncreasingSubsequence(int[] arr) {
        int n = arr.length;
        int[] tails = new int[n];
        int len = 0;
        for (int x : arr) {
            int idx = java.util.Arrays.binarySearch(tails, 0, len, x);
            if (idx < 0) idx = -(idx + 1);
            tails[idx] = x;
            if (idx == len) len++;
        }
        return len;
    }

    public static int editDistance(String a, String b) {
        int m = a.length(), n = b.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j],
                                   Math.min(dp[i][j - 1], dp[i - 1][j - 1]));
                }
            }
        }
        return dp[m][n];
    }
}