# LeetCode Pattern Cheatsheet — Data Structures

This cheatsheet organizes LeetCode problems by data structure pattern, with time/space complexity and company frequency indicators.

---

## Array Patterns

### Two-Pointer

When to use: Sorted array, need pairs or triplets, in-place modification.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 1 Two Sum | O(n) | O(n) | Google, Meta, Amazon, Microsoft, Apple |
| LC 11 Container With Most Water | O(n) | O(1) | Meta, Google, Amazon, Microsoft |
| LC 15 3Sum | O(n^2) | O(1) | Meta, Amazon, Google, Microsoft, Apple |
| LC 16 3Sum Closest | O(n^2) | O(1) | Meta, Amazon, Google |
| LC 26 Remove Duplicates from Sorted Array | O(n) | O(1) | Microsoft, Amazon, Meta |
| LC 27 Remove Element | O(n) | O(1) | Microsoft, Apple |
| LC 42 Trapping Rain Water | O(n) | O(1) | Amazon, Meta, Google, Microsoft |
| LC 75 Sort Colors | O(n) | O(1) | Meta, Google, Microsoft, Amazon |
| LC 88 Merge Sorted Array | O(n) | O(1) | Amazon, Microsoft, Meta, Apple |
| LC 125 Valid Palindrome | O(n) | O(1) | Meta, Amazon, Google, Microsoft |
| LC 167 Two Sum II | O(n) | O(1) | Google, Amazon, Meta |
| LC 283 Move Zeroes | O(n) | O(1) | Meta, Amazon, Apple, Google |
| LC 344 Reverse String | O(n) | O(1) | Google, Amazon, Microsoft |
| LC 581 Shortest Unsorted Continuous Subarray | O(n) | O(1) | Microsoft, Google |

### Sliding Window

When to use: Subarray/substring problems with contiguous elements.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 3 Longest Substring Without Repeating Characters | O(n) | O(k) | Google, Meta, Amazon, Microsoft, Apple |
| LC 76 Minimum Window Substring | O(n) | O(k) | Meta, Google, Amazon, Microsoft |
| LC 209 Minimum Size Subarray Sum | O(n) | O(1) | Microsoft, Amazon, Google |
| LC 239 Sliding Window Maximum | O(n) | O(k) | Google, Amazon, Meta, Microsoft |
| LC 424 Longest Repeating Character Replacement | O(n) | O(1) | Meta, Google, Amazon |
| LC 438 Find All Anagrams in a String | O(n) | O(1) | Meta, Amazon, Google, Microsoft |
| LC 567 Permutation in String | O(n) | O(1) | Meta, Microsoft, Google, Amazon |
| LC 713 Subarray Product Less Than K | O(n) | O(1) | Google, Meta, Amazon |
| LC 904 Fruit Into Baskets | O(n) | O(1) | Amazon, Google |
| LC 1004 Max Consecutive Ones III | O(n) | O(1) | Amazon, Google, Microsoft |

### Prefix Sum

When to use: Subarray sum queries, range sum, cumulative frequency.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 53 Maximum Subarray | O(n) | O(1) | Amazon, Meta, Google, Microsoft, Apple |
| LC 238 Product of Array Except Self | O(n) | O(1) | Meta, Amazon, Google, Microsoft, Apple |
| LC 303 Range Sum Query | O(n) | O(n) | Meta, Google |
| LC 304 Range Sum Query 2D | O(n*m) | O(n*m) | Google, Amazon, Meta |
| LC 325 Maximum Size Subarray Sum Equals k | O(n) | O(n) | Meta, Google, Amazon |
| LC 523 Continuous Subarray Sum | O(n) | O(n) | Meta, Google, Microsoft |
| LC 560 Subarray Sum Equals K | O(n) | O(n) | Meta, Amazon, Google, Microsoft |
| LC 724 Find Pivot Index | O(n) | O(1) | Meta, Amazon, Google |
| LC 930 Binary Subarrays With Sum | O(n) | O(n) | Google, Meta |
| LC 974 Subarray Sums Divisible by K | O(n) | O(n) | Meta, Amazon |

