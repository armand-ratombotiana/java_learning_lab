$labDir = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\algorithms\14-approximation-algorithms"

function wf($name, $content) {
    Set-Content -Path (Join-Path $labDir $name) -Value $content
}

wf "README.md" @"
# Approximation Algorithms — Overview

Covers NP-hard problems, approximation ratios, and classic approximation algorithms.

## Learning Objectives
- Understand why some problems need approximation
- Define and compute approximation ratios
- Implement classic approximation algorithms in Java
- Analyze trade-offs between ratio and running time

## Prerequisites
- Complexity classes (P, NP, NP-Complete)
- Greedy algorithms
- Graph algorithms basics

## Estimated Time
- **Total**: 4–5 hours
"@

wf "THEORY.md" @"
# Approximation Algorithms — Theoretical Foundation

## Why Approximate?
For NP-hard optimization problems, polynomial-time optimal solutions are unlikely. Approximation algorithms provide polynomial-time solutions with provable quality guarantees.

## Approximation Ratio
Algorithm A has ratio ρ if for all inputs:
- Minimization: A(I) ≤ ρ × OPT(I)
- Maximization: OPT(I) ≤ ρ × A(I)
where ρ ≥ 1 (minimization) or ρ ≥ 1 (maximization)

## Classification
- PTAS: Polynomial-Time Approximation Scheme (1+ε for any ε>0)
- FPTAS: Fully PTAS (polynomial in n AND 1/ε)
- APX: Constant-factor approximation exists
- Log-APX: O(log n) approximation
- Poly-APX: Polynomial approximation
"@

wf "WHY_IT_EXISTS.md" @"
# Why Approximation Algorithms Exist

Many important real-world problems are NP-hard (TSP, vertex cover, set cover). Approximation algorithms provide the best of both worlds: polynomial-time solutions with guaranteed quality bounds. They're essential when exact solutions are computationally infeasible.
"@

wf "WHY_IT_MATTERS.md" @"
# Why Approximation Algorithms Matter

- Real-World NP-Hard Problems: Logistics (TSP), resource allocation (knapsack), network design
- Guaranteed Quality: Unlike heuristics, approximation algorithms provide worst-case guarantees
- Practical: Often produce near-optimal solutions quickly
- Foundation for heuristics: Understanding approximation helps design better heuristics
- Inapproximability: Knowing what cannot be approximated well is equally important
"@

wf "HISTORY.md" @"
# History of Approximation

- 1970s: Approximation algorithms for NP-hard problems emerge
- 1974: Johnson's approximation for set cover (log n ratio)
- 1975: Christofides algorithm for TSP (1.5-approximation)
- 1980s: Connection to PCP theorem and inapproximability
- 1990s: PTAS developments, hardness of approximation results
- 2000s+: Advanced techniques (LP rounding, primal-dual, semidefinite programming)
"@

wf "MENTAL_MODELS.md" @"
# Mental Models

## The Budget Optimizer
You need the best solution but can't check all possibilities. You find a solution that's provably within X% of optimal. Good enough for government work.

