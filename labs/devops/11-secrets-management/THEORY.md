# Secrets Management Theory

## Core Concepts
- **Secrets**: Sensitive data (passwords, API keys, certificates, tokens, SSH keys).
- **Encryption at rest**: Secrets encrypted when stored (AES-256, GCM).
- **Encryption in transit**: Secrets encrypted over network (TLS/mTLS).
- **Access control**: Who can read/create/rotate secrets.
- **Audit logging**: Every secret access recorded.
- **Rotation**: Periodically changing secret values.
- **Dynamic secrets**: Short-lived, generated on-demand (not static).

## HashiCorp Vault
- **Seal/Unseal**: Vault starts sealed; unseal keys needed to decrypt master key.
- **Secret Engine**: Plugin that stores/generates secrets (KV, database, AWS, PKI).
- **Auth Method**: How clients authenticate (token, Kubernetes, AWS IAM, OIDC, LDAP).
- **Policy**: ACL rules defining access to paths.
- **Path**: Hierarchical namespace for secrets (e.g., `secret/data/myapp`).
- **Lease**: TTL for dynamic secrets (auto-renew, expire, revoke).

## Secrets Management Principles
- **Never hardcode secrets**: Not in code, config files, or environment variables.
- **Least privilege**: Applications get only secrets they need.
- **Short-lived credentials**: Dynamic secrets with automatic rotation.
- **Centralized storage**: Single source of truth for all secrets.
- **Audit trail**: Every secret access logged for compliance.
- **Rotation**: Automatic secret rotation without downtime.
