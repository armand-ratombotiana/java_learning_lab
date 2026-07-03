# Debugging — Greedy

## Counterexample Search
`java
public static <T> boolean verifyGreedy(List<T> input) {
    T greedyResult = greedySolve(input);
    T optimalResult = bruteForceSolve(input);
    return compare(greedyResult, optimalResult);
}
`

## Random Testing
`java
Random rand = new Random(42);
for (int trial = 0; trial < 1000; trial++) {
    List<Item> items = generateRandomItems(rand);
    double greedy = fractionalKnapsack(items, capacity);
    double optimal = dpKnapsack(items, capacity);
    assert Math.abs(greedy - optimal) < 1e-9;
}
`
"@

wf "REFACTORING.md" @"
# Refactoring — Greedy

## Configurable Greedy
`java
public class GreedySolver<T> {
    private final Comparator<T> greedyComparator;
    private final BiPredicate<T, State> canAdd;
    
    public List<T> solve(List<T> candidates) {
        candidates.sort(greedyComparator);
        List<T> result = new ArrayList<>();
        for (T item : candidates) {
            if (canAdd.test(item, currentState()))
                result.add(item);
        }
        return result;
    }
}
`
"@

wf "PERFORMANCE.md" @"
# Performance — Greedy

| Algorithm | Time | Space |
|-----------|------|-------|
| Activity Selection | O(n log n) | O(1) |
| Huffman Coding | O(n log n) | O(n) |
| Dijkstra | O((V+E) log V) | O(V) |
| Kruskal | O(E log E) | O(V) |
| Fractional Knapsack | O(n log n) | O(1) |

## Greedy vs DP
| Aspect | Greedy | DP |
|--------|--------|-----|
| Time | Usually O(n log n) | Often O(n²)+ |
| Memory | Minimal | Often O(n²) |
| Proof | Exchange argument | Induction |
| Applicability | Matroids | General |
