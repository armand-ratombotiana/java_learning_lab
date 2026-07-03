# Recursion — Theoretical Foundation

## The Recursive Paradigm
- Base case(s): Smallest instance solved directly
- Recursive case: Function calls itself with modified parameters

## Key Concepts
- Call Stack: Each call pushes a frame onto the stack
- Stack Overflow: Excessive depth exceeds available memory
- Tail Recursion: Recursive call is last operation
- Mutual Recursion: Functions calling each other

## Classic Patterns
- Divide and Conquer: Divide, solve recursively, combine
- Backtracking: Explore candidates, abandon invalid partial solutions
- DP: Recursion + memoization to avoid redundant computation

## Complexity
- Time: O(branching factor^depth)
- Space: O(depth) for call stack
