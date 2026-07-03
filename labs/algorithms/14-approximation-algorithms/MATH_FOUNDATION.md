# Math Foundation for Approximation

## Ratio Definitions
- Minimization: A(I) ≤ ρ · OPT(I)
- Maximization: OPT(I) ≤ ρ · A(I)
- ρ ≥ 1 (closer to 1 = better)

## Key Bounds
| Problem | Best Ratio | Hardness |
|---------|-----------|----------|
| Vertex Cover | 2 | 1.36 (unless P=NP) |
| Set Cover | O(log n) | Ω(log n) (unless P=NP) |
| Metric TSP | 1.5 (Christofides) | 1.0045 (unless P=NP) |
| Knapsack | FPTAS (1+ε) | FPTAS is optimal |
| Max-Cut | 0.878 (Goemans-Williamson) | 0.941 (UGC) |

## The PCP Theorem
Every NP problem can be probabilistically verified with constant queries. This implies hardness of approximation for many problems.
