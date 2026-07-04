# Interview Questions: Stacks & Queues

## Easy

1. **Valid Parentheses** — Check if a string of brackets is properly matched.

2. **Implement Queue using Stacks** — Two-stack queue (amortized O(1)).

3. **Implement Stack using Queues** — Two-queue stack.

4. **Min Stack** — Stack with O(1) getMin.

## Medium

5. **Evaluate Reverse Polish Notation** — Evaluate `["2","1","+","3","*"]` → 9.

6. **Daily Temperatures** — For each day, find days until a warmer temperature (monotonic stack).

7. **Sliding Window Maximum** — Maximum in every k-sized window (monotonic deque).

8. **Asteroid Collision** — Asteroids moving left/right; simulate collisions.

9. **Decode String** — `"3[a2[c]]"` → `"accaccacc"`.

10. **Simplify Path** — Canonical path from Unix-like path string.

## Hard

11. **Largest Rectangle in Histogram** — Max rectangle area in a bar chart (monotonic stack).

12. **Trapping Rain Water** — Water trapped between bars (two-pointer or stack).

13. **Basic Calculator** — Expression with `+`, `-`, `*`, `/`, `(`, `)`.

14. **Max Stack** — Stack with push, pop, top, peekMax, popMax.

## Key Patterns

- **Monotonic stack**: maintains increasing/decreasing order; used for next greater/smaller elements
- **Two stacks**: queue from stacks, expression evaluation (operand + operator stacks)
- **Sliding window + deque**: maintain window elements in decreasing order
- **Backtracking with stack**: DFS, expression parsing, path finding

## Java-Specific Topics

- `ArrayDeque` vs `Stack` vs `LinkedList` — which to use when
- `PriorityQueue` with custom Comparators
- `BlockingQueue` for concurrency (put/take block)
- Legacy `Stack` issues (synchronized, extends Vector)
