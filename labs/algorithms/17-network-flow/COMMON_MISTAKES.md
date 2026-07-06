# Common Mistakes — Network Flow

- **Missing reverse edges** — Every edge needs a reverse edge with 0 initial capacity
- **Wrong rev index** — Setting rev incorrectly breaks residual updates
- **Overflow with int capacities** — Use long for capacities that may exceed 2^31
- **BFS/DFS infinite loops** — Not checking visited states in DFS
- **Forgetting level graph recomputation** — Must recompute after each blocking flow
- **Self-loops** — Adding edges from v to v can cause problems
- **Integer capacity but long flow** — Flow accumulation may exceed int range
- **Multiple edges between same nodes** — Should merge them into one edge
- **Not handling disconnected graphs** — BFS returning false means no more flow
- **Reusing graph without resetting** — Residual capacities must be reset between runs
