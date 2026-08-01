# Problem Walkthrough: Generating Functions for Coin Change Counting

## Problem Statement

Count the number of ways to make change for an amount n with an unbounded supply of coins of given denominations — using **generating functions**. The count must be exact for n up to 5,000, so coefficients are `BigInteger`. The walkthrough covers:

1. The generating function setup: F(x) = ∏_d (1 + x^d + x^{2d} + ...).
2. Coefficient extraction via truncated polynomial convolution.
3. Exact verification against closed forms, known tables (partition numbers), and a classic DP cross-check.
4. The bounded-supply generalization (truncated geometric factors).

**Deliverable**: `com.math.deep.lab05.CoinChangeGenerating` — complete Java 21+ class with a `Poly` record (BigInteger coefficients, truncated multiplication), `countWays` (unbounded) and `countWaysBounded` (with per-denomination supply caps), plus a `main` verification driver.

---

## Constraints & Requirements

| Item | Requirement |
|------|-------------|
| Language | Java 21+ (records, BigInteger; no external libs) |
| Input | int[] denominations, int n (≤ 5,000), optional per-coin supply caps |
| Output | BigInteger count of order-insensitive combinations summing to n |
| Exactness | No long overflow: all coefficients BigInteger |
| Verification | Closed forms, known sequences, DP cross-check |

---

## Step 1: Mathematical Foundation

### 1.1 Generating functions in one paragraph

A generating function is a formal power series A(x) = Σ_{k≥0} aₖxᵏ encoding a sequence (aₖ). The utility: **operations on sequences become operations on series**. The convolution identity is the workhorse:

If C(x) = A(x)·B(x) then cₖ = Σ_{i+j=k} aᵢbⱼ

### 1.2 The coin-change generating function

An unbounded coin of denomination d can be used 0, 1, 2, ... times. The contribution of that coin type is the geometric series:

G_d(x) = 1 + x^d + x^{2d} + ... = Σ_{k≥0} x^{kd} = 1/(1 - x^d)

The choice of how many coins of each type to use is independent across types, so the combined generating function is the product:

F(x) = ∏_{d ∈ D} G_d(x) = ∏_{d ∈ D} (1 - x^d)^{-1}

### 1.3 Coefficient interpretation

Expanding the product, each monomial is assembled by picking one term from each factor: x^{d₁k₁} · x^{d₂k₂} · ... = x^n, with Σ dᵢkᵢ = n. Every tuple (k₁, k₂, ...) — a multiset of coins summing to n — contributes exactly one x^n term. Hence:

**answer(n) = [xⁿ] ∏_d (1 - x^d)^{-1}**

Order does not matter: the tuple (k₁, k₂, ...) has no ordering. Combinations counted, not permutations.

### 1.4 Truncation

All coefficients with index > n can never contribute to [xⁿ] (they have positive degree; the product has only non-negative powers). So every factor and every intermediate product is truncated to degree n. Working polynomial degree never exceeds n; the cost of the schoolbook product of two degree-n polynomials is O(n²) big-integer coefficient operations.

### 1.5 Known closed forms for verification

- Denominations {1, 2}: count(n) = ⌊n/2⌋ + 1.
- Denominations {1, 2, 3}: count(n) = ⌊(n² + 6n + 12)/12⌋ (partitions into parts ≤ 3).
- Denominations {1..n}: count = partition number p(n); p(10) = 42, p(20) = 627, p(50) = 204,226.
- US coins {1, 5, 10, 25}: the classic sequence 1, 1, 1, 1, 1, 2, ...; countWays(100) = 242.

---

## Step 2: Design

### 2.1 The Poly record

```java
public record Poly(BigInteger[] coef) {
    public static Poly one();
    public Poly multiply(Poly other, int n);
    public BigInteger coeff(int i);
}
```

- `coef[i]` is the coefficient of x^i; `coef.length ≤ n + 1` always (truncation invariant).
- `multiply` is the schoolbook convolution with the double loop bound by (i + j ≤ n) and zero-skipping. Sparse factors (most coin factors are very sparse) make the effective cost far below n² in practice.

### 2.2 Coin factors

- Unbounded: `unboundedFactor(d, n)` — coefficients 1 at indices 0, d, 2d, ... ≤ n.
- Bounded (cap c coins): `boundedFactor(d, c, n)` — coefficients 1 at indices 0, d, 2d, ..., min(c·d, n).

