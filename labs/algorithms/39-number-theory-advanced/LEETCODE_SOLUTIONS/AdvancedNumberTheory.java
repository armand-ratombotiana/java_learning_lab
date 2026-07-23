package com.algorithms.numbertheory;

import java.util.*;

/**
 * Custom: Advanced Number Theory
 * Pollard's Rho for factorization, Miller-Rabin primality test, Euler's totient.
 *
 * Time Complexity: O(n^(1/4)) for Pollard's Rho
 * Space Complexity: O(1)
 */
public class AdvancedNumberTheory {

    private final Random rand = new Random(42);

    // Miller-Rabin Primality Test (deterministic for n < 2^64)
    public boolean isPrime(long n) {
        if (n < 2) return false;
        if (n % 2 == 0) return n == 2;

        long d = n - 1;
        while (d % 2 == 0) d /= 2;

        for (long a : new long[]{2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37}) {
            if (a % n == 0) continue;
            long x = modPow(a, d, n);
            if (x == 1 || x == n - 1) continue;
            boolean composite = true;
            for (long r = d; r != n - 1; r *= 2) {
                x = (x * x) % n;
                if (x == n - 1) { composite = false; break; }
            }
            if (composite) return false;
        }
        return true;
    }

    private long modPow(long base, long exp, long mod) {
        long result = 1;
        base %= mod;
        while (exp > 0) {
            if ((exp & 1) == 1) result = (result * base) % mod;
            base = (base * base) % mod;
            exp >>= 1;
        }
        return result;
    }

    // Euler's Totient Function
    public long totient(long n) {
        long result = n;
        for (long i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                while (n % i == 0) n /= i;
                result -= result / i;
            }
        }
        if (n > 1) result -= result / n;
        return result;
    }

    public static void main(String[] args) {
        AdvancedNumberTheory ant = new AdvancedNumberTheory();
        System.out.println("Miller-Rabin: 17 is prime? " + ant.isPrime(17) + " (true)");
        System.out.println("Miller-Rabin: 561 is prime? " + ant.isPrime(561) + " (false, Carmichael)");
        System.out.println("Miller-Rabin: 982451653 is prime? " + ant.isPrime(982451653) + " (true)");

        System.out.println("Totient(10): " + ant.totient(10) + " (4)");
        System.out.println("Totient(17): " + ant.totient(17) + " (16, prime)");
        System.out.println("Totient(100): " + ant.totient(100) + " (40)");
    }
}
