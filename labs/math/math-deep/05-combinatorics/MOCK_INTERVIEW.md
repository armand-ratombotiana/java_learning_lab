# Mock Interview: Generating Functions for Coin Change

> Coding mock interview — 45-minute session, Java 21+, whiteboard style.

---

## Interview Setup

**Role**: Quantitative Analyst (Risk / Portfolio Analytics Team)
**Candidate Level**: Senior Engineer
**Focus Area**: Combinatorics, generating functions, polynomial arithmetic, big integers
**Problem**: Implement generating-function based coin change counting: compute the number of ways to make change for an amount n using given coin denominations, via polynomial multiplication — and contrast with DP.
**Language**: Java 21+ (records, streams, BigInteger allowed)

---

## Interviewer Cheat Sheet (Prepared Questions)

1. Set up the generating function for coin change. Where does each coin type appear?
2. What is the coefficient interpretation — what does [x^n] of the product count?
3. Why use a generating function when DP does the same job? What's actually different?
4. What's the cost of truncated polynomial multiplication, and how does it compare to DP?
5. What changes for *bounded* coin supply (limited coins of each denomination)?
6. Follow-up: convolution theorem, FFT/NTT, partition numbers, restricted partitions.

---

## Transcript

### Part 1: Problem Clarification (5 minutes)

**Interviewer**: "We need to compute, for a pricing system, the number of ways a basket of items can sum to a target price — each item type has a price and we can use unlimited copies. The classic coin-change counting problem. Implement it with generating functions. Clarify."

**Candidate**: "Three questions. Is the coin supply unbounded — can I use as many copies of each denomination as I want? Second, what's the target size — that decides whether BigInteger coefficients matter: for amount 10,000 with denominations like 1 and 2, the counts are astronomically large. And third, do you want only the count, or the actual combinations?"

**Interviewer**: "Unbounded supply, amount up to ~5,000, and I want the count — huge counts are exactly why I want you to think about it."

**Candidate**: "Then the counts overflow long easily — the count of partitions into {1,2} alone grows like n²/4, and with more denominations the exponents climb further. So the implementation needs BigInteger coefficients, and the elegant framing is: the count is the coefficient of x^n in a product of geometric series."

### Part 2: Theory (10 minutes)

**Interviewer**: "Set up the generating function."

**Candidate**: "A generating function encodes a sequence a₀, a₁, a₂, ... as the power series A(x) = Σ aₖ xᵏ. For coin change, each denomination d contributes one factor: using 0, 1, 2, ... coins of denomination d contributes the geometric series 1 + x^d + x^{2d} + ... = 1/(1 - x^d). Unbounded supply → each coin type is an independent choice, so the total generating function is the product over denominations:

F(x) = ∏_d (1 + x^d + x^{2d} + ...) = ∏_d 1/(1 - x^d)

and the answer for amount n is the coefficient [xⁿ]F(x)."

**Interviewer**: "Why does the product work? Walk me through the coefficient extraction."

**Candidate**: "Expanding the product, a term x^n is assembled by choosing one power from each factor: x^{d·k₁} · x^{d'·k₂} · ... = x^n where Σ d·kᵢ = n. Each choice (k₁, k₂, ...) is exactly one way to use k₁ coins of the first denomination, k₂ of the second, etc. — a distinct combination of coins summing to n. Since each combination gives exactly one term x^n, the coefficient of x^n counts the combinations. The multiplication is a convolution of the coefficient sequences — the combinatorial operation 'combine choices of two types' is arithmetic convolution."

**Interviewer**: "And the bound n ≤ 5,000 — what does truncation buy us?"

**Candidate**: "We never need terms beyond x^n: any contribution using coins summing above n can't affect [x^n]. So every polynomial is truncated to degree n, and each multiplication of two degree-n polynomials costs O(n²) coefficient operations — or O(n log n) with FFT-style convolution, though BigInteger arithmetic changes the constant. The whole product costs O(|D| · n²) with schoolbook multiplication."

### Part 3: Design (8 minutes)

**Interviewer**: "Compare with the DP approach. Where does generating-function multiplication differ?"

**Candidate**: "The classic DP: `dp[a] = sum over d of dp[a - d]` with unlimited coins — O(|D|·n) time, O(n) memory, and it IS the same convolution computed cleverly. The subtlety: the DP update must loop denominations *outer* and amounts *inner* to count combinations (order-insensitive), not permutations. The generating function automatically counts combinations because each factor is per-denomination — the product structure builds order-insensitivity in by construction. The real differences are: (1) correctness clarity — the generating function is a proof, the DP is an algorithm; (2) extensibility — bounded supply is a one-line change in the product (truncate the geometric series to k+1 terms) whereas the DP needs a 2D state; (3) computational profile — polynomial multiplication composes, so you can use divide-and-conquer or NTT for many denominations, and it parallelizes; (4) the generating function gives you *all* amounts up to n at once, like the DP table."

**Interviewer**: "What about a bounded supply — ten coins of denomination 2, say?"

**Candidate**: "The factor becomes a truncated geometric series 1 + x² + x⁴ + ... + x^{2·10} — a finite polynomial with 11 terms. Multiply it into the product; the coefficient of x^n still counts the ways. In the DP world this becomes a harder knapsack-style problem. The generating function treats both cases with the identical operation — that's its elegance."

### Part 4: Implementation (18 minutes)

**Interviewer**: "Code the core."

