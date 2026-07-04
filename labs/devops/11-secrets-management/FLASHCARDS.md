# Secrets Management Flashcards

**Q: What is Vault?**
A: HashiCorp tool for secrets management (CNCF project).

**Q: What is a dynamic secret?**
A: Short-lived credential generated on-demand (e.g., temporary DB user).

**Q: What is a static secret?**
A: Pre-defined, long-lived secret (password, API key) stored in vault.

**Q: What is Shamir's Secret Sharing?**
A: Splits a secret into N shares; K required to reconstruct.

**Q: What is sealing in Vault?**
A: Vault locks its encryption key; needs unseal to operate.

**Q: What is a policy in Vault?**
A: ACL rules defining access to secret paths (paths + capabilities).

**Q: What is a lease in Vault?**
A: TTL-based validity period for dynamic secrets and tokens.

**Q: What is Vault Agent?**
A: Sidecar that handles Vault authentication and secret injection.

**Q: What is a secret engine?**
A: Plugin that stores or generates secrets (KV, database, AWS, PKI).

**Q: What is audit logging in Vault?**
A: Records all authenticated requests (client, path, operation, time).
