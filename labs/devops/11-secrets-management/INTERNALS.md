# Secrets Management Internals

## Vault Encryption
- **Master Key**: Encrypts all other keys (split via Shamir's Secret Sharing).
- **Unseal keys**: N of M shards required to reconstruct master key.
- **Barrier encryption**: AES-256-GCM for storage backend.
- **Transit engine**: Encryption-as-a-service without storing data (application sends plaintext, receives ciphertext).

## Storage Backends
- **Raft** (integrated): Built-in distributed storage; no external dependency.
- **Consul**: Strong consistency, service discovery (traditional deployment).
- **File**: Local file storage for dev/test.
- **Cloud**: S3, GCS, Azure Storage (with HA via DynamoDB/Consul).

## Auth Methods
- **Token**: Default; static or periodic tokens.
- **Kubernetes**: Validate service account JWT against K8s API.
- **AWS IAM**: Verify EC2 instance identity document or IAM principal.
- **OIDC**: Single sign-on with Okta, Azure AD, Google, etc.
- **LDAP**: Microsoft AD / OpenLDAP integration.
- **JWT/OIDC**: External identity provider integration.

## Secret Engines
- **KV v2**: Key-value store with versioning, delete protection.
- **Database**: Dynamic credentials for PostgreSQL, MySQL, MongoDB, MSSQL, Oracle.
- **AWS**: Dynamic IAM credentials (access key + secret).
- **PKI**: Dynamic X.509 certificates (internal CA).
- **Transit**: Encryption-as-a-service (encrypt/decrypt/sign/verify).
- **Consul**: Dynamic Consul API tokens.
