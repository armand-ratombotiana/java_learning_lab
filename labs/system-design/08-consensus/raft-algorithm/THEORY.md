# Raft Consensus Algorithm Theory & Intuition

## 💡 The Distributed Consensus Problem
Imagine you have a distributed database cluster with 3 nodes (A, B, and C). A client sends a write request: "Set X = 5".
If the client sends it to Node A, how do we guarantee that Node B and Node C also agree that X is 5? What if Node C's network connection drops exactly as the message is sent? What if Node A crashes immediately after receiving the message but before telling the others?

This is the problem of **Distributed Consensus**: Getting a group of independent machines to agree on a single value or a sequence of state transitions, even if some of the machines fail or the network is unreliable.

## 🏛️ The Predecessor: Paxos
For decades, the industry standard for consensus was an algorithm called **Paxos**.
- **The Problem**: Paxos is notoriously difficult to understand, and even more difficult to implement correctly in software. The original paper was considered so opaque that the author had to write a follow-up paper called "Paxos Made Simple" (which was still extremely complex).

## 🚀 The Solution: Raft
In 2014, researchers at Stanford created **Raft**. The explicit, primary design goal of Raft was **Understandability**. It was designed to be easily taught to students and easily implemented by engineers, without sacrificing any mathematical correctness.

Raft achieves consensus by breaking the problem down into three distinct sub-problems:
1. **Leader Election**: A cluster must always have exactly one Leader.
2. **Log Replication**: The Leader accepts commands from clients, appends them to its log, and replicates them to the Followers.
3. **Safety**: Ensuring that if any server has applied a particular log entry to its state machine, no other server may apply a different command for the same log index.

## 🎭 The Three States of a Raft Node
At any given time, a Raft node is in one of three states:
1. **Follower**: The passive state. It only responds to requests from Leaders or Candidates. If it doesn't hear from a Leader for a while, it becomes a Candidate.
2. **Candidate**: The active state attempting to become a Leader. It requests votes from all other nodes.
3. **Leader**: The boss. It handles all client requests and continuously sends "Heartbeat" messages to all Followers to maintain its authority and replicate logs.