package com.algorithms.stringmatch;

/**
 * Implement KMP (Knuth-Morris-Pratt) Pattern Matching
 * Find all occurrences of a pattern in a text in O(n + m) time.
 *
 * Time Complexity: O(n + m)
 * Space Complexity: O(m)
 */
public class KMPSearch {

    public int firstOccurrence(String text, String pattern) {
        if (pattern.isEmpty()) return 0;
        int n = text.length(), m = pattern.length();
        int[] lps = computeLPS(pattern);

        int i = 0, j = 0;
        while (i < n) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++; j++;
                if (j == m) return i - j;
            } else if (j > 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }
        return -1;
    }

    private int[] computeLPS(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0, i = 1;
        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                lps[i++] = ++len;
            } else if (len > 0) {
                len = lps[len - 1];
            } else {
                i++;
            }
        }
        return lps;
    }

    public static void main(String[] args) {
        KMPSearch kmp = new KMPSearch();
        System.out.println("Test 1: " + kmp.firstOccurrence("hello", "ll") + " (expected: 2)");
        System.out.println("Test 2: " + kmp.firstOccurrence("aaaaa", "bba") + " (expected: -1)");
        System.out.println("Test 3: " + kmp.firstOccurrence("", "") + " (expected: 0)");
        System.out.println("Test 4: " + kmp.firstOccurrence("abcabcabc", "abc") + " (expected: 0)");
        System.out.println("Test 5: " + kmp.firstOccurrence("mississippi", "issip") + " (expected: 4)");

        // KMP should match the naive approach
        String text = "AABAACAADAABAABA";
        String pattern = "AABA";
        System.out.println("KMP finds pattern at: " + kmp.firstOccurrence(text, pattern) + " (expected: 0)");
    }
}
