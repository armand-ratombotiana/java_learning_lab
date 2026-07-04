# CDC Theory

## CDC Mechanisms
- **Log-based**: Read DB transaction log (binlog, WAL) - minimal impact
- **Trigger-based**: DB triggers - performance impact
- **Query-based**: Timestamp columns - simple, no deletes

## Debezium Architecture
```
[Source DB] -> [Debezium Connector] -> [Kafka Topic] -> [Stream/Store]
```

## Change Event Structure
```json
{op: "c/u/d/r", before: {...}, after: {...}, source: {db, table, ts_ms}}
```
