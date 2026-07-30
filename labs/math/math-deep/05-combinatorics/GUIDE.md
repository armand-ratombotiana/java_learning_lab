# Combinatorics — Study Guide

## Core Concepts

### Permutations and Combinations
- **Permutations**: P(n,k) = n!/(n-k)! — ordered arrangements
- **Combinations**: C(n,k) = n!/(k!(n-k)!) — unordered selections
- **Stars and Bars**: number of ways to distribute n identical items into k bins = C(n+k-1, k-1)

### Inclusion-Exclusion
- |A ∪ B| = |A| + |B| - |A ∩ B|
- General: |∪Aᵢ| = Σ|Aᵢ| - Σ|Aᵢ∩Aⱼ| + Σ|Aᵢ∩Aⱼ∩Aₖ| - ...

### Generating Functions
- Ordinary: G(x) = Σ a_n x^n
- Exponential: G(x) = Σ a_n x^n/n!
- Closed forms: 1/(1-x) = Σ x^n, (1+x)^n = Σ C(n,k) x^k

## Implementation Checklist
1. Use BigInteger for large factorial/binomial computations
2. Precompute factorials and inverse factorials for O(1) C(n,k)
3. Generate all permutations using Heap's algorithm (O(n!))
4. Use DP for recurrence relations (memoization)

## Common Pitfalls
- Integer overflow in factorial/binomial computations
- Off-by-one in recurrence base cases
- Forgetting to divide by symmetry factor in circular permutations
