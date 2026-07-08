package com.arch.eventdriven;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

class DomainEventTest {
    @Test
    void shouldCreateDomainEvent() {
        DomainEvent event = DomainEvent.create("TestEvent", "agg-1", Map.of("key", "value"));
        assertEquals("TestEvent", event.eventType());
        assertEquals("agg-1", event.aggregateId());
        assertNotNull(event.eventId());
        assertNotNull(event.occurredAt());
    }

    @Test
    void shouldDefaultVersion() {
        DomainEvent event = DomainEvent.create("Test", "agg-1", Map.of());
        assertEquals(1, event.version());
    }
}
