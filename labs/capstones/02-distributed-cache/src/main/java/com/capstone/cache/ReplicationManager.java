package com.capstone.cache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class ReplicationManager {
    private final String localNodeId;
    private final ConsistentHashRing ring;
    private final Map<String, List<String>> replicationLog = new ConcurrentHashMap<>();
    private final AtomicLong replicationId = new AtomicLong(0);
    private final int replicationFactor;

    public ReplicationManager(String localNodeId, ConsistentHashRing ring, int replicationFactor) {
        this.localNodeId = localNodeId;
        this.ring = ring;
        this.replicationFactor = replicationFactor;
    }

    public record ReplicaEntry(String key, byte[] value, long timestamp, long version) {}

    public void replicatePut(String key, byte[] value) {
        List<ConsistentHashRing.CacheNode> replicas = ring.getNodes(key, replicationFactor);
        long version = replicationId.incrementAndGet();
        long timestamp = System.currentTimeMillis();
        for (ConsistentHashRing.CacheNode replica : replicas) {
            if (!replica.nodeId().equals(localNodeId)) {
                replicationLog.computeIfAbsent(replica.nodeId(), k -> new CopyOnWriteArrayList<>())
                    .add(key + "@" + version);
            }
        }
    }

    public void replicateDelete(String key) {
        List<ConsistentHashRing.CacheNode> replicas = ring.getNodes(key, replicationFactor);
        long version = replicationId.incrementAndGet();
        for (ConsistentHashRing.CacheNode replica : replicas) {
            if (!replica.nodeId().equals(localNodeId)) {
                replicationLog.computeIfAbsent(replica.nodeId(), k -> new CopyOnWriteArrayList<>())
                    .add("DELETE:" + key + "@" + version);
            }
        }
    }

    public List<String> getPendingReplications(String nodeId) {
        return List.copyOf(replicationLog.getOrDefault(nodeId, List.of()));
    }

    public void acknowledgeReplication(String nodeId, String entryId) {
        List<String> log = replicationLog.get(nodeId);
        if (log != null) log.remove(entryId);
    }

    public void leaderElection(List<String> nodeIds) {
        String leader = nodeIds.stream().min(String::compareTo).orElseThrow();
        if (leader.equals(localNodeId)) {
            initiateLeaderSync(nodeIds);
        }
    }

    private void initiateLeaderSync(List<String> nodeIds) {
        for (String node : nodeIds) {
            if (!node.equals(localNodeId)) {
                replicationLog.computeIfAbsent(node, k -> new CopyOnWriteArrayList<>())
                    .add("SYNC@" + System.currentTimeMillis());
            }
        }
    }

    public int getPendingCount() {
        return replicationLog.values().stream().mapToInt(List::size).sum();
    }

    public int getReplicationFactor() { return replicationFactor; }
}
