# Divide and Conquer — Theoretical Foundation

## The Paradigm
1. **Divide**: Break problem into smaller subproblems
2. **Conquer**: Solve subproblems recursively
3. **Combine**: Merge subproblem solutions into final solution

## Classic Examples
- Merge Sort: Divide array, sort halves, merge
- Quick Sort: Partition array, sort partitions
- Binary Search: Divide search space in half
- Closest Pair: Divide points, find closest in halves and strip
- Strassen's Matrix Multiplication: O(n²·⁸¹)
- Karatsuba Multiplication: O(n¹·⁵⁸)

## Complexity Analysis
Use Master Theorem: T(n) = aT(n/b) + f(n)
- Merge Sort: T(n) = 2T(n/2) + O(n) → O(n log n)
- Binary Search: T(n) = T(n/2) + O(1) → O(log n)
- Closest Pair: T(n) = 2T(n/2) + O(n) → O(n log n)
