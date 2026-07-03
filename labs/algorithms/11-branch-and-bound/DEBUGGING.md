# Debugging — Branch and Bound

## Print Search Progress
`java
System.out.printf("Node: level=%d, value=%d, weight=%d, bound=%.2f%n",
    node.level, node.value, node.weight, node.bound);
`

## Verify Bound Correctness
`java
double actualBest = dpKnapsack(items, capacity);
double computedBound = bound(items, capacity);
assert computedBound >= actualBest : "Bound must be optimistic";
`

## Track Pruning Statistics
`java
System.out.println("Nodes explored: " + nodesExplored);
System.out.println("Nodes pruned: " + nodesPruned);
`
"@

wf "REFACTORING.md" @"
# Refactoring — Branch and Bound

## Abstract B&B Framework
`java
abstract class BranchAndBound<T, R> {
    protected abstract double bound(T node);
    protected abstract boolean isFeasible(T node);
    protected abstract List<T> branch(T node);
    protected abstract boolean isSolution(T node);
    protected abstract R buildResult(T node);
    protected abstract T root();
}
`

## Parallel B&B
`java
// Use fork/join to explore branches in parallel
// Share best solution via AtomicInteger
`
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
