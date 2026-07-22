# Interview Questions: Advanced Sorting

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| LC 215 Kth Largest Element in Array | Medium | Google, Meta, Amazon, Microsoft | QuickSelect / Heap |
| LC 347 Top K Frequent Elements | Medium | Google, Meta, Amazon | Bucket sort / Heap |
| LC 912 Sort an Array | Medium | Amazon, Microsoft | Merge sort |
| LC 148 Sort List | Medium | Google, Meta | Merge sort |
| LC 324 Wiggle Sort II | Medium | Google | Virtual indexing |
| LC 493 Reverse Pairs | Hard | Google | Merge sort (Fenwick) |
| LC 327 Count Range Sum | Hard | Google | Merge sort / BIT |

## NeetCode Reference
- LC 215 Kth Largest Element (NeetCode 150)
- LC 347 Top K Frequent Elements (NeetCode 150)

## Company-Specific Questions
### Google
- Reverse Pairs and Count Range Sum are Google signature questions
- How would you sort a 10GB file with only 512MB RAM?
- Design an external sort algorithm that minimizes disk seeks

### Microsoft
- How does SQL Server choose between sort-merge, hash, and nested loop joins?
- Design a stable external sort for Azure Data Lake
- Explain dual-pivot Quick Sort vs TimSort performance characteristics

### Meta
- Top K Frequent Elements is a Meta favorite
- Focus on linear-time selection (QuickSelect) and its average-case analysis
- May combine sorting with system design for News Feed ranking

### Amazon
- How does DynamoDB sort results across partitions?
- Implement a distributed sort for Redshift intermediate results
- Sort products by a weighted score of price, reviews, and availability

### Apple
- Design a memory-efficient sort for devices with 1GB RAM
- How would you find the median of a stream on Apple Watch?
- Implement an external sort that works on flash storage with wear leveling

### Oracle
- How does Oracle parallel sort work across RAC nodes?
- Design a sort algorithm for database index rebuild with minimal temp space
- Explain sort area size and how Oracle manages PGA memory for sorts

## Real Production Scenarios
- Scenario 1: Distributed log sorting - sorting terabytes of CDN logs across a Spark cluster using external merge sort with custom partitioning
- Scenario 2: Real-time analytics - computing top-K trending hashtags from a stream of millions of tweets per minute
- Scenario 3: E-commerce ranking - debugging a product sort that fails when certain products have identical scores, causing pagination flickering

## Interview Tips
- QuickSelect for O(n) average, O(n^2) worst-case; heap for O(n log k) guaranteed
- External merge sort: split into chunks, sort each, merge with k-way merge
- Reverse Pairs and Count Range Sum test your ability to modify merge sort
- Common edge cases: duplicates, single element, k larger than array size

## Java-Specific Considerations
- `PriorityQueue` for top-K (min-heap for largest K, max-heap for smallest K)
- `Arrays.parallelSort()` uses ForkJoin for parallel merge sort
- Custom sort with `Comparator` for complex ordering; ensure contract consistency
- Pitfall: integer overflow in range sum problems (use `long` for prefix sums)
- For external sort: `BufferedReader`/`BufferedWriter` with sorted temporary files
