$labDir = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\algorithms\19-number-theory"

function wf($name, $content) {
    Set-Content -Path (Join-Path $labDir $name) -Value $content
}

wf "README.md" @"
# Number Theory and Modular Arithmetic — Overview

This lab covers fundamental number theory algorithms: GCD (Euclidean and Extended Euclidean), prime testing (Miller-Rabin, Sieve of Eratosthenes), modular arithmetic (exponentiation, inverse), Chinese Remainder Theorem, Euler's totient function, and fast exponentiation. These algorithms are essential for cryptography, coding theory, and computational mathematics.

## Learning Objectives

- Implement Euclidean and Extended Euclidean algorithms for GCD
- Build a segmented Sieve of Eratosthenes for prime generation
- Implement Miller-Rabin probabilistic primality test
- Perform modular exponentiation and compute modular inverses
- Solve systems of congruences using CRT

## Prerequisites

- Basic modular arithmetic
- Understanding of prime numbers and divisibility
- Java big integer support
"@

wf "THEORY.md" @"
# Number Theory — Theoretical Foundation

## Euclidean Algorithm

The Euclidean algorithm computes GCD(a,b) by repeatedly applying the property gcd(a,b) = gcd(b, a mod b). The algorithm terminates when b = 0, at which point a is the GCD. It runs in O(log min(a,b)) steps, making it exceptionally fast even for large numbers.

## Extended Euclidean Algorithm

The extended Euclidean algorithm finds integers x and y such that ax + by = gcd(a,b). These coefficients are crucial for computing modular inverses and solving linear Diophantine equations. The algorithm extends the Euclidean algorithm by tracking the coefficients at each step.

## Sieve of Eratosthenes

The Sieve of Eratosthenes generates all primes up to n by iteratively marking multiples of each prime as composite. The segmented sieve extends this to ranges where the full range does not fit in memory. Complexity is O(n log log n).

## Miller-Rabin Primality Test

Miller-Rabin is a probabilistic test that determines whether a number is prime. It is based on the properties of modular exponentiation and Fermat's little theorem. By testing with carefully chosen bases, the test can be made deterministic for numbers up to known bounds.

## Modular Arithmetic

Modular arithmetic operates on residues modulo m. Key operations include modular addition ((a+b) mod m), multiplication ((a*b) mod m), exponentiation (a^b mod m), and finding the modular inverse (a^-1 mod m). The modular inverse exists only when gcd(a,m) = 1.

## Chinese Remainder Theorem

CRT solves systems of congruences x ≡ a_i (mod n_i) where the moduli n_i are pairwise coprime. The solution is unique modulo the product N = n_1 * n_2 * ... * n_k. CRT has applications in cryptography (RSA), large integer arithmetic, and signal processing.

## Euler's Totient Function

φ(n) counts the number of integers between 1 and n that are coprime to n. For a prime p, φ(p) = p-1. For n = p*q where p,q are prime, φ(n) = (p-1)(q-1), which is crucial for RSA encryption.
"@

wf "WHY_IT_EXISTS.md" @"
# Why Number Theory Algorithms Exist

Number theory has been studied for thousands of years, but algorithmic number theory emerged with the advent of computers. The ancient Euclidean algorithm (c. 300 BC) is the oldest known algorithm still in use. Modern cryptographic systems rely entirely on the computational difficulty of certain number theory problems.

The Sieve of Eratosthenes (c. 240 BC) remained the most efficient prime generation method for over 2000 years. It is still used as a building block in more complex algorithms. The segmented sieve variant handles ranges that exceed memory, enabling prime generation up to 10^12 and beyond.

Miller-Rabin (1980) addressed the practical need for fast primality testing. Prior algorithms like trial division were too slow for the large numbers used in cryptography (1024-4096 bits). Miller-Rabin provides a tradeoff between certainty and speed: each test round halves the probability of error.

