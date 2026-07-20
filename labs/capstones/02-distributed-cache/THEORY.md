# Distributed Cache - Theory

## Core Concepts

### 1. ConsistentHashRing distributes keys across nodes using virtual nodes for balanced distribution. Uses MD5 hashing and supports N-node lookups for replication routing.

ConsistentHashRing distributes keys across nodes using virtual nodes for balanced distribution. Uses MD5 hashing and supports N-node lookups for replication routing.

Key implementation details: the system uses concurrent data structures (ConcurrentHashMap, CopyOnWriteArrayList) for thread safety. Error handling follows fail-fast principles with IllegalArgumentException for validation and IllegalStateException for invalid operations. All components are designed for testability with clear interfaces and dependency injection.

### 2. EvictionPolicy provides three pluggable strategies: LRU evicts least recently accessed entries using LinkedHashMap access-order, LFU tracks access frequencies with AtomicLong counters, and TTL expires entries based on configurable time-to-live.

EvictionPolicy provides three pluggable strategies: LRU evicts least recently accessed entries using LinkedHashMap access-order, LFU tracks access frequencies with AtomicLong counters, and TTL expires entries based on configurable time-to-live.

Key implementation details: the system uses concurrent data structures (ConcurrentHashMap, CopyOnWriteArrayList) for thread safety. Error handling follows fail-fast principles with IllegalArgumentException for validation and IllegalStateException for invalid operations. All components are designed for testability with clear interfaces and dependency injection.

### 3. PartitionManager maps keys to partitions using hash partitioning and assigns primary/replica nodes using the consistent hash ring. Supports dynamic rebalancing.

PartitionManager maps keys to partitions using hash partitioning and assigns primary/replica nodes using the consistent hash ring. Supports dynamic rebalancing.

Key implementation details: the system uses concurrent data structures (ConcurrentHashMap, CopyOnWriteArrayList) for thread safety. Error handling follows fail-fast principles with IllegalArgumentException for validation and IllegalStateException for invalid operations. All components are designed for testability with clear interfaces and dependency injection.

### 4. CacheClient provides a simple get/put/delete API with automatic node routing, hit/miss statistics tracking, and optional eviction policy integration.

CacheClient provides a simple get/put/delete API with automatic node routing, hit/miss statistics tracking, and optional eviction policy integration.

Key implementation details: the system uses concurrent data structures (ConcurrentHashMap, CopyOnWriteArrayList) for thread safety. Error handling follows fail-fast principles with IllegalArgumentException for validation and IllegalStateException for invalid operations. All components are designed for testability with clear interfaces and dependency injection.

### 5. ReplicationManager handles leader/follower replication with versioned entries, pending replication tracking, and acknowledgment-based consistency. Supports leader election via node ID comparison.

ReplicationManager handles leader/follower replication with versioned entries, pending replication tracking, and acknowledgment-based consistency. Supports leader election via node ID comparison.

Key implementation details: the system uses concurrent data structures (ConcurrentHashMap, CopyOnWriteArrayList) for thread safety. Error handling follows fail-fast principles with IllegalArgumentException for validation and IllegalStateException for invalid operations. All components are designed for testability with clear interfaces and dependency injection.

### 6. GossipProtocol 

GossipProtocol implements cluster membership with ALIVE, SUSPECT, DEAD, and LEFT states. Uses gossip messages to propagate node state changes across the cluster.

Key implementation details: the system uses concurrent data structures (ConcurrentHashMap, CopyOnWriteArrayList) for thread safety. Error handling follows fail-fast principles with IllegalArgumentException for validation and IllegalStateException for invalid operations. All components are designed for testability with clear interfaces and dependency injection.

## Design Principles

The architecture follows SOLID principles with single-responsibility classes, open-for-extension design, and dependency inversion. Thread safety is achieved through immutable records, atomic operations, and synchronized blocks where necessary. All components include comprehensive JUnit 5 test coverage with parameterized tests and edge case handling.

## Performance Characteristics

Operations are O(1) for hash-based lookups, O(log n) for tree-based structures, and O(n) for linear scans. Memory usage is proportional to the number of stored elements with per-entry overhead for indexing structures. Concurrent access patterns use lock striping and non-blocking algorithms where possible to minimize contention.

