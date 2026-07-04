# Why Time and Ordering Exist

## Problem
- No global clock in distributed systems
- Physical clocks drift and aren't perfectly synchronized
- Need to determine event ordering without synchronized time

## Purpose
1. **Causality tracking**: Determine which events caused others
2. **Consistency**: Enforce ordering guarantees
3. **Debugging**: Reconstruct distributed execution order
4. **Conflict detection**: Identify concurrent updates

## Use Cases
- Database replication ordering
- Debugging distributed traces
- Conflict resolution (last-write-wins)
- Causal consistency enforcement
- Distributed snapshotting
