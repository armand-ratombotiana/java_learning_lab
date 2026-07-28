package com.ds.advanced.lab08;

import java.util.Arrays;

public class SuffixArray {
    private final String text;
    private final int[] sa;
    private final int[] lcp;

    public SuffixArray(String text) {
        this.text = text;
        int n = text.length();
        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) indices[i] = i;
        Arrays.sort(indices, (a, b) -> text.substring(a).compareTo(text.substring(b)));
        sa = new int[n];
        for (int i = 0; i < n; i++) sa[i] = indices[i];
        lcp = buildLCP();
    }

    private int[] buildLCP() {
        int n = text.length();
        int[] rank = new int[n];
        for (int i = 0; i < n; i++) rank[sa[i]] = i;
        int[] lcp = new int[n - 1];
        int k = 0;
        for (int i = 0; i < n; i++) {
            if (rank[i] == n - 1) { k = 0; continue; }
            int j = sa[rank[i] + 1];
            while (i + k < n && j + k < n && text.charAt(i + k) == text.charAt(j + k)) k++;
            lcp[rank[i]] = k;
            if (k > 0) k--;
        }
        return lcp;
    }

    public int[] getSuffixArray() { return sa; }
    public int[] getLCP() { return lcp; }

    public boolean patternSearch(String pat) {
        int l = 0, r = text.length() - 1;
        while (l <= r) {
            int mid = (l + r) / 2;
            int cmp = text.substring(sa[mid]).compareTo(pat);
            if (cmp == 0) return true;
            if (cmp < 0) l = mid + 1;
            else r = mid - 1;
        }
        return false;
    }

    public String longestRepeatedSubstring() {
        int maxLen = 0, idx = 0;
        for (int i = 0; i < lcp.length; i++) {
            if (lcp[i] > maxLen) { maxLen = lcp[i]; idx = sa[i]; }
        }
        return text.substring(idx, idx + maxLen);
    }
}