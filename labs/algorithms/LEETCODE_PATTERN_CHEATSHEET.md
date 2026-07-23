# LeetCode Pattern Cheatsheet — Algorithms

This cheatsheet organizes LeetCode problems by algorithm pattern, with time/space complexity, company frequency, and guidance on when to use each pattern.

---

## Sorting Patterns

### Comparison-Based Sorting

When to use: General-purpose sorting, need stability or in-place.

| Algorithm | Best | Average | Worst | Space | Stable | Companies |
|-----------|------|---------|-------|-------|--------|-----------|
| Quick Sort | O(n log n) | O(n log n) | O(n^2) | O(log n) | No | Google, Meta, Amazon |
| Merge Sort | O(n log n) | O(n log n) | O(n log n) | O(n) | Yes | Microsoft, Amazon, Google |
| Heap Sort | O(n log n) | O(n log n) | O(n log n) | O(1) | No | Meta, Amazon |
| TimSort | O(n) | O(n log n) | O(n log n) | O(n) | Yes | Oracle, Microsoft |
| Insertion Sort | O(n) | O(n^2) | O(n^2) | O(1) | Yes | Apple, Microsoft |
| Bubble Sort | O(n) | O(n^2) | O(n^2) | O(1) | Yes | (educational only) |
| Selection Sort | O(n^2) | O(n^2) | O(n^2) | O(1) | No | (educational only) |

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 75 Sort Colors | O(n) | O(1) | Meta, Google, Amazon, Microsoft |
| LC 148 Sort List | O(n log n) | O(1) | Amazon, Meta, Google, Microsoft |
| LC 912 Sort an Array | O(n log n) | O(n) | Amazon, Microsoft, Google, Meta |
| LC 179 Largest Number | O(n log n) | O(n) | Meta, Amazon, Microsoft, Google |
| LC 324 Wiggle Sort II | O(n) avg | O(1) | Google, Meta, Amazon |

### Linear-Time Sorting

When to use: Integer data, bounded range, need O(n) performance.

| Algorithm | Time | Space | When to Use |
|-----------|------|-------|-------------|
| Counting Sort | O(n + k) | O(k) | Integer range k is small |
| Radix Sort | O(d(n + k)) | O(n + k) | Fixed-length integer keys |
| Bucket Sort | O(n + k) avg | O(n) | Uniformly distributed data |

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 164 Maximum Gap | O(n) | O(n) | Google, Amazon, Meta |
| LC 347 Top K Frequent Elements | O(n) using bucket | O(n) | Meta, Amazon, Google, Microsoft |
| LC 451 Sort Characters By Frequency | O(n) using bucket | O(n) | Meta, Amazon, Google, Microsoft |
| LC 561 Array Partition | O(n log n) | O(1) | Amazon, Google, Meta |
| LC 1051 Height Checker | O(n log n) | O(n) | Google, Amazon |

### Divide & Conquer Sorting

When to use: Problems where the merge step can be customized.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 315 Count of Smaller Numbers After Self | O(n log n) | O(n) | Google, Meta, Amazon |
| LC 327 Count of Range Sum | O(n log n) | O(n) | Google, Amazon |
| LC 493 Reverse Pairs | O(n log n) | O(n) | Google, Amazon, Meta |
| LC 23 Merge k Sorted Lists | O(n log k) | O(k) | Amazon, Meta, Google, Microsoft |

---

## Searching Patterns

### Binary Search — Standard

When to use: Sorted array, find element, first/last occurrence.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 34 Find First and Last Position | O(log n) | O(1) | Meta, Amazon, Google, Microsoft |
| LC 35 Search Insert Position | O(log n) | O(1) | Amazon, Google, Meta, Microsoft |
| LC 69 Sqrt(x) | O(log x) | O(1) | Meta, Amazon, Google, Microsoft |
| LC 278 First Bad Version | O(log n) | O(1) | Meta, Amazon, Google, Microsoft |
| LC 704 Binary Search | O(log n) | O(1) | Amazon, Google, Meta, Microsoft |
| LC 744 Find Smallest Letter | O(log n) | O(1) | Amazon, Google |
| LC 852 Peak Index in Mountain | O(log n) | O(1) | Google, Amazon, Meta |
| LC 1095 Find in Mountain Array | O(log n) | O(1) | Google, Amazon |

