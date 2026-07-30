# Lab 04: Consistent Hashing

## Overview
Master consistent hashing for distributed systems: ring-based hashing, virtual nodes, replication, sharding, and dynamic cluster management.

## Core Concepts

| Concept | Purpose | Implementation |
|---------|---------|---------------|
| **Hash Ring** | Distribute keys across nodes | Sorted ring of hash values |
| **Virtual Nodes** | Improve distribution balance | Multiple ring entries per node |
| **Replication** | Fault tolerance | Keys replicated to N successors |
| **Sharding** | Horizontal partitioning | Each node owns a ring segment |

## Learning Objectives
- Implement a consistent hash ring with virtual nodes
- Compare distribution quality with/without virtual nodes
- Implement replication and read repair
- Design a sharded cache cluster
- Handle node addition/removal with minimal rehashing

## When to Use Consistent Hashing
- Distributed caches (Redis Cluster, Memcached)
- Database sharding (Cassandra, DynamoDB)
- Load balancer affinity
- Content delivery networks (CDNs)
