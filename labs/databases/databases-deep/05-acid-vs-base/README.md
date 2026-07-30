# 05 - ACID vs BASE

## Topics Covered
- ACID properties (Atomicity, Consistency, Isolation, Durability)
- BASE (Basically Available, Soft state, Eventual consistency)
- CAP Theorem (Consistency, Availability, Partition tolerance)
- PACELC extension (if partition, trade-off; else trade-off)
- Isolation levels (Read Uncommitted → Serializable)
- Distributed transactions (2PC, Saga patterns)

## Goal
Understand the spectrum of consistency guarantees and when to apply each model.

## Exercises

1. Simulate write skew under Snapshot Isolation and show how Serializable prevents it.
2. Implement a 2PC coordinator with prepare/commit/abort phases.
3. Implement a Saga pattern using compensating transactions.
4. Categorize real-world databases on the PACELC spectrum.