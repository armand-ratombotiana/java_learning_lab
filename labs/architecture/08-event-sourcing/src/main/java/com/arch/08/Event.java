package com.arch.eventsourcing;

import java.time.Instant;

public class Event {
    private final String type;
    private final String aggregateId;
    private final String data;
    private final Instant timestamp;

    public Event(String type, String aggregateId, String data) {
        this.type = type;
        this.aggregateId = aggregateId;
        this.data = data;
        this.timestamp = Instant.now();
    }

    public String getType() { return type; }
    public String getAggregateId() { return aggregateId; }
    public String getData() { return data; }
    public Instant getTimestamp() { return timestamp; }
}
