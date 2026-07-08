# 14 - Distributed Locks

## Overview
Distributed locks coordinate access to shared resources across multiple nodes. This lab covers Redis Redlock, ZooKeeper locks, lease-based locking, and fencing tokens for safe distributed mutual exclusion.

## Prerequisites
- Java 21+
- Maven 3.8+
- Understanding of distributed systems
- Familiarity with concurrency concepts

## Topics Covered
- Redis Redlock algorithm
- ZooKeeper ephemeral sequential znodes
- Lease-based locking with TTL
- Fencing tokens for resource protection
- Lock reentrancy in distributed systems
- Deadlock detection and recovery
- Split-brain scenarios and mitigation
- Comparison of lock providers

## Package Structure
- com.distributed.distributedlocks â€” Core implementations
  - DistributedLock.java â€” Lock interface
  - RedisLock.java â€” Redis-based distributed lock
  - ZooKeeperLock.java â€” ZooKeeper-based lock
  - FencingToken.java â€” Fencing token management
  - LeaseManager.java â€” Lease-based lock management
