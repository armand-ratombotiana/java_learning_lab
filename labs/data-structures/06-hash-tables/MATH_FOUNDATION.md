# Math Foundation: Hash Tables

## Hash Function Mathematics

A good hash function produces a **uniform distribution** of hash values over the output range. For a hash function h: K → {0, ..., m-1}:

```
P(h(k) = i) = 1/m  for all keys k and all i ∈ [0, m-1]
```

## Load Factor Analysis

```
α = n / m
where n = number of entries, m = capacity
```

### Separate Chaining
- Expected chain length: α
- Expected search cost (successful): 1 + α/2
- Expected search cost (unsuccessful): α
- Variance in chain length depends on hash quality

### Linear Probing
- Expected probes (successful): 1/2 × (1 + 1/(1-α))
- Expected probes (unsuccessful): 1/2 × (1 + 1/(1-α)²)
- At α = 0.5: ~1.5 probes (successful), ~2.5 (unsuccessful)
- At α = 0.75: ~2.5 probes (successful), ~8.5 (unsuccessful)
- At α = 0.9: ~5.5 probes (successful), ~50.5 (unsuccessful)

## Birthday Paradox for Collisions

Probability of at least one collision among n keys with m buckets:
```
P(collision) = 1 - m! / ((m-n)! × mⁿ)
≈ 1 - e^{-n(n-1)/(2m)}  for large m
```

With m = 365, only 23 people needed for P > 0.5.
With m = 2³², ~77,000 keys needed for P > 0.5.

## Load Factor Threshold Design

`Java's DEFAULT_LOAD_FACTOR = 0.75f` is a trade-off:
- At 0.75, linear probing gives ~2.5 probes per lookup
- Space overhead: ~33% (1/0.75 - 1)
- Performance-memory trade-off validated by empirical testing

## Rehashing Cost

Resizing from capacity C to 2C:
- All n ≈ 0.75C entries must be rehashed
- Probability each entry maps to new index: ~50%
- Cost: O(n) = O(C)

## Power-of-2 Indexing

Using `hash & (length - 1)` instead of `hash % length`:
- Requires length = 2^k
- Bitwise AND is ~10x faster than modulo on most CPUs
- Lower bits of hash directly determine index → hash must be well-distributed in low bits
