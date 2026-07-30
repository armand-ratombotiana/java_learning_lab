# 04 - Replication

## Topics Covered
- Master-Slave (Primary-Replica) replication
- Multi-Master replication
- Synchronous vs Asynchronous replication
- Conflict resolution strategies
- Logical vs Physical replication
- Replication lag, durability trade-offs

## Goal
Understand the mechanics of keeping multiple database copies consistent and the CAP trade-offs involved.

## Exercises

1. Simulate asynchronous replication lag and a read-your-writes inconsistency.
2. Implement two conflict resolution strategies: last-write-wins (LWW) and merge.
3. Compare synchronous (wait for ack) vs asynchronous throughput.
4. Design a multi-master topology and identify conflict scenarios.