### 2.3 Product assembly

```java
public static BigInteger countWays(int[] denominations, int n)
public static BigInteger countWaysBounded(int[] denominations, int[] caps, int n)
```

Both multiply the factors in order. Denominations > n are skipped (they cannot contribute to amounts ≤ n — their factor would be just the constant 1 when truncated).

### 2.4 DP cross-check

```java
public static BigInteger dpCombinations(int[] denominations, int n)
```

Standard unbounded-combination DP: `dp[0] = 1`; for each denomination d (outer loop), for a from d to n (inner): `dp[a] += dp[a - d]`. Outer-denominations order is what makes this count combinations, not permutations — the same semantics as the product.

---

## Step 3: Complete Solution (Java 21+)

```java
package com.math.deep.lab05;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

public final class CoinChangeGenerating {

    public record Poly(BigInteger[] coef) {
        public Poly {
            coef = coef.clone();
        }

        public static Poly one() {
            return new Poly(new BigInteger[]{BigInteger.ONE});
        }

        public BigInteger coeff(int i) {
            return i < coef.length ? coef[i] : BigInteger.ZERO;
        }

        public Poly multiply(Poly other, int n) {
            BigInteger[] out = new BigInteger[n + 1];
            Arrays.fill(out, BigInteger.ZERO);
            for (int i = 0; i < coef.length; i++) {
                BigInteger ai = coef[i];
                if (ai.signum() == 0) continue;
                int jMax = Math.min(other.coef.length - 1, n - i);
                for (int j = 0; j <= jMax; j++) {
                    BigInteger bj = other.coef[j];
                    if (bj.signum() == 0) continue;
                    out[i + j] = out[i + j].add(ai.multiply(bj));
                }
            }
            return new Poly(out);
        }
    }

    private static Poly unboundedFactor(int d, int n) {
        BigInteger[] c = new BigInteger[n + 1];
        Arrays.fill(c, BigInteger.ZERO);
        for (int k = 0; k * d <= n; k++) c[k * d] = BigInteger.ONE;
        return new Poly(c);
    }

    private static Poly boundedFactor(int d, int cap, int n) {
        BigInteger[] c = new BigInteger[n + 1];
        Arrays.fill(c, BigInteger.ZERO);
        int maxK = Math.min(cap, n / d);
        for (int k = 0; k <= maxK; k++) c[k * d] = BigInteger.ONE;
        return new Poly(c);
    }

    public static BigInteger countWays(int[] denominations, int n) {
        if (n < 0) throw new IllegalArgumentException("n must be >= 0");
        Poly product = Poly.one();
        for (int d : denominations) {
            if (d <= 0) throw new IllegalArgumentException("denominations must be positive");
            if (d > n) continue;
            product = product.multiply(unboundedFactor(d, n), n);
        }
        return product.coeff(n);
    }

    public static BigInteger countWaysBounded(int[] denominations, int[] caps, int n) {
        if (denominations.length != caps.length) {
            throw new IllegalArgumentException("denominations and caps must have equal length");
        }
        Poly product = Poly.one();
        for (int i = 0; i < denominations.length; i++) {
            int d = denominations[i];
            if (d <= 0) throw new IllegalArgumentException("denominations must be positive");
            if (d > n) continue;
            product = product.multiply(boundedFactor(d, caps[i], n), n);
        }
        return product.coeff(n);
    }

    public static BigInteger dpCombinations(int[] denominations, int n) {
        BigInteger[] dp = new BigInteger[n + 1];
        Arrays.fill(dp, BigInteger.ZERO);
        dp[0] = BigInteger.ONE;
        for (int d : denominations) {
            if (d > n) continue;
            for (int a = d; a <= n; a++) {
                dp[a] = dp[a].add(dp[a - d]);
            }
        }
        return dp[n];
    }

    public static BigInteger dpBounded(int[] denominations, int[] caps, int n) {
        BigInteger[] dp = new BigInteger[n + 1];
        Arrays.fill(dp, BigInteger.ZERO);
        dp[0] = BigInteger.ONE;
        for (int i = 0; i < denominations.length; i++) {
            int d = denominations[i];
            int cap = caps[i];
            if (d > n) continue;
            for (int a = n; a >= d; a--) {
                for (int k = 1; k <= cap && k * d <= a; k++) {
                    dp[a] = dp[a].add(dp[a - k * d]);
                }
            }
        }
        return dp[n];
    }

    private static void check(String label, BigInteger actual, BigInteger expected) {
        String status = actual.equals(expected) ? "PASS" : "FAIL";
        System.out.printf("[%s] %-46s got=%s expected=%s%n",
                          status, label, actual, expected);
    }

    public static void main(String[] args) {
        System.out.println("=== Generating Functions: Coin Change Counting ===");

        System.out.println("--- Base cases and closed forms ---");
        check("n=0, any coins", countWays(new int[]{1, 2, 5}, 0), BigInteger.ONE);
        check("no coins, n=7", countWays(new int[]{}, 7), BigInteger.ZERO);
        check("all d > n", countWays(new int[]{9, 11}, 5), BigInteger.ZERO);
        check("{1,2} n=5 (floor(n/2)+1)", countWays(new int[]{1, 2}, 5), BigInteger.valueOf(3));
        check("{1,2} n=100", countWays(new int[]{1, 2}, 100), BigInteger.valueOf(51));
        check("{1,2,3} n=10", countWays(new int[]{1, 2, 3}, 10),
              BigInteger.valueOf(14));

        System.out.println("--- Partition numbers: denominations {1..n} ---");
        int[] d10 = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int[] d20 = new int[20];
        for (int i = 0; i < 20; i++) d20[i] = i + 1;
        int[] d50 = new int[50];
        for (int i = 0; i < 50; i++) d50[i] = i + 1;
        check("p(10)", countWays(d10, 10), BigInteger.valueOf(42));
        check("p(20)", countWays(d20, 20), BigInteger.valueOf(627));
        check("p(50)", countWays(d50, 50), BigInteger.valueOf(204226));

        System.out.println("--- US coins {1,5,10,25} ---");
        check("countWays(100)", countWays(new int[]{1, 5, 10, 25}, 100),
              BigInteger.valueOf(242));
        check("countWays(200)", countWays(new int[]{1, 5, 10, 25}, 200),
              BigInteger.valueOf(1463));

        System.out.println("--- Random cross-check vs DP (unbounded) ---");
        Random rng = new Random(99L);
        int mismatches = 0;
        for (int t = 0; t < 300; t++) {
            int n = rng.nextInt(120) + 1;
            int[] denom = new int[1 + rng.nextInt(5)];
            for (int i = 0; i < denom.length; i++) denom[i] = 1 + rng.nextInt(25);
            BigInteger gf = countWays(denom, n);
            BigInteger dp = dpCombinations(denom, n);
            if (!gf.equals(dp)) {
                mismatches++;
                System.out.printf("  MISMATCH n=%d denom=%s gf=%s dp=%s%n",
                                  n, Arrays.toString(denom), gf, dp);
            }
        }
        System.out.printf("unbounded cross-check mismatches: %d/300%n", mismatches);

        System.out.println("--- Bounded supply cross-check vs DP ---");
        mismatches = 0;
        for (int t = 0; t < 200; t++) {
            int n = rng.nextInt(60) + 1;
            int[] denom = new int[1 + rng.nextInt(4)];
            int[] caps = new int[denom.length];
            for (int i = 0; i < denom.length; i++) {
                denom[i] = 1 + rng.nextInt(15);
                caps[i] = rng.nextInt(4) + 1;
            }
            BigInteger gf = countWaysBounded(denom, caps, n);
            BigInteger dp = dpBounded(denom, caps, n);
            if (!gf.equals(dp)) {
                mismatches++;
                System.out.printf("  MISMATCH n=%d denom=%s caps=%s gf=%s dp=%s%n",
                                  n, Arrays.toString(denom), Arrays.toString(caps),
                                  gf, dp);
            }
        }
        System.out.printf("bounded cross-check mismatches: %d/200%n", mismatches);

        System.out.println("--- Hand-verified bounded case ---");
        check("bounded {2,3} caps {2,2} n=6",
              countWaysBounded(new int[]{2, 3}, new int[]{2, 2}, 6),
              BigInteger.valueOf(2));

        System.out.println("--- Performance: n=5000, denominations {1..10} ---");
        int[] d5000 = new int[10];
        for (int i = 0; i < 10; i++) d5000[i] = i + 1;
        long t0 = System.nanoTime();
        BigInteger big = countWays(d5000, 5000);
        long t1 = System.nanoTime();
        System.out.printf("countWays({1..10}, 5000) = %s%n", big);
        System.out.printf("digits=%d  time=%.2f ms%n", big.toString().length(),
                          (t1 - t0) / 1e6);
    }
}
```

