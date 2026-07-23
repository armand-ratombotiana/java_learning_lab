# LeetCode Solutions — How to Practice Algorithms Effectively

## How to Use LeetCode Effectively

### The Algorithm-First Approach

Unlike data structures practice which focuses on knowing the tools, algorithm practice focuses on recognizing the paradigm.

**Step 1: Identify the Paradigm**
When you see a problem, ask:
- Is there an optimal substructure? → DP
- Does a local decision lead to global optimum? → Greedy
- Can I split the input in half? → Divide & Conquer
- Do I need to explore all configurations? → Backtracking
- Is it a traversal problem? → Graph algorithm
- Is it about string matching? → String algorithm

**Step 2: Start with the Brute Force**
Before optimizing, articulate the brute force solution. This demonstrates that you understand the problem and gives you a correctness baseline.

**Step 3: Optimize Systematically**
- Time optimization: Can I use a better data structure? Can I reduce repeated work?
- Space optimization: Can I compute values on the fly? Can I reuse memory?
- Both: Is there a fundamentally different approach?

**Step 4: Verify with Edge Cases**
After implementing, test with:
- Smallest possible input
- Large input (to visualize performance)
- Duplicates, negatives, zeros
- Boundary values (MAX_INT, MIN_INT)

### Problem Selection

| Your Goal | Problems to Solve |
|-----------|------------------|
| General FAANG prep | Blind 75 + NeetCode 150 |
| Google-specific | Focus on DP, Graphs, Binary Search on Answer |
| Meta-specific | Focus on Arrays, Strings, DP, Greedy |
| Amazon-specific | Focus on Greedy, Graphs, Design DS |
| Microsoft-specific | Focus on Sorting, Recursion, Searching |
| Apple-specific | Focus on Recursion, Bit Manipulation, Math |
| Oracle-specific | Focus on Complexity Analysis, Sorting |

---

## Spaced Repetition System for Problem Review

### The Leitner System for Algorithms

Create 5 boxes for problem review:

| Box | Review Frequency | What Goes Here |
|-----|-----------------|---------------|
| 1 | Every day | New problems, problems you struggled with |
| 2 | Every 2 days | Problems you solved with hints |
| 3 | Every 4 days | Problems you solved independently |
| 4 | Weekly | Problems you solved easily |
| 5 | Monthly | Mastered problems |

### Review Template

```
Problem: LC 322 Coin Change [Medium]
Paradigm: Dynamic Programming (Unbounded Knapsack)
Date Solved: 2024-01-15
Review 1: 2024-01-16 (Solved in 12 min, slightly slower)
Review 2: 2024-01-18 (Solved in 8 min, optimal)
Review 3: 2024-01-22 (Solved in 5 min)
Review 4: 2024-02-05 (Solved in 3 min)
Review 5: 2024-03-05 (Solved in 2 min - MASTERED)
Key Insight: dp[i] = min(dp[i], dp[i-coin] + 1) for each coin
```

### Active Recall Process

1. **Read the problem title** — Do not look at your notes
2. **Try to recall** the paradigm, approach, key insight
3. **Attempt to code** from scratch without references
4. **Compare** with your previous solution
5. **Rate yourself**:
   - 5: Solved faster than before with optimal code
   - 4: Solved correctly but slower than ideal
   - 3: Solved with minor issues (off-by-one, etc.)
   - 2: Needed to peek at solution
   - 1: Could not solve, completely forgot

If rated 1-2, move the problem to Box 1 for daily review.

---

## How to Time-Box Practice Sessions

### 2-Hour Algorithm Study Session

| Phase | Duration | Activity |
|-------|----------|----------|
| Paradigm Review | 10 min | Review notes on one algorithm paradigm |
| Warm-up Problem | 15 min | Solve 1 easy problem using that paradigm |
| Main Problem 1 | 30 min | Medium problem, focus on optimization |
| Solution Study | 10 min | Compare with optimal solution |
| Main Problem 2 | 30 min | Another medium, aim for 30 min completion |
| Deep Analysis | 10 min | Write complexity analysis, edge cases |
| Spaced Repetition | 10 min | Review 2-3 older problems |
| Journal | 5 min | Log progress, patterns noticed |

### 45-Minute Mock Interview

| Phase | Duration | Activity |
|-------|----------|----------|
| Problem Understanding | 5 min | Read, ask clarifying questions |
| Approach Design | 5 min | Discuss brute force → optimal |
| Coding | 25 min | Write production-quality code |
| Testing | 5 min | Walk through test cases |
| Analysis | 5 min | Time/space, discuss trade-offs |

### 1-Hour Company-Specific Session

