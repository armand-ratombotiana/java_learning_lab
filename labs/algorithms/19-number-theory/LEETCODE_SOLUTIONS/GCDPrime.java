package com.algorithms.numbertheory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Custom: Number Theory Utilities
 * GCD, prime sieve, prime factorization.
 *
 * Time Complexity: O(log min(a,b)) for gcd, O(n log log n) for sieve
 * Space Complexity: O(n) for sieve
 */
public class GCDPrime {

    public int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    public int lcm(int a, int b) {
        return a / gcd(a, b) * b;
    }

    public boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; i * i <= n; i++) if (n % i == 0) return false;
        return true;
    }

    public boolean[] sieve(int n) {
        boolean[] prime = new boolean[n + 1];
        Arrays.fill(prime, true);
        prime[0] = prime[1] = false;
        for (int i = 2; i * i <= n; i++) {
            if (prime[i]) {
                for (int j = i * i; j <= n; j += i) prime[j] = false;
            }
        }
        return prime;
    }

    public List<Integer> primeFactors(int n) {
        List<Integer> factors = new ArrayList<>();
        for (int i = 2; i * i <= n; i++) {
            while (n % i == 0) { factors.add(i); n /= i; }
        }
        if (n > 1) factors.add(n);
        return factors;
    }

    public static void main(String[] args) {
        GCDPrime nt = new GCDPrime();
        System.out.println("GCD(12, 18): " + nt.gcd(12, 18) + " (expected: 6)");
        System.out.println("LCM(12, 18): " + nt.lcm(12, 18) + " (expected: 36)");
        System.out.println("Is 17 prime: " + nt.isPrime(17) + " (expected: true)");
        System.out.println("Is 1 prime: " + nt.isPrime(1) + " (expected: false)");

        boolean[] primes = nt.sieve(30);
        System.out.print("Primes up to 30: ");
        for (int i = 2; i <= 30; i++) if (primes[i]) System.out.print(i + " ");
        System.out.println("(expected: 2 3 5 7 11 13 17 19 23 29)");

        System.out.println("Prime factors of 84: " + nt.primeFactors(84) + " (expected: [2, 2, 3, 7])");
    }
}