### Cyclic Sort

When to use: Array of numbers from 1 to n with some missing/duplicate.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 41 First Missing Positive | O(n) | O(1) | Google, Amazon, Meta, Microsoft |
| LC 268 Missing Number | O(n) | O(1) | Meta, Amazon, Google, Microsoft |
| LC 287 Find the Duplicate Number | O(n) | O(1) | Amazon, Meta, Google, Microsoft |
| LC 442 Find All Duplicates | O(n) | O(1) | Google, Amazon, Meta |
| LC 448 Find All Numbers Disappeared | O(n) | O(1) | Amazon, Google, Meta |
| LC 645 Set Mismatch | O(n) | O(1) | Amazon, Microsoft |

---

## Linked List Patterns

### Fast & Slow Pointer

When to use: Cycle detection, middle element, palindrome check.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 141 Linked List Cycle | O(n) | O(1) | Amazon, Microsoft, Google, Meta, Apple |
| LC 142 Linked List Cycle II | O(n) | O(1) | Google, Amazon, Microsoft, Meta |
| LC 143 Reorder List | O(n) | O(1) | Amazon, Google, Meta, Microsoft |
| LC 234 Palindrome Linked List | O(n) | O(1) | Meta, Amazon, Google, Microsoft |
| LC 876 Middle of the Linked List | O(n) | O(1) | Amazon, Google, Microsoft |

### Reverse Linked List

When to use: Reverse all or part of a list.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 24 Swap Nodes in Pairs | O(n) | O(1) | Microsoft, Amazon, Google |
| LC 25 Reverse Nodes in k-Group | O(n) | O(1) | Microsoft, Amazon, Google, Meta |
| LC 92 Reverse Linked List II | O(n) | O(1) | Amazon, Microsoft, Meta |
| LC 206 Reverse Linked List | O(n) | O(1) | Microsoft, Amazon, Apple, Google |

### Merge Linked Lists

When to use: Combining sorted lists or partitioning.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 2 Add Two Numbers | O(n) | O(1) | Amazon, Meta, Google, Microsoft, Apple |
| LC 21 Merge Two Sorted Lists | O(n) | O(1) | Google, Amazon, Microsoft, Meta, Apple |
| LC 23 Merge k Sorted Lists | O(n log k) | O(k) | Amazon, Meta, Google, Microsoft |
| LC 86 Partition List | O(n) | O(1) | Google, Amazon, Meta |
| LC 148 Sort List | O(n log n) | O(1) | Amazon, Meta, Google, Microsoft |
| LC 328 Odd Even Linked List | O(n) | O(1) | Meta, Amazon, Microsoft |

### Cycle Detection & Removal

When to use: Detecting cycles and finding entry point.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 141 Linked List Cycle | O(n) | O(1) | Amazon, Microsoft, Google |
| LC 142 Linked List Cycle II | O(n) | O(1) | Google, Amazon, Microsoft |
| LC 160 Intersection of Two Linked Lists | O(n) | O(1) | Amazon, Meta, Google, Microsoft |

---

## Tree Patterns

### BFS (Level Order)

When to use: Level-by-level processing, shortest path in unweighted tree.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 102 Binary Tree Level Order Traversal | O(n) | O(n) | Amazon, Meta, Google, Microsoft, Apple |
| LC 103 Binary Tree Zigzag Level Order | O(n) | O(n) | Amazon, Meta, Microsoft, Google |
| LC 104 Maximum Depth of Binary Tree | O(n) | O(h) | Amazon, Meta, Google |
| LC 107 Binary Tree Level Order II | O(n) | O(n) | Amazon, Microsoft |
| LC 111 Minimum Depth of Binary Tree | O(n) | O(h) | Amazon, Google, Microsoft |
| LC 199 Binary Tree Right Side View | O(n) | O(n) | Amazon, Meta, Microsoft, Apple |
| LC 314 Binary Tree Vertical Order Traversal | O(n) | O(n) | Google, Meta, Amazon |
| LC 429 N-ary Tree Level Order | O(n) | O(n) | Amazon, Google |
| LC 513 Find Bottom Left Tree Value | O(n) | O(n) | Amazon, Meta |
| LC 515 Find Largest Value in Each Tree Row | O(n) | O(n) | Microsoft, Google |
| LC 637 Average of Levels in Binary Tree | O(n) | O(n) | Amazon, Meta, Google |
| LC 662 Maximum Width of Binary Tree | O(n) | O(n) | Google, Amazon, Meta |

