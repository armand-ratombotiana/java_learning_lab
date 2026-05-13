package com.stream.service;

import com.stream.core.ConsumerGroupManager;
import com.stream.core.LogSegment;
import com.stream.core.TopicPartition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventStreamingService {

    private final Map<String, Map<Integer, TopicPartition>> topics = new ConcurrentHashMap<>();
    private final Map<String, List<CompletableFuture<Void>>> producers = new ConcurrentHashMap<>();
    private final Map<String, List<ConsumerRunner>> consumers = new ConcurrentHashMap<>();
    private final ConsumerGroupManager groupManager;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public void createTopic(String topicName, int partitions, short replicationFactor) throws IOException {
        Path topicDir = Paths.get("data", topicName);
        Files.createDirectories(topicDir);

        Map<Integer, TopicPartition> partitionsMap = new ConcurrentHashMap<>();
        for (int i = 0; i < partitions; i++) {
            Path partitionDir = topicDir.resolve(String.valueOf(i));
            partitionsMap.put(i, new TopicPartition(topicName, i, partitionDir));
        }

        topics.put(topicName, partitionsMap);
        log.info("Created topic {} with {} partitions", topicName, partitions);
    }

    public long produce(String topic, String key, String value) {
        return produce(topic, key.getBytes(), value.getBytes());
    }

    public long produce(String topic, byte[] key, byte[] value) {
        Map<Integer, TopicPartition> topicPartitions = topics.get(topic);
        if (topicPartitions == null || topicPartitions.isEmpty()) {
            throw new RuntimeException("Topic not found: " + topic);
        }

        int partition = selectPartition(key, topicPartitions.size());
        TopicPartition tp = topicPartitions.get(partition);

        LogSegment.AppendResult result = tp.append(key, value);
        if (result.success()) {
            log.debug("Produced to {}-{} offset {}", topic, partition, result.offset());
            return result.offset();
        }
        return -1;
    }

    private int selectPartition(byte[] key, int partitionCount) {
        if (key == null || key.length == 0) {
            return (int) (System.currentTimeMillis() % partitionCount);
        }
        return Math.abs(Arrays.hashCode(key) % partitionCount);
    }

    public void subscribe(String groupId, String consumerId, List<String> topicNames) {
        groupManager.registerConsumer(groupId, consumerId, topicNames);

        for (String topic : topicNames) {
            Map<Integer, TopicPartition> topicPartitions = topics.get(topic);
            if (topicPartitions == null) continue;

            ConsumerRunner runner = new ConsumerRunner(
                groupId, consumerId, topic, topicPartitions, groupManager
            );

            consumers.computeIfAbsent(groupId, k -> new ArrayList<>()).add(runner);
            executor.submit(runner);
        }

        log.info("Consumer {} subscribed to topics {} in group {}", consumerId, topicNames, groupId);
    }

    public List<LogSegment.Record> consume(String groupId, String consumerId, String topic, int partition, long offset, int maxRecords) {
        Map<Integer, TopicPartition> topicPartitions = topics.get(topic);
        if (topicPartitions == null) {
            return List.of();
        }

        TopicPartition tp = topicPartitions.get(partition);
        if (tp == null) {
            return List.of();
        }

        long committedOffset = groupManager.getCommittedOffset(groupId, consumerId, topic, partition);
        long startOffset = Math.max(offset, committedOffset);

        return tp.read(startOffset, maxRecords);
    }

    public void stopConsumer(String groupId, String consumerId) {
        List<ConsumerRunner> groupConsumers = consumers.get(groupId);
        if (groupConsumers != null) {
            groupConsumers.forEach(c -> {
                if (c.consumerId.equals(consumerId)) {
                    c.stop();
                }
            });
        }
    }

    public Map<String, Object> getTopicMetadata(String topic) {
        Map<Integer, TopicPartition> partitions = topics.get(topic);
        if (partitions == null) {
            return Map.of("error", "Topic not found");
        }

        return Map.of(
            "topic", topic,
            "partitions", partitions.keySet().stream()
                .map(p -> Map.of(
                    "partition", p,
                    "endOffset", partitions.get(p).endOffset(),
                    "highWatermark", partitions.get(p).getHighWatermark()
                ))
                .toList()
        );
    }

    public record ProduceResult(String topic, int partition, long offset, boolean success) {}

    private static class ConsumerRunner implements Runnable {
        private final String groupId;
        private final String consumerId;
        private final String topic;
        private final Map<Integer, TopicPartition> partitions;
        private final ConsumerGroupManager groupManager;
        private volatile boolean running = true;

        ConsumerRunner(String groupId, String consumerId, String topic,
                      Map<Integer, TopicPartition> partitions, ConsumerGroupManager groupManager) {
            this.groupId = groupId;
            this.consumerId = consumerId;
            this.topic = topic;
            this.partitions = partitions;
            this.groupManager = groupManager;
        }

        @Override
        public void run() {
            log.info("Consumer {} starting for topic {}", consumerId, topic);
            while (running) {
                for (Map.Entry<Integer, TopicPartition> entry : partitions.entrySet()) {
                    long offset = groupManager.getCommittedOffset(groupId, consumerId, topic, entry.getKey());
                    List<LogSegment.Record> records = entry.getValue().read(offset, 10);

                    for (LogSegment.Record record : records) {
                        log.debug("Consumed {} from {}-{}", record.offset(), topic, entry.getKey());
                    }

                    if (!records.isEmpty()) {
                        long newOffset = records.get(records.size() - 1).offset() + 1;
                        groupManager.commitOffset(groupId, consumerId, topic, entry.getKey(), newOffset);
                    }
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

        void stop() {
            running = false;
        }
    }
}