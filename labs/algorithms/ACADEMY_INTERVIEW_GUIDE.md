# Algorithms Academy — Interview Preparation Guide

## Overview

This guide consolidates interview preparation strategies across major tech companies, focusing specifically on algorithms. Master these patterns and you will be prepared for any algorithm-focused interview round.

---

## Google Interview: Algorithm Focus

Google is known for the hardest algorithm interviews in the industry. They test depth of understanding, optimization, and ability to handle follow-up constraints.

### Algorithm Focus Areas

| Area | Weight | Why Google Cares | Example Problems |
|------|--------|-----------------|-----------------|
| Dynamic Programming | Very High | Optimal substructure, state definition | Coin Change, Edit Distance, Burst Balloons |
| Graphs | Very High | Real-world data is graph-structured | Word Ladder, Alien Dictionary, Course Schedule |
| Strings | High | Search, NLP, pattern matching | Longest Substring, Rabin-Karp, KMP |
| Optimization | High | Scalability, performance tuning | Median of Two Sorted Arrays, Trapping Rain Water |

### Typical Round Breakdown

**Phone Screen (45 min):**
- Problem 1: Medium difficulty, typically arrays or strings (Longest Substring Without Repeating)
- Problem 2: Medium-Hard if there is time (or a follow-up to problem 1)

**Onsite Coding Round 1 (45 min):**
- Algorithm design: Often DP or graph
- Example: "Find the longest increasing path in a matrix" (LC 329)
- Expected: Brute force → memoization → optimal DP

**Onsite Coding Round 2 (45 min):**
- Complex algorithm with multiple passes
- Example: "Serialize and deserialize a binary tree" (LC 297)
- Expected: Compare BFS vs DFS, handle edge cases

**Onsite Coding Round 3 (45 min):**
- Very hard problem, often involving a clever insight
- Example: "Median of two sorted arrays in O(log(min(n,m)))" (LC 4)

### Google-Specific Algorithm Tips
- State the brute force solution first, then optimize
- Prove optimality: "This is O(n) because we must examine each element"
- Discuss all trade-offs in detail
- Google loves binary search on answer patterns
- Know how to solve problems that combine multiple algorithm paradigms

---

## Microsoft Interview: Algorithm Focus

Microsoft interviews emphasize fundamentals and real-world problem-solving.

| Algorithm Area | Focus | Example Problems |
|---------------|-------|-----------------|
| Sorting | Stability, comparison vs linear | Sort Colors, Merge Sort, Quick Sort |
| Searching | Binary search variants, search space | Search in Rotated Array, Find Peak Element |
| Recursion | Tree recursion, backtracking | Subsets, Permutations, N-Queens |
| String algorithms | Pattern matching | KMP, Rabin-Karp, String to Integer |

### Microsoft-Specific Algorithm Tips
- Expect questions about sort stability and why it matters
- Binary search on a range (not just an array) is a common pattern
- Recursion depth analysis: iterative vs recursive trade-offs
- Microsoft values O(n) solutions for most problems
- Coding style: clean, commented, production-ready

---

## Meta Interview: Algorithm Focus

Meta (Facebook) focuses on breadth-first algorithmic thinking with time pressure.

| Algorithm Area | Weight | Example Problems |
|---------------|--------|-----------------|
| Dynamic Programming | Very High | Coin Change, House Robber, Word Break |
| Greedy | High | Jump Game, Task Scheduler, Interval Scheduling |
| Recursion | High | Generate Parentheses, Letter Combinations |
| Backtracking | High | Subsets, Permutations, Combination Sum |

### Meta-Specific Algorithm Tips
- Meta asks 2 problems in 45 minutes — time management is critical
- They prefer O(n) solutions; O(n^2) may be too slow
- Practice verbalizing while coding — Meta interviewers rarely interrupt
- Follow-ups: What if input is 10x larger? What if it's a stream?
- Many Meta problems have a "trick" — once you see it, the code is simple

---

## Amazon Interview: Algorithm Focus

Amazon connects algorithm questions to real-world engineering challenges.

| Algorithm Area | Focus | Example Problems |
|---------------|-------|-----------------|
| Greedy | Optimization under constraints | Jump Game II, Task Scheduler, Gas Station |
| Dynamic Programming | Resource allocation | Knapsack, Coin Change, Partition Equal Subset Sum |
| Optimization | Efficiency at scale | Minimum Cost to Connect Sticks, Top K Frequent |
| Graph | Route optimization | Course Schedule, Network Delay Time |

### Amazon Leadership Principles and Algorithms
- **Customer Obsession**: How does your algorithm improve user experience?
- **Deliver Results**: Optimize for what matters most (time, space, or both)
- **Dive Deep**: Be ready to analyze every line of code for edge cases