The Extended Euclidean algorithm is essential for computing modular inverses, which are required in RSA key generation, elliptic curve cryptography, and the CRT implementation. Without it, asymmetric cryptography as we know it would not be possible.

CRT was discovered by Sun Tzu in the 3rd century AD and later proved by Gauss. In modern computing, CRT enables fast computation by splitting arithmetic modulo a large number N into parallel arithmetic modulo smaller factors. RSA decryption uses CRT for a 4x speedup.

Euler's totient function forms the mathematical foundation of RSA: the encryption and decryption exponents are inverses modulo φ(n). The security of RSA relies on the difficulty of computing φ(n) without knowing the factorization of n.
"@

wf "WHY_IT_MATTERS.md" @"
# Why Number Theory Matters

Number theory algorithms are the foundation of modern cryptography. Every time you connect to a secure website, your browser performs modular exponentiation for RSA or elliptic curve key exchange. Every cryptocurrency transaction relies on modular arithmetic. Every SSH session uses number theory for authentication.

## Practical Impact

- RSA encryption uses modular exponentiation with 2048-bit numbers
- Digital signatures use modular arithmetic for signing and verification
- Random number generators use modular arithmetic for generating sequences
- Hash functions use modular arithmetic in their design
- Error-correcting codes use finite field arithmetic for Reed-Solomon codes

## Performance Critical

Modular exponentiation of 2048-bit numbers must complete in milliseconds on mobile devices. The square-and-multiply algorithm reduces the number of operations from exponential to linear in the bit length. CRT speeds up RSA decryption by a factor of 4.

## Beyond Cryptography

Number theory algorithms are used in programming contests for solving problems involving large numbers, in computational biology for sequence alignment scoring, and in computer graphics for hash-based texture generation.
"@

wf "HISTORY.md" @"
# History of Number Theory Algorithms

c. 300 BC: Euclid's Elements describes the Euclidean algorithm for GCD.

c. 240 BC: Eratosthenes develops the Sieve of Eratosthenes for prime generation.

3rd century AD: Sun Tzu discovers the Chinese Remainder Theorem.

1736: Euler publishes his totient function and Fermat-Euler theorem.

1801: Gauss publishes Disquisitiones Arithmeticae, formalizing number theory.

1969: Pollard's rho algorithm for integer factorization using modular arithmetic.

1977: Rivest, Shamir, and Adleman publish RSA, launching modern cryptography.

1980: Miller-Rabin probabilistic primality test published.

1987: Lenstra's elliptic curve factorization algorithm developed.

1990s: Number Field Sieve becomes the fastest general-purpose factorization method.

2000s: Pairing-based cryptography extends number theory to elliptic curve bilinear maps.

2010s: Post-quantum cryptography research explores number theory-based alternatives.

2020s: Homomorphic encryption uses advanced number theory for computation on encrypted data.
"@

wf "MENTAL_MODELS.md" @"
# Mental Models for Number Theory

## Euclidean Algorithm — "The Measuring Stick"

Imagine finding the largest ruler that can measure both 9 and 6 units exactly. Try 6: 9 = 1*6 + 3 (remainder 3). Now measure 6 with remainder 3: 6 = 2*3 + 0. The last non-zero remainder 3 is the GCD. This is Euclid's algorithm: repeatedly replace the larger number with the remainder when divided by the smaller.

## Sieve of Eratosthenes — "The Prime Filter"

Picture a row of numbered boxes from 1 to n. Cross out 1 (not prime). Circle 2, then cross out every 2nd box. Circle 3, cross out every 3rd box. Skip 4 (already crossed). Circle 5, cross out every 5th box. Continue until you've circled all remaining numbers. The circled numbers are prime.

## Miller-Rabin — "The Witness"

To check if a number might be prime, ask a series of witnesses. Each witness provides evidence: "Based on my test, this number is probably prime." A single witness saying "composite" is definite. To be sure, you need enough witnesses that the probability of all being wrong is negligible.

## CRT — "The Coordinate System"

