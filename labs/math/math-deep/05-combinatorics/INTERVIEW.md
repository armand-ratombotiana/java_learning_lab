# Interview: Combinatorics

## Q1: Conceptual Understanding
**Q**: Explain the inclusion-exclusion principle with an example.
**A**: It counts elements in a union by summing individual sets, subtracting pairwise intersections, adding triple intersections, etc. Example: count numbers ≤ 100 divisible by 2 or 3: 100/2 + 100/3 - 100/6 = 67.

## Q2: Implementation
**Q**: How would you generate the nth permutation of a set in lexicographic order?
**A**: Factoradic representation: divide n by (k-1)!, the quotient gives the element index, remainder continues. O(k) time to generate.

## Q3: Performance
**Q**: How to compute C(n,k) for large n,k without overflow?
**A**: Use multiplicative formula: C(n,k) = Π_{i=1}^{k} (n-k+i)/i. Or use Pascal's triangle with modulo arithmetic. For huge values, use BigInteger with repeated multiplication and GCD cancellation.

## Coding Challenge
Implement a function that returns all permutations of an array using Heap's algorithm.
