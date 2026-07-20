package com.capstone.kafka;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class MessageBroker {
    private final Map<String, List<TopicPartition>> topics = new ConcurrentHashMap<>();
    private final Map<String, Map<Integer, Long>> consumerOffsets = new ConcurrentHashMap<>();
    private final Map<String, ConsumerGroup> groups = new ConcurrentHashMap<>();

    public record BrokerConfig(String bootstrapServers, int numPartitions, short replicationFactor) {}

    public TopicPartition createTopic(String topic, int partitions) {
        List<TopicPartition> topicPartitions = new CopyOnWriteArrayList<>();
        for (int i = 0; i < partitions; i++) {
            topicPartitions.add(new TopicPartition(topic, i));
        }
        topics.put(topic, topicPartitions);
        return topicPartitions.get(0);
    }

    public long produce(String topic, byte[] key, byte[] value) {
        return produce(topic, key, value, Map.of());
    }

    public long produce(String topic, byte[] key, byte[] value, Map<String, String> headers) {
        List<TopicPartition> partitions = topics.get(topic);
        if (partitions == null || partitions.isEmpty()) {
            createTopic(topic, 1);
            partitions = topics.get(topic);
        }
        int partitionIdx = key != null ? Math.abs(Arrays.hashCode(key) % partitions.size()) : 0;
        return partitions.get(partitionIdx).append(key, value, headers);
    }

    public List<TopicPartition.Message> consume(String topic, int partition, long offset, int maxMessages) {
        List<TopicPartition> partitions = topics.get(topic);
        if (partitions == null || partition >= partitions.size()) return List.of();
        return partitions.get(partition).read(offset, maxMessages);
    }

    public void commitOffset(String groupId, String topic, int partition, long offset) {
        consumerOffsets.computeIfAbsent(groupId, k -> new ConcurrentHashMap<>())
            .put(topic + ":" + partition, offset);
    }

    public long getCommittedOffset(String groupId, String topic, int partition) {
        Map<Integer, Long> offsets = consumerOffsets.get(groupId);
        if (offsets == null) return 0;
        return offsets.getOrDefault(topic + ":" + partition, 0L);
    }

    public ConsumerGroup getOrCreateGroup(String groupId) {
        return groups.computeIfAbsent(groupId, ConsumerGroup::new);
    }

    public List<TopicPartition> getPartitions(String topic) {
        return List.copyOf(topics.getOrDefault(topic, List.of()));
    }

    public Set<String> getTopics() { return topics.keySet(); }

    public int topicCount() { return topics.size(); }

    public void reset() { topics.clear(); consumerOffsets.clear(); groups.clear(); }
}
