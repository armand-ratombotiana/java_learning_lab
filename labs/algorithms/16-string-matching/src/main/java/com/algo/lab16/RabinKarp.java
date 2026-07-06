package com.algo.lab16;

import java.util.ArrayList;
import java.util.List;

/**
 * Rabin-Karp string matching algorithm using rolling hash.
 * Uses a sliding window hash for efficient pattern matching.
 * Time: O(n + m) average, O(n*m) worst-case, Space: O(1)
 */
public class RabinKarp {

    private static final int BASE = 256;
    private static final int MOD = 101;

    private RabinKarp() {}

    public static List<Integer> search(String text, String pattern) {
        List<Integer> result = new ArrayList<>();
        if (pattern == null || pattern.isEmpty() || text == null || text.length() < pattern.length()) {
            return result;
        }
        int n = text.length();
        int m = pattern.length();
        int patternHash = 0;
        int textHash = 0;
        int h = 1;
        for (int i = 0; i < m - 1; i++) {
            h = (h * BASE) % MOD;
        }
        for (int i = 0; i < m; i++) {
            patternHash = (BASE * patternHash + pattern.charAt(i)) % MOD;
            textHash = (BASE * textHash + text.charAt(i)) % MOD;
        }
        for (int i = 0; i <= n - m; i++) {
            if (patternHash == textHash) {
                int j = 0;
                while (j < m && text.charAt(i + j) == pattern.charAt(j)) {
                    j++;
                }
                if (j == m) {
                    result.add(i);
                }
            }
            if (i < n - m) {
                textHash = (BASE * (textHash - text.charAt(i) * h) + text.charAt(i + m)) % MOD;
                if (textHash < 0) {
                    textHash += MOD;
                }
            }
        }
        return result;
    }
}