A number modulo N can be represented by its residues modulo the factors of N. This is like representing a point in 2D by its (x,y) coordinates. Operations on the number can be done independently on each coordinate and then reconstructed.
"@

wf "HOW_IT_WORKS.md" @"
# How Number Theory Algorithms Work

## Euclidean Algorithm Example

GCD(252, 105):
252 = 2 * 105 + 42
105 = 2 * 42 + 21
42 = 2 * 21 + 0
GCD = 21

## Extended Euclidean Algorithm Example

GCD(252, 105) = 21, find x,y: 252x + 105y = 21
42 = 252 - 2*105
21 = 105 - 2*42 = 105 - 2*(252 - 2*105) = 5*105 - 2*252
So x = -2, y = 5

## Miller-Rabin Test for n = 221

n-1 = 220 = 2^2 * 55 (r=2, d=55)
Base a = 174: a^55 mod 221 = 47 != 1, != n-1
Check a^(55*2) mod 221 = 47^2 mod 221 = 220 = n-1 -> base 174 is witness
That tells us nothing. Try a = 137: a^55 mod 221 = ...
Continue with multiple bases. 221 = 13 * 17, will eventually be detected.

## Sieve for n = 30

Initial: [true, true, true, true, true, ...] for 0..30
i=2: mark 4,6,8,10,12,14,16,18,20,22,24,26,28,30 as false
i=3: mark 6,9,12,15,18,21,24,27,30 as false
i=5: mark 10,15,20,25,30 as false
i=7: 7^2 = 49 > 30, stop
Primes: 2,3,5,7,11,13,17,19,23,29
"@

wf "INTERNALS.md" @"
# Number Theory — Internal Mechanics

## Euclidean Algorithm

```java
long gcd(long a, long b) {
    while (b != 0) {
        long t = b;
        b = a % b;
        a = t;
    }
    return a;
}
```

## Extended Euclidean Algorithm

```java
long[] extendedGcd(long a, long b) {
    if (b == 0) return new long[]{a, 1, 0};
    long[] vals = extendedGcd(b, a % b);
    long d = vals[0], x1 = vals[1], y1 = vals[2];
    return new long[]{d, y1, x1 - (a / b) * y1};
}
```

## Modular Exponentiation (Square-and-Multiply)

```java
long modPow(long base, long exp, long mod) {
    long result = 1;
    base %= mod;
    while (exp > 0) {
        if ((exp & 1) == 1) result = (result * base) % mod;
        base = (base * base) % mod;
        exp >>= 1;
    }
    return result;
}
```

## Modular Inverse (using Extended GCD)

```java
long modInverse(long a, long m) {
    long[] vals = extendedGcd(a, m);
    if (vals[0] != 1) return -1;  // inverse doesn't exist
    return (vals[1] % m + m) % m;
}
```
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for Number Theory

## Euclidean Algorithm Correctness

gcd(a,b) = gcd(b, a mod b). Proof: Any divisor of a and b also divides b and a - q*b. The algorithm reduces arguments until reaching zero. The number of steps is O(log min(a,b)) because the arguments decrease by at least a factor of phi (golden ratio) every two steps.

## Miller-Rabin Error Probability

For a composite n, at most 1/4 of bases a in [1, n-1] are false witnesses (Miller-Rabin is not a liar). With k independent random bases, the probability of falsely declaring n prime is 4^{-k}. For k = 10, error probability < 10^{-6}.

## CRT Uniqueness

If x ≡ a_i (mod n_i) for coprime n_i, the solution is unique modulo N = product n_i. Proof: If x and y are both solutions, x-y ≡ 0 (mod n_i) for all i, so x-y ≡ 0 (mod N) by coprimality.

## Euler's Totient

φ(n) = n * product_{p|n} (1 - 1/p). For prime power p^k, φ(p^k) = p^k - p^{k-1}. φ is multiplicative: φ(mn) = φ(m)φ(n) for coprime m,n.

## Fermat's Little Theorem

