package com.arch.cqrs;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;

public class EventStore {
    private final Map<String, List<Event>> store = new ConcurrentHashMap<>();

    public void append(String aggregateId, Event event) {
        store.computeIfAbsent(aggregateId, k -> new ArrayList<>()).add(event);
    }

    public List<Event> getEvents(String aggregateId) {
        return store.getOrDefault(aggregateId, new ArrayList<>());
    }

    public static class Event {
        private final String type;
        private final String data;

        public Event(String type, String data) {
            this.type = type;
            this.data = data;
        }

        public String getType() { return type; }
        public String getData() { return data; }
    }
}
