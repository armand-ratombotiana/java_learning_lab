package com.algo.lab19;

/**
 * Chinese Remainder Theorem solver.
 * Finds x such that x ≡ a_i (mod m_i) for pairwise coprime moduli.
 * Time: O(n log M) where M is the product of moduli.
 */
public class ChineseRemainder {

    private ChineseRemainder() {}

    public static long solve(long[] remainders, long[] moduli) {
        if (remainders.length != moduli.length || remainders.length == 0) {
            throw new IllegalArgumentException("Arrays must be non-empty and same length");
        }
        long M = 1;
        for (long m : moduli) M *= m;
        long result = 0;
        for (int i = 0; i < remainders.length; i++) {
            long Mi = M / moduli[i];
            long inv = ModularArithmetic.modInverse(Mi % moduli[i], moduli[i]);
            result = (result + remainders[i] * Mi % M * inv % M) % M;
        }
        return result;
    }

    public static long solveTwo(long a1, long m1, long a2, long m2) {
        return ModularArithmetic.crt2(a1, m1, a2, m2);
    }
}
