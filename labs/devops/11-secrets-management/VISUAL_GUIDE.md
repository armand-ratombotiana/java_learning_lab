# Visual Guide to Secrets Management

## Vault Lifecycle
```
┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐
│  Vault   │   │  Unseal  │   │  Auth    │   │  Access  │
│  Start   │──▶│  (K of N)│──▶│  Method  │──▶│  Secrets │
│  (sealed)│   │          │   │          │   │          │
└──────────┘   └──────────┘   └──────────┘   └──────────┘
```

## Seal/Unseal Process
```
Vault starts SEALED (encrypted master key locked)
    │
    │ Enter unseal key 1/5 (partial unseal)
    ▼
    │ Enter unseal key 2/5
    ▼
    │ Enter unseal key 3/5 (threshold reached!)
    ▼
Vault UNSEALED (master key decrypted, barrier open)
```

## Dynamic Secret Flow
```
Application           Vault            Database
    │                   │                  │
    │── Auth ──────────▶│                  │
    │◀─ Token ─────────│                  │
    │── Request DB ────▶│                  │
    │   creds           │── CREATE USER ──▶│
    │◀─ user: app_xyz  │◀─ OK ────────────│
    │   pass: aB3$...  │                  │
    │   lease: 24h     │                  │
    │                   │                  │
    │══ Use creds ════════════════════════▶│
    │                   │                  │
    │── Renew lease ───▶│                  │
    │◀─ OK (extend 24h)│                  │
    │                   │                  │
    │ (24h passes,      │                  │
    │  no renewal)      │                  │
    │                   │── DROP USER ────▶│
    │                   │                  │
```

## Vault Integration with K8s
```
Pod (with SA token) → Vault Agent Sidecar → Vault Server
                          │
                    ┌─────┴─────┐
                    │ Secret    │
                    │ written   │
                    │ to file   │
                    │ /vault/secrets
                    └───────────┘
```