### Amazon-Specific Algorithm Tips
- Expect "real-world" problem framing (delivery routes, inventory, recommendations)
- Always discuss scalability: "What if we have 1 billion items?"
- Bar raiser may ask a non-coding algorithm design question
- Practice the STRAIGHT approach: Situation, Task, Resolution, Analysis, Implementation, Gateway, Handling

---

## Apple Interview: Algorithm Focus

Apple cares about algorithms that work efficiently on memory-constrained devices.

| Algorithm Area | Focus | Example Problems |
|---------------|-------|-----------------|
| Recursion | Elegant, stack-depth aware | Pow(x,n), Generate Parentheses, Permutations |
| Bit Manipulation | Memory efficiency | Single Number, Counting Bits, Power of Two |
| Math | Number theory, geometry | Reverse Integer, Palindrome Number, GCD |
| In-place operations | Low memory overhead | Rotate Image, Reverse Words, Next Permutation |

### Apple-Specific Algorithm Tips
- Memory constraints are often explicit in the problem statement
- Recursive solutions must account for limited stack depth
- Bit manipulation and bit-level optimization are valued
- Apple prefers iterative solutions when recursion could overflow
- Test code very thoroughly — Apple interviewers run your code

---

## Oracle Interview: Algorithm Focus

Oracle focuses on correctness, complexity analysis, and enterprise-grade solutions.

| Algorithm Area | Focus | Example Problems |
|---------------|-------|-----------------|
| Sorting | External sorting, merge sort | External sort, Sort Large File, Merge Sort |
| Searching | Indexing, optimization | Binary search on database indexes, Search in Rotated Array |
| Complexity Analysis | Amortized, worst-case, average | ArrayList growth amortization, HashMap collision |
| Mathematical algorithms | Correctness proof | Fast exponentiation, Large integer multiplication |

### Oracle-Specific Algorithm Tips
- Deep Java-specific algorithm knowledge required
- Amortized analysis questions are common
- Know how Java's sort works (TimSort, Dual-Pivot QuickSort)
- Be ready to discuss concurrent algorithmic correctness
- Oracle values provably correct solutions over clever hacks

---

## LeetCode Roadmap: Blind 75 → NeetCode 150 → Company-Specific

### Full Progression Path

```
Blind 75 → NeetCode 150 → Company-Specific Tagged Problems → LeetCode Hard
```

### Phase 1: Blind 75 (Weeks 1-4)

| Category | Problems | Pattern |
|----------|----------|---------|
| Array | Two Sum, Best Time to Buy/Sell Stock, Contains Duplicate, Product of Array Except Self, Maximum Subarray | HashMap, prefix, Kadane |
| String | Longest Substring Without Repeating, Longest Palindromic Substring, Group Anagrams, Valid Parentheses | Sliding window, expand |
| Linked List | Reverse Linked List, Merge Two Sorted Lists, Linked List Cycle | Pointer manipulation |
| Tree | Max Depth, Validate BST, Level Order, Serialize/Deserialize | BFS, DFS, recursion |
| Graph | Number of Islands, Clone Graph, Course Schedule | BFS, DFS, topological |
| DP | Climbing Stairs, Coin Change, Longest Increasing Subsequence | Fibonacci, knapsack |
| Interval | Insert Interval, Merge Intervals, Non-overlapping Intervals | Sort + merge |
| Heap | Top K Frequent Elements, Kth Largest, Merge K Sorted | Heap patterns |

### Phase 2: NeetCode 150 (Weeks 5-10)

| Section | Problems | Focus |
|---------|----------|-------|
| Arrays & Hashing | 9 problems | Frequency, prefix, sliding window |
| Two Pointers | 5 problems | Sorted arrays, palindromes |
| Sliding Window | 5 problems | Substring optimization |
| Stack | 4 problems | Monotonic, expression evaluation |
| Binary Search | 7 problems | Search space, rotated arrays |
| Linked List | 9 problems | Fast/slow, reversal, merge |
| Trees | 14 problems | BFS, DFS, BST, LCA, construction |
| Tries | 3 problems | Prefix, Word Search |
| Heap | 6 problems | Top K, median, scheduling |
| Backtracking | 9 problems | Subsets, permutations, combination |
| Graphs | 10 problems | BFS, DFS, topological, union-find |
| Dynamic Programming | 15 problems | 1D, 2D, knapsack, intervals |
| Greedy | 6 problems | Interval, activity selection |
| Advanced Graphs | 5 problems | Dijkstra, Prim, Bellman-Ford |
| Bit Manipulation | 5 problems | XOR tricks, bit counting |
| Math & Geometry | 6 problems | Primes, GCD, geometry |
| Intervals | 4 problems | Merge, insert, overlap |

