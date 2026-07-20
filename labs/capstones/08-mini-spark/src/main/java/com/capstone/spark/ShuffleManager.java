package com.capstone.spark;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class ShuffleManager {
    private final Map<Integer, Map<Integer, List<ShuffleBlock>>> shuffleData = new ConcurrentHashMap<>();
    private final int numPartitions;

    public record ShuffleBlock(int partitionId, Object key, Object value, long size) {}

    public ShuffleManager(int numPartitions) {
        this.numPartitions = numPartitions;
    }

    public int getPartition(Object key) {
        return Math.abs(key.hashCode() % numPartitions);
    }

    public <K, V> Map<Integer, List<Map.Entry<K, V>>> partitionByKey(Map<K, V> data) {
        Map<Integer, List<Map.Entry<K, V>>> partitions = new ConcurrentHashMap<>();
        for (int i = 0; i < numPartitions; i++) partitions.put(i, new CopyOnWriteArrayList<>());
        data.entrySet().parallelStream().forEach(e -> {
            int p = getPartition(e.getKey());
            partitions.get(p).add(e);
        });
        return partitions;
    }

    public <K, V> void writeShuffle(int shuffleId, int mapId, Map<K, V> data) {
        Map<Integer, List<ShuffleBlock>> blocks = shuffleData.computeIfAbsent(shuffleId, k -> new ConcurrentHashMap<>());
        Map<Integer, List<Map.Entry<K, V>>> partitioned = partitionByKey(data);
        for (Map.Entry<Integer, List<Map.Entry<K, V>>> entry : partitioned.entrySet()) {
            int reducePartition = entry.getKey();
            List<ShuffleBlock> shuffleBlocks = entry.getValue().stream()
                .map(e -> new ShuffleBlock(reducePartition, e.getKey(), e.getValue(), 64))
                .collect(Collectors.toList());
            blocks.computeIfAbsent(mapId, k -> new CopyOnWriteArrayList<>()).addAll(shuffleBlocks);
        }
    }

    @SuppressWarnings("unchecked")
    public <K, V> Map<K, List<V>> readShuffle(int shuffleId, int reducePartition) {
        Map<Integer, List<ShuffleBlock>> blocks = shuffleData.get(shuffleId);
        if (blocks == null) return Map.of();
        Map<K, List<V>> result = new ConcurrentHashMap<>();
        blocks.values().stream()
            .flatMap(List::stream)
            .filter(b -> b.partitionId() == reducePartition)
            .forEach(b -> result.computeIfAbsent((K) b.key(), k -> new CopyOnWriteArrayList<>())
                .add((V) b.value()));
        return result;
    }

    public int getNumPartitions() { return numPartitions; }

    public void clear() { shuffleData.clear(); }
}
