# Common Mistakes: Space-Filling Curves

## Implementation Pitfalls

### 1. Incorrect Hash Function

Using a poor hash function leads to collisions and degraded performance. Ensure the hash function provides good distribution across the key space.

### 2. Off-by-One Errors

Index calculations are a common source of bugs. Double-check boundary conditions in array accesses and comparisons.

### 3. Forgetting to Update Metadata

Operations that modify the structure must update all relevant metadata including size, depth, and structural properties.

### 4. Improper Rebalancing

Failure to rebalance when the structure exceeds load factor thresholds leads to performance degradation.

### 5. Infinite Loops in Recursive Operations

Recursive implementations must have proper base cases to terminate, especially during rebalancing operations.

### 6. Not Handling Duplicates

Decide whether duplicates are allowed and handle them consistently across all operations.

## Design Mistakes

### 1. Ignoring Constant Factors

Asymptotic analysis alone is insufficient. Consider cache behavior, memory allocation overhead, and branching costs.

### 2. Over-Engineering

Start with a simple implementation and optimize only after profiling identifies bottlenecks.

### 3. Inadequate Testing

Test edge cases extensively. Unit tests should cover empty, single-element, full, and adversarial input scenarios.
