$labDir = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\algorithms\05-dynamic-programming"

function wf($name, $content) {
    Set-Content -Path (Join-Path $labDir $name) -Value $content
}

wf "README.md" @"
# Dynamic Programming — Overview

Covers memoization, tabulation, and classic DP problems.

## Learning Objectives
- Recognize optimal substructure and overlapping subproblems
- Implement top-down (memoization) and bottom-up (tabulation) DP
- Analyze time and space complexity of DP solutions
- Apply DP to classic problems (knapsack, LCS, edit distance)

## Prerequisites
- Recursion and stack mechanics
- Big O complexity analysis
- Basic array/matrix manipulation

## Estimated Time
- **Total**: 5–6 hours
"@

wf "THEORY.md" @"
# Dynamic Programming — Theoretical Foundation

## Key Properties
### Optimal Substructure
Optimal solution contains optimal solutions to subproblems.

### Overlapping Subproblems
Same subproblems solved multiple times if naive recursion is used.

## Two Approaches

### Top-Down (Memoization)
- Recursive with caching
- Start from original problem, recurse to base cases
- Store computed results in array/HashMap
- Time: subproblems × time per subproblem
- Space: O(subproblems) + recursion stack

### Bottom-Up (Tabulation)
- Iterative
- Solve subproblems in order of increasing size
- Build table from base cases to target
- Often faster (no recursion overhead)
- Can be space-optimized (rolling arrays)
"@

wf "WHY_IT_EXISTS.md" @"
# Why Dynamic Programming Exists

Richard Bellman coined "dynamic programming" in the 1940s for multistage decision processes. DP converts exponential-time problems to polynomial time by avoiding redundant computation through structured subproblem solving.
"@

wf "WHY_IT_MATTERS.md" @"
# Why Dynamic Programming Matters

- Exponential → Polynomial: Many problems become tractable
- Ubiquitous: Bioinformatics (sequence alignment), AI (RL), economics
- Interview critical: ~30% of medium/hard coding problems use DP
- Foundation: Essential for advanced algorithms
- Optimization: Space-efficient DP enables large-scale solutions
"@

wf "HISTORY.md" @"
# History of DP

- 1940s: Bellman developed DP at RAND Corporation
- 1953: Principle of Optimality formalized
- 1957: Bellman published "Dynamic Programming"
- 1970s: DP for sequence alignment (Needleman-Wunsch)
- 1970s: CYK algorithm for parsing
- 1990s-2000s: DP becomes standard interview topic
- 2010s+: DP in reinforcement learning
"@

wf "MENTAL_MODELS.md" @"
# Mental Models

## The Staircase
To reach step n, come from n-1 or n-2. Ways to reach n = ways(n-1) + ways(n-2).

## The Fibonacci Tree
Naive recursion = tree with repeated nodes. Memoization prunes the tree.

## The Table Builder
Fill a spreadsheet where each cell depends on previously computed cells. Start from top-left (base cases) toward bottom-right (target).

## Optimal Substructure
"If I know the best subproblem solutions, I can combine them for the larger problem."
"@

wf "HOW_IT_WORKS.md" @"
# How Dynamic Programming Works

## Fibonacci with Memoization
```java
long[] memo = new long[n + 1];
Arrays.fill(memo, -1);
memo[0] = 0; memo[1] = 1;

long fib(int n) {
    if (memo[n] != -1) return memo[n];
    memo[n] = fib(n - 1) + fib(n - 2);
    return memo[n];
}
```

## Fibonacci with Tabulation
```java
long fib(int n) {
    if (n <= 1) return n;
    long[] dp = new long[n + 1];
    dp[0] = 0; dp[1] = 1;
    for (int i = 2; i <= n; i++)
        dp[i] = dp[i - 1] + dp[i - 2];
    return dp[n];
}
```

## Space-Optimized
```java
long fib(int n) {
    if (n <= 1) return n;
    long prev2 = 0, prev1 = 1;
    for (int i = 2; i <= n; i++) {
        long curr = prev1 + prev2;
        prev2 = prev1; prev1 = curr;
    }
    return prev1;
}
```
"@

wf "INTERNALS.md" @"
# DP — Internal Mechanics

## 0/1 Knapsack
```java
int knapsack(int[] weights, int[] values, int capacity) {
    int n = weights.length;
    int[][] dp = new int[n + 1][capacity + 1];
    for (int i = 1; i <= n; i++) {
        for (int w = 1; w <= capacity; w++) {
            if (weights[i - 1] <= w)
                dp[i][w] = Math.max(dp[i - 1][w],
                    values[i - 1] + dp[i - 1][w - weights[i - 1]]);
            else
                dp[i][w] = dp[i - 1][w];
        }
    }
    return dp[n][capacity];
}
```

