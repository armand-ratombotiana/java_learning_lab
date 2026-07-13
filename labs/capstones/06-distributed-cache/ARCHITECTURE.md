# Distributed Cache Architecture

## 🏛️ System Design Overview
A distributed cache is a fast, in-memory data store that scales horizontally across multiple nodes. It is used to reduce database load and improve application latency.

### 1. The Storage Engine
The core of each cache node is an in-memory Key-Value store.
- We use Java's `ConcurrentHashMap` for O(1) thread-safe reads and writes.
- **Expiration**: Each key has a Time-To-Live (TTL). A background thread periodically scans and removes expired keys to prevent memory exhaustion.

### 2. Eviction Policy (LRU)
When the cache reaches its memory limit, it must evict old data to make room for new data.
- We implement a thread-safe **Least Recently Used (LRU)** policy using a Doubly-Linked List combined with the `ConcurrentHashMap`.

### 3. Data Distribution (Consistent Hashing)
To scale beyond a single node's memory capacity, data is partitioned across multiple nodes.
- A **Consistent Hash Ring** is used to determine which node owns which key.
- **Virtual Nodes** are used to ensure an even distribution of keys across physical nodes.

### 4. High Availability (Replication)
If a node crashes, its data is lost. To prevent this, data is replicated.
- Each key is written to its primary node on the hash ring, and asynchronously replicated to the next $N$ nodes on the ring (where $N$ is the replication factor).
- If the primary node dies, the next node on the ring automatically takes over ownership of the keys.

## 📊 Component Diagram

```mermaid
graph TD
    Client[Client Application] --> Router[Cache Client Router]
    
    Router --> |Hash(Key)| Ring[Consistent Hash Ring]
    
    Ring --> NodeA[Cache Node A]
    Ring --> NodeB[Cache Node B]
    Ring --> NodeC[Cache Node C]
    
    NodeA --> |Replicate| NodeB
    NodeB --> |Replicate| NodeC
    NodeC --> |Replicate| NodeA
```