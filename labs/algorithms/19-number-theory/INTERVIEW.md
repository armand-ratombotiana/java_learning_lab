# Interview Questions: Number Theory

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| LC 204 Count Primes | Medium | Google, Meta, Amazon, Microsoft | Sieve of Eratosthenes |
| LC 263 Ugly Number | Easy | Google, Amazon | Prime factorization |
| LC 264 Ugly Number II | Medium | Google, Amazon | DP / three pointers |
| LC 279 Perfect Squares | Medium | Google, Meta, Amazon | BFS / DP / Lagrange |
| LC 365 Water and Jug Problem | Medium | Google | GCD / Bézout's identity |
| LC 858 Mirror Reflection | Medium | Google | LCM / GCD |
| LC 914 X of a Kind in Deck | Easy | Google | GCD of frequencies |

## NeetCode Reference
- LC 204 Count Primes (NeetCode 150)
- LC 279 Perfect Squares (NeetCode 150)

## Company-Specific Questions
### Google
- Count Primes is a Google classic; expect follow-ups on segmented sieve
- Water and Jug Problem tests your understanding of GCD/Bézout's identity
- Expect number theory applied to cryptography (modular arithmetic)
- How would you generate very large prime numbers?

### Microsoft
- Perfect Squares for optimization-based problems
- GCD/LCM for rational number arithmetic
- How does Windows cryptography use number theory?

### Meta
- Less common; appears in math-heavy screening rounds
- Ugly Number series tests factorization concepts
- Expect number theory combined with DP (Ugly II)

### Amazon
- Count Primes for order number generation
- GCD-based problems for inventory batch processing
- How would you hash product IDs uniformly?

### Apple
- Number theory for cryptography in Secure Enclave
- GCD for audio processing (sample rate conversion)
- Efficient prime generation for device security

### Oracle
- How does Oracle's random number generator work?
- Number theory in database encryption (TDE)
- Design a hash function for distributed database partitioning

## Real Production Scenarios
- Scenario 1: Cryptographic key generation - using prime number generation (Miller-Rabin) to create RSA key pairs for TLS certificate generation in a PKI infrastructure
- Scenario 2: Distributed hash partitioning - using consistent hashing with prime modulo to distribute data across database shards while minimizing rebalancing on cluster resize
- Scenario 3: Audio sample rate conversion - debugging audio distortion by computing GCD of source and target sample rates to determine optimal resampling ratio

## Interview Tips
- Sieve of Eratosthenes is O(n log log n); segmented sieve extends to large ranges
- Euclidean algorithm for GCD is O(log min(a,b))
- Extended Euclidean algorithm for computing modular inverses and Bézout coefficients
- Common edge cases: n=0, n=1, large prime numbers, overflow in multiplication

## Java-Specific Considerations
- `BigInteger` for arbitrary-precision integer arithmetic; `BigInteger.TWO`, `BigInteger.probablePrime()`
- `BigInteger.isProbablePrime(int certainty)` uses Miller-Rabin with configurable certainty
- `BigInteger.modInverse()` and `BigInteger.modPow()` for modular arithmetic
- Seive implementation: `boolean[] isPrime = new boolean[n+1]` for n up to ~10^7
- Pitfall: `isPrime[0] = isPrime[1] = false` initialization is easy to forget
- Pitfall: `for (int i = 2; i * i <= n; i++)` loop bound overflow for very large n
- Use `BitSet` for memory-efficient sieves when n is large (saves 7/8 memory vs boolean[])
