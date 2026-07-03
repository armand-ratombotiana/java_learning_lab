# Performance — DP

| Problem | Time | Space | Optimized |
|---------|------|-------|-----------|
| Fibonacci | O(n) | O(n) | O(1) |
| 0/1 Knapsack | O(nW) | O(nW) | O(W) |
| LCS | O(mn) | O(mn) | O(min(m,n)) |
| Edit Distance | O(mn) | O(mn) | O(min(m,n)) |
| Coin Change | O(n×amount) | O(amount) | O(amount) |

## When DP is Appropriate
- n < 1000 for O(n²) DP
- n < 100 for O(n³) DP
- n < 20 for O(2ⁿ) bitmask DP
