# Distributed Messaging: Security

## Security Features

### Kafka
- TLS for encryption in transit
- SASL authentication (PLAIN, SCRAM, Kerberos)
- ACL-based authorization (topic, group, cluster)
- Encryption at rest (disk encryption)

### RabbitMQ
- TLS support
- SASL authentication
- Per-vhost, per-exchange, per-queue permissions
- Client certificate authentication

## Best Practices
1. Encrypt all sensitive message payloads
2. Use TLS between producers/brokers/consumers
3. Implement least-privilege ACLs
4. Audit all administrative operations
5. Use separate credentials per application

```java
public class SecureMessagePublisher {
    private final KafkaProducer<String, byte[]> producer;
    private final Cipher cipher;
    
    public void sendEncrypted(String topic, String key, Object payload) {
        byte[] plaintext = serialize(payload);
        byte[] encrypted = encrypt(plaintext);
        producer.send(new ProducerRecord<>(topic, key, encrypted));
    }
}
```
