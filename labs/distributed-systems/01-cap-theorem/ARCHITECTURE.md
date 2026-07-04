# CAP Theorem: Architecture Patterns

## CP Architecture

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Client    │────▶│   Proxy     │────▶│  ZooKeeper  │
└─────────────┘     │   (Leader)  │     │  (Ensemble) │
                    └─────────────┘     └─────────────┘
                           │
              ┌────────────┼────────────┐
              ▼            ▼            ▼
        ┌──────────┐ ┌──────────┐ ┌──────────┐
        │  Node 1  │ │  Node 2  │ │  Node 3  │
        │ (Leader) │ │(Follower)│ │(Follower)│
        └──────────┘ └──────────┘ └──────────┘
              │            │            │
              └────────────┼────────────┘
                           │
                    [Shared Log]
```

## AP Architecture

```
Client ──▶ Cassandra Cluster
              │
     ┌────────┼────────┐
     │        │        │
  ┌──▼──┐ ┌──▼──┐ ┌──▼──┐
  │N1   │ │N2   │ │N3   │
  │     │◀────▶│     │◀────▶│     │
  └─────┘ └─────┘ └─────┘
  [Gossip protocol between all nodes]
```

## Hybrid Architecture
```java
public class HybridService {
    // CP for critical operations
    private final CPBankingService banking;
    // AP for non-critical operations
    private final APNotificationService notifications;
}
```
