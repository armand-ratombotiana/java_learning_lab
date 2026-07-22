# Interview Questions: Backtracking

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| LC 46 Permutations | Medium | Google, Meta, Amazon, Microsoft | Swap / pick |
| LC 78 Subsets | Medium | Google, Meta, Amazon, Microsoft | Inclusion/exclusion |
| LC 90 Subsets II | Medium | Google, Meta | Handle duplicates |
| LC 39 Combination Sum | Medium | Google, Meta, Amazon | Pick with repetition |
| LC 40 Combination Sum II | Medium | Google, Meta | Pick without repetition |
| LC 79 Word Search | Medium | Google, Meta, Amazon, Microsoft | Grid DFS |
| LC 51 N-Queens | Hard | Google, Meta, Amazon | Row by row |
| LC 37 Sudoku Solver | Hard | Google, Microsoft | Cell by cell |
| LC 22 Generate Parentheses | Medium | Google, Meta, Amazon | Open/close count |

## NeetCode Reference
- LC 46 Permutations (NeetCode 150)
- LC 78 Subsets (NeetCode 150)
- LC 90 Subsets II (NeetCode 150)
- LC 39 Combination Sum (NeetCode 150)
- LC 40 Combination Sum II (NeetCode 150)
- LC 79 Word Search (NeetCode 150)
- LC 51 N-Queens (NeetCode 150)
- LC 22 Generate Parentheses (NeetCode 150)

## Company-Specific Questions
### Google
- Word Search, N-Queens, and Sudoku Solver are Google signatures
- Expect complex backtracking with pruning optimization
- Focus on state management: how to undo changes efficiently

### Microsoft
- Generate Parentheses is a Microsoft favorite
- How would you solve a maze with multiple paths?
- Backtracking for constraint satisfaction problems

### Meta
- Subsets, Permutations, and Combination Sum form the core Meta backtracking repertoire
- Meta tests your ability to handle duplicates efficiently
- Focus on tree representation of recursion stack

### Amazon
- Word Search for product categorization in a hierarchy
- Combination Sum for resource allocation problems
- Backtracking for order fulfillment optimization

### Apple
- Memory-optimized backtracking for mobile apps
- Solving puzzles (N-Queens, Sudoku) in resource-constrained environments
- Pruning strategies to minimize search space

### Oracle
- Backtracking for database query optimization (join ordering)
- How does Oracle's constraint satisfaction solver work?
- Design a backtracking algorithm for schema migration planning

## Real Production Scenarios
- Scenario 1: Route optimization - using backtracking with pruning to find the optimal delivery route under multiple constraints (time windows, vehicle capacity, driver hours)
- Scenario 2: Configuration generation - backtracking to generate all valid configurations of a cloud deployment satisfying security, compliance, and cost constraints
- Scenario 3: Crossword puzzle generation - debugging a crossword generator that runs indefinitely due to insufficient pruning in the search space

## Interview Tips
- Backtracking = recursion + loop + undo; visualized as a decision tree
- Pruning is critical: sort input, skip duplicates, check bounds before recursing
- Time complexity: O(branching_factor ^ depth); pruning reduces this in practice
- Common edge cases: empty input, single element, all duplicates, large constraint ranges

## Java-Specific Considerations
- Use `List<Integer>` with `ArrayList` for current state; `new ArrayList<>(current)` for snapshot
- For grid backtracking (Word Search), modify in-place and restore: `board[i][j] = '#'; ...; board[i][j] = orig`
- N-Queens: `int[] queens` where `queens[row] = col` for O(1) conflict check
- Pitfall: not removing the last element after recursion (`list.remove(list.size()-1)`)
- Pitfall: sharing mutable state across recursive calls (always copy when storing results)
- For performance: use `boolean[]` instead of `Set<Integer>` for used-element tracking
