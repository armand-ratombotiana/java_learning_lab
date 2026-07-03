$labDir = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\algorithms\11-branch-and-bound"

function wf($name, $content) {
    Set-Content -Path (Join-Path $labDir $name) -Value $content
}

wf "README.md" @"
# Branch and Bound — Overview

Covers Branch and Bound paradigm for Traveling Salesman Problem (TSP) and 0/1 Knapsack.

## Learning Objectives
- Understand branch and bound as optimization-oriented backtracking
- Apply bounding functions to prune suboptimal branches
- Implement B&B for TSP and knapsack in Java
- Compare B&B with DP and backtracking

## Prerequisites
- Backtracking fundamentals
- Graph algorithms (TSP)
- Dynamic programming (knapsack)

## Estimated Time
- **Total**: 4–5 hours
"@

wf "THEORY.md" @"
# Branch and Bound — Theoretical Foundation

## The Paradigm
Branch and Bound = Backtracking + Bounding Function

Unlike backtracking (which finds all solutions), B&B finds the optimal solution by keeping track of the best solution found so far and pruning branches that cannot beat it.

## Key Components
1. **Branching**: How to divide problem into subproblems
2. **Bounding**: Estimate upper/lower bound for a subproblem
3. **Pruning**: Discard subproblem if bound is worse than current best
4. **Search Strategy**: Best-first, depth-first, breadth-first

## Bounding Types
- **Lower Bound**: Minimum cost achievable from this node (minimization)
- **Upper Bound**: Maximum value achievable (maximization)
- If bound cannot improve on current best → prune
"@

wf "WHY_IT_EXISTS.md" @"
# Why Branch and Bound Exists

NP-hard optimization problems (TSP, knapsack) cannot be solved optimally in polynomial time. B&B provides a systematic way to find optimal solutions by intelligently pruning the search space, often solving large instances that exhaustive search cannot handle.
"@

wf "WHY_IT_MATTERS.md" @"
# Why Branch and Bound Matters

- Optimal Solutions: Finds provably optimal solutions for NP-hard problems
- Scalability: Can solve much larger instances than brute force
- Applications: Logistics (TSP), resource allocation (knapsack), scheduling
- Foundation: B&B is the basis for integer programming solvers
- Bounding: The art of finding tight bounds is a critical skill
"@

wf "HISTORY.md" @"
# History of Branch and Bound

- 1960: Land and Doig developed branch and bound for integer programming
- 1963: Little et al. applied B&B to TSP
- 1960s-70s: B&B became standard for combinatorial optimization
- 1980s: Branch and cut (B&B + cutting planes) for integer programming
- 1990s: Branch and price (B&B + column generation)
- 2000s+: B&B used in modern MILP solvers (CPLEX, Gurobi)
"@

wf "MENTAL_MODELS.md" @"
# Mental Models

## The Budget Traveler
Planning a trip with a limited budget. You estimate costs for partial itineraries. If a partial plan already exceeds your best found trip cost, you abandon it.

## The Tournament Bracket
Like a bracket where you know the best player's score. Any bracket branch that can't beat that score is dropped.

## The Explorer with a Map
You have a map with contour lines (bounds). Before hiking a trail, you check if it can possibly lead to a higher peak than your current best. If not, skip it.
"@

wf "HOW_IT_WORKS.md" @"
# How Branch and Bound Works

## TSP — Branching on Edges
```
Cities: A, B, C, D
Start at A

Level 1: Choose first edge
  Branch: A→B (cost 10) → bound = 10 + lowerBound = 25
  Branch: A→C (cost 15) → bound = 15 + lowerBound = 30
  Branch: A→D (cost 12) → bound = 12 + lowerBound = 27

Process A→B first (lowest bound):
  Level 2: From B, choose next edge
    Branch: B→C (cost 20) → bound = 30
    Branch: B→D (cost 8) → bound = 18 + lowerBound = 28

Continue until complete tour found, prune branches with bound ≥ best
```

## Knapsack — Branching on Items
```
Items sorted by value/weight ratio
Node: bound = current value + (remaining capacity × best ratio)
If bound ≤ best value → prune
```
"@

wf "INTERNALS.md" @"
# Branch and Bound — Internal Mechanics

