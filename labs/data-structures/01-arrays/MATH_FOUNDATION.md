# Math Foundation for Arrays

## Memory Address Calculation

For a 1D array with base address `B` and element size `s`:

```
address(arr[i]) = B + s × i
```

For a 2D row-major array with `r` rows and `c` columns:

```
address(arr[row][col]) = B + s × (row × c + col)
```

## Amortized Analysis of Dynamic Arrays

**Theorem**: A sequence of n append operations on a dynamic array that doubles capacity when full costs O(n) total, or O(1) amortized per operation.

**Proof**: Let capacity start at 1. Resizings occur at capacities 1, 2, 4, 8, ..., 2^k where 2^k ≤ n.

```
Total cost = n inserts + sum of copy costs during resizes
           = n + (1 + 2 + 4 + ... + 2^k)
           = n + (2^{k+1} - 1)
           ≤ n + 2n = 3n
```

Per operation: 3n / n = O(1).

## Geometric Growth Factor

Given current capacity `C` and growth factor `f > 1`:

- Capacity after t resizes: `C × f^t`
- Total elements copied: `C + Cf + Cf^2 + ... + Cf^k = C × (f^{k+1} - 1) / (f - 1)`
- Amortized cost per insert: `1 + f / (f - 1)`

Common factors: 2 (Java HashMap), 1.5 (Java ArrayList), 1.25 (some implementations).

## Cache Performance

Modern CPUs load cache lines (typically 64 bytes). For an `int[]`, each cache line holds 16 elements. Sequential access achieves:

- **L1 hit**: ~1 ns
- **L2 hit**: ~4 ns
- **L3 hit**: ~10 ns
- **RAM hit**: ~100 ns

Sequential array traversal achieves near peak memory bandwidth because the hardware prefetcher detects the stride pattern.
