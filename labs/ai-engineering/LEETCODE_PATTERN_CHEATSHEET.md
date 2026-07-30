# LeetCode Pattern Cheatsheet

## Array & String Patterns

| Pattern | When to Use | Example Problems |
|---------|-------------|------------------|
| Two Pointers | Sorted array, palindrome, target sum | Two Sum II, Container With Most Water |
| Sliding Window | Subarray/substring problems | Longest Substring Without Repeating, Minimum Window Substring |
| Prefix Sum | Range sum queries, running totals | Subarray Sum Equals K, Range Sum Query |
| String Matching | Pattern search in strings | Implement strStr(), Regular Expression Matching |

## Linked List Patterns

| Pattern | When to Use | Example Problems |
|---------|-------------|------------------|
| Fast & Slow Pointer | Cycle detection, middle element | Linked List Cycle, Find Middle |
| Reverse In-Place | Palindrome check, reorder | Reverse Linked List, Palindrome Linked List |
| Merge Lists | Sorted list combination | Merge Two Sorted Lists, Merge K Sorted |

## Tree & Graph Patterns

| Pattern | When to Use | Example Problems |
|---------|-------------|------------------|
| BFS | Shortest path, level-order traversal | Binary Tree Level Order, Word Ladder |
| DFS | Path existence, tree properties | Same Tree, Path Sum, Number of Islands |
| Trie | Prefix matching, autocomplete | Implement Trie, Word Search II |
| Topological Sort | Dependency resolution | Course Schedule, Alien Dictionary |
| Union Find | Connected components, dynamic connectivity | Number of Islands II, Accounts Merge |

## Dynamic Programming Patterns

| Pattern | When to Use | Example Problems |
|---------|-------------|------------------|
| 1D DP | Linear sequence optimization | Climbing Stairs, House Robber, Coin Change |
| 2D DP | Grid problems, string alignment | Longest Common Subsequence, Edit Distance |
| Knapsack | Resource allocation with constraints | 0/1 Knapsack, Partition Equal Subset Sum |
| LIS | Longest increasing subsequence variants | Longest Increasing Subsequence, Russian Doll |
| Palindromic | Palindrome substring problems | Longest Palindromic Substring, Palindromic Substrings |

## Stack & Queue Patterns

| Pattern | When to Use | Example Problems |
|---------|-------------|------------------|
| Monotonic Stack | Next greater/smaller element | Daily Temperatures, Largest Rectangle |
| Min/Max Stack | O(1) min/max tracking | Min Stack, Max Stack |
| Deque | Sliding window extremum | Sliding Window Maximum |

## Advanced Patterns

| Pattern | When to Use | Example Problems |
|---------|-------------|------------------|
| Binary Search | Sorted array search, rotated arrays | Search in Rotated Array, Find Peak Element |
| Backtracking | Combinatorial enumeration, constraints | Permutations, N-Queens, Subsets |
| Dijkstra | Weighted shortest path | Network Delay Time, Cheapest Flights |
| Bit Manipulation | XOR tricks, subset generation | Single Number, Power of Two |

## Quick Reference: Time Complexities

| Structure | Access | Search | Insert | Delete |
|-----------|--------|--------|--------|--------|
| Array | O(1) | O(n) | O(n) | O(n) |
| Stack | O(n) | O(n) | O(1) | O(1) |
| Queue | O(n) | O(n) | O(1) | O(1) |
| HashMap | O(1) | O(1) | O(1) | O(1) |
| BST (balanced) | O(log n) | O(log n) | O(log n) | O(log n) |
| Heap | O(1) | O(n) | O(log n) | O(log n) |