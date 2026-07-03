# Dynamic Programming — Theoretical Foundation

## Key Properties
### Optimal Substructure
Optimal solution contains optimal solutions to subproblems.

### Overlapping Subproblems
Same subproblems solved multiple times if naive recursion is used.

## Two Approaches

### Top-Down (Memoization)
- Recursive with caching
- Start from original problem, recurse to base cases
- Store computed results in array/HashMap
- Time: subproblems × time per subproblem
- Space: O(subproblems) + recursion stack

### Bottom-Up (Tabulation)
- Iterative
- Solve subproblems in order of increasing size
- Build table from base cases to target
- Often faster (no recursion overhead)
- Can be space-optimized (rolling arrays)
