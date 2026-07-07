package com.algo.lab27;

public class LCPArray {

    public static int[] build(String s, int[] sa) {
        int n = s.length();
        int[] lcp = new int[n];
        int[] inv = new int[n];
        for (int i = 0; i < n; i++) inv[sa[i]] = i;
        int k = 0;
        for (int i = 0; i < n; i++) {
            int rank = inv[i];
            if (rank == n - 1) {
                k = 0;
                continue;
            }
            int j = sa[rank + 1];
            while (i + k < n && j + k < n && s.charAt(i + k) == s.charAt(j + k)) k++;
            lcp[rank] = k;
            if (k > 0) k--;
        }
        return lcp;
    }
}
