# Paxos vs. Raft Internals

## 🔬 Paxos Mechanics (Basic Paxos)
Paxos is designed to agree on a single value in two phases:

1. **Phase 1: Prepare**
   - A Proposer picks a unique proposal number $N$ and sends a `Prepare(N)` to a majority of Acceptors.
   - If an Acceptor receives a `Prepare(N)` with $N$ greater than any previous proposal, it promises not to accept any future proposals numbered less than $N$.

2. **Phase 2: Accept**
   - If the Proposer receives responses from a majority, it sends an `Accept(N, value)` request.
   - Acceptors accept the proposal unless they've already promised a higher number.

**Problem**: If two proposers keep increasing $N$ and sending Prepare requests, they can block each other forever (Livelock).

## 🏢 Raft Mechanics
Raft simplifies this by enforcing a strong Leader.

1. **Leader Election**: Uses randomized timeouts to elect a leader. Only one node can be leader in a given "Term".
2. **Log Replication**: All writes go through the leader. The leader appends to its log and replicates to followers.
3. **Safety**: A node can only be elected leader if its log is at least as up-to-date as the majority. This guarantees that a committed entry is never lost.

## ⚖️ Key Differences in implementation
- **Log Gaps**: Paxos allows gaps in the log (out-of-order commits). Raft requires the log to be strictly sequential and contiguous.
- **Membership Changes**: Raft includes a specific mechanism for changing cluster membership (adding/removing nodes). In Paxos, this is usually handled by a higher-level protocol.
- **Read Requests**: In Raft, reads usually go to the leader to ensure consistency. In Paxos variants, reads can sometimes be more decentralized.