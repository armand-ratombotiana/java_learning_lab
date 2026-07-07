# Mathematical Foundation of Space-Filling Curves

## Asymptotic Analysis

### Time Complexity

Operations achieve logarithmic expected and amortized time bounds.

### Space Complexity

Total space is O(n) for n elements with constant overhead per element.

## Key Mathematical Concepts

### The Power of Recursion

Recursive decomposition yields recurrences that solve to sublinear bounds.

### Probability and Randomization

Error probabilities can be made arbitrarily small through repetition for randomized variants.

### Amortization

Amortized analysis uses potential functions to bound total operation sequence costs.

## Proof Sketches

### Correctness Proof

Invariants guarantee every element is stored according to rules, every operation preserves invariants, and every query returns the correct result.

### Complexity Proof

The complexity follows from bounded structure depth, O(1) work per level, and infrequent rebalancing that amortizes.

## Edge Cases

The analysis accounts for empty structures, single-element operations, full-capacity scenarios, and repeated elements.
