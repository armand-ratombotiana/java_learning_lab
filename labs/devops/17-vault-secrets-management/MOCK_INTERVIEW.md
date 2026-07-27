# Vault Secrets Management MOCK_INTERVIEW.md

## Scenario 1: Vault HA Configuration
You need to deploy Vault in production with high availability.

**Questions**:
1. How does Vault HA work?
2. Compare integrated storage (Raft) vs Consul storage.
3. How do you handle Vault scaling?
4. How do you perform Vault upgrades?

**Expected approach**: Vault HA with integrated Raft storage (simpler, embedded). Consul storage for large deployments with existing Consul. Scaling: add more Vault nodes, performance standby replicas for read scaling. Upgrades: rolling restart, `vault operator migrate` if needed.

## Scenario 2: Vault Auto-Unseal
You need to automate Vault unsealing for HA.

**Questions**:
1. Why is Vault sealed on startup?
2. How does auto-unseal work?
3. What auto-unseal methods are available?
4. How do you recover if the auto-unseal key is lost?

**Expected approach**: Vault seals on startup to protect encryption key. Auto-unseal: Vault uses cloud KMS (AWS KMS, GCP Cloud KMS, Azure Key Vault) to wrap the unseal key. Recovery: KMS key has its own recovery process, or use Shamir backup with threshold.

## Scenario 3: Vault Policy Design
You need to design Vault policies for multiple teams.

**Questions**:
1. How does Vault's policy model work?
2. How do you implement least privilege?
3. How do you audit policy compliance?
4. How do you design multi-tenant policies?

**Expected approach**: Policy path-based: `path "secret/data/team-a/*"`. Least privilege: explicit allow, deny by default, no wildcards in production. Audit via Vault audit logs. Multi-tenant: namespaces (Enterprise), `path "secret/team-a/..."` vs `path "secret/team-b/..."`. Read-only vs read-write policies.

## Scenario 4: Dynamic Database Credentials
Your PostgreSQL database needs dynamic, short-lived credentials.

**Questions**:
1. How do you configure the database secrets engine?
2. How does credential rotation work?
3. How do apps get credentials?
4. How do you handle connection pooling?

**Expected approach**: Configure `database/postgres` engine with connection string, default TTL (1h), max TTL (24h). Apps authenticate to Vault, request creds, use for TTL. Vault Agent sidecar automates fetching and renewing. Connection pooling: set TTL longer than pool idle timeout, or use Vault Agent's caching.

## Scenario 5: Vault PKI
You need to automate TLS certificate issuance for 500+ services.

**Questions**:
1. How do you configure the PKI secrets engine?
2. How do you set up intermediate CAs?
3. How do you issue short-lived certificates?
4. How do you handle certificate revocation?

**Expected approach**: Root CA (offline), intermediate CAs per cluster. Short-lived certs (24-48h TTL) reduce revocation complexity. Issue via Vault PKI role. Automate renewal with cert-manager or Vault Agent. Revocation: CRL distribution points, OCSP responders.

## Key Vault Interview Questions
1. Explain Vault's encryption mechanism (barrier, seal, unseal).
2. How does Vault's storage backend affect HA?
3. What's the difference between KV v1 and v2?
4. Explain Vault's replication modes.
5. How does Vault integrate with Kubernetes?
6. What's the Vault Agent Injector?
7. Explain Vault's dynamic secrets vs static secrets.
8. How does Vault handle secret rotation?
9. What's the Vault PKI engine and how does it work?
10. How does Vault audit logging work?

## Whiteboard Challenge
Design a Vault architecture for a multi-region, multi-company platform with:
- 3 regions, each with a Vault cluster
- Dynamic DB credentials for PostgreSQL
- PKI for 500+ services
- Kubernetes auth for workload identity
- Audit logging and compliance

## Follow-up
1. How would you handle Vault disaster recovery?
2. How would you migrate secrets between Vault clusters?
3. How would you integrate Vault with CI/CD pipelines?