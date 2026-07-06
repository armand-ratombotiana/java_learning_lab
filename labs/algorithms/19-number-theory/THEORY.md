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
