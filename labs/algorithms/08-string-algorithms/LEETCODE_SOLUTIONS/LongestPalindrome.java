package com.algorithms.string;

/**
 * LeetCode 5: Longest Palindromic Substring
 * https://leetcode.com/problems/longest-palindromic-substring/
 *
 * Given a string, find the longest palindromic substring.
 *
 * Time Complexity: O(n^2)
 * Space Complexity: O(1)
 */
public class LongestPalindrome {

    private int start = 0, maxLen = 0;

    /**
     * Approach 1 (Optimal): Expand Around Center
     * Check each character and between characters as palindrome centers.
     */
    public String longestPalindrome(String s) {
        if (s == null || s.length() < 2) return s;
        for (int i = 0; i < s.length(); i++) {
            expand(s, i, i);
            expand(s, i, i + 1);
        }
        return s.substring(start, start + maxLen);
    }

    private void expand(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        int len = right - left - 1;
        if (len > maxLen) {
            maxLen = len;
            start = left + 1;
        }
    }

    public static void main(String[] args) {
        LongestPalindrome lp = new LongestPalindrome();
        System.out.println("Test 1: " + lp.longestPalindrome("babad") + " (expected: bab or aba)");
        System.out.println("Test 2: " + lp.longestPalindrome("cbbd") + " (expected: bb)");
        System.out.println("Test 3: " + lp.longestPalindrome("a") + " (expected: a)");
        System.out.println("Test 4: " + lp.longestPalindrome("ac") + " (expected: a or c)");
        System.out.println("Test 5: " + lp.longestPalindrome("") + " (expected: )");
    }
}
