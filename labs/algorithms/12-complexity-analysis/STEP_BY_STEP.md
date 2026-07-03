# Step-by-Step — Complexity Analysis

## Analyzing an Algorithm
1. Identify basic operations (comparisons, swaps, array accesses)
2. Determine how many times each operation executes
3. Express as function of input size n
4. Find the dominant term (highest growth rate)
5. Drop constants and lower-order terms

## Recursion Analysis
1. Write recurrence relation: T(n) = aT(n/b) + f(n)
2. Draw recursion tree (optional)
3. Apply Master Theorem if applicable
4. Verify with substitution method

## Common Pitfalls
- Ignoring constants when they matter (n=10 vs n=10⁶)
- Forgetting about space complexity
- Confusing average-case with worst-case
- Not accounting for input distribution
