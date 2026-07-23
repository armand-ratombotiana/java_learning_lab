package com.algorithms.stringadv;

/**
 * Custom: Advanced String Algorithms
 * Manacher's algorithm for longest palindrome, Z-algorithm for pattern matching.
 *
 * Time Complexity: O(n) for Manacher/Z
 * Space Complexity: O(n)
 */
public class AdvancedStringAlgorithms {

    // Manacher's Algorithm - Longest Palindromic Substring in O(n)
    public String manacherLongestPalindrome(String s) {
        if (s == null || s.length() == 0) return "";
        char[] t = new char[2 * s.length() + 1];
        for (int i = 0; i < s.length(); i++) {
            t[2 * i] = '#';
            t[2 * i + 1] = s.charAt(i);
        }
        t[2 * s.length()] = '#';

        int n = t.length;
        int[] p = new int[n];
        int center = 0, right = 0;
        for (int i = 0; i < n; i++) {
            int mirror = 2 * center - i;
            if (i < right) p[i] = Math.min(right - i, p[mirror]);
            while (i - p[i] - 1 >= 0 && i + p[i] + 1 < n && t[i - p[i] - 1] == t[i + p[i] + 1]) p[i]++;
            if (i + p[i] > right) { center = i; right = i + p[i]; }
        }

        int maxLen = 0, centerIdx = 0;
        for (int i = 0; i < n; i++) {
            if (p[i] > maxLen) { maxLen = p[i]; centerIdx = i; }
        }
        int start = (centerIdx - maxLen) / 2;
        return s.substring(start, start + maxLen);
    }

    // Z-Algorithm for pattern matching
    public int[] zAlgorithm(String s) {
        int n = s.length();
        int[] z = new int[n];
        int l = 0, r = 0;
        for (int i = 1; i < n; i++) {
            if (i <= r) z[i] = Math.min(r - i + 1, z[i - l]);
            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) z[i]++;
            if (i + z[i] - 1 > r) { l = i; r = i + z[i] - 1; }
        }
        return z;
    }

    public int searchWithZ(String text, String pattern) {
        String combined = pattern + "$" + text;
        int[] z = zAlgorithm(combined);
        for (int i = pattern.length() + 1; i < combined.length(); i++) {
            if (z[i] == pattern.length()) return i - pattern.length() - 1;
        }
        return -1;
    }

    public static void main(String[] args) {
        AdvancedStringAlgorithms asa = new AdvancedStringAlgorithms();
        System.out.println("Manacher 'babad': " + asa.manacherLongestPalindrome("babad") + " (expected: bab or aba)");
        System.out.println("Manacher 'cbbd': " + asa.manacherLongestPalindrome("cbbd") + " (expected: bb)");
        System.out.println("Manacher 'a': " + asa.manacherLongestPalindrome("a") + " (expected: a)");

        System.out.println("Z-search in 'aabcaabx' for 'aab': " + asa.searchWithZ("aabcaabx", "aab") + " (expected: 0)");
        System.out.println("Z-search in 'hello' for 'll': " + asa.searchWithZ("hello", "ll") + " (expected: 2)");
        System.out.println("Z-search in 'aaaaa' for 'bba': " + asa.searchWithZ("aaaaa", "bba") + " (expected: -1)");
    }
}
