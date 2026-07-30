# Consensus Algorithms — Interview Questions

## Beginner
1. What problem does consensus solve in distributed systems?
2. What is a quorum and why is it typically a majority?
3. What are the three sub-problems that Raft divides consensus into?

## Intermediate
4. Explain the Leader Completeness Property in Raft and how it's enforced.
5. How does Raft's randomized election timeout prevent split votes?
6. What is the difference between Basic Paxos and Multi-Paxos?

## Advanced
7. How does Raft's safety mechanism prevent a stale leader from committing entries?
8. Compare Zookeeper's Zab protocol with Raft's log replication.
9. Explain the FLP impossibility result and how consensus algorithms circumvent it (hint: timeouts).

## System Design
10. Design a replicated state machine using Raft for a distributed key-value store with linearizable reads.