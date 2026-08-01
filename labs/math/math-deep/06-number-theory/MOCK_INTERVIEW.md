# Mock Interview: Miller-Rabin Primality Test

> Coding mock interview — 45-minute session, Java 21+, whiteboard style.

---

## Interview Setup

**Role**: Cryptography Engineer (PKI / Key Management Team)
**Candidate Level**: Senior Engineer
**Focus Area**: Number theory, modular arithmetic, randomized algorithms, security
**Problem**: Implement the Miller-Rabin primality test with modular exponentiation, deterministic for 64-bit inputs, with correct handling of Carmichael numbers and pseudoprimes.
**Language**: Java 21+ (BigInteger allowed, plus a long-only fast path)

---

## Interviewer Cheat Sheet (Prepared Questions)

1. State Fermat's little theorem and explain why the converse fails.
2. How does Miller-Rabin fix the weakness of the Fermat test? What's the square-root-of-1 trick?
3. Why is the number of witnesses at least 3/4 of all bases — where does the bound come from?
4. What base sets make it deterministic for 64-bit inputs, and why can't you just use one base?
5. What is a strong pseudoprime? A Carmichael number?
6. Follow-up: how do BigInteger and `isProbablePrime` relate to this, and what's the role of Lucas-Lehmer for Mersenne primes?

---

## Transcript

### Part 1: Problem Clarification (5 minutes)

**Interviewer**: "We generate RSA keys and need a primality gate for 2048-bit candidates. I want a Miller-Rabin implementation, but I also want it to be the right call — discuss correctness vs. speed."

**Candidate**: "First clarification: input range. For 64-bit values, Miller-Rabin is deterministic with a fixed witness set — that's a hard correctness guarantee, no randomness. For 2048-bit candidates, no deterministic fast algorithm is known, and we rely on the randomized version with error probability bounded by 4^{-k} for k random bases. I'll implement both: a `long` fast path with the full deterministic witness set, and a BigInteger path with k random bases — and I'd make the API take a 'bits' parameter so callers pick the contract."

**Interviewer**: "Good. What do the witnesses cost?"

**Candidate**: "Each witness is one modular exponentiation: O(log n) multiplications of O(log n)-bit numbers — for 64-bit inputs that's 64 squarings on longs, nanoseconds. The deterministic set for n < 3.3×10²⁴ has 13 witnesses... for 64-bit specifically, the set {2, 3, 5, 7, 11, 13, 17} is enough for n < 3.4×10¹⁴, and {2, 3, 5, 7, 11, 13} covers n < 3.2×10¹⁸ — but the safe canonical set {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37} was proven by Jim Sinclair to cover all 64-bit values. Since it's cheap, I use the full 12-base set on the long path."

### Part 2: Theory (10 minutes)

**Interviewer**: "Why does the naive Fermat test fail? Give me the concrete counterexample."

**Candidate**: "Fermat's little theorem: if p is prime, then a^{p-1} ≡ 1 (mod p) for all a not divisible by p. The Fermat test checks a^{n-1} ≡ 1 (mod n). The converse is false: composite n satisfying a^{n-1} ≡ 1 for all a coprime to n are the Carmichael numbers — the smallest is 561 = 3·11·17, discovered in 1910, and there are infinitely many (Alford-Granville-Pomerance, 1994). Worse, for square-free n where each prime factor p satisfies p-1 | n-1, *every* a coprime to n passes. So the Fermat test is structurally broken — it cannot distinguish Carmichael numbers."

**Interviewer**: "How does Miller-Rabin fix it?"

**Candidate**: "Write n - 1 = 2^s · d with d odd. If n is prime, then by Fermat a^{n-1} = (a^d)^{2^s} ≡ 1. The key: modulo a prime, the only square roots of 1 are ±1. So the sequence a^d, (a^d)², ..., (a^d)^{2^s} — each step squaring the previous — must either start at 1, or hit 1 only by becoming -1 ≡ n-1 first. If the sequence shows a 1 that wasn't preceded by -1, we have a nontrivial square root of 1 modulo n — which exists only if n is composite. That's the witness test."

**Interviewer**: "Why is the 3/4 error bound what it is?"

