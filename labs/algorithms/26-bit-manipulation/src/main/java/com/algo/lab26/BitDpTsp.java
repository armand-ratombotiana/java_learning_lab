package com.algo.lab26;

import java.util.Arrays;

public class BitDpTsp {

    private BitDpTsp() {}

    public static int solve(int[][] dist) {
        int n = dist.length;
        int size = 1 << n;
        int INF = Integer.MAX_VALUE / 2;
        int[][] dp = new int[size][n];
        for (int[] row : dp) {
            Arrays.fill(row, INF);
        }
        dp[1][0] = 0;
        for (int mask = 1; mask < size; mask++) {
            for (int v = 0; v < n; v++) {
                if ((mask & (1 << v)) == 0) continue;
                if (dp[mask][v] == INF) continue;
                for (int u = 0; u < n; u++) {
                    if ((mask & (1 << u)) != 0) continue;
                    int next = mask | (1 << u);
                    dp[next][u] = Math.min(dp[next][u], dp[mask][v] + dist[v][u]);
                }
            }
        }
        int full = size - 1;
        int ans = INF;
        for (int v = 0; v < n; v++) {
            if (dp[full][v] != INF) {
                ans = Math.min(ans, dp[full][v] + dist[v][0]);
            }
        }
        return ans;
    }
}