## The Dimmer Switch
Unlike a binary on/off (heuristic works/doesn't), approximation gives you a dimmer: you can trade time for quality (PTAS).

## The Quality Guarantee
Like a delivery service that guarantees your package arrives within 2 days. It might not be next-day (optimal), but you know the worst-case delay.
"@

wf "HOW_IT_WORKS.md" @"
# How Approximation Algorithms Work

## Vertex Cover — 2-Approximation
```java
Set<Integer> approxVertexCover(Graph g) {
    Set<Integer> cover = new HashSet<>();
    Set<Edge> remaining = new HashSet<>(g.edges());
    while (!remaining.isEmpty()) {
        Edge e = remaining.iterator().next();
        cover.add(e.u);
        cover.add(e.v);
        remaining.removeIf(edge -> edge.u == e.u || edge.v == e.v
            || edge.u == e.v || edge.v == e.u);
    }
    return cover;
}
```
Ratio: 2 (optimal solution is at most twice the size)

## Set Cover — O(log n) Approximation
```java
Set<Integer> approxSetCover(Set<Integer> universe, List<Set<Integer>> sets) {
    Set<Integer> uncovered = new HashSet<>(universe);
    Set<Integer> cover = new HashSet<>();
    while (!uncovered.isEmpty()) {
        int best = -1, maxCovered = -1;
        for (int i = 0; i < sets.size(); i++) {
            Set<Integer> overlap = new HashSet<>(sets.get(i));
            overlap.retainAll(uncovered);
            if (overlap.size() > maxCovered) {
                maxCovered = overlap.size();
                best = i;
            }
        }
        cover.add(best);
        uncovered.removeAll(sets.get(best));
    }
    return cover;
}
```
Ratio: O(log n)
"@

wf "INTERNALS.md" @"
# Approximation Algorithms — Internal Mechanics

## TSP — Christofides Algorithm (1.5-Approximation)
```java
public int christofidesTSP(int[][] dist) {
    int n = dist.length;
    // 1. Compute MST
    int[][] mst = primMST(dist, n);
    
    // 2. Find vertices with odd degree in MST
    List<Integer> oddVertices = findOddDegreeVertices(mst, n);
    
    // 3. Compute minimum-weight perfect matching on odd vertices
    int[][] matching = minWeightPerfectMatching(dist, oddVertices);
    
    // 4. Union MST and matching → Eulerian multigraph
    List<Edge> eulerianGraph = union(mst, matching, n);
    
    // 5. Find Eulerian tour
    List<Integer> tour = eulerianTour(eulerianGraph);
    
    // 6. Shortcut to Hamiltonian cycle
    return shortcut(tour, dist);
}
```

## Knapsack — FPTAS
```java
public int fptasKnapsack(int[] values, int[] weights, int capacity, double epsilon) {
    int n = values.length;
    int maxValue = Arrays.stream(values).max().orElse(0);
    double scale = (epsilon * maxValue) / n;
    
    int[] scaledValues = Arrays.stream(values)
        .map(v -> (int)(v / scale))
        .toArray();
    
    int maxScaled = Arrays.stream(scaledValues).sum();
    long[] dp = new long[maxScaled + 1];
    Arrays.fill(dp, Long.MAX_VALUE);
    dp[0] = 0;
    
    for (int i = 0; i < n; i++)
        for (int s = maxScaled; s >= scaledValues[i]; s--)
            if (dp[s - scaledValues[i]] != Long.MAX_VALUE)
                dp[s] = Math.min(dp[s], dp[s - scaledValues[i]] + weights[i]);
    
    int best = 0;
    for (int s = 0; s <= maxScaled; s++)
        if (dp[s] <= capacity) best = s;
    
    return (int)(best * scale);
}
```
Runtime: O(n²/ε), Ratio: (1+ε)
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for Approximation

## Ratio Definitions
- Minimization: A(I) ≤ ρ · OPT(I)
- Maximization: OPT(I) ≤ ρ · A(I)
- ρ ≥ 1 (closer to 1 = better)

## Key Bounds
| Problem | Best Ratio | Hardness |
|---------|-----------|----------|
| Vertex Cover | 2 | 1.36 (unless P=NP) |
| Set Cover | O(log n) | Ω(log n) (unless P=NP) |
| Metric TSP | 1.5 (Christofides) | 1.0045 (unless P=NP) |
| Knapsack | FPTAS (1+ε) | FPTAS is optimal |
| Max-Cut | 0.878 (Goemans-Williamson) | 0.941 (UGC) |

## The PCP Theorem
Every NP problem can be probabilistically verified with constant queries. This implies hardness of approximation for many problems.
"@

wf "VISUAL_GUIDE.md" @"
# Visual Guide — Approximation

## Vertex Cover Approximation
```
Graph:           Optimal VC (size 3):
A - B - D        {B, D, E}
|   |   |
C - E - F

2-Approx VC (size 4):
Pick edge (A,B) → add {A,B}
Remove incident edges
Pick edge (D,F) → add {D,F}
Result: {A, B, D, F} (size 4 ≤ 2 × 3)
```

## TSP Christofides Tour
```
Step 1: MST               Step 2: Odd vertices
A---B---C                  A(odd)  B(even)  C(odd)
|       |                          
D---E---F                  D(even) E(odd)   F(even)

Step 3: Match odds        Step 4: Combine
A---C (match)             MST + Matching → Eulerian
E---F (match)             Shortcut → Hamiltonian
```
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive — Approximation

## Max-Cut — 0.5-Approximation (Randomized)
```java
public int maxCutRandomized(int[][] graph) {
    Random rand = new Random();
    boolean[] partition = new boolean[graph.length];
    for (int i = 0; i < graph.length; i++)
        partition[i] = rand.nextBoolean();
    
    int cutSize = 0;
    for (int i = 0; i < graph.length; i++)
        for (int j : graph[i])
            if (partition[i] != partition[j]) cutSize++;
    return cutSize / 2; // each edge counted twice
}
```
Expected ratio: 0.5 (can be derandomized by method of conditional expectations)

## Load Balancing (Makespan Minimization)
```java
public int greedyLoadBalance(int[] jobs, int machines) {
    int[] loads = new int[machines];
    Arrays.sort(jobs); // or use priority queue for better ratio
    for (int i = jobs.length - 1; i >= 0; i--) {
        int minMachine = 0;
        for (int m = 1; m < machines; m++)
            if (loads[m] < loads[minMachine]) minMachine = m;
        loads[minMachine] += jobs[i];
    }
    return Arrays.stream(loads).max().orElse(0);
}
```
Ratio: LPT (Longest Processing Time) = 4/3 - 1/(3m)
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step — Approximation

## Analyzing Approximation Ratio
1. Find lower bound on OPT (for minimization)
   - LP relaxation, MST cost, greedy bound
2. Show algorithm output ≤ ρ × lower bound
3. Since OPT ≥ lower bound, algorithm is ρ-approximation

## Designing Approximation Algorithms
1. Try greedy approach first (analyze ratio)
2. Consider LP rounding (solve LP, round to integer solution)
3. Primal-dual methods (simultaneously construct solution and bound)
4. Local search (iteratively improve, bound local optimum)
5. Randomized rounding (randomize LP solution)
"@

wf "COMMON_MISTAKES.md" @"
# Common Mistakes

- Confusing heuristic with approximation algorithm — heuristic lacks guarantee
- Analyzing ratio incorrectly — must hold for ALL inputs
- Forgetting to prove the lower bound on OPT
- TSP without triangle inequality — no constant ratio exists
- Applying greedy set cover without analyzing log n ratio
- Not handling edge cases (empty graph, single element)
- Confusing PTAS with FPTAS — FPTAS is polynomial in 1/ε
"@

wf "DEBUGGING.md" @"
# Debugging — Approximation

## Verify Ratio Empirically
```java
// Compare with optimal for small instances (use brute force)
for (int trial = 0; trial < 1000; trial++) {
    Graph g = randomGraph(8); // small enough for brute force
    int approx = approxVertexCover(g);
    int optimal = optimalVertexCover(g); // brute force
    double ratio = (double) approx / optimal;
    assert ratio <= 2.0 : "Ratio violated: " + ratio;
    System.out.printf("Trial %d: approx=%d, opt=%d, ratio=%.2f%n", trial, approx, optimal, ratio);
}
```

## Track Worst-Case Ratio
```java
double maxRatio = 0;
// in loop: maxRatio = Math.max(maxRatio, ratio);
System.out.println("Worst-case ratio: " + maxRatio);
```
"@

wf "REFACTORING.md" @"
# Refactoring — Approximation

## Algorithm Selection Strategy
```java
interface ApproximationAlgorithm<T, R> {
    R solve(T problem);
    double getRatio();
    String getName();
}
```

## From Exact to Approximate
```java
if (n < THRESHOLD || isEasy(n)) {
    return solveExactly(problem);
} else {
    return solveApproximately(problem, epsilon);
}
```
"@

wf "PERFORMANCE.md" @"
# Performance — Approximation

| Problem | Algorithm | Ratio | Time |
|---------|-----------|-------|------|
| Vertex Cover | Maximal matching | 2 | O(E) |
| Set Cover | Greedy | O(log n) | O(nm) |
| Metric TSP | Christofides | 1.5 | O(n³) |
| Knapsack | FPTAS | 1+ε | O(n²/ε) |
| Max-Cut | Random | 0.5 | O(V+E) |
| Load Balance | LPT | 4/3 | O(n log n) |

## When to Use Approximation
- Problem is NP-hard and n is too large for exact algorithms
- Guaranteed quality beats heuristic uncertainty
- ε can be tuned for time-vs-quality trade-off
"@

wf "SECURITY.md" @"
# Security — Approximation

- Misleading guarantees: Ratio guarantees apply to all inputs, but adversary might find worst case
- Timing attacks on approximation: Adaptive computation could reveal information
- Input manipulation: Attacker can craft instances where approximation performs poorly
- Randomness: Deterministic approximations can be hard to predict
- Resource bounds: Approximation algorithms still need to complete in polynomial time
"@

wf "ARCHITECTURE.md" @"
# Architecture — Approximation

## Real-World Applications
- Logistics: Vehicle routing (Christofides for TSP)
- Cloud Computing: VM placement (bin packing approximation)
- Machine Learning: Feature selection (set cover)
- Network Design: Facility location
- Resource Allocation: Budgeted maximum coverage
- Sensor Networks: Coverage problems

## OR-Tools and Solvers
- Google OR-Tools: Contains approximation algorithms
- CPLEX: Exact MILP solver (can use approximations as starting points)
- LocalSolver: Local search-based heuristics
"@

wf "EXERCISES.md" @"
# Exercises — Approximation

## Beginner
1. Implement 2-approximation for vertex cover
2. Implement greedy set cover approximation
3. Analyze greedy MAX-CUT ratio (randomized)
4. Implement greedy load balancing (LPT)

## Intermediate
5. Implement Christofides algorithm for metric TSP
6. Implement FPTAS for knapsack
7. Implement LP rounding for vertex cover
8. Analyze approximation ratio of greedy set cover

## Advanced
9. Implement Goemans-Williamson MAX-CUT (SDP-based)
10. Primal-dual algorithm for facility location
11. Implement PTAS for subset sum (scaling and rounding)
12. Prove hardness of approximation (PCP theorem overview)
"@

wf "QUIZ.md" @"
# Quiz — Approximation

1. What is an approximation ratio?
2. Why can't TSP be approximated without triangle inequality?
3. What is the difference between PTAS and FPTAS?
4. Why is vertex cover 2-approximation tight?
5. What does the PCP theorem imply for approximation?
6. What makes Christofides algorithm achieve 1.5 ratio for metric TSP?
"@

wf "FLASHCARDS.md" @"
# Flashcards

- Q: Vertex cover ratio? → A: 2
- Q: Set cover ratio? → A: O(log n)
- Q: Metric TSP ratio (Christofides)? → A: 1.5
- Q: Knapsack FPTAS? → A: (1+ε) in O(n²/ε)
- Q: PTAS vs FPTAS? → A: FPTAS is poly in 1/ε, PTAS may not be
- Q: PCP theorem implication? → A: Many problems are hard to approximate
"@

wf "INTERVIEW.md" @"
# Interview Questions

1. "Design a 2-approximation for vertex cover." — Maximal matching
2. "How would you approximate TSP?" — Christofides or MST shortcut
3. "Explain the greedy set cover algorithm and its ratio." — O(log n)
4. "What is the difference between a heuristic and an approximation?" — Guarantees
5. "Design an FPTAS for knapsack." — Scaling and DP
6. "What makes metric TSP easier than general TSP?" — Triangle inequality
7. "Why is MAX-CUT 0.5 approximable by random assignment?" — Expected analysis
"@

wf "REFLECTION.md" @"
# Reflection

- When is a constant-factor approximation good enough in practice?
- How does the PCP theorem reshape our understanding of computation?
- What is the relationship between approximation and randomization?
- How do lower-poly problems differ from constants in real applications?
- When would you prefer a heuristic over an approximation algorithm?
"@

wf "REFERENCES.md" @"
# References

- Vazirani, V. "Approximation Algorithms" — The standard text
- CLRS, Chapter 35 (Approximation Algorithms)
- Williamson & Shmoys "The Design of Approximation Algorithms"
- Arora & Barak "Computational Complexity" — PCP and hardness
- Goemans & Williamson "Improved approximation algorithms for MAX CUT" (1995)
"@

Write-Host "14-approximation-algorithms: All 24 files created"
