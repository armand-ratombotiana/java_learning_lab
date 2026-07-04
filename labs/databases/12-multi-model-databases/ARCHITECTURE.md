# Architecture: Polyglot Persistence Patterns

## Common Architecture Patterns

### CQRS + Polyglot
```
Command Side:   PostgreSQL (strong consistency, ACID)
Query Side:     MongoDB / Elasticsearch (denormalized, fast reads)
Event Bus:      Kafka / Debezium (CDC for synchronization)
```

### Strangler Fig Pattern
Gradually migrate from monolithic DB to polyglot:
1. Identify bounded context per data type
2. Extract to dedicated database
3. Dual-write during transition
4. Remove old data after migration

### Microservice + Polyglot
```
Order Service   → PostgreSQL
Catalog Service → MongoDB
Search Service  → Elasticsearch
Session Service → Redis
Analytics       → ClickHouse / TimescaleDB
```

## When to Use Each Approach

| Factor | Multi-Model DB | Polyglot Persistence |
|---|---|---|
| Operations | Single engine to manage | Multiple DB engines |
| Consistency | Cross-model consistency | No global consistency |
| Query | Single query across models | Application-level joins |
| Performance | Jack of all trades | Best-in-class per model |
| Team size | Smaller teams | Larger teams with specialists |
| Migration | Lower complexity | Higher complexity |

## Decision Framework
```
Question: Do I need one database or multiple?
├─ Single data model + single workload → Single DB
├─ Multiple data models, small team   → Multi-model DB
├─ Multiple workloads, large team     → Polyglot persistence
└─ Different consistency requirements  → Polyglot (per-consistency model)
```
