package com.arch.eventdriven;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

class EventStoreTest {
    private EventStore eventStore;

    @BeforeEach
    void setUp() { eventStore = new EventStore(); }

    @Test
    void shouldAppendAndReadEvents() {
        DomainEvent event = DomainEvent.create("UserCreated", "user-1", Map.of("name", "Alice"));
        eventStore.appendToStream("user-1", event, -1);
        assertEquals(1, eventStore.getStreamVersion("user-1"));
    }

    @Test
    void shouldRejectConcurrentWrites() {
        eventStore.appendToStream("user-1", DomainEvent.create("Created", "user-1", Map.of()), 0);
        assertThrows(ConcurrencyException.class, () ->
            eventStore.appendToStream("user-1", DomainEvent.create("Updated", "user-1", Map.of()), 1));
    }

    @Test
    void shouldNotifySubscribers() {
        final boolean[] notified = {false};
        eventStore.subscribe(n -> notified[0] = true);
        eventStore.appendToStream("test", DomainEvent.create("Test", "test", Map.of()), -1);
        assertTrue(notified[0]);
    }
}

class ProjectionEngineTest {
    @Test
    void shouldBuildProjection() {
        ProjectionEngine engine = new ProjectionEngine();
        engine.registerProjection("count", new Projection() {
            public Object initialState() { return 0L; }
            public boolean handlesEvent(String type) { return type.equals("UserCreated"); }
            public Object apply(Object state, DomainEvent event) { return ((Long)state) + 1; }
        });
        engine.applyEvent(DomainEvent.create("UserCreated", "u1", Map.of()));
        engine.applyEvent(DomainEvent.create("UserCreated", "u2", Map.of()));
        assertEquals(2L, engine.getState("count"));
    }
}
