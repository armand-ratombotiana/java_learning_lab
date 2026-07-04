# Service Mesh Security

## mTLS Configuration
- **Mode**: `PERMISSIVE` (allow both mTLS and plaintext) → `STRICT` (mTLS required).
- **Certificate rotation**: Default 24h; Citadel auto-rotates.
- **Custom CA**: Integrate with external CA (cert-manager, Vault).
- **SPIFFE identities**: Workload identity based on namespace + service account.

## Authorization Policies
- **Default deny**: Block all traffic; explicitly allow required communication.
- **Principle of least privilege**: Minimal needed access between services.
- **JWT validation**: End-user authentication with `RequestAuthentication`.
- **Conditional rules**: Allow based on source, paths, methods, headers.

## Network Security
- **Egress gateway**: Route external traffic through controlled gateway.
- **Ingress gateway**: TLS termination, rate limiting, WAF integration.
- **Network policies**: Combine Istio AuthorizationPolicy with K8s NetworkPolicy.

## Common Security Pitfalls
- Leaving mTLS in `PERMISSIVE` mode indefinitely (no real security).
- Overly broad authorization rules (allow all GET requests).
- Not rotating certificates; relying on defaults.
- Exposing internal services through ingress without authentication.
- No audit logging for security-sensitive operations.
