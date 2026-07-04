# Theory: Bloom Filters

## Bloom Filter Definition

A **Bloom filter** is a probabilistic data structure used for **membership testing** with:
- **No false negatives**: if an element was added, the filter always says "maybe yes"
- **Tunable false positives**: the filter may say "yes" for an element that was never added

## Components

1. **Bit array**: m bits, all initially 0
2. **Hash functions**: k independent hash functions, each mapping an element to a bit position

## Operations

### Insert
1. Compute k hash values: h₁(x), h₂(x), ..., hₖ(x)
2. Set all k bit positions to 1

### Query
1. Compute k hash values: h₁(x), h₂(x), ..., hₖ(x)
2. If any bit is 0 → element **definitely not** in set
3. If all bits are 1 → element **probably** in set

## False Positive Probability

Given m bits, n elements, k hash functions:

```
P(false positive) = (1 - e^(-kn/m))^k
```

### Optimal k
```
k_optimal = (m/n) × ln(2)
```

### Required m for target P
```
m = -n × ln(P) / (ln(2))²
```

## Example

```
m = 10 bits, k = 3 hash functions
Insert "cat": set bits 2, 5, 7
Insert "dog": set bits 1, 5, 9

Query "cat": bits 2=1, 5=1, 7=1 → probably present ✓
Query "rat": bits 3=0 → definitely absent ✓
Query "bat": bits 2=1, 4=0 → definitely absent (bit 4 is 0) ✓
But: if "bat" hashes to bits 1, 5, 9 → false positive!
```

## Time Complexity

| Operation | Time | Note |
|-----------|------|------|
| Insert | O(k) | Compute k hashes, set k bits |
| Query | O(k) | Compute k hashes, check k bits |
| Space | O(m) | m-bit array |

## Variants

- **Counting Bloom filter**: uses counters instead of bits (supports delete)
- **Scalable Bloom filter**: grows dynamically as elements are added
- **Cuckoo filter**: higher density, supports deletion, may have false negatives
- **Bloomier filter**: maps keys to values (not just membership)
