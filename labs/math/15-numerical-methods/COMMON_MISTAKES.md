# Common Mistakes: Numerical Methods

## Conceptual Mistakes

### 1. Misunderstanding Assumptions
Many theorems and algorithms have specific assumptions (e.g., positive definiteness, coprimality, differentiability). Applying them without verifying assumptions leads to incorrect results.

### 2. Ignoring Edge Cases
Zero vectors, singular matrices, empty sets, uniform distributions, and boundary conditions often break naive implementations.

### 3. Confusing Notation
Mathematical notation and programming notation differ. Be precise about what each symbol represents.

### 4. Overgeneralization
Not all properties that hold for small cases generalize. Counterexamples exist for seemingly natural conjectures.

## Implementation Mistakes

### 1. Integer Overflow
Using int for intermediate computations that exceed 2^31-1. Use long or BigInteger appropriately.

### 2. Floating Point Errors
Comparing floating point values with ==. Always use tolerance-based comparisons.

### 3. Off-by-One Errors
Loop bounds, array indices, and mathematical index conventions must match.

### 4. Missing Input Validation
Failing to check preconditions (non-null inputs, valid dimensions, positive values).

### 5. Inefficient Algorithms
Using naive algorithms when efficient alternatives exist (e.g., trial division vs. Miller-Rabin).

### 6. Thread Safety
Using non-thread-safe random number generators or mutable shared state in concurrent contexts.

### 7. Memory Leaks
Not releasing large temporary matrices or arrays in long-running applications.

### 8. Silent Failures
Returning incorrect results without indicating failure when numerical issues arise.

## Debugging Strategies

- Test with known simple cases first
- Verify intermediate results
- Compare against reference implementations
- Use assertions to check invariants
- Profile to find performance bottlenecks
- Test edge cases explicitly
- Use tolerance-based comparisons for floating point
- Log or print intermediate values when debugging