### DFS (Preorder, Inorder, Postorder)

When to use: Path problems, property validation, tree construction.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 94 Binary Tree Inorder Traversal | O(n) | O(h) | Amazon, Google, Microsoft, Meta |
| LC 98 Validate Binary Search Tree | O(n) | O(h) | Amazon, Meta, Google, Microsoft, Apple |
| LC 100 Same Tree | O(n) | O(h) | Amazon, Google, Meta, Apple |
| LC 101 Symmetric Tree | O(n) | O(h) | Amazon, Meta, Google, Microsoft |
| LC 105 Construct from Preorder & Inorder | O(n) | O(n) | Amazon, Meta, Google, Microsoft |
| LC 106 Construct from Inorder & Postorder | O(n) | O(n) | Amazon, Meta, Google, Microsoft |
| LC 108 Convert Sorted Array to BST | O(n) | O(log n) | Amazon, Meta, Google, Microsoft |
| LC 110 Balanced Binary Tree | O(n) | O(h) | Amazon, Meta, Google |
| LC 112 Path Sum | O(n) | O(h) | Amazon, Meta, Google, Microsoft |
| LC 113 Path Sum II | O(n) | O(h) | Amazon, Meta, Google |
| LC 114 Flatten Binary Tree to Linked List | O(n) | O(h) | Amazon, Google, Microsoft, Meta |
| LC 124 Binary Tree Maximum Path Sum | O(n) | O(h) | Amazon, Meta, Google, Microsoft, Apple |
| LC 129 Sum Root to Leaf Numbers | O(n) | O(h) | Amazon, Meta, Google, Microsoft |
| LC 144 Binary Tree Preorder Traversal | O(n) | O(h) | Amazon, Microsoft, Google |
| LC 145 Binary Tree Postorder Traversal | O(n) | O(h) | Amazon, Microsoft |
| LC 156 Binary Tree Upside Down | O(n) | O(h) | Meta |
| LC 226 Invert Binary Tree | O(n) | O(h) | Amazon, Meta, Google, Microsoft, Apple |
| LC 230 Kth Smallest Element in BST | O(n) | O(h) | Amazon, Meta, Google, Microsoft, Oracle |
| LC 235 Lowest Common Ancestor of BST | O(h) | O(1) | Amazon, Meta, Google, Microsoft |
| LC 236 Lowest Common Ancestor of Binary Tree | O(n) | O(h) | Amazon, Meta, Google, Microsoft, Apple |
| LC 250 Count Univalue Subtrees | O(n) | O(h) | Google, Amazon |
| LC 257 Binary Tree Paths | O(n) | O(h) | Amazon, Google, Meta |
| LC 270 Closest Binary Search Tree Value | O(h) | O(1) | Meta, Google, Amazon |
| LC 272 Closest Binary Search Tree Value II | O(n) | O(n) | Google, Meta |
| LC 285 Inorder Successor in BST | O(h) | O(1) | Amazon, Meta, Google, Microsoft |
| LC 298 Binary Tree Longest Consecutive Sequence | O(n) | O(h) | Google, Meta |

### Morris Traversal (O(1) Space)

When to use: Tree traversal with O(1) space constraint.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 94 Binary Tree Inorder Traversal (Morris) | O(n) | O(1) | Google, Microsoft |
| LC 99 Recover Binary Search Tree | O(n) | O(1) | Google, Amazon, Microsoft |
| LC 144 Binary Tree Preorder Traversal (Morris) | O(n) | O(1) | Google |

