# Pedagogic Guide - Debezium CDC

## Learning Path

### Phase 1: Fundamentals
1. Understand CDC concept
2. Install and configure Debezium
3. Capture basic changes

### Phase 2: Intermediate
1. Connector configurations
2. Event structure understanding
3. Schema evolution

### Phase 3: Advanced
1. Custom SMTs
2. Debezium Server
3. Production deployment

## Supported Databases

| Database | Connector |
|----------|-----------|
| PostgreSQL | PostgresConnector |
| MySQL | MySqlConnector |
| MongoDB | MongoDbConnector |
| SQL Server | SqlServerConnector |

## Event Structure
- before: Previous state (for updates/deletes)
- after: New state
- op: Operation type (c=create, u=update, d=delete)
- ts_ms: Timestamp

## Use Cases
- Data replication
- Cache invalidation
- Audit logging
- Event sourcing