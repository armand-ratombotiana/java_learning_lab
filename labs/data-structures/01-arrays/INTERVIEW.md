# Interview Questions: Arrays

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| [LC 1 Two Sum](https://leetcode.com/problems/two-sum/) | Easy | Google, Meta, Amazon, Microsoft, Apple | HashMap lookup |
| [LC 121 Best Time to Buy and Sell Stock](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/) | Easy | Amazon, Meta, Google, Microsoft | Min tracking |
| [LC 217 Contains Duplicate](https://leetcode.com/problems/contains-duplicate/) | Easy | Amazon, Google, Apple, Microsoft | HashSet |
| [LC 238 Product of Array Except Self](https://leetcode.com/problems/product-of-array-except-self/) | Medium | Meta, Amazon, Microsoft, Google | Prefix/suffix products |
| [LC 53 Maximum Subarray](https://leetcode.com/problems/maximum-subarray/) | Medium | Amazon, Meta, Google, Microsoft | Kadane's algorithm |
| [LC 11 Container With Most Water](https://leetcode.com/problems/container-with-most-water/) | Medium | Meta, Amazon, Google, Microsoft | Two-pointer |
| [LC 42 Trapping Rain Water](https://leetcode.com/problems/trapping-rain-water/) | Hard | Amazon, Meta, Google, Microsoft | Two-pointer / stack |
| [LC 4 Median of Two Sorted Arrays](https://leetcode.com/problems/median-of-two-sorted-arrays/) | Hard | Google, Amazon, Meta, Microsoft | Binary search |
| [LC 26 Remove Duplicates from Sorted Array](https://leetcode.com/problems/remove-duplicates-from-sorted-array/) | Easy | Microsoft, Amazon, Meta | Two-pointer in-place |
| [LC 88 Merge Sorted Array](https://leetcode.com/problems/merge-sorted-array/) | Easy | Amazon, Microsoft, Meta, Apple | Two-pointer from end |

## NeetCode Reference
NeetCode 150: Arrays & Hashing category — 9 problems including Two Sum, Buy/Sell Stock, Contains Duplicate, Product Except Self, Maximum Subarray.

## Company-Specific Questions

### Google
- Find the median of two sorted arrays in O(log(min(n,m))) time
- Longest subarray with sum at most K (sliding window)
- Design a data structure that supports random access, insert/delete at any position in O(1) amortized
- How would you rotate an array in-place by k positions?

### Microsoft
- Merge two sorted arrays in-place (LC 88) — what are the edge cases?
- First missing positive integer (LC 41) — how do you mark visited elements without extra space?
- Shuffle an array using Fisher-Yates algorithm — prove correctness

### Meta
- Product of Array Except Self — solve without division and in O(1) extra space (excluding output)
- Subarray sum equals K — how does prefix sum + HashMap reduce to O(n)?
- Container With Most Water — prove two-pointer optimality

### Amazon
- Trapping Rain Water — all approaches (brute force, DP, two-pointer, stack)
- Maximum subarray using Kadane's algorithm — what if all elements are negative?
- Rotate a 2D matrix/image by 90 degrees (LC 48)

### Apple
- Move all zeros to the end while maintaining relative order (LC 283)
- Find the duplicate number in an array of n+1 integers (LC 287)
- How would you implement a circular array?

### Oracle
- Array sort stability and its implications in enterprise applications
- System.arraycopy vs Arrays.copyOf — performance differences and use cases
- How does ArrayList grow internally? What is the growth factor in different JDK versions?

## Real Production Scenarios

- **Scenario 1: Real-Time Metrics Dashboard** — A monitoring system collects metrics (CPU, memory, requests) every second into a rolling array buffer. The array serves as a circular buffer with O(1) appends and O(n) snapshot for rendering.

- **Scenario 2: Image Sensor Readout** — Embedded camera firmware reads pixel values from an array-based sensor buffer. The array must be processed line-by-line with optimal cache locality. Row-major vs column-major access affects performance by 10x.

- **Scenario 3: Log Aggregation** — A log processing pipeline reads millions of log lines into a byte array for efficient parsing. The array enables direct memory access without object overhead, crucial for throughput of 100K+ logs/second.

## Interview Tips

- Time: O(n) for single-pass problems, O(n log n) for sorting-based, O(n²) for brute force
- Space: O(1) for in-place, O(n) for auxiliary structures
- Common edge cases: empty array, single element, all same values, negative numbers, overflow
- Key patterns: two-pointer, sliding window, prefix sum, in-place modification, cyclic sort

## Java-Specific Considerations

- `int[]` vs `List<Integer>` — boxing penalty for primitive arrays vs object arrays
- `Arrays.sort()` — dual-pivot quicksort for primitives (O(n log n)), TimSort for objects (O(n log n))
- `ArrayList` grows by 50% (Java 8+), initial capacity tuning avoids resizing pauses
- `System.arraycopy()` is native — prefer it over manual loops for bulk copy
- `Arrays.asList()` returns a fixed-size list backed by original array
- `Stream.of(array)` or `Arrays.stream(arr)` for functional-style operations
