# Interview Questions: Branch and Bound

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| LC 37 Sudoku Solver | Hard | Google, Microsoft | B&B with constraint propagation |
| LC 51 N-Queens | Hard | Google, Meta, Amazon | B&B with pruning |
| LC 935 Knight Dialer | Medium | Google | DP / B&B state space |
| LC 1655 Distribute Repeating Integers | Hard | Google | B&B / DP + bitmask |
| LC 698 Partition to K Equal Sum Subsets | Medium | Google, Amazon, Microsoft | B&B / backtracking |
| LC 1723 Minimum Time to Complete All Work | Hard | Google | B&B with assignment |

## NeetCode Reference
- LC 51 N-Queens (NeetCode 150)
- LC 37 Sudoku Solver (NeetCode 150)
- LC 698 Partition to K Equal Sum Subsets (NeetCode 150)

## Company-Specific Questions
### Google
- Partition to K Equal Sum Subsets with optimization and pruning is Google-signature
- B&B problems test your ability to design bounding functions
- Expect travel-related optimization (TSP with branch and bound)

### Microsoft
- How does B&B differ from simple backtracking?
- Design a B&B algorithm for resource allocation
- N-Queens with symmetry reduction

### Meta
- Less common; when B&B appears, it's for optimization variants of backtracking
- Focus on bounding function design to prune search space
- Partition equal subset sum with optimization constraints

### Amazon
- B&B for warehouse robot path optimization
- How would you schedule warehouse tasks minimizing completion time?
- Partition problems with real-world constraints

### Apple
- Memory-efficient B&B for small devices
- Pruning strategies that save computation on battery-constrained devices
- Traveling salesman with B&B for route planning

### Oracle
- B&B for database query optimization
- How does Oracle's optimizer prune plan space?
- Design a bounding function for join order enumeration

## Real Production Scenarios
- Scenario 1: Manufacturing scheduling - using B&B to minimize makespan across machines with job dependencies, due dates, and setup times
- Scenario 2: Cloud resource allocation - applying B&B to assign workloads to the optimal mix of reserved and spot instances minimizing total cost
- Scenario 3: Travel routing - debugging a TSP solver that takes hours when it should take seconds due to a poor bounding function

## Interview Tips
- B&B = branching (explore partial solutions) + bounding (discard if lower bound exceeds best known)
- A good bounding function is the key to efficiency; the tighter the bound, the more pruning
- B&B is exact (finds optimal) but worst-case exponential; heuristics are approximate
- Common edge cases: infeasible problems, tight constraints, symmetrical solutions

## Java-Specific Considerations
- Priority queue for best-first B&B (branch with best bound explored first)
- Lower bound functions should be fast to compute; O(n) or O(1) if possible
- For TSP: use MST-based lower bound (sum of minimum edges)
- Implement `Comparable` on state objects for priority queue ordering
- Pitfall: storing too many states in the priority queue causing OOM
- Pitfall: producing duplicate states due to symmetry; use canonical representation to deduplicate
