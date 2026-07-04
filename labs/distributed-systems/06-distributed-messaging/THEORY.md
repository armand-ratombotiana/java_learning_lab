# Distributed Messaging: Theory

## Messaging Models

### Point-to-Point (Queue)
- Each message consumed by exactly one consumer
- Load balancing across consumers
- Example: task queues

### Publish-Subscribe (Topic)
- Each message delivered to all subscribers
- Fan-out delivery pattern
- Example: event notifications

## Delivery Guarantees

### At-Most-Once
Message may be lost but never redelivered.

### At-Least-Once
Message never lost but may be redelivered.

### Exactly-Once
Message delivered precisely once (most expensive).

## Broker Architectures

### Kafka
- Distributed log-based storage
- Partitioned topics
- Consumer groups for scaling
- Pull-based consumption

### RabbitMQ
- AMQP protocol
- Exchange-based routing
- Push-based consumption
- Rich routing patterns

### Pulsar
- Separate compute and storage
- Segment-based architecture
- Multi-tenancy built-in
- Geo-replication native
