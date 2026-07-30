# Mock Interview: Bit Manipulation / Number Theory (Count Primes)

## Meta Information

| Aspect | Detail |
|--------|--------|
| Company | Apple |
| Level | Senior SWE |
| Problem | Count Primes (LeetCode 204) |
| Duration | 45 minutes |
| Paradigm | Sieve of Eratosthenes |

---

## Transcript

### Phase 1: Problem Understanding (0:00–5:00)

**Interviewer:** Count the number of prime numbers less than a given non-negative integer n.

**Candidate:** So for `n = 10`, primes less than 10 are `{2, 3, 5, 7}` → count = 4. For `n = 0` and `n = 1` → count = 0.

**Interviewer:** Correct.

**Candidate:** What's the upper bound for n?

**Interviewer:** Up to 5 million.

**Candidate:** That's large enough that we need an efficient algorithm. Simple trial division for each number would be O(n * sqrt(n)) ≈ 5 million * 2236 ≈ too slow.

### Phase 2: Approach Design (5:00–15:00)

**Candidate:** The Sieve of Eratosthenes is the classic O(n log log n) algorithm for this.

**Interviewer:** Can you explain how it works?

**Candidate:** The idea is simple and elegant:
1. Start with a boolean array `isPrime[2..n-1]` all set to true.
2. For each number `i` from 2 to sqrt(n):
   - If `isPrime[i]` is true, then `i` is prime.
   - Mark all multiples of `i` starting from `i*i` as false.
3. Count the remaining true values.

The key insight is that any composite number `c` has a prime factor ≤ sqrt(c). So if we've processed all primes up to sqrt(n), any number still marked as prime is truly prime.

**Interviewer:** Why start marking from `i*i` instead of `2*i`?

**Candidate:** Great question. For a prime `i`, the multiples `2*i, 3*i, ..., (i-1)*i` have already been marked by smaller primes. For example, when `i = 5`:
- `2*5 = 10` was already marked when `i = 2`.
- `3*5 = 15` was already marked when `i = 3`.
- `4*5 = 20` was already marked when `i = 2`.
- `5*5 = 25` hasn't been marked yet.
So starting from `i*i` saves redundant work.

This is the standard optimization and cuts the total markings from approximately n log n down to n log log n.

**Interviewer:** Could we optimize memory?

**Candidate:** Yes — a `boolean[]` uses 1 byte per element. For n = 5,000,000, that's 5 MB. A `BitSet` uses 1 bit per element, reducing memory to ~625 KB. Java's `BitSet` also has a `cardinality()` method that efficiently counts set bits.

The BitSet version:

```java
BitSet isPrime = new BitSet(n);
isPrime.set(2, n);  // set all from 2 to n-1

for (int i = 2; i * i < n; i++) {
    if (isPrime.get(i)) {
        for (int j = i * i; j < n; j += i) {
            isPrime.clear(j);
        }
    }
}
return isPrime.cardinality();
```

**Interviewer:** What about 2 as a special case?

**Candidate:** The sieve handles it naturally. When `i = 2`, we mark 4, 6, 8, 10, ... as composite. And 2 remains marked as prime. Some implementations handle 2 separately and then only iterate over odd numbers from 3 onwards, which halves the memory and the outer loop iterations. But for n ≤ 5 million, the simple optimization is sufficient.

### Phase 3: Coding (15:00–33:00)

**Candidate:** I'll implement the standard sieve with a minor optimization — handling even numbers separately.

```java
class Solution {
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
}
```

**Interviewer:** Walk through `n = 10`.

**Candidate:**
Initial: `isPrime = [F, F, T, T, T, T, T, T, T, T]` (indices 0-9).

- `i = 2`: `isPrime[2] = T`. Mark multiples from 4: j=4,6,8 → F.
  - `[F, F, T, T, F, T, F, T, F, T]`
- `i = 3`: `isPrime[3] = T`. Mark multiples from 9: j=9 → F.
  - `[F, F, T, T, F, T, F, T, F, F]`
- `i = 4`: `isPrime[4] = F`, skip.
- `i = 5` through `i = 9`: `i*i >= 10`, loop ends.

Count: indices 2, 3, 5, 7 are true → 4. Correct.

### Phase 4: Complexity & Follow-ups (33:00–45:00)

**Interviewer:** Time and space complexity?

**Candidate:** Time is O(n log log n). The inner loop marks n/i times for each prime i. Summing over primes ≤ sqrt(n): n * sum(1/p) ≈ n * log log n. Space is O(n) for the boolean array.

**Interviewer:** Could you make it even faster?

**Candidate:** Several optimizations exist:

1. **Skip even numbers**: Only store and iterate over odd numbers. Reduces memory and iterations by half.
2. **Wheel factorization**: Skip multiples of 2, 3, 5 (30 numbers per wheel).
3. **Segmented sieve**: Process the range in segments of size sqrt(n). This reduces memory to O(sqrt(n)) and improves cache locality.
4. **Linear sieve** (Euler's sieve): Marks each composite exactly once using its smallest prime factor. Also O(n), but the constant factor is better in practice.

```java
// Linear sieve - marks each number once
public int countPrimes(int n) {
    if (n <= 2) return 0;
    boolean[] isPrime = new boolean[n];
    Arrays.fill(isPrime, true);
    int[] primes = new int[n / 2];
    int idx = 0;

    for (int i = 2; i < n; i++) {
        if (isPrime[i]) primes[idx++] = i;
        for (int j = 0; j < idx && (long) i * primes[j] < n; j++) {
            isPrime[i * primes[j]] = false;
            if (i % primes[j] == 0) break;  // key: smallest prime factor
        }
    }
    return idx;
}
```

**Interviewer:** What does `i % primes[j] == 0` do?

**Candidate:** That's the key insight of the linear sieve. When `primes[j]` divides `i`, then `i = primes[j] * k`. For the next prime `primes[j+1]`, the composite `i * primes[j+1] = primes[j] * k * primes[j+1]` has `primes[j]` as a smaller prime factor. It would be marked later by `primes[j]` anyway, so we break to avoid marking it now. This ensures each composite is marked exactly once by its smallest prime factor.

**Interviewer:** If we were writing this in a real production system, which version would you pick?

**Candidate:** For n ≤ 10^7, the standard sieve is fine and most readable. For n up to 10^10 with memory constraints, I'd use a segmented sieve. For quality-critical code, I'd consider using a production-tested primality testing library rather than implementing from scratch. But for an interview, the standard sieve demonstrates understanding of the core algorithm.

**Interviewer:** What about probabilistic primality tests like Miller-Rabin?

**Candidate:** Miller-Rabin is useful when we need to check if a single large number is prime (not counting all primes up to a bound). For counting all primes up to n, the sieve is actually faster for n ≤ 10^7 because the constant factors and setup cost of Miller-Rabin for each number would be prohibitive. Miller-Rabin becomes relevant for n > 10^8 in combination with a segmented sieve.

**Interviewer:** Good answer. That covers the problem well.

---

## Key Takeaways

| Topic | Insight |
|-------|---------|
| Sieve of Eratosthenes | O(n log log n) — mark multiples of each prime |
| i*i Optimization | Start marking from i*i to avoid redundant work |
| Memory | boolean[] (1 byte/element) vs BitSet (1 bit/element) |
| Further Optimization | Skip evens, wheel factorization, segmented sieve |
| Linear Sieve | O(n) by marking each composite by its smallest prime factor |