### Binary Search — Rotated Array

When to use: Array that is sorted but rotated.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 33 Search in Rotated Sorted Array | O(log n) | O(1) | Meta, Amazon, Google, Microsoft |
| LC 81 Search in Rotated Sorted Array II | O(log n) avg | O(1) | Amazon, Meta, Google |
| LC 153 Find Minimum in Rotated Sorted Array | O(log n) | O(1) | Meta, Amazon, Google, Microsoft |
| LC 154 Find Minimum in Rotated Sorted Array II | O(log n) avg | O(1) | Amazon, Meta, Google |
| LC 162 Find Peak Element | O(log n) | O(1) | Meta, Amazon, Google, Microsoft |
| LC 240 Search a 2D Matrix II | O(m + n) | O(1) | Meta, Amazon, Google, Microsoft |

### Binary Search — Answer Pattern

When to use: Minimize maximum or maximize minimum.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 4 Median of Two Sorted Arrays | O(log(min(n,m))) | O(1) | Google, Amazon, Meta, Microsoft |
| LC 378 Kth Smallest in Sorted Matrix | O(n log max) | O(1) | Meta, Amazon, Google, Microsoft |
| LC 410 Split Array Largest Sum | O(n log sum) | O(1) | Google, Amazon, Meta |
| LC 774 Minimize Max Distance | O(n log precision) | O(1) | Google |
| LC 875 Koko Eating Bananas | O(n log max) | O(1) | Meta, Amazon, Google, Microsoft |
| LC 1011 Capacity To Ship Packages | O(n log total) | O(1) | Amazon, Google, Meta |
| LC 1231 Divide Chocolate | O(n log sum) | O(1) | Google, Amazon |
| LC 1283 Find Smallest Divisor | O(n log max) | O(1) | Amazon, Google, Meta |
| LC 1292 Maximum Side Length | O(mn log min(m,n)) | O(mn) | Google |
| LC 1300 Sum of Mutated Array | O(n log max) | O(1) | Google, Meta |
| LC 1482 Minimum Days to Make Bouquets | O(n log max) | O(1) | Amazon, Google |

### Ternary Search

When to use: Unimodal function, peak finding.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 162 Find Peak Element | O(log n) | O(1) | Meta, Amazon, Google, Microsoft |
| LC 852 Peak Index in Mountain | O(log n) | O(1) | Google, Amazon |
| LC 1095 Find in Mountain Array | O(log n) | O(1) | Google, Meta |

### Interpolation Search

When to use: Uniformly distributed sorted array, large range.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| (Used as optimization in practice) | O(log log n) avg | O(1) | Google |

---

## Dynamic Programming Patterns

### Linear DP (1D)

When to use: Single dimension state, Fibonacci-like.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 70 Climbing Stairs | O(n) | O(1) | Google, Meta, Amazon, Microsoft |
| LC 198 House Robber | O(n) | O(1) | Google, Meta, Amazon, Microsoft |
| LC 213 House Robber II | O(n) | O(1) | Amazon, Meta, Google, Microsoft |
| LC 276 Paint Fence | O(n) | O(1) | Meta, Google, Amazon |
| LC 300 Longest Increasing Subsequence | O(n log n) | O(n) | Google, Meta, Amazon, Microsoft |
| LC 322 Coin Change | O(n * amount) | O(amount) | Google, Meta, Amazon, Microsoft |
| LC 338 Counting Bits | O(n) | O(n) | Meta, Amazon, Google, Microsoft |
| LC 343 Integer Break | O(n) | O(1) | Google, Amazon |
| LC 368 Largest Divisible Subset | O(n^2) | O(n) | Google |
| LC 377 Combination Sum IV | O(n * target) | O(target) | Meta, Google, Amazon |
| LC 416 Partition Equal Subset Sum | O(n * sum) | O(sum) | Google, Meta, Amazon |
| LC 474 Ones and Zeroes | O(m*n*len) | O(m*n) | Google |
| LC 518 Coin Change II | O(n * amount) | O(amount) | Google, Amazon, Meta |
| LC 650 2 Keys Keyboard | O(n^2) | O(n) | Microsoft |
| LC 746 Min Cost Climbing Stairs | O(n) | O(1) | Google, Meta, Amazon |
| LC 790 Domino and Tromino Tiling | O(n) | O(1) | Google |
| LC 873 Length of Longest Fibonacci Subsequence | O(n^2) | O(n^2) | Google |
| LC 983 Minimum Cost For Tickets | O(n) | O(n) | Amazon, Google |