| Phase | Duration | Activity |
|-------|----------|----------|
| Company Research | 5 min | Review company tag problems |
| Problem 1 | 20 min | High-frequency problem for target company |
| Review | 5 min | Compare with optimal |
| Problem 2 | 20 min | Another high-frequency problem |
| Review | 5 min | Focus on company-specific patterns |
| Log | 5 min | Update tracking spreadsheet |

---

## How to Review Solutions After Solving

### The Algorithm Review Framework

**1. Paradigm Classification**
What paradigm did the problem use? Could it be solved with a different paradigm?

| Problem | My Paradigm | Alternative | Why Alternative Fails |
|---------|------------|-------------|---------------------|
| Coin Change | DP | Greedy | Greedy fails with denominations [1,3,4] for amount 6 |
| Course Schedule | Topological Sort | DFS | Topo sort gives actual order, DFS just detects cycle |

**2. Complexity Deep Dive**

For every solution, analyze:
- **Best case**: When does your algorithm perform best?
- **Worst case**: When does it perform worst?
- **Average case**: What is the expected performance?
- **Amortized**: Are there expensive operations that are rare?

**3. Optimization Path**
Trace the path from brute force → optimal:

```
Example: Two Sum
Brute Force: O(n^2) — Check every pair
HashMap: O(n) — Trade space for time
Could we do O(1) space? No, lower bound is O(n) in general case
Could we do O(n log n) with sorting + two-pointer? Yes, but O(n) with HashMap is better
```

**4. Variation Exploration**
For each problem, think of 3 variations:

| Variation | How Solution Changes |
|-----------|---------------------|
| Input is a stream | Need online algorithm |
| Input size 10^10 | Need sub-linear time |
| Need all solutions | Need backtracking instead |
| Negative numbers allowed | Adjust base cases |
| Duplicate values | Handle equality carefully |

**5. One-Sentence Summary**
Write a single sentence that captures the essence:

```
LC 322 Coin Change: "Unbounded knapsack where dp[i] = min coins to make amount i."
LC 207 Course Schedule: "Detect cycle in directed graph via topological sort (Kahn's or DFS)."
LC 33 Search in Rotated Array: "Modified binary search comparing mid with left to determine sorted half."
LC 5 Longest Palindrome: "Expand around centers — each character and between characters as center."
LC 200 Number of Islands: "DFS/BFS from each unvisited land cell, count connected components."
LC 300 LIS: "Maintain patience-sorting piles; binary search for insertion position."
LC 312 Burst Balloons: "Interval DP — add balloons last, dp[i][j] = max over last balloon k between i and j."
LC 72 Edit Distance: "2D DP where dp[i][j] = min operations to convert s1[0..i] to s2[0..j]."
LC 146 LRU Cache: "HashMap + Doubly Linked List — get moves to front, put evicts from back."
LC 53 Maximum Subarray: "Kadane's — track current sum, reset to 0 if negative."
```

---

## Common Mistakes in LeetCode Preparation

### Mistake 1: Not Knowing When to Use DP vs Greedy

**The Problem**: You try greedy on a DP problem and get wrong answers on certain edge cases.

**The Fix**: Learn to identify when greedy fails:
- If you need to try all possibilities → DP or backtracking
- If the greedy choice property is violated → DP
- If there are overlapping subproblems → DP
- Famous greedy pitfalls: Coin Change (wrong denomination set), Knapsack (fractional vs 0/1)

| Problem | Looks Greedy But Needs DP | Why |
|---------|--------------------------|-----|
| Coin Change | Pick largest coin first | Fails on denominations [1,3,4], amount 6 |
| Word Break | String starts with word? | Fails on "catsanddog", dict ["cat","cats","and","dog"] |
| LIS | Greedy merge? | Actually need DP to track ends |
| Weighted Job Scheduling | Pick earliest finish? | Need DP to compare options |

### Mistake 2: Neglecting to Analyze Space Complexity

**The Problem**: You optimize time to O(n) but use O(n^2) space, still failing large tests.

**The Fix**: Always state space complexity. Look for space optimization opportunities:
- Can you reduce 2D DP to 1D? (Use only previous row)
- Can you reduce 1D DP to O(1)? (Use only 2-3 variables)
- Can you use iterative instead of recursive? (Avoid call stack)
- Can you reuse input array for auxiliary storage?

**Space Reduction Patterns**:
| DP Type | Full Table | Optimized | When Possible |
|---------|-----------|-----------|---------------|
| 1D DP | O(n) | O(1) | dp[i] depends on dp[i-1], dp[i-2] only |
| 2D DP LCS | O(m*n) | O(min(m,n)) | Only need previous row |
| Knapsack | O(n*capacity) | O(capacity) | Process items one at a time |
| Interval DP | O(n^2) | O(n^2) | Usually not reducible |

