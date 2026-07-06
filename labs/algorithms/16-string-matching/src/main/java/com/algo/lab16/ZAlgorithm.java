package com.algo.lab16;

import java.util.ArrayList;
import java.util.List;

/**
 * Z-algorithm for string matching.
 * Computes the Z-array where Z[i] is the longest substring starting at i
 * that matches the prefix of the string.
 * Time: O(n + m), Space: O(n + m)
 */
public class ZAlgorithm {

    private ZAlgorithm() {}

    public static List<Integer> search(String text, String pattern) {
        List<Integer> result = new ArrayList<>();
        if (pattern == null || pattern.isEmpty() || text == null || text.length() < pattern.length()) {
            return result;
        }
        String concat = pattern + "$" + text;
        int[] z = computeZ(concat);
        int m = pattern.length();
        for (int i = 0; i < z.length; i++) {
            if (z[i] == m) {
                result.add(i - m - 1);
            }
        }
        return result;
    }

    public static int[] computeZ(String s) {
        int n = s.length();
        int[] z = new int[n];
        int l = 0, r = 0;
        for (int i = 1; i < n; i++) {
            if (i <= r) {
                z[i] = Math.min(r - i + 1, z[i - l]);
            }
            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) {
                z[i]++;
            }
            if (i + z[i] - 1 > r) {
                l = i;
                r = i + z[i] - 1;
            }
        }
        return z;
    }
}
