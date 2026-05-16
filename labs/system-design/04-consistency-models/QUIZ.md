# Consistency Models - QUIZ

## Section 1: CAP Theorem

**Q1: What does CAP stand for?**
- A) Cloud, Availability, Performance
- B) Consistency, Availability, Partition tolerance
- C) Consistency, Async, Partition
- D) Cache, Async, Protocol

**Q2: In CAP theorem, what must you choose between during partition?**
- A) Consistency and Availability
- B) Performance and Scalability
- C) Sync and Async
- D) Strong and Weak consistency

**Q3: Which is a CP system?**
- A) Cassandra with eventual consistency
- B) MongoDB with primary replica
- C) DynamoDB with all consistency levels
- D) CouchDB

**Q4: Which is an AP system?**
- A) ZooKeeper
- B) MongoDB with strong consistency
- C) Cassandra with QUORUM
- D) HBase

**Q5: What is partition tolerance?**
- A) System handles network partitions gracefully
- B) System never partitions
- C) Partition is disabled
- D) System uses partitioning

## Section 2: Consistency Models

**Q6: Strong consistency guarantees:**
- A) Reads always return latest write
- B) Reads may return stale data
- C) Writes always succeed
- D) No latency guarantees

**Q7: Eventual consistency means:**
- A) All reads return immediately
- B) Updates propagate asynchronously
- C) System never updates
- D) Updates are blocked

**Q8: Causal consistency preserves:**
- A) All orderings
- B) Cause-and-effect relationships
- C) Strong ordering
- D) No relationships

**Q9: Sequential consistency requires:**
- A) Same order on all nodes
- B) Random ordering
- C) No ordering
- D) Single thread only

**Q10: Read-your-own-writes is a form of:**
- A) Strong consistency
- B) Session consistency
- C) No consistency
- D) Async consistency

## Section 3: ACID vs BASE

**Q11: ACID stands for:**
- A) Async, Concurrent, Isolated, Durable
- B) Atomic, Consistent, Isolated, Durable
- C) Atomic, Consistent, Integrated, Durable
- D) Async, Consistent, Isolated, Distributed

**Q12: BASE guarantees:**
- A) Immediate consistency
- B) Zero downtime
- C) Eventual consistency
- D) Strong isolation

**Q13: Which is an ACID database?**
- A) MongoDB
- B) Cassandra
- C) PostgreSQL
- D) DynamoDB

**Q14: Which follows BASE model?**
- A) PostgreSQL
- B) MySQL
- C) Cassandra
- D) Oracle

**Q15: The "S" in BASE stands for:**
- A) Strong
- B) Soft state
- C) Simple
- D) Scalable

## Section 4: Distributed Transactions

**Q16: Two-Phase Commit (2PC) phases are:**
- A) Prepare and Commit/Rollback
- B) Lock and Unlock
- C) Vote and Execute
- D) Start and End

**Q17: What happens if one participant votes "no" in 2PC?**
- A) Commit proceeds
- B) Rollback all
- C) Retry vote
- D) Skip participant

**Q18: Saga pattern uses:**
- A) Locking
- B) Compensating transactions
- C) 2PC
- D) Strong consistency

**Q19: Saga compensation runs:**
- A) Forward on success
- B) Backward on failure
- C) Random order
- D) Never

**Q20: Saga is suitable for:**
- A) Strong consistency requirements
- B) Eventual consistency acceptable
- C) Single service only
- D) Real-time systems

## Section 5: Conflict Resolution

**Q21: Last-Writer-Wins uses:**
- A) Vector clocks
- B) Timestamps
- C) Random selection
- D) User preference

**Q22: Vector clocks track:**
- A) Time only
- B) Causality
- C) Network delays
- D) Clock drift

**Q23: CRDT stands for:**
- A) Conflict Resolution Data Type
- B) Conflict-free Replicated Data Type
- C) Consistent Replicated Data Type
- D) Causal Replicated Data Type

**Q24: G-Counter is a type of:**
- A) LWW Register
- B) Grow-only CRDT
- C) Vector clock
- D) Set CRDT

**Q25: CRDTs guarantee:**
- A) Strong consistency
- B) Eventual consistency with convergence
- C) No consistency
- D) Immediate convergence

## Section 6: Implementation

**Q26: Quorum read requires:**
- A) R replicas
- B) W replicas
- C) All replicas
- D) Single replica

**Q27: For strong consistency, W + R must be:**
- A) Equal to N
- B) Greater than N
- C) Less than N
- D) Zero

**Q28: Circuit breaker opens when:**
- A) One failure occurs
- B) Threshold failures exceeded
- C) No traffic
- D) System is healthy

**Q29: Circuit breaker half-open state:**
- A) Always allows requests
- B) Allows limited requests to test recovery
- C) Blocks all requests
- D) Resets counter

**Q30: Sticky sessions help with:**
- A) Load balancing
- B) Read-your-own-writes consistency
- C) Data replication
- D) Service discovery

---

## Answers

1. B
2. A
3. B
4. C
5. A
6. A
7. B
8. B
9. A
10. B
11. B
12. C
13. C
14. C
15. B
16. A
17. B
18. B
19. B
20. B
21. B
22. B
23. B
24. B
25. B
26. A
27. B
28. B
29. B
30. B