### LCA Patterns

When to use: Find common ancestor, distance between nodes.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 235 LCA of BST | O(h) | O(1) | Amazon, Meta, Google, Microsoft |
| LC 236 LCA of Binary Tree | O(n) | O(h) | Amazon, Meta, Google, Microsoft, Apple |
| LC 1644 LCA of Binary Tree II | O(n) | O(h) | Meta, Amazon |
| LC 1650 LCA of Binary Tree III | O(h) | O(1) | Meta, Amazon |
| LC 1676 LCA of Binary Tree IV | O(n) | O(h) | Amazon, Google |

### Serialization

When to use: Tree ↔ string conversion for transmission or storage.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 297 Serialize and Deserialize Binary Tree | O(n) | O(n) | Amazon, Meta, Google, Microsoft, Apple |
| LC 428 Serialize and Deserialize N-ary Tree | O(n) | O(n) | Amazon, Microsoft, Google |
| LC 449 Serialize and Deserialize BST | O(n) | O(n) | Google, Amazon, Meta |
| LC 431 Encode N-ary Tree to Binary Tree | O(n) | O(n) | Amazon, Microsoft |

---

## Graph Patterns

### BFS on Graphs

When to use: Shortest path in unweighted graph, level-order traversal.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 127 Word Ladder | O(n*m^2) | O(n) | Amazon, Meta, Google, Microsoft |
| LC 133 Clone Graph | O(V+E) | O(V) | Amazon, Meta, Google, Microsoft |
| LC 200 Number of Islands | O(M*N) | O(min(M,N)) | Amazon, Meta, Google, Microsoft, Apple |
| LC 286 Walls and Gates | O(M*N) | O(M*N) | Meta, Amazon, Google, Microsoft |
| LC 317 Shortest Distance from All Buildings | O(M*N*B) | O(M*N) | Google, Amazon, Meta |
| LC 433 Minimum Genetic Mutation | O(n) | O(n) | Meta, Amazon |
| LC 542 01 Matrix | O(M*N) | O(M*N) | Amazon, Meta, Google, Microsoft |
| LC 743 Network Delay Time | O(E log V) | O(V+E) | Google, Amazon, Meta, Microsoft |
| LC 752 Open the Lock | O(10000) | O(10000) | Amazon, Meta, Google |
| LC 773 Sliding Puzzle | O(6!) | O(6!) | Google, Amazon, Meta |
| LC 909 Snakes and Ladders | O(n^2) | O(n^2) | Amazon, Google, Meta |
| LC 994 Rotting Oranges | O(M*N) | O(M*N) | Amazon, Meta, Google, Microsoft |
| LC 1293 Shortest Path with Obstacles | O(M*N*K) | O(M*N*K) | Google, Amazon |

### DFS on Graphs

When to use: Connected components, path existence, cycle detection.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 130 Surrounded Regions | O(M*N) | O(M*N) | Amazon, Google, Meta, Microsoft |
| LC 200 Number of Islands | O(M*N) | O(M*N) | Amazon, Meta, Google, Microsoft, Apple |
| LC 207 Course Schedule | O(V+E) | O(V+E) | Amazon, Meta, Google, Microsoft, Apple |
| LC 210 Course Schedule II | O(V+E) | O(V+E) | Amazon, Meta, Google, Microsoft |
| LC 261 Graph Valid Tree | O(V+E) | O(V+E) | Meta, Amazon, Google, Microsoft |
| LC 323 Number of Connected Components | O(V+E) | O(V+E) | Amazon, Meta, Google, Microsoft |
| LC 329 Longest Increasing Path | O(M*N) | O(M*N) | Amazon, Google, Meta, Microsoft |
| LC 332 Reconstruct Itinerary | O(E log E) | O(E) | Amazon, Meta, Google, Microsoft |
| LC 394 Decode String | O(n) | O(n) | Google, Amazon, Meta, Microsoft |
| LC 417 Pacific Atlantic Water Flow | O(M*N) | O(M*N) | Amazon, Google, Meta, Microsoft |
| LC 490 The Maze | O(M*N) | O(M*N) | Meta, Amazon, Google |
| LC 505 The Maze II | O(M*N log) | O(M*N) | Google, Amazon |
| LC 547 Number of Provinces | O(n^2) | O(n) | Amazon, Meta, Google, Microsoft |
| LC 694 Number of Distinct Islands | O(M*N) | O(M*N) | Amazon, Google, Meta, Microsoft |
| LC 695 Max Area of Island | O(M*N) | O(M*N) | Amazon, Meta, Google, Microsoft |
| LC 733 Flood Fill | O(M*N) | O(M*N) | Amazon, Meta, Google, Microsoft |