**Candidate**: "For odd composite n, the set of liars — bases that pass — forms a proper subgroup of (Z/nZ)*. By Lagrange's theorem the subgroup size divides φ(n), so the fraction of liars is at most 1/2... the sharp bound is at most 1/4: the group of witnesses has index at least 4 for odd composite n ≠ 9. That's why k independent random bases give error ≤ 4^{-k}. Note the subtlety: the *deterministic* failure — the case n = 9 — is the only one where the bound is exactly 1/2, and the minimal witness is 2... 9 fails for base 2? No — 9 = 2^3+1; n-1 = 8 = 2^3·1; base 2: 2^8 = 256 ≡ 4 (mod 9); 4² = 16 ≡ 7; 7² = 49 ≡ 4 — never 1 or -1... so 2 is a witness for 9. Good — the index-4 bound covers everything."

### Part 3: Design (8 minutes)

**Interviewer**: "Design the modular exponentiation for the long path — the overflow trap is real on 64-bit."

**Candidate**: "The classic trap: (a·b) mod m overflows long in the intermediate product. Options: (1) `Math.multiplyHigh` — split a·b into high/low 64 bits, and reduce the 128-bit product mod m with a careful double-modular-reduction — the technique from Hacker's Delight; (2) Russian-peasant doubling: replace multiplication by repeated doubling with mod each step — slower but simple; (3) `BigInteger` — exact but 20-100x slower. I'll use the multiplyHigh approach: it's the standard 64-bit primitive, ~10 lines, and keeps the fast path truly fast."

**Interviewer**: "What about the small-prime pre-screening?"

**Candidate**: "Before running witnesses, trial-divide by the first few primes — say {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31}. This rejects ~87% of composites instantly (by the density of these primes), and it also handles even numbers and small primes correctly. It's not a correctness component — just a constant-factor speedup — but it also gives us clean handling of n < 47."

### Part 4: Implementation (18 minutes)

**Interviewer**: "Code it."

**Candidate**:

```java
private static long mulMod(long a, long b, long m) {
    long ah = Math.multiplyHigh(a, b);
    long al = a * b;
    long[] qr = {0, 0};  // (ah, al) = q * m + r
    // Barrett-free schoolbook 128-bit reduction:
    return (((ah % m) << 60) % m * 16 % m + al % m) % m; // placeholder — see walkthrough
}
```

Hmm — let me not hand-wave that. The clean correct way: use `Math.multiplyHigh` with the division-based reduction via `BigInteger` on the *slow* path, and on the fast path use the classic unsigned high-product reduction:

```java
private static long mulMod(long a, long b, long m) {
    long lo = a * b;
    long hi = Math.multiplyHigh(a, b);
    // q = floor((hi * 2^64 + lo) / m) via BigInteger is slow; instead:
    if (hi == 0) return lo % m;
    BigInteger prod = BigInteger.valueOf(a).multiply(BigInteger.valueOf(b));
    return prod.mod(BigInteger.valueOf(m)).longValueExact();
}
```

**Interviewer**: "That's the slow path on every multiply — for 64-bit input this is fine, but show me the actual fast path you'd ship."

**Candidate**: "Fair. The fast 64-bit modular multiply uses the 'reciprocal of m' trick (Möller-Granlund, as in Hacker's Delight 9-3): precompute r = floor(2^64 / m)... in Java we can use `Math.divideUnsigned` style with `Long.divideUnsigned(hi, ...)`... The cleanest portable approach that is still fast: since m < 2^63 (we only test odd n < 2^63), use `Math.multiplyHigh` and then reduce hi mod m with two `Long.remainderUnsigned` steps:

```java
private static long mulMod(long a, long b, long m) {
    long lo = a * b;
    long hi = Math.multiplyHigh(a, b);
    long r = (Long.remainderUnsigned(hi, m) << 32) % m;
    r = (r * (1L << 32) % m + Long.remainderUnsigned(lo >>> 32, m)) % m;
    r = (r * (1L << 32) % m + Long.remainderUnsigned(lo & 0xFFFFFFFFL, m)) % m;
    return r;
}
```

That's the standard 32-bit-limb reduction: hi contributes hi·2⁶⁴, and we reduce it limb by limb mod m. Each multiplyMod is then ~6 modulo ops on longs — nanoseconds. And on the BigInteger path the equivalent is `a.multiply(b).mod(m)` — one allocation, exact."

