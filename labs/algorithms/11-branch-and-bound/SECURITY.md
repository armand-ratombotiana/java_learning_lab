# Security — Branch and Bound

- DoS: Pathological input causing worst-case exponential search
- Memory exhaustion: Best-first search can consume significant RAM
- Timeout: Long-running B&B can be exploited for resource exhaustion
- Input validation: Limit problem size (n ≤ 20 for TSP, n ≤ 50 for knapsack)
- Use iterative deepening or depth-first for memory-constrained environments
