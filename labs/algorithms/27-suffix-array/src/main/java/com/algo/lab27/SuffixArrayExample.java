package com.algo.lab27;

public class SuffixArrayExample {

    public static int[] patternMatches(String text, String pattern, int[] sa) {
        int n = text.length();
        int m = pattern.length();
        int lo = 0, hi = n;
        int l = 0, r = n;
        while (lo < hi) {
            int mid = (lo + hi) / 2;
            int cmp = compare(text, sa[mid], pattern);
            if (cmp >= 0) hi = mid;
            else lo = mid + 1;
        }
        int left = lo;
        lo = 0;
        hi = n;
        while (lo < hi) {
            int mid = (lo + hi) / 2;
            int cmp = compare(text, sa[mid], pattern);
            if (cmp > 0) hi = mid;
            else lo = mid + 1;
        }
        int right = lo;
        int[] result = new int[right - left];
        for (int i = left; i < right; i++) {
            result[i - left] = sa[i];
        }
        return result;
    }

    private static int compare(String text, int start, String pattern) {
        int i = 0;
        while (i < pattern.length() && start + i < text.length()) {
            char tc = text.charAt(start + i);
            char pc = pattern.charAt(i);
            if (tc != pc) return tc - pc;
            i++;
        }
        if (i < pattern.length()) return -1;
        return 0;
    }

    public static String longestRepeatedSubstring(String s, int[] sa, int[] lcp) {
        int maxIdx = 0;
        for (int i = 1; i < lcp.length; i++) {
            if (lcp[i] > lcp[maxIdx]) maxIdx = i;
        }
        if (lcp[maxIdx] == 0) return "";
        return s.substring(sa[maxIdx], sa[maxIdx] + lcp[maxIdx]);
    }

    public static long countDistinctSubstrings(int n, int[] lcp) {
        long total = (long) n * (n + 1) / 2;
        long sum = 0;
        for (int v : lcp) sum += v;
        return total - sum;
    }
}