## Knapsack B&B
```java
class Node implements Comparable<Node> {
    int level, value, weight;
    double bound;
    public int compareTo(Node o) { return Double.compare(o.bound, this.bound); }
}

public int knapsack(int[] values, int[] weights, int capacity) {
    int n = values.length;
    Integer[] indices = IntStream.range(0, n).boxed().toArray(Integer[]::new);
    Arrays.sort(indices, (a, b) -> 
        Double.compare((double)values[b]/weights[b], (double)values[a]/weights[a]));

    PriorityQueue<Node> pq = new PriorityQueue<>();
    pq.offer(new Node(-1, 0, 0, bound(indices, values, weights, capacity, -1, 0, 0)));
    int best = 0;

    while (!pq.isEmpty()) {
        Node node = pq.poll();
        if (node.bound <= best) continue;

        int next = node.level + 1;
        if (next == n) continue;

        // take item
        int w = node.weight + weights[indices[next]];
        int v = node.value + values[indices[next]];
        if (w <= capacity) {
            best = Math.max(best, v);
            pq.offer(new Node(next, w, v, bound(indices, values, weights, capacity, next, w, v)));
        }

        // don't take item
        pq.offer(new Node(next, node.weight, node.value,
            bound(indices, values, weights, capacity, next, node.weight, node.value)));
    }
    return best;
}

private double bound(Integer[] indices, int[] values, int[] weights, int cap, int level, int weight, int value) {
    double bound = value;
    int remain = cap - weight;
    for (int i = level + 1; i < indices.length && remain > 0; i++) {
        int idx = indices[i];
        if (remain >= weights[idx]) {
            bound += values[idx];
            remain -= weights[idx];
        } else {
            bound += (double)values[idx] / weights[idx] * remain;
            break;
        }
    }
    return bound;
}
```
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for Branch and Bound

## Bounding Functions

### Knapsack Upper Bound
Sort by value/weight ratio. Take whole items greedily, then fraction of next item:
bound = currentValue + Σ(full items) + remainingCapacity × (next value/weight)

### TSP Lower Bound
Using reduction of cost matrix:
1. Subtract minimum from each row
2. Subtract minimum from each column
3. Sum of all subtracted values = lower bound

## Pruning Condition
If upper bound (maximization) ≤ current best → prune
If lower bound (minimization) ≥ current best → prune

## Search Strategies
- Best-first: Choose node with best bound (memory intensive)
- Depth-first: Low memory, may explore poor branches early
- Breadth-first: High memory, uniform exploration
"@

wf "VISUAL_GUIDE.md" @"
# Visual Guide — Branch and Bound

## B&B Search Tree for Knapsack
```
Capacity=5, Items: (w=2,v=3), (w=3,v=4), (w=4,v=5)
Sorted by ratio: item2(1.33), item3(1.25), item1(1.5)

Root: v=0, w=0, bound=7.33
├── Take item2: v=4, w=3, bound=7
│   ├── Take item3: v=9, w=7 ✗ (overweight)
│   └── Skip item3: v=4, w=3, bound=5.5
│       ├── Take item1: v=7, w=5, bound=7 ✓ best=7
│       └── Skip item1: v=4, w=3, bound=4 (prune ≤ 7)
└── Skip item2: v=0, w=0, bound=6.5 (prune ≤ 7)
Best solution: take item2 + item1 = value 7
```
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive — Branch and Bound

## TSP with B&B
```java
public int tsp(int[][] dist) {
    int n = dist.length;
    int[] bestTour = new int[n];
    int[] currTour = new int[n];
    boolean[] visited = new boolean[n];
    int[] bestCost = {Integer.MAX_VALUE};

    currTour[0] = 0;
    visited[0] = true;
    tspBranch(dist, currTour, visited, 1, 0, bestTour, bestCost);
    return bestCost[0];
}

private void tspBranch(int[][] dist, int[] currTour, boolean[] visited,
        int level, int currCost, int[] bestTour, int[] bestCost) {
    if (level == currTour.length) {
        int totalCost = currCost + dist[currTour[level-1]][currTour[0]];
        if (totalCost < bestCost[0]) bestCost[0] = totalCost;
        return;
    }

    for (int i = 1; i < currTour.length; i++) {
        if (!visited[i]) {
            int newCost = currCost + dist[currTour[level-1]][i];
            if (newCost < bestCost[0]) {
                visited[i] = true;
                currTour[level] = i;
                tspBranch(dist, currTour, visited, level + 1, newCost, bestTour, bestCost);
                visited[i] = false;
            }
        }
    }
}
```
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step — Branch and Bound

## General Approach
1. Define node structure (level, value, weight, bound)
2. Choose search strategy (best-first typically)
3. Compute initial best (heuristic or greedy)
4. For each node:
   a. If bound cannot improve best → prune
   b. Branch: generate child nodes
   c. Compute bound for each child
   d. Add children to priority queue
5. Continue until queue empty

## Designing a Good Bound
- Tight bound = more pruning = faster
- But tight bound is costly to compute
- Trade-off between bound quality and computation time
- Fractional knapsack bound is a common starting point
"@

wf "COMMON_MISTAKES.md" @"
# Common Mistakes

- Bound not optimistic enough — prunes optimal solutions
- Bound too loose — no pruning, degenerates to brute force
- Using depth-first when best-first would prune more
- Not updating best solution early — many branches don't get pruned
- Incorrect bound computation for remaining capacity
- Not handling integer vs fractional bound properly
- Priority queue grows too large (memory issues)
"@

wf "DEBUGGING.md" @"
# Debugging — Branch and Bound

## Print Search Progress
```java
System.out.printf("Node: level=%d, value=%d, weight=%d, bound=%.2f%n",
    node.level, node.value, node.weight, node.bound);
```

## Verify Bound Correctness
```java
double actualBest = dpKnapsack(items, capacity);
double computedBound = bound(items, capacity);
assert computedBound >= actualBest : "Bound must be optimistic";
```

