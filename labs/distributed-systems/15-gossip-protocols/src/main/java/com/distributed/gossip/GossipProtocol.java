package com.distributed.gossip;

import java.util.List;

public interface GossipProtocol {
    void start();
    void stop();
    void broadcast(byte[] data);
    void onMessage(NodeId from, byte[] data);
    List<NodeId> getKnownPeers();
}
