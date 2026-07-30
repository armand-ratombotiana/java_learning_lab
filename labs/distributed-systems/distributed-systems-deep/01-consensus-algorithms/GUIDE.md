# Consensus Algorithms — Deep Dive Guide

## Raft

Raft divides consensus into three sub-problems:
1. **Leader election**: followers become candidates with randomized election timeouts
2. **Log replication**: leader appends entries, replicates to majority, commits
3. **Safety**: Leader Completeness Property — a committed entry appears in all future leaders' logs

**Terms**: time divided into terms of arbitrary length, each with at most one leader.

**Quorum**: majority of nodes (N/2 + 1)

**Commit semantics**: leader commits when entry is replicated to majority.

## Paxos

- **Basic Paxos**: three phases (Prepare/Promise, Accept/Accepted, Learn)
- **Multi-Paxos**: stable leader to skip Prepare phase for subsequent instances
- Distinguished proposer (leader) per epoch

## Zab

- ZooKeeper's atomic broadcast protocol
- Uses epoch numbers similar to Raft terms
- Leader proposes transactions; followers ack
- Commit happens when quorum acks

## Viewstamped Replication (VR)

- Non-leader-based consensus
- Views (similar to Raft terms)
- Primary processes operations; backups accept

## Comparison

| Protocol | Leader-based | Election | Log Replication |
|----------|-------------|----------|-----------------|
| Raft     | Yes         | Randomized timeouts | AppendEntries RPC |
| Paxos    | Yes (Multi) | Prepare/Promise | Accept RPC |
| Zab      | Yes         | Fast leader election | Proposal + Ack |
| VR       | Yes (Primary)| View changes | Normal operation |