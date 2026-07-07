package com.algo.lab26;

public class XorBasis {

    private final int[] basis;
    private static final int BITS = 31;

    public XorBasis() {
        basis = new int[BITS];
    }

    public void insert(int x) {
        for (int i = BITS - 1; i >= 0; i--) {
            if ((x & (1 << i)) == 0) continue;
            if (basis[i] == 0) {
                basis[i] = x;
                return;
            }
            x ^= basis[i];
        }
    }

    public int queryMax() {
        int result = 0;
        for (int i = BITS - 1; i >= 0; i--) {
            if ((result ^ basis[i]) > result) {
                result ^= basis[i];
            }
        }
        return result;
    }
}
