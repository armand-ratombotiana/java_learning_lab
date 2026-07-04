# Event-Driven Architecture Security

## Event Authentication
```java
@Configuration
public class EventSecurityConfig {

    @Bean
    public ProducerInterceptor<String, Event> authInterceptor() {
        return new ProducerInterceptor<>() {
            @Override
            public ProducerRecord<String, Event> onSend(ProducerRecord<String, Event> record) {
                record.headers().add("X-Service-Auth",
                    generateServiceToken().getBytes());
                record.headers().add("X-Service-Id",
                    "order-service".getBytes());
                return record;
            }
        };
    }
}
```

## Event Encryption
```java
@Configuration
public class EventEncryptionConfig {

    @Bean
    public Serializer<Object> encryptedSerializer() {
        return new Serializer<>() {
            @Override
            public byte[] serialize(String topic, Object data) {
                byte[] json = objectMapper.writeValueAsBytes(data);
                return encrypt(json);
            }
        };
    }
}
```

## Access Control
```yaml
# Kafka ACLs for topic-level security
kafka-acls.bat --authorizer-properties \
  zookeeper.connect=localhost:2181 \
  --add --allow-principal User:order-service \
  --operation Write --topic order-events
```

## Audit Trail
```java
@Component
public class EventAuditLogger {
    @EventListener
    public void auditEvent(Event event) {
        auditLogRepository.save(new AuditEntry(
            event.getEventId(), event.getType(),
            event.getTimestamp(), getCurrentPrincipal()
        ));
    }
}
```

## Schema Registry Security
```yaml
spring:
  kafka:
    properties:
      schema.registry.url: https://schema-registry:8081
      schema.registry.auth:
        username: ${SCHEMA_REGISTRY_USER}
        password: ${SCHEMA_REGISTRY_PASSWORD}
```
