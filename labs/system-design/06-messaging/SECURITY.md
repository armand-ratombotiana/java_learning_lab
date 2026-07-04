# Messaging - SECURITY

## Authentication

### Kafka SASL
```yaml
spring.kafka.properties:
  security.protocol: SASL_SSL
  sasl.mechanism: SCRAM-SHA-512
  sasl.jaas.config: org.apache.kafka.common.security.scram.ScramLoginModule required
    username="producer-user"
    password="producer-password";
```

### SSL/TLS
```yaml
spring.kafka.ssl:
  truststore-location: classpath:kafka.truststore.jks
  truststore-password: ${TRUSTSTORE_PASSWORD}
  keystore-location: classpath:kafka.keystore.jks
  keystore-password: ${KEYSTORE_PASSWORD}
  key-password: ${KEY_PASSWORD}
```

## Authorization

### Kafka ACLs
```bash
# Producer can write to orders topic
kafka-acls --authorizer-properties \
  --add --allow-principal User:producer-user \
  --operation Write --topic orders

# Consumer can read from orders topic
kafka-acls --authorizer-properties \
  --add --allow-principal User:consumer-user \
  --operation Read --topic orders \
  --group order-group
```

## Message Encryption

### At Rest
```yaml
# Kafka broker encryption at rest
# Use encrypted disks (LUKS, EBS encryption)
# Or Kafka's built-in log encryption (KIP-496)
```

### In Transit
```yaml
spring.kafka.properties:
  security.protocol: SSL  # encrypts traffic between producer/consumer and broker
  # Broker-to-broker encryption also required
```

### End-to-End Encryption
```java
// Encrypt sensitive fields before sending
public class SecureMessagePublisher {
    public void publishSensitiveData(String topic, SensitivePayload payload) {
        payload.setSsn(encrypt(payload.getSsn(), publicKey));
        payload.setCardNumber(encrypt(payload.getCardNumber(), publicKey));
        kafkaTemplate.send(topic, payload);
    }
}
```

## Message Validation

### Schema Registry
```java
// Validate message structure before processing
@KafkaListener(topics = "orders")
public void consume(GenericRecord record) {
    // Schema registry ensures backward compatibility
    String schemaName = record.getSchema().getName();
    int schemaVersion = record.getSchema().getVersion();
    log.info("Processing {} v{}", schemaName, schemaVersion);
}
```

## Audit Logging
All messages sent through the broker can be logged for compliance. Kafka's immutable log provides a natural audit trail.