## Longest Common Subsequence
```java
int lcs(String s1, String s2) {
    int m = s1.length(), n = s2.length();
    int[][] dp = new int[m + 1][n + 1];
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (s1.charAt(i - 1) == s2.charAt(j - 1))
                dp[i][j] = dp[i - 1][j - 1] + 1;
            else
                dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
        }
    }
    return dp[m][n];
}
```
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for DP

## Bellman's Principle of Optimality
"An optimal policy has the property that whatever the initial state and decision, remaining decisions must constitute an optimal policy."

## Typical Recurrences
- Fibonacci: dp[i] = dp[i-1] + dp[i-2]
- Knapsack: dp[i][w] = max(dp[i-1][w], value[i] + dp[i-1][w-weight[i]])
- LCS: dp[i][j] = dp[i-1][j-1] + 1 if match, else max(dp[i-1][j], dp[i][j-1])
- Edit Distance: dp[i][j] = min(dp[i-1][j]+1, dp[i][j-1]+1, dp[i-1][j-1]+cost)

## Complexity
- Subproblems = table size
- Time = subproblems × transition cost
- Space = table size (can be optimized)
"@

wf "VISUAL_GUIDE.md" @"
# Visual Guide — DP

## LCS Table for "ABCBDAB" × "BDCAB"
```
     Ø  B  D  C  A  B
  Ø  0  0  0  0  0  0
  A  0  0  0  0  1  1
  B  0  1  1  1  1  2
  C  0  1  1  2  2  2
  B  0  1  1  2  2  3
  D  0  1  2  2  2  3
  A  0  1  2  2  3  3
  B  0  1  2  2  3  4
```
LCS length = 4 (BCAB or BDAB)

## Knapsack DP Table
Items: (w=2,v=3), (w=3,v=4), (w=4,v=5), Capacity=5
```
     w=0 w=1 w=2 w=3 w=4 w=5
i=0   0   0   0   0   0   0
i=1   0   0   3   3   3   3
i=2   0   0   3   4   4   7
i=3   0   0   3   4   5   7
```
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive — DP

## Edit Distance (Levenshtein)
```java
public int minDistance(String word1, String word2) {
    int m = word1.length(), n = word2.length();
    int[][] dp = new int[m + 1][n + 1];
    for (int i = 0; i <= m; i++) dp[i][0] = i;
    for (int j = 0; j <= n; j++) dp[0][j] = j;
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (word1.charAt(i - 1) == word2.charAt(j - 1))
                dp[i][j] = dp[i - 1][j - 1];
            else
                dp[i][j] = 1 + Math.min(dp[i - 1][j],
                    Math.min(dp[i][j - 1], dp[i - 1][j - 1]));
        }
    }
    return dp[m][n];
}
```

## Coin Change (Minimum Coins)
```java
public int coinChange(int[] coins, int amount) {
    int[] dp = new int[amount + 1];
    Arrays.fill(dp, amount + 1);
    dp[0] = 0;
    for (int i = 1; i <= amount; i++) {
        for (int coin : coins) {
            if (coin <= i)
                dp[i] = Math.min(dp[i], dp[i - coin] + 1);
        }
    }
    return dp[amount] > amount ? -1 : dp[amount];
}
```
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step — DP

## DP Problem-Solving Framework
1. Define state: What does dp[i][j] represent?
2. Find recurrence: How does dp[i][j] relate to smaller subproblems?
3. Identify base cases: Initial values?
4. Determine iteration order: Bottom-up or top-down?
5. Optimize space: Can we reduce dimensions?

## Recognizing DP Problems
- "Find minimum/maximum of something"
- "Count the number of ways to..."
- "Find longest/shortest..."
- Choices with overlapping subproblems
- Brute-force is exponential

## Common DP Patterns
- Linear DP: Fibonacci, house robber
- 2D DP: LCS, edit distance, knapsack
- Interval DP: Matrix chain multiplication
- Tree DP: Diameter of tree
- State Machine DP: Stock with cooldown
"@

wf "COMMON_MISTAKES.md" @"
# Common Mistakes — DP

- Not identifying overlapping subproblems
- Incorrect base cases
- Off-by-one in indices
- Wrong iteration order
- Missing state dimensions
- Space optimization errors (overwriting needed values)
- Integer overflow — use long when needed
- Not resetting arrays for multiple test cases
"@

wf "DEBUGGING.md" @"
# Debugging — DP

