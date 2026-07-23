package com.algorithms.dp;

/**
 * LeetCode 1143: Longest Common Subsequence
 * https://leetcode.com/problems/longest-common-subsequence/
 *
 * Find the length of the longest common subsequence between two strings.
 *
 * Time Complexity: O(m * n)
 * Space Complexity: O(m * n)
 */
public class LongestCommonSubsequence {

    /**
     * Approach 1 (Optimal): 2D DP
     * dp[i][j] = LCS of text1[0..i-1] and text2[0..j-1]
     * If chars match: 1 + dp[i-1][j-1]
     * Else: max(dp[i-1][j], dp[i][j-1])
     */
    public int longestCommonSubsequence(String text1, String text2) {
        int m = text1.length(), n = text2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = 1 + dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[m][n];
    }

    /**
     * Approach 2: Space-optimized (O(min(m,n)))
     */

    public static void main(String[] args) {
        LongestCommonSubsequence lcs = new LongestCommonSubsequence();
        System.out.println("Test 1: " + lcs.longestCommonSubsequence("abcde", "ace") + " (expected: 3)");
        System.out.println("Test 2: " + lcs.longestCommonSubsequence("abc", "abc") + " (expected: 3)");
        System.out.println("Test 3: " + lcs.longestCommonSubsequence("abc", "def") + " (expected: 0)");
        System.out.println("Test 4: " + lcs.longestCommonSubsequence("", "abc") + " (expected: 0)");
        System.out.println("Test 5: " + lcs.longestCommonSubsequence("ABCDGH", "AEDFHR") + " (expected: 3)");
    }
}
