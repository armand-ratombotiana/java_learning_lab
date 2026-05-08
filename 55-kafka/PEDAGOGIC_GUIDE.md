# Pedagogic Guide - Kafka Connect

## Learning Path

### Phase 1: Fundamentals
1. Understand Kafka Connect architecture
2. Learn connector types (source/sink)
3. Configure basic connectors

### Phase 2: Intermediate
1. Connect REST API usage
2. Transformations (SMTs)
3. Offset management

### Phase 3: Advanced
1. Custom connector development
2. Schema evolution
3. Deployment modes

## Connector Types

| Type | Purpose |
|------|---------|
| Source | Push data TO Kafka |
| Sink | Pull data FROM Kafka |

## Common Connectors
- JDBC Source/Sink
- Debezium (CDC)
- Elasticsearch
- S3
- MQTT

## Best Practices
- Use schema registry
- Configure appropriate transforms
- Monitor connector status
- Handle schema changes