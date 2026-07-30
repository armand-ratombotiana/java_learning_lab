# Lab 02: Distributed Caching

## Overview
Master distributed caching strategies used in high-scale systems: cache-aside, read-through, write-through, write-behind, eviction policies, and distributed cache topology design.

## Caching Strategies

| Strategy | Read Miss Behavior | Write Behavior | Use Case |
|----------|-------------------|----------------|----------|
| **Cache-Aside** | App loads from DB, populates cache | App invalidates/updates cache | General purpose, most common |
| **Read-Through** | Cache loads from DB automatically | App writes to DB directly | Consistent read model |
| **Write-Through** | Cache loads from DB | App writes to cache, cache writes DB | Strong consistency |
| **Write-Behind** | Cache loads from DB | App writes to cache, async DB write | High write throughput |

## Eviction Policies
- **LRU** (Least Recently Used) — evicts oldest accessed item
- **LFU** (Least Frequently Used) — evicts least accessed item
- **FIFO** — evicts in insertion order
- **TTL** — eviction based on time
- **Random** — simple, predictable performance

## Distributed Cache Topologies
- **Client-side** — local cache in app process (Caffeine, Guava)
- **Sidecar** — co-located cache process per node
- **Centralized** — dedicated cache cluster (Redis, Memcached)
- **Hybrid** — L1 local + L2 centralized

## Learning Objectives
- Implement all four caching strategies
- Compare eviction policies with metrics
- Design a distributed Redis cluster topology
- Handle cache invalidation and consistency challenges
