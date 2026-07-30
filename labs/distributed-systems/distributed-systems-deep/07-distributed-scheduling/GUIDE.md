# Distributed Scheduling — Deep Dive Guide

## Consistent Hashing

Maps keys to nodes on a hash ring (0 to 2³²−1). Each node is placed at multiple positions using virtual nodes.

**Advantages**: when N changes, only K/N keys move (vs K for mod-N).

**Rebalancing**: a node joining takes keys from its neighbors.

## Rendezvous Hashing (HRW)

Each key computes a hash with each candidate node. The node with the highest hash wins.

```
winner = argmax_node hash(key + "_" + node)
```

No virtual nodes needed. Rebalancing still O(K/N) but simpler implementation.

## Power-of-Two Choices

Place each item on the least-loaded of two random candidates. Results in near-optimal load balancing.

## Rebalancing Scenarios

| Event | Action |
|-------|--------|
| Node joins | Keys reassigned from neighbors |
| Node leaves | Keys reassigned to remaining nodes |
| Weight change | Reshuffle keys to match target weights |

## Virtual Nodes

Each physical node is represented by R virtual points on the ring. Higher R → better distribution but more metadata.