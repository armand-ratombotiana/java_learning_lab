package com.arch.eventdriven;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public class EventStore {
    private final Map<String, List<DomainEvent>> eventStreams = new ConcurrentHashMap<>();
    private final Map<String, Integer> streamVersions = new ConcurrentHashMap<>();
    private final List<EventSubscriber> subscribers = new CopyOnWriteArrayList<>();

    public void appendToStream(String streamId, DomainEvent event, int expectedVersion) {
        List<DomainEvent> stream = eventStreams.computeIfAbsent(streamId, k -> new CopyOnWriteArrayList<>());
        int currentVersion = streamVersions.getOrDefault(streamId, 0);
        if (expectedVersion != -1 && currentVersion != expectedVersion) {
            throw new ConcurrencyException("Stream " + streamId + " version mismatch. Expected: " + expectedVersion + " Actual: " + currentVersion);
        }
        stream.add(event);
        streamVersions.put(streamId, currentVersion + 1);
        notifySubscribers(streamId, event, currentVersion + 1);
    }

    public Stream<DomainEvent> readStream(String streamId) {
        List<DomainEvent> stream = eventStreams.get(streamId);
        if (stream == null) return Stream.empty();
        return stream.stream();
    }

    public List<DomainEvent> readStreamFromVersion(String streamId, int fromVersion) {
        List<DomainEvent> stream = eventStreams.get(streamId);
        if (stream == null) return List.of();
        if (fromVersion < 0 || fromVersion >= stream.size()) return List.of();
        return stream.subList(fromVersion, stream.size());
    }

    public int getStreamVersion(String streamId) {
        return streamVersions.getOrDefault(streamId, 0);
    }

    public void subscribe(EventSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    private void notifySubscribers(String streamId, DomainEvent event, int version) {
        EventNotification notification = new EventNotification(streamId, event, version);
        subscribers.forEach(s -> {
            try { s.onEvent(notification); }
            catch (Exception e) { System.err.println("Subscriber error: " + e.getMessage()); }
        });
    }

    public Set<String> getAllStreamIds() { return eventStreams.keySet(); }
    public long getTotalEventCount() { return eventStreams.values().stream().mapToLong(List::size).sum(); }
}

record EventNotification(String streamId, DomainEvent event, int version) {}

interface EventSubscriber {
    void onEvent(EventNotification notification);
}

class ConcurrencyException extends RuntimeException {
    public ConcurrencyException(String message) { super(message); }
}