### Mistake 3: Not Practicing Communication During Coding

**The Problem**: You can solve the problem in silence but underperform when explaining.

**The Fix**: Practice the "think aloud" protocol:

```
"Before coding, let me outline my approach:"
"First, I'll handle the edge case where the input is empty..."
"Now I'm initializing my data structures..."
"This loop runs in O(n) because we process each element once..."
"At this point, I need to be careful about the off-by-one..."
"Finally, I return the result."
```

### Mistake 4: Using the Wrong Graph Algorithm

**The Problem**: You use DFS where BFS is better, or Dijkstra on unweighted graph.

**The Quick Reference**:
| Graph Problem | Algorithm |
|--------------|-----------|
| Shortest path, unweighted | BFS |
| Shortest path, weighted, non-negative | Dijkstra |
| Shortest path, negative weights | Bellman-Ford |
| All pairs shortest | Floyd-Warshall |
| Detect cycle in directed | DFS with states / Kahn's |
| Detect cycle in undirected | DFS with parent / Union-Find |
| Topological order | DFS post-order / Kahn's |
| MST | Prim (dense) / Kruskal (sparse) |
| Strongly connected components | Tarjan / Kosaraju |
| Bipartite check | BFS coloring |
| Max flow | Dinic / Push-Relabel |
| Minimum cut | Max flow min cut theorem |

### Mistake 5: Forgetting About Recursion Depth

**The Problem**: Recursive solution passes examples but stack overflows on large input.

**The Fix**: Know your language's recursion limit (Java ~10,000 frames). Convert to iterative when:
- Tree depth can be large (skewed tree: O(n) depth)
- Graph DFS on large graph
- DP problems that can be tabulated

| Problem | Recursive Risk | Iterative Alternative |
|---------|---------------|----------------------|
| Tree inorder | Deep tree → stack overflow | Morris traversal O(1) space |
| Quick Sort | Worst case O(n) depth | Use randomized pivot, or hybrid sort |
| DFS on grid | Large grid → overflow | Use explicit stack (Deque) |
| Fibonacci DP | 2^n recursion → terrible | Tabulation O(n) |

### Mistake 6: Not Understanding Sorting Internals

**The Problem**: You use sort() but cannot explain how it works or its stability.

**The Fix**: Know these sorting facts:
- Java: `Arrays.sort(int[])` uses Dual-Pivot QuickSort (unstable)
- Java: `Arrays.sort(Object[])` uses TimSort (stable, O(n) on nearly sorted)
- Java: `Collections.sort()` uses TimSort
- Python: `list.sort()` uses TimSort (stable)
- C++: `std::sort()` uses Introsort (unstable)
- Merge sort is the go-to for stable O(n log n) sorting
- Quick Sort is fastest on average due to cache locality

### Mistake 7: Over-Engineering the Solution

**The Problem**: You implement a complex solution with many edge cases when a simpler solution exists.

**The Fix**: Follow the KISS principle. Before coding, ask:
- Is there a built-in function that does this?
- Can I use a simpler data structure?
- Does the problem really need this complexity?
- What is the simplest thing that could work?

### Mistake 8: Ignoring the Follow-Up

**The Problem**: You solve the problem but cannot handle the follow-up constraint change.

**The Fix**: After solving, anticipate follow-ups:
- "What if the input is a stream?" → Online algorithm
- "What if memory is O(1)?" → In-place operations
- "What if it's 10x larger?" → Parallelization
- "What if data is distributed?" → MapReduce approach
- "What if we need real-time?" → Latency optimization

### Mistake 9: Not Using the Right Data Structure for Algorithm Problems

**The Problem**: You know the algorithm but choose the wrong underlying data structure.

**Decision Guide**:
| What You Need | Use |
|--------------|-----|
| Fast lookup | HashMap |
| Ordered data | TreeMap / TreeSet |
| Min/max tracking | Heap |
| Queue operations | LinkedList / ArrayDeque |
| Stack operations | ArrayDeque |
| Graph adjacency | HashMap<V, List<V>> |
| Range queries | Segment Tree / Fenwick Tree |
| String prefix | Trie |

### Mistake 10: Rushing to Code Without Understanding

**The Problem**: You start coding immediately, realize mid-way you misunderstood, and have to restart.

**The Fix**: Follow this process before writing any code:

