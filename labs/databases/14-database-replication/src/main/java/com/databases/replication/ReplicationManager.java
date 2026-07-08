package com.databases.replication;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class ReplicationManager {
    private final Map<String, ReplicaNode> nodes = new ConcurrentHashMap<>();
    private final ReplicationStrategy strategy;
    private final ScheduledExecutorService healthCheck = Executors.newSingleThreadScheduledExecutor();

    public enum ReplicationStrategy { SYNCHRONOUS, ASYNCHRONOUS, QUORUM }

    public static class ReplicaNode {
        private final String nodeId;
        private volatile boolean leader;
        private volatile boolean healthy = true;
        private final AtomicLong replicationLag = new AtomicLong(0);
        private final AtomicLong lastHeartbeat = new AtomicLong(System.currentTimeMillis());

        public ReplicaNode(String id, boolean isLeader) { this.nodeId = id; this.leader = isLeader; }
        public String getNodeId() { return nodeId; }
        public boolean isLeader() { return leader; }
        public boolean isHealthy() { return healthy; }
        public long getReplicationLag() { return replicationLag.get(); }
        public void setLeader(boolean l) { this.leader = l; }
        public void setHealthy(boolean h) { this.healthy = h; }
        public void updateReplicationLag(long lag) { replicationLag.set(lag); }
        public void heartbeat() { lastHeartbeat.set(System.currentTimeMillis()); }
        public long getLastHeartbeat() { return lastHeartbeat.get(); }
    }

    public ReplicationManager(ReplicationStrategy strategy) {
        this.strategy = strategy;
        healthCheck.scheduleAtFixedRate(this::checkHealth, 5, 5, TimeUnit.SECONDS);
    }

    public void addNode(String nodeId, boolean isLeader) {
        nodes.put(nodeId, new ReplicaNode(nodeId, isLeader));
    }

    public ReplicaNode getLeader() {
        return nodes.values().stream().filter(ReplicaNode::isLeader).findFirst().orElseThrow();
    }

    public List<ReplicaNode> getFollowers() {
        return nodes.values().stream().filter(n -> !n.isLeader()).toList();
    }

    public boolean replicate(String data) {
        var leader = getLeader();
        if (!leader.isHealthy()) return false;
        var followers = getFollowers();
        int success = 0;
        for (var f : followers) {
            try {
                if (replicateToNode(f, data)) success++;
            } catch (Exception e) { f.setHealthy(false); }
        }
        return switch (strategy) {
            case SYNCHRONOUS -> success == followers.size();
            case QUORUM -> success >= (followers.size() / 2) + 1;
            case ASYNCHRONOUS -> true;
        };
    }

    private boolean replicateToNode(ReplicaNode node, String data) {
        long start = System.nanoTime();
        try { Thread.sleep(1); } catch (InterruptedException e) { return false; }
        node.updateReplicationLag(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
        node.heartbeat();
        return true;
    }

    public void promoteFollower(String nodeId) {
        var node = nodes.get(nodeId);
        if (node == null || node.isLeader()) return;
        var oldLeader = getLeader();
        oldLeader.setLeader(false);
        node.setLeader(true);
    }

    public boolean handleLeaderFailure() {
        var healthyFollowers = getFollowers().stream().filter(ReplicaNode::isHealthy).toList();
        if (healthyFollowers.isEmpty()) return false;
        promoteFollower(healthyFollowers.get(0).getNodeId());
        return true;
    }

    private void checkHealth() {
        var now = System.currentTimeMillis();
        for (var n : nodes.values()) {
            if (now - n.getLastHeartbeat() > 15000) n.setHealthy(false);
        }
        var leader = nodes.values().stream().filter(ReplicaNode::isLeader).findFirst();
        if (leader.isPresent() && !leader.get().isHealthy()) handleLeaderFailure();
    }

    public ReplicaNode getNode(String id) { return nodes.get(id); }
    public Collection<ReplicaNode> getAllNodes() { return nodes.values(); }
    public ReplicationStrategy getStrategy() { return strategy; }
    public void shutdown() { healthCheck.shutdown(); }
}
