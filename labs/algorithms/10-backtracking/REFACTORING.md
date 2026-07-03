# Refactoring — Backtracking

## Generic Backtracking Framework
`java
abstract class BacktrackingSolver<T, R> {
    protected abstract boolean isSolution(T state);
    protected abstract List<T> getChoices(T state);
    protected abstract boolean isValid(T state, T choice);
    protected abstract T apply(T state, T choice);
    protected abstract T undo(T state, T choice);
    protected abstract R buildResult(T state);
}
`

## Iterative Backtracking
`java
Deque<State> stack = new ArrayDeque<>();
stack.push(initialState);
while (!stack.isEmpty()) {
    State s = stack.pop();
    // process, push continuations
}
`
"@

wf "PERFORMANCE.md" @"
# Performance — Backtracking

| Problem | Worst-Case | With Pruning | Notes |
|---------|-----------|--------------|-------|
| N-Queens | O(N!) | ~O(N!) | Symmetry breaking helps |
| Sudoku | O(9ⁿ) | Near-constant | Well-constrained |
| Subset Sum | O(2ⁿ) | O(2ⁿ) | NP-complete |
| Graph Coloring | O(kⁿ) | Near-linear for 2-col | 3-col+ is NP-complete |
| Permutations | O(n!) | O(n!) | No pruning possible |

## Optimization Impact
- Forward checking: 10-100x faster for CSP
- Most constrained variable: 2-10x faster
- Symmetry breaking: 2x for N-Queens
