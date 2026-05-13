# Mathematical Foundations: Proofs for Arithmetic

## 1. Proof by Mathematical Induction

### 1.1 Sum of First n Natural Numbers

**Theorem**: For all n ∈ ℕ, 1 + 2 + 3 + ... + n = n(n+1)/2

**Proof**:
- Base case (n=1): Left side = 1, Right side = 1(2)/2 = 1. ✓
- Inductive hypothesis: Assume true for n=k: 1 + 2 + ... + k = k(k+1)/2
- Inductive step: Prove for n=k+1
  - 1 + 2 + ... + k + (k+1)
  - = k(k+1)/2 + (k+1) [by IH]
  - = (k(k+1) + 2(k+1)) / 2
  - = (k+1)(k+2) / 2
  - = (k+1)((k+1)+1)/2 ✓

∎

### 1.2 Sum of Powers of 2

**Theorem**: 1 + 2 + 4 + ... + 2ⁿ = 2ⁿ⁺¹ - 1

**Proof**:
- Base case (n=0): 1 = 2¹ - 1 ✓
- IH: Assume 1 + 2 + ... + 2ᵏ = 2ᵏ⁺¹ - 1
- Step: 1 + 2 + ... + 2ᵏ + 2ᵏ⁺¹
  - = 2ᵏ⁺¹ - 1 + 2ᵏ⁺¹ [by IH]
  - = 2 × 2ᵏ⁺¹ - 1
  - = 2ᵏ⁺² - 1 ✓

∎

### 1.3 Divisibility by 3

**Theorem**: 3 divides (10ⁿ - 1) for all n ∈ ℕ

**Proof**:
- Base case (n=0): 10⁰ - 1 = 0, divisible by 3 ✓
- IH: 3 | (10ᵏ - 1)
- Step: 10ᵏ⁺¹ - 1 = 10 × 10ᵏ - 1
  - = 10(10ᵏ - 1) + 9
  - = 10(10ᵏ - 1) + 3×3
  - 3 | [10(10ᵏ - 1)] by IH
  - 3 | 9
  - ∴ 3 | (10ᵏ⁺¹ - 1) ✓

∎

## 2. Euclid's Proof of Infinite Primes

### 2.1 Classic Proof

**Theorem**: There are infinitely many prime numbers.

**Proof**:
1. Assume finitely many primes: p₁, p₂, ..., pₙ
2. Consider N = p₁p₂...pₙ + 1
3. N is either prime or composite
4. If N is prime, it's not in {p₁, ..., pₙ} - contradiction
5. If N is composite, it has a prime factor p
6. p cannot be any of p₁, ..., pₙ because:
   - If p = pᵢ, then p divides N and p₁...pₙ
   - So p divides their difference: (N - p₁...pₙ) = 1
   - But p > 1, so p cannot divide 1 - contradiction
7. Therefore, our assumption was false; infinitely many primes exist.

∎

### 2.2 Stronger Version

**Theorem**: The number of primes less than n is > log(log(n)) for n > eᵉ

**Proof**: Using the product formula for primes and bounding techniques.

## 3. Euclidean Algorithm Proof

### 3.1 Correctness Proof

**Lemma**: gcd(a, b) = gcd(b, a mod b)

**Proof**:
- Let d = gcd(a, b), so a = d×a', b = d×b' with gcd(a', b') = 1
- a mod b = a - ⌊a/b⌋×b = d×(a' - ⌊a'/b'⌋×b')
- d divides both a and b, so d divides (a mod b)
- gcd(b, a mod b) ≥ d
- But if g = gcd(b, a mod b), then g divides b and (a mod b)
- So g divides: a = (a mod b) + ⌊a/b⌋×b
- Thus g divides a and b, so g ≤ d
- Therefore gcd(b, a mod b) = d = gcd(a, b) ✓

∎

### 3.2 Extended Euclidean Algorithm Proof

**Theorem**: For integers a, b, there exist x, y such that ax + by = gcd(a, b)

