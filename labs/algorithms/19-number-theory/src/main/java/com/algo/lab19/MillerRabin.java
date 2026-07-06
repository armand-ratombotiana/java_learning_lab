package com.algo.lab19;

import java.math.BigInteger;
import java.util.random.RandomGenerator;

/**
 * Miller-Rabin primality test.
 * A probabilistic test that can be made deterministic for 32/64-bit integers.
 * Time: O(k log^3 n) where k is the number of rounds.
 */
public class MillerRabin {

    private static final int[] DETERMINISTIC_BASES_32 = {2, 7, 61};
    private static final long[] DETERMINISTIC_BASES_64 = {2, 325, 9375, 28178, 450775, 9780504, 1795265022L};

    private MillerRabin() {}

    public static boolean isPrime(int n) {
        if (n < 2) return false;
        if (n == 2 || n == 3 || n == 5) return true;
        if (n % 2 == 0) return false;
        int d = n - 1;
        int s = 0;
        while (d % 2 == 0) {
            d /= 2;
            s++;
        }
        for (int a : DETERMINISTIC_BASES_32) {
            if (a % n == 0) continue;
            if (!checkComposite(n, a, d, s)) return false;
        }
        return true;
    }

    public static boolean isPrime(long n) {
        if (n < 2) return false;
        if (n == 2 || n == 3 || n == 5) return true;
        if (n % 2 == 0) return false;
        long d = n - 1;
        int s = 0;
        while (d % 2 == 0) {
            d /= 2;
            s++;
        }
        for (long a : DETERMINISTIC_BASES_64) {
            if (a % n == 0) continue;
            if (!checkComposite(n, a, d, s)) return false;
        }
        return true;
    }

    public static boolean isProbablePrime(BigInteger n, int rounds) {
        return n.isProbablePrime(rounds);
    }

    private static boolean checkComposite(int n, int a, int d, int s) {
        long x = modPow(a, d, n);
        if (x == 1 || x == n - 1) return true;
        for (int r = 1; r < s; r++) {
            x = (x * x) % n;
            if (x == n - 1) return true;
        }
        return false;
    }

    private static boolean checkComposite(long n, long a, long d, int s) {
        long x = modPow(a, d, n);
        if (x == 1 || x == n - 1) return true;
        for (int r = 1; r < s; r++) {
            x = mulMod(x, x, n);
            if (x == n - 1) return true;
        }
        return false;
    }

    private static long modPow(long base, long exp, long mod) {
        long result = 1;
        base %= mod;
        while (exp > 0) {
            if ((exp & 1) == 1) result = mulMod(result, base, mod);
            base = mulMod(base, base, mod);
            exp >>= 1;
        }
        return result;
    }

    private static long mulMod(long a, long b, long mod) {
        long result = 0;
        a %= mod;
        while (b > 0) {
            if ((b & 1) == 1) result = (result + a) % mod;
            a = (a * 2) % mod;
            b >>= 1;
        }
        return result;
    }
}
