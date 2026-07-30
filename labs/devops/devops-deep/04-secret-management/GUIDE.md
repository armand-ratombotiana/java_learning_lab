# Secret Management — Step-by-Step Guide

## 1. HashiCorp Vault
- Secret Engines: KV v2, AWS, Database dynamic secrets.
- Auth Methods: Token, Kubernetes, LDAP, AppRole.
- Leasing: secrets have TTLs; auto-renew or re-read.

## 2. Kubernetes Secrets
- Base64 encoded (not encrypted) by default.
- Enable encryption at rest: `--encryption-provider-config`.
- Better: use External Secrets Operator or Sealed Secrets.

## 3. External Secrets Operator
- `SecretStore` → `ExternalSecret` → `Kubernetes Secret`.
- Syncs from Vault, AWS Secrets Manager, GCP Secret Manager.

## 4. Encryption in Transit
- TLS/mTLS between services.
- Vault PKI can issue short-lived certs.

## 5. Secret Rotation
- Database passwords: Vault can generate dynamic creds per lease.
- Rotation: update secret → notify consumers → verify → expire old.

## Build & Run
```bash
javac --enable-preview -source 21 -d out src/com/devops/deep/lab04/*.java
java --enable-preview -cp out com.devops.deep.lab04.SecretManagementLab
```
