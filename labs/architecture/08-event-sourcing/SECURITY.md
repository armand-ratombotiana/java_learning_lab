# Event Sourcing Security

## Security Considerations

### Event Encryption
```java
@Component
public class EncryptedEventStore implements EventStore {

    private final EventStore delegate;
    private final EncryptionService encryption;

    @Override
    public void append(String aggregateId, List<DomainEvent> events, int expectedVersion) {
        List<DomainEvent> encrypted = events.stream()
            .map(event -> new EncryptedEvent(
                event.getEventId(),
                encryption.encrypt(serialize(event)),
                event.getTimestamp()
            ))
            .toList();
        delegate.append(aggregateId, encrypted, expectedVersion);
    }
}
```

### Audit Logging
```java
@Component
public class EventAuditLogger {
    @EventListener
    public void onAnyEvent(DomainEvent event) {
        auditLogRepository.save(new AuditEntry(
            event.getEventId(),
            event.getClass().getSimpleName(),
            java.time.Instant.now(),
            SecurityContextHolder.getContext().getAuthentication().getName()
        ));
    }
}
```

### Sensitive Data Protection
```java
public record UserRegisteredEvent(
    String userId,
    String username,
    @JsonIgnore String passwordHash, // Never serialize to event store
    String email
) {}
```

## Event Store Access Control
- Read access limited to projections and admin
- Write access limited to aggregates
- No direct event deletion or modification
- Event retention policy with archival
- GDPR compliance: store events per user with anonymization capability
