# Number Theory — Study Guide

## Core Concepts

### Primality Testing
- **Trial Division**: test divisibility by primes up to √n — O(√n)
- **Miller-Rabin**: probabilistic test, O(k log³ n) for k rounds
- **AKS**: deterministic polynomial-time but impractical

### Modular Arithmetic
- a mod m = remainder when a is divided by m
- Modular inverse: a⁻¹ mod m exists iff gcd(a,m)=1
- Fast exponentiation: a^b mod m using binary exponentiation O(log b)

### Chinese Remainder Theorem
- System x ≡ a_i (mod n_i) with pairwise coprime n_i
- Unique solution modulo N = Π n_i
- Solution: x = Σ a_i * N_i * N_i⁻¹ mod N

## Implementation Checklist
1. Use long (64-bit) and handle overflow with BigInteger when needed
2. Miller-Rabin: test small primes as bases first
3. Modular exponentiation: use powMod with repeated squaring
4. Extended GCD returns (g, x, y) where ax + by = g

## Common Pitfalls
- Not using BigInteger for large number arithmetic (>2⁶³)
- Miller-Rabin: Carmichael numbers pass Fermat test but fail Miller-Rabin
- CRT requires pairwise coprime moduli