### Phase 3: Company-Specific (Weeks 11-16)

| Company | Focus Problems |
|---------|---------------|
| Google | LC 4, 10, 42, 124, 128, 269, 297, 329, 336, 354, 489, 642, 679, 727, 815, 843, 900, 939, 1057, 1088, 1101, 1153, 1192, 1215, 1229, 1231, 1240, 1263, 1265, 1274, 1284, 1293, 1301, 1306, 1307, 1320, 1335, 1340, 1349, 1354, 1363, 1368, 1373, 1376, 1377, 1383, 1386, 1391, 1397, 1401, 1406, 1410, 1420, 1423, 1424, 1425, 1428, 1430, 1432, 1434, 1438, 1440, 1442, 1444, 1449, 1451, 1452, 1453, 1455, 1457, 1458, 1460, 1462, 1463, 1464, 1466, 1467, 1470, 1471, 1472, 1475, 1477, 1478, 1480, 1481, 1482, 1483, 1485, 1486, 1487, 1488, 1489, 1490, 1491, 1492, 1493, 1494, 1495, 1496, 1497, 1498, 1499, 1500 |
| Meta | LC 1, 3, 10, 11, 13, 15, 20, 23, 31, 33, 42, 49, 50, 56, 57, 67, 71, 76, 79, 88, 91, 98, 102, 103, 105, 106, 114, 121, 124, 125, 127, 128, 133, 139, 140, 146, 150, 155, 157, 158, 161, 173, 186, 199, 200, 206, 207, 208, 209, 210, 211, 212, 215, 221, 227, 235, 236, 238, 239, 240, 242, 249, 253, 257, 261, 265, 269, 270, 273, 274, 277, 278, 282, 283, 285, 286, 297, 301, 304, 307, 310, 311, 314, 317, 323, 325, 329, 332, 333, 334, 337, 338, 339, 340, 341, 343, 346, 347, 348, 349, 350, 354, 358, 359, 360, 361, 362, 364, 366, 367, 368, 369, 370, 371, 373, 374, 375, 377, 378, 380, 381, 382, 384, 386, 387, 388, 389, 390, 391, 392, 393, 394, 395, 396, 397, 398, 399, 400, 401, 402, 403, 404, 405, 406, 407, 408, 409, 410, 411, 412, 413, 414, 415, 416, 417, 418, 419, 420, 421, 422, 423, 424, 425, 426, 427, 428, 429, 430, 431, 432, 433, 434, 435, 436, 437, 438, 439, 440, 441, 442, 443, 444, 445, 446, 447, 448, 449, 450, 451, 452, 453, 454, 455, 456, 457, 458, 459, 460, 461, 462, 463, 464, 465, 466, 467, 468, 469, 470, 471, 472, 473, 474, 475, 476, 477, 478, 479, 480, 481, 482, 483, 484, 485, 486, 487, 488, 489, 490, 491, 492, 493, 494, 495, 496, 497, 498, 499, 500 |
| Amazon | LC 1, 2, 3, 5, 7, 11, 12, 13, 15, 17, 20, 21, 22, 23, 24, 26, 27, 28, 31, 32, 33, 34, 36, 37, 38, 39, 40, 41, 42, 45, 46, 47, 48, 49, 50, 51, 53, 54, 55, 56, 57, 62, 63, 64, 66, 67, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 171, 172, 173, 174, 175, 176, 177, 178, 179, 186, 187, 188, 189, 190, 191, 198, 199, 200, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 215, 216, 217, 218, 219, 221, 222, 224, 225, 226, 227, 228, 229, 230, 231, 232, 234, 235, 236, 237, 238, 239, 240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254, 255, 256, 257, 258, 259, 260, 261, 263, 264, 265, 266, 267, 268, 269, 270, 271, 272, 273, 274, 275, 276, 277, 278, 279, 280, 281, 282, 283, 284, 285, 286, 287, 288, 289, 290, 291, 292, 293, 294, 295, 296, 297, 298, 299, 300, 301, 302, 303, 304, 305, 306, 307, 308, 309, 310, 311, 312, 313, 314, 315, 316, 317, 318, 319, 320, 321, 322, 323, 324, 325, 326, 327, 328, 329, 330, 331, 332, 333, 334, 335, 336, 337, 338, 339, 340, 341, 342, 343, 344, 345, 346, 347, 348, 349, 350, 351, 352, 353, 354, 355, 356, 357, 358, 359, 360, 361, 362, 363, 364, 365, 366, 367, 368, 369, 370, 371, 372, 373, 374, 375, 376, 377, 378, 379, 380, 381, 382, 383, 384, 385, 386, 387, 388, 389, 390, 391, 392, 393, 394, 395, 396, 397, 398, 399, 400 |

