# Partitioning: Mathematical Foundation

## Consistent Hashing

Given N nodes and K keys, with consistent hashing:

### Key Distribution
P(key assigned to node i) = 1/N (uniform with good hash)

### Rebalancing on Node Add/Remove
P(key moves when 1 node added) = 1/(N+1)
P(key moves when 1 node removed) = 1/N

Total keys moved on add: K/(N+1)
Total keys moved on remove: K/N

### Compare with Modular Hashing
With modular hashing: K * (N-1)/N keys move on add (catastrophic)
With consistent hashing: K/(N+1) keys move (minimal)

## Virtual Nodes vs Real Nodes

With V virtual nodes per real node:
- Standard deviation of load = O(1/√V)
- With V=256: ~6% std deviation
- Without virtual nodes: can be >50%

## Range Partition Balance

For uniform key distribution across range partitions:
- Load balance = max(load_i) / avg(load)
- With random range boundaries: ~ln(N)
- With ordered boundaries with splitting: ~1.2 (with good splitting)