**Proof by induction on b**:
- Base: b = 0: gcd(a, 0) = a, so x = 1, y = 0 works
- IH: Assume for remainder r = a mod b
- Step: gcd(a, b) = gcd(b, r)
- By IH: bx₁ + ry₁ = gcd(a, b)
- Since r = a - ⌊a/b⌋×b:
  - bx₁ + (a - ⌊a/b⌋×b)y₁ = gcd(a, b)
  - ax₁ + b(y₁ - ⌊a/b⌋×y₁) = gcd(a, b)
- Set x = y₁, y = (y₁ - ⌊a/b⌋×y₁)
- Then ax + by = gcd(a, b) ✓

∎

## 4. Fundamental Theorem of Arithmetic Proof

### 4.1 Existence Proof (Construction)

**Lemma**: Every integer n > 1 can be written as product of primes.

**Proof by strong induction**:
- Base: n = 2 is prime, so trivially a product
- IH: Assume all integers < n can be written as product of primes
- Step: If n is prime, done. If composite, n = a×b with 1 < a, b < n
- By IH: a = p₁...pₖ, b = q₁...qₘ
- So n = p₁...pₖq₁...qₘ ✓

∎

### 4.2 Uniqueness Proof

**Theorem**: Every integer n > 1 has a unique prime factorization.

**Proof by contradiction**:
- Assume two different factorizations: n = p₁p₂...pₛ = q₁q₂...qₜ
- Cancel common primes to get: p'₁...p'ₓ = q'₁...q'ᵧ where x, y ≥ 1
- p'₁ divides left side, so must divide right side
- But p'₁ is not equal to any q'ᵢ (otherwise it would have been cancelled)
- Since p'₁ divides product q'₁...q'ᵧ, it must divide one of them
- But primes are irreducible, so p'₁ = q'ⱼ - contradiction
- Therefore factorization is unique ✓

∎

## 5. Modular Arithmetic Theorems

### 5.1 Fermat's Little Theorem

**Theorem**: If p is prime and a is not divisible by p, then a^(p-1) ≡ 1 (mod p)

**Proof using group theory**:
- Consider multiplicative group mod p: {1, 2, ..., p-1}
- Order of group = p - 1
- By Lagrange's theorem: a^(p-1) = 1 in the group
- Therefore a^(p-1) ≡ 1 (mod p) ✓

**Alternative elementary proof**:
- Consider numbers {a, 2a, ..., (p-1)a} mod p
- They are all distinct (otherwise p divides a)
- They must be permutation of {1, 2, ..., p-1}
- Multiply all: a^(p-1) × (p-1)! ≡ (p-1)! (mod p)
- Cancel (p-1)!: a^(p-1) ≡ 1 (mod p) ✓

∎

### 5.2 Euler's Theorem

**Theorem**: If gcd(a, n) = 1, then a^φ(n) ≡ 1 (mod n)

**Proof**:
- φ(n) = |{1 ≤ k ≤ n | gcd(k, n) = 1}|
- The set is a multiplicative group of order φ(n)
- By Lagrange's theorem: a^φ(n) = 1 in the group
- Therefore a^φ(n) ≡ 1 (mod n) ✓

∎

## 6. Proof of Irrationality

### 6.1 √2 is Irrational

**Theorem**: √2 ∉ ℚ

**Proof by contradiction**:
- Assume √2 = a/b in lowest terms (gcd(a,b) = 1)
- Then 2 = a²/b², so a² = 2b²
- Therefore a² is even, so a is even: a = 2k
- Substituting: 4k² = 2b², so 2k² = b²
- Therefore b² is even, so b is even
- But gcd(a, b) = 1 and both even - contradiction!
- Therefore √2 is irrational ✓

∎

### 6.2 √3 is Irrational

**Proof**: Similar structure
- Assume √3 = a/b in lowest terms
- 3 = a²/b², a² = 3b²
- a² divisible by 3, so a = 3k
- 9k² = 3b², so 3k² = b²
- b² divisible by 3, so b = 3m
- gcd(a,b) ≥ 3 - contradiction ✓

## 7. Properties of Divisibility

### 7.1 Transitivity Proof

