# Pedagogic Guide - RabbitMQ

## Learning Path

### Phase 1: Core Concepts
1. Producers, exchanges, queues, consumers
2. Exchange types (direct, fanout, topic, headers)
3. Message attributes and properties
4. Binding keys and routing patterns

### Phase 2: Java Integration
1. Spring AMQP setup and configuration
2. RabbitTemplate for sending messages
3. @RabbitListener for consuming
4. Message converter serialization

### Phase 3: Reliability Patterns
1. Publisher confirms and returns
2. Consumer acknowledgments
3. Manual vs automatic ack modes
4. Transaction management

### Phase 4: Advanced Features
1. Dead letter exchanges and queues
2. Message TTL and max length
3. Priority queues
4. Virtual hosts for isolation

### Phase 5: Operations
1. Cluster setup and management
2. Federation and shovel
3. Memory and disk alarms
4. Monitoring with management UI

## Exchange Types

| Type | Routing Pattern |
|------|-----------------|
| Direct | Exact routing key match |
| Fanout | All bound queues |
| Topic | Wildcard pattern matching |
| Headers | Header attribute matching |

## Interview Topics
- Message broker vs. direct database writes
- RabbitMQ vs. Kafka comparison
- Ensuring message delivery (confirmations)
- Handling message ordering
- Scaling consumers and queues
- Exchange design patterns

## Key Configurations
- `prefetchCount` - controls unacked messages per consumer
- `default-requeue-rejected` - retry behavior
- `x-max-priority` - priority queue support
- `x-message-ttl` - per-queue expiration

## Next Steps
- Explore Shovel plugin for queue mirroring
- Learn Federation for cross-datacenter replication
- Study clustering for high availability