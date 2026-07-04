# How Secrets Management Works

## Vault Architecture
```
┌─────────────────────────────────────────────────────────────┐
│                       Vault Server                           │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │ Auth     │  │ Secret   │  │ Audit    │  │ Storage  │  │
│  │ Methods  │──│ Engines  │──│ Devices  │──│ Backend  │  │
│  │ (K8s,    │  │ (KV, DB, │  │ (File,   │  │ (Raft,   │  │
│  │  Token,  │  │  AWS, PKI)│  │  Syslog) │  │  Consul) │  │
│  │  OIDC)   │  └──────────┘  └──────────┘  └──────────┘  │
│  └──────────┘                                              │
└───────────────────────────────┬─────────────────────────────┘
                                │
        ┌───────────────────────┼───────────────────────┐
        │                       │                       │
  ┌─────▼─────┐          ┌──────▼──────┐          ┌─────▼─────┐
  │  App 1    │          │  App 2      │          │  CI/CD    │
  │ (K8s SA)  │          │ (AWS EC2)   │          │ (Jenkins) │
  └───────────┘          └─────────────┘          └───────────┘
```

## Secret Lifecycle
```
1. Vault Server starts (sealed)
2. Unseal with threshold of keys (unsealed)
3. App authenticates (Kubernetes, token, AWS IAM)
4. Vault verifies identity against auth method
5. App requests secret at path
6. Vault checks policy → allows/denies
7. App receives secret + lease (TTL)
8. App renews lease if needed
9. Secret expires or is revoked
```

## Dynamic Database Credentials
```
1. Vault connects to PostgreSQL with admin credentials
2. App requests DB credentials
3. Vault creates temporary DB user with limited permissions
4. App uses credentials for 24h (or configurable)
5. Vault revokes DB user on lease expiry
```
