package com.arch.eventdriven;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class Event {
    private final String id;
    private final String type;
    private final String aggregateId;
    private final Instant timestamp;
    private final Map<String, String> data;

    public Event(String type, String aggregateId, Instant timestamp, Map<String, String> data) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.aggregateId = aggregateId;
        this.timestamp = timestamp;
        this.data = data;
    }

    public String getId() { return id; }
    public String getType() { return type; }
    public String getAggregateId() { return aggregateId; }
    public Instant getTimestamp() { return timestamp; }
    public Map<String, String> getData() { return data; }
}
