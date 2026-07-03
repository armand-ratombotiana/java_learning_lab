# Complexity Analysis — Theoretical Foundation

## Asymptotic Notation

### Big O (Upper Bound)
f(n) = O(g(n)) if ∃ c, n₀ > 0: 0 ≤ f(n) ≤ c·g(n) for all n ≥ n₀

### Big Ω (Lower Bound)
f(n) = Ω(g(n)) if ∃ c, n₀ > 0: 0 ≤ c·g(n) ≤ f(n) for all n ≥ n₀

### Big Θ (Tight Bound)
f(n) = Θ(g(n)) if f(n) = O(g(n)) AND f(n) = Ω(g(n))

### Little o and ω
- f(n) = o(g(n)): f grows strictly slower than g
- f(n) = ω(g(n)): f grows strictly faster than g

## Common Complexity Classes
| Notation | Name | Example |
|----------|------|---------|
| O(1) | Constant | Array access |
| O(log n) | Logarithmic | Binary search |
| O(n) | Linear | Linear search |
| O(n log n) | Linearithmic | Merge sort |
| O(n²) | Quadratic | Bubble sort |
| O(n³) | Cubic | Floyd-Warshall |
| O(2ⁿ) | Exponential | Subset sum |
| O(n!) | Factorial | TSP brute force |
