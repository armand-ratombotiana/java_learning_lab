package com.arch.eventdriven;

import java.time.Instant;

public class EventPublisher {
    private final EventBus eventBus;

    public EventPublisher(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void publishOrderCreated(String orderId, String customerId) {
        Event event = new Event("OrderCreated", orderId, Instant.now(),
                java.util.Map.of("customerId", customerId));
        eventBus.publish(event);
        System.out.println("Published: " + event.getType() + " for order " + orderId);
    }
}
