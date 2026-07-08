package com.arch.eventdriven;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record DomainEvent(
    String eventId,
    String eventType,
    String aggregateId,
    Map<String, Object> data,
    Instant occurredAt,
    int version
) {
    public DomainEvent {
        if (eventId == null || eventId.isBlank()) eventId = UUID.randomUUID().toString();
        if (occurredAt == null) occurredAt = Instant.now();
    }

    public DomainEvent(String eventType, String aggregateId, Map<String, Object> data) {
        this(UUID.randomUUID().toString(), eventType, aggregateId, data, Instant.now(), 1);
    }

    public static DomainEvent create(String eventType, String aggregateId, Map<String, Object> data) {
        return new DomainEvent(eventType, aggregateId, data);
    }
}
