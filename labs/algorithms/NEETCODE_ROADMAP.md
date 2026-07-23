# NEETCODE_ROADMAP.md — Algorithms Academy Mapping

> Complete mapping of Algorithms Academy content (labs 01-40) to popular interview prep lists: NeetCode 150, Blind 75, Grind 75, and Striver's SDE Sheet. Each problem links to the lab teaching the underlying technique.

---

## Table of Contents
1. [NeetCode 150 Mapping](#neetcode-150-mapping)
2. [Blind 75 Mapping](#blind-75-mapping)
3. [Grind 75 (Top 50) Mapping](#grind-75-top-50-mapping)
4. [Striver's SDE Sheet (Top 50) Mapping](#strievers-sde-sheet-top-50-mapping)
5. [Study Plans](#study-plans)

---

## NeetCode 150 Mapping

### Arrays & Hashing
| # | Problem | Difficulty | Company Tags | Lab |
|---|---------|-----------|--------------|-----|
| 1 | Two Sum | Easy | Amazon, Google, Apple | 01-sorting (sort + two-pointer variant) |
| 2 | Contains Duplicate | Easy | Amazon, Microsoft, FB | 01-sorting |
| 3 | Valid Anagram | Easy | Amazon, Google, Meta | 01-sorting (sort comparison) |
| 4 | Group Anagrams | Medium | Amazon, Bloomberg, Uber | 01-sorting, 24-hashing |
| 5 | Top K Frequent Elements | Medium | Meta, Amazon, Google | 28-bloom-filters (counting), 24-hashing |
| 6 | Product of Array Except Self | Medium | Meta, Amazon, Apple | 01-sorting (prefix/suffix technique) |
| 7 | Valid Sudoku | Medium | Amazon, Apple, Uber | 24-hashing |
| 8 | Encode and Decode Strings | Medium | Microsoft, Google | 02-searching |
| 9 | Longest Consecutive Sequence | Medium | Amazon, Google | 01-sorting, 24-hashing |
| 10 | Largest Number | Medium | Amazon, Microsoft | 01-sorting (custom comparator) |

### Two Pointers
| # | Problem | Difficulty | Company Tags | Lab |
|---|---------|-----------|--------------|-----|
| 11 | Valid Palindrome | Easy | Meta, Amazon, Apple | 02-searching |
| 12 | Two Sum II | Medium | Amazon, Google | 02-searching, 01-sorting |
| 13 | 3Sum | Medium | Amazon, Meta, Google | 02-searching, 01-sorting |
| 14 | Container With Most Water | Medium | Meta, Amazon, Google | 02-searching (two-pointer area) |
| 15 | Trapping Rain Water | Hard | Amazon, Meta, Google | 01-sorting (prefix/suffix max), 11-dp |

### Sliding Window
| # | Problem | Difficulty | Company Tags | Lab |
|---|---------|-----------|--------------|-----|
| 16 | Best Time to Buy & Sell Stock | Easy | Amazon, Meta, Google | 11-dynamic-programming |
| 17 | Longest Substring Without Repeating | Medium | Amazon, Meta, Google | 02-searching, 24-hashing |
| 18 | Longest Repeating Character Replacement | Medium | Meta, Google | 02-searching |
| 19 | Permutation in String | Medium | Microsoft, Google | 02-searching |
| 20 | Minimum Window Substring | Hard | Meta, Amazon, Google | 02-searching, 24-hashing |
| 21 | Sliding Window Maximum | Hard | Amazon, Google, Citadel | 07-queue-deque |

### Stack
| # | Problem | Difficulty | Company Tags | Lab |
|---|---------|-----------|--------------|-----|
| 22 | Valid Parentheses | Easy | Amazon, Meta, Microsoft | 05-stack |
| 23 | Min Stack | Medium | Amazon, Google, Uber | 05-stack |
| 24 | Evaluate Reverse Polish Notation | Medium | Amazon, Meta | 05-stack |
| 25 | Generate Parentheses | Medium | Amazon, Meta, Google | 10-backtracking |
| 26 | Daily Temperatures | Medium | Meta, Amazon, Bloomberg | 05-stack (monotonic) |
| 27 | Car Fleet | Medium | Google, Uber | 05-stack, 01-sorting |
| 28 | Largest Rectangle in Histogram | Hard | Amazon, Meta, Google | 05-stack (monotonic) |

### Binary Search
| # | Problem | Difficulty | Company Tags | Lab |
|---|---------|-----------|--------------|-----|
| 29 | Binary Search | Easy | Amazon, Google, Meta | 02-searching |
| 30 | Search a 2D Matrix | Medium | Amazon, Meta | 02-searching |
| 31 | Koko Eating Bananas | Medium | Amazon, Google | 02-searching |
| 32 | Find Minimum in Rotated Sorted Array | Medium | Amazon, Meta, Microsoft | 02-searching |
| 33 | Search in Rotated Sorted Array | Medium | Amazon, Meta, Google | 02-searching |
| 34 | Time Based Key-Value Store | Medium | Google, Amazon, Uber | 02-searching, 24-hashing |
| 35 | Median of Two Sorted Arrays | Hard | Amazon, Google, Meta | 02-searching, 03-divide-and-conquer |

### Linked List
| # | Problem | Difficulty | Company Tags | Lab |
|---|---------|-----------|--------------|-----|
| 36 | Reverse Linked List | Easy | Amazon, Meta, Microsoft | 06-linked-list |
| 37 | Merge Two Sorted Lists | Easy | Amazon, Meta, Google | 06-linked-list, 01-sorting |
| 38 | Linked List Cycle | Easy | Amazon, Meta, Microsoft | 06-linked-list |
| 39 | Remove Nth Node From End | Medium | Amazon, Meta, Google | 06-linked-list |
| 40 | Reorder List | Medium | Amazon, Meta, Uber | 06-linked-list |
| 41 | Add Two Numbers | Medium | Amazon, Meta, Google | 06-linked-list |
| 42 | LRU Cache | Medium | Amazon, Meta, Google | 06-linked-list, 24-hashing |
| 43 | Merge K Sorted Lists | Hard | Amazon, Meta, Google | 06-linked-list, 14-heap-priority-queue |
| 44 | Reverse Nodes in k-Group | Hard | Amazon, Meta, Microsoft | 06-linked-list |

### Trees
| # | Problem | Difficulty | Company Tags | Lab |
|---|---------|-----------|--------------|-----|
| 45 | Invert Binary Tree | Easy | Amazon, Meta, Google | 08-tree |
| 46 | Maximum Depth of Binary Tree | Easy | Amazon, Meta, Google | 08-tree |
| 47 | Same Tree | Easy | Amazon, Meta | 08-tree |
| 48 | Subtree of Another Tree | Easy | Meta, Amazon, Google | 08-tree |
| 49 | Lowest Common Ancestor | Medium | Meta, Amazon, Google | 08-tree, 17-graph-traversal |
| 50 | Binary Tree Level Order | Medium | Amazon, Meta, Microsoft | 08-tree, 07-queue-deque |
| 51 | Binary Tree Right Side View | Medium | Meta, Amazon | 08-tree |
| 52 | Kth Smallest in BST | Medium | Amazon, Meta, Google | 08-tree |
| 53 | Build Tree from Inorder/Preorder | Medium | Amazon, Meta, Microsoft | 08-tree, 03-divide-and-conquer |
| 54 | Binary Tree Max Path Sum | Hard | Meta, Amazon, Google | 08-tree, 11-dynamic-programming |
| 55 | Serialize and Deserialize Binary Tree | Hard | Amazon, Meta, Google | 08-tree |

### Tries
| # | Problem | Difficulty | Company Tags | Lab |
|---|---------|-----------|--------------|-----|
| 56 | Implement Trie (Prefix Tree) | Medium | Amazon, Meta, Google | 09-trie |
| 57 | Design Add and Search Words | Medium | Amazon, Meta | 09-trie, 10-backtracking |
| 58 | Word Search II | Hard | Amazon, Meta, Google | 09-trie, 10-backtracking, 17-graph-traversal |

### Heap / Priority Queue
| # | Problem | Difficulty | Company Tags | Lab |
|---|---------|-----------|--------------|-----|
| 59 | Kth Largest Element in Stream | Easy | Amazon, Google | 14-heap-priority-queue |
| 60 | Last Stone Weight | Easy | Amazon, Meta | 14-heap-priority-queue |
| 61 | Kth Largest Element in Array | Medium | Amazon, Meta, Google | 14-heap-priority-queue, 03-divide-and-conquer (quickselect) |
| 62 | Task Scheduler | Medium | Amazon, Meta, Google | 14-heap-priority-queue, 12-greedy |
| 63 | Design Twitter | Medium | Amazon, Meta, Google | 14-heap-priority-queue, 24-hashing |
| 64 | Find Median from Data Stream | Hard | Amazon, Google, Two Sigma | 14-heap-priority-queue |

### Backtracking
| # | Problem | Difficulty | Company Tags | Lab |
|---|---------|-----------|--------------|-----|
| 65 | Subsets | Medium | Amazon, Meta, Google | 10-backtracking |
| 66 | Combinations | Medium | Amazon, Meta | 10-backtracking |
| 67 | Permutations | Medium | Amazon, Meta, Google | 10-backtracking |
| 68 | Combination Sum | Medium | Amazon, Meta, Google | 10-backtracking |
| 69 | Word Search | Medium | Amazon, Meta, Microsoft | 10-backtracking, 17-graph-traversal |
| 70 | Palindrome Partitioning | Medium | Amazon, Meta, Google | 10-backtracking |
| 71 | Letter Combinations of Phone Number | Medium | Amazon, Meta, Google | 10-backtracking |
| 72 | N-Queens | Hard | Amazon, Meta, Google | 10-backtracking |

### Graphs
| # | Problem | Difficulty | Company Tags | Lab |
|---|---------|-----------|--------------|-----|
| 73 | Number of Islands | Medium | Amazon, Meta, Google | 17-graph-traversal |
| 74 | Clone Graph | Medium | Amazon, Meta, Google | 17-graph-traversal |
| 75 | Max Area of Island | Medium | Amazon, Meta | 17-graph-traversal |
| 76 | Pacific Atlantic Water Flow | Medium | Amazon, Meta, Google | 17-graph-traversal |
| 77 | Surrounded Regions | Medium | Amazon, Meta | 17-graph-traversal |
| 78 | Rotting Oranges | Medium | Amazon, Meta, Uber | 17-graph-traversal |
| 79 | Walls and Gates | Medium | Amazon, Meta, Google | 17-graph-traversal |
| 80 | Course Schedule | Medium | Amazon, Meta, Google | 18-topological-sort |
| 81 | Course Schedule II | Medium | Amazon, Meta, Google | 18-topological-sort |
| 82 | Graph Valid Tree | Medium | Amazon, Meta | 17-graph-traversal, 19-union-find |
| 83 | Number of Connected Components | Medium | Amazon, Meta | 19-union-find |
| 84 | Redundant Connection | Medium | Amazon, Google | 19-union-find |
| 85 | Word Ladder | Hard | Amazon, Meta, Google | 17-graph-traversal (BFS) |

### Advanced Graphs
| # | Problem | Difficulty | Company Tags | Lab |
|---|---------|-----------|--------------|-----|
| 86 | Reconstruct Itinerary | Hard | Amazon, Google | 18-topological-sort, 17-graph-traversal |
| 87 | Min Cost to Connect All Points | Medium | Amazon, Google | 20-minimum-spanning-tree |
| 88 | Network Delay Time | Medium | Amazon, Google, Meta | 21-shortest-path (Dijkstra) |
| 89 | Cheapest Flights Within K Stops | Medium | Amazon, Meta, Google | 21-shortest-path (Bellman-Ford) |
| 90 | Alien Dictionary | Hard | Amazon, Meta, Google | 18-topological-sort |
| 91 | Swim in Rising Water | Hard | Google, Amazon | 14-heap-priority-queue, 19-union-find |

### 1D Dynamic Programming
| # | Problem | Difficulty | Company Tags | Lab |
|---|---------|-----------|--------------|-----|
| 92 | Climbing Stairs | Easy | Amazon, Meta, Google | 11-dynamic-programming |
| 93 | Min Cost Climbing Stairs | Easy | Amazon, Meta | 11-dynamic-programming |
| 94 | House Robber | Medium | Amazon, Meta, Google | 11-dynamic-programming |
| 95 | House Robber II | Medium | Amazon, Meta | 11-dynamic-programming |
| 96 | Longest Palindromic Substring | Medium | Amazon, Meta, Microsoft | 11-dynamic-programming |
| 97 | Palindromic Substrings | Medium | Amazon, Meta | 11-dynamic-programming |
| 98 | Decode Ways | Medium | Amazon, Meta, Google | 11-dynamic-programming |
| 99 | Coin Change | Medium | Amazon, Meta, Google | 11-dynamic-programming, 12-greedy |
| 100 | Maximum Product Subarray | Medium | Amazon, Meta, Google | 11-dynamic-programming |
| 101 | Word Break | Medium | Amazon, Meta, Google | 11-dynamic-programming, 09-trie |
| 102 | Longest Increasing Subsequence | Medium | Amazon, Meta, Google | 11-dynamic-programming |
| 103 | Partition Equal Subset Sum | Medium | Amazon, Meta, Google | 11-dynamic-programming (knapsack) |

### 2D Dynamic Programming
| # | Problem | Difficulty | Company Tags | Lab |
|---|---------|-----------|--------------|-----|
| 104 | Unique Paths | Medium | Amazon, Meta, Google | 11-dynamic-programming |
| 105 | Longest Common Subsequence | Medium | Amazon, Meta, Google | 11-dynamic-programming |
| 106 | Edit Distance | Medium | Amazon, Meta, Google | 11-dynamic-programming |
| 107 | Distinct Subsequences | Hard | Amazon, Google | 11-dynamic-programming |
| 108 | Burst Balloons | Hard | Amazon, Google, Meta | 11-dynamic-programming (interval DP) |
| 109 | Regular Expression Matching | Hard | Amazon, Meta, Google | 11-dynamic-programming, 25-string-algorithms |
| 110 | Best Time to Buy/Sell Stock IV | Hard | Amazon, Meta | 11-dynamic-programming |
| 111 | Minimum Difficulty Job Schedule | Hard | Amazon, Google | 11-dynamic-programming |

### Greedy
| # | Problem | Difficulty | Company Tags | Lab |
|---|---------|-----------|--------------|-----|
| 112 | Maximum Subarray | Easy | Amazon, Meta, Microsoft | 03-divide-and-conquer, 12-greedy |
| 113 | Jump Game | Medium | Amazon, Meta, Google | 12-greedy |
| 114 | Jump Game II | Medium | Amazon, Meta, Google | 12-greedy |
| 115 | Gas Station | Medium | Amazon, Meta, Google | 12-greedy |
| 116 | Hand of Straights | Medium | Amazon, Google | 12-greedy |
| 117 | Merge Triplets | Medium | Amazon, Google | 12-greedy |
| 118 | Partition Labels | Medium | Amazon, Meta, Google | 12-greedy, 02-searching |
| 119 | Valid Parenthesis String | Medium | Amazon, Meta | 12-greedy |

### Intervals
| # | Problem | Difficulty | Company Tags | Lab |
|---|---------|-----------|--------------|-----|
| 120 | Insert Interval | Medium | Amazon, Meta, Google | 01-sorting, 04-interval-scheduling |
| 121 | Merge Intervals | Medium | Amazon, Meta, Google | 01-sorting, 04-interval-scheduling |
| 122 | Non-Overlapping Intervals | Medium | Amazon, Meta, Google | 04-interval-scheduling, 12-greedy |
| 123 | Meeting Rooms II | Medium | Amazon, Meta, Google | 04-interval-scheduling, 14-heap |
| 124 | Minimum Interval to Include Each Query | Hard | Amazon, Google | 04-interval-scheduling, 14-heap |

### Math & Geometry
| # | Problem | Difficulty | Company Tags | Lab |
|---|---------|-----------|--------------|-----|
| 125 | Rotate Image | Medium | Amazon, Meta, Google | 02-searching |
| 126 | Spiral Matrix | Medium | Amazon, Meta, Microsoft | 02-searching |
| 127 | Set Matrix Zeroes | Medium | Amazon, Meta | 01-sorting |
| 128 | Happy Number | Easy | Amazon, Meta, Google | 24-hashing |
| 129 | Plus One | Easy | Amazon, Google | 01-sorting |
| 130 | Pow(x, n) | Medium | Amazon, Meta, Google | 03-divide-and-conquer |
| 131 | Multiply Strings | Medium | Amazon, Meta, Microsoft | 03-divide-and-conquer |
| 132 | Detect Squares | Medium | Amazon, Google | 24-hashing |

### Bit Manipulation
| # | Problem | Difficulty | Company Tags | Lab |
|---|---------|-----------|--------------|-----|
| 133 | Single Number | Easy | Amazon, Meta, Google | 13-bit-manipulation |
| 134 | Number of 1 Bits | Easy | Amazon, Meta, Microsoft | 13-bit-manipulation |
| 135 | Counting Bits | Easy | Amazon, Meta, Google | 13-bit-manipulation, 11-dp |
| 136 | Reverse Integer | Medium | Amazon, Meta, Google | 13-bit-manipulation |
| 137 | Missing Number | Easy | Amazon, Meta, Microsoft | 13-bit-manipulation |
| 138 | Sum of Two Integers | Medium | Amazon, Meta, Google | 13-bit-manipulation |
| 139 | Reverse Bits | Easy | Amazon, Meta, Google | 13-bit-manipulation |
| 140 | Maximum XOR of Two Numbers | Medium | Amazon, Google | 13-bit-manipulation, 09-trie |

---

## Blind 75 Mapping

| # | Problem | Lab | Pattern | Difficulty | Company |
|---|---------|-----|---------|-----------|---------|
| 1 | Two Sum | 01-sorting | Hash Map + Sort | Easy | Amazon, Google |
| 2 | Longest Substring Without Repeating Characters | 02-searching | Sliding Window | Medium | Amazon, Meta |
| 3 | Longest Palindromic Substring | 11-dynamic-programming | DP | Medium | Amazon, Microsoft |
| 4 | Container With Most Water | 02-searching | Two Pointer | Medium | Meta, Google |
| 5 | 3Sum | 01-sorting | Sort + Two Pointer | Medium | Amazon, Meta |
| 6 | Remove Nth Node From End | 06-linked-list | Two Pointer | Medium | Amazon, Meta |
| 7 | Valid Parentheses | 05-stack | Stack | Easy | Amazon, Meta |
| 8 | Merge Two Sorted Lists | 06-linked-list | Merge | Easy | Amazon, Meta |
| 9 | Merge K Sorted Lists | 14-heap-priority-queue | Heap | Hard | Amazon, Meta |
| 10 | Search in Rotated Sorted Array | 02-searching | Binary Search | Medium | Amazon, Meta |
| 11 | Combination Sum | 10-backtracking | Backtracking | Medium | Amazon, Meta |
| 12 | Rotate Image | 02-searching | Matrix | Medium | Amazon, Meta |
| 13 | Group Anagrams | 01-sorting | Hash Map | Medium | Amazon, Bloomberg |
| 14 | Maximum Subarray | 03-divide-and-conquer | D&C / Greedy | Medium | Amazon, Meta |
| 15 | Spiral Matrix | 02-searching | Simulation | Medium | Amazon, Microsoft |
| 16 | Jump Game | 12-greedy | Greedy | Medium | Amazon, Meta |
| 17 | Merge Intervals | 04-interval-scheduling | Sort + Merge | Medium | Amazon, Meta |
| 18 | Insert Interval | 04-interval-scheduling | Sort + Merge | Medium | Amazon, Meta |
| 19 | Unique Paths | 11-dynamic-programming | 2D DP | Medium | Amazon, Meta |
| 20 | Climbing Stairs | 11-dynamic-programming | 1D DP | Easy | Amazon, Meta |
| 21 | Coin Change | 11-dynamic-programming | DP | Medium | Amazon, Meta |
| 22 | Longest Increasing Subsequence | 11-dynamic-programming | DP | Medium | Amazon, Meta |
| 23 | Word Break | 11-dynamic-programming | DP | Medium | Amazon, Meta |
| 24 | House Robber | 11-dynamic-programming | DP | Medium | Amazon, Meta |
| 25 | Number of Islands | 17-graph-traversal | BFS/DFS | Medium | Amazon, Meta |
| 26 | Clone Graph | 17-graph-traversal | DFS/Hash | Medium | Amazon, Meta |
| 27 | Pacific Atlantic Water Flow | 17-graph-traversal | DFS | Medium | Amazon, Meta |
| 28 | Course Schedule | 18-topological-sort | Topo Sort | Medium | Amazon, Meta |
| 29 | Graph Valid Tree | 19-union-find | Union-Find | Medium | Amazon, Meta |
| 30 | Number of Connected Components | 19-union-find | Union-Find | Medium | Amazon, Meta |
| 31 | Alien Dictionary | 18-topological-sort | Topo Sort | Hard | Amazon, Meta |
| 32 | Longest Repeating Character Replacement | 02-searching | Sliding Window | Medium | Meta, Google |
| 33 | Minimum Window Substring | 02-searching | Sliding Window | Hard | Meta, Amazon |
| 34 | Find Minimum in Rotated Sorted Array | 02-searching | Binary Search | Medium | Amazon, Meta |
| 35 | Word Search | 10-backtracking | Backtracking | Medium | Amazon, Meta |
| 36 | LRU Cache | 06-linked-list | LL + HashMap | Medium | Amazon, Meta |
| 37 | Task Scheduler | 14-heap-priority-queue | Heap/Greedy | Medium | Amazon, Meta |
| 38 | Serialize and Deserialize Binary Tree | 08-tree | BFS/DFS | Hard | Amazon, Meta |
| 39 | Longest Consecutive Sequence | 01-sorting | Hash Set | Medium | Amazon, Google |
| 40 | Trapping Rain Water | 11-dynamic-programming | DP/Two Pointer | Hard | Amazon, Meta |
| 41 | Find Median from Data Stream | 14-heap-priority-queue | Two Heaps | Hard | Amazon, Two Sigma |
| 42 | Word Ladder | 17-graph-traversal | BFS | Hard | Amazon, Meta |
| 43 | Basic Calculator II | 05-stack | Stack | Medium | Amazon, Meta |
| 44 | Implement Trie | 09-trie | Trie | Medium | Amazon, Meta |
| 45 | Top K Frequent Elements | 24-hashing | Bucket Sort | Medium | Amazon, Meta |
| 46 | Decode Ways | 11-dynamic-programming | DP | Medium | Amazon, Meta |
| 47 | Best Time to Buy and Sell Stock | 11-dynamic-programming | DP/Greedy | Easy | Amazon, Meta |
| 48 | Set Matrix Zeroes | 01-sorting | In-Place | Medium | Amazon, Meta |
| 49 | Subsets | 10-backtracking | Backtracking | Medium | Amazon, Meta |
| 50 | Palindromic Substrings | 11-dynamic-programming | DP | Medium | Amazon, Meta |
| 51 | Maximal Rectangle | 05-stack | Monotonic Stack | Hard | Amazon, Google |
| 52 | Binary Tree Level Order Traversal | 08-tree | BFS | Medium | Amazon, Meta |
| 53 | Maximum Depth of Binary Tree | 08-tree | DFS | Easy | Amazon, Meta |
| 54 | Construct Binary Tree from Pre/Inorder | 08-tree | D&C | Medium | Amazon, Meta |
| 55 | Same Tree | 08-tree | DFS | Easy | Amazon, Meta |
| 56 | Binary Tree Maximum Path Sum | 08-tree | DFS/DP | Hard | Meta, Amazon |
| 57 | Validate Binary Search Tree | 08-tree | DFS | Medium | Amazon, Meta |
| 58 | Invert Binary Tree | 08-tree | DFS | Easy | Amazon, Meta |
| 59 | Kth Smallest Element in BST | 08-tree | In-order | Medium | Amazon, Meta |
| 60 | Lowest Common Ancestor of BST | 08-tree | DFS | Medium | Amazon, Meta |
| 61 | Product of Array Except Self | 01-sorting | Prefix/Suffix | Medium | Amazon, Meta |
| 62 | Missing Number | 13-bit-manipulation | XOR | Easy | Amazon, Meta |
| 63 | Sum of Two Integers | 13-bit-manipulation | Bit Ops | Medium | Amazon, Meta |
| 64 | Reverse Bits | 13-bit-manipulation | Bit Ops | Easy | Amazon, Meta |
| 65 | Number of 1 Bits | 13-bit-manipulation | Bit Ops | Easy | Amazon, Meta |
| 66 | Counting Bits | 13-bit-manipulation | DP/Bit | Easy | Amazon, Meta |
| 67 | House Robber II | 11-dynamic-programming | DP | Medium | Amazon, Meta |
| 68 | Longest Common Subsequence | 11-dynamic-programming | 2D DP | Medium | Amazon, Meta |
| 69 | Edit Distance | 11-dynamic-programming | 2D DP | Medium | Amazon, Meta |
| 70 | Minimum Path Sum | 11-dynamic-programming | 2D DP | Medium | Amazon, Meta |
| 71 | Coin Change II | 11-dynamic-programming | Knapsack DP | Medium | Amazon, Meta |
| 72 | Partition Equal Subset Sum | 11-dynamic-programming | Knapsack DP | Medium | Amazon, Meta |
| 73 | Maximum Product Subarray | 11-dynamic-programming | DP | Medium | Amazon, Meta |
| 74 | Contains Duplicate | 01-sorting | Hash Set | Easy | Amazon, Google |
| 75 | Valid Anagram | 01-sorting | Sort/Hash | Easy | Amazon, Meta |

---

## Grind 75 (Top 50) Mapping

| # | Problem | Lab | Pattern | Difficulty | Company |
|---|---------|-----|---------|-----------|---------|
| 1 | Two Sum | 01-sorting | Hash Map | Easy | Amazon, Google |
| 2 | Valid Parentheses | 05-stack | Stack | Easy | Amazon, Meta |
| 3 | Merge Two Sorted Lists | 06-linked-list | Two Pointer | Easy | Amazon, Meta |
| 4 | Best Time to Buy and Sell Stock | 11-dynamic-programming | Kadane | Easy | Amazon, Meta |
| 5 | Valid Palindrome | 02-searching | Two Pointer | Easy | Meta, Amazon |
| 6 | Invert Binary Tree | 08-tree | DFS | Easy | Amazon, Meta |
| 7 | Valid Anagram | 01-sorting | Sort | Easy | Amazon, Meta |
| 8 | Binary Search | 02-searching | Binary Search | Easy | Amazon, Google |
| 9 | Flood Fill | 17-graph-traversal | BFS/DFS | Easy | Amazon, Meta |
| 10 | Lowest Common Ancestor BST | 08-tree | DFS | Easy | Amazon, Meta |
| 11 | Balanced Binary Tree | 08-tree | DFS | Easy | Amazon, Google |
| 12 | Linked List Cycle | 06-linked-list | Floyd | Easy | Amazon, Meta |
| 13 | Implement Queue using Stacks | 05-stack | Two Stacks | Easy | Amazon, Google |
| 14 | First Bad Version | 02-searching | Binary Search | Easy | Amazon, Meta |
| 15 | Ransom Note | 24-hashing | Hash Map | Easy | Amazon, Apple |
| 16 | Climbing Stairs | 11-dynamic-programming | DP | Easy | Amazon, Meta |
| 17 | Longest Palindrome | 01-sorting | Hash Set | Easy | Amazon, Google |
| 18 | Reverse Linked List | 06-linked-list | Iterative | Easy | Amazon, Meta |
| 19 | Majority Element | 01-sorting | Boyer-Moore | Easy | Amazon, Meta |
| 20 | Add Binary | 13-bit-manipulation | Bit Ops | Easy | Amazon, Meta |
| 21 | Diameter of Binary Tree | 08-tree | DFS | Easy | Amazon, Meta |
| 22 | Middle of Linked List | 06-linked-list | Fast/Slow | Easy | Amazon, Meta |
| 23 | Maximum Depth of Binary Tree | 08-tree | DFS | Easy | Amazon, Meta |
| 24 | Contains Duplicate | 01-sorting | Hash Set | Easy | Amazon, Google |
| 25 | Meeting Rooms | 04-interval-scheduling | Sort | Easy | Amazon, Meta |
| 26 | Roman to Integer | 24-hashing | Hash Map | Easy | Amazon, Google |
| 27 | Backspace String Compare | 05-stack | Stack | Easy | Amazon, Google |
| 28 | Counting Bits | 13-bit-manipulation | DP/Bit | Easy | Amazon, Meta |
| 29 | Longest Common Prefix | 09-trie | Trie | Easy | Amazon, Meta |
| 30 | Single Number | 13-bit-manipulation | XOR | Easy | Amazon, Meta |
| 31 | Palindrome Linked List | 06-linked-list | Fast/Slow | Easy | Amazon, Meta |
| 32 | Move Zeroes | 02-searching | Two Pointer | Easy | Amazon, Meta |
| 33 | Same Tree | 08-tree | DFS | Easy | Amazon, Meta |
| 34 | Sqrt(x) | 02-searching | Binary Search | Easy | Amazon, Meta |
| 35 | Maximum Subarray | 03-divide-and-conquer | Kadane | Medium | Amazon, Meta |
| 36 | Insert Interval | 04-interval-scheduling | Sort/Merge | Medium | Amazon, Google |
| 37 | 3Sum | 01-sorting | Two Pointer | Medium | Amazon, Meta |
| 38 | Product of Array Except Self | 01-sorting | Prefix/Suffix | Medium | Amazon, Meta |
| 39 | Lowest Common Ancestor III | 08-tree | DFS | Medium | Amazon, Meta |
| 40 | Validate Binary Search Tree | 08-tree | DFS | Medium | Amazon, Meta |
| 41 | Number of Islands | 17-graph-traversal | BFS/DFS | Medium | Amazon, Meta |
| 42 | Letter Combinations of Phone Number | 10-backtracking | Backtracking | Medium | Amazon, Meta |
| 43 | Maximum Width of Binary Tree | 08-tree | BFS | Medium | Amazon, Meta |
| 44 | Decode Ways | 11-dynamic-programming | DP | Medium | Amazon, Meta |
| 45 | Word Break | 11-dynamic-programming | DP | Medium | Amazon, Meta |
| 46 | K Closest Points to Origin | 14-heap-priority-queue | Heap | Medium | Amazon, Meta |
| 47 | Longest Substring Without Repeating | 02-searching | Sliding Window | Medium | Amazon, Meta |
| 48 | Minimum Window Substring | 02-searching | Sliding Window | Hard | Meta, Amazon |
| 49 | Serialize and Deserialize BST | 08-tree | BFS/DFS | Medium | Amazon, Meta |
| 50 | Trapping Rain Water | 11-dynamic-programming | Two Pointer | Hard | Amazon, Meta |

---

## Striver's SDE Sheet (Top 50) Mapping

| # | Problem | Lab | Pattern | Difficulty | Company |
|---|---------|-----|---------|-----------|---------|
| 1 | Set Matrix Zeroes | 01-sorting | In-Place | Medium | Amazon, Meta |
| 2 | Pascal's Triangle | 01-sorting | Simulation | Easy | Amazon, Google |
| 3 | Next Permutation | 01-sorting | Permutation | Medium | Amazon, Meta |
| 4 | Maximum Subarray | 03-divide-and-conquer | Kadane | Medium | Amazon, Meta |
| 5 | Sort Colors | 01-sorting | Dutch Flag | Medium | Amazon, Meta |
| 6 | Best Time to Buy and Sell Stock | 11-dynamic-programming | Greedy | Easy | Amazon, Meta |
| 7 | Rotate Image | 02-searching | Matrix | Medium | Amazon, Meta |
| 8 | Merge Intervals | 04-interval-scheduling | Sort/Merge | Medium | Amazon, Meta |
| 9 | Merge Sorted Array | 01-sorting | Two Pointer | Easy | Amazon, Meta |
| 10 | Find the Duplicate Number | 02-searching | Cycle Detection | Medium | Amazon, Google |
| 11 | Search in 2D Matrix | 02-searching | Binary Search | Medium | Amazon, Meta |
| 12 | Majority Element II | 01-sorting | Boyer-Moore | Medium | Amazon, Google |
| 13 | Reverse Pairs | 03-divide-and-conquer | Merge Sort | Hard | Amazon, Google |
| 14 | Two Sum | 01-sorting | Hash Map | Easy | Amazon, Google |
| 15 | 4Sum | 01-sorting | Two Pointer | Medium | Amazon, Meta |
| 16 | Longest Consecutive Sequence | 01-sorting | Hash Set | Medium | Amazon, Google |
| 17 | Largest Subarray with 0 Sum | 24-hashing | Prefix Sum | Medium | Amazon, Google |
| 18 | Subarray Sum Equals K | 24-hashing | Prefix Sum | Medium | Amazon, Meta |
| 19 | Longest Substring Without Repeat | 02-searching | Sliding Window | Medium | Amazon, Meta |
| 20 | Reverse Linked List | 06-linked-list | Iterative | Easy | Amazon, Meta |
| 21 | Middle of Linked List | 06-linked-list | Fast/Slow | Easy | Amazon, Meta |
| 22 | Merge Two Sorted Lists | 06-linked-list | Two Pointer | Easy | Amazon, Meta |
| 23 | Remove Nth Node From End | 06-linked-list | Two Pointer | Medium | Amazon, Meta |
| 24 | Add Two Numbers | 06-linked-list | Math | Medium | Amazon, Meta |
| 25 | Intersection of Two Linked Lists | 06-linked-list | Two Pointer | Easy | Amazon, Meta |
| 26 | Palindrome Linked List | 06-linked-list | Fast/Slow | Easy | Amazon, Meta |
| 27 | Linked List Cycle II | 06-linked-list | Floyd | Medium | Amazon, Meta |
| 28 | Flatten a Linked List | 06-linked-list | Merge | Medium | Amazon, Google |
| 29 | Rotate List | 06-linked-list | Two Pointer | Medium | Amazon, Meta |
| 30 | Clone a Linked List with Random | 06-linked-list | Hash Map | Hard | Amazon, Meta |
| 31 | 3Sum | 01-sorting | Two Pointer | Medium | Amazon, Meta |
| 32 | Trapping Rain Water | 11-dynamic-programming | Two Pointer | Hard | Amazon, Meta |
| 33 | Remove Duplicates from Sorted Array | 02-searching | Two Pointer | Easy | Amazon, Meta |
| 34 | Max Consecutive Ones | 02-searching | Sliding Window | Easy | Amazon, Google |
| 35 | Binary Tree Inorder Traversal | 08-tree | DFS/Stack | Easy | Amazon, Meta |
| 36 | Max Depth of Binary Tree | 08-tree | DFS | Easy | Amazon, Meta |
| 37 | Diameter of Binary Tree | 08-tree | DFS | Easy | Amazon, Meta |
| 38 | Balanced Binary Tree | 08-tree | DFS | Easy | Amazon, Meta |
| 39 | Same Tree | 08-tree | DFS | Easy | Amazon, Meta |
| 40 | Binary Tree Zigzag Level Order | 08-tree | BFS | Medium | Amazon, Meta |
| 41 | Vertical Order Traversal | 08-tree | BFS + Map | Hard | Amazon, Meta |
| 42 | Binary Tree Max Path Sum | 08-tree | DFS/DP | Hard | Meta, Amazon |
| 43 | Construct Tree from In/Preorder | 08-tree | D&C | Medium | Amazon, Meta |
| 44 | Symmetric Tree | 08-tree | DFS | Easy | Amazon, Meta |
| 45 | Flatten Binary Tree to LL | 08-tree | DFS | Medium | Amazon, Meta |
| 46 | Lowest Common Ancestor | 08-tree | DFS | Medium | Amazon, Meta |
| 47 | Kth Smallest in BST | 08-tree | In-order | Medium | Amazon, Meta |
| 48 | Validate BST | 08-tree | DFS | Medium | Amazon, Meta |
| 49 | Two Sum IV BST | 08-tree | DFS/Set | Easy | Amazon, Meta |
| 50 | BST Iterator | 05-stack | Stack | Medium | Amazon, Meta |

---

## Study Plans

### 4-Week Algorithm Interview Crash Course

Focus: Most-frequently-asked patterns (sorting, binary search, BFS/DFS, DP basics)

| Week | Topics | Labs | Goals |
|------|--------|------|-------|
| Week 1 | Sorting, Searching, Arrays | 01-sorting, 02-searching, 03-divide-and-conquer | Solve 20 Easy LC problems. Master Big-O notation. |
| Week 2 | Stacks, Queues, Linked Lists | 05-stack, 06-linked-list, 07-queue-deque | Solve 15 Medium problems. Implement from scratch. |
| Week 3 | Trees, Recursion, Backtracking | 08-tree, 09-trie, 10-backtracking | Solve 15 Medium problems. Master tree traversals. |
| Week 4 | DP Basics, Hash Maps | 11-dynamic-programming, 24-hashing | Solve 10 DP problems (Fibonacci, knapsack, LCS). |

**Daily routine**: 2 LeetCode problems (1 Easy + 1 Medium), 30 min each, review solutions.

### 8-Week Algorithm Mastery

| Week | Topics | Labs | Goals |
|------|--------|------|-------|
| Weeks 1-2 | Core data structures & sorting | 01-08 | Build fundamentals. 40+ problems solved. |
| Weeks 3-4 | DP, Greedy, Backtracking | 10-12, 14 | Solve all classic DP patterns. 30+ problems. |
| Weeks 5-6 | Graphs, Trees, String Algorithms | 17-21, 25 | BFS/DFS, Dijkstra, MST, KMP, Rabin-Karp. 30+ problems. |
| Weeks 7-8 | Advanced: Tries, Geometry, Bit Manipulation | 09, 13, 30, 31 | 20+ problems from hard categories. Mock interviews. |

### 12-Week Competitive Algorithm Preparation

| Week | Topics | Labs | Goals |
|------|--------|------|-------|
| 1-4 | All core + graph algorithms | 01-25 | 80+ problems. Strong fundamentals. |
| 5-6 | Number Theory, Cryptography, Game Theory | 26, 27, 29 | Mathematical maturity. Proof-based solutions. |
| 7-8 | Optimization, Scheduling, Load Balancing | 32, 33, 34 | Real-world algorithm design. |
| 9-10 | Advanced: FFT, Randomized, Streaming, External | 35, 36, 37, 38 | Deep theoretical understanding. |
| 11-12 | P/NP, Approximation, Online, Parallel | 39, 40, 41, 42 | Research-level discussion readiness. |

### Per-Company Study Plans

#### Google (DP + Graphs + String Algorithms)
- **Weeks 1-2**: Labs 01-08 (fundamentals)
- **Weeks 3-4**: Lab 11-dynamic-programming (30+ DP problems: knapsack, LCS, LIS, matrix DP)
- **Weeks 5-6**: Labs 17-21 (graph traversal, shortest path, MST, network flow)
- **Weeks 7-8**: Lab 25-string-algorithms (KMP, Rabin-Karp, Z-algorithm)
- **Focus**: Hard LC problems, optimal complexity, Google Docs coding practice

#### Amazon (Arrays + DP + Greedy)
- **Weeks 1-2**: Labs 01-sorting, 02-searching (arrays, two-pointer, sliding window)
- **Weeks 3-4**: Lab 11-dynamic-programming, Lab 12-greedy (stock problems, task scheduling)
- **Weeks 5-6**: Labs 14-heap-priority-queue, Lab 24-hashing (top-k, LRU cache)
- **Weeks 7-8**: Leadership Principles stories + system design
- **Focus**: Medium LC, STAR format, OOP + algorithm design

#### Meta (Graphs + DP + Backtracking)
- **Weeks 1-2**: Labs 08-tree, 09-trie, 10-backtracking
- **Weeks 3-4**: Labs 17-18 (graph traversal, topological sort, connected components)
- **Weeks 5-6**: Lab 11-dynamic-programming (2D DP, palindrome, edit distance)
- **Weeks 7-8**: Speed practice (2 problems per 45 min), CoderPad practice
- **Focus**: Graph + backtracking + speed

#### Microsoft (Trees + Sorting + String Algorithms)
- **Weeks 1-2**: Lab 01-sorting, Lab 02-searching (sorting variants, binary search)
- **Weeks 3-4**: Lab 08-tree, Lab 09-trie (all tree traversals, LCA, BST properties)
- **Weeks 5-6**: Lab 25-string-algorithms (pattern matching, anagrams, palindromes)
- **Weeks 7-8**: Lab 03-divide-and-conquer (quickselect, merge sort variants)
- **Focus**: Medium LC, whiteboard practice, recursion-heavy solutions

#### Two Sigma (Probability + Math + Randomized)
- **Weeks 1-2**: Labs 01-08 (fundamentals, speed)
- **Weeks 3-4**: Lab 29-probability-and-statistics (expected value, Markov chains, Monte Carlo)
- **Weeks 5-6**: Lab 26-number-theory (modular arithmetic, combinatorics), Lab 27-cryptography
- **Weeks 7-8**: Lab 36-randomized-algorithms (random sampling, shuffle, Las Vegas)
- **Focus**: Math + probability puzzles, DP with probability, distributed algorithms

### Resources Reference

| Resource | Location in Academy |
|----------|-------------------|
| LeetCode Solutions (per lab) | `labs/algorithms/XX-*/LEETCODE_SOLUTIONS/*.java` |
| Interview Questions (per lab) | `labs/algorithms/XX-*/INTERVIEW.md` |
| Mock Interview Questions (per lab) | `labs/algorithms/XX-*/MOCK_INTERVIEW.md` |
| Company Interview Process | `labs/algorithms/COMPANY_INTERVIEW_GUIDE.md` |
| Behavioral Interview Prep | `labs/algorithms/COMPANY_BEHAVIORAL_GUIDE.md` |
| Interview Full Guide | `labs/algorithms/ACADEMY_INTERVIEW_GUIDE.md` |
| Cracking the Interview Guide | `labs/algorithms/CRACKING_THE_INTERVIEW_GUIDE.md` |
| Interview Cheatsheet | `labs/algorithms/INTERVIEW_CHEATSHEET.md` |
| Pattern Cheatsheet | `labs/algorithms/LEETCODE_PATTERN_CHEATSHEET.md` |
