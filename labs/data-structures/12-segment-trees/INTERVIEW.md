# Interview Questions: Segment Trees

## Basic Questions

1. **What is a segment tree and what problem does it solve?**
   A segment tree is a binary tree that stores array segment information. It solves the problem of needing both efficient range queries and updates on an array.

2. **Explain how to build a segment tree.**
   Recursively partition the array into halves until reaching single elements. Each node stores the aggregate (sum, min, max) of its segment. Combine children's values to compute parent values.

3. **What is the time and space complexity of a segment tree?**
   Build: O(n). Query: O(log n). Update: O(log n). Space: O(n) using 4n array.

## Problem-Solving Questions

4. **Range Sum Query** (LeetCode 307)
   Given an array, implement range sum queries with point updates. Solution: Standard segment tree or Fenwick tree.

5. **Range Sum Query 2D** (LeetCode 308)
   Given a 2D matrix, support submatrix sum queries and point updates. Solution: 2D segment tree or 2D BIT.

6. **My Calendar I** (LeetCode 729)
   Book events without double-booking. Solution: Segment tree with range max query and range update.

7. **Falling Squares** (LeetCode 699)
   Track height of falling squares. Solution: Segment tree with range max and range update.

8. **Range Module** (LeetCode 715)
   Track intervals with track, add, and remove operations. Solution: Segment tree with lazy propagation.

## Advanced Questions

9. **Design a data structure for range minimum query with point updates** (Google)
   Solution: Segment tree storing min values per range.

10. **Implement a range update + range sum segment tree** (Facebook)
    Solution: Segment tree with lazy propagation for range addition.

11. **Dynamic segment tree for large ranges** (Amazon)
    Solution: Implement using node objects (not array) that allocate children on demand.

## Tips for Interview

1. Always clarify whether ranges are inclusive or exclusive
2. Mention that 4n size accounts for all possible node allocations
3. Distinguish between segment trees (any associative op) and Fenwick trees (sum only)
4. For 2D problems, mention that nested segment trees or BITs give O(log^2 n)

## Common Follow-ups

- "Can you optimize space?" â†’ Use iterative implementation (2n)
- "Can you handle updates?" â†’ Add point update or range update with lazy propagation
- "Can you handle large values?" â†’ Coordinate compression or dynamic allocation
- "What about concurrency?" â†’ Immutable persistent segment trees for concurrent reads
