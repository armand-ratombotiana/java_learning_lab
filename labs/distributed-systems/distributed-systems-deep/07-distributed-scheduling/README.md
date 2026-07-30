# 07 - Distributed Scheduling

## Topics Covered
- Consistent hashing (hash ring, virtual nodes)
- Rendezvous hashing (highest random weight — HRW)
- Workload distribution (power-of-two choices, round-robin, weighted)
- Rebalancing strategies
- Partition assignment (static vs dynamic)
- Load-based vs hash-based scheduling

## Goal
Understand how distributed systems assign and balance work across nodes.

## Exercises

1. Implement consistent hashing with virtual nodes and measure distribution.
2. Implement rendezvous hashing (HRW) and compare distribution quality.
3. Simulate a rebalancing event when nodes join and leave.
4. Compare power-of-two choices with round-robin for task assignment.