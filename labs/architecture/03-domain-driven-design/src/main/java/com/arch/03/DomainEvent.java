package com.arch.ddd;

import java.time.Instant;
import java.util.UUID;

public class DomainEvent {
    private final String eventId;
    private final String aggregateId;
    private final String eventType;
    private final Instant occurredOn;

    public DomainEvent(String aggregateId, String eventType) {
        this.eventId = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.occurredOn = Instant.now();
    }

    public String getEventId() { return eventId; }
    public String getAggregateId() { return aggregateId; }
    public String getEventType() { return eventType; }
    public Instant getOccurredOn() { return occurredOn; }
}
