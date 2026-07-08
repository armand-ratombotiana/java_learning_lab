# Network Security - Why It Exists

## The Problem

Communication over the internet is inherently insecure:
1. **Eavesdropping**: Anyone on the network can see data
2. **Tampering**: Data can be modified in transit
3. **Impersonation**: Attackers can pretend to be legitimate servers
4. **Replay attacks**: Captured messages can be replayed

## Security Mechanisms
| Mechanism | Protection | Technology |
|-----------|-----------|------------|
| Encryption | Confidentiality | AES, ChaCha20 |
| Authentication | Identity verification | Certificates, JWT |
| Integrity | No tampering | HMAC, Digital signatures |
| Authorization | Access control | OAuth2, RBAC |

## Why mTLS?
In microservice environments, mTLS ensures:
- Every service knows which services it's talking to
- No unauthenticated requests reach services
- Encryption is automatic (no configuration errors)
- Service mesh (Istio, Linkerd) provides mTLS transparently
