# Failure Detection: Architecture

## Gossip-Based Detection Architecture

```
        ┌─────────────────────────┐
        │   Membership Service     │
        │                         │
        │  ┌───┐  ┌───┐  ┌───┐  │
        │  │ A │◀─▶│ B │◀─▶│ C │  │
        │  └─┬─┘  └─┬─┘  └─┬─┘  │
        │    │      │      │     │
        │  ┌─▼─┐  ┌─▼─┐  ┌─▼─┐ │
        │  │ D │◀─▶│ E │◀─▶│ F │ │
        │  └───┘  └───┘  └───┘  │
        └─────────────────────────┘
```

## Integration with Cluster Management

```
Failure Detector → Membership List → Topology Service
       │                                  │
       │                                  ├── Load Balancer
       │                                  │   (remove failed nodes)
       │                                  │
       │                                  ├── Replication Manager
       │                                  │   (trigger repair)
       │                                  │
       │                                  └── Consensus Module
       │                                      (trigger election)
       │
       └── Health Check API → Monitoring
```

## Layered Detection

```
L1: Local Heartbeat (Direct pings)
L2: Gossip Protocol (Cluster-wide propagation)
L3: Application Health Check (Business logic)
L4: External Monitoring (Cloud provider health checks)
```
