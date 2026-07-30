# LeetCode 204 — Count Primes

## Problem

Given an integer `n`, return the **number of prime numbers** that are strictly less than `n`.

**Constraints:**
- `0 <= n <= 5 * 10^6`

---

## Solution: Sieve of Eratosthenes

```java
import java.util.*;

/**
 * LeetCode 204 — Count Primes
 * Sieve of Eratosthenes — optimal for counting primes up to 5*10^6.
 *
 * Time: O(n log log n) | Space: O(n)
 */
public class CountPrimes {

    public int countPrimes(int n) {
        if (n <= 2) return 0;

        boolean[] isPrime = new boolean[n];
        Arrays.fill(isPrime, true);
        isPrime[0] = false;
        isPrime[1] = false;

        for (int i = 2; i * i < n; i++) {
            if (isPrime[i]) {
                for (int j = i * i; j < n; j += i) {
                    isPrime[j] = false;
                }
            }
        }

        int count = 0;
        for (boolean p : isPrime) {
            if (p) count++;
        }
        return count;
    }

    public static void main(String[] args) {
        CountPrimes s = new CountPrimes();

        System.out.println("Test 1 (n=10): " + s.countPrimes(10) + " (expected: 4)");
        // Primes < 10: 2, 3, 5, 7

        System.out.println("Test 2 (n=0): " + s.countPrimes(0) + " (expected: 0)");
        System.out.println("Test 3 (n=1): " + s.countPrimes(1) + " (expected: 0)");
        System.out.println("Test 4 (n=2): " + s.countPrimes(2) + " (expected: 0)");

        System.out.println("Test 5 (n=100): " + s.countPrimes(100) + " (expected: 25)");
        System.out.println("Test 6 (n=5000000): " + s.countPrimes(5000000) + " (expected: 348513)");
    }
}
```

---

## Solution: Optimized Sieve (Bit Manipulation)

```java
import java.util.*;

/**
 * LeetCode 204 — Count Primes
 * Memory-optimized Sieve using BitSet.
 *
 * Time: O(n log log n) | Space: O(n/8)
 */
public class CountPrimesBitSet {

    public int countPrimes(int n) {
        if (n <= 2) return 0;

        BitSet isPrime = new BitSet(n);
        isPrime.set(2, n);

        for (int i = 2; i * i < n; i++) {
            if (isPrime.get(i)) {
                for (int j = i * i; j < n; j += i) {
                    isPrime.clear(j);
                }
            }
        }

        return isPrime.cardinality();
    }

    public static void main(String[] args) {
        CountPrimesBitSet s = new CountPrimesBitSet();

        System.out.println("Test 1 (n=10): " + s.countPrimes(10) + " (expected: 4)");
        System.out.println("Test 2 (n=0): " + s.countPrimes(0) + " (expected: 0)");
        System.out.println("Test 3 (n=1): " + s.countPrimes(1) + " (expected: 0)");
        System.out.println("Test 4 (n=100): " + s.countPrimes(100) + " (expected: 25)");
        System.out.println("Test 5 (n=5000000): " + s.countPrimes(5000000) + " (expected: 348513)");
    }
}
```

---

## Complexity Analysis

| Approach | Time | Space |
|----------|------|-------|
| Standard Sieve | O(n log log n) | O(n) |
| BitSet Sieve | O(n log log n) | O(n/8) |

### Why the Sieve Works

Start with all numbers ≥ 2 marked as prime. For each prime `i`, mark all multiples of `i` (starting from `i*i`, not `2*i`) as composite. The starting point `i*i` is an optimization: any smaller multiple `k*i` (with `k < i`) has already been marked by a smaller prime factor.

### Why `i*i < n` as Loop Bound?

For any composite number `c`, there exists a prime factor `p <= sqrt(c)`. If we've checked all primes up to `sqrt(n-1)`, any unmarked number larger than that must be prime.

### Memory Optimization

A `boolean[]` uses 1 byte per element. A `BitSet` uses 1 bit per element, reducing memory by 8x — useful at the 5*10^6 constraint where the standard sieve uses ~5 MB and BitSet uses ~0.6 MB.
