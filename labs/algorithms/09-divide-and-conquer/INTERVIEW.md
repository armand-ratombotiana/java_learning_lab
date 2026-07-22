# Interview Questions: Divide and Conquer

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| LC 53 Maximum Subarray | Medium | Google, Meta, Amazon, Microsoft | D&C / Kadane's |
| LC 315 Count of Smaller Numbers After Self | Hard | Google | Merge sort D&C |
| LC 327 Count Range Sum | Hard | Google | Merge sort D&C |
| LC 493 Reverse Pairs | Hard | Google | Merge sort D&C |
| LC 218 The Skyline Problem | Hard | Google, Microsoft | D&C merge |
| LC 23 Merge K Sorted Lists | Hard | Google, Meta, Amazon, Microsoft | D&C / heap |
| LC 169 Majority Element | Easy | Google, Meta, Amazon | D&C / Boyer-Moore |

## NeetCode Reference
- LC 53 Maximum Subarray (NeetCode 150)
- LC 23 Merge K Sorted Lists (NeetCode 150)
- LC 169 Majority Element (NeetCode 150)
- LC 218 The Skyline Problem (NeetCode 150)

## Company-Specific Questions
### Google
- Count of Smaller Numbers After Self, Reverse Pairs, and Count Range Sum are Google signatures
- Master the merge-sort modification pattern for counting problems
- D&C is Google's way of testing if you can split problems recursively and combine solutions

### Microsoft
- Merge K Sorted Lists is a Microsoft favorite
- How would you merge N sorted streams from distributed databases?
- D&C for parallel processing of large datasets

### Meta
- Maximum Subarray with follow-up (how to return the subarray, not just sum)
- Majority Element with follow-up (O(1) memory Boyer-Moore)
- Skyline problem tests your ability to manage complex merge logic

### Amazon
- Merge K Sorted product feeds from different vendors
- D&C for processing large product catalogs
- How would you find the most popular product using majority element?

### Apple
- Cache-optimized D&C for mobile processors
- Implementing D&C with minimal memory overhead
- Merge sort for large files on flash storage

### Oracle
- How does Oracle parallelize D&C operations in query execution?
- Design a D&C approach for distributed database sort
- Explain Oracle's recursive query processing strategy

## Real Production Scenarios
- Scenario 1: Log anomaly detection - using merge-sort divide and conquer to count inversions in time-series data to detect unusual patterns across millions of data points
- Scenario 2: Distributed computation - splitting a large map-reduce job into subproblems, processing in parallel, and merging results for a nightly ETL pipeline
- Scenario 3: Skyline extraction - debugging a city skyline algorithm that produces incorrect results due to improper handling of overlapping buildings in a visualization tool

## Interview Tips
- D&C has three steps: divide, conquer (recurse), combine
- Merge sort is the canonical example; understand how to modify it for counting problems
- The master theorem: T(n) = aT(n/b) + O(n^d) gives complexity analysis
- Common edge cases: single element, empty input, all elements equal, reversed order

## Java-Specific Considerations
- Implement merge sort with `int[] temp = new int[n]` auxiliary array for merging
- D&C is naturally parallelizable with `ForkJoinPool` and `RecursiveAction`/`RecursiveTask`
- For skyline: `List<int[]>` with custom comparison for merging strip lists
- Pitfall: creating new arrays at every recursion level (use pre-allocated temp)
- Pitfall: integer overflow in range sum problems (use `long` for prefix sums)
- `Arrays.copyOfRange()` for subarray creation; be mindful of memory usage
