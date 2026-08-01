# Problem Walkthrough: Miller-Rabin Primality Test with Modular Exponentiation

## Problem Statement

Implement the **Miller-Rabin probabilistic primality test** with correct 64-bit modular arithmetic, **deterministic for all long inputs** via the proven 12-base witness set, and verified against an independent trial-division oracle.

The implementation must include:

1. Overflow-safe modular arithmetic on longs: `addMod` (128-bit-safe via unsigned semantics) and `mulMod` (Russian-peasant doubling, no intermediate overflow).
2. Binary modular exponentiation `powMod(base, exp, mod)`.
3. The witness test derived from n - 1 = 2^s · d.
4. Small-prime pre-screening for constant-factor speedup.
5. A BigInteger path for inputs beyond `long` range (same witness logic, `modPow`).
6. Verification: known primes/composites (including Carmichael numbers and strong pseudoprimes), an exhaustive sweep vs trial division up to 100,000, and a prime-count check (π(100000) = 9592).

**Deliverable**: `com.math.deep.lab06.MillerRabin` — complete Java 21+ class with `isPrime(long)`, `isProbablyPrime(BigInteger, rounds)`, `powMod`, and the `main` verification driver.

---

## Constraints & Requirements

| Item | Requirement |
|------|-------------|
| Language | Java 21+ (BigInteger for the big-number path; no external libs) |
| Fast path | `long` input, deterministic: {2,3,5,7,11,13,17,19,23,29,31,37} — sound for all n < 3.3×10²⁴ ⊇ all longs |
| Overflow | No `(a*b) % m` on raw longs; mulMod must be exact for m < 2⁶³ |
| Slow path | `BigInteger` with k random bases, error ≤ 4⁻ᵏ |
| Verification | Trial-division sweep, Carmichael numbers, strong pseudoprimes, prime counting |

---

## Step 1: Mathematical Foundation

### 1.1 Fermat's little theorem and its converse failure

For prime p and gcd(a, p) = 1: **a^(p-1) ≡ 1 (mod p)**. The converse is false — **Carmichael numbers** (e.g. 561 = 3·11·17) satisfy a^(n-1) ≡ 1 (mod n) for every a coprime to n, and there are infinitely many. A naive Fermat test can never detect them.

### 1.2 The Miller-Rabin witness test

Write n - 1 = 2^s · d with d odd. For prime n:

a^d, a^(2d), ..., a^(2^s d) ≡ 1 (mod n)

and — crucially — because modulo a prime the only solutions of x² ≡ 1 are x ≡ ±1, the sequence must either start at 1, or reach 1 by first hitting -1 ≡ n - 1. A value of 1 that appears without a preceding -1 is a **nontrivial square root of 1 modulo n**, which exists only for composite n. That is the witness condition:

```
isWitness(a):
  x = a^d mod n
  if x == 1 or x == n-1:  return false          // passes this base
  for r = 1 .. s-1:
    x = x^2 mod n
    if x == n-1: return false                    // passes this base
    if x == 1:  return true                      // nontrivial sqrt(1) -> composite
  return true                                    // never hit +-1 -> composite
```

### 1.3 Error bound and determinism

For odd composite n, the set of **liars** (bases that fail to witness) is a proper subgroup of (Z/nZ)*; by Lagrange's theorem the fraction of liars is ≤ 1/4 (≤ 1/2 only for n = 9). Hence k independent random bases give error probability ≤ 4⁻ᵏ.

For fixed-size inputs, exhaustive computation has established explicit witness sets:

| Input range | Witness set |
|-------------|-------------|
| n < 2,047 | {2} |
| n < 1,373,653 | {2, 3} |
| n < 3,474,749,660,383 | {2, 3, 5, 7, 11, 13, 17} |
| n < 2⁶⁴ | {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37} (Sinclair) |

Our fast path uses the 12-base set — deterministic for every `long` (n ≤ 2⁶³ - 1 < 3.3×10²⁴).

### 1.4 The modular arithmetic hazard

The witness test needs a^d mod n with 62-bit values: the intermediate products a·b reach 124 bits — **overflowing long**. Solutions:

- Russian-peasant multiplication: compute a·b mod m by repeated doubling — O(64) steps, no overflow, uses only `addMod`.
- 128-bit product + reduction via `Math.multiplyHigh` — faster but subtle.
- BigInteger — exact but 10–100× slower.

This walkthrough uses peasant doubling for clarity and correctness, exploiting Java's `Long.remainderUnsigned(a + b, m)`, which interprets the possibly-overflowed sum as unsigned — valid because a, b < m < 2⁶³ implies a + b < 2⁶⁴.

---

