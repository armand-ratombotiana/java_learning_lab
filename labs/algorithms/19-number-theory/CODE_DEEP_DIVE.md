# Code Deep Dive — Number Theory

## Segmented Sieve Implementation

The segmented sieve handles ranges that don't fit in memory by computing primes up to sqrt(upper) and then processing the range in segments. Each segment is a boolean array representing the range [low, high]. Multiples of each base prime are marked within the segment.

`java
List<Integer> segmentedSieve(long low, long high) {
    int limit = (int) Math.sqrt(high);
    List<Integer> basePrimes = simpleSieve(limit);
    int segmentSize = (int) (high - low + 1);
    boolean[] isPrime = new boolean[segmentSize];
    Arrays.fill(isPrime, true);
    for (int prime : basePrimes) {
        long start = Math.max(prime * prime, 
            (low + prime - 1) / prime * prime);
        for (long j = start; j <= high; j += prime)
            isPrime[(int)(j - low)] = false;
    }
    // Collect primes
    List<Integer> result = new ArrayList<>();
    for (int i = 0; i < segmentSize; i++)
        if (isPrime[i]) result.add((int)(low + i));
    return result;
}
`

## Miller-Rabin Deterministic Bases

For 32-bit integers, use bases {2, 7, 61}. For 64-bit, use {2, 325, 9375, 28178, 450775, 9780504, 1795265022}. These sets are proven to cover all numbers in their ranges.

## Fast CRT Implementation

CRT requires computing the modular inverse of N/n_i modulo n_i for each congruence. These inverses can be precomputed if the system is reused.
