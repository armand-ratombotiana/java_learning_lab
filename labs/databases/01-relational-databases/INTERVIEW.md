# Interview Prep: Relational Databases

## Common Interview Questions

### Q1: "Explain normalization and when you would denormalize."
**Key points:** 1NF (atomic columns), 2NF (no partial dependencies), 3NF (no transitive dependencies). Denormalize for read performance, reporting, or when joins are too expensive.

### Q2: "How does MVCC work in PostgreSQL?"
**Key points:** Each tuple has xmin/xmax. Snapshots track active transactions. VACUUM removes dead tuples.

### Q3: "What's the difference between JPA/Hibernate `fetch = FetchType.LAZY` and `FetchType.EAGER`?"
**Key points:** LAZY fetches on access (proxy), EAGER fetches eagerly with join/select. LAZY preferred to avoid unnecessary data loading.

### Q4: "How do you handle transactions in Spring?"
**Key points:** `@Transactional` annotation, proxy-based AOP, propagation levels (REQUIRED, REQUIRES_NEW, etc.), isolation levels.

### Q5: "What is the N+1 problem and how do you fix it?"
**Key points:** One query for parent + N for children. Fix with JOIN FETCH, @EntityGraph, @BatchSize.

### Q6: "Explain ACID in the context of a banking transaction."
**Key points:** Atomicity (all-or-nothing), Consistency (invariants hold), Isolation (no interference), Durability (survives crash).

### Q7: "How do connection pools work?"
**Key points:** Pre-create connections, reuse them, test idle connections, configurable pool size.

## Whiteboard Challenge
Design a schema for a ride-sharing application (riders, drivers, trips, payments). Discuss normalization tradeoffs.
