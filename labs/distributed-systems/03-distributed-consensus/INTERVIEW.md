# Distributed Consensus - Interview Preparation

> Key interview questions about distributed consensus algorithms (Paxos, Raft, Zab).

---

## Core Interview Questions

### Q1: Explain Paxos in detail (both phases)
**Answer**: Paxos has two phases. Phase 1 (Prepare): Proposer sends prepare(n) to acceptors. Acceptors promise to never accept proposals < n and reply with the highest-numbered proposal they've accepted. Phase 2 (Accept): If proposer gets majority promises, it sends accept(n, value) where value is the value from the highest accepted proposal (or its own if none). Acceptor accepts if n >= promised number.

### Q2: How is Raft different from Paxos?
**Answer**: Raft is more understandable. Key differences: Raft has strong leader, log entries flow leader->followers only, leader election is explicit (timeout-based), log matching ensures consistency, safety guaranteed by election restriction (only candidate with up-to-date log can become leader). Raft also has explicit cluster membership changes.

### Q3: Explain Raft leader election
**Answer**: Followers have randomized election timeouts (150-300ms). When timeout expires, follower becomes candidate, increments term, votes for self, sends RequestVote to peers. If candidate gets majority votes, becomes leader. Leader sends empty AppendEntries (heartbeats) to maintain authority. If candidate doesn't win, reverts to follower if sees higher term.

### Q4: How does ZooKeeper's Zab differ from Raft?
**Answer**: Zab (ZooKeeper Atomic Broadcast) focuses on ordered broadcast, not consensus on a single value. Zab uses epoch-based leader election. Key difference: Raft uses randomized timeouts; Zab uses lexicographic ordering of peer IDs. Zab guarantees total order of messages; Raft guarantees linearizability through leader.

### Q5: What happens if a leader fails in Raft?
**Answer**: Followers detect leader failure via election timeout. New election starts. The candidate with the most up-to-date log wins. Old leader's stale entries are overridden. New leader completes log replication for committed entries.

## Company-Specific Focus

| Company | Consensus Focus |
|---------|-----------------|
| Google | "How does Spanner use Paxos + TrueTime?" |
| Confluent | "Explain KRaft - Raft-based consensus in Kafka" |
| Apache | "Zab vs Raft - which is better and when?" |
| Amazon | "Does DynamoDB use consensus? (No - quorum-based)" |

## LeetCode Connections

| Problem | # | Consensus Concept |
|---------|---|-----------------|
| Course Schedule II | 210 | Topological ordering (log ordering) |
| Redundant Connection | 684 | Cycle = split brain detection |
| Accounts Merge | 721 | Accept quorum |
| Minimum Height Trees | 310 | Leader election |

## System Design Connections

- **Design ZooKeeper**: Consensus for coordination
- **Design etcd**: Raft-based key-value store
- **Design a Lock Service**: Consensus for leader election
- **Design a Replicated Log**: Consensus for ordering

> **Key Insight**: You must be able to trace Raft leader election step-by-step and Paxos prepare/accept flow. Draw timeline diagrams during interviews.