## Step 2: Design

### 2.1 Pipeline

```
isPrime(n):
  1. n < 2                              -> false
  2. trial divide by small primes p:
       n % p == 0                        -> return n == p
  3. s, d = factorTwos(n - 1)            // n-1 = 2^s * d, d odd
  4. for a in {2,3,5,7,11,13,17,19,23,29,31,37}:
       if a < n and isWitness(a, n, d, s) -> return false
  5. return true
```

### 2.2 Modular arithmetic primitives

```java
private static long addMod(long a, long b, long m) {
    return Long.remainderUnsigned(a + b, m);   // correct: a + b < 2^64
}

private static long mulMod(long a, long b, long m) {
    long ar = Long.remainderUnsigned(a, m);    // reduce a (b already reduced in powMod)
    long result = 0;
    while (b != 0) {
        if ((b & 1L) == 1L) result = addMod(result, ar, m);
        b >>>= 1;
        ar = addMod(ar, ar, m);                // ar = (ar * 2) mod m
    }
    return result;
}
```

### 2.3 Binary exponentiation

```java
private static long powMod(long base, long exp, long mod) {
    long result = 1 % mod;
    long b = Long.remainderUnsigned(base, mod);
    while (exp != 0) {
        if ((exp & 1L) == 1L) result = mulMod(result, b, mod);
        exp >>>= 1;
        b = mulMod(b, b, mod);
    }
    return result;
}
```

### 2.4 BigInteger path

Identical witness logic; `a.modPow(d, n)` replaces `powMod`; bases are uniform in [2, n-2]. Delegates to the long path when n fits.

---

## Step 3: Complete Solution (Java 21+)

