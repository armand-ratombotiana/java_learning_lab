# ACID vs BASE — Deep Dive Guide

## ACID

| Property    | Meaning                                      |
|------------|----------------------------------------------|
| Atomicity  | All or nothing                               |
| Consistency | Invariants preserved                         |
| Isolation  | Concurrent transactions appear serial        |
| Durability | Committed data survives failures             |

## Isolation Levels (ANSI SQL)

| Level                | Dirty Read | Non-repeatable | Phantom |
|----------------------|-----------|----------------|---------|
| Read Uncommitted     | Possible  | Possible       | Possible|
| Read Committed       | Safe      | Possible       | Possible|
| Repeatable Read      | Safe      | Safe           | Possible|
| Serializable         | Safe      | Safe           | Safe    |

## CAP Theorem

Pick two of three: **C**onsistency (linearizability), **A**vailability (every request gets a response), **P**artition tolerance (system works despite network splits).

- CP: MongoDB (older), HBase
- AP: Cassandra, DynamoDB
- CA: Single-node (no partition)

## PACELC

If Partition (P): trade-off between Availability and Consistency (A vs C)
Else (E): trade-off between Latency and Consistency (L vs C)

## Distributed Transactions

### Two-Phase Commit (2PC)

1. **Prepare phase**: coordinator asks all participants to prepare
2. **Commit phase**: if all prepared, coordinator sends commit; otherwise abort

Blocking if coordinator crashes after prepare.

### Saga Pattern

Sequence of local transactions with compensating actions. Non-blocking, eventual consistency.

- Choreography: each service publishes events
- Orchestration: central coordinator sends commands