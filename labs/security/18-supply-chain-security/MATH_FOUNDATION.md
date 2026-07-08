# Math Foundation: 18-supply-chain-security

## Mathematics of Supply Chain Security

### Dependency Graph Analysis

**Transitive Dependency Count**:
- Direct dependencies: D1
- Total transitive: D_total = S(D_children_of_i)
- Explosion factor: E = D_total / D1 (typically 5-20x)

### Vulnerability Propagation

- P(vuln in dependency) = 1 - ?(1 - p?)
- p? = vulnerability probability per component
- For 100 components at p=0.01: P = 63.4%

### SBOM Size Estimation

- CycloneDX JSON: ~200-400 bytes per component
- SPDX tag-value: ~150-300 bytes per component
- Typical Java project: 100-500 components ? ~50-200KB SBOM

### Sigstore Verification

Fulcio short-lived certificate validity:
- TTL: typically 10 minutes
- Rekor entry: permanent append-only log
- Verification: certificate chain + log inclusion proof

### SLSA Level Requirements

- SLSA 1: Provenance exists
- SLSA 2: Provenance signed + hosted
- SLSA 3: Hardened builds (no user-controlled CI)
- SLSA 4: Hermetic + reproducible + dependencies verified

### Dependency Score

- Criticality score: based on downstream dependents
- Popularity: downloads per period
- Maintenance: time since last update
- Security: known vulnerabilities, fix velocity

### Build Reproducibility

- Identical inputs ? identical outputs
- Timestamps, file ordering, randomness must be deterministic
- Bit-for-bit identical: SHA-256 hash matches exactly

### Cryptographic Hash Functions

Hash functions are fundamental to security protocols:
- **SHA-256**: 256-bit output, 64-bit blocks, 64 rounds
- **SHA-3**: Sponge construction, arbitrary output length
- **BLAKE2**: Faster alternative to SHA-3 with comparable security

### Random Number Generation

Secure random numbers require:
- Entropy source: OS-provided (SecureRandom in Java)
- Minimum 128 bits for challenge values
- 256 bits recommended for key material
- Never use java.util.Random for security

### Key Derivation Functions

KDFs stretch passwords into cryptographic keys:
- PBKDF2: Iterated HMAC, configurable work factor
- bcrypt: Blowfish-based, adaptive cost
- scrypt: Memory-hard, resists ASIC attacks
- Argon2: Modern, winner of PHC competition

### Timing Attacks Prevention

Constant-time operations prevent side-channel attacks:
- XOR operations instead of branching
- Fixed-time memory access patterns
- MessageDigest.isEqual() for hash comparison
- Avoid short-circuit boolean evaluation

### Encoding Overhead

Base64 encoding increases size by exactly 33%:
- 3 bytes ? 4 characters
- Padding: 0-2 '=' characters
- Used in: SAML assertions, JWT tokens, certificates

### Use Case: This Lab

For this specific lab's mathematical requirements:
- [Specific math topic 1]: Applied in [context]
- [Specific math topic 2]: Applied in [context]
- [Specific math topic 3]: Applied in [context]
- [Specific math topic 4]: Applied in [context]
- [Specific math topic 5]: Applied in [context]

### Further Reading

- Handbook of Applied Cryptography (Menezes, van Oorschot, Vanstone)
- Cryptography Engineering (Ferguson, Schneier, Kohno)
- NIST SP 800-57: Recommendations for Key Management
- NIST SP 800-107: Recommendation for Applications Using Hash Functions
- RFC 8017: PKCS #1 v2.2 RSA Cryptography Standard
