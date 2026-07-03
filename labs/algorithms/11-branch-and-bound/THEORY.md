# Branch and Bound — Theoretical Foundation

## The Paradigm
Branch and Bound = Backtracking + Bounding Function

Unlike backtracking (which finds all solutions), B&B finds the optimal solution by keeping track of the best solution found so far and pruning branches that cannot beat it.

## Key Components
1. **Branching**: How to divide problem into subproblems
2. **Bounding**: Estimate upper/lower bound for a subproblem
3. **Pruning**: Discard subproblem if bound is worse than current best
4. **Search Strategy**: Best-first, depth-first, breadth-first

## Bounding Types
- **Lower Bound**: Minimum cost achievable from this node (minimization)
- **Upper Bound**: Maximum value achievable (maximization)
- If bound cannot improve on current best → prune
