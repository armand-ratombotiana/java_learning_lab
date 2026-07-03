$labDir = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\algorithms\12-complexity-analysis"

function wf($name, $content) {
    Set-Content -Path (Join-Path $labDir $name) -Value $content
}

wf "README.md" @"
# Complexity Analysis — Overview

Covers Big O notation, amortized analysis, Master Theorem, and complexity classes.

## Learning Objectives
- Master asymptotic notation (Big O, Ω, Θ, o, ω)
- Apply Master Theorem to recurrence relations
- Understand amortized analysis (aggregate, accounting, potential)
- Classify problems by complexity (P, NP, NP-Complete)

## Prerequisites
- Basic algorithm analysis concepts
- Recurrence relations
- Formal logic (for complexity classes)

## Estimated Time
- **Total**: 3–4 hours
"@

wf "THEORY.md" @"
# Complexity Analysis — Theoretical Foundation

## Asymptotic Notation

### Big O (Upper Bound)
f(n) = O(g(n)) if ∃ c, n₀ > 0: 0 ≤ f(n) ≤ c·g(n) for all n ≥ n₀

### Big Ω (Lower Bound)
f(n) = Ω(g(n)) if ∃ c, n₀ > 0: 0 ≤ c·g(n) ≤ f(n) for all n ≥ n₀

### Big Θ (Tight Bound)
f(n) = Θ(g(n)) if f(n) = O(g(n)) AND f(n) = Ω(g(n))

### Little o and ω
- f(n) = o(g(n)): f grows strictly slower than g
- f(n) = ω(g(n)): f grows strictly faster than g

## Common Complexity Classes
| Notation | Name | Example |
|----------|------|---------|
| O(1) | Constant | Array access |
| O(log n) | Logarithmic | Binary search |
| O(n) | Linear | Linear search |
| O(n log n) | Linearithmic | Merge sort |
| O(n²) | Quadratic | Bubble sort |
| O(n³) | Cubic | Floyd-Warshall |
| O(2ⁿ) | Exponential | Subset sum |
| O(n!) | Factorial | TSP brute force |
"@

wf "WHY_IT_EXISTS.md" @"
# Why Complexity Analysis Exists

Complexity analysis provides a mathematical framework for comparing algorithm efficiency independent of hardware and implementation details. It enables engineers to predict how algorithms scale and make informed design decisions.
"@

wf "WHY_IT_MATTERS.md" @"
# Why Complexity Analysis Matters

- Predict Scalability: Know how your system behaves at 10x data
- Algorithm Selection: Compare algorithms independently of hardware
- Performance Tuning: Identify bottlenecks using complexity
- Capacity Planning: Estimate resource requirements
- Interview Essential: Every technical interview tests complexity
- Proving Optimality: Show an algorithm cannot be improved asymptotically
"@

wf "HISTORY.md" @"
# History of Complexity Analysis

- 1965: Hartmanis and Stearns founded computational complexity theory
- 1971: Cook's theorem (SAT is NP-Complete)
- 1972: Karp's 21 NP-Complete problems
- 1976: Knuth popularized Big O notation
- 1985: Amortized analysis formalized by Tarjan
- 1990s: Average-case complexity developed
- 2000s: Parameterized complexity, approximation complexity
"@

wf "MENTAL_MODELS.md" @"
# Mental Models

## Big O — "Growth Rate Ceiling"
Big O is like a speed limit: the algorithm's actual time won't exceed this rate (up to constants).

## Amortized Analysis — "Bank Account"
Some operations are expensive but rare. By averaging over many operations, the amortized cost is small. Like paying a little extra into savings each month to cover an expensive annual purchase.

## P vs NP — "Check vs Find"
P problems are easy to solve. NP problems are easy to verify (check a solution). The big question: is checking as easy as finding?
"@

wf "HOW_IT_WORKS.md" @"
# How Complexity Analysis Works

## Analyzing Loop Complexity
```java
// O(n)
for (int i = 0; i < n; i++) { sum += arr[i]; }

// O(n²)
for (int i = 0; i < n; i++)
    for (int j = 0; j < n; j++)
        sum += arr[i][j];

// O(n log n)
for (int i = 0; i < n; i++)
    for (int j = i; j > 0; j /= 2)
        sum += j;
```

## Recurrence Analysis
Merge Sort: T(n) = 2T(n/2) + O(n)
- Level 0: 1 node, O(n) work
- Level 1: 2 nodes, O(n/2) each → O(n) total
- Level 2: 4 nodes, O(n/4) each → O(n) total
- Total: log₂(n) levels × O(n) = O(n log n)
"@

wf "INTERNALS.md" @"
# Complexity Analysis — Internal Mechanics

## Master Theorem Details
T(n) = aT(n/b) + f(n) where a ≥ 1, b > 1

### Case 1: f(n) = O(n^c), c < log_b(a)
T(n) = Θ(n^{log_b(a)})

### Case 2: f(n) = Θ(n^c log^k n), c = log_b(a)
T(n) = Θ(n^c log^{k+1} n)

