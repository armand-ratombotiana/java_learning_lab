package com.capstone.kafka;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class OffsetManager {
    private final Map<String, Map<Integer, OffsetMetadata>> offsets = new ConcurrentHashMap<>();
    private final AtomicLong globalOffsetSeq = new AtomicLong(0);

    public record OffsetMetadata(long offset, long timestamp, String metadata) {
        public OffsetMetadata { metadata = metadata == null ? "" : metadata; }
    }

    public void commitOffset(String groupId, String topic, int partition, long offset) {
        commitOffset(groupId, topic, partition, offset, "");
    }

    public void commitOffset(String groupId, String topic, int partition, long offset, String metadata) {
        offsets.computeIfAbsent(groupId + ":" + topic, k -> new ConcurrentHashMap<>())
            .put(partition, new OffsetMetadata(offset, System.currentTimeMillis(), metadata));
    }

    public OffsetMetadata getCommittedOffset(String groupId, String topic, int partition) {
        Map<Integer, OffsetMetadata> topicOffsets = offsets.get(groupId + ":" + topic);
        if (topicOffsets == null) return new OffsetMetadata(0, 0, "");
        return topicOffsets.getOrDefault(partition, new OffsetMetadata(0, 0, ""));
    }

    public long getNextOffset(String groupId, String topic, int partition) {
        OffsetMetadata meta = getCommittedOffset(groupId, topic, partition);
        return meta.offset() + 1;
    }

    public void resetOffset(String groupId, String topic, int partition, long newOffset) {
        commitOffset(groupId, topic, partition, newOffset, "reset");
    }

    public long generateGlobalOffset() { return globalOffsetSeq.incrementAndGet(); }

    public void deleteOffsets(String groupId, String topic) {
        offsets.remove(groupId + ":" + topic);
    }

    public Map<String, Map<Integer, OffsetMetadata>> getAllOffsets() {
        Map<String, Map<Integer, OffsetMetadata>> copy = new ConcurrentHashMap<>();
        for (Map.Entry<String, Map<Integer, OffsetMetadata>> e : offsets.entrySet()) {
            copy.put(e.getKey(), Map.copyOf(e.getValue()));
        }
        return Map.copyOf(copy);
    }

    public void clear() { offsets.clear(); globalOffsetSeq.set(0); }
}
