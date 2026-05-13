package com.stream.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConsumerGroupManager {

    private final Map<String, ConsumerGroup> groups = new ConcurrentHashMap<>();

    public void registerConsumer(String groupId, String consumerId, List<String> topics) {
        ConsumerGroup group = groups.computeIfAbsent(groupId, ConsumerGroup::new);
        group.addConsumer(consumerId, topics);
        log.info("Registered consumer {} in group {} for topics {}", consumerId, groupId, topics);
    }

    public void unregisterConsumer(String groupId, String consumerId) {
        ConsumerGroup group = groups.get(groupId);
        if (group != null) {
            group.removeConsumer(consumerId);
            log.info("Unregistered consumer {} from group {}", consumerId, groupId);
        }
    }

    public Map<String, List<Integer>> getPartitionAssignments(String groupId) {
        ConsumerGroup group = groups.get(groupId);
        if (group == null) {
            return Map.of();
        }
        return group.getAssignments();
    }

    public void commitOffset(String groupId, String consumerId, String topic, int partition, long offset) {
        ConsumerGroup group = groups.get(groupId);
        if (group != null) {
            group.commitOffset(consumerId, topic, partition, offset);
        }
    }

    public long getCommittedOffset(String groupId, String consumerId, String topic, int partition) {
        ConsumerGroup group = groups.get(groupId);
        if (group == null) {
            return 0;
        }
        return group.getOffset(consumerId, topic, partition);
    }

    private static class ConsumerGroup {
        private final String groupId;
        private final Map<String, String> consumerTopics;
        private final Map<String, Map<Integer, Long>> offsets;
        private final Map<String, List<Integer>> assignments;

        ConsumerGroup(String groupId) {
            this.groupId = groupId;
            this.consumerTopics = new ConcurrentHashMap<>();
            this.offsets = new ConcurrentHashMap<>();
            this.assignments = new ConcurrentHashMap<>();
        }

        void addConsumer(String consumerId, List<String> topics) {
            consumerTopics.put(consumerId, String.join(",", topics));
            recalculateAssignments();
        }

        void removeConsumer(String consumerId) {
            consumerTopics.remove(consumerId);
            offsets.remove(consumerId);
            recalculateAssignments();
        }

        void commitOffset(String consumerId, String topic, int partition, long offset) {
            offsets.computeIfAbsent(consumerId, k -> new ConcurrentHashMap<>())
                   .put(topic + "-" + partition, offset);
        }

        long getOffset(String consumerId, String topic, int partition) {
            Map<Integer, Long> topicOffsets = offsets.get(consumerId);
            if (topicOffsets == null) {
                return 0;
            }
            return topicOffsets.getOrDefault(topic + "-" + partition, 0L);
        }

        Map<String, List<Integer>> getAssignments() {
            return assignments;
        }

        private synchronized void recalculateAssignments() {
            assignments.clear();
            List<String> consumers = new ArrayList<>(consumerTopics.keySet());
            int idx = 0;

            for (String consumerId : consumerTopics.keySet()) {
                String topics = consumerTopics.get(consumerId);
                for (String topic : topics.split(",")) {
                    assignments.computeIfAbsent(topic, k -> new CopyOnWriteArrayList())
                              .add(idx % 3 + 1);
                    idx++;
                }
            }
        }
    }
}