package com.algo.lab40;

/**
 * Manacher's algorithm for finding the longest palindromic substring in O(n).
 * Handles both odd and even length palindromes by inserting separators.
 * Uses symmetry properties to avoid redundant character comparisons.
 */
public class Manacher {

    public static String longestPalindrome(String s) {
        if (s == null || s.length() == 0) return "";
        String t = preprocess(s);
        int n = t.length();
        int[] p = new int[n];
        int center = 0, right = 0;

        for (int i = 1; i < n - 1; i++) {
            if (i < right) {
                p[i] = Math.min(right - i, p[2 * center - i]);
            }
            while (t.charAt(i + p[i] + 1) == t.charAt(i - p[i] - 1)) {
                p[i]++;
            }
            if (i + p[i] > right) {
                center = i;
                right = i + p[i];
            }
        }

        int maxLen = 0, centerIdx = 0;
        for (int i = 1; i < n - 1; i++) {
            if (p[i] > maxLen) {
                maxLen = p[i];
                centerIdx = i;
            }
        }
        int start = (centerIdx - maxLen) / 2;
        return s.substring(start, start + maxLen);
    }

    private static String preprocess(String s) {
        StringBuilder sb = new StringBuilder();
        sb.append('^');
        for (char ch : s.toCharArray()) {
            sb.append('#').append(ch);
        }
        sb.append("#$");
        return sb.toString();
    }

    public static int countPalindromes(String s) {
        if (s == null || s.length() == 0) return 0;
        String t = preprocess(s);
        int n = t.length();
        int[] p = new int[n];
        int center = 0, right = 0, count = 0;

        for (int i = 1; i < n - 1; i++) {
            if (i < right) {
                p[i] = Math.min(right - i, p[2 * center - i]);
            }
            while (t.charAt(i + p[i] + 1) == t.charAt(i - p[i] - 1)) {
                p[i]++;
            }
            if (i + p[i] > right) {
                center = i;
                right = i + p[i];
            }
            count += (p[i] + 1) / 2;
        }
        return count;
    }
}