### Topological Sort

When to use: Dependency resolution, task scheduling.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 207 Course Schedule | O(V+E) | O(V+E) | Amazon, Meta, Google, Microsoft, Apple |
| LC 210 Course Schedule II | O(V+E) | O(V+E) | Amazon, Meta, Google, Microsoft |
| LC 269 Alien Dictionary | O(C) | O(1) | Google, Amazon, Meta, Microsoft |
| LC 310 Minimum Height Trees | O(V) | O(V) | Google, Meta, Amazon |
| LC 802 Find Eventual Safe States | O(V+E) | O(V+E) | Google, Amazon |
| LC 1203 Sort Items by Groups | O(V+E+G) | O(V+E) | Google, Amazon, Meta |
| LC 1857 Largest Color Value in a Directed Graph | O(V+E) | O(V+E) | Amazon, Google |

### Union-Find

When to use: Dynamic connectivity, Kruskal's MST, number of components.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 200 Number of Islands | O(M*N*alpha) | O(M*N) | Amazon, Meta, Google, Microsoft |
| LC 261 Graph Valid Tree | O(V*alpha) | O(V) | Meta, Amazon, Google, Microsoft |
| LC 323 Number of Connected Components | O(V*alpha) | O(V) | Amazon, Meta, Google, Microsoft |
| LC 547 Number of Provinces | O(n^2*alpha) | O(n) | Amazon, Meta, Google, Microsoft |
| LC 684 Redundant Connection | O(V*alpha) | O(V) | Amazon, Meta, Google, Microsoft |
| LC 685 Redundant Connection II | O(V*alpha) | O(V) | Google, Amazon, Meta |
| LC 721 Accounts Merge | O(A log A) | O(A) | Amazon, Meta, Google, Microsoft |
| LC 737 Sentence Similarity II | O(N*alpha) | O(N) | Amazon, Google |
| LC 765 Couples Holding Hands | O(N*alpha) | O(N) | Google, Amazon |
| LC 886 Possible Bipartition | O(V+E*alpha) | O(V+E) | Meta, Amazon, Google |
| LC 924 Minimize Malware Spread | O(V^2*alpha) | O(V) | Google, Amazon |
| LC 947 Most Stones Removed | O(N*alpha) | O(N) | Amazon, Google |
| LC 952 Largest Component Size by Common Factor | O(N*sqrt(M)*alpha) | O(N) | Google |
| LC 959 Regions Cut by Slashes | O(N^2*alpha) | O(N^2) | Google, Amazon |
| LC 990 Satisfiability of Equations | O(N*alpha) | O(1) | Amazon, Google, Meta |
| LC 1061 Lexicographically Smallest String | O(N*alpha) | O(1) | Amazon, Google |
| LC 1135 Connecting Cities With Minimum Cost | O(E*alpha) | O(V) | Amazon, Google |
| LC 1319 Number of Operations to Make Network Connected | O(V+E*alpha) | O(V) | Amazon, Google |
| LC 1579 Remove Max Number of Edges | O(V+E*alpha) | O(V) | Google, Amazon |
| LC 1631 Path With Minimum Effort | O(M*N log(M*N)) | O(M*N) | Google, Amazon, Meta |