**Candidate**: "A `Poly` record holding a BigInteger[] of coefficients. Multiplication is the schoolbook convolution, with early termination at degree n; I'll also implement the optimized bounded-unbounded product loop: instead of multiplying factor by factor with the O(n²) convolution repeatedly, I accumulate by iterating coins in the classical DP order — but the walkthrough version will keep the explicit product of polynomials to show the structure."

```java
public record Poly(BigInteger[] coef) {
    public static Poly one() { return new Poly(new BigInteger[]{BigInteger.ONE}); }

    public Poly multiply(Poly other, int n) {
        BigInteger[] a = this.coef, b = other.coef;
        BigInteger[] out = new BigInteger[n + 1];
        Arrays.fill(out, BigInteger.ZERO);
        for (int i = 0; i < a.length && i <= n; i++) {
            if (a[i].signum() == 0) continue;
            int jMax = Math.min(b.length - 1, n - i);
            for (int j = 0; j <= jMax; j++) {
                if (b[j].signum() == 0) continue;
                out[i + j] = out[i + j].add(a[i].multiply(b[j]));
            }
        }
        return new Poly(out);
    }
}
```

**Interviewer**: "Now build the coin factor and the full product."

**Candidate**:

```java
public static Poly unboundedFactor(int d, int n) {
    BigInteger[] c = new BigInteger[n + 1];
    Arrays.fill(c, BigInteger.ZERO);
    for (int k = 0; k * d <= n; k++) c[k * d] = BigInteger.ONE;
    return new Poly(c);
}

public static BigInteger countWays(int[] denominations, int n) {
    Poly product = Poly.one();
    for (int d : denominations) {
        if (d > n) continue;
        product = product.multiply(unboundedFactor(d, n), n);
    }
    return product.coef[n];
}
```

**Interviewer**: "A sanity check on the classic case?"

**Candidate**: "Denominations {1, 2} and amount 5: the product (1 + x + x² + x³ + x⁴ + x⁵)(1 + x² + x⁴). Coeff of x⁵: combinations are 5×1; 3×1+1×2; 1×1+2×2 — three ways. From the product: x⁵·1, x³·x², x¹·x⁴ — three contributions. Matches. And I'd add a cross-check against the DP implementation in the test driver — two independent implementations agreeing is the strongest correctness signal for this kind of code."

### Part 5: Testing (5 minutes)

**Interviewer**: "Test plan?"

**Candidate**: "(1) Base cases: amount 0 with any denominations → 1 way (the empty combination — the constant coefficient). No denominations or all d > n → 0 for n > 0. (2) Known values: {1,2} sequence is ⌊n/2⌋+1; {1,2,3} matches the partition-into-parts-≤3 closed form; {1,5,10,25} is the classic US coin problem with the well-known table starting 1, 1, 1, 1, 1, 2, ... (3) The partition function p(n): denominations {1..n} counts the number of partitions of n — compare against the known values p(10)=42, p(20)=627, p(50)=204226. (4) Cross-check with DP for random denomination sets and n up to 200 — agree exactly. (5) Performance: n = 5,000 with {1..10} — verify BigInteger arithmetic completes in reasonable time."

### Part 6: Follow-ups (5 minutes)

**Interviewer**: "The counts grow fast. Give me the asymptotic."

**Candidate**: "For a fixed coin set, the count grows polynomially: with denominations {1, 2}, the count is ~n²/4. In general, with |D| coin types, the count is Θ(n^{|D|-1} / (|D|-1)! · ∏ d) — the leading asymptotic comes from the pole at x = 1 of the generating function: near x = 1, each factor 1/(1 - x^d) ≈ 1/(d(1 - x)), so F(x) ≈ ∏(1/d) · (1-x)^{-|D|}, and [xⁿ] of (1-x)^{-k} is C(n + k - 1, k - 1) ~ n^{k-1}/(k-1)! — the dominant term. That's a beautiful application of singularity analysis: the *coefficient asymptotics* come from the *singularity structure* of the generating function."

**Interviewer**: "And the partition function itself — denominations 1 through infinity?"

**Candidate**: "p(n) has the famous Hardy-Ramanujan asymptotics p(n) ~ exp(π√(2n/3))/(4n√3) — irrational-exponent growth, from the exponential singularity of the infinite product near x = 1. That's the same generating function, ∏_{k≥1} 1/(1 - x^k), analyzed with the saddle-point method. It shows how far the technique scales: from a schoolbook product to analytic number theory."

---

## Scoring Rubric

| Area | Excellent (3) | Good (2) | Needs Work (1) |
|------|---------------|----------|----------------|
| Setup | Writes ∏ 1/(1-x^d) and explains coefficient extraction | States product formula | No generating function |
| Implementation | Truncated convolution, BigInteger, sparse handling | Correct but long-only | Wrong convolution |
| Comparison | Explains DP equivalence, bounded-supply extension, composition | Mentions DP | Ignores DP |
| Asymptotics | Singularity analysis, p(n) Hardy-Ramanujan | Polynomial growth bound | No growth discussion |

## Red Flags
- Using `long` for coefficients without overflow analysis.
- Counting permutations instead of combinations (the inner/outer loop confusion in DP).
- Forgetting the n=0 case (1 way).
- Multiplying untruncated polynomials (degree explosion → O(n²) memory).

## Key Takeaways
- [xⁿ] ∏ 1/(1 - x^d) counts the combinations; convolution = choice composition.
- Truncate at degree n: O(|D|·n²) schoolbook, extendable to NTT.
- Bounded supply = truncated geometric-series factors — one-line generalization.
- Coefficient asymptotics from singularities: F(x) ≈ (∏1/d)(1-x)^{-|D|} near x=1.
