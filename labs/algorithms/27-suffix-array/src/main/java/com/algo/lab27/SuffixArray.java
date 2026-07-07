package com.algo.lab27;

import java.util.Arrays;

public class SuffixArray {

    public static int[] build(String s) {
        int n = s.length();
        Integer[] sa = new Integer[n];
        int[] rank = new int[n];
        int[] tmp = new int[n];
        for (int i = 0; i < n; i++) {
            sa[i] = i;
            rank[i] = s.charAt(i);
        }
        int k = 1;
        while (true) {
            int[] r = rank;
            int kk = k;
            Arrays.sort(sa, (a, b) -> {
                if (r[a] != r[b]) return Integer.compare(r[a], r[b]);
                int ra = a + kk < n ? r[a + kk] : -1;
                int rb = b + kk < n ? r[b + kk] : -1;
                return Integer.compare(ra, rb);
            });
            tmp[sa[0]] = 0;
            int p = 0;
            for (int i = 1; i < n; i++) {
                int prev = sa[i - 1];
                int cur = sa[i];
                if (r[prev] != r[cur] ||
                    (prev + kk < n ? r[prev + kk] : -1) != (cur + kk < n ? r[cur + kk] : -1)) {
                    p++;
                }
                tmp[cur] = p;
            }
            System.arraycopy(tmp, 0, rank, 0, n);
            if (p == n - 1) break;
            k <<= 1;
        }
        int[] result = new int[n];
        for (int i = 0; i < n; i++) result[i] = sa[i];
        return result;
    }
}