---

## Hash Map Patterns

### Frequency Counting

When to use: Count occurrences, find majority, detect duplicates.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 1 Two Sum | O(n) | O(n) | Google, Meta, Amazon, Microsoft, Apple |
| LC 49 Group Anagrams | O(n*k) | O(n*k) | Meta, Amazon, Google, Microsoft, Apple |
| LC 136 Single Number | O(n) | O(1) | Amazon, Meta, Google, Microsoft |
| LC 169 Majority Element | O(n) | O(1) | Meta, Amazon, Google, Microsoft |
| LC 217 Contains Duplicate | O(n) | O(n) | Amazon, Google, Apple, Microsoft |
| LC 242 Valid Anagram | O(n) | O(1) | Meta, Amazon, Google, Microsoft, Apple |
| LC 347 Top K Frequent Elements | O(n) | O(n) | Meta, Amazon, Google, Microsoft |
| LC 383 Ransom Note | O(n) | O(1) | Amazon, Google, Meta |
| LC 387 First Unique Character | O(n) | O(1) | Meta, Amazon, Google, Microsoft |
| LC 451 Sort Characters By Frequency | O(n) | O(n) | Meta, Amazon, Google, Microsoft |
| LC 692 Top K Frequent Words | O(n log k) | O(n) | Meta, Amazon, Google, Microsoft |

### Sliding Window + HashMap

When to use: Substring search with character constraints.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 3 Longest Substring Without Repeating | O(n) | O(k) | Google, Meta, Amazon, Microsoft |
| LC 76 Minimum Window Substring | O(n) | O(k) | Meta, Google, Amazon, Microsoft |
| LC 159 Longest Substring with At Most Two Distinct | O(n) | O(1) | Meta, Google |
| LC 340 Longest Substring with At Most K Distinct | O(n) | O(k) | Meta, Google, Amazon |
| LC 424 Longest Repeating Character Replacement | O(n) | O(1) | Meta, Google, Amazon |
| LC 438 Find All Anagrams in a String | O(n) | O(1) | Meta, Amazon, Google, Microsoft |
| LC 567 Permutation in String | O(n) | O(1) | Meta, Microsoft, Google, Amazon |
| LC 904 Fruit Into Baskets | O(n) | O(1) | Amazon, Google |

### Caching Patterns

