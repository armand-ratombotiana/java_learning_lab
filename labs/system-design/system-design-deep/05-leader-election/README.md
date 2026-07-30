# Lab 05: Leader Election

## Overview
Master leader election algorithms for distributed systems: Bully algorithm, Raft consensus, ZooKeeper/Etcd coordination, lease-based leadership, and fencing tokens.

## Algorithms

| Algorithm | Model | Fault Tolerance | Use Case |
|-----------|-------|----------------|----------|
| **Bully** | Synchronous, node IDs | Basic crash failure | Small clusters (<20 nodes) |
| **Raft** | Consensus, log replication | Crash + partition | Consistent state machines |
| **ZooKeeper** | Sequential ephemeral znodes | Crash + partition | Coordination services |
| **Etcd** | Raft-based key-value store | Crash + partition | Cloud-native coordination |
| **Lease-based** | Time-bound leadership | Crash tolerance | When clock skew is bounded |

## Learning Objectives
- Implement the Bully leader election algorithm
- Understand Raft's leader election phase (terms, votes, log replication)
- Implement ZooKeeper ephemeral znode-based leader election
- Design lease-based leadership with fencing tokens
- Analyze fault tolerance properties of each approach
