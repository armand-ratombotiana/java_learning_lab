# Consistency Models - ARCHITECTURE

## System Architecture with Multiple Consistency Levels

```
┌────────────────────────────────────────────────────────────┐
│                   Application Layer                         │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │ Auth     │  │ Product  │  │ Order    │  │ Analytics│  │
│  │ Service  │  │ Service  │  │ Service  │  │ Service  │  │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘  │
└───────┼──────────────┼─────────────┼──────────────┼────────┘
        │              │             │              │
┌───────▼──────────────▼─────────────▼──────────────▼────────┐
│                    Consistency Layer                         │
│                                                             │
│  ┌──────────────────┐  ┌──────────────────┐                │
│  │ Strong           │  │ Causal           │                │
│  │ (Raft/Spanner)   │  │ (Vector Clocks)  │                │
│  ├──────────────────┤  ├──────────────────┤                │
│  │ Payments         │  │ Comments         │                │
│  │ Orders           │  │ Replies          │                │
│  │ Inventory        │  │ Activity Feed    │                │
│  └──────────────────┘  └──────────────────┘                │
│                                                             │
│  ┌──────────────────┐  ┌──────────────────┐                │
│  │ Read-Your-Writes  │  │ Eventual         │                │
│  │ (Session Tokens)   │  │ (Gossip/CRDT)   │                │
│  ├──────────────────┤  ├──────────────────┤                │
│  │ User Session     │  │ Search Index     │                │
│  │ Cart Preferences │  │ Recommendations  │                │
│  └──────────────────┘  └──────────────────┘                │
└────────────────────────────────────────────────────────────┘
        │              │             │              │
┌───────▼──────────────▼─────────────▼──────────────▼────────┐
│                   Data Layer                                 │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │PostgreSQL│  │ MongoDB  │  │ Cassandra│  │ Elastic  │  │
│  │(Sync Repl)│  │(Causal)  │  │(Tunable) │  │(Async)   │  │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘  │
└────────────────────────────────────────────────────────────┘
```

## Replication Topology

### Per-Data Consistency Strategy

| Data | Model | DB | Reason |
|------|-------|-----|--------|
| Orders | Strong | PostgreSQL sync | Financial accuracy |
| Products | Read-Your-Writes | Redis + PostgreSQL | Inventory visibility |
| Comments | Causal | MongoDB | Thread ordering |
| Search Index | Eventual | Elasticsearch | Performance |
| User Sessions | Strong within session | Redis cluster | Session integrity |
| Analytics | Eventual | Cassandra | Write throughput |

## Cross-Region Consistency

```
Region 1 (Primary)          Region 2 (Secondary)
┌───────────────┐          ┌───────────────┐
│ Orders:       │    Raft   │ Orders:       │
│ Leader        │◄──────────│ Follower      │
├───────────────┤          ├───────────────┤
│ Comments:     │  Async    │ Comments:     │
│ R/W           │──────────►│ R (eventual)  │
├───────────────┤          ├───────────────┤
│ Analytics:    │  Gossip   │ Analytics:    │
│ R/W           │◄─────────►│ R/W           │
└───────────────┘          └───────────────┘
```
