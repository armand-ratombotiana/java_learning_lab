# Distributed Consensus: Quiz

## Questions
1. What does FLP stand for?
2. How many nodes are needed for a fault-tolerant Raft cluster?
3. What is the role of the leader in Raft?
4. How does Paxos differ from Raft?
5. What is a split-brain scenario?
6. What is the purpose of terms in Raft?
7. What happens when a Raft leader fails?
8. What is Multi-Paxos?
9. How does Zab differ from Raft?
10. What is the FLP impossibility result?

## Answers
1. Fischer, Lynch, Paterson
2. 2f+1 to tolerate f failures (typically 3 or 5)
3. Handles all client requests and log replication
4. Raft has strong leader, simpler design; Paxos is more complex
5. Two parts of system both believe they are leaders
6. Acts as logical clock for leader election
7. New election triggered by heartbeat timeout
8. Single leader optimizes Paxos to 1 RTT per entry
9. Zab uses epoch numbers; Raft uses terms
10. No deterministic consensus can guarantee termination in async systems