### Knapsack DP

When to use: Resource allocation with capacity constraints.

| Subpattern | Characteristics | Examples |
|-----------|-----------------|----------|
| 0/1 Knapsack | Each item used at most once | LC 416, LC 474, LC 494 |
| Unbounded Knapsack | Each item used any number of times | LC 322, LC 518, LC 377 |
| Bounded Knapsack | Each item has limited copies | LC 956, LC 1049 |

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 416 Partition Equal Subset Sum | O(n*sum) | O(sum) | Google, Meta, Amazon |
| LC 474 Ones and Zeroes | O(l*m*n) | O(m*n) | Google |
| LC 494 Target Sum | O(n*sum) | O(sum) | Meta, Amazon, Google |
| LC 646 Maximum Length of Pair Chain | O(n log n) | O(1) | Google, Microsoft |
| LC 956 Tallest Billboard | O(n*sum) | O(sum) | Google |
| LC 1049 Last Stone Weight II | O(n*sum) | O(sum) | Google |
| LC 1449 Form Largest Integer With Digits | O(target) | O(target) | Google |

### LCS / LIS DP

When to use: Sequence comparison, longest common/subsequence patterns.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 5 Longest Palindromic Substring | O(n^2) | O(1) | Meta, Amazon, Google, Microsoft |
| LC 72 Edit Distance | O(m*n) | O(min(m,n)) | Google, Meta, Microsoft, Amazon |
| LC 97 Interleaving String | O(m*n) | O(m*n) | Google |
| LC 115 Distinct Subsequences | O(m*n) | O(m*n) | Google, Amazon |
| LC 1143 Longest Common Subsequence | O(m*n) | O(min(m,n)) | Google, Meta, Amazon, Microsoft |
| LC 1312 Minimum Insertions to Make Palindrome | O(n^2) | O(n) | Google |
| LC 516 Longest Palindromic Subsequence | O(n^2) | O(n^2) | Meta, Google, Amazon |
| LC 583 Delete Operation for Two Strings | O(m*n) | O(min(m,n)) | Google |
| LC 647 Palindromic Substrings | O(n^2) | O(1) | Meta, Amazon, Google, Microsoft |
| LC 712 Minimum ASCII Delete Sum | O(m*n) | O(m*n) | Google |
| LC 718 Maximum Length of Repeated Subarray | O(m*n) | O(min(m,n)) | Google, Amazon |
| LC 727 Minimum Window Subsequence | O(m*n) | O(1) | Google |
| LC 1035 Uncrossed Lines | O(m*n) | O(min(m,n)) | Amazon, Google |
| LC 1092 Shortest Common Supersequence | O(m*n) | O(m*n) | Google, Amazon |

### Interval DP

When to use: Problems where DP state is defined by range [i, j].

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 312 Burst Balloons | O(n^3) | O(n^2) | Google, Meta, Amazon |
| LC 375 Guess Number Higher or Lower II | O(n^2) | O(n^2) | Google |
| LC 486 Predict the Winner | O(n^2) | O(n^2) | Amazon, Google |
| LC 516 Longest Palindromic Subsequence | O(n^2) | O(n^2) | Meta, Google, Amazon |
| LC 546 Remove Boxes | O(n^4) | O(n^3) | Google |
| LC 664 Strange Printer | O(n^3) | O(n^2) | Google |
| LC 877 Stone Game | O(n^2) | O(n^2) | Google, Amazon, Meta |
| LC 1000 Minimum Cost to Merge Stones | O(n^3) | O(n^2) | Google |
| LC 1130 Minimum Cost Tree From Leaf Values | O(n^3) | O(n^2) | Google, Amazon |
| LC 1140 Stone Game II | O(n^2) | O(n^2) | Google |
| LC 1246 Palindrome Removal | O(n^3) | O(n^2) | Google |
| LC 1478 Allocate Mailboxes | O(n^3) | O(n^2) | Google |
| LC 1547 Minimum Cost to Cut a Stick | O(n^3) | O(n^2) | Google, Amazon |
| LC 1691 Maximum Height by Stacking Cuboids | O(n^2) | O(n) | Amazon, Google |

