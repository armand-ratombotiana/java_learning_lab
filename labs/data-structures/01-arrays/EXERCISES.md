# Exercises: Arrays

## Basic

1. **Implement a dynamic array** — Create a generic `DynamicArray<T>` class with `add`, `get`, `set`, `remove`, `size`, `isEmpty`. Use 1.5x growth factor.

2. **Array rotation** — Rotate an array of n elements to the right by k steps. Solve with O(1) extra space (reversal algorithm).

3. **Two-sum** — Given a sorted array and a target, find two indices whose values sum to target. Solve in O(n) time, O(1) space.

4. **Remove duplicates** — Remove duplicates from a sorted array in-place, return new length. O(n) time, O(1) space.

## Intermediate

5. **Kadane's algorithm** — Find the maximum subarray sum in O(n).

6. **Merge intervals** — Given an array of intervals `[start, end]`, merge overlapping intervals.

7. **Product of array except self** — Compute `output[i]` = product of all elements except `arr[i]` without division. O(n) time, O(1) extra space.

8. **First missing positive** — Find the smallest missing positive integer in O(n) time, O(1) space using the array itself as a hash set.

## Advanced

9. **Sparse array** — Implement a sparse array using two parallel arrays (indices + values) or a binary search tree.

10. **Median of two sorted arrays** — Find the median of two sorted arrays in O(log(min(n, m))) time.

11. **Maximum gap** — Given an unsorted array, find the maximum difference between successive elements in sorted form. Solve in O(n) time using pigeonhole principle (bucket sort).

12. **Range sum queries with updates** — Implement a structure supporting `update(index, value)` and `sumRange(left, right)` in O(log n) each (Fenwick tree on array).