## Track Pruning Statistics
```java
System.out.println("Nodes explored: " + nodesExplored);
System.out.println("Nodes pruned: " + nodesPruned);
```
"@

wf "REFACTORING.md" @"
# Refactoring — Branch and Bound

## Abstract B&B Framework
```java
abstract class BranchAndBound<T, R> {
    protected abstract double bound(T node);
    protected abstract boolean isFeasible(T node);
    protected abstract List<T> branch(T node);
    protected abstract boolean isSolution(T node);
    protected abstract R buildResult(T node);
    protected abstract T root();
}
```

## Parallel B&B
```java
// Use fork/join to explore branches in parallel
// Share best solution via AtomicInteger
```
"@

wf "PERFORMANCE.md" @"
# Performance — Branch and Bound

| Problem | Brute Force | B&B (avg) | B&B (worst) |
|---------|------------|-----------|-------------|
| TSP (n=20) | 20! ≈ 10¹⁸ | ~10⁶ nodes | ~10¹⁸ (pathological) |
| Knapsack (n=50) | 2⁵⁰ ≈ 10¹⁵ | ~10⁴ nodes | ~10¹⁵ |
| Graph Coloring | kⁿ | ~10⁵ nodes | kⁿ |

## Key Factors
- Bound quality: Tight bounds → exponential speedup
- Problem structure: Well-constrained problems solve faster
- Search order: Best-first typically best
- Memory: Best-first can use O(branching³) memory
"@

wf "SECURITY.md" @"
# Security — Branch and Bound

- DoS: Pathological input causing worst-case exponential search
- Memory exhaustion: Best-first search can consume significant RAM
- Timeout: Long-running B&B can be exploited for resource exhaustion
- Input validation: Limit problem size (n ≤ 20 for TSP, n ≤ 50 for knapsack)
- Use iterative deepening or depth-first for memory-constrained environments
"@

wf "ARCHITECTURE.md" @"
# Architecture — Branch and Bound

## Real-World B&B Systems
- CPLEX, Gurobi: MILP solvers (branch and cut)
- Concorde: TSP solver (uses B&B with cutting planes)
- OR-Tools: Google's optimization library
- OptaPlanner: Constraint satisfaction with B&B-like search

## Applications
- Logistics: Vehicle routing (TSP variant)
- Manufacturing: Job shop scheduling
- Finance: Portfolio optimization (knapsack variant)
- Telecom: Network design
- Biology: Protein folding
"@

wf "EXERCISES.md" @"
# Exercises — Branch and Bound

## Beginner
1. Implement fractional knapsack bound
2. Solve 0/1 knapsack with B&B (best-first)
3. Compute TSP lower bound using row/column reduction

## Intermediate
4. Implement TSP with B&B
5. Add bounding function to N-Queens (count solutions with bound)
6. Compare B&B vs DP for knapsack performance
7. Implement depth-first B&B for knapsack

## Advanced
8. Implement assignment problem with B&B
9. Implement job shop scheduling with B&B
10. Compare best-first vs depth-first strategies
11. Implement parallel B&B using ForkJoinPool
"@

wf "QUIZ.md" @"
# Quiz — Branch and Bound

1. What distinguishes B&B from backtracking?
2. What makes a good bounding function?
3. Why must bound be optimistic?
4. Compare best-first vs depth-first search in B&B
5. How does bound tightness affect performance?
6. What is branch and cut?
7. When would you choose B&B over DP?
"@

wf "FLASHCARDS.md" @"
# Flashcards

- Q: B&B vs backtracking? → A: B&B uses bounds to prune suboptimal branches
- Q: Optimal solution guarantee? → A: Yes, if bound is optimistic
- Q: B&B memory issue? → A: Best-first can use exponential memory
- Q: Knapsack upper bound? → A: Fractional knapsack greedy
- Q: TSP lower bound? → A: Cost matrix reduction
- Q: Branch and cut? → A: B&B + cutting planes
"@

wf "INTERVIEW.md" @"
# Interview Questions

1. "Solve 0/1 knapsack with B&B." — Bound design matters
2. "How would you solve TSP with 50 cities?" — Discuss B&B + heuristics
3. "Design a bounding function for X." — Tests creativity
4. "Compare B&B, DP, and greedy for knapsack." — Trade-off analysis
5. "How does B&B relate to A* search?" — Both use bounds/heuristics
"@

wf "REFLECTION.md" @"
# Reflection

- How does bound quality affect the search space?
- What trade-offs exist between bound computation cost and pruning?
- When is B&B preferable to approximation algorithms?
- How does search strategy affect memory and speed?
- What real-world problems would you solve with B&B vs DP?
"@

wf "REFERENCES.md" @"
# References

- Land & Doig "An automatic method for solving discrete programming problems" (1960)
- Little et al. "An algorithm for the traveling salesman problem" (1963)
- CLRS, Chapter 35 (Approximation Algorithms) — also covers B&B
- Papadimitriou & Steiglitz "Combinatorial Optimization"
- CPLEX and Gurobi documentation
"@

Write-Host "11-branch-and-bound: All 24 files created"