### Tree DP

When to use: Problems on trees where state transitions follow parent-child relationships.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 95 Unique Binary Search Trees II | O(4^n) | O(4^n) | Google |
| LC 96 Unique Binary Search Trees | O(n^2) | O(n) | Meta, Amazon, Google, Microsoft |
| LC 124 Binary Tree Maximum Path Sum | O(n) | O(h) | Amazon, Meta, Google, Microsoft, Apple |
| LC 337 House Robber III | O(n) | O(h) | Meta, Amazon, Google |
| LC 543 Diameter of Binary Tree | O(n) | O(h) | Amazon, Meta, Google, Microsoft |
| LC 968 Binary Tree Cameras | O(n) | O(h) | Google, Amazon |
| LC 979 Distribute Coins in Binary Tree | O(n) | O(h) | Amazon, Meta, Google |
| LC 1372 Longest ZigZag Path | O(n) | O(h) | Amazon, Google |
| LC 1373 Maximum Sum BST | O(n) | O(h) | Google |
| LC 1569 Number of Ways to Reorder Array | O(n log n) | O(n) | Google |

### Digit DP

When to use: Problems involving digit constraints, counting numbers in a range.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 233 Number of Digit One | O(log n) | O(log n) | Google |
| LC 357 Count Numbers with Unique Digits | O(n) | O(1) | Google |
| LC 600 Non-negative Integers without Consecutive Ones | O(log n) | O(log n) | Google |
| LC 788 Rotated Digits | O(n) | O(1) | Google |
| LC 902 Numbers At Most N Given Digit Set | O(log n * |D|) | O(log n) | Google |
| LC 1012 Numbers With Repeated Digits | O(log n) | O(log n) | Google |
| LC 1067 Digit Count in Range | O(log n * k) | O(log n) | Google |
| LC 1088 Confusing Number II | O(5^k) | O(k) | Google |
| LC 1397 Find All Good Strings | O(n*4*m) | O(n*m) | Google |

### State Compression DP

When to use: Subset problems, small N (N <= 20).

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 191 Number of 1 Bits | O(1) | O(1) | Meta, Amazon, Google, Microsoft |
| LC 338 Counting Bits | O(n) | O(n) | Meta, Amazon, Google, Microsoft |
| LC 464 Can I Win | O(2^n) | O(2^n) | Google |
| LC 526 Beautiful Arrangement | O(2^n) | O(2^n) | Google |
| LC 691 Stickers to Spell Word | O(2^n * |stickers|) | O(2^n) | Google |
| LC 698 Partition to K Equal Sum Subsets | O(k * 2^n) | O(2^n) | Amazon, Google, Meta |
| LC 847 Shortest Path Visiting All Nodes | O(n^2 * 2^n) | O(2^n * n) | Google, Amazon |
| LC 943 Find the Shortest Superstring | O(n^2 * 2^n) | O(n * 2^n) | Google |
| LC 996 Number of Squareful Arrays | O(n^2 * 2^n) | O(n * 2^n) | Google |
| LC 1125 Smallest Sufficient Team | O(2^p * n) | O(2^p) | Google |
| LC 1434 Number of Ways to Wear Different Hats | O(m * 2^n) | O(m * 2^n) | Google |
| LC 1655 Distribute Repeating Integers | O(m * 2^n) | O(m * 2^n) | Google |
| LC 1799 Maximize Score After N Operations | O(n^2 * 2^n) | O(2^n) | Google |
| LC 1879 Minimum XOR Sum of Two Arrays | O(n * 2^n) | O(2^n) | Google |

---

## Graph Algorithm Patterns

### Shortest Path

When to use: Find minimum distance between nodes.

