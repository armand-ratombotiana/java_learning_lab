package com.algo.lab40;

import java.util.*;

/**
 * Burrows-Wheeler Transform (BWT) and its inverse.
 * BWT is a reversible transformation that groups similar characters together,
 * improving compression ratios. Used in bzip2 compression. The forward
 * transform sorts all rotations of the input; the inverse uses the
 * last-first (LF) mapping property.
 */
public class BurrowsWheeler {

    public static String transform(String s) {
        int n = s.length();
        String[] rotations = new String[n];
        String doubled = s + s;
        for (int i = 0; i < n; i++) {
            rotations[i] = doubled.substring(i, i + n);
        }
        Arrays.sort(rotations);
        StringBuilder lastCol = new StringBuilder();
        int originalIdx = 0;
        for (int i = 0; i < n; i++) {
            lastCol.append(rotations[i].charAt(n - 1));
            if (rotations[i].equals(s)) originalIdx = i;
        }
        return lastCol.toString() + "|" + originalIdx;
    }

    public static String inverse(String transformed) {
        int sep = transformed.lastIndexOf('|');
        String lastCol = transformed.substring(0, sep);
        int originalIdx = Integer.parseInt(transformed.substring(sep + 1));
        int n = lastCol.length();

        char[] firstCol = lastCol.toCharArray();
        Arrays.sort(firstCol);

        int[] next = new int[n];
        boolean[] used = new boolean[n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (!used[j] && lastCol.charAt(j) == firstCol[i]) {
                    next[i] = j;
                    used[j] = true;
                    break;
                }
            }
        }

        StringBuilder result = new StringBuilder();
        int idx = originalIdx;
        for (int i = 0; i < n; i++) {
            idx = next[idx];
            result.append(lastCol.charAt(idx));
        }
        return result.toString();
    }

    public static String runLengthEncode(String s) {
        StringBuilder sb = new StringBuilder();
        int count = 1;
        for (int i = 1; i <= s.length(); i++) {
            if (i < s.length() && s.charAt(i) == s.charAt(i - 1)) {
                count++;
            } else {
                sb.append(s.charAt(i - 1));
                if (count > 1) sb.append(count);
                count = 1;
            }
        }
        return sb.toString();
    }

    public static String runLengthDecode(String s) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < s.length()) {
            char ch = s.charAt(i++);
            StringBuilder num = new StringBuilder();
            while (i < s.length() && Character.isDigit(s.charAt(i))) {
                num.append(s.charAt(i++));
            }
            int count = num.length() > 0 ? Integer.parseInt(num.toString()) : 1;
            for (int j = 0; j < count; j++) sb.append(ch);
        }
        return sb.toString();
    }
}
