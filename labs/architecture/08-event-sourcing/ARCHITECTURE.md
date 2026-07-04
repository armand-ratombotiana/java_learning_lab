# Event Sourcing Architecture Reference

## System Architecture
```
                     +-------------------+
                     |   Command Bus      |
                     +--------+----------+
                              |
                     +--------v----------+
                     |   Aggregate        |
                     |   (Event Sourced)  |
                     +--------+----------+
                              |
               +----------------+----------------+
               |                |                |
        +------v-----+  +------v-----+  +------v-------+
        | Event Store |  | Snapshot   |  | Projections  |
        | (Postgres)  |  | Repository |  | (Read Models)|
        +------+------+  +------------+  +--------------+
               |
        +------v------+
        | Event Bus    |
        +------+------+
               |
        +------v------+
        | Consumers    |
        | (Analytics,  |
        |  Notification|
        |  etc.)       |
        +--------------+
```

## Event Schema
```
events table:
  global_sequence: BIGSERIAL PK
  aggregate_id: VARCHAR(36)
  aggregate_type: VARCHAR(255)
  event_type: VARCHAR(255)
  event_data: JSONB
  version: INT
  timestamp: TIMESTAMP
  metadata: JSONB (user, IP, correlation ID)
```

## Projection Strategy
| Projection | Events Used | Update Strategy | Technology |
|-----------|-------------|-----------------|------------|
| Account View | AccountCreated, MoneyDeposited, MoneyWithdrawn | Event-driven | MongoDB |
| Daily Summary | MoneyDeposited, MoneyWithdrawn | Batch nightly | PostgreSQL |
| Audit Report | All | On demand (full replay) | Elasticsearch |

## Snapshot Configuration
- Threshold: Every 50 events
- Storage: Same database as event store
- Format: Serialized aggregate state as JSON
- Load strategy: Load snapshot, then apply newer events