For prime p and a not divisible by p: a^{p-1} ≡ 1 (mod p). This is the basis for the Miller-Rabin test and for computing modular inverses.
"@

wf "VISUAL_GUIDE.md" @"
# Visual Guide — Number Theory

## Sieve of Eratosthenes for n=20

Step 1: [2] [3] [4] [5] [6] [7] [8] [9] [10] [11] [12] [13] [14] [15] [16] [17] [18] [19] [20]
Step 2: Mark multiples of 2 -> [4] [6] [8] [10] [12] [14] [16] [18] [20] crossed
Step 3: Mark multiples of 3 -> [6] [9] [12] [15] [18] crossed
Step 5: Mark multiples of 5 -> [10] [15] [20] crossed (already crossed)
Stop at sqrt(20) ≈ 4.5 (stop after 5^2 > 20)

Primes: 2, 3, 5, 7, 11, 13, 17, 19

## Modular Exponentiation Visualization

3^13 mod 5: 13 = 1101 binary
  bit 1 (LSB): result = 3^1 mod 5 = 3
  bit 0: base = 3^2 = 9 mod 5 = 4
  bit 1: result = 3 * 4^2 = 3 * 16 mod 5 = 48 mod 5 = 3
           base = 4^2 = 16 mod 5 = 1
  bit 1: result = 3 * 1^2 = 3
  Final: 3^13 mod 5 = 3
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive — Number Theory

## Segmented Sieve Implementation

The segmented sieve handles ranges that don't fit in memory by computing primes up to sqrt(upper) and then processing the range in segments. Each segment is a boolean array representing the range [low, high]. Multiples of each base prime are marked within the segment.

```java
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
```

## Miller-Rabin Deterministic Bases

For 32-bit integers, use bases {2, 7, 61}. For 64-bit, use {2, 325, 9375, 28178, 450775, 9780504, 1795265022}. These sets are proven to cover all numbers in their ranges.

## Fast CRT Implementation

CRT requires computing the modular inverse of N/n_i modulo n_i for each congruence. These inverses can be precomputed if the system is reused.
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step — Number Theory Implementation

## Sieve of Eratosthenes Steps

1. Create boolean array isPrime[0..n] initialized to true
2. Set isPrime[0] = isPrime[1] = false
3. For i from 2 to sqrt(n):
   a. If isPrime[i] is true:
      - Mark all multiples of i from i*i to n as false
4. Collect all i where isPrime[i] is true

## Miller-Rabin Steps

1. Write n-1 = d * 2^r where d is odd
2. For each base a in test set:
   a. Compute x = a^d mod n
   b. If x == 1 or x == n-1, continue to next base
   c. Repeat r-1 times: x = x^2 mod n
      - If x == n-1, break (inconclusive for this base)
      - If x == 1, n is composite
   d. If loop completes without breaking, n is composite
3. If all bases pass, n is probably prime
"@

wf "COMMON_MISTAKES.md" @"
# Common Mistakes — Number Theory

- **Forgetting modulo in every multiplication** — Intermediate multiplication of large numbers overflows Java long
- **Modular inverse with non-coprime numbers** — Inverse doesn't exist when gcd(a,m) != 1
- **Negative modulo** — Java's % returns negative for negative numbers; always add m before % m
- **Sieve array too large** — Boolean array of size 10^9 uses 1GB; use segmented sieve
- **Miller-Rabin without strong liars check** — Carmichael numbers pass Fermat test but fail Miller-Rabin
- **Integer overflow in fast exponentiation** — Use java.math.BigInteger for numbers > 2^63
- **GCD of negative numbers** — Handle sign; return absolute value
- **CRT moduli not coprime** — CRT requires pairwise coprime moduli
- **Forgetting long conversion** — Multiplying two ints may overflow before being stored in long
- **Sieve starting from i^2** — Starting from i instead of i^2 wastes operations
"@

wf "DEBUGGING.md" @"
# Debugging — Number Theory

## GCD Verification

