# Common Secrets Management Mistakes

1. **Running Vault in dev mode in production** — dev mode has no TLS, static root token.
2. **Not enabling audit logging** — can't investigate who accessed which secret.
3. **Overly permissive policies** — `*` capabilities on `secret/*` is dangerous.
4. **Ignoring lease renewal** — dynamic credentials expire, causing app failures.
5. **Using static secrets when dynamic are possible** — longer blast radius.
6. **Storing secrets in version control** — even encrypted, secrets in git leave trace.
7. **No secret rotation** — static credentials unchanged for years.
8. **Single unseal key holder** — risk of losing access.
9. **No backup of Vault storage** — losing storage backend loses all secrets.
10. **Vault as a single point of failure** — deploy HA with Raft/Consul.
11. **Storing Vault tokens in environment variables** — use Vault Agent or sidecar injection.
12. **Hardcoding Vault address/token in application code** — use environment-specific config.
