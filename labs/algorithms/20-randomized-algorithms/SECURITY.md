# Security — Randomized Algorithms

## Predictable Randomness

Using java.util.Random with a known seed allows attackers to predict all random choices. For security applications, always use SecureRandom.

## Randomness Exhaustion

On systems with low entropy, SecureRandom may block. Have fallback strategies for non-cryptographic randomization.

## Shuffle Timing Attacks

If shuffle timing depends on random values, attackers may infer bits of randomness through timing observations. Use constant-time implementations when randomness is secret.

## Cryptographic vs Statistical Randomness

Fisher-Yates with java.util.Random is fine for games but insecure for cryptographic key generation. Use SecureRandom for any security-sensitive shuffling.

## Input Validation

Reservoir sampling with k > stream length: handle gracefully. Quickselect with k out of range: check bounds.