```java
// Verify that result divides both inputs
long g = gcd(a, b);
assert a % g == 0 : "g does not divide a";
assert b % g == 0 : "g does not divide b";
```

## Extended GCD Verification

```java
long[] ext = extendedGcd(252, 105);
assert ext[1] * 252 + ext[2] * 105 == ext[0];
```

## Sieve Correctness

```java
// Verify by trial division
boolean isPrimeNaive(int n) {
    if (n < 2) return false;
    for (int i = 2; i * i <= n; i++)
        if (n % i == 0) return false;
    return true;
}
```

## Modular Exponentiation Verification

```java
// Verify with known results
assert modPow(2, 10, 1000) == 24;  // 2^10 = 1024 mod 1000 = 24
```
"@

wf "REFACTORING.md" @"
# Refactoring — Number Theory

## Extract ModArithmetic Class

```java
class ModArithmetic {
    private final long mod;
    public ModArithmetic(long mod) { this.mod = mod; }
    public long add(long a, long b) { return (a + b) % mod; }
    public long mul(long a, long b) { return (a * b) % mod; }
    public long pow(long a, long e) { ... }
    public long inv(long a) { ... }
}
```

## Use BigInteger for Large Numbers

For numbers exceeding 64 bits, use java.math.BigInteger which provides all modular arithmetic operations natively.

## Caching

Cache small primes for repeated queries. Cache totient values for numbers up to n using a sieve-like computation.

## Functional Composition

Compose number theory operations using a functional pipeline: gcd -> extended -> modular inverse.
"@

wf "PERFORMANCE.md" @"
# Performance — Number Theory

## Algorithm Comparison

| Algorithm | Complexity | Input Range |
|-----------|-----------|-------------|
| Euclidean GCD | O(log n) | Up to 2^63 |
| Extended GCD | O(log n) | Up to 2^63 |
| Simple Sieve | O(n log log n) | Up to 10^7 (memory bound) |
| Segmented Sieve | O(n log log n) | Up to 10^12 |
| Miller-Rabin (k rounds) | O(k log^3 n) | Arbitrary precision |
| Modular exponentiation | O(log exp) | Up to 2^63 |
| CRT | O(k log N) | k moduli |

## Benchmark Data

- GCD(10^12, 10^12-1): < 1 microsecond
- Sieve up to 10^7: ~100ms
- Miller-Rabin on 10^12: ~1 microsecond per base
- Modular exponentiation 2^10^9 mod p: ~1 microsecond

## Optimization Tips

- Use bit operations for modulus by power of 2
- Precompute powers for repeated modular exponentiation
- Use segmented sieve for range queries over 10^7
- Cache totient function values up to common limits
"@

wf "SECURITY.md" @"
# Security — Number Theory

## Cryptographic Strength

The security of RSA (2048-bit) relies on the practical difficulty of factoring large semiprimes. Miller-Rabin with sufficient rounds provides the primality guarantees needed for key generation.

## Side-Channel Attacks

Naive modular exponentiation is vulnerable to timing and power analysis. The exponent bits can be inferred from operation patterns. Use constant-time implementations for security-critical applications.

## Weak Primality Tests

Fermat's test alone is insufficient (Carmichael numbers pass). Always use Miller-Rabin or a deterministic test for cryptographic key generation.

## Random Number Quality

Modular arithmetic for cryptography requires cryptographically secure random numbers (SecureRandom, not Random).

## Small Subgroup Attacks

In Diffie-Hellman, using a group where the order has small factors allows attacks. Use safe primes or check subgroup order.
"@

wf "ARCHITECTURE.md" @"
# Architecture — Number Theory

## Library Design

```
NumberTheory Library
├── GCD
│   ├── Euclidean
│   └── ExtendedEuclidean
├── Primality
│   ├── SimpleSieve
│   ├── SegmentedSieve
│   └── MillerRabin
├── Modular
│   ├── Exponentiation
│   ├── Inverse
│   └── Arithmetic
├── CRT
│   ├── Solver
│   └── Garner (mixed radix)
└── Totient
    └── EulerTotient
```

