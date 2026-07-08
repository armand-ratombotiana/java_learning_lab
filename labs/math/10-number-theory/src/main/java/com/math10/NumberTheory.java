package com.math10;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NumberTheory {

    public static long gcd(long a, long b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    public static long lcm(long a, long b) {
        return a / gcd(a, b) * b;
    }

    public static long[] extendedEuclid(long a, long b) {
        if (b == 0) {
            return new long[]{a, 1, 0};
        }
        long[] vals = extendedEuclid(b, a % b);
        long d = vals[0];
        long x = vals[2];
        long y = vals[1] - (a / b) * vals[2];
        return new long[]{d, x, y};
    }

    public static long modularInverse(long a, long m) {
        long[] vals = extendedEuclid(a, m);
        if (vals[0] != 1) {
            throw new ArithmeticException("Inverse does not exist: gcd(" + a + ", " + m + ") = " + vals[0]);
        }
        return (vals[1] % m + m) % m;
    }

    public static long modPow(long base, long exp, long mod) {
        if (mod == 1) return 0;
        long result = 1;
        base = base % mod;
        while (exp > 0) {
            if ((exp & 1) == 1) {
                result = (result * base) % mod;
            }
            base = (base * base) % mod;
            exp >>= 1;
        }
        return result;
    }

    public static BigInteger modPow(BigInteger base, BigInteger exp, BigInteger mod) {
        return base.modPow(exp, mod);
    }

    public static boolean isPrime(long n) {
        if (n < 2) return false;
        if (n == 2 || n == 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (long i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }

    public static boolean millerRabin(BigInteger n, int iterations) {
        if (n.compareTo(BigInteger.ONE) <= 0) return false;
        if (n.compareTo(BigInteger.valueOf(3)) <= 0) return true;
        if (n.mod(BigInteger.TWO).equals(BigInteger.ZERO)) return false;

        BigInteger d = n.subtract(BigInteger.ONE);
        int s = 0;
        while (d.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            d = d.divide(BigInteger.TWO);
            s++;
        }

        SecureRandom random = new SecureRandom();
        for (int i = 0; i < iterations; i++) {
            BigInteger a;
            do {
                a = new BigInteger(n.bitLength() - 1, random);
            } while (a.compareTo(BigInteger.TWO) < 0 || a.compareTo(n.subtract(BigInteger.TWO)) > 0);

            BigInteger x = a.modPow(d, n);
            if (x.equals(BigInteger.ONE) || x.equals(n.subtract(BigInteger.ONE))) continue;

            boolean composite = true;
            for (int r = 0; r < s - 1; r++) {
                x = x.modPow(BigInteger.TWO, n);
                if (x.equals(BigInteger.ONE)) return false;
                if (x.equals(n.subtract(BigInteger.ONE))) {
                    composite = false;
                    break;
                }
            }
            if (composite) return false;
        }
        return true;
    }

    public static List<Long> primeFactors(long n) {
        List<Long> factors = new ArrayList<>();
        while (n % 2 == 0) {
            factors.add(2L);
            n /= 2;
        }
        for (long i = 3; i * i <= n; i += 2) {
            while (n % i == 0) {
                factors.add(i);
                n /= i;
            }
        }
        if (n > 1) factors.add(n);
        return factors;
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
        long prod = 1;
        for (long m : moduli) prod *= m;
        long result = 0;
        for (int i = 0; i < moduli.length; i++) {
            long pi = prod / moduli[i];
            long inv = modularInverse(pi % moduli[i], moduli[i]);
            result = (result + remainders[i] * pi * inv) % prod;
        }
        return (result + prod) % prod;
    }

    public static RSAData generateRSAKeyPair(int bitLength) {
        SecureRandom random = new SecureRandom();
        BigInteger p = BigInteger.probablePrime(bitLength / 2, random);
        BigInteger q = BigInteger.probablePrime(bitLength / 2, random);
        BigInteger n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        BigInteger e = BigInteger.valueOf(65537);
        BigInteger d = e.modInverse(phi);
        return new RSAData(n, e, d);
    }

    public static BigInteger rsaEncrypt(BigInteger plaintext, BigInteger n, BigInteger e) {
        return plaintext.modPow(e, n);
    }

    public static BigInteger rsaDecrypt(BigInteger ciphertext, BigInteger n, BigInteger d) {
        return ciphertext.modPow(d, n);
    }

    public static long pollardRho(long n) {
        if (n % 2 == 0) return 2;
        Random rand = new Random();
        long x = rand.nextLong() % (n - 2) + 2;
        long y = x;
        long c = rand.nextLong() % (n - 1) + 1;
        long d = 1;
        while (d == 1) {
            x = (modPow(x, 2, n) + c + n) % n;
            y = (modPow(y, 2, n) + c + n) % n;
            y = (modPow(y, 2, n) + c + n) % n;
            d = gcd(Math.abs(x - y), n);
        }
        return d == n ? pollardRho(n) : d;
    }

    public record RSAData(BigInteger n, BigInteger e, BigInteger d) {}

    public record CRTSolution(long x, long modulus) {}
}
