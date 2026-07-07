package com.algo.lab39;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Pollard's Rho algorithm for integer factorization.
 * Uses Floyd's cycle detection and a pseudo-random function
 * f(x) = (x^2 + c) mod n to find non-trivial factors.
 * Handles composite numbers up to 2^63 efficiently.
 */
public class PollardRho {
    private static final Random rng = new Random();

    private PollardRho() {}

    public static List<Long> factorize(long n) {
        List<Long> factors = new ArrayList<>();
        factor(n, factors);
        return factors;
    }

    private static void factor(long n, List<Long> factors) {
        if (n == 1) return;
        if (isPrime(n)) { factors.add(n); return; }
        long d = pollardRho(n);
        factor(d, factors);
        factor(n / d, factors);
    }

    private static long pollardRho(long n) {
        if (n % 2 == 0) return 2;
        long x = Math.abs(rng.nextLong()) % (n - 2) + 2;
        long y = x;
        long c = Math.abs(rng.nextLong()) % (n - 1) + 1;
        long d = 1;
        while (d == 1) {
            x = (mulMod(x, x, n) + c) % n;
            y = (mulMod(y, y, n) + c) % n;
            y = (mulMod(y, y, n) + c) % n;
            d = gcd(Math.abs(x - y), n);
        }
        return d == n ? pollardRho(n) : d;
    }

    private static long mulMod(long a, long b, long mod) {
        return BigInteger.valueOf(a).multiply(BigInteger.valueOf(b)).mod(BigInteger.valueOf(mod)).longValue();
    }

    private static long gcd(long a, long b) {
        while (b != 0) { long t = b; b = a % b; a = t; }
        return a;
    }

    public static boolean isPrime(long n) {
        if (n < 2) return false;
        if (n % 2 == 0) return n == 2;
        long d = n - 1;
        int s = 0;
        while (d % 2 == 0) { d /= 2; s++; }
        for (long a : new long[]{2, 3, 5, 7, 11, 13, 17}) {
            if (a >= n) continue;
            long x = BigInteger.valueOf(a).modPow(BigInteger.valueOf(d), BigInteger.valueOf(n)).longValue();
            if (x == 1 || x == n - 1) continue;
            boolean composite = true;
            for (int r = 0; r < s - 1; r++) {
                x = BigInteger.valueOf(x).multiply(BigInteger.valueOf(x)).mod(BigInteger.valueOf(n)).longValue();
                if (x == n - 1) { composite = false; break; }
            }
            if (composite) return false;
        }
        return true;
    }
}
