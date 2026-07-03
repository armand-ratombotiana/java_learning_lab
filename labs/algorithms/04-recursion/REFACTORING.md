# Refactoring — Recursion

## Memoization
`java
private final Map<Integer, Long> memo = new HashMap<>();
public long fib(int n) {
    if (n <= 1) return n;
    return memo.computeIfAbsent(n, k -> fib(k-1) + fib(k-2));
}
`

## Iterative Conversion
`java
Deque<State> stack = new ArrayDeque<>();
stack.push(new State(initialArgs));
while (!stack.isEmpty()) {
    State s = stack.pop();
    // process, push substates
}
`
"@

wf "PERFORMANCE.md" @"
# Performance — Recursion

| Operation | Recursive | Iterative |
|-----------|-----------|-----------|
| factorial(20) | ~200ns | ~50ns |
| factorial(10000) | StackOverflow | ~2μs |
| fib(30) naive | ~10ms | <1μs |
| fib(30) memoized | ~50ns | N/A |

## Guidelines
- Use recursion when depth < 1000
- Use iteration for linear recursion
- Use memoization for overlapping subproblems