When to use: Store computed values, memoization.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 146 LRU Cache | O(1) | O(n) | Amazon, Meta, Google, Microsoft, Apple |
| LC 460 LFU Cache | O(1) | O(n) | Amazon, Google, Meta, Microsoft |
| LC 432 All O`one Data Structure | O(1) | O(n) | Amazon, Google |

---

## Heap Patterns

### Top K Elements

When to use: K largest/smallest, K most frequent, K closest.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 215 Kth Largest Element in an Array | O(n log k) | O(k) | Meta, Amazon, Google, Microsoft |
| LC 347 Top K Frequent Elements | O(n log k) | O(n) | Meta, Amazon, Google, Microsoft |
| LC 373 Find K Pairs with Smallest Sums | O(k log k) | O(k) | Meta, Amazon, Google |
| LC 378 Kth Smallest Element in a Sorted Matrix | O(n log n) | O(n) | Meta, Amazon, Google, Microsoft |
| LC 451 Sort Characters By Frequency | O(n log n) | O(n) | Meta, Amazon, Google |
| LC 658 Find K Closest Elements | O(log n + k) | O(1) | Meta, Amazon, Google, Microsoft |
| LC 692 Top K Frequent Words | O(n log k) | O(n) | Meta, Amazon, Google, Microsoft |
| LC 703 Kth Largest Element in a Stream | O(n log k) | O(k) | Amazon, Google, Meta, Microsoft |
| LC 973 K Closest Points to Origin | O(n log k) | O(k) | Meta, Amazon, Google, Microsoft |
| LC 1046 Last Stone Weight | O(n log n) | O(n) | Meta, Amazon, Google |

### Median / Running Median

When to use: Finding median from data stream, sliding window median.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 295 Find Median from Data Stream | O(log n) | O(n) | Meta, Amazon, Google, Microsoft |
| LC 480 Sliding Window Median | O(n log k) | O(k) | Google, Amazon, Meta |

### Merge K Lists

When to use: Combining k sorted lists or arrays.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 23 Merge k Sorted Lists | O(n log k) | O(k) | Amazon, Meta, Google, Microsoft |
| LC 264 Ugly Number II | O(n log n) | O(n) | Meta, Amazon, Google, Microsoft |
| LC 313 Super Ugly Number | O(n log k) | O(n) | Google, Amazon |

---

## Trie Patterns

### Prefix Search

When to use: Autocomplete, spell checker, prefix-based queries.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 208 Implement Trie (Prefix Tree) | O(n) per op | O(n*m) | Meta, Amazon, Google, Microsoft, Apple |
| LC 211 Design Add and Search Words | O(n) per op | O(n*m) | Meta, Amazon, Google, Microsoft |
| LC 212 Word Search II | O(M*N*4^L) | O(K*L) | Amazon, Meta, Google, Microsoft |
| LC 336 Palindrome Pairs | O(n*k^2) | O(n*k) | Google, Meta, Amazon |
| LC 421 Maximum XOR of Two Numbers | O(n) | O(n) | Google, Meta, Microsoft |
| LC 472 Concatenated Words | O(n*k^2) | O(n*k) | Amazon, Google |
| LC 642 Design Search Autocomplete | O(n) per op | O(n*m) | Meta, Google, Amazon |
| LC 648 Replace Words | O(n*k) | O(m) | Amazon, Meta, Google |
| LC 676 Implement Magic Dictionary | O(n*k) | O(n*k) | Google, Meta, Amazon |
| LC 677 Map Sum Pairs | O(k) per op | O(n*k) | Google, Amazon |
| LC 720 Longest Word in Dictionary | O(n*k) | O(n*k) | Amazon, Google, Meta |
| LC 745 Prefix and Suffix Search | O(n*k) | O(n*k) | Google, Amazon |
| LC 820 Short Encoding of Words | O(n*k) | O(n*k) | Amazon, Google |

### Autocomplete Systems

When to use: Real-time search suggestions, typeahead.

| Problem | Time | Space | Companies |
|---------|------|-------|-----------|
| LC 642 Design Search Autocomplete | O(n log k) | O(n*m) | Meta, Google, Amazon |
| LC 1065 Index Pairs of a String | O(n*k) | O(m) | Amazon |
| LC 1166 Design File System | O(k) | O(n*k) | Google, Amazon, Meta |
| LC 1268 Search Suggestions System | O(n*k + m) | O(m) | Meta, Amazon, Google, Microsoft |

---

## Complexity Reference

| Operation | Array (Sorted) | Linked List | Hash Table | BST (Balanced) | Heap | Trie | Union-Find |
|-----------|---------------|-------------|------------|----------------|------|------|------------|
| Access | O(1) / O(log n) | O(n) | O(1) avg | O(log n) | O(1) min/max | O(n) | O(alpha(n)) |
| Search | O(n) / O(log n) | O(n) | O(1) avg | O(log n) | O(n) | O(k) | O(alpha(n)) |
| Insert | O(n) | O(1) | O(1) avg | O(log n) | O(log n) | O(k) | O(alpha(n)) |
| Delete | O(n) | O(1) | O(1) avg | O(log n) | O(log n) | O(k) | O(alpha(n)) |
| Space | O(n) | O(n) | O(n) | O(n) | O(n) | O(n*k) | O(n) |

### Company Frequency Key
- **Very High**: Asked in every interview
- **High**: Asked in majority of interviews
- **Medium**: Commonly asked
- **Low**: Occasionally asked
