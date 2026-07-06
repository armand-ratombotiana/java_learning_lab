# Visual Guide — Java Security (Lab 33)

## Symmetric Encryption / Decryption Flow

```
     Plaintext                         Plaintext
    "Hello World"                     "Hello World"
         │                                  ▲
         │                                  │
         ▼                                  │
   ┌──────────┐                       ┌──────────┐
   │   AES    │                       │   AES    │
   │ Encrypt  │                       │ Decrypt  │
   └────┬─────┘                       └────┬─────┘
        │                                  │
   Key: "secret123"                 Key: "secret123"
        │                                  │
        ▼                                  │
   ┌──────────┐                            │
   │ Cipher-  │                            │
   │ text     │ ──── (insecure channel) ───┘
   │ xA4Bc... │
   └──────────┘
```

- **Symmetric** (AES) — same key encrypts and decrypts. Fast, but key distribution is a problem.
- **Asymmetric** (RSA) — public key encrypts, private key decrypts. Slower, but solves key exchange.

## Digital Signature Process

```
      ┌──────────┐
      │ Document │
      └────┬─────┘
           │
           ▼  Hash (SHA-256)
      ┌──────────┐
      │ Digest   │  →  "a7b2c9..."
      └────┬─────┘
           │
           ▼  Encrypt with Sender's Private Key
      ┌──────────┐
      │ Signature│
      └────┬─────┘
           │
    ┌──────┴──────┐
    │ Send Doc +  │
    │ Signature   │────────► Receiver
    └─────────────┘
           │
           ▼
      Verify with Sender's Public Key:
      Decrypt signature → digest1
      Hash document     → digest2
      Compare digest1 == digest2 ? "Valid" : "Tampered"
```

- Provides **authenticity** (proves sender identity) and **integrity** (proves document wasn't altered).
- Used in JWT signing, SSL/TLS certificates, and code signing.
- Java provides `java.security.Signature`, `KeyPairGenerator`, and `CertificateFactory` for these operations.
