package com.distributed.queues;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DeadLetterQueue<T> {
    private final int maxRetries;
    private final ConcurrentHashMap<String, DLEntry<T>> dlq = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> retryCounts = new ConcurrentHashMap<>();

    public record DLEntry<T>(String originalId, T payload, String reason, long timestamp) {}

    public DeadLetterQueue(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public Optional<T> process(String messageId, T payload) {
        AtomicInteger count = retryCounts.computeIfAbsent(messageId, k -> new AtomicInteger(0));
        int attempts = count.incrementAndGet();
        if (attempts > maxRetries) {
            dlq.put(messageId, new DLEntry<>(messageId, payload, "Max retries exceeded: " + maxRetries, System.currentTimeMillis()));
            retryCounts.remove(messageId);
            return Optional.empty();
        }
        return Optional.of(payload);
    }

    public List<DLEntry<T>> getDeadLetters() {
        return new ArrayList<>(dlq.values());
    }

    public boolean requeue(String messageId) {
        DLEntry<T> entry = dlq.remove(messageId);
        return entry != null;
    }

    public int getDeadLetterCount() { return dlq.size(); }
}
