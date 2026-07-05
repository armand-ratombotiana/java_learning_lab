package com.arch.eventdriven;

import java.time.Instant;

public class EventPublisher {
    private final EventBus eventBus;

    public EventPublisher(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void publishOrderCreated(String orderId, String customerId) {
        Event event = new Event("OrderCreated", orderId, Instant.now(),
                Map.of("customerId", customerId));
        eventBus.publish(event);
        System.out.println("Published: " + event.getType() + " for order " + orderId);
    }

    private static class Map {
        private final java.util.Map<String, String> entries;

        public Map(String key, String value) {
            this.entries = new java.util.HashMap<>();
            this.entries.put(key, value);
        }

        public java.util.Map<String, String> toMap() {
            return entries;
        }
    }
}
