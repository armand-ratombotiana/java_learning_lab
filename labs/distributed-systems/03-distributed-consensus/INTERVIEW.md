# Distributed Consensus: Interview Questions

## Q1: Explain Raft consensus in 2 minutes.
**A**: Raft elects a leader that handles all client requests. The leader replicates log entries to followers. When a majority acknowledges, the entry is committed and applied to the state machine. If the leader fails, a new election is triggered by timeout.

## Q2: How does Paxos handle multiple proposers?
**A**: Paxos uses prepare phase to establish a priority (proposal number). Only the highest-numbered proposal can proceed to the accept phase. If multiple proposers contend, no value gets majority and rounds restart.

## Q3: What's the minimum cluster size for Raft?
**A**: 3 nodes tolerates 1 failure. 5 nodes tolerates 2 failures. Even numbers don't help since majority is N/2+1.

## Q4: How do you handle split-brain?
**A**: Use quorum-based decisions. A node only acts as leader if it communicates with majority. Stale leaders discover they're out of term and step down.

## Q5: What's the performance bottleneck in Raft?
**A**: Leader is serialization point. All writes go through leader. Log replication consumes bandwidth. Disk sync adds latency.
