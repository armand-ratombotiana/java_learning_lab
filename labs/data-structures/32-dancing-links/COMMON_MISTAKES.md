# Common Mistakes: Dancing Links (DLX)

## Design Errors

### Incorrect Invariant Maintenance
The most common and serious mistake is failing to maintain structural invariants after operations. Always verify that insert, delete, and update operations leave the structure in a valid state.

### Off-by-One Errors
Operations involving indices or positions are prone to off-by-one errors. Carefully validate boundary conditions, especially with inclusive versus exclusive ranges.

## Implementation Mistakes

### Null Pointer Issues
Failing to check for null references when accessing child nodes or following pointers. This is especially common in recursive implementations.

### Infinite Loops
Recursive operations that terminate incorrectly can cause stack overflow. Iterative implementations can loop forever if pointer updates are incorrect.

### Memory Leaks
Unreachable nodes that remain referenced prevent garbage collection. In persistent structures, old versions accumulate unless explicitly discarded.

## Performance Mistakes

### Excessive Allocation
Creating unnecessary node objects or temporary data structures increases GC pressure. Reuse objects when possible.

### Poor Cache Locality
Scattered memory access patterns degrade performance. Consider using arrays instead of node objects for cache-friendly implementations.

## Algorithmic Mistakes

### Wrong Complexity
Using an algorithm with worse asymptotic complexity than necessary. For example, linear search in a structure designed for log-time operations.

### Ignoring Worst Cases
Assuming average-case performance without considering adversarial inputs. Randomized structures mitigate this but are not immune.

## Testing Mistakes

### Insufficient Coverage
Testing only happy paths. Include edge cases, error conditions, and stress tests with large datasets.
