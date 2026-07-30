# Cryptography Deep — Study Guide

## Core Concepts

### Symmetric vs Asymmetric
- **Symmetric**: same key for encrypt/decrypt (AES). Fast, secure, but key distribution problem.
- **Asymmetric**: public/private key pair (RSA). Slower, solves key distribution.

### AES Modes
- **ECB**: each block independently — insecure (patterns visible)
- **CBC**: each block XORed with previous ciphertext — needs IV
- **GCM**: authenticated encryption — provides confidentiality + integrity

### RSA
- Key generation: n=pq, ed ≡ 1 (mod φ(n))
- Encryption: c = m^e mod n
- Decryption: m = c^d mod n
- Minimum key size: 2048 bits

### Diffie-Hellman
- Public parameters: prime p, generator g
- Alice: a, sends g^a mod p; Bob: b, sends g^b mod p
- Shared secret: g^(ab) mod p

## Implementation Checklist
1. Use SecureRandom for key generation, not Random
2. Properly handle IV (random, never reuse with same key)
3. GCM requires unique nonce per encryption
4. RSA with PKCS#1 v1.5 or OAEP padding (never textbook RSA)

## Common Pitfalls
- ECB mode leaks plaintext structure
- Reusing nonce in GCM destroys all security
- Textbook RSA is malleable — always use padding
- Weak DH parameters (small primes) enable attacks
