# Consistency Models - FLASHCARDS

## CAP Theorem

### Card 1
**Q:** What does CAP theorem state?
**A:** You can only guarantee two of three: Consistency, Availability, Partition Tolerance.

### Card 2
**Q:** CP systems examples?
**A:** MongoDB (primary), HBase, ZooKeeper, Redis Cluster.

### Card 3
**Q:** AP systems examples?
**A:** Cassandra, DynamoDB, CouchDB.

### Card 4
**Q:** Why is partition tolerance mandatory?
**A:** Networks can fail; you must choose between consistency and availability.

## Consistency Models

### Card 5
**Q:** Strong consistency?
**A:** All reads return most recent write. Highest latency, lowest availability during partition.

### Card 6
**Q:** Eventual consistency?
**A:** Updates propagate asynchronously. Lower latency, higher availability.

### Card 7
**Q:** Session consistency?
**A:** Client sees own writes. Read-your-own-writes within a session.

### Card 8
**Q:** Causal consistency?
**A:** Preserves cause-and-effect. Operations that are causally related are seen in order.

## ACID vs BASE

### Card 9
**Q:** ACID properties?
**A:** Atomicity, Consistency, Isolation, Durability.

### Card 10
**Q:** BASE properties?
**A:** Basically Available, Soft state, Eventually consistent.

### Card 11
**Q:** When use ACID?
**A:** Financial transactions, critical data, strong consistency requirements.

### Card 12
**Q:** When use BASE?
**A:** High scalability, flexible schemas, can tolerate eventual consistency.

## Distributed Transactions

### Card 13
**Q:** Two-Phase Commit phases?
**A:** Phase 1: Prepare (vote). Phase 2: Commit or Rollback.

### Card 14
**Q:** 2PC drawback?
**A:** Blocking - participants hold locks until commit/rollback.

### Card 15
**Q:** Saga pattern?
**A:** Sequence of local transactions with compensating actions on failure.

### Card 16
**Q:** Saga vs 2PC?
**A:** Saga: eventual consistency, no locks. 2PC: strong consistency, blocking.

### Card 17
**Q:** Saga compensation order?
**A:** Reverse order of executed steps.

## Conflict Resolution

### Card 18
**Q:** Last-Writer-Wins?
**A:** Resolution based on timestamp. Simple but may lose data.

### Card 19
**Q:** Vector clock purpose?
**A:** Track causality between events to determine happens-before relationship.

### Card 20
**Q:** When vectors clocks concurrent?
**A:** Neither happensBefore the other - neither knows about the other.

### Card 21
**Q:** CRDT definition?
**A:** Conflict-free Replicated Data Type - mathematically proven to converge.

### Card 22
**Q:** G-Counter CRDT?
**A:** Grow-only counter. Each node increments its own count; merge takes max.

### Card 23
**Q:** LWW Register CRDT?
**A:** Last-writer-wins register. Stores (value, timestamp); merge keeps higher timestamp.

## Quorum Systems

### Card 24
**Q:** Quorum read?
**A:** Read from R replicas out of N total.

### Card 25
**Q:** Quorum write?
**A:** Write to W replicas out of N total.

### Card 26
**Q:** Strong consistency formula?
**A:** W + R > N (e.g., N=5, W=3, R=3 means 6 > 5)

### Card 27
**Q:** Eventual consistency formula?
**A:** W + R <= N (e.g., N=5, W=2, R=2 means 4 < 5)

## Implementation Patterns

### Card 28
**Q:** Circuit breaker states?
**A:** CLOSED (normal), OPEN (fail-fast), HALF-OPEN (test recovery).

### Card 29
**Q:** Circuit breaker opens when?
**A:** Failures exceed threshold within time window.

### Card 30
**Q:** Sticky sessions benefit?
**A:** Ensures client hits same node for read-your-own-writes consistency.

### Card 31
**Q:** Read-your-own-writes implementation?
**A:** Cache client's writes locally; check local cache before quorum read.

---

**Total: 31 flashcards**