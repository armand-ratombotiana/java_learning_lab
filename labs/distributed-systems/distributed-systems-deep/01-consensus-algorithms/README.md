# 01 - Consensus Algorithms

## Topics Covered
- Raft (leader election, log replication, safety, commit semantics)
- Paxos (basic Paxos, Multi-Paxos)
- Zab (ZooKeeper Atomic Broadcast)
- Viewstamped Replication (VR)
- Quorums, log matching, leader completeness

## Goal
Understand how distributed consensus works and the key differences between Raft, Paxos, Zab, and VR.

## Exercises

1. Implement a Raft leader election simulation with randomized timeouts.
2. Simulate log replication across a 3-node cluster and handle a leader failure.
3. Trace Multi-Paxos prepare/accept phases and compare to Raft.
4. Implement log matching safety checks.