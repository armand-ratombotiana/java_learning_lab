# Arithmetic: Operations, Properties, and Number Systems

## 1. The Foundation of Mathematics

### 1.1 Historical Context
Arithmetic is the oldest branch of mathematics, dating back to ancient civilizations. The Egyptians used hieroglyphics for numbers around 3000 BCE, while the Babylonians developed a base-60 (sexagesimal) system. The Hindu-Arabic numeral system we use today emerged in India around 500 CE and was refined by Arab mathematicians in the 9th century.

### 1.2 The Peano Axioms
The natural numbers are defined by the Peano axioms:
1. 0 is a natural number
2. Every natural number has a successor
3. 0 is not the successor of any number
4. Different numbers have different successors
5. (Induction axiom) If a property holds for 0 and holds for the successor of any number that has it, it holds for all natural numbers

## 2. Basic Operations

### 2.1 Addition (+)
- **Definition**: Combining two quantities
- **Notation**: a + b = c
- **Properties**:
  - Commutative: a + b = b + a
  - Associative: (a + b) + c = a + (b + c)
  - Identity: a + 0 = a
  - Closure: If a, b ∈ ℕ, then a + b ∈ ℕ

### 2.2 Subtraction (−)
- **Definition**: Finding the difference between two quantities
- **Notation**: a − b = c means a = b + c
- **Note**: Not closed in ℕ (requires integers for all results)

### 2.3 Multiplication (×)
- **Definition**: Repeated addition
- **Notation**: a × b = c
- **Properties**:
  - Commutative: a × b = b × a
  - Associative: (a × b) × c = a × (b × c)
  - Identity: a × 1 = a
  - Distributive: a(b + c) = ab + ac
  - Zero property: a × 0 = 0

### 2.4 Division (÷)
- **Definition**: Finding how many times one number fits in another
- **Notation**: a ÷ b = c means a = b × c
- **Types**:
  - Exact division (divisible)
  - Quotient with remainder: a = bq + r where 0 ≤ r < b

## 3. Number Systems

### 3.1 Natural Numbers (ℕ)
- ℕ = {0, 1, 2, 3, 4, ...}
- Foundational for counting
- Additive closure holds

### 3.2 Integers (ℤ)
- ℤ = {..., -3, -2, -1, 0, 1, 2, 3, ...}
- Additive closure, subtraction closed
- No multiplicative inverse for all elements

### 3.3 Rational Numbers (ℚ)
- ℚ = {a/b | a, b ∈ ℤ, b ≠ 0}
- Decimal representation either terminates or repeats
- Dense: Between any two rationals, there's another rational
- Every rational has an additive inverse

### 3.4 Real Numbers (ℝ)
- All rationals plus all irrationals
- Includes √2, π, e
- Complete: Every Cauchy sequence converges to a real number
- Dedekind cuts or Cauchy sequences define them

### 3.5 Complex Numbers (ℂ)
- ℂ = {a + bi | a, b ∈ ℝ, i² = -1}
- Fundamental Theorem of Algebra: Every polynomial has n complex roots
- Argand plane visualization

## 4. Advanced Operations

### 4.1 Exponentiation
- aⁿ = a × a × ... × a (n times)
- Laws:
  - aᵐ × aⁿ = aᵐ⁺ⁿ
  - aᵐ ÷ aⁿ = aᵐ⁻ⁿ
  - (aᵐ)ⁿ = aᵐⁿ
  - (ab)ⁿ = aⁿbⁿ
  - a⁰ = 1 (a ≠ 0)

### 4.2 Roots
- √a = b means b² = a
- ∛a = b means b³ = a
- n-th root: xⁿ = a → x = a^(1/n)

### 4.3 Logarithms
- logₐ(x) = y means aʸ = x
- Laws:
  - log(ab) = log(a) + log(b)
  - log(a/b) = log(a) - log(b)
  - log(aⁿ) = n log(a)

## 5. Divisibility and Primes

### 5.1 Divisibility
- a divides b (a|b) if ∃c ∈ ℤ such that b = ac
- Properties:
  - Transitive: a|b and b|c → a|c
  - Linear combination: a|b and a|c → a|(mb + nc) for any m,n

### 5.2 Prime Numbers
- p is prime if p > 1 and only divisors are 1 and p
- Fundamental Theorem of Arithmetic: Every integer > 1 is either prime or product of primes (unique)
- Prime counting function π(x) ~ x/ln(x)

### 5.3 Euclidean Algorithm
For finding GCD(a, b):
```
gcd(a, b) = gcd(b, a mod b) until b = 0
```

### 5.4 Extended Euclidean Algorithm
Finds integers x, y such that ax + by = gcd(a, b)

## 6. Modular Arithmetic

### 6.1 Definition
a ≡ b (mod n) means n | (a - b)

### 6.2 Properties
- Reflexive: a ≡ a (mod n)
- Symmetric: a ≡ b → b ≡ a
- Transitive: a ≡ b, b ≡ c → a ≡ c

### 6.3 Operations
- Addition: a ≡ b, c ≡ d → a + c ≡ b + d
- Multiplication: a ≡ b, c ≡ d → ac ≡ bd
- Exponentiation: a ≡ b → aᵏ ≡ bᵏ

## 7. Number Theory Results

### 7.1 Euclid's Theorem
There are infinitely many primes.

**Proof**: Assume finitely many p₁, p₂, ..., pₙ. Consider N = p₁p₂...pₙ + 1. N is not divisible by any pᵢ, so N is either prime or has a prime factor not in the list.

### 7.2 Fermat's Little Theorem
If p is prime and a is not divisible by p:
a^(p-1) ≡ 1 (mod p)

### 7.3 Euler's Theorem
If gcd(a, n) = 1:
a^φ(n) ≡ 1 (mod n)
Where φ(n) = number of integers < n coprime to n

## 8. Special Numbers

### 8.1 Fibonacci Numbers
F₀ = 0, F₁ = 1, Fₙ = Fₙ₋₁ + Fₙ₋₂
- Closed form: Fₙ = (φⁿ - (-φ)⁻ⁿ) / √5
- φ = (1 + √5) / 2 (Golden ratio)

### 8.2 Perfect Numbers
n equals sum of its proper divisors
- Even: n = 2^(p-1)(2^p - 1) where 2^p - 1 is prime (Mersenne prime)

### 8.3 Bernoulli Numbers
Appear in series: Σiⁿ = Bₙ₊₁((n+1)!)/...

## 9. Mathematical Induction

### 9.1 Principle of Mathematical Induction
To prove P(n) for all n ≥ n₀:
1. Base case: Prove P(n₀)
2. Inductive step: If P(k) is true, then P(k+1) is true

### 9.2 Strong Induction
Instead of assuming P(k), assume P(n₀), P(n₀+1), ..., P(k)

## 10. Representational Systems

### 10.1 Base-b Representation
Any number can be written as:
Σ(dᵢ × bⁱ) where dᵢ ∈ {0, 1, ..., b-1}

### 10.2 Common Bases
- Binary (base 2): 0, 1
- Octal (base 8): 0-7
- Decimal (base 10): 0-9
- Hexadecimal (base 16): 0-9, A-F

### 10.3 Base Conversion Algorithm
```
Divide by new base repeatedly, remainder sequence is the representation
```