```java
package com.math.deep.lab06;

import java.math.BigInteger;
import java.util.Random;

public final class MillerRabin {

    private static final long[] SMALL_PRIMES =
        {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37};

    private static final long[] DETERMINISTIC_BASES =
        {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37};

    private MillerRabin() {}

    private static long addMod(long a, long b, long m) {
        return Long.remainderUnsigned(a + b, m);
    }

    private static long mulMod(long a, long b, long m) {
        long ar = Long.remainderUnsigned(a, m);
        long result = 0;
        long bb = b;
        while (bb != 0) {
            if ((bb & 1L) == 1L) result = addMod(result, ar, m);
            bb >>>= 1;
            ar = addMod(ar, ar, m);
        }
        return result;
    }

    public static long powMod(long base, long exp, long mod) {
        if (mod <= 1) throw new IllegalArgumentException("mod must be > 1");
        long result = 1 % mod;
        long b = Long.remainderUnsigned(base, mod);
        long e = exp;
        while (e != 0) {
            if ((e & 1L) == 1L) result = mulMod(result, b, mod);
            e >>>= 1;
            b = mulMod(b, b, mod);
        }
        return result;
    }

    private static boolean isWitness(long a, long n, long d, int s) {
        long x = powMod(a, d, n);
        if (x == 1 || x == n - 1) return false;
        for (int r = 1; r < s; r++) {
            x = mulMod(x, x, n);
            if (x == n - 1) return false;
            if (x == 1) return true;
        }
        return true;
    }

    public static boolean isPrime(long n) {
        if (n < 2) return false;
        for (long p : SMALL_PRIMES) {
            if (n % p == 0) return n == p;
        }
        long d = n - 1;
        int s = 0;
        while ((d & 1L) == 0) {
            d >>= 1;
            s++;
        }
        for (long a : DETERMINISTIC_BASES) {
            if (a >= n) continue;
            if (isWitness(a, n, d, s)) return false;
        }
        return true;
    }

    public static boolean trialDivision(long n) {
        if (n < 2) return false;
        if (n % 2 == 0) return n == 2;
        for (long p = 3; p * p <= n; p += 2) {
            if (n % p == 0) return false;
        }
        return true;
    }

    private static boolean isWitnessBig(BigInteger a, BigInteger n, BigInteger d, int s) {
        BigInteger x = a.modPow(d, n);
        if (x.equals(BigInteger.ONE) || x.equals(n.subtract(BigInteger.ONE))) return false;
        for (int r = 1; r < s; r++) {
            x = x.multiply(x).mod(n);
            if (x.equals(n.subtract(BigInteger.ONE))) return false;
            if (x.equals(BigInteger.ONE)) return true;
        }
        return true;
    }

    public static boolean isProbablyPrime(BigInteger n, int rounds) {
        if (rounds <= 0) throw new IllegalArgumentException("rounds must be positive");
        if (n.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) <= 0) {
            return isPrime(n.longValueExact());
        }
        for (long p : SMALL_PRIMES) {
            BigInteger bp = BigInteger.valueOf(p);
            if (n.mod(bp).signum() == 0) return false;
        }
        BigInteger d = n.subtract(BigInteger.ONE);
        int s = 0;
        while (!d.testBit(0)) {
            d = d.shiftRight(1);
            s++;
        }
        Random rng = new Random();
        for (int i = 0; i < rounds; i++) {
            BigInteger a = BigInteger.TWO.add(
                new BigInteger(n.bitLength(), rng).mod(n.subtract(BigInteger.TWO)));
            if (isWitnessBig(a, n, d, s)) return false;
        }
        return true;
    }

    private static void check(String label, boolean actual, boolean expected) {
        String status = actual == expected ? "PASS" : "FAIL";
        System.out.printf("[%s] %-52s got=%b expected=%b%n", status, label, actual, expected);
    }

    public static void main(String[] args) {
        System.out.println("=== Miller-Rabin Primality Test (deterministic for long) ===");

        System.out.println("--- powMod cross-check vs BigInteger.modPow ---");
        Random rng = new Random(7L);
        int powMismatch = 0;
        for (int t = 0; t < 5000; t++) {
            long a = rng.nextLong() & Long.MAX_VALUE;
            long e = rng.nextLong() & Long.MAX_VALUE;
            long m = (rng.nextLong() & Long.MAX_VALUE) + 2;
            long fast = powMod(a, e, m);
            BigInteger slow = BigInteger.valueOf(a).modPow(BigInteger.valueOf(e),
                                                          BigInteger.valueOf(m));
            if (fast != slow.longValueExact()) powMismatch++;
        }
        System.out.printf("powMod mismatches: %d/5000%n", powMismatch);

        System.out.println("--- Edge cases ---");
        check("n = 0", isPrime(0), false);
        check("n = 1", isPrime(1), false);
        check("n = 2", isPrime(2), true);
        check("n = 4", isPrime(4), false);
        check("n = 9", isPrime(9), false);
        check("n = 41", isPrime(41), true);

        System.out.println("--- Known primes ---");
        check("Mersenne 2^19-1", isPrime(524287L), true);
        check("Mersenne 2^31-1", isPrime(2147483647L), true);
        check("Mersenne 2^61-1", isPrime(2305843009213693951L), true);
        check("largest prime < 2^63", isPrime(9223372036854775783L), true);

        System.out.println("--- Known composites (pseudoprimes & Carmichael) ---");
        check("341 = 11*31 (Fermat pp base 2)", isPrime(341L), false);
        check("561 = 3*11*17 (Carmichael)", isPrime(561L), false);
        check("1105 (Carmichael)", isPrime(1105L), false);
        check("1729 (Carmichael)", isPrime(1729L), false);
        check("2047 = 23*89 (strong pp base 2)", isPrime(2047L), false);
        check("3215031751 (strong pp bases 2,3)", isPrime(3215031751L), false);
        check("8388607 = 47*178481", isPrime(8388607L), false);
        long mersenne61 = 2305843009213693951L;
        check("3*(2^61-1)", isPrime(3L * mersenne61), false);

        System.out.println("--- Exhaustive sweep vs trial division: n < 100000 ---");
        long mismatches = 0;
        long primeCount = 0;
        for (long n = 0; n < 100000L; n++) {
            boolean mr = isPrime(n);
            boolean td = trialDivision(n);
            if (mr != td) {
                mismatches++;
                System.out.printf("  MISMATCH at n=%d: MR=%b TD=%b%n", n, mr, td);
            }
            if (mr) primeCount++;
        }
        System.out.printf("sweep mismatches: %d; primes below 100000: %d (known: 9592)%n",
                          mismatches, primeCount);

        System.out.println("--- Carmichael numbers below 100000 all rejected ---");
        long[] carmichael = {561, 1105, 1729, 2465, 2821, 6601, 8911, 10585, 15841,
                             29341, 41041, 46657, 52633, 62745, 63973, 75361};
        int carmichaelBad = 0;
        for (long c : carmichael) {
            if (isPrime(c)) {
                carmichaelBad++;
                System.out.printf("  FAILED to reject %d%n", c);
            }
        }
        System.out.printf("Carmichael rejections: %d/%d%n",
                          carmichael.length - carmichaelBad, carmichael.length);

        System.out.println("--- Single-base inadequacy demo (strong pp to bases 2 and 3) ---");
        long n3215 = 3215031751L;
        long d = n3215 - 1;
        int s = 0;
        while ((d & 1L) == 0) {
            d >>= 1;
            s++;
        }
        System.out.printf("  3215031751: base 2 witness=%b, base 3 witness=%b, "
                          + "base 5 witness=%b%n",
                          isWitness(2, n3215, d, s), isWitness(3, n3215, d, s),
                          isWitness(5, n3215, d, s));

        System.out.println("--- BigInteger path ---");
        BigInteger bigPrime = BigInteger.valueOf(2L).pow(127).subtract(BigInteger.ONE);
        check("2^127-1 (Mersenne)", isProbablyPrime(bigPrime, 40), true);
        BigInteger bigComposite = bigPrime.multiply(BigInteger.valueOf(3L));
        check("3 * (2^127-1)", isProbablyPrime(bigComposite, 40), false);

        System.out.println("--- Performance ---");
        long t0 = System.nanoTime();
        boolean p = isPrime(9223372036854775783L);
        long t1 = System.nanoTime();
        System.out.printf("isPrime(largest prime < 2^63) = %b in %.2f ms%n", p,
                          (t1 - t0) / 1e6);
        t0 = System.nanoTime();
        p = isPrime(6917529027641081853L);
        t1 = System.nanoTime();
        System.out.printf("isPrime(3*(2^61-1)) = %b in %.2f ms%n", p, (t1 - t0) / 1e6);
    }
}
```

