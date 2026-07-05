package com.arch.eventdriven;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class EventBus {
    private final Map<String, List<EventSubscriber>> subscribers = new ConcurrentHashMap<>();

    public void subscribe(String eventType, EventSubscriber subscriber) {
        subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(subscriber);
    }

    public void publish(Event event) {
        List<EventSubscriber> subs = subscribers.get(event.getType());
        if (subs != null) {
            subs.forEach(s -> s.onEvent(event));
        }
    }

    public void unsubscribe(String eventType, EventSubscriber subscriber) {
        List<EventSubscriber> subs = subscribers.get(eventType);
        if (subs != null) {
            subs.remove(subscriber);
        }
    }
}
