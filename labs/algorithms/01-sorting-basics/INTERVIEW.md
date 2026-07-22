# Interview Questions: Sorting Basics

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| LC 912 Sort an Array | Medium | Amazon, Microsoft | Merge sort / Quick sort |
| LC 75 Sort Colors | Medium | Google, Meta, Amazon | Dutch National Flag |
| LC 283 Move Zeroes | Easy | Meta, Amazon, Apple | Two pointers |
| LC 147 Insertion Sort List | Medium | Microsoft | Insertion sort on linked list |
| LC 148 Sort List | Medium | Google, Meta | Merge sort on linked list |

## NeetCode Reference
- LC 912 Sort an Array (NeetCode 150)
- LC 75 Sort Colors (NeetCode 150)
- LC 283 Move Zeroes (NeetCode 150)
- LC 148 Sort List (NeetCode 150)

## Company-Specific Questions
### Google
- Design a sorting algorithm for 1TB of log files with limited RAM
- How would you sort a nearly-sorted array efficiently?
- Implement a custom comparator for non-standard ordering rules

### Microsoft
- Explain why Java uses TimSort for objects and Dual-Pivot QuickSort for primitives
- Design a sorting algorithm for linked lists in O(n log n) time and O(1) space
- How does Windows Explorer sort files with natural number ordering?

### Meta
- Sort colors / Dutch National Flag is a Meta favorite
- Questions focus on stable sorting and in-place constraints
- Expect real-world scenarios involving sorting user feeds by multiple criteria

### Amazon
- Sort an array of products by price, rating, and relevance
- How does DynamoDB sort data across partitions?
- Implement a custom sorter for shipping cost optimization

### Apple
- Memory-constrained sorting on embedded devices
- How would you sort a linked list with O(1) extra space?
- In-place sorting algorithms for low-memory environments

### Oracle
- How does Oracle sort query results without enough memory for the full sort?
- Design a sorting algorithm for database index creation with external merge sort
- Explain sort-merge join and when it outperforms hash join

## Real Production Scenarios
- Scenario 1: ETL pipeline sorting - sorting 100GB of customer transactions by timestamp across distributed worker nodes
- Scenario 2: Autocomplete ranking - sorting candidate suggestions by a composite score of popularity, recency, and user history
- Scenario 3: Log file analysis - diagnosing a production incident by sorting time-ordered logs from multiple microservices with clock skew

## Interview Tips
- Know the trade-off between stable (Merge, TimSort) and unstable (Quick, Heap) sorts
- Quick Sort is O(n^2) worst-case; in practice it's fastest due to cache locality
- Arrays.sort() uses Dual-Pivot QuickSort for primitives, TimSort for objects
- Common edge cases: already sorted arrays, reverse sorted arrays, all equal elements

## Java-Specific Considerations
- `Arrays.sort()` and `Collections.sort()` are the go-to; implement `Comparable` or pass `Comparator`
- For custom sort performance, understand that TimSort is O(n) on partially sorted data
- `List.sort()` modifies in-place; `Stream.sorted()` creates a new list
- Pitfall: using `Comparator` with inconsistent equality (violating compare-equals contract)
- Pitfall: sorting with `null` values in the list requires explicit null-handling in comparator
- Consider `ParallelSort` for large arrays on multi-core systems