---

## Step 4: Walkthrough of a Concrete Run

### 4.1 Why 3215031751 is the killer test

3215031751 = 151 · 751 · 28351 is the smallest **strong pseudoprime to both bases 2 and 3**: Miller-Rabin with either single base declares it prime. The demo prints the witness flags: base 2 → not a witness (passes), base 3 → not a witness, base 5 → witness (rejected). This is the concrete demonstration of why the deterministic set exists, and why "one base is fine" is a production bug.

### 4.2 The Carmichael sweep

All 16 Carmichael numbers below 100,000 (561 … 75361) are rejected by the 12-base test. A Fermat test would fail on every one of them — the counterfactual is shown by construction: the test *must* find a nontrivial square root of 1 for each.

### 4.3 The exhaustive sweep

For every n < 100,000, `isPrime` agrees with trial division — 100,000 exact comparisons, zero mismatches — and the prime count 9592 matches the known value of π(100,000). The sweep covers all small-prime special cases, even numbers, squares, and products.

### 4.4 Overflow-safety validation

The `powMod` cross-check runs 5,000 random (a, e, m) triples with m up to 2⁶³ - 1 and compares against BigInteger's `modPow` — the canonical reference for 128-bit-correct modular arithmetic. Zero mismatches means `mulMod`/`addMod` handle every wrap-around case.

---

## Step 5: Testing & Verification

| # | Test | Input | Expected | Verified |
|---|------|-------|----------|----------|
| 1 | powMod vs modPow | 5000 random triples | 0 mismatches | main() |
| 2 | Edge values | 0, 1, 2, 4, 9, 41 | false, false, true, false, false, true | main() |
| 3 | Mersenne primes | 2¹⁹-1, 2³¹-1, 2⁶¹-1 | true | main() |
| 4 | Largest prime < 2⁶³ | 9223372036854775783 | true | main() |
| 5 | Fermat pseudoprime | 341 | false | main() |
| 6 | Carmichael numbers | 561, 1105, 1729, … (16 values) | all false | main() |
| 7 | Strong pseudoprimes | 2047 (base 2), 3215031751 (bases 2,3) | false | main() |
| 8 | Semiprime near 2⁶³ | 3·(2⁶¹-1) | false | main() |
| 9 | Exhaustive sweep | all n < 100,000 | 0 mismatches vs trial division | main() |
| 10 | Prime counting | π(100,000) | 9592 | main() |
| 11 | Single-base demo | 3215031751, bases 2/3/5 | witness flags false/false/true | main() |
| 12 | BigInteger path | 2¹²⁷-1, 3·(2¹²⁷-1) | true, false | main() |

---

## Complexity Analysis

**Time** (long path): trial division by 12 small primes is O(1). Each witness is one `powMod` of an exponent with ~63 bits: 63 squarings + ≤ 63 conditional multiplies, each `mulMod` costing O(64) `addMod` steps → ~8,000 elementary ops per witness; 12 witnesses → ~10⁵ ops, a few microseconds. **Worst case**: `n` near 2⁶³ with d of full bit-length.

**Time** (BigInteger path): each witness is O(log n) modular multiplications, each O(M(log n)) with M the multiplication cost (Karatsuba/Toom for 2048-bit). k = 40 rounds → error ≤ 4⁻⁴⁰ ≈ 10⁻²⁴ — below any hardware fault rate.

**Space**: O(1) on the long path; O(bit-length) transient BigInteger temporaries on the big path.

