package com.databases.cassandra;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TunableConsistency {
    private final Map<String, NodeState> nodes = new ConcurrentHashMap<>();
    private final int replicationFactor;

    public enum ConsistencyLevel { ONE, QUORUM, ALL, LOCAL_QUORUM, EACH_QUORUM, ANY }

    public static class NodeState {
        private final String id;
        private volatile boolean up = true;
        private final AtomicLong lastWriteTime = new AtomicLong();
        public NodeState(String id) { this.id = id; }
        public boolean isUp() { return up; }
        public void setDown() { this.up = false; }
        public void setFailed() { this.up = false; }
    }

    public TunableConsistency(int replicationFactor) {
        this.replicationFactor = replicationFactor;
    }

    public void addNode(String id) { nodes.put(id, new NodeState(id)); }

    public boolean write(String key, String value, ConsistencyLevel cl) {
        int required = switch (cl) {
            case ONE -> 1;
            case QUORUM -> (replicationFactor / 2) + 1;
            case ALL -> replicationFactor;
            case LOCAL_QUORUM -> (replicationFactor / 2) + 1;
            case EACH_QUORUM -> (replicationFactor / 2) + 1;
            case ANY -> 1;
        };

        long count = nodes.values().stream().filter(NodeState::isUp).limit(replicationFactor).count();
        return count >= required;
    }

    public String read(String key, ConsistencyLevel cl) {
        int required = switch (cl) {
            case ONE -> 1;
            case QUORUM -> (replicationFactor / 2) + 1;
            case ALL -> replicationFactor;
            case LOCAL_QUORUM -> (replicationFactor / 2) + 1;
            case EACH_QUORUM -> (replicationFactor / 2) + 1;
            case ANY -> 1;
        };

        long available = nodes.values().stream().filter(NodeState::isUp).count();
        if (available < required) throw new RuntimeException("Insufficient replicas: " + available + "/" + required);
        return "value_for_" + key;
    }

    public int getRF() { return replicationFactor; }
    public void markNodeFailed(String id) {
        var n = nodes.get(id);
        if (n != null) n.setFailed();
    }
}
