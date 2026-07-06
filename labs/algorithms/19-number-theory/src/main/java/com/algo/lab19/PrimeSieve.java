package com.algo.lab19;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Segmented sieve for generating primes up to a limit.
 * Uses a segmented approach to reduce memory usage.
 * Time: O(n log log n), Space: O(sqrt(n) + segment size)
 */
public class PrimeSieve {

    private PrimeSieve() {}

    public static List<Integer> sieve(int limit) {
        List<Integer> primes = new ArrayList<>();
        if (limit < 2) return primes;
        boolean[] isPrime = new boolean[limit + 1];
        for (int i = 2; i <= limit; i++) isPrime[i] = true;
        for (int i = 2; i * i <= limit; i++) {
            if (isPrime[i]) {
                for (int j = i * i; j <= limit; j += i) {
                    isPrime[j] = false;
                }
            }
        }
        for (int i = 2; i <= limit; i++) {
            if (isPrime[i]) primes.add(i);
        }
        return primes;
    }

    public static List<Integer> segmentedSieve(int limit) {
        List<Integer> primes = new ArrayList<>();
        if (limit < 2) return primes;
        int segmentSize = (int) Math.sqrt(limit) + 1;
        List<Integer> basePrimes = sieve(segmentSize);
        primes.addAll(basePrimes);
        for (int low = segmentSize; low <= limit; low += segmentSize) {
            int high = Math.min(low + segmentSize - 1, limit);
            BitSet mark = new BitSet(high - low + 1);
            mark.set(0, high - low + 1);
            for (int p : basePrimes) {
                int start = Math.max(p * p, ((low + p - 1) / p) * p);
                for (int j = start; j <= high; j += p) {
                    mark.clear(j - low);
                }
            }
            for (int i = 0; i <= high - low; i++) {
                if (mark.get(i)) {
                    primes.add(low + i);
                }
            }
        }
        return primes;
    }

    public static boolean isPrime(int n) {
        if (n < 2) return false;
        if (n == 2 || n == 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }
}
