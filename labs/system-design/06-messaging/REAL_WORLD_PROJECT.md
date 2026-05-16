# Messaging - REAL WORLD PROJECT

Event-driven architecture with:
- Kafka for events
- RabbitMQ for tasks
- Saga orchestration

```java
@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic ordersTopic() {
        return TopicBuilder.name("orders").partitions(3).replicas(1).build();
    }
}
```