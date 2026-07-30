package com.math.deep.lab06;

import java.math.BigInteger;
import java.util.Random;

public class NumberTheory {

    private static final Random RNG = new Random();

    public static long gcd(long a, long b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0) {
            long t = b;
            b = a % b;
            a = t;
        }
        return a;
    }

    public static long[] extendedGcd(long a, long b) {
        if (b == 0) return new long[]{a, 1, 0};
        long[] vals = extendedGcd(b, a % b);
        return new long[]{vals[0], vals[2], vals[1] - (a / b) * vals[2]};
    }

    public static long modInverse(long a, long m) {
        long[] vals = extendedGcd(a, m);
        if (vals[0] != 1) throw new ArithmeticException("Inverse does not exist");
        return (vals[1] % m + m) % m;
    }

    public static long modPow(long base, long exp, long mod) {
        long result = 1;
        base = base % mod;
        while (exp > 0) {
            if ((exp & 1) == 1) result = (result * base) % mod;
            base = (base * base) % mod;
            exp >>= 1;
        }
        return result;
    }

    public static boolean isPrimeMillerRabin(long n, int k) {
        if (n < 2) return false;
        if (n == 2 || n == 3) return true;
        if (n % 2 == 0) return false;
        long d = n - 1;
        int s = 0;
        while (d % 2 == 0) { d /= 2; s++; }
        for (int i = 0; i < k; i++) {
            long a = 2 + Math.abs(RNG.nextLong()) % (n - 4);
            long x = modPow(a, d, n);
            if (x == 1 || x == n - 1) continue;
            boolean cont = false;
            for (int r = 0; r < s - 1; r++) {
                x = (x * x) % n;
                if (x == n - 1) { cont = true; break; }
            }
            if (cont) continue;
            return false;
        }
        return true;
    }

    public static long totient(long n) {
        long result = n;
        long temp = n;
        for (long p = 2; p * p <= temp; p++) {
            if (temp % p == 0) {
                while (temp % p == 0) temp /= p;
                result -= result / p;
            }
        }
        if (temp > 1) result -= result / temp;
        return result;
    }

    public static long chineseRemainder(long[] remainders, long[] moduli) {
        long M = 1;
        for (long m : moduli) M *= m;
        long result = 0;
        for (int i = 0; i < remainders.length; i++) {
            long Mi = M / moduli[i];
            long inv = modInverse(Mi % moduli[i], moduli[i]);
            result = (result + remainders[i] * Mi % M * inv % M) % M;
        }
        return result;
    }

    public static BigInteger factorialMod(int n, int mod) {
        BigInteger result = BigInteger.ONE;
        BigInteger modBI = BigInteger.valueOf(mod);
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i)).mod(modBI);
        }
        return result;
    }
}