**Theorem**: If a|b and b|c, then a|c

**Proof**:
- a|b means b = a·k for some integer k
- b|c means c = b·m for some integer m
- Substituting: c = (a·k)·m = a·(k·m)
- Since k·m ∈ ℤ, a|c ✓

∎

### 7.2 Linear Combination Theorem

**Theorem**: If d = gcd(a, b), then d = ax + by for some integers x, y

**Proof**: Follows from Extended Euclidean Algorithm proof above.

### 7.3 Euclid's Lemma

**Theorem**: If p is prime and p|ab, then p|a or p|b

**Proof**:
- If p|a, done
- If not, gcd(p, a) = 1 (since only divisors of p are 1 and p)
- By Bezout: 1 = xp + ya for some x, y
- Multiply by b: b = xpb + yab
- p|b because p|(xpb) and p|(yab) by assumption
- Therefore p|b ✓

∎

## 8. Binomial Theorem Proof

**Theorem**: (a + b)ⁿ = Σₖ₌₀ⁿ C(n,k) aⁿ⁻ᵏbᵏ

**Proof by induction**:
- Base (n=0): (a + b)⁰ = 1 = C(0,0)a⁰b⁰ ✓
- IH: Assume (a + b)ᵏ = Σᵢ₌₀ᵏ C(k,i) aᵏ⁻ⁱbⁱ
- Step: (a + b)ᵏ⁺¹ = (a + b)(a + b)ᵏ
  - = a·Σᵢ C(k,i)aᵏ⁻ⁱbⁱ + b·Σᵢ C(k,i)aᵏ⁻ⁱbⁱ
  - = Σᵢ C(k,i)aᵏ⁻ⁱ⁺¹bⁱ + Σᵢ C(k,i)aᵏ⁻ⁱbⁱ⁺¹
  - = Σᵢ C(k,i)aᵏ⁻ⁱ⁺¹bⁱ + Σⱼ C(k,j-1)aᵏ⁻ⁱbʲ where j=i+1
  - = C(k,0)aᵏ⁺¹ + Σᵢ₌₁ᵏ [C(k,i) + C(k,i-1)]aᵏ⁻ⁱ⁺¹bⁱ + C(k,k)bᵏ⁺¹
  - = C(k+1,0)aᵏ⁺¹ + Σᵢ₌₁ᵏ C(k+1,i)aᵏ⁻ⁱ⁺¹bⁱ + C(k+1,k+1)bᵏ⁺¹
  - = Σₖ₌₀ᵏ⁺¹ C(k+1,i) aᵏ⁺¹⁻ⁱbⁱ ✓

∎

## 9. Archimedean Property Proof

**Theorem**: For any real ε > 0 and any real M, ∃n ∈ ℕ such that nε > M

**Proof**:
- Choose n > M/ε
- By density of ℚ, such n exists in ℕ
- Then nε > M ✓

∎

## 10. Well-Ordering Principle

**Theorem**: Every non-empty subset of ℕ has a least element.

**Proof** (equivalent to induction):
- Assume S ⊆ ℕ, S ≠ ∅, has no least element
- Define P(n): "n ∉ S"
- P(0): If 0 ∈ S, it would be least element - contradiction
- IH: Assume P(k) for all k < n
- If n ∈ S, then n is least (since all < n are not in S)
- But this contradicts assumption
- So n ∉ S
- By induction, P(n) holds for all n
- Therefore S = ∅ - contradiction
- So every non-empty S ⊆ ℕ has least element ✓

∎

## 11. Bernoulli's Inequality

**Theorem**: For real x > -1 and integer n ≥ 0: (1 + x)ⁿ ≥ 1 + nx

**Proof by induction**:
- Base (n=0): (1+x)⁰ = 1 ≥ 1 ✓
- IH: (1+x)ᵏ ≥ 1 + kx
- Step: (1+x)ᵏ⁺¹ = (1+x)(1+x)ᵏ ≥ (1+x)(1 + kx)
  - = 1 + kx + x + kx² ≥ 1 + (k+1)x ✓

∎