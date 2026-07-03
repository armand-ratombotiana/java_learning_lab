# Debugging — Approximation

## Verify Ratio Empirically
`java
// Compare with optimal for small instances (use brute force)
for (int trial = 0; trial < 1000; trial++) {
    Graph g = randomGraph(8); // small enough for brute force
    int approx = approxVertexCover(g);
    int optimal = optimalVertexCover(g); // brute force
    double ratio = (double) approx / optimal;
    assert ratio <= 2.0 : "Ratio violated: " + ratio;
    System.out.printf("Trial %d: approx=%d, opt=%d, ratio=%.2f%n", trial, approx, optimal, ratio);
}
`

## Track Worst-Case Ratio
`java
double maxRatio = 0;
// in loop: maxRatio = Math.max(maxRatio, ratio);
System.out.println("Worst-case ratio: " + maxRatio);
`
"@

wf "REFACTORING.md" @"
# Refactoring — Approximation

## Algorithm Selection Strategy
`java
interface ApproximationAlgorithm<T, R> {
    R solve(T problem);
    double getRatio();
    String getName();
}
`

## From Exact to Approximate
`java
if (n < THRESHOLD || isEasy(n)) {
    return solveExactly(problem);
} else {
    return solveApproximately(problem, epsilon);
}
`
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
