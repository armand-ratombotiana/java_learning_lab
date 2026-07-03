# Searching — Theoretical Foundation

## Linear Search
Sequentially checks each element until target is found.
- Time: O(n) avg/worst, O(1) best
- Space: O(1)
- Prerequisite: None (works on unsorted data)

## Binary Search
Repeatedly divides sorted array in half.
- Time: O(log n)
- Space: O(1) iterative, O(log n) recursive
- Prerequisite: Sorted array

## Interpolation Search
Estimates position using probe formula (uniform distribution).
- Time: O(log log n) avg, O(n) worst
- Space: O(1)
- Prerequisite: Sorted, uniformly distributed data
