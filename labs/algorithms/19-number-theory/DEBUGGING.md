# Debugging — Number Theory

## GCD Verification

`java
// Verify that result divides both inputs
long g = gcd(a, b);
assert a % g == 0 : "g does not divide a";
assert b % g == 0 : "g does not divide b";
`

## Extended GCD Verification

`java
long[] ext = extendedGcd(252, 105);
assert ext[1] * 252 + ext[2] * 105 == ext[0];
`

## Sieve Correctness

`java
// Verify by trial division
boolean isPrimeNaive(int n) {
    if (n < 2) return false;
    for (int i = 2; i * i <= n; i++)
        if (n % i == 0) return false;
    return true;
}
`

## Modular Exponentiation Verification

`java
// Verify with known results
assert modPow(2, 10, 1000) == 24;  // 2^10 = 1024 mod 1000 = 24
`
"@

wf "REFACTORING.md" @"
# Refactoring — Number Theory

## Extract ModArithmetic Class

`java
class ModArithmetic {
    private final long mod;
    public ModArithmetic(long mod) { this.mod = mod; }
    public long add(long a, long b) { return (a + b) % mod; }
    public long mul(long a, long b) { return (a * b) % mod; }
    public long pow(long a, long e) { ... }
    public long inv(long a) { ... }
}
`

## Use BigInteger for Large Numbers

For numbers exceeding 64 bits, use java.math.BigInteger which provides all modular arithmetic operations natively.

## Caching

Cache small primes for repeated queries. Cache totient values for numbers up to n using a sieve-like computation.

## Functional Composition

Compose number theory operations using a functional pipeline: gcd -> extended -> modular inverse.
