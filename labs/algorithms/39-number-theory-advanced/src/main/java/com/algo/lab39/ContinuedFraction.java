package com.algo.lab39;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Continued fraction expansion for sqrt(n).
 * Computes the periodic continued fraction representation
 * of the square root of a non-square positive integer n.
 * Used for solving Pell's equation and Diophantine approximation.
 */
public class ContinuedFraction {

    public static List<Long> sqrtExpansion(long n) {
        List<Long> terms = new ArrayList<>();
        long a0 = (long) Math.sqrt(n);
        if (a0 * a0 == n) { terms.add(a0); return terms; }
        long m = 0, d = 1, a = a0;
        terms.add(a);
        while (true) {
            m = d * a - m;
            d = (n - m * m) / d;
            a = (a0 + m) / d;
            terms.add(a);
            if (a == 2 * a0) break;
        }
        return terms;
    }

    public static List<Long> convergent(List<Long> expansion, int k) {
        int period = expansion.size() - 1;
        int idx = (k - 1) % period;
        long h2 = 0, h1 = 1, h = expansion.get(0);
        long k2 = 1, k1 = 0, kk = 1;
        for (int i = 1; i <= k; i++) {
            int termIdx = (i - 1) % period;
            long a = expansion.get(termIdx + 1);
            h = a * h1 + h2; h2 = h1; h1 = h;
            kk = a * k1 + k2; k2 = k1; k1 = kk;
        }
        List<Long> result = new ArrayList<>();
        result.add(h);
        result.add(kk);
        return result;
    }

    public static String expansionToString(List<Long> expansion) {
        StringBuilder sb = new StringBuilder("[");
        sb.append(expansion.get(0)).append("; ");
        for (int i = 1; i < expansion.size() - 1; i++) sb.append(expansion.get(i)).append(", ");
        sb.append(expansion.get(expansion.size() - 1)).append("]");
        return sb.toString();
    }
}
