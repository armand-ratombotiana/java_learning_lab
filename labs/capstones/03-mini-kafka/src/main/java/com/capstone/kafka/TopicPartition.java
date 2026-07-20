package com.capstone.kafka;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class TopicPartition {
    private final String topic;
    private final int partition;
    private final List<Message> messages = new CopyOnWriteArrayList<>();
    private final AtomicLong offsetSeq = new AtomicLong(0);
    private volatile long baseOffset = 0;

    public record Message(long offset, byte[] key, byte[] value, long timestamp, Map<String, String> headers) {
        public Message { headers = headers == null ? Map.of() : Map.copyOf(headers); }
    }

    public TopicPartition(String topic, int partition) {
        this.topic = topic;
        this.partition = partition;
    }

    public long append(byte[] key, byte[] value) {
        return append(key, value, Map.of());
    }

    public long append(byte[] key, byte[] value, Map<String, String> headers) {
        long offset = offsetSeq.getAndIncrement();
        messages.add(new Message(offset, key, value, System.currentTimeMillis(), headers));
        return offset;
    }

    public List<Message> read(long offset, int maxMessages) {
        int startIdx = (int)(offset - baseOffset);
        if (startIdx >= messages.size() || startIdx < 0) return List.of();
        int endIdx = Math.min(startIdx + maxMessages, messages.size());
        return List.copyOf(messages.subList(startIdx, endIdx));
    }

    public long getHighWatermark() { return offsetSeq.get(); }

    public long getBaseOffset() { return baseOffset; }

    public String getTopic() { return topic; }

    public int getPartition() { return partition; }

    public int messageCount() { return messages.size(); }

    public void compact() {
        Map<ByteArrayWrapper, Message> latest = new HashMap<>();
        for (Message msg : messages) {
            if (msg.key() != null) latest.put(new ByteArrayWrapper(msg.key()), msg);
        }
        messages.clear();
        latest.values().stream().sorted(Comparator.comparingLong(Message::offset)).forEach(messages::add);
        baseOffset = messages.isEmpty() ? offsetSeq.get() : messages.get(0).offset();
        if (!messages.isEmpty()) offsetSeq.set(messages.get(messages.size() - 1).offset() + 1);
    }

    public void clear() { messages.clear(); offsetSeq.set(0); baseOffset = 0; }

    private record ByteArrayWrapper(byte[] data) {
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ByteArrayWrapper that)) return false;
            return Arrays.equals(data, that.data);
        }
        @Override public int hashCode() { return Arrays.hashCode(data); }
    }
}
