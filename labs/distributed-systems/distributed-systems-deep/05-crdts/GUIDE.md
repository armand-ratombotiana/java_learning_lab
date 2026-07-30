# CRDTs — Deep Dive Guide

## State-based CRDT (CvRDT)

State converges when merged via **least upper bound (LUB)** of a join-semilattice.

Requirements: monotonic (state only grows), idempotent (merge(a,a)=a), commutative (merge(a,b)=merge(b,a)).

## G-Counter

Each replica maintains a vector of N integers. Increment only at own index. Merge = element-wise max.

```
Replica i: [a₁,a₂,...,aᵢ,...,aₙ]
Increment: aᵢ++
Merge:     max(aⱼ, bⱼ) for each j
Value:     sum of all entries
```

## PN-Counter

Two G-Counters: P (increments) and N (decrements). Value = P - N.

## G-Set

Add-only set. Merge = union.

## 2P-Set

Two G-Sets: A (add set) and R (remove set). An element can only be removed if it was added first. Once removed, cannot be re-added. Merge = union of both sets.

## LWW-Register

Each write carries a timestamp. Latest timestamp wins on merge. Can lose concurrent writes (hence "last-writer-wins").

## Op-based CRDT (CmRDT)

Operations (not state) are broadcast. Must be **commutative** — order doesn't matter. Requires reliable broadcast (at-least-once + causal order).

## Comparison

| CRDT | Type | Use Case |
|------|------|----------|
| G-Counter | State | Page views, likes |
| PN-Counter | State | Inventory, votes |
| G-Set | State | Tags, followers |
| 2P-Set | State | Shopping cart |
| LWW-Register | State/Op | KV store value |