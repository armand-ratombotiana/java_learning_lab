# CQRS Architecture Reference

## System Architecture
```
                    +---------------------+
                    |   API Gateway        |
                    +----------+----------+
                               |
              +----------------+----------------+
              |                                 |
     +--------v--------+             +----------v----------+
     |  Command Bus     |             |    Query Bus        |
     |  (Commands)      |             |    (Queries)        |
     +--------+---------+             +----------+----------+
              |                                 |
     +--------v--------+             +----------v----------+
     |  Command Handler |             |    Query Handler    |
     |  (Business Logic)|             |    (Read Model)     |
     +--------+---------+             +----------+----------+
              |                                 |
     +--------v--------+             +----------v----------+
     |  Event Store     |             |    Read Database    |
     |  (Source of Truth)|            |    (Materialized)   |
     +-------------------+             +---------------------+
              |
     +--------v--------+
     |  Event Bus       |
     +--------+---------+
              |
     +--------v--------+
     |  Projections     |
     +------------------+
```

## Technology Choices
| Component | Technology | Purpose |
|-----------|-----------|---------|
| Command Bus | Axon CommandBus | Route commands to handlers |
| Event Store | Axon EventStore | Persist events immutably |
| Read Model | MongoDB | Fast denormalized reads |
| Query Bus | Axon QueryBus | Route queries to handlers |
| Projection | Spring Component | Update read models from events |

## Database Strategy
- Write: PostgreSQL (normalized, ACID)
- Read: MongoDB (denormalized, indexed for queries)
- Events: Dedicated event store table or Axon Event Store
- Caching: Redis for hot read models
