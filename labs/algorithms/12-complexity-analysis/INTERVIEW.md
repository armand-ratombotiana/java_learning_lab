# Interview Questions: Complexity Analysis

## LeetCode Problem Map
No direct LeetCode problems for complexity analysis. Theory-focused: Master Theorem, Big O derivation, amortized analysis.

## NeetCode Reference
Complexity analysis is embedded in every NeetCode problem's discussion.

## Company-Specific Questions
### Google
- Derive the time complexity of a complex recursive algorithm using Master Theorem
- Why is HashMap get() O(1) amortized? Explain the resize mechanism
- Analyze the complexity of a cache-oblivious algorithm
- Design a data structure and analyze all operations

### Microsoft
- What is the complexity of Java's Arrays.sort() and why?
- Analyze the space complexity of recursion vs iteration
- Compare complexity of different data structures for a given use case
- Explain the complexity of Windows file system operations

### Meta
- Prove that comparison-based sorting is O(n log n)
- Analyze the complexity of a backtracking algorithm with pruning
- Meta focuses on understanding worst-case vs average-case vs amortized
- "What's the complexity of your solution?" is asked after every code submission

### Amazon
- Complexity analysis of distributed algorithms (network overhead, data transfer)
- Analyze the complexity of a MapReduce job
- How does DynamoDB's consistent hashing affect operation complexity?
- Design a scalable system and analyze its bottlenecks

### Apple
- Complexity analysis under memory constraints
- How does cache size affect algorithm complexity in practice?
- Analyze complexity for mobile-specific constraints (battery, thermal)

### Oracle
- How does Oracle's CBO estimate query complexity?
- Analyze index scan vs full table scan complexity
- Explain the complexity of database join algorithms
- Design a query plan and analyze its cost model

## Real Production Scenarios
- Scenario 1: Production performance regression - investigating why a previously O(n) API call degraded to O(n^2) after a seemingly minor code change in the hot path
- Scenario 2: Query optimization - analyzing why a database query that was O(log n) with an index became O(n) after a schema migration dropped covering indices
- Scenario 3: Memory leak analysis - using space complexity analysis to identify that a cache grows without bound because entries are never evicted, causing OOM after days of operation

## Interview Tips
- Know time complexity of all major algorithms: sorting, searching, graph traversals
- Master theorem: T(n) = aT(n/b) + O(n^d) -> O(n^d) if a < b^d, O(n^d log n) if a = b^d, O(n^(log_b a)) if a > b^d
- Amortized analysis: aggregate, accounting, and potential methods
- Common edge cases: recursion depth, hidden loops, library function complexity, parallel overhead
- Big O is asymptotic; understand constants and real-world performance too

## Java-Specific Considerations
- Java's `List.contains()` is O(n) on ArrayList, O(1) amortized on HashSet
- `String.substring()` was O(1) pre-Java 7u6, O(n) post (new char[] copy)
- Stream API operations: intermediate ops are lazy, terminal ops trigger computation
- `ConcurrentHashMap` size() is O(1) but requires segment counting
- Pitfall: assuming `ArrayList.get()` is O(1) but forgetting the insert cost
- Pitfall: not accounting for autoboxing overhead in time complexity analysis
