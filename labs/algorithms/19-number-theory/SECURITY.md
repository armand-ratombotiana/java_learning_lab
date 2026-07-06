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
