package com.distributed.queues;

import java.util.concurrent.ConcurrentHashMap;

public class MessageDeduplicator {
    private final ConcurrentHashMap<String, Long> seenIds = new ConcurrentHashMap<>();
    private final long dedupWindowMs;

    public MessageDeduplicator(long dedupWindowMs) {
        this.dedupWindowMs = dedupWindowMs;
    }

    public boolean isDuplicate(String messageId) {
        Long existing = seenIds.get(messageId);
        if (existing != null && (System.currentTimeMillis() - existing) < dedupWindowMs) {
            return true;
        }
        seenIds.put(messageId, System.currentTimeMillis());
        return false;
    }

    public void cleanup() {
        long cutoff = System.currentTimeMillis() - dedupWindowMs;
        seenIds.entrySet().removeIf(entry -> entry.getValue() < cutoff);
    }

    public int getCacheSize() { return seenIds.size(); }
}
