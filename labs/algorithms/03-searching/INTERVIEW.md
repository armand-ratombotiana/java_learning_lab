# Interview Questions: Searching

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| LC 704 Binary Search | Easy | Any FAANG | Classic binary search |
| LC 34 First and Last Position | Medium | Google, Meta, Amazon | Binary search bounds |
| LC 33 Search in Rotated Array | Medium | Google, Meta, Microsoft | Modified binary search |
| LC 81 Search Rotated Array II | Medium | Google | Handle duplicates |
| LC 153 Find Min in Rotated | Medium | Google, Amazon | Binary search |
| LC 162 Find Peak Element | Medium | Google, Meta | Binary search on range |
| LC 74 Search 2D Matrix | Medium | Meta, Amazon, Microsoft | Binary search on flattened |
| LC 240 Search 2D Matrix II | Medium | Google, Amazon, Apple | Search from corner |
| LC 4 Median of Two Sorted | Hard | Google, Meta, Amazon | Binary search on partitions |

## NeetCode Reference
- LC 704 Binary Search (NeetCode 150)
- LC 34 First and Last Position (NeetCode 150)
- LC 33 Search in Rotated Array (NeetCode 150)
- LC 153 Find Min in Rotated (NeetCode 150)
- LC 4 Median of Two Sorted (NeetCode 150)

## Company-Specific Questions
### Google
- Design a search algorithm for a trillion sorted integers on disk
- How would you implement autocomplete search with prefix lookup?
- Search in a rotated array with unknown pivot - all variants tested

### Microsoft
- How does Bing's search index work under the hood?
- Design a search feature for OneDrive file content
- Implement binary search on a linked list (trap question)

### Meta
- Search in 2D matrix and rotated array are Meta staples
- Focus on boundary conditions and off-by-one errors
- Expect follow-up questions about infinite streams or unknown-size arrays

### Amazon
- Design DynamoDB's query engine search across partitions
- How does product search ranking work with multiple sort criteria?
- Search with wildcards and fuzzy matching in product catalog

### Apple
- Search in memory-constrained environments (Apple Watch)
- Implement a search algorithm that uses minimal comparisons
- Peak element finding for signal processing on device

### Oracle
- How does Oracle B-tree index search work?
- Design a search algorithm for compressed database blocks
- Explain Oracle's index skip scan and when it's used

## Real Production Scenarios
- Scenario 1: Search index lookup - finding documents matching a query across a distributed search index with shard routing
- Scenario 2: Time-series database query - implementing efficient range queries on sorted timestamp data with billions of points
- Scenario 3: Debugging binary search - diagnosing an infinite loop in production code due to integer overflow in mid calculation

## Interview Tips
- Master the three binary search templates: basic, lower bound, upper bound
- For rotated arrays, always compare `mid` with `left` or `right` to determine which half is sorted
- Median of Two Sorted Arrays is a hard problem; understand partition-based approach
- Common edge cases: empty arrays, single element, duplicates, array not found

## Java-Specific Considerations
- `Arrays.binarySearch()` and `Collections.binarySearch()` for standard use
- Custom binary search: `int mid = left + (right - left) / 2` avoids overflow
- For 2D binary search: treat as flattened with `matrix[mid / cols][mid % cols]`
- Pitfall: implicit conversion in `mid = (left + right) / 2` causing overflow for large arrays
- Pitfall: forgetting that `binarySearch` returns `-(insertionPoint) - 1` when not found
- `TreeSet` and `TreeMap` provide `floor()`, `ceiling()`, `lower()`, `higher()` for nearest searches