| Algorithm | Time | Space | When to Use |
|-----------|------|-------|-------------|
| Dijkstra | O(E log V) | O(V+E) | Non-negative weights |
| Bellman-Ford | O(VE) | O(V) | Negative weights possible |
| Floyd-Warshall | O(V^3) | O(V^2) | All-pairs shortest path |
| BFS | O(V+E) | O(V) | Unweighted graph |
| A* | O(E) | O(V) | Heuristic-guided search |
| SPFA | O(VE) avg | O(V) | Negative weights, dequeue optimizations |

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 743 Network Delay Time | O(E log V) | O(V+E) | Google, Amazon, Meta, Microsoft |
| LC 787 Cheapest Flights Within K Stops | O(K*E) | O(V) | Google, Amazon, Meta |
| LC 847 Shortest Path Visiting All Nodes | O(n^2 * 2^n) | O(2^n * n) | Google, Amazon |
| LC 882 Reachable Nodes In Subdivided Graph | O(E log V) | O(V+E) | Google |
| LC 1334 Find the City With Smallest Number of Neighbors | O(V^3) | O(V^2) | Amazon, Google |
| LC 1368 Minimum Cost to Make at Least One Valid Path | O(mn log(mn)) | O(mn) | Google |
| LC 1514 Path with Maximum Probability | O(E log V) | O(V+E) | Google, Amazon |
| LC 1631 Path With Minimum Effort | O(mn log(mn)) | O(mn) | Google, Amazon, Meta |
| LC 1786 Number of Restricted Paths | O(E log V) | O(V+E) | Google |
| LC 1976 Number of Ways to Arrive at Destination | O(E log V) | O(V+E) | Google |

### Minimum Spanning Tree

When to use: Connect all nodes with minimum total edge weight.

| Algorithm | Time | Space | When to Use |
|-----------|------|-------|-------------|
| Kruskal | O(E log E) | O(V+E) | Sparse graphs |
| Prim | O(E log V) | O(V+E) | Dense graphs |
| Boruvka | O(E log V) | O(V+E) | Parallel processing |

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 1135 Connecting Cities With Minimum Cost | O(E log E) | O(V+E) | Amazon, Google |
| LC 1168 Optimize Water Distribution | O(E log E) | O(V+E) | Amazon, Google |
| LC 1489 Find Critical and Pseudo-Critical Edges | O(E^2) | O(V+E) | Google |
| LC 1584 Min Cost to Connect All Points | O(V^2 log V) | O(V^2) | Meta, Amazon, Google |
| LC 1631 Path With Minimum Effort | O(mn log(mn)) | O(mn) | Google, Amazon |

### Max Flow / Min Cut

When to use: Network flow, bipartite matching, project selection.

| Algorithm | Time | Space | When to Use |
|-----------|------|-------|-------------|
| Ford-Fulkerson | O(E * max_flow) | O(V+E) | Small integer capacities |
| Dinic | O(E * V^2) | O(V+E) | General flow |
| Edmonds-Karp | O(VE^2) | O(V+E) | BFS-based augmentation |
| Push-Relabel | O(V^2 * sqrt(E)) | O(V+E) | Fastest in practice |

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 1345 Jump Game IV | O(n) | O(n) | Google |
| LC 1559 Detect Cycles in 2D Grid | O(mn) | O(mn) | Google |
| LC 1761 Minimum Degree of a Connected Trio | O(n^3) | O(n^2) | Google |
| LC 1905 Count Sub Islands | O(mn) | O(mn) | Amazon, Google |
| LC 2092 Find All People With Secret | O((V+E) log V) | O(V+E) | Google |

### Bipartite Graph

When to use: Two-coloring, matching problems.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 785 Is Graph Bipartite | O(V+E) | O(V) | Meta, Amazon, Google, Microsoft |
| LC 886 Possible Bipartition | O(V+E) | O(V+E) | Meta, Amazon, Google |
| LC 1042 Flower Planting With No Adjacent | O(V+E) | O(V+E) | Google |

---

## String Algorithm Patterns

### KMP (Knuth-Morris-Pratt)