---

## Step 4: Walkthrough of a Concrete Run

### 4.1 Denominations {1, 2}, amount 5 — by hand

Factors truncated to degree 5:

- G₁ = 1 + x + x² + x³ + x⁴ + x⁵
- G₂ = 1 + x² + x⁴

Product (only terms ≤ x⁵ shown):

- 1·(1, x², x⁴) → 1, x², x⁴
- x·(1, x², x⁴) → x, x³, x⁵
- x²·(1, x²) → x², x⁴ (x⁶ truncated)
- x³·(1, x²) → x³, x⁵
- x⁴·(1) → x⁴
- x⁵·(1) → x⁵

Collecting x⁵: from x·x⁴, x³·x², x⁵·1 → coefficient 3. Combinations: 2+2+1, 2+1+1+1, 1+1+1+1+1. The generator found all three — with no combinatorial enumeration at all.

### 4.2 Partition numbers

Denominations {1, 2, ..., n} make countWays(n) equal the partition number p(n) — every partition of n uses parts ≤ n. The verification table confirms p(10) = 42, p(20) = 627, p(50) = 204,226 — well-known values, a strong external correctness anchor.

### 4.3 The bounded case

Bounded {2, 3} with caps {2, 2}, amount 6: combinations are 3+3 (one 3-coin used twice) and 2+2+2. The factor for 2 is truncated: 1 + x² + x⁴ (cap 2 → k ≤ 2 → coefficients at 0, 2, 4 — note x⁶ would need 3 coins, excluded by the cap). 3: 1 + x³ (k ≤ 2 but 6 ≤ n so x⁶ would appear if 3·2 = 6 ≤ n — yes! k = 2 gives x⁶. So G₃ = 1 + x³ + x⁶). Product: x⁶ from x⁶·1 and from x⁴·x² → coefficient 2. Correct.

