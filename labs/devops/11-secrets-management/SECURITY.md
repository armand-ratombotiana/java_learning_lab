# Secrets Management Security

## Vault Security Architecture
- **Barrier encryption**: All data encrypted in storage with AES-256-GCM.
- **Seal/Unseal**: Master key split via Shamir's Secret Sharing (no single point of failure).
- **Auto-unseal**: Cloud KMS (AWS KMS, GCP Cloud KMS, Azure Key Vault) for automatic unseal.
- **Response wrapping**: Secure token distribution (wrap token for one-time use).

## Access Control
- **Policies**: ACL-based path patterns with explicit allow/deny.
- **Identities**: Entity (human) and Entity Group with attached policies.
- **Control groups**: Require multiple approvals for sensitive operations.
- **Namespaces**: Tenancy isolation (Vault Enterprise).

## Network Security
- **TLS**: Required for all client-Vault and Vault-Vault communication.
- **Firewalls**: Restrict Vault access to authorized clients only.
- **Network policies**: Only necessary pods/apps can reach Vault.
- **Vault proxy**: Sidecar/Vault Agent for local secret retrieval without network exposure.

## Dynamic Secrets Security
- **Unique credentials per request**: Every credential request generates new credentials.
- **Automatic revocation**: Credentials expire and removed automatically.
- **Limited permissions**: Dynamic credentials have minimum required database/API permissions.
- **Credential reuse prevention**: Never reuse credentials across applications.

## Compliance
- **Audit logging**: Every request logged (client, path, operation, timestamp).
- **Audit device types**: File (local), Syslog, Socket (remote logging).
- **Integrity**: Audit logs HMAC-signed for tamper detection.
- **Retention**: Store audit logs per compliance requirements (typically 1-7 years).
