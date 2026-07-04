# Theory: Messaging

## Messaging Patterns

### Point-to-Point (Queue)
One sender, one receiver. Messages are consumed once.
- JMS Queue, RabbitMQ queue with one consumer

### Pub/Sub (Topic)
One sender, multiple receivers. Each gets a copy.
- JMS Topic, RabbitMQ fanout exchange, Kafka topic

### Message Broker Comparison
| Feature | RabbitMQ | Kafka | JMS |
|---------|----------|-------|-----|
| Protocol | AMQP 0-9-1 | Custom TCP | JMS API |
| Ordering | Per queue | Per partition | Per queue |
| Retention | Ack-based | Configurable | Ack-based |
| Speed | Moderate | Very high | Moderate |

## Spring Messaging Abstractions
- JmsTemplate for JMS
- RabbitTemplate for RabbitMQ
- KafkaTemplate for Kafka
- @JmsListener, @RabbitListener, @KafkaListener

## Dead Letter Queue
Messages that fail processing are moved to a DLQ for later inspection.
