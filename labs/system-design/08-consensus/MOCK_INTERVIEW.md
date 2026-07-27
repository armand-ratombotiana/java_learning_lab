# Mock Interview: Consensus

> System Design Mock Interview — 45-minute session

---

## Setup

**Role**: Distributed Systems Architect Interviewer  
**Candidate Level**: Senior Staff Engineer (L6)  
**Problem**: Implement a fault-tolerant consensus algorithm for a configuration service.

---

## Transcript

**Interviewer**: "We need a configuration service that stores cluster-wide settings. Every node must agree on the configuration. If a node fails, the cluster should still function. Design this using a consensus algorithm."

**Candidate**: "This is the exact use case for the Raft consensus algorithm. It's designed for managing a replicated log with strong consistency. The configuration is the log — each change is a log entry. A cluster of 3 or 5 nodes runs Raft to agree on the current config."

**Interviewer**: "Walk me through Raft's leader election."

**Candidate**: "Nodes start as followers. They expect periodic heartbeats from the leader. If a follower doesn't receive a heartbeat within the election timeout (randomized 150-300ms), it becomes a candidate and starts an election. It votes for itself and requests votes from other nodes. If it gets majority (N/2+1), it becomes the leader. The randomized timeout prevents split votes."

**Interviewer**: "How does log replication work?"

**Candidate**: "The client submits a config change to the leader. The leader appends it to its log as an entry, then sends AppendEntries RPCs to followers. Followers append the entry. Once the leader knows the entry is on a majority of nodes, it commits the entry and applies it to its state machine. The leader then responds to the client."

**Interviewer**: "What happens if the leader crashes?"

**Candidate**: "A new leader is elected (see leader election above). The new leader takes over log replication. Log entries that were committed are preserved. Entries that were not replicated to a majority may be lost — that's the trade-off for availability. The new leader forces followers to replicate its log (truncating inconsistent entries)."

**Interviewer**: "How do you handle the change to the cluster membership itself?"

**Candidate**: "Joint consensus, as described in the Raft paper. The cluster transitions through an intermediate configuration where both the old and new configurations have authority. This prevents split-brain scenarios during membership changes. The transition: submit C_old,new → operate under joint consensus → submit C_new → operate under new config only."

**Interviewer**: "When would you use Paxos instead of Raft?"

**Candidate**: "Paxos is more abstract and flexible but harder to implement correctly. Raft is designed for understandability — it's the right choice for most systems. I'd consider Multi-Paxos for high-performance logging where you need lower latency (single round trip vs Raft's two rounds). But for a config service (low throughput, high consistency), Raft is ideal."

---

## Key Takeaways

- **Raft for configuration**: Understandable consensus for cluster config
- **Leader election**: Randomized timeouts prevent split votes
- **Log replication**: Entries committed after majority acknowledgment
- **Joint consensus**: Safe cluster membership changes
- **Raft vs Paxos**: Raft for understandability, Paxos for performance
