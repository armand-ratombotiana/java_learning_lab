# 21 - Hazelcast (In-Memory Data Grid)

In-memory data grid and distributed caching with Hazelcast concepts. Covers cluster formation, distributed map operations, entry listeners, distributed locking, cache topologies (near cache, partitioned, replicated), and data replication across nodes.

## Prerequisites

- Java 11+
- Maven 3.x

## Key Concepts

- Cluster formation: node discovery and joining
- Distributed Map: put/get across cluster nodes, data replication
- Entry listeners for cache event notification
- Distributed locks for concurrency control
- Cache topologies: near cache (local read-only copy), partitioned (data split across nodes), replicated (full copy on every node)
- Entry processors for data transformation

## Module Structure

- `01-caching/` - Hazelcast distributed caching simulation

## Learning Objectives

- Set up a distributed data grid with cluster nodes
- Perform distributed map operations with replication
- Implement distributed locking and cache event listeners

## Estimated Time

- 2-3 hours

## How to Build

```bash
cd 21-hazelcast
mvn clean package
```

Run the lab:

```bash
cd 01-caching
mvn compile exec:java -Dexec.mainClass="com.learning.hazelcast.Lab"
```
