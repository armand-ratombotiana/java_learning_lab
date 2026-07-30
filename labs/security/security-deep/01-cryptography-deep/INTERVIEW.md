# Interview: Cryptography Deep

## Q1: Conceptual Understanding
**Q**: Why should ECB mode never be used?
**A**: ECB encrypts each block independently, so identical plaintext blocks produce identical ciphertext blocks. Patterns in the plaintext (like an image silhouette) remain visible in the ciphertext.

## Q2: Implementation
**Q**: How does RSA-OAEP padding improve security over textbook RSA?
**A**: OAEP adds randomness and redundancy before encryption. This prevents chosen-plaintext attacks, makes the scheme semantically secure, and provides IND-CCA2 security.

## Q3: System Design
**Q**: Design a secure messaging system using hybrid encryption.
**A**: Use ECDH for key exchange to derive a shared secret, then AES-GCM for encrypting messages (symmetric is faster). Include signatures (Ed25519) for authentication and nonce counters for replay protection.

## Coding Challenge
Implement AES-GCM encryption and decryption with a random IV and AAD (additional authenticated data).
