package com.algo.lab39;

import java.math.BigInteger;
import java.util.List;

/**
 * Solver for Pell's equation: x^2 - d*y^2 = 1.
 * Uses the continued fraction expansion of sqrt(d) to find
 * fundamental solutions. The minimal solution corresponds to
 * a convergent of the continued fraction.
 */
public class PellEquation {

    public static BigInteger[] solve(long d) {
        long sqrtD = (long) Math.sqrt(d);
        if (sqrtD * sqrtD == d)
            throw new IllegalArgumentException("d must not be a perfect square");

        List<Long> expansion = ContinuedFraction.sqrtExpansion(d);
        int period = expansion.size() - 1;

        BigInteger x = BigInteger.ONE, y = BigInteger.ZERO;

        for (int k = 1; k <= 10000; k++) {
            int idx = (k - 1) % period;
            long a = expansion.get(idx + 1);

            // Compute convergent using recurrence
            if (k == 1) {
                x = BigInteger.valueOf(expansion.get(0));
                y = BigInteger.ONE;
            } else {
                BigInteger h2 = BigInteger.valueOf(k == 2 ? expansion.get(0) : 0);
                BigInteger h1 = BigInteger.ONE;
                // Recompute
                BigInteger h_2 = BigInteger.ZERO, h_1 = BigInteger.ONE;
                BigInteger k_2 = BigInteger.ONE, k_1 = BigInteger.ZERO;
                for (int i = 1; i <= k; i++) {
                    int termIdx = (i - 1) % period;
                    long ai = expansion.get(i == k ? 0 : termIdx + 1);
                    BigInteger hi = BigInteger.valueOf(ai).multiply(h_1).add(h_2);
                    BigInteger ki = BigInteger.valueOf(ai).multiply(k_1).add(k_2);
                    h_2 = h_1; h_1 = hi;
                    k_2 = k_1; k_1 = ki;
                }
                x = h_2; y = k_2;
            }

            // Check fundamental solution
            BigInteger lhs = x.multiply(x).subtract(BigInteger.valueOf(d).multiply(y).multiply(y));
            if (lhs.equals(BigInteger.ONE)) {
                return new BigInteger[]{x, y};
            }
        }
        return new BigInteger[]{BigInteger.valueOf(-1), BigInteger.valueOf(-1)};
    }
}
