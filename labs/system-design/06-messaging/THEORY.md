# Messaging Patterns - THEORY

## Overview

Messaging enables asynchronous communication, loose coupling, and scalability in distributed systems.

## 1. Message Patterns

### Point-to-Point
```java
public class PointToPointMessaging {
    public void send(String queue, Message message) {
        producer.send(queue, message);
    }
    
    public Message receive(String queue) {
        return consumer.receive(queue);
    }
}
```

### Publish-Subscribe
```java
public class PubSubMessaging {
    public void publish(String topic, Event event) {
        producer.publish(topic, event);
    }
    
    public void subscribe(String topic, Consumer<Event> handler) {
        consumer.subscribe(topic, handler);
    }
}
```

## 2. Message Broker Comparison

| Broker | Type | Throughput | Use Case |
|--------|------|------------|----------|
| Kafka | Pub/Sub | Very High | Event streaming |
| RabbitMQ | Hybrid | High | Task queues |
| ActiveMQ | Hybrid | Medium | Legacy integration |

## 3. Spring Integration

```java
@Configuration
@EnableIntegration
public class MessagingConfig {
    @Bean
    public MessageChannel orderChannel() {
        return new DirectChannel();
    }
    
    @Bean
    public IntegrationFlow orderFlow() {
        return f -> f
            .channel("orderChannel")
            .transform(orderTransformer())
            .handle(orderHandler());
    }
}
```

## Summary

1. **Point-to-Point**: One consumer per message (queues)
2. **Pub/Sub**: Multiple consumers (topics)
3. **Chose broker**: Kafka for streaming, RabbitMQ for tasks
4. **Use Spring**: Integration framework simplifies messaging