1. **Restate**: "So I need to find the longest substring without repeating characters..."
2. **Example**: Walk through a small example manually
3. **Brute Force**: "The simplest approach would be to check every substring..."
4. **Optimize**: "But we can use a sliding window with a HashMap to achieve O(n)..."
5. **Edge Cases**: "What about empty strings, single characters, all unique, all same?"
6. **Pseudocode**: Write the key logic in plain English
7. **Only then**: Start coding

### Mistake 11: Not Practicing with a Timer

**The Problem**: You solve problems slowly and cannot finish interviews on time.

**The Fix**: Track your time per problem:

| Difficulty | Target Time | Hard Stop |
|------------|-------------|-----------|
| Easy | 10 min | 15 min |
| Medium | 25 min | 35 min |
| Hard | 35 min | 45 min |

If you hit the hard stop, study the solution and re-implement later.

### Mistake 12: Solution Hoarding Without Understanding

**The Problem**: You have solved 500 problems but cannot solve new variations.

**The Fix**: Depth over breadth:
- Solve 150 problems with deep understanding
- For each problem, solve 2-3 variations
- Practice pattern recognition, not solution memorization
- Use the One-Sentence Summary technique for every problem

---

## Recommended Algorithm Problem Progression

### Beginner Path (0-30 Days)

Focus on understanding algorithm fundamentals.

| Week | Focus | Key Problems |
|------|-------|-------------|
| 1 | Sorting + Binary Search | LC 704, 35, 34, 69, 278, 852, 912, 75 |
| 2 | Recursion + Divide & Conquer | LC 50, 21, 169, 53, 70, 78, 90 |
| 3 | Basic DP (1D) | LC 70, 198, 213, 746, 276, 343, 338 |
| 4 | Basic Graphs | LC 733, 200, 695, 133, 207, 417 |

### Intermediate Path (30-60 Days)

Focus on core algorithm paradigms.

| Week | Focus | Key Problems |
|------|-------|-------------|
| 5 | DP — 2D + Knapsack | LC 322, 416, 494, 474, 518, 1143, 72 |
| 6 | Greedy + Intervals | LC 55, 435, 452, 621, 763, 1029, 1094 |
| 7 | Backtracking | LC 39, 40, 46, 47, 51, 78, 90, 131, 211 |
| 8 | Graph — BFS + DFS | LC 127, 200, 286, 542, 743, 994, 1263 |

### Advanced Path (60-90 Days)

Focus on hard algorithms and optimization.

| Week | Focus | Key Problems |
|------|-------|-------------|
| 9 | DP — Advanced | LC 312, 72, 10, 44, 87, 97, 115, 132 |
| 10 | Graph — Shortest Path + MST | LC 743, 787, 1135, 1584, 1631, 882, 1368 |
| 11 | String Algorithms | LC 5, 10, 28, 214, 459, 647, 1044, 214, 1392 |
| 12 | Mixed Practice + Mock | Company-specific problems |

### Expert Path (90-180 Days)

Focus on contest-level problems and rare patterns.

| Week | Focus | Key Problems |
|------|-------|-------------|
| 13-16 | Hard DP + Bit Mask | LC 691, 943, 996, 1125, 1655, 1799, 1879 |
| 17-20 | Network Flow + Advanced Graphs | LC 847, 1345, 1559, 1761, 1905, 2092 |
| 21-24 | Complex String Algorithms | Suffix array, suffix automaton, Manacher advanced |
| 25+ | Contest participation + Systematic review | Codeforces, AtCoder, LeetCode contests |

---

## Final Tips

### The Night Before the Interview

- Review your one-sentence summaries for 50 core problems
- Get 8 hours of sleep
- Prepare questions to ask the interviewer about their team's algorithm challenges
- Prepare your materials (laptop, charger, water)

### During the Interview

- **0-5 min**: Understand the problem completely. Ask clarifying questions.
- **5-10 min**: Design the approach. Discuss brute force → optimal. Get buy-in.
- **10-35 min**: Code. Write clean, well-named code. Talk through it.
- **35-45 min**: Test. Walk through examples. Discuss edge cases. Analyze complexity.

### After the Interview

- Log the problems you solved for future review
- Note any patterns you struggled with
- Review the company's other common problems
- Rest and recover for the next interview

### Resources for Algorithm Mastery

- **CLRS**: Read chapters on DP, Greedy, Graph algorithms
- **Algorithm Design Manual**: Skiena's war stories for real-world algorithm design
- **Competitive Programming (Halim)**: For contest-level algorithm mastery
- **CP-Algorithms**: Free online reference for algorithm implementations
- **LeetCode Discuss**: Company-specific algorithm patterns
- **NeetCode.io**: Algorithm pattern breakdowns with video explanations