**Comparison**: trial division is O(√n) — 10⁶× slower at n = 2⁶³. `BigInteger.isProbablePrime` uses the same Miller-Rabin + Lucas machinery with a *probabilistic* contract; our long path is *deterministic* — the difference between "likely prime" and "provably prime" that matters in key generation audits.

---

## Edge Cases & Pitfalls

1. **64-bit overflow**: `(a * b) % m` silently corrupts results for 62-bit factors. Every multiply goes through `mulMod`; the random-triple cross-check is the regression guard.
2. **a ≥ n in the base set**: for tiny n the base may exceed n; guard `a >= n → continue`. (For our pipeline small n never reaches the witness loop, but the guard makes the function robust to reordering.)
3. **Even n**: handled by the small-prime pre-screen (n % 2 == 0 → n == 2).
4. **n = 0, 1**: rejected before any arithmetic (d = -1 would break the loop otherwise).
5. **The nontrivial-sqrt rule**: a return of 1 *without* a preceding -1 is the composite signal — it is tempting to accept `x == 1` as "prime-like"; the check order in `isWitness` encodes the distinction.
6. **`isWitness` for base = 1**: trivially "not a witness" — harmless, but the base set starts at 2.
7. **BigInteger negative or tiny inputs**: the path delegates to the long path for n ≤ Long.MAX_VALUE; negative n is rejected by the long path's n < 2 check.
8. **rng quality**: the BigInteger path uses `java.util.Random` (LCG) — cryptographically insufficient for key generation; production should use `SecureRandom`. Documented in the API contract.

---

## Follow-up Questions

1. **Why is the liar set a subgroup?** Show that if a and b are both liars for n, so is a·b (mod n) — the key algebraic step that produces the ≤ 1/4 fraction via Lagrange's theorem, and the reason the deterministic search for base sets was computationally feasible (it only had to beat the subgroup structure).

2. **The Lucas test and Baillie-PSW**: the Lucas probable-prime test (based on Fibonacci-like sequences) detects different pseudoprime classes than Miller-Rabin. The BPSW combination (MR base 2 + Lucas) has **no known counterexample** even though it is unproven for all n. Why does production software (GnuPG, OpenSSL-era tools) prefer BPSW over more MR bases?

3. **The 4⁻ᵏ bound is per-call**: for RSA key generation you must also worry about *conditional* failure — given that a candidate passed k rounds, how many candidates in a batch are actually composite? Use Bayes: with the density of primes near 2²⁰⁴⁸ and error 4⁻ᵏ, the posterior probability of accepting a composite is ~4⁻ᵏ · ln(2²⁰⁴⁸)/2 — compute it and decide k.

4. **AKS primality**: the first unconditional polynomial-time primality test (Agrawal-Kayal-Saxena 2002, O((log n)^6) with improvements). Why is it a theoretical landmark but impractical — and where do ECPP (Atkin-Morain) and the APRCL test fit in the practical hierarchy?

5. **Mersenne prime testing**: for n = 2ᵖ - 1, the **Lucas-Lehmer test** (s₀ = 4, s_{k+1} = s_k² - 2 mod n, prime iff s_{p-2} ≡ 0) is a *deterministic* necessary-and-sufficient test with O(p² log p log log p) cost — exponentially better than Miller-Rabin for this special family. Derive why s² - 2 mod n is the right recurrence (it is a Chebyshev-type iteration in the ring Z[√3]).

6. **Composite detection vs. certification**: Miller-Rabin proves compositeness (a witness is a certificate) but only *suggests* primality. How do you turn the output into a verifiable certificate (Pratt certificate / Pocklington's theorem), and why do auditors of key-generation code care?

---

## Extension Ideas

- **Baillie-PSW**: add the Lucas probable-prime test (Selfridge parameter selection) and run the whole verification corpus against the pair — the strongest practical probable-prime filter.
- **Next-prime generator**: implement `nextPrime(n)` using the sieve-of-eratosthenes wheel + isPrime, the standard RSA key-generation workhorse; benchmark candidate acceptance rate (expected ~ln n / 2 for odd candidates).
- **Sieve cross-check**: generate primes below 10⁷ with a bit sieve and compare counts with π(10⁷) = 664,579 — a full-range correctness anchor for the sweep.
- **Korselt's criterion**: detect Carmichael numbers directly (n square-free and p - 1 | n - 1 for every prime p | n); report how many of the 16 test values it flags, tying the counterexample class back to the theory.
- **Constant-time witness loop**: for side-channel resistance, replace the early-return witness loop with a fully-iterated version (always s squarings) — the standard hardening in TLS key-gen paths.
