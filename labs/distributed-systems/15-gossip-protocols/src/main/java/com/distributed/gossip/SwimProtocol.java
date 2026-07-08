package com.distributed.gossip;

import java.util.*;
import java.util.concurrent.*;

public class SwimProtocol {
    private final GossipNode node;
    private final long suspicionTimeoutMs;
    private final int indirectProbeCount;
    private final Map<NodeId, Long> suspectTimestamps = new ConcurrentHashMap<>();
    private final Map<NodeId, Integer> missedPings = new ConcurrentHashMap<>();
    private final int pingThreshold;

    public SwimProtocol(GossipNode node, long suspicionTimeoutMs, int indirectProbeCount, int pingThreshold) {
        this.node = node;
        this.suspicionTimeoutMs = suspicionTimeoutMs;
        this.indirectProbeCount = indirectProbeCount;
        this.pingThreshold = pingThreshold;
    }

    public boolean probe(NodeId target) {
        GossipNode.MemberState state = node.getKnownPeers().stream()
            .filter(p -> p.equals(target))
            .findFirst()
            .map(p -> { return null; })
            .orElse(null);
        if (state == null && !node.getKnownPeers().contains(target)) {
            return false;
        }
        missedPings.merge(target, 1, Integer::sum);
        int missed = missedPings.getOrDefault(target, 0);
        if (missed >= pingThreshold) {
            suspectTimestamps.put(target, System.currentTimeMillis());
            return false;
        }
        return true;
    }

    public boolean isSuspect(NodeId nodeId) {
        Long ts = suspectTimestamps.get(nodeId);
        if (ts == null) return false;
        if (System.currentTimeMillis() - ts > suspicionTimeoutMs) {
            suspectTimestamps.remove(nodeId);
            return false;
        }
        return true;
    }

    public void clearSuspicion(NodeId nodeId) {
        suspectTimestamps.remove(nodeId);
        missedPings.remove(nodeId);
    }
}