### Case 3: f(n) = Ω(n^c), c > log_b(a)
T(n) = Θ(f(n)) if af(n/b) ≤ kf(n) for some k < 1

## Amortized Analysis Methods

### Aggregate Method
Sum cost of all operations, divide by number of operations.

### Accounting Method
Assign different costs to operations. Some pay extra (credit), credit used for expensive operations.

### Potential Method
Define potential function Φ. Amortized cost = actual cost + ΔΦ.
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for Complexity

## Formal Definitions
```
O(g(n)) = {f(n): ∃c,n₀>0 ∀n≥n₀ 0≤f(n)≤c·g(n)}
Ω(g(n)) = {f(n): ∃c,n₀>0 ∀n≥n₀ 0≤c·g(n)≤f(n)}
Θ(g(n)) = O(g(n)) ∩ Ω(g(n))
```

## log Properties
- n log b a = a log b n
- log a (n) = log b (n) / log b (a)

## Useful Summations
- Σᵢ₌₁ⁿ 1 = n
- Σᵢ₌₁ⁿ i = n(n+1)/2 = Θ(n²)
- Σᵢ₌₁ⁿ i² = n(n+1)(2n+1)/6 = Θ(n³)
- Σᵢ₌₁ⁿ 2ⁱ = 2ⁿ⁺¹ - 1 = Θ(2ⁿ)
- Σᵢ₌₁ⁿ 1/i = ln n + γ = Θ(log n)
"@

wf "VISUAL_GUIDE.md" @"
# Visual Guide — Complexity Analysis

## Growth Rate Comparison
```
Operations
^
|                    2ⁿ (exponential)
|                  n² (quadratic)
|               n log n
|            n (linear)
|         log n
|      1 (constant)
+------------------------> n
```

## Amortized Analysis — Dynamic Array
```
Cost per insert
^
|  * (costly resize)
|  |  |  |  |  *  |  |  |  |  *
+------------------------> insert #
Most inserts cost O(1), resizing costs O(n)
Amortized cost: O(1) per insert
```
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive — Complexity Analysis

## Bad Complexity (O(n²) when O(n) is possible)
```java
// Bad: O(n²) — repeated string concatenation
String result = "";
for (String s : strings) result += s;  // String is immutable!

// Good: O(n)
StringBuilder sb = new StringBuilder();
for (String s : strings) sb.append(s);
```

## Amortized O(1) — Dynamic Array
```java
public class DynamicArray {
    private int[] arr = new int[1];
    private int size = 0;

    public void add(int value) {
        if (size == arr.length) {
            int[] newArr = new int[arr.length * 2]; // O(n) — but rare
            System.arraycopy(arr, 0, newArr, 0, arr.length);
            arr = newArr;
        }
        arr[size++] = value; // O(1) — most of the time
    }
}
```
Amortized analysis: n inserts cost O(n), so O(1) per insert amortized.
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step — Complexity Analysis

## Analyzing an Algorithm
1. Identify basic operations (comparisons, swaps, array accesses)
2. Determine how many times each operation executes
3. Express as function of input size n
4. Find the dominant term (highest growth rate)
5. Drop constants and lower-order terms

## Recursion Analysis
1. Write recurrence relation: T(n) = aT(n/b) + f(n)
2. Draw recursion tree (optional)
3. Apply Master Theorem if applicable
4. Verify with substitution method

## Common Pitfalls
- Ignoring constants when they matter (n=10 vs n=10⁶)
- Forgetting about space complexity
- Confusing average-case with worst-case
- Not accounting for input distribution
"@

wf "COMMON_MISTAKES.md" @"
# Common Mistakes

- Confusing Big O with Big Θ — O is upper bound, Θ is tight
- Dropping constants when constants dominate (e.g., n=10)
- Analyzing wrong operation — focus on dominant operation
- Hidden costs — String concatenation, autoboxing, array copying
- Best-case != average-case — always mention which
- Ignoring space complexity — memory is often the bottleneck
- Assuming all O(n log n) sorts are equal — constant factors matter
"@

wf "DEBUGGING.md" @"
# Debugging Complexity

## Benchmark to Verify Complexity
```java
long start = System.nanoTime();
// run algorithm with n=100, 1000, 10000
long end = System.nanoTime();
System.out.printf("n=%d: %d ns%n", n, end - start);
```

## Use Big O to Find Bugs
If an algorithm should be O(n) but the benchmark shows O(n²), there's a hidden loop or inefficient operation.
"@

wf "REFACTORING.md" @"
# Refactoring for Complexity

## Reduce Quadratic to Linear
```java
// O(n²): nested loop search
for (int a : listA)
    for (int b : listB)
        if (a == b) result.add(a);

// O(n): HashSet lookup
Set<Integer> setB = new HashSet<>(listB);
for (int a : listA)
    if (setB.contains(a)) result.add(a);
```

## Reduce O(n) to O(log n)
Use binary search, balanced BST, or binary heap.
"@

wf "PERFORMANCE.md" @"
# Performance — Complexity Classes

