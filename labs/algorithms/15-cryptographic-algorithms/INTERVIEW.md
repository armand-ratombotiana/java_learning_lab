# Interview Questions: Cryptographic Algorithms

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| LC 535 Encode and Decode TinyURL | Medium | Amazon, Microsoft | Hash mapping |
| LC 729 My Calendar I | Medium | Google | BST / TreeMap |
| LC 1299 Replace Elements | Easy | Apple | Array scanning |

## NeetCode Reference
Not covered in NeetCode 150. Relevant to system design and security rounds.

## Company-Specific Questions
### Google
- Design a secure hash function for distributed storage
- How would you implement a cryptographic signing scheme for internal APIs?
- Explain TLS 1.3 handshake and its performance benefits

### Microsoft
- How does Azure Key Vault manage encryption keys?
- Design a secure file encryption system using AES-GCM
- Explain certificate chain validation in Windows Update

### Meta
- How would you encrypt messages in Messenger end-to-end?
- Design a secure content-addressed storage for encrypted photos
- Key rotation strategies for large-scale systems

### Amazon
- How does AWS KMS handle envelope encryption?
- Design a secure checkout system with PCI compliance
- Explain S3 server-side encryption options (SSE-S3, SSE-KMS, SSE-C)

### Apple
- How does iOS Data Protection encrypt files at rest?
- Design a secure enclave for biometric authentication
- Explain Apple's approach to privacy-preserving cryptography

### Oracle
- How does Oracle Transparent Data Encryption (TDE) work?
- Design a database encryption scheme with row-level security
- Explain Oracle Key Vault integration patterns

## Real Production Scenarios
- Scenario 1: CDN signed URL generation - using HMAC-SHA256 to generate time-limited access tokens for private content delivery
- Scenario 2: Database column encryption - implementing deterministic encryption for searchable encrypted columns while maintaining security guarantees
- Scenario 3: TLS termination debugging - diagnosing SSL handshake failures, cipher suite negotiation, and certificate chain validation

## Interview Tips
- Understand symmetric vs asymmetric encryption trade-offs (AES-256 vs RSA-4096)
- Know hash properties: preimage resistance, collision resistance, avalanche effect
- Be prepared to discuss perfect forward secrecy and why it matters
- Common edge cases: short message padding, IV reuse catastrophes, timing side-channels

## Java-Specific Considerations
- `javax.crypto.Cipher` for AES/RSA operations; `javax.crypto.Mac` for HMAC
- `MessageDigest` for SHA-256/512 hashing; `SecureRandom` for cryptographically secure randomness
- Pitfall: using `String.getBytes()` without specifying charset for crypto operations
- Pitfall: hardcoding keys or seeds in source code
- Java `KeyStore` (JKS/PKCS12) for certificate and key management
- Avoid ECB mode (insecure); prefer GCM or CBC with IV
