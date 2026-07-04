# How Multi-Model Databases & Polyglot Persistence Work

## Multi-Model Database Architecture
```
Unified Query Layer (e.g., AQL in ArangoDB)
         │
    ┌────┴────┐
    │  Query  │
    │  Router │
    └────┬────┘
         │
    ┌────┴─────────────────────┐
    │  Storage Engine          │
    │  ┌──────┐ ┌────┐ ┌────┐ │
    │  │ Doc  │ │Graph│ │KV  │ │
    │  │Store │ │Store│ │Store│ │
    │  └──────┘ └────┘ └────┘ │
    └─────────────────────────┘
```

## Polyglot Application Architecture
```
Application
    │
    ├─ RDBMS (PostgreSQL)   ── Orders, Customers, Payments
    ├─ MongoDB               ── Product Catalog, Content
    ├─ Redis                 ── Session Cache, Rate Limiter
    ├─ Neo4j                 ── Recommendations, Social Graph
    └─ Elasticsearch         ── Full-text Search
```

## Data Synchronization
- **Application-level**: Service coordinates writes across databases
- **Eventual synchronization**: CDC (Change Data Capture) with Kafka
- **Dual-write**: Write to primary DB, publish event, secondary DB consumes
- **CQRS**: Separate write model (RDBMS) from read models (Elasticsearch, cache)

## Cross-Model Consistency
- No global transaction across heterogeneous databases
- Use Sagas for distributed transactions
- Compensating actions for rollback
- Application-level idempotency for retries
