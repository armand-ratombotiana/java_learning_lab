# Architectural Patterns with Date-Time API

## Layered Architecture
- **Controller/API layer**: Accept and return ISO-8601 strings, convert to java.time at service boundary
- **Service layer**: Use `ZonedDateTime` for business logic, `Instant` for audit trails
- **Repository layer**: Use `LocalDateTime` with UTC for database storage

## Domain Events
```java
record OrderPlaced(String orderId, Instant occurredAt) {}
record PaymentReceived(String transactionId, ZonedDateTime processedAt) {}
```

## Audit Trail
```java
public class AuditEntry {
    private final Instant timestamp = Instant.now();
    private final String userId;
    private final String action;
}
```

## Scheduling Systems
Use `LocalDate` for date-based schedules, `ZonedDateTime` for time-based appointments, and `Duration` for service level agreements. Store all times in UTC internally, convert to user timezone at presentation.
