# Common Mistakes

- Not handling base cases correctly (infinite recursion)
- Subproblems that overlap (should be DP, not D&C)
- Inefficient combine step dominating the complexity
- Recomputing subproblem results (if overlapping)
- Not accounting for recursion overhead in small inputs
- Closest Pair: Not sorting strip by y, causing O(n²) strip check
- Off-by-one in array indices during divide step
