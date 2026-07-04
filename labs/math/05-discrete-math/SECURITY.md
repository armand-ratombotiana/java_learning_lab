# Security in Discrete Mathematics

## Cryptographic Hash Functions

```
Input (any length) → Hash function → Fixed-length digest
```

Properties: preimage resistance, second preimage resistance, collision resistance.

```java
// Java's cryptographic hashing
MessageDigest md = MessageDigest.getInstance("SHA-256");
byte[] hash = md.digest("message".getBytes(StandardCharsets.UTF_8));
```

## RSA Cryptosystem

Based on modular arithmetic and the difficulty of factoring large numbers:

1. Choose primes $p, q$
2. Compute $n = pq$, $\phi(n) = (p-1)(q-1)$
3. Pick $e$ coprime to $\phi(n)$
4. Compute $d = e^{-1} \mod \phi(n)$ (extended Euclidean)
5. Encrypt: $c = m^e \mod n$
6. Decrypt: $m = c^d \mod n$

## Zero-Knowledge Proofs

A prover convinces a verifier of a fact without revealing the fact itself — uses graph isomorphism, discrete log, or other hard problems.

## Error Detection (Parity Bits)

```java
// Even parity: XOR of all bits should be 0
public static boolean checkParity(byte[] data, byte parity) {
    byte computed = 0;
    for (byte b : data) computed ^= b;
    return computed == parity;
}
```
