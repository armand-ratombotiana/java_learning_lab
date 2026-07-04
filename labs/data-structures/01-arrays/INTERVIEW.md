# Interview Questions: Arrays

## Easy

1. **Two Sum** — Given an array of integers and a target, return indices of two numbers that add up to target. (HashMap solution)

2. **Best Time to Buy and Sell Stock** — Find max profit from a single buy/sell. (Track min price so far)

3. **Contains Duplicate** — Return true if any value appears at least twice. (HashSet)

4. **Product of Array Except Self** — Compute product of all elements except current, without division.

## Medium

5. **Maximum Subarray** — Kadane's algorithm for max contiguous sum.

6. **Rotate Array** — Rotate by k steps in O(1) extra space (reverse algorithm).

7. **First Missing Positive** — Smallest missing positive integer in O(n) time, O(1) space.

8. **Container With Most Water** — Find two lines that form max water container (two-pointer).

## Hard

9. **Median of Two Sorted Arrays** — O(log(min(n,m))) binary search solution.

10. **Trapping Rain Water** — Compute how much water can be trapped between bars.

11. **Maximum Gap** — O(n) using pigeonhole principle / bucket sort.

## Key Patterns

- **Two pointers**: opposite ends or slow/fast
- **Sliding window**: subarray problems
- **Prefix sum**: range sum queries
- **In-place modification**: reuse array for O(1) space
- **Cyclic sort**: place elements at correct indices

## Java-Specific Interview Topics

- Difference between `int[]` and `List<Integer>` (boxing penalty)
- Why `Arrays.asList()` returns a fixed-size list
- `ArrayList` vs `LinkedList` — when to use each
- `System.arraycopy` vs `Arrays.copyOf` vs manual loop
