package com.capstone.cache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PartitionManager {
    private final int partitionCount;
    private final Map<Integer, Partition> partitions = new ConcurrentHashMap<>();
    private final ConsistentHashRing ring;
    private final AtomicInteger nextId = new AtomicInteger(0);

    public record Partition(int id, String primaryNode, Set<String> replicaNodes) {
        public Partition { replicaNodes = Set.copyOf(replicaNodes); }
    }

    public PartitionManager(int partitionCount, ConsistentHashRing ring) {
        this.partitionCount = partitionCount;
        this.ring = ring;
        for (int i = 0; i < partitionCount; i++) {
            partitions.put(i, new Partition(i, null, Set.of()));
        }
    }

    public int assignPartition(String key) {
        ConsistentHashRing.CacheNode node = ring.getNode(key).orElse(null);
        if (node == null) throw new IllegalStateException("No nodes available");
        int partitionId = Math.abs(key.hashCode() % partitionCount);
        Partition existing = partitions.get(partitionId);
        Set<String> replicas = new HashSet<>();
        for (ConsistentHashRing.CacheNode rn : ring.getNodes(key, 3)) {
            if (!rn.nodeId().equals(node.nodeId())) replicas.add(rn.nodeId());
        }
        partitions.put(partitionId, new Partition(partitionId, node.nodeId(), replicas));
        return partitionId;
    }

    public Optional<Partition> getPartition(int id) {
        return Optional.ofNullable(partitions.get(id));
    }

    public int getPartitionForKey(String key) {
        return Math.abs(key.hashCode() % partitionCount);
    }

    public List<Partition> getPartitionsForNode(String nodeId) {
        return partitions.values().stream()
            .filter(p -> nodeId.equals(p.primaryNode()) || p.replicaNodes().contains(nodeId))
            .toList();
    }

    public void rebalance() {
        for (int i = 0; i < partitionCount; i++) {
            Partition p = partitions.get(i);
            String dummyKey = "rebalance-key-" + i;
            ConsistentHashRing.CacheNode node = ring.getNode(dummyKey).orElse(null);
            if (node != null) {
                Set<String> replicas = new HashSet<>();
                for (ConsistentHashRing.CacheNode rn : ring.getNodes(dummyKey, 3)) {
                    if (!rn.nodeId().equals(node.nodeId())) replicas.add(rn.nodeId());
                }
                partitions.put(i, new Partition(i, node.nodeId(), replicas));
            }
        }
    }

    public Map<Integer, Partition> getAllPartitions() { return Map.copyOf(partitions); }

    public int getPartitionCount() { return partitionCount; }

    public void reset() { partitions.clear(); for (int i = 0; i < partitionCount; i++) partitions.put(i, new Partition(i, null, Set.of())); }
}
