# Secrets Management Architecture

## Vault Architecture (HA with Raft)
```
┌──────────────────────────────────────────────────────────────┐
│                     Vault Cluster (3 nodes)                   │
│                                                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   Vault A    │  │   Vault B    │  │   Vault C    │      │
│  │  (Active)    │◀─│ (Standby)    │◀─│ (Standby)    │      │
│  │              │──▶              │──▶              │      │
│  │ Raft Node    │  │ Raft Node    │  │ Raft Node    │      │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘      │
│         │                 │                 │                │
│         └─────────────────┼─────────────────┘                │
│                           │ (Raft consensus)                 │
└───────────────────────────┼──────────────────────────────────┘
                            │
┌───────────────────────────┼──────────────────────────────────┐
│                    Applications                               │
│  ┌──────────┐  ┌──────────┴──────────┐  ┌──────────┐       │
│  │  App A   │  │    App B            │  │   App C  │       │
│  │(Vault    │  │   (Vault Agent      │  │  (Direct │       │
│  │ Agent)   │  │    Sidecar in K8s)  │  │   API)   │       │
│  └──────────┘  └─────────────────────┘  └──────────┘       │
└──────────────────────────────────────────────────────────────┘
```

## Components
- **Vault Server**: Core secrets engine (HA with Raft or Consul).
- **Storage Backend**: Raft (integrated), Consul, or cloud storage.
- **Seal Mechanism**: Shamir or Auto-unseal (KMS).
- **Auth Methods**: Token, Kubernetes, AWS, OIDC, LDAP.
- **Secret Engines**: KV, Database, AWS, PKI, Transit, etc.
- **Audit Devices**: File, Syslog, Socket.
- **Vault Agent**: Sidecar for automatic authentication and secret injection.
