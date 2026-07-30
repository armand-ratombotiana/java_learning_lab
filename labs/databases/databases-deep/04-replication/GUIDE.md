# Replication — Deep Dive Guide

## Physical vs Logical Replication

| Aspect            | Physical                         | Logical                              |
|-------------------|----------------------------------|--------------------------------------|
| Data              | Exact block-level copy           | Row-based changes (WAL decoding)     |
| Version           | Same major version               | Cross-version, cross-DB possible     |
| Subset            | Entire database                  | Selective tables                     |
| Use case          | HA, failover                     | Migration, CDC, analytics            |

## Synchronous Replication

- Master waits for at least one sync replica to confirm flush before committing
- Guarantees zero data loss if sync replica is alive
- Trade-off: increased latency, reduced throughput

## Asynchronous Replication

- Master commits without waiting for replica
- Replica may lag — data loss on master failure
- MySQL default; PostgreSQL has both modes

## Conflict Resolution

| Strategy       | Description                                  |
|---------------|----------------------------------------------|
| LWW (last-write-wins) | Use timestamp; simplest but loses data |
| Merge         | Combine values (e.g., CRDTs, JSON merge)     |
| Custom        | Application-level conflict handler            |
| Version vectors | Detect concurrent updates, manual resolution |

## Multi-Master

- Both nodes accept writes
- Requires conflict detection and resolution
- Examples: MySQL Group Replication, CockroachDB