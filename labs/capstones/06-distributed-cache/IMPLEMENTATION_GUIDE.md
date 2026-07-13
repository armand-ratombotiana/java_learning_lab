# Distributed Cache Implementation Guide

This guide outlines the step-by-step process to build the Distributed Cache Capstone in Java.

## 🛠️ Phase 1: The Local Cache Engine
1. **Core Storage**: Implement a `LocalCache` class wrapping a `ConcurrentHashMap<String, CacheEntry>`.
2. **TTL Expiration**:
   - Add an `expiresAt` timestamp to `CacheEntry`.
   - Create a background `ScheduledExecutorService` that runs every 1 second, scanning the map and removing entries where `expiresAt < now`.
3. **LRU Eviction**:
   - Integrate a doubly-linked list to track access order.
   - When the cache reaches `maxCapacity`, remove the tail of the list and delete the corresponding key from the map.

## 🕸️ Phase 2: The Consistent Hash Ring
1. **The Ring**: Implement a `HashRing` class using a `TreeMap<Long, NodeMetadata>`.
2. **Hashing**: Use MD5 to hash node IDs and place them on the ring.
3. **Virtual Nodes**: Add a configurable number of virtual nodes (e.g., 100) per physical node to ensure uniform data distribution.
4. **Routing**: Implement a `getNode(String key)` method that hashes the key, finds the `tailMap`, and returns the next available node on the ring.

## 🌐 Phase 3: The Network Protocol
1. **TCP Server**: Use Java NIO (`ServerSocketChannel` and `Selector`) to build a non-blocking TCP server.
2. **Wire Protocol**: Define a simple text-based protocol (similar to Redis RESP).
   - `SET <key> <value> <ttl_ms>`
   - `GET <key>`
   - `DEL <key>`
3. **Command Parsing**: Parse incoming bytes into command objects and execute them against the `LocalCache`.

## 💾 Phase 4: Replication & Cluster Management
1. **Gossip Protocol**: Implement a lightweight heartbeat mechanism where nodes ping each other to detect failures.
2. **Replication**:
   - When a `SET` command arrives at the primary node, it executes the command locally.
   - It then asynchronously forwards the `SET` command to the next $N$ nodes on the hash ring.
3. **Failover**: If the Hash Ring detects a node failure (via missed heartbeats), it removes the node from the ring. Subsequent `GET` requests will automatically route to the replica node.

## 🧪 Phase 5: Benchmarking
1. **JMH**: Write JMH benchmarks to measure the throughput (ops/sec) of the `LocalCache` under highly concurrent read/write loads.
2. **Cluster Test**: Spin up 3 instances of your cache on different ports. Write a client script that inserts 1,000,000 keys. Kill one instance and verify that no data is lost when querying the remaining two instances.