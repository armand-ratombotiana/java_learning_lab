package com.arch.eventdriven;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ProjectionEngine {
    private final Map<String, Projection> projections = new ConcurrentHashMap<>();
    private final Map<String, Object> projectionStates = new ConcurrentHashMap<>();

    public void registerProjection(String name, Projection projection) {
        projections.put(name, projection);
        projectionStates.put(name, projection.initialState());
    }

    @SuppressWarnings("unchecked")
    public void applyEvent(DomainEvent event) {
        projections.forEach((name, projection) -> {
            if (projection.handlesEvent(event.eventType())) {
                Object currentState = projectionStates.get(name);
                Object newState = projection.apply(currentState, event);
                projectionStates.put(name, newState);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public void rebuild(String name, EventStore eventStore) {
        Projection projection = projections.get(name);
        if (projection == null) throw new IllegalArgumentException("Unknown projection: " + name);
        Object state = projection.initialState();
        for (String streamId : eventStore.getAllStreamIds()) {
            List<DomainEvent> events = eventStore.readStreamFromVersion(streamId, 0);
            for (DomainEvent event : events) {
                if (projection.handlesEvent(event.eventType())) {
                    state = projection.apply(state, event);
                }
            }
        }
        projectionStates.put(name, state);
    }

    public Object getState(String name) { return projectionStates.get(name); }
    public Set<String> getProjectionNames() { return projections.keySet(); }
}

interface Projection {
    Object initialState();
    boolean handlesEvent(String eventType);
    Object apply(Object currentState, DomainEvent event);
}
