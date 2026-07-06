package com.algo.lab16;

import java.util.ArrayList;
import java.util.List;

/**
 * Boyer-Moore string matching algorithm using the bad character rule.
 * Skips sections of the text to achieve sublinear performance on average.
 * Time: O(n/m) average, O(n*m) worst-case, Space: O(character set)
 */
public class BoyerMoore {

    private static final int ALPHABET_SIZE = 256;

    private BoyerMoore() {}

    public static List<Integer> search(String text, String pattern) {
        List<Integer> result = new ArrayList<>();
        if (pattern == null || pattern.isEmpty() || text == null || text.length() < pattern.length()) {
            return result;
        }
        int[] badChar = buildBadCharTable(pattern);
        int n = text.length();
        int m = pattern.length();
        int shift = 0;
        while (shift <= n - m) {
            int j = m - 1;
            while (j >= 0 && pattern.charAt(j) == text.charAt(shift + j)) {
                j--;
            }
            if (j < 0) {
                result.add(shift);
                shift += (shift + m < n) ? m - badChar[text.charAt(shift + m)] : 1;
            } else {
                shift += Math.max(1, j - badChar[text.charAt(shift + j)]);
            }
        }
        return result;
    }

    private static int[] buildBadCharTable(String pattern) {
        int[] badChar = new int[ALPHABET_SIZE];
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            badChar[i] = -1;
        }
        for (int i = 0; i < pattern.length(); i++) {
            badChar[pattern.charAt(i)] = i;
        }
        return badChar;
    }
}
