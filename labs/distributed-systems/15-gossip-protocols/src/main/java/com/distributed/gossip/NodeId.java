package com.distributed.gossip;

import java.net.InetSocketAddress;
import java.util.Objects;

public record NodeId(String id, InetSocketAddress address) implements Comparable<NodeId> {
    public NodeId {
        Objects.requireNonNull(id);
        Objects.requireNonNull(address);
    }

    @Override
    public int compareTo(NodeId other) {
        return this.id.compareTo(other.id);
    }

    @Override
    public String toString() {
        return id + "@" + address;
    }
}
