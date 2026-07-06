# Interview Questions: Fenwick Tree

## Basic Questions

1. **Explain the Fenwick tree data structure.**
   A tree structure using an array where each index i stores the sum of a range ending at i, with length determined by the LSB of i.

2. **What is the time and space complexity of BIT?**
   O(log n) per operation, O(n) space.

3. **How does the LSB function work?**
   i & -i extracts the lowest set bit using two's complement negation.

## Problem-Solving Questions

4. **Range Sum Query with Updates** (LeetCode 307)
   Maintain prefix sums with point updates. Both BIT and segment tree work.

5. **Count of Smaller Numbers After Self** (LeetCode 315)
   Count, for each element, how many smaller elements are to its right. Use BIT with coordinate compression.

6. **Range Sum Query 2D** (LeetCode 308)
   2D matrix with point updates and submatrix sum queries. Use 2D BIT.

7. **Reverse Pairs** (LeetCode 493)
   Count significant inversions. Use BIT with careful coordinate compression.

8. **Longest Increasing Subsequence** (LeetCode 300)
   Can be solved with BIT in O(n log n).

## Advanced Questions

9. **Design a data structure for range sum with range updates** (Google)
   Use two BITs for range updates and range queries.

10. **Implement an order statistics tree** (Amazon)
    BIT can support insert, delete, and find-kth in O(log n).

11. **Maximum sum of k-disjoint intervals** (Facebook)
    Use BIT for dynamic programming optimization.

## Tips for Interview

1. Always mention the 1-indexed internal representation
2. Clearly explain LSB and its role in navigation
3. Distinguish between BIT (prefix sum) and segment tree (general range)
4. For range update problems, explain the two-BIT approach
5. For inversion problems, mention coordinate compression