## Integration

- Used as foundation for cryptographic libraries
- Integrated into math competition and programming contest libraries
- Foundation for algebraic algorithms (polynomial GCD, finite fields)
"@

wf "EXERCISES.md" @"
# Exercises — Number Theory

## Beginner
1. Implement GCD using Euclidean algorithm
2. Check if a number is prime via trial division
3. Compute 3^100 mod 7 using modular exponentiation
4. Find all primes up to 100 using simple sieve

## Intermediate
5. Implement Extended Euclidean algorithm
6. Compute modular inverse using Extended GCD
7. Implement Miller-Rabin with bases {2, 3, 5}
8. Solve CRT system: x ≡ 2 mod 3, x ≡ 3 mod 5, x ≡ 2 mod 7

## Advanced
9. Implement segmented sieve for range [10^9, 10^9+10^6]
10. Compute Euler's totient for all numbers up to 1000
11. Implement fast exponentiation using BigInteger
12. Build a complete modular arithmetic class with inverse, pow, and CRT
"@

wf "QUIZ.md" @"
# Quiz — Number Theory

1. What is the time complexity of the Euclidean algorithm?
2. When does a modular inverse not exist?
3. What is a Carmichael number?
4. Why does the sieve start marking from i^2?
5. How many Miller-Rabin bases are needed for 64-bit integers?
6. What is the probability of Miller-Rabin falsely declaring a composite as prime with k rounds?
7. What is Euler's totient of a prime number?
8. How does CRT enable faster RSA decryption?
"@

wf "FLASHCARDS.md" @"
# Flashcards — Number Theory

- Q: Euclidean algorithm complexity? -> A: O(log min(a,b))
- Q: Extended GCD output? -> A: x, y such that ax + by = gcd(a,b)
- Q: Sieve complexity? -> A: O(n log log n)
- Q: Miller-Rabin error bound per base? -> A: 1/4
- Q: Modular inverse exists when? -> A: gcd(a,m) = 1
- Q: CRT requires moduli to be? -> A: Pairwise coprime
- Q: Euler's totient φ(p) for prime p? -> A: p-1
- Q: Modular exponentiation complexity? -> A: O(log exp)
"@

wf "INTERVIEW.md" @"
# Interview Questions — Number Theory

1. "Implement GCD." — Standard coding question
2. "Generate all primes up to n." — Sieve of Eratosthenes
3. "Compute modular inverse." — Extended Euclidean algorithm
4. "How do you test if a large number is prime?" — Miller-Rabin
5. "Solve a system of congruences." — Chinese Remainder Theorem
6. "Implement fast exponentiation." — Square and multiply
"@

wf "REFLECTION.md" @"
# Reflection — Number Theory

- Why is the Euclidean algorithm one of the oldest surviving algorithms?
- How do probabilistic algorithms like Miller-Rabin fit into a field that values exact proofs?
- What is the relationship between GCD and modular inverses?
- How does the CRT enable parallel computation with large numbers?
- Why is number theory, a seemingly pure mathematical field, so crucial for practical security?
"@

wf "REFERENCES.md" @"
# References — Number Theory

- Hardy, G.H., Wright, E.M. "An Introduction to the Theory of Numbers." Oxford, 1975.
- Miller, G.L. "Riemann's Hypothesis and Tests for Primality." J. Computer and System Sciences, 1976.
- Rabin, M.O. "Probabilistic Algorithm for Testing Primality." J. Number Theory, 1980.
- Cormen, T.H. et al. "Introduction to Algorithms." MIT Press, 4th Edition, 2022.
- Knuth, D. "The Art of Computer Programming, Vol. 2: Seminumerical Algorithms." Addison-Wesley.
- Menezes, A. et al. "Handbook of Applied Cryptography." CRC Press, 1996.
"@

Write-Host "19-number-theory: All 24 markdown files created"
