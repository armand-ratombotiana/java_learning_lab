package com.capstone.cache;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class GossipProtocol {
    private final String localNodeId;
    private final Map<String, NodeState> cluster = new ConcurrentHashMap<>();
    private final List<GossipMessage> messageLog = new CopyOnWriteArrayList<>();
    private final AtomicLong incarnation = new AtomicLong(0);
    private final long gossipIntervalMs;

    public enum NodeStatus { ALIVE, SUSPECT, DEAD, LEFT }

    public record NodeState(String nodeId, NodeStatus status, long incarnation, Instant lastSeen, Map<String, String> metadata) {
        public NodeState { metadata = metadata == null ? Map.of() : Map.copyOf(metadata); }
    }

    public record GossipMessage(String from, String to, List<NodeState> nodeStates, long timestamp) {
        public GossipMessage { nodeStates = List.copyOf(nodeStates); }
    }

    public GossipProtocol(String localNodeId, long gossipIntervalMs) {
        this.localNodeId = localNodeId;
        this.gossipIntervalMs = gossipIntervalMs;
        cluster.put(localNodeId, new NodeState(localNodeId, NodeStatus.ALIVE, 0, Instant.now(), Map.of()));
    }

    public void join(String nodeId) {
        cluster.put(nodeId, new NodeState(nodeId, NodeStatus.ALIVE, 0, Instant.now(), Map.of()));
        messageLog.add(new GossipMessage(localNodeId, nodeId, List.of(cluster.get(localNodeId)), System.currentTimeMillis()));
    }

    public void leave() {
        cluster.put(localNodeId, new NodeState(localNodeId, NodeStatus.LEFT,
            incarnation.incrementAndGet(), Instant.now(), Map.of()));
    }

    public void markSuspected(String nodeId) {
        cluster.computeIfPresent(nodeId, (k, v) ->
            new NodeState(nodeId, NodeStatus.SUSPECT, v.incarnation(), Instant.now(), v.metadata()));
    }

    public void markDead(String nodeId) {
        cluster.computeIfPresent(nodeId, (k, v) ->
            new NodeState(nodeId, NodeStatus.DEAD, v.incarnation(), Instant.now(), v.metadata()));
    }

    public void processGossip(GossipMessage message) {
        for (NodeState incoming : message.nodeStates()) {
            cluster.merge(incoming.nodeId(), incoming, (existing, _new) -> {
                if (_new.incarnation() > existing.incarnation() ||
                    (_new.incarnation() == existing.incarnation() && _new.status().ordinal() > existing.status().ordinal())) {
                    return _new;
                }
                return existing;
            });
        }
        messageLog.add(message);
    }

    public GossipMessage generateGossip(String targetNode) {
        return new GossipMessage(localNodeId, targetNode, List.copyOf(cluster.values()), System.currentTimeMillis());
    }

    public NodeState getNodeState(String nodeId) {
        return cluster.get(nodeId);
    }

    public List<NodeState> getAliveNodes() {
        return cluster.values().stream()
            .filter(n -> n.status() == NodeStatus.ALIVE)
            .collect(Collectors.toList());
    }

    public List<NodeState> getAllNodes() { return List.copyOf(cluster.values()); }

    public List<GossipMessage> getMessageLog() { return List.copyOf(messageLog); }

    public int clusterSize() { return cluster.size(); }

    public int aliveCount() { return (int) cluster.values().stream().filter(n -> n.status() == NodeStatus.ALIVE).count(); }

    public void clear() { cluster.clear(); messageLog.clear(); cluster.put(localNodeId, new NodeState(localNodeId, NodeStatus.ALIVE, 0, Instant.now(), Map.of())); }
}