When to use: Find pattern occurrences in text, O(n+m) time.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 28 Find the Index of First Occurrence | O(n+m) | O(m) | Meta, Amazon, Google, Microsoft |
| LC 214 Shortest Palindrome | O(n^2) or O(n) | O(n) | Google |
| LC 459 Repeated Substring Pattern | O(n) | O(n) | Meta, Amazon, Google |
| LC 686 Repeated String Match | O(n+m) | O(m) | Google, Amazon |
| LC 1392 Longest Happy Prefix | O(n) | O(n) | Google |

### Rabin-Karp (Rolling Hash)

When to use: Multiple pattern matching, plagiarism detection.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 28 Find the Index of First Occurrence | O(n+m) avg | O(1) | Meta, Amazon, Google, Microsoft |
| LC 187 Repeated DNA Sequences | O(n) | O(n) | Meta, Amazon, Google, Microsoft |
| LC 1044 Longest Duplicate Substring | O(n log n) | O(n) | Google, Amazon |
| LC 718 Maximum Length of Repeated Subarray | O(m*n) | O(n) | Google |
| LC 1062 Longest Repeating Substring | O(n log n) | O(n) | Google, Amazon |
| LC 1316 Distinct Echo Substrings | O(n^2) | O(n) | Google |
| LC 1698 Number of Distinct Substrings in a String | O(n^2) | O(n^2) | Google |
| LC 1923 Longest Common Subpath | O(n log n) | O(n) | Google |

### Z-Algorithm

When to use: Pattern matching, prefix-function problems.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 28 Find the Index of First Occurrence | O(n+m) | O(n+m) | Meta, Amazon, Google, Microsoft |
| LC 214 Shortest Palindrome | O(n) | O(n) | Google |
| LC 459 Repeated Substring Pattern | O(n) | O(n) | Meta, Amazon, Google |
| LC 796 Rotate String | O(n) | O(n) | Amazon, Google, Meta |
| LC 1392 Longest Happy Prefix | O(n) | O(n) | Google |

### Manacher's Algorithm

When to use: Find all palindromic substrings in O(n) time.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 5 Longest Palindromic Substring | O(n) | O(n) | Meta, Amazon, Google, Microsoft |
| LC 214 Shortest Palindrome | O(n^2) or O(n) | O(n) | Google |
| LC 647 Palindromic Substrings | O(n) | O(n) | Meta, Amazon, Google, Microsoft |
| LC 1960 Maximum Product of Lengths of Two Palindromic Substrings | O(n) | O(n) | Google |
| LC 2472 Maximum Number of Non-overlapping Palindrome Substrings | O(n) | O(n) | Google |

---

## Bit Manipulation Patterns

### XOR Tricks

When to use: Find unique element, detect parity, swap without temp.

| Trick | Description | Example |
|-------|-------------|---------|
| a ^ a = 0 | Cancels same values | LC 136 Single Number |
| a ^ 0 = a | Identity | LC 136, LC 260 |
| a ^ b ^ a = b | Find missing number | LC 268 Missing Number |
| a ^= b; b ^= a; a ^= b | Swap without temp | In-place operations |
| prefix xor array | Query range xor | Subarray XOR queries |

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 136 Single Number | O(n) | O(1) | Amazon, Meta, Google, Microsoft |
| LC 137 Single Number II | O(n) | O(1) | Google, Amazon, Meta |
| LC 260 Single Number III | O(n) | O(1) | Amazon, Google, Meta |
| LC 268 Missing Number | O(n) | O(1) | Meta, Amazon, Google, Microsoft |
| LC 389 Find the Difference | O(n) | O(1) | Amazon, Google |
| LC 421 Maximum XOR of Two Numbers | O(n) | O(n) | Google, Meta, Microsoft |
| LC 477 Total Hamming Distance | O(n) | O(1) | Google, Amazon |
| LC 1707 Maximum XOR With an Element From Array | O(n log M) | O(n log M) | Google |
| LC 1734 Decode XORed Permutation | O(n) | O(1) | Google |
| LC 1829 Maximum XOR for Each Query | O(n) | O(1) | Google |

### Bit DP

