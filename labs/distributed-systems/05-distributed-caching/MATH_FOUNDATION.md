# Distributed Caching: Mathematical Foundation

## Cache Hit Ratio

H(t) = hits(t) / (hits(t) + misses(t))

Expected value depends on:
- Working set size W
- Cache capacity C
- Access distribution (Zipf-like)

For Zipf distribution with parameter α:
P(k) = 1/k^α / Σ(1/i^α)

Hit ratio ≈ 1 - (C/W)^(1-α) for α < 1

## Optimal Cache Size

Given:
- Cache hit ratio h(C) as function of capacity C
- Database query cost D
- Cache query cost C_q

Total cost: N * [h(C) * C_q + (1-h(C)) * D]

Optimal when marginal benefit of +1 capacity ≤ marginal cost

## MTTR and Availability

Cache cluster availability:
A = 1 / (1 + MTTR/MTBF)

For N-node cluster with replication factor R:
Availability = (1 - (1 - A)^R)^N