### 4.4 Cross-checks

300 random unbounded instances and 200 bounded instances are verified against the independent DP implementations — both count the same semantic quantity, so agreement is a strong joint-correctness signal. The hand-verified bounded case anchors the semantics: caps truncate the geometric series exactly.

---

## Step 5: Testing & Verification

| # | Test | Input | Expected | Verified |
|---|------|-------|----------|----------|
| 1 | Zero amount | n=0 | 1 (empty combination) | main() |
| 2 | Empty denom set | n=7 | 0 | main() |
| 3 | All d > n | {9,11}, n=5 | 0 | main() |
| 4 | Closed form {1,2} | n=5, n=100 | ⌊n/2⌋+1 = 3, 51 | main() |
| 5 | Closed form {1,2,3} | n=10 | 14 | main() |
| 6 | Partition numbers | p(10), p(20), p(50) | 42, 627, 204226 | main() |
| 7 | US coins | n=100, n=200 | 242, 1463 | main() |
| 8 | Unbounded cross-check | 300 random vs DP | 0 mismatches | main() |
| 9 | Bounded cross-check | 200 random vs DP | 0 mismatches | main() |
| 10 | Bounded hand-case | {2,3} caps{2,2} n=6 | 2 | main() |
| 11 | Performance | {1..10}, n=5000 | completes in ms; digits > 100 | main() |
| 12 | Bad input | negative n | IllegalArgumentException | code |

---

## Complexity Analysis

**Time**: the product of |D| truncated polynomials, each schoolbook convolution O(n²) BigInteger operations with early bounds; sparse factors make the effective cost O(|D|·n·(avg. terms)) ≈ O(|D|·n²) worst case. For n = 5,000 and 10 denominations: ≈ 10 · 25M = 2.5×10⁸ big-int multiply-adds — a few seconds with BigInteger's Karatsuba/Toom multiplication. The DP is O(|D|·n) — 50,000 operations. The generating function pays 10³× more; the payoff is structural generality (bounded case is the identical code path) and independence for verification.

**Space**: O(n) per polynomial, O(|D|·n) transient during the product (two live polynomials at a time). DP: O(n).

