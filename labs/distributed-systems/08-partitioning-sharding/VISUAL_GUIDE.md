# Partitioning: Visual Guide

## Consistent Hash Ring

```
                Node A
          ┌──────────────┐
          │               │
     Node │    ┌────┐     │ Node
      D   │    │Key │     │  B
          │    └────┘     │
          │   assigned to │
          │   next node   │
          └──────────────┘
                Node C

Key hashed to position → assigned to nearest clockwise node
```

## Range Partitioning

```
Partition 0: keys A-I
┌─────────────────────────┐
│ [Shard 1 - Node A]      │
└─────────────────────────┘

Partition 1: keys J-R
┌─────────────────────────┐
│ [Shard 2 - Node B]      │
└─────────────────────────┘

Partition 2: keys S-Z
┌─────────────────────────┐
│ [Shard 3 - Node C]      │
└─────────────────────────┘
```

## Rebalancing on Node Add

```
Before: 3 nodes (A, B, C)
After:  4 nodes (A, B, C, D)
Keys moved: ~25% of total
```
