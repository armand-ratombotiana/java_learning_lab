# Interview Cheatsheet — Algorithms Academy

## The 5-Step Method
1. **Understand** — Restate → clarify → walk through example → identify paradigm
2. **Brute Force** — State naive approach → analyze complexity → find bottleneck
3. **Optimize** — Better DS? DP? Greedy? D&C? Graph algo? Binary search on answer?
4. **Implement** — Clean code, base cases first, talk through it, handle edges
5. **Test** — Trace example → edge cases → complexity → follow-ups

## Paradigm Identification
| Question | Paradigm |
|----------|----------|
| Optimal substructure + overlapping subproblems? | DP |
| Local choice → global optimum? | Greedy |
| Split input, combine results? | Divide & Conquer |
| Explore all configurations? | Backtracking |
| Shortest path in graph? | BFS/Dijkstra |
| Cycle detection / dependency? | Topological sort |
| String pattern matching? | KMP / Rabin-Karp |
| Monotonic search space? | Binary Search |

## Time Complexity Reference
| Complexity | Name | When |
|------------|------|------|
| O(1) | Constant | HashMap lookup, array access |
| O(log n) | Logarithmic | Binary search, balanced BST |
| O(n) | Linear | Single pass, BFS, DFS |
| O(n log n) | Linearithmic | Merge sort, quick sort avg, Dijkstra |
| O(n^2) | Quadratic | Nested loops, bubble sort |
| O(2^n) | Exponential | Subsets, brute force |
| O(n!) | Factorial | Permutations brute force |

## Algorithm Patterns Cheatsheet

### DP Patterns
| Pattern | Key Insight | Example |
|---------|------------|---------|
| Fibonacci | dp[i] = dp[i-1] + dp[i-2] | LC 70, 198, 746 |
| Knapsack | dp[i][w] = max(dp[i-1][w], val[i] + dp[i-1][w-wt[i]]) | LC 416, 494, 518 |
| LIS | dp[i] = 1 + max(dp[j]) for j < i | LC 300, 354 |
| LCS | dp[i][j] = 1 + dp[i-1][j-1] if match else max of prev | LC 1143, 72, 583 |
| Interval | dp[i][j] = min over k of dp[i][k] + dp[k+1][j] + cost | LC 312, 375, 516 |
| String DP | dp[i][j] = depending on char match/mismatch | LC 10, 44, 72, 97 |

### Graph Algorithms
| Algorithm | Use | Complexity |
|-----------|-----|------------|
| BFS | Shortest path (unweighted), level order | O(V+E) |
| DFS | Connectivity, cycle detection, topological | O(V+E) |
| Dijkstra | Shortest path (non-negative weights) | O((V+E) log V) |
| Bellman-Ford | Shortest path (negative weights allowed) | O(V*E) |
| Floyd-Warshall | All pairs shortest path | O(V^3) |
| Prim/Kruskal | MST | O(E log V) |
| Kahn's / DFS | Topological sort | O(V+E) |
| Kosaraju/Tarjan | Strongly connected components | O(V+E) |

### Binary Search Variants
| Variant | Use Case |
|---------|----------|
| Standard | Sorted array (LC 704) |
| Rotated | Find min in rotated array (LC 153) |
| Search in rotated | Modified binary search (LC 33) |
| First/Last position | Duplicate handling (LC 34) |
| Search on answer | Feasibility function (LC 875, 1011, 1283) |
| Bitonic peak | Find peak element (LC 162) |

## Must-Know Complexity Analysis Phrases
- "The lower bound is Omega(n) because we must examine each element"
- "Sorting costs O(n log n), which dominates the total complexity"
- "DP reduces from O(2^n) to O(n^2) by caching subproblem results"
- "The bottleneck is the nested loop — can we eliminate one pass?"
- "Amortized O(1) insert — resizing happens rarely"

## Quick Ref — Core Algorithm LC Problems
| LC # | Problem | Paradigm | Complexity |
|------|---------|----------|------------|
| 53 | Maximum Subarray | Kadane's (DP) | O(n) / O(1) |
| 70 | Climbing Stairs | DP (Fibonacci) | O(n) / O(1) |
| 200 | Number of Islands | DFS/BFS/UF | O(M*N) |
| 206 | Reverse Linked List | Iterative | O(n) / O(1) |
| 322 | Coin Change | DP (Unbounded Knapsack) | O(n*amt) / O(amt) |
| 33 | Search Rotated Array | Binary Search | O(log n) / O(1) |
| 55 | Jump Game | Greedy | O(n) / O(1) |
| 46 | Permutations | Backtracking | O(n*n!) |
| 56 | Merge Intervals | Sort + Merge | O(n log n) / O(n) |
| 1143 | Longest Common Subseq | DP (2D) | O(m*n) / O(m*n) |
| 743 | Network Delay Time | Dijkstra | O(E log V) |
| 208 | Trie | Trie | O(k) per op |
| 215 | Kth Largest | QuickSelect/Heap | O(n) avg |
| 5 | Longest Palindrome | Expand around center | O(n^2) / O(1) |

## Edge Cases Checklist
- [ ] Empty input (array, string, tree, graph)
- [ ] Single element
- [ ] Two elements
- [ ] Already sorted / reversed
- [ ] All same values
- [ ] Negative numbers / zeros
- [ ] Integer overflow (MAX_VALUE, MIN_VALUE)
- [ ] Duplicates
- [ ] Cycle in graph
- [ ] Disconnected graph

## STAR for Behavioral
- **S**ituation: Context (project scope, team size, timeline)
- **T**ask: Your specific responsibility
- **A**ction: What you did (emphasize algorithmic thinking)
- **R**esult: Quantified outcome (latency reduction, throughput increase)

## Company Focus
| Company | Top Algorithms | Interview Style |
|---------|---------------|-----------------|
| Google | DP, Graphs, Binary Search on Answer | Deep optimization, multiple follow-ups |
| Meta | Arrays, Strings, Recursion, DP | Speed: 2 problems in 45 min |
| Amazon | Greedy, Scheduling, Graph | Real-world framing, scale discussion |
| Microsoft | Sorting, Binary Search, Recursion | Clean implementation, fundamentals |
| Apple | Bit Manipulation, In-place, Recursion | Memory-constrained, compile + run |

## Time Management (45 min)
| 0-5 min | 5-10 min | 10-30 min | 30-40 min | 40-45 min |
|---------|----------|-----------|-----------|-----------|
| Understand + paradigm ID | Brute force → optimize + buy-in | Code | Test + edges | Analyze + follow-ups |

## Resources
- **NeetCode.io**: Pattern-based algorithm videos
- **CP-Algorithms**: Comprehensive algorithm reference
- **Visualgo.net**: Algorithm visualization
- **Pramp / interviewing.io**: Free mock interviews
- **Blind 75 + NeetCode 150**: Curated problem paths
