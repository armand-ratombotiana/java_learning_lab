# Common Mistakes: Cache-Oblivious Data Structures

## Implementation Pitfalls

1. **Incorrect Hash Function**: Poor distribution leads to collisions and degraded performance.

2. **Off-by-One Errors**: Double-check boundary conditions in array accesses and comparisons.

3. **Forgetting to Update Metadata**: All relevant metadata must be updated after modifications.

4. **Improper Rebalancing**: Failure to rebalance at thresholds leads to degradation.

5. **Infinite Loops**: Recursive implementations must have proper base cases.

## Design Mistakes

1. **Ignoring Constant Factors**: Consider cache behavior and allocation overhead.

2. **Over-Engineering**: Start simple, optimize after profiling.

3. **Inadequate Testing**: Test edge cases including empty, single, full, and adversarial inputs.
