# Secrets Management MOCK_INTERVIEW.md

## Scenario 1: Secrets Leak
A developer accidentally committed a production database password to a public GitHub repo.

**Questions**:
1. What's your immediate response?
2. How do you rotate credentials across all services?
3. How do you prevent this in the future?
4. How would you audit for leaked secrets?

**Expected approach**: Immediate: revoke compromised credentials, rotate DB password, check CloudTrail/Git history for access. Prevention: pre-commit hooks (truffleHog, Gitleaks), secret scanning in CI, secrets management tool (Vault), environment variables, no secrets in git. Audit: GitHub secret scanning, periodic scans.

## Scenario 2: Vault Architecture
Design a HashiCorp Vault architecture for a multi-region, multi-team environment.

**Questions**:
1. How do you deploy Vault for high availability?
2. How do you handle Vault unsealing?
3. How do you manage multi-tenancy?
4. How do you back up Vault?

**Expected approach**: Vault HA with Consul or integrated storage. Auto unseal via cloud KMS (AWS KMS, GCP Cloud KMS, Azure Key Vault). Multi-tenancy via namespaces (Enterprise) or separate mount paths per team. Backup: snapshots via `vault operator raft snapshot save`, stored in S3.

## Scenario 3: Dynamic Secrets
Your database credentials are static and never rotate. Security audit requires dynamic credentials.

**Questions**:
1. How would Vault generate dynamic DB credentials?
2. What's the lifecycle of a dynamic secret?
3. How do applications authenticate to Vault?
4. How do you handle connection pooling with dynamic secrets?

**Expected approach**: Vault database secrets engine creates ephemeral PostgreSQL/MySQL credentials with TTL. Apps authenticate via Kubernetes auth method (ServiceAccount), Vault Agent injects sidecar, refreshes before TTL. Connection pooling: use Vault Agent's secret caching, or configure TTL > pool idle timeout.

## Scenario 4: Kubernetes Secrets Strategy
Your team needs to manage secrets for 50+ microservices on Kubernetes.

**Questions**:
1. Compare native K8s Secrets vs External Secrets Operator vs Sealed Secrets.
2. How do you rotate secrets without restarting pods?
3. How do you audit secret access?
4. How do you handle multi-cluster secrets?

**Expected approach**: Native Secrets: base64 only, not encrypted. External Secrets Operator: sync from Vault/AWS SM/GCPSM to K8s Secrets. Sealed Secrets: encrypt in Git, decrypt in cluster. Rotation: External Secrets refreshes automatically, CSI Secrets Store driver mounts secrets as volumes. Audit: K8s audit logs, Vault audit logs.

## Scenario 5: PKI and Certificate Management
You need to issue and manage TLS certificates for 1000+ services.

**Questions**:
1. How would you automate certificate issuance?
2. How does Vault PKI work?
3. How do you handle certificate rotation?
4. How do you monitor certificate expiration?

**Expected approach**: Vault PKI secrets engine — generate intermediate CA per cluster, issue short-lived certificates (24h-72h). cert-manager on Kubernetes for automating certificate requests. Rotation via cert-manager renewal. Monitoring: Prometheus Blackbox exporter, certificate expiry alerts, cert-manager metrics.

## Key Secrets Management Interview Questions
1. What's the difference between static and dynamic secrets?
2. How does Vault secure the encryption key (seal/unseal)?
3. Explain Vault's secret engines and auth methods.
4. How does Vault's replication work (DR vs Performance)?
5. What's the difference between encryption in transit and at rest?
6. How do you handle secrets in CI/CD pipelines?
7. Explain the concept of a secrets zero-trust model.
8. How do you audit secrets access?
9. What's the difference between Vault and AWS Secrets Manager?
10. How does SPIFFE/SPIRE work for workload identity?

## Whiteboard Challenge
Design a secrets management architecture for a multi-cloud (AWS + GCP), multi-cluster (K8s) platform with 200+ services. Consider dynamic secrets, PKI, audit, and compliance.

## Follow-up
1. How would you handle secrets rotation during an incident?
2. How would you implement secrets for serverless (Lambda)?
3. How would you handle secrets for non-K8s workloads?