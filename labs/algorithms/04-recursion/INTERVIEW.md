# Interview Questions: Recursion

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| LC 50 Pow(x, n) | Medium | Google, Meta, Amazon | Divide & conquer recursion |
| LC 779 Kth Symbol in Grammar | Medium | Google | Recursive pattern |
| LC 394 Decode String | Medium | Google, Meta, Microsoft | Stack / recursion |
| LC 687 Longest Univalue Path | Medium | Google | Recursive DFS |
| LC 247 Strobogrammatic Number II | Medium | Google | Recursive construction |
| LC 78 Subsets | Medium | Google, Meta, Amazon | Recursive backtracking |
| LC 46 Permutations | Medium | Google, Meta, Amazon | Recursive backtracking |

## NeetCode Reference
- LC 78 Subsets (NeetCode 150)
- LC 46 Permutations (NeetCode 150)
- LC 50 Pow(x, n) (NeetCode 150)

## Company-Specific Questions
### Google
- Strobogrammatic Number series is a Google specialty
- Recursion with memoization for combinatorial generation
- How would you convert a recursive function to iterative without a stack?

### Microsoft
- Recursive linked list reversal is a common Microsoft question
- How does recursion depth impact production systems?
- Explain tail recursion and why Java does not optimize it

### Meta
- Recursion + backtracking for combinatorial problems
- Meta focuses on clean recursive decomposition with clear base cases
- Expect recursion tree analysis: time complexity = branching factor ^ depth

### Amazon
- Recursion in tree-based systems (product categories)
- Decode String tests nested structure processing (JSON-like)
- How would you handle deeply recursive XML/JSON parsing?

### Apple
- Memory-efficient recursion with minimal stack usage
- Recursive data structures for file system traversal
- Tail recursion elimination via trampoline pattern (manual)

### Oracle
- How does Oracle SQL handle recursive CTEs (Common Table Expressions)?
- Design a recursive query for hierarchical organizational charts
- Explain CONNECT BY vs recursive WITH in Oracle SQL

## Real Production Scenarios
- Scenario 1: File system traversal - recursively scanning a directory tree to calculate total disk usage with depth limits and symlink cycle detection
- Scenario 2: Configuration merging - recursively merging nested JSON/YAML configuration files from multiple sources with override semantics
- Scenario 3: Debugging stack overflow - investigating a production crash from infinite recursion caused by cyclic references in an object graph

## Interview Tips
- Always define the base case first, then the recursive case
- Draw the recursion tree to analyze time complexity (branching factor ^ depth)
- Space complexity includes the call stack depth (O(n) worst for linear recursion)
- Common edge cases: n=0, n=1, empty input, deeply nested input causing stack overflow

## Java-Specific Considerations
- Java does NOT optimize tail recursion; every recursive call adds a stack frame
- Default stack size is ~1MB (configurable with -Xss); recursion depth > 10000 risks StackOverflowError
- Alternative: use trampoline pattern (loop + Continuation) for deep recursion
- `Stack<Frame>` or `Deque<Frame>` for manual recursion-to-iteration conversion
- Pitfall: modifying shared mutable state across recursive calls (use return values instead)
- Pitfall: forgetting to mark memoization cache as static for repeated calls
