package com.algo.lab19;

/**
 * Euclidean algorithms for GCD and Extended Euclidean.
 * Standard GCD: O(log min(a,b)), Extended: O(log min(a,b)).
 */
public class GCD {

    private GCD() {}

    public static int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return Math.abs(a);
    }

    public static long gcd(long a, long b) {
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return Math.abs(a);
    }

    public static long lcm(int a, int b) {
        if (a == 0 || b == 0) return 0;
        return Math.abs((long) a * b) / gcd(a, b);
    }

    public static ExtendedGcdResult extendedGcd(int a, int b) {
        if (b == 0) {
            return new ExtendedGcdResult(a, 1, 0);
        }
        ExtendedGcdResult result = extendedGcd(b, a % b);
        return new ExtendedGcdResult(result.gcd, result.y, result.x - (a / b) * result.y);
    }

    public static class ExtendedGcdResult {
        public final long gcd;
        public final long x;
        public final long y;

        public ExtendedGcdResult(long gcd, long x, long y) {
            this.gcd = gcd;
            this.x = x;
            this.y = y;
        }
    }
}
