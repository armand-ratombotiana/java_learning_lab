# Distributed Cache

A mini distributed cache system built from scratch in Java, featuring consistent hashing, multiple eviction policies, partitioned storage, client library, replication management, and gossip-based cluster membership protocol.

## Architecture Overview

```
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│  Cache       │    │  Cache       │    │  Cache       │
│  Node 1      │    │  Node 2      │    │  Node 3      │
│  (Primary)   │    │  (Replica)   │    │  (Replica)   │
└──────┬───────┘    └──────┬───────┘    └──────┬───────┘
       │                   │                   │
       └───────────────────┼───────────────────┘
                           │
              ┌────────────┴────────────┐
              │   Consistent Hash Ring  │
              │   + Partition Manager   │
              └─────────────────────────┘
```

## Features

- **ConsistentHashRing**: Virtual node-based consistent hashing with MD5 hash function, node add/remove, N-node lookup for replication
- **EvictionPolicy**: Pluggable LRU (access-order), LFU (frequency-based), and TTL (time-based) policies
- **PartitionManager**: Key-to-partition mapping, per-node partition listing, rebalancing
- **CacheClient**: Put/get/delete operations with hit/miss statistics tracking
- **ReplicationManager**: Leader/follower replication with versioned entries, acknowledgment tracking
- **GossipProtocol**: Node join/leave, suspected/dead detection, state propagation via gossip messages

## Eviction Policies

| Policy | Strategy | Use Case |
|--------|----------|----------|
| LRU | Evict least recently accessed | General-purpose caching |
| LFU | Evict least frequently accessed | Stable access patterns |
| TTL | Evict after time-to-live expires | Time-sensitive data |

## Usage

```java
var ring = new ConsistentHashRing(150, ConsistentHashRing::md5Hash);
ring.addNode(new CacheNode("node1", "localhost", 8081, true));
ring.addNode(new CacheNode("node2", "localhost", 8082, true));

var client = new CacheClient(ring, new LRUPolicy<>(1000, 0));
client.put("key1", "value1".getBytes());
var val = client.get("key1"); // Optional<byte[]>

var gossip = new GossipProtocol("node1", 1000);
gossip.join("node2");
gossip.join("node3");
```