## Print DP Table
```java
private static void printTable(int[][] dp, String[] rowLabels, String[] colLabels) {
    System.out.print("\t");
    for (String col : colLabels) System.out.print(col + "\t");
    System.out.println();
    for (int i = 0; i < dp.length; i++) {
        System.out.print(rowLabels[i] + "\t");
        for (int j = 0; j < dp[i].length; j++)
            System.out.print(dp[i][j] + "\t");
        System.out.println();
    }
}
```

## Small Test Cases
```java
assertEquals(3, editDistance("cat", "dog"));
assertEquals(0, editDistance("", ""));
assertEquals(1, editDistance("a", ""));
```
"@

wf "REFACTORING.md" @"
# Refactoring — DP

## Space Optimization
```java
// 2D → 1D for knapsack
int[] dp = new int[capacity + 1];
for (int i = 0; i < n; i++)
    for (int w = capacity; w >= weights[i]; w--)
        dp[w] = Math.max(dp[w], dp[w - weights[i]] + values[i]);
```

## Memoization → Tabulation
- Eliminates recursion overhead
- No stack overflow risk
- Easier to space-optimize
"@

wf "PERFORMANCE.md" @"
# Performance — DP

| Problem | Time | Space | Optimized |
|---------|------|-------|-----------|
| Fibonacci | O(n) | O(n) | O(1) |
| 0/1 Knapsack | O(nW) | O(nW) | O(W) |
| LCS | O(mn) | O(mn) | O(min(m,n)) |
| Edit Distance | O(mn) | O(mn) | O(min(m,n)) |
| Coin Change | O(n×amount) | O(amount) | O(amount) |

## When DP is Appropriate
- n < 1000 for O(n²) DP
- n < 100 for O(n³) DP
- n < 20 for O(2ⁿ) bitmask DP
"@

wf "SECURITY.md" @"
# Security — DP

- Resource Exhaustion: DP tables with attacker-controlled dimensions can cause OOM
- Cache Timing: DP operations may leak information
- Input Validation: Validate dimensions to prevent excessive allocation
- Integer Overflow: Use long/BigInteger for large problems
"@

wf "ARCHITECTURE.md" @"
# Architecture — DP

## DP in Java Libraries
- StringUtils: LCS and edit distance (Apache Commons)
- BioJava: Sequence alignment
- OptaPlanner: Constraint satisfaction

## Modern Applications
- Spell Checkers: Edit distance
- DNA Sequencing: Needleman-Wunsch
- Route Planning: Shortest path
- NLP: Viterbi algorithm
- Game AI: Value iteration
"@

wf "EXERCISES.md" @"
# Exercises — DP

## Beginner
1. Fibonacci with memoization and tabulation
2. Climbing Stairs
3. House Robber
4. Maximum subarray (Kadane's)

## Intermediate
5. 0/1 Knapsack — reconstruct items
6. LCS — find subsequence, not just length
7. Coin Change — min coins and number of ways
8. Edit Distance — reconstruct operations

## Advanced
9. Matrix Chain Multiplication
10. Longest Palindromic Subsequence
11. Word Break
12. Wildcard Pattern Matching
"@

wf "QUIZ.md" @"
# Quiz — DP

1. Two properties required for DP?
2. Memoization vs tabulation?
3. Why is naive Fibonacci exponential?
4. What is optimal substructure?
5. How to reduce DP space from 2D to 1D?
6. Time complexity of 0/1 knapsack DP?
"@

wf "FLASHCARDS.md" @"
# Flashcards

- Q: Two DP requirements? → A: Optimal substructure, overlapping subproblems
- Q: Fibonacci DP time? → A: O(n)
- Q: Knapsack DP time? → A: O(nW)
- Q: Space optimization pattern? → A: Rolling arrays
- Q: LCS DP time? → A: O(mn)
- Q: Bellman's principle? → A: Optimal solution contains optimal sub-solutions
"@

wf "INTERVIEW.md" @"
# Interview Questions

1. "Climbing Stairs" — Entry-level
2. "House Robber" — Linear DP
3. "Coin Change" — Unbounded knapsack
4. "Longest Common Subsequence" — 2D DP
5. "Edit Distance" — Classic
6. "0/1 Knapsack" — Essential
7. "Longest Increasing Subsequence" — O(n log n)
"@

wf "REFLECTION.md" @"
# Reflection

- How does recognizing overlapping subproblems change your approach?
- Why is state definition the most critical step?
- How does DP relate to recursion and induction?
- When memoization vs tabulation?
- How does space optimization affect readability?
"@

wf "REFERENCES.md" @"
# References

- Bellman, R. "Dynamic Programming" (1957)
- CLRS, Chapter 15
- Kleinberg & Tardos "Algorithm Design"
- Dasgupta et al. "Algorithms"
- LeetCode DP problem list
"@

Write-Host "05-dynamic-programming: All 24 files created"
