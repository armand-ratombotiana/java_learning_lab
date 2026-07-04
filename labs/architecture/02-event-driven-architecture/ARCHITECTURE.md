# Event-Driven Architecture Reference

## System Architecture
```
[Payment Service] --> [Payment Events Topic]
                           |
              +------------+------------+
              |            |            |
        [Analytics]   [Notification]  [Accounting]

[Order Service] --> [Order Events Topic]
                           |
              +------------+------------+
              |            |            |
        [Inventory]   [Shipping]    [Invoicing]

[User Service] --> [User Events Topic]
                           |
              +------------+------------+
              |            |            |
        [Email]       [CRM]        [Marketing]
```

## Event Catalog
| Event | Publisher | Consumers | Schema | Retention |
|-------|-----------|-----------|--------|-----------|
| OrderPlaced | Order Service | Inventory, Notification, Analytics | v2 | 7 days |
| PaymentReceived | Payment Service | Order, Notification, Accounting | v1 | 30 days |
| UserRegistered | User Service | Email, CRM, Marketing | v3 | 90 days |

## Data Flow
```
Event Size: ~2KB average
Throughput: ~50,000 events/second peak
Latency: <100ms p99 end-to-end
```

## Infrastructure
- Apache Kafka (3 brokers, replication=3)
- Schema Registry (Avro/JSON Schema)
- Kafka Connect for DB integration
- Kafka Streams for processing
- MirrorMaker for cross-DC replication
