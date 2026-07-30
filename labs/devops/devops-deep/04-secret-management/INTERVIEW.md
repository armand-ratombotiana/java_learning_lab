# Interview Questions — Secret Management

## Q1: What is the difference between static and dynamic secrets?
**A:** Static secrets (API keys, passwords) are long-lived and manually rotated. Dynamic secrets (Vault DB creds) are generated on-demand with TTLs and automatically expire.

## Q2: How does the External Secrets Operator work?
**A:** It defines a `SecretStore` (connection to Vault/AWS), then `ExternalSecret` resources map external secret paths to Kubernetes Secret fields. The operator syncs changes automatically.

## Q3: Are Kubernetes Secrets secure by default?
**A:** No. Secrets are base64 encoded but not encrypted in etcd unless encryption at rest is configured. RBAC limits access but doesn't protect the data at rest.

## Q4: How would you rotate a database password with zero downtime?
**A:** Use dual-credential rotation: update app to use new password while old one still works, then revoke the old credential after all connections have migrated.
