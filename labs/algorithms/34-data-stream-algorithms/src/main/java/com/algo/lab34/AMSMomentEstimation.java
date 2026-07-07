package com.algo.lab34;

import java.util.Random;

public class AMSMomentEstimation {

    private final int m;
    private final int[] positions;
    private final int[] values;
    private final int n;
    private static final Random random = new Random();

    public AMSMomentEstimation(int m, int[] stream) {
        this.m = m;
        this.n = stream.length;
        positions = new int[m];
        values = new int[m];
        for (int j = 0; j < m; j++) {
            positions[j] = random.nextInt(n);
            values[j] = stream[positions[j]];
        }
    }

    public long estimateF2(int[] stream) {
        long sum = 0;
        for (int j = 0; j < m; j++) {
            int c = 0;
            for (int i = positions[j]; i < n; i++) {
                if (stream[i] == values[j]) c++;
            }
            long Y = (long) n * (2L * c - 1);
            sum += Y;
        }
        return sum / m;
    }
}
