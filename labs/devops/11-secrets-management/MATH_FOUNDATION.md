# Math Foundation for Secrets Management

## Shamir's Secret Sharing
- **Polynomial interpolation**: Split secret S into N shares; any K (threshold) shares reconstruct S.
- **f(x) = S + a₁x + a₂x² + ... + aₖ₋₁xᵏ⁻¹**: Polynomial of degree K-1.
- **Shares**: Points (x, f(x)) on the polynomial.
- **Security**: Knowledge of K-1 shares provides zero information about S.

## Encryption
- **AES-256-GCM**: Symmetric encryption (256-bit key) with authenticated encryption (Galois/Counter Mode).
- **TLS**: Asymmetric (RSA/ECDSA) key exchange + symmetric session encryption.
- **Argon2**: Password hashing (key derivation) for token/identity verification.

## Lease Management
- **TTL (Time To Live)**: Maximum duration a secret is valid.
- **Renewal**: Client extends lease before expiration.
- **Grace period**: Time after expiry before revocation.
- **Max TTL**: Hard limit even with renewals.

## Token Expiry Math
- **Periodic tokens**: Fixed TTL; must be renewed before expiry.
- **Batch tokens**: Stateless, one-time use, no TTL management.
- **Orphan tokens**: Auth method deleted; tokens still valid.

## Certificate Validity
- **CA certificate**: Typically 10 years (Root), 1-5 years (Intermediate).
- **Leaf certificates**: Days to months (short-lived certs for mTLS).
- **Revocation lists**: CRL or OCSP for certificate revocation.