---

## Study Plans

### 4-Week Accelerated Plan

| Week | Focus | Daily Commitment |
|------|-------|-----------------|
| 1 | Sorting + Searching + Recursion | 3-4 hrs |
| 2 | DP + Greedy + Backtracking | 3-4 hrs |
| 3 | Graphs + Strings + Divide & Conquer | 4 hrs |
| 4 | Advanced + Mock Interviews | 4-5 hrs |

### 8-Week Standard Plan

| Week | Focus | Problems |
|------|-------|----------|
| 1 | Sorting + Searching basics | 10-12 |
| 2 | Recursion + Divide & Conquer | 8-10 |
| 3 | Dynamic Programming — 1D | 10-12 |
| 4 | Dynamic Programming — 2D | 10-12 |
| 5 | Greedy + Backtracking | 8-10 |
| 6 | Graph algorithms | 10-12 |
| 7 | String algorithms | 8-10 |
| 8 | Complexity + Mock interviews | 12-15 |

### 12-Week Comprehensive Plan

| Week | Focus | Details |
|------|-------|---------|
| 1 | Sorting basics (bubble, selection, insertion) | Understand stability, in-place, comparison |
| 2 | Advanced sorting (merge, quick, heap, TimSort) | Recursive depth, pivot selection, hybrid sort |
| 3 | Searching (linear, binary, interpolation) | Binary search for answer, search space |
| 4 | Recursion + Divide & Conquer | Recurrence relations, recursion trees |
| 5 | Dynamic Programming — 1D (Climbing Stairs, House Robber, Coin Change) | State definition, memoization vs tabulation |
| 6 | Dynamic Programming — 2D (LCS, Edit Distance, LPS) | Table filling, space optimization |
| 7 | Greedy + Backtracking | Greedy choice property, constraint satisfaction |
| 8 | Graph — BFS, DFS, topological | Adjacency representation, cycle detection |
| 9 | Graph — shortest path, MST | Dijkstra, Bellman-Ford, Prim, Kruskal |
| 10 | String algorithms | KMP, Rabin-Karp, Z-algorithm, Manacher |
| 11 | Complexity analysis + Optimization | Master theorem, amortized, competitive analysis |
| 12 | Mock interviews + Company-specific | Full-length mocks, real interview simulation |

---

## Resources

### Books

| Book | Best For | Level |
|------|----------|-------|
| Introduction to Algorithms (CLRS) | Comprehensive theory, proofs | Advanced |
| Algorithm Design Manual (Skiena) | Practical problem-solving | Intermediate |
| Competitive Programming (Halim) | Contest preparation | Advanced-Expert |
| Algorithms (Sedgewick) | Java implementations | Intermediate |
| Algorithm Design (Kleinberg & Tardos) | Algorithmic thinking | Advanced |
| The Art of Computer Programming (Knuth) | Encyclopedia reference | Expert |

### Online Courses

| Course | Platform | Instructor |
|--------|----------|------------|
| MIT 6.006 Introduction to Algorithms | MIT OCW | Erik Demaine |
| MIT 6.046 Design and Analysis of Algorithms | MIT OCW | Devadas |
| Princeton Algorithms Part I & II | Coursera | Robert Sedgewick |
| Algorithms Specialization | Stanford/Coursera | Tim Roughgarden |
| Competitive Programming | Codeforces | Various |

### Websites

| Website | Use |
|---------|-----|
| LeetCode | Primary practice |
| NeetCode.io | Pattern-based learning |
| HackerRank | Topic-specific practice |
| Codeforces | Advanced contest problems |
| AtCoder | Japanese contest platform |
| USACO Guide | CP training path |
| CP-Algorithms | Algorithm reference |
| GeeksforGeeks | DS/Algo quick reference |

### YouTube Channels

| Channel | Best For |
|---------|----------|
| NeetCode | Pattern-based explanations |
| Errichto | Competitive programming |
| SecondThread | Advanced algorithm visualizations |
| Algorithms Live! | University-level algorithm lectures |
| MIT OpenCourseWare | Formal algorithm education |

---

## Final Checklist

Before your interview, ensure you can:

- [ ] Analyze time and space complexity of any algorithm instantly
- [ ] Implement binary search on both arrays and search spaces
- [ ] Solve any DP problem using memoization and tabulation
- [ ] Implement BFS, DFS, Dijkstra, and topological sort from scratch
- [ ] Write merge sort and quick sort without referencing implementation
- [ ] Solve backtracking problems (subsets, permutations, combinations)
- [ ] Implement KMP or Z-algorithm for substring matching
- [ ] Recognize when greedy works and when it fails
- [ ] Handle recursion depth limits with iterative alternatives
- [ ] Communicate your algorithm design process clearly with code