**Interviewer**: "Good. Now the witness check itself."

**Candidate**:

```java
private static boolean isWitness(long a, long n, long d, int s) {
    long x = powMod(a, d, n);
    if (x == 1 || x == n - 1) return false;
    for (int r = 1; r < s; r++) {
        x = mulMod(x, x, n);
        if (x == n - 1) return false;
        if (x == 1) return true;  // nontrivial sqrt(1) — composite
    }
    return true;  // never hit +-1 — composite
}
```

### Part 5: Testing (5 minutes)

**Interviewer**: "Test strategy?"

**Candidate**: "Four layers. (1) Known primes: 2, 3, 5, the Mersenne primes 2⁶¹-1 and 2¹²⁷-1 (fit in long? 2¹²⁷-1 overflows long — use the Mersenne primes that fit: 2⁶¹-1 = 2305843009213693951, and 2⁸⁹-1... 2⁸⁹ > 2⁶³. So 2⁶¹-1 and the largest known 64-bit primes). (2) Known composites: even numbers, perfect squares, products of known primes, 341 (Fermat pseudoprime base 2!), 561 (Carmichael!), 1105 (Carmichael), and 3215031751 (the smallest strong pseudoprime to bases 2 and 3 — the exact reason a single base is not enough; it fails base 2 and 3, needs base 5). (3) Exhaustive brute-force sweep: compare against a trial-division oracle for all odd n < 100,000 — 50,000 comparisons, exact agreement required. (4) The OEIS strong-pseudoprime list against the full 12-base set for 64-bit: the guarantee is *no* strong pseudoprime passes all 12 bases — verified up to 2⁶⁴."

### Part 6: Follow-ups (5 minutes)

**Interviewer**: "What about the big-number path — how does Java's own `isProbablePrime` relate?"

**Candidate**: "`BigInteger.isProbablePrime(certainty)` runs Miller-Rabin with random bases (plus a Lucas primality test) and certifies failure probability < 2^{-certainty}. For RSA key generation the JDK uses it internally. My BigInteger path would mirror it, but for the lab I'd also implement the **Baillie-PSW** combination — Miller-Rabin base 2 plus the Lucas probable-prime test — which has no known counterexample below 2⁶⁴ and is what serious libraries use to cut witness count. If the interview goes further: the Lucas test detects the Carmichael-type traps that BPSW handles; the famous 2004 counterexample search stopped at 10¹⁷ with none found... the famous counterexample to BPSW is still unknown."

**Interviewer**: "And the exact-primality fallback for when Miller-Rabin says maybe?"

**Candidate**: "For the 64-bit path, determinism is already exact — the 12-base set *proves* primality for every long. For big inputs, when the customer demands a certificate, you switch to ECPP (Atkin-Morain) or AKS — the latter is polynomial but with a horrible constant; ECPP is what production certifiers (PRIMO, PARI's isprime) actually use. Miller-Rabin is the filter; ECPP is the certifier. I'd document that boundary in the API: `isProbablyPrime` vs `isPrime`."

---

## Scoring Rubric

| Area | Excellent (3) | Good (2) | Needs Work (1) |
|------|---------------|----------|----------------|
| Theory | Square-root-of-1 argument, liar-subgroup bound 1/4, Carmichael example | States Fermat + failure | No theory |
| Overflow | Handles 128-bit products correctly (multiplyHigh / limbs) | Uses BigInteger everywhere | a·b overflows silently |
| Witness logic | Full sequence analysis, nontrivial sqrt detection | Basic loop | Confused with Fermat |
| Determinism | 12-base set for long; documents probabilistic contract for big n | Random bases only | Single base |

## Red Flags
- Using `(a * b) % m` on longs without overflow analysis.
- Believing one base is enough (the 3215031751 trap).
- Testing only with small primes and calling it verified.
- Confusing the Fermat test with Miller-Rabin.

## Key Takeaways
- n - 1 = 2^s · d; witness iff a^d hits 1 without preceding -1 (nontrivial sqrt of 1).
- Liars form a proper subgroup → ≤ 1/4 fraction → k bases give 4^{-k} error.
- Deterministic 64-bit: the 12-base set; Carmichael numbers (561) explain why Fermat fails.
- 128-bit products on longs need `Math.multiplyHigh` or limb decomposition.
