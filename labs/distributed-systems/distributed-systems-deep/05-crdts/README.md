# 05 - CRDTs (Conflict-free Replicated Data Types)

## Topics Covered
- State-based CRDTs (CvRDTs): merge via LUB of semilattice
- Op-based CRDTs (CmRDTs): commutative operations
- G-Counter (grow-only counter)
- PN-Counter (positive/negative counters)
- G-Set (grow-only set), 2P-Set (two-phase set)
- LWW-Register (last-writer-wins register)
- Merging resolution: monotonic, idempotent, commutative

## Goal
Understand how CRDTs provide strong eventual consistency (SEC) without conflict resolution.

## Exercises

1. Implement G-Counter and PN-Counter with merge.
2. Implement G-Set and 2P-Set.
3. Implement LWW-Register with hybrid logical clocks (HLC).
4. Simulate concurrent updates merging from two nodes.