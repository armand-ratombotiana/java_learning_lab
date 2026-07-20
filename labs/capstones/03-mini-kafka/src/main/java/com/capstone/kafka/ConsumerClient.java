package com.capstone.kafka;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class ConsumerClient {
    private final MessageBroker broker;
    private final String groupId;
    private final String clientId;
    private final Map<String, Integer> assignments = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> positions = new ConcurrentHashMap<>();
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final ConsumerConfig config;

    public record ConsumerConfig(String groupId, String clientId, String autoOffsetReset, int maxPollRecords) {
        public ConsumerConfig {
            autoOffsetReset = autoOffsetReset == null ? "latest" : autoOffsetReset;
            if (maxPollRecords <= 0) maxPollRecords = 500;
        }
    }

    public ConsumerClient(MessageBroker broker, ConsumerConfig config) {
        this.broker = broker;
        this.config = config;
        this.groupId = config.groupId();
        this.clientId = config.clientId();
    }

    public void subscribe(String topic) {
        List<TopicPartition> partitions = broker.getPartitions(topic);
        ConsumerGroup group = broker.getOrCreateGroup(groupId);
        for (TopicPartition tp : partitions) {
            String tpKey = topic + ":" + tp.getPartition();
            assignments.put(tpKey, tp.getPartition());
            long committed = broker.getCommittedOffset(groupId, topic, tp.getPartition());
            if ("earliest".equals(config.autoOffsetReset()) && committed == 0) {
                positions.put(tpKey, new AtomicLong(0));
            } else {
                positions.put(tpKey, new AtomicLong(committed));
            }
            group.assignPartition(tp.getPartition(), clientId);
        }
    }

    public List<TopicPartition.Message> poll(Duration timeout) {
        List<TopicPartition.Message> allMessages = new ArrayList<>();
        for (Map.Entry<String, Integer> assignment : assignments.entrySet()) {
            String[] parts = assignment.getKey().split(":");
            String topic = parts[0];
            int partition = assignment.getValue();
            AtomicLong pos = positions.get(assignment.getKey());
            if (pos == null) continue;
            List<TopicPartition.Message> messages = broker.consume(topic, partition, pos.get(), config.maxPollRecords());
            if (!messages.isEmpty()) {
                pos.set(messages.get(messages.size() - 1).offset() + 1);
                allMessages.addAll(messages);
            }
        }
        try { Thread.sleep(timeout.toMillis()); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        return allMessages;
    }

    public void commitSync() {
        for (Map.Entry<String, Integer> assignment : assignments.entrySet()) {
            String[] parts = assignment.getKey().split(":");
            String topic = parts[0];
            int partition = assignment.getValue();
            AtomicLong pos = positions.get(assignment.getKey());
            if (pos != null) broker.commitOffset(groupId, topic, partition, pos.get());
        }
    }

    public Set<String> getAssignment() { return Set.copyOf(assignments.keySet()); }

    public void close() { running.set(false); commitSync(); }

    public ConsumerConfig getConfig() { return config; }
}