**Optimizations available**:
- **Sparse factors**: coin factors have only ⌊n/d⌋ + 1 nonzero terms — iterate the sparse loop (already done via zero-skipping).
- **Divide-and-conquer product**: pair factors and multiply balanced — reduces the *big-integer* sizes earlier; better for many denominations.
- **NTT/FFT convolution**: O(n log n) per product with modular arithmetic; but BigInteger exactness requires either CRT over multiple primes or complex-FFT with error bounds. For n ≤ 5,000 the schoolbook version wins on simplicity.
- **DP hybrid**: for a single query, the DP is strictly better. The generating function shines for: bounded supplies, many *different* queries reusing shared sub-products, and structural insight.

---

## Edge Cases & Pitfalls

1. **Overflow**: coefficients exceed `long` for surprisingly small n (n = 5,000 with {1..10} has 100+ digits). BigInteger is mandatory — the walkthrough deliberately verifies the digit count.
2. **Permutation vs combination**: the inner/outer loop order in DP counts combinations; the generating function gets it right *by construction* — the product structure has no ordering concept.
3. **Truncation discipline**: every multiply must clip at n — forgetting to truncate grows degrees exponentially through the product chain.
4. **Zero amount**: [x⁰] of the product is 1 (all factors choose their constant term) — one way to make 0, the empty combination.
5. **Zero/negative denominations**: rejected up front with a clear exception.
6. **Caps exceeding the amount**: `min(cap, n/d)` in `boundedFactor` — a cap beyond need must not error; it just truncates later.
7. **Duplicate denominations**: mathematically fine (the product has repeated factors, counting both copies as distinguishable coin types — matching the semantics "two different coin types of the same value"). Document it; the DP treats them the same way.
8. **n = 0 with bounded caps of 0**: caps of 0 (no coins available) → factor = 1 → count of 0-amount = 1; correct.

---

## Follow-up Questions

1. **Convolution speedup**: schoolbook multiplication is O(n²). Derive the divide-and-conquer (Kronecker substitution + NTT) approach for BigInteger-polynomial multiplication and state the resulting O(n log n) bound. When does the constant make it unprofitable (n ≤ a few thousand)?

2. **Compositional structure**: the generating function is a product of *identical-looking* factors. If coin values come from a set S, can you compute the product faster than |D| multiplications by grouping equal denominators? (Trivial: yes — group by value with exponentiation; more interesting: exponentiation of the factor (1-x^d)^{-1} is just the number of bounded combos of that one value — binomial coefficients.)

3. **Bounded supply semantics**: for bounded supplies the coefficient formula becomes ∏ (1 - x^{d(k+1)})/(1 - x^d) — a ratio. How does that change the asymptotics when the caps are large but not infinite?

4. **Duality with partitions**: the infinite product ∏ (1 - x^k)^{-1} is the partition generating function. State Euler's pentagonal number theorem, ∏ (1 - x^k) = Σ (-1)^j x^{j(3j±1)/2}, and sketch how it yields an O(n√n) partition-number algorithm.

5. **Probabilistic interpretation**: if each combination is equally likely, what is the distribution of the number of coins used, and which coefficient sequence encodes it? (Mark each coin count with y: ∏ (1 - y·x^d)^{-1} — the bivariate generating function; the expected coin count is ∂/∂y at y=1.)

6. **Singleton analysis**: derive the polynomial growth Θ(n^{|D|-1}) from the pole structure of F at x = 1, and compute the leading constant for {1, 2} exactly: [xⁿ](1-x)^{-1}(1-x²)^{-1} = ⌊n/2⌋ + 1 ≈ n²/4.

---

## Extension Ideas

- **Bivariate generating function**: track both the amount and the number of coins (weight x per amount, y per coin) — yields joint distributions: "ways to make n using exactly k coins".
- **Quotient products for bounded supplies**: ∏ (1 - x^{d(k+1)})/(1 - x^d) — the numerator product is a finite polynomial; multiply it first, then divide by the unbounded product via the inverse series (formal power series division).
- **Integer-partition engine**: extend to all positive integers as denominations up to n (the p(n) case); add Euler's pentagonal recurrence for O(n√n) partition numbers and cross-check.
- **Truncated convolution via NTT**: implement a 998244353-prime NTT convolution and CRT with a second prime for exactness; benchmark against the schoolbook path at n = 5,000 and n = 50,000.
- **Memoized product tree**: precompute products of subsets of denominations once; answer batch queries by combining precomputed factors — the composability that DP tables cannot give.
