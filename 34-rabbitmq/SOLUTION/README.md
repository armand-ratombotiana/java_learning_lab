# RabbitMQ Solution

## Concepts Covered

### Publisher Confirms
- Confirm mode for reliable publishing
- Wait for acknowledgment
- Batch publishing

### Dead Letter Queues
- Handling failed messages
- DLX exchange configuration

### Delayed Messages
- Delayed message exchange plugin

### Advanced Queues
- Priority queues
- TTL (Time-to-Live) on messages and queues
- Lazy queues for disk-backed storage

### Message Patterns
- Correlation IDs for request/reply
- Reply-to pattern

### Clustering & Federation
- Cluster setup for high availability
- Federation for cross-datacenter replication
- Shovel for queue-to-queue transfer

### Quorum & Streams
- Quorum queues for distributed replication
- Stream for log-based messaging

## Dependencies

```xml
<dependency>
    <groupId>com.rabbitmq</groupId>
    <artifactId>amqp-client</artifactId>
    <version>5.19.0</version>
</dependency>
```

## Configuration

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    publisher-confirm-type: correlated
```

## Running Tests

```bash
mvn test -Dtest=RabbitMQSolutionTest
```