| Class | n=10 | n=100 | n=1,000 | n=10⁶ |
|-------|------|-------|---------|-------|
| O(1) | 1 | 1 | 1 | 1 |
| O(log n) | 3 | 7 | 10 | 20 |
| O(n) | 10 | 100 | 1,000 | 10⁶ |
| O(n log n) | 33 | 700 | 10,000 | 20×10⁶ |
| O(n²) | 100 | 10,000 | 10⁶ | 10¹² |
| O(2ⁿ) | 1,024 | 10³⁰ | — | — |

## Practical Guidelines
- n < 10: O(n!) is fine
- n < 100: O(n³) is fine
- n < 10,000: O(n²) is acceptable
- n < 10⁶: O(n log n) is expected
- n > 10⁶: O(n) or better is needed
"@

wf "SECURITY.md" @"
# Security — Complexity Analysis

- Algorithmic Complexity Attack: Attacker crafts input triggering worst-case complexity
- Hash DoS: Known in Java HashMap (fixed with random hash seed)
- ReDoS: Regular expression catastrophic backtracking
- ZIP bomb: Decompression leads to exponential memory usage
- Mitigation: Use deterministic performance, input limits, randomized algorithms
"@

wf "ARCHITECTURE.md" @"
# Architecture — Complexity Analysis

## P vs NP Overview
- **P**: Problems solvable in polynomial time (sorting, searching, shortest path)
- **NP**: Solutions verifiable in polynomial time (SAT, TSP, knapsack)
- **NP-Complete**: Hardest problems in NP (SAT, 3-SAT, Hamiltonian cycle)
- **NP-Hard**: At least as hard as NP-Complete (may not be in NP)

## Practical Implications
- NP-Complete → use heuristics, approximations, or B&B
- P problems → prefer optimal solutions
- Know which complexity class your problem falls in
"@

wf "EXERCISES.md" @"
# Exercises — Complexity Analysis

## Beginner
1. Find Big O of: n³ + 100n² + 2ⁿ
2. Analyze nested loop: for(i) for(j<i) ...
3. Binary search recurrence → solve with Master Theorem
4. Classify: sqrt(n), n², n!, 2ⁿ, n log n, n

## Intermediate
5. Analyze amortized cost of HashMap resize
6. Prove Merge Sort is Ω(n log n)
7. Find complexity of recursive Fibonacci (naive vs memoized)
8. Analyze disjoint-set forest (Union-Find)

## Advanced
9. Prove that any comparison-based sort is Ω(n log n)
10. Show that SAT is NP-Complete (Cook's theorem outline)
11. Analyze the potential method for splay trees
12. Complexity of Strassen's matrix multiplication
"@

wf "QUIZ.md" @"
# Quiz — Complexity Analysis

1. What does f(n) = O(g(n)) mean formally?
2. What is the difference between O and o?
3. When should you use amortized analysis?
4. What are the three cases of the Master Theorem?
5. What does P vs NP ask?
6. Why is worst-case often preferred over average-case?
7. What is the complexity of binary search?
"@

wf "FLASHCARDS.md" @"
# Flashcards

- Q: Big O definition? → A: f(n) ≤ c·g(n) for n ≥ n₀
- Q: Merge Sort complexity? → A: O(n log n)
- Q: Master Theorem? → A: T(n) = aT(n/b) + f(n)
- Q: P vs NP? → A: P=solvable poly, NP=verifiable poly
- Q: Amortized analysis? → A: Average cost over sequence
- Q: NP-Complete? → A: Hardest problems in NP
"@

wf "INTERVIEW.md" @"
# Interview Questions

1. "What's the Big O of this algorithm?" — Basic analysis
2. "Design an O(1) algorithm for X." — Constant time design
3. "Why is HashMap get() O(1) amortized?" — Hash + resizing
4. "Compare O(log n) vs O(n) search." — Binary vs linear
5. "What is P vs NP and why does it matter?" — Complexity theory
6. "Design a O(n) algorithm for X dominance problem." — Linear time
7. "Prove that comparison sorting is Ω(n log n)." — Decision tree
"@

wf "REFLECTION.md" @"
# Reflection

- How does Big O notation guide algorithm design decisions?
- Why is amortized analysis more useful than worst-case for some data structures?
- What does the P vs NP question mean for real-world computing?
- How do theoretical complexity and practical performance differ?
- When should you ignore asymptotic complexity and optimize for constants?
"@

wf "REFERENCES.md" @"
# References

- CLRS, Chapters 3 (Growth of Functions), 4 (Recurrences), 17 (Amortized Analysis), 34 (NP-Completeness)
- Sedgewick, R. "Algorithms", Chapter 2 (Analysis)
- Knuth, D. "Big Omicron and Big Omega and Big Theta" (1976)
- Cook, S. "The Complexity of Theorem-Proving Procedures" (1971)
- Garey & Johnson "Computers and Intractability" (NP-Completeness)
"@

Write-Host "12-complexity-analysis: All 24 files created"