When to use: State compression with bit masks.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 338 Counting Bits | O(n) | O(n) | Meta, Amazon, Google, Microsoft |
| LC 191 Number of 1 Bits | O(1) | O(1) | Meta, Amazon, Google, Microsoft |
| LC 342 Power of Four | O(1) | O(1) | Google, Amazon |
| LC 371 Sum of Two Integers | O(1) | O(1) | Meta, Amazon, Google, Microsoft |
| LC 461 Hamming Distance | O(1) | O(1) | Meta, Amazon, Google, Microsoft |
| LC 693 Binary Number with Alternating Bits | O(1) | O(1) | Amazon, Google |
| LC 762 Prime Number of Set Bits | O(n) | O(1) | Google |
| LC 898 Bitwise ORs of Subarrays | O(n log max) | O(log max) | Google |
| LC 1178 Number of Valid Words for Each Puzzle | O(n * 2^p) | O(n) | Google |
| LC 1239 Maximum Length of Concatenated String | O(2^n) | O(2^n) | Amazon, Google |
| LC 1452 People Whose List of Favorite Companies Is Not a Subset | O(n^2 * k) | O(n*k) | Google |
| LC 1521 Find a Value of a Mysterious Function Closest to Target | O(n log max) | O(log max) | Google |

### Subset Enumeration

When to use: Generate all subsets, sum over subsets.

| Technique | Time | Space | When to Use |
|-----------|------|-------|-------------|
| For mask = 0 to 2^n - 1 | O(n * 2^n) | O(1) | Small n |
| Submask enumeration | O(3^n) | O(1) | Sum over subsets |
| Gray code | O(2^n) | O(n) | Orderly subset generation |

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 78 Subsets | O(n * 2^n) | O(n * 2^n) | Meta, Amazon, Google, Microsoft |
| LC 90 Subsets II | O(n * 2^n) | O(n * 2^n) | Amazon, Meta, Google |
| LC 320 Generalized Abbreviation | O(n * 2^n) | O(n * 2^n) | Google |
| LC 784 Letter Case Permutation | O(n * 2^n) | O(n * 2^n) | Amazon, Meta, Google |
| LC 860 Lemonade Change | O(n) | O(1) | Amazon, Google |
| LC 1079 Letter Tile Possibilities | O(n!) | O(n) | Amazon, Google |
| LC 1286 Iterator for Combination | O(combinations) | O(1) | Google |

---

## Complexity Reference

| Algorithm Paradigm | Typical Time | Typical Space | Recognition Clues |
|--------------------|-------------|---------------|-------------------|
| Brute Force | O(n^2) - O(n!) | O(1) | Small constraints, no clear pattern |
| Divide & Conquer | O(n log n) | O(log n) | Split input, recursive combine |
| Greedy | O(n log n) | O(1) | Local optimum = global, monotonic |
| Dynamic Programming | O(n^2) - O(n^3) | O(n) - O(n^2) | Overlapping subproblems, optimal substructure |
| Backtracking | O(2^n) - O(n!) | O(n) | Explore all solutions, pruning |
| Branch & Bound | O(2^n) worst | O(n) | Optimization with pruning |
| Graph Traversal | O(V+E) | O(V) | Nodes and edges |
| Shortest Path | O((V+E) log V) | O(V+E) | Minimize distance |
| String Matching | O(n+m) | O(m) | Pattern in text |
| Bit Manipulation | O(n) - O(2^n) | O(1) - O(2^n) | Small constraints, bit operations |

### Company Frequency Key
- **Very High**: Asked in every interview
- **High**: Asked in majority of interviews
- **Medium**: Commonly asked
- **Low**: Occasionally asked

### Decision Tree for Choosing an Algorithm

```
1. Is the input sorted?
   → Yes: Binary search, two-pointer
   → No: Sort first, or use HashMap

2. Is it a shortest path problem?
   → Yes: BFS (unweighted), Dijkstra (weighted non-negative), Bellman-Ford (negative)
   → No: Consider other patterns

3. Does the problem ask for minimum/maximum?
   → Yes: Greedy (local optimal), DP (optimal substructure)

4. Can you brute force with 2^n or n! for small n?
   → Yes: Backtracking, subset enumeration, state DP

5. Is it a string pattern matching?
   → Yes: KMP, Rabin-Karp, Z-algorithm, Manacher (palindromes)

6. Are there overlapping subproblems?
   → Yes: DP — memoize or tabulate
   → No: Greedy or Divide & Conquer may apply
```
