# Math Foundation: Bloom Filters

## False Positive Probability

Given m bits, n elements, and k hash functions:

The probability that a specific bit is still 0 after n inserts:
```
P(bit = 0) = (1 - 1/m)^(kn) ≈ e^(-kn/m)
```

The probability that all k bits for a non-inserted element are 1:
```
P(false positive) = (1 - (1 - 1/m)^(kn))^k ≈ (1 - e^(-kn/m))^k
```

## Optimal Number of Hash Functions

Minimizing the false positive probability P:
```
dP/dk = 0 → k = (m/n) × ln(2)
```

At k_optimal:
```
P = (1 - e^(-(m/n) × ln(2) × n/m))^((m/n) × ln(2))
  = (1 - e^(-ln(2)))^((m/n) × ln(2))
  = (1/2)^((m/n) × ln(2))
  ≈ 0.6185^(m/n)
```

## Bit Array Size

Given n elements and desired false positive probability P:
```
m = -n × ln(P) / (ln(2))²
```

Common values:
| P | m/n (bits per element) | k_optimal |
|---|----------------------|-----------|
| 10% | 4.79 | 3 |
| 5% | 6.24 | 4 |
| 1% | 9.59 | 7 |
| 0.1% | 14.38 | 10 |
| 0.01% | 19.17 | 14 |

## Fill Ratio

After n inserts with k hash functions:
```
Fill ratio = 1 - e^(-kn/m)
```

For optimal k = (m/n) × ln(2):
```
Fill ratio = 1 - e^(-ln(2)) = 1/2 = 50%
```

At optimal configuration, exactly 50% of bits are set to 1.

## False Negatives

Bloom filters have **zero false negatives**:
- If an element was inserted, all k bits were set to 1
- Querying that element will always find all k bits as 1
- Therefore the filter always returns "maybe yes"

## Counting Bloom Filter Counter Overflow

For 4-bit counters (max value 15):
- Expected number of bits set per element: k
- If each of n elements is counted, some counters receive > 15 hits
- Probability of overflow is negligible for reasonable n (≤ 1M with appropriate counter bits)
- Solution: use larger counters or saturating arithmetic

## Union of Two Bloom Filters

Given two filters A and B with same m and k:
- Union: bit array = A OR B (bitwise)
- Expected false positive of union: P(A ∪ B) ≈ P(A) + P(B) - P(A)P(B)

## Scalable Bloom Filter

When total elements is unknown:
- Start with an initial filter (capacity n₀)
- When fill ratio exceeds target, create a new filter (capacity n₀ × r^t)
- Query checks all filters (OR of individual results)
- Total space: O(n / (1 - 1/r)) ≈ O(n)
