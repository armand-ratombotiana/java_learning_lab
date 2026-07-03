$labDir = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\algorithms\04-recursion"

function wf($name, $content) {
    Set-Content -Path (Join-Path $labDir $name) -Value $content
}

wf "README.md" @"
# Recursion — Overview

Covers recursive thinking, backtracking fundamentals, and divide-and-conquer patterns.

## Learning Objectives
- Understand recursive paradigm: base case and recursive case
- Implement recursive solutions for classic problems
- Analyze recursion depth and stack space complexity
- Convert between recursive and iterative solutions

## Prerequisites
- Basic Java syntax and methods
- Understanding of stack memory

## Estimated Time
- **Total**: 3–4 hours
"@

wf "THEORY.md" @"
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
"@

wf "WHY_IT_EXISTS.md" @"
# Why Recursion Exists

Recursion mirrors mathematical induction and provides elegant solutions for self-similar problems. Many problems (tree traversal, graph search, sorting) have natural recursive formulations that are more intuitive than iterative alternatives. McCarthy's Lisp (1958) made recursion a central programming paradigm.
"@

wf "WHY_IT_MATTERS.md" @"
# Why Recursion Matters

- Natural expression: Tree traversal, parsing, sorting are inherently recursive
- Divide and Conquer: Foundation for efficient algorithms
- Backtracking: Essential for constraint satisfaction
- Functional programming: Pure languages use recursion instead of loops
- Compiler design: Recursive descent parsing is standard
- Interview essential: ~40% of technical interviews include recursion
"@

wf "HISTORY.md" @"
# History of Recursion

- 1930s: Gödel's incompleteness theorems used self-reference
- 1936: Church's lambda calculus
- 1958: Lisp — first language embracing recursion
- 1960: ALGOL 60 introduced recursion in imperative languages
- 1972: C language added recursion
- 1995: Java included recursion from inception
"@

wf "MENTAL_MODELS.md" @"
# Mental Models

## Matryoshka Doll
Open a Russian doll to find a smaller doll inside, which contains an even smaller doll, until the smallest solid doll (base case).

## Recursive Prayer
"To understand recursion, you must first understand recursion."

## Stack of Papers
Read top paper, which tells you to process another stack. Pause, process sub-stack, return where you left off.
"@

wf "HOW_IT_WORKS.md" @"
# How Recursion Works

## Factorial Call Stack
```
factorial(4) → 4 * factorial(3)
  factorial(3) → 3 * factorial(2)
    factorial(2) → 2 * factorial(1)
      factorial(1) → 1
    ← returns 2
  ← returns 6
← returns 24
```

## Fibonacci (inefficient)
```java
public static int fib(int n) {
    if (n <= 1) return n;
    return fib(n - 1) + fib(n - 2);
}
```
fib(5) calls fib(4) and fib(3); fib(4) calls fib(3) and fib(2) — many redundant calls (O(2ⁿ)).
"@

wf "INTERNALS.md" @"
# Recursion — Internal Mechanics

## Stack Frame
Each recursive call creates a frame with:
- Return address
- Local variables
- Parameters
- Return value slot

## Java Stack Limits
- Default: ~1MB per thread
- ~10,000-20,000 calls before StackOverflowError
- Can increase with -Xss flag

## Tail Recursion (NOT optimized in Java)
```java
public static int factorialTail(int n, int acc) {
    if (n <= 1) return acc;
    return factorialTail(n - 1, n * acc);
}
```
Java does NOT perform tail call optimization. Use iteration for deep recursion.

## Recursion vs Iteration
```java
// Iterative factorial — preferred in Java for deep recursion
public static int factorialIter(int n) {
    int result = 1;
    for (int i = 2; i <= n; i++) result *= i;
    return result;
}
```
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for Recursion

## Recurrence Relations
- Factorial: T(n) = T(n-1) + O(1) → O(n)
- Binary Search: T(n) = T(n/2) + O(1) → O(log n)
- Merge Sort: T(n) = 2T(n/2) + O(n) → O(n log n)
- Fibonacci (naive): T(n) = T(n-1) + T(n-2) + O(1) → O(φⁿ)

## Master Theorem
T(n) = aT(n/b) + f(n)
- If f(n) = O(n^c) where c < log_b(a): Θ(n^{log_b(a)})
- If f(n) = Θ(n^c log^k n) where c = log_b(a): Θ(n^c log^{k+1} n)
- If f(n) = Ω(n^c) where c > log_b(a): Θ(f(n))
"@

wf "VISUAL_GUIDE.md" @"
# Visual Guide — Recursion

## Recursion Tree for fib(5)
```
                    fib(5)
                   /      \
              fib(4)      fib(3)
             /     \      /    \
         fib(3)   fib(2) fib(2) fib(1)
         /    \   /   \   /   \
     fib(2) fib(1) 1 1  1 1   1
     /   \
    1    1
```
Total: 15 calls for fib(5), O(φⁿ) growth.

## Tower of Hanoi (3 disks)
```
Move disk 1: A → C
Move disk 2: A → B
Move disk 1: C → B
Move disk 3: A → C
Move disk 1: B → A
Move disk 2: B → C
Move disk 1: A → C
```
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive — Recursion

## Tower of Hanoi
```java
public static void hanoi(int n, char from, char to, char aux) {
    if (n == 1) {
        System.out.println("Move disk 1 from " + from + " to " + to);
        return;
    }
    hanoi(n - 1, from, aux, to);
    System.out.println("Move disk " + n + " from " + from + " to " + to);
    hanoi(n - 1, aux, to, from);
}
```

## Generate All Subsets (Backtracking)
```java
public static void subsets(int[] nums, int idx, List<Integer> current, List<List<Integer>> result) {
    if (idx == nums.length) {
        result.add(new ArrayList<>(current));
        return;
    }
    subsets(nums, idx + 1, current, result);
    current.add(nums[idx]);
    subsets(nums, idx + 1, current, result);
    current.remove(current.size() - 1);
}
```
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step — Recursion

## Solving Recursively
1. Identify base case(s): simplest instance
2. Assume subproblem solved: function works for smaller input
3. Combine solutions: use subproblem result for current problem
4. Ensure progress: each call moves toward base case

## Converting Iteration → Recursion
1. Loop variable → parameter
2. Loop condition → base case check
3. Loop body → recursive case
4. Loop update → recursive call with modified parameter

## Common Pitfalls
- Missing base case → infinite recursion
- Base case never reached
- Excessive depth → use iteration for deep recursion
- Recomputation → use memoization for overlapping subproblems
"@

wf "COMMON_MISTAKES.md" @"
# Common Mistakes — Recursion

- No base case — StackOverflowError
- Base case never reached — parameters don't converge
- Forgetting to return — missing return statement
- Modifying shared state — mutable objects cause subtle bugs
- Stack overflow — >10,000 calls; use -Xss or iteration
- Exponential complexity — naive Fibonacci; use memoization
- Not trusting recursion — trying to trace all calls instead of assuming subproblem solved
"@

wf "DEBUGGING.md" @"
# Debugging — Recursion

## Print Depth Tracing
```java
public static int factorial(int n, String indent) {
    System.out.println(indent + "factorial(" + n + ")");
    if (n <= 1) { System.out.println(indent + "→ 1"); return 1; }
    int result = n * factorial(n - 1, indent + "  ");
    System.out.println(indent + "→ " + result);
    return result;
}
```

## IDE Debugger
- Set breakpoint on recursive call
- Watch call stack in debugger
- Step Into to trace recursion, Step Over to skip
- In IntelliJ: Drop Frame to back out
"@

wf "REFACTORING.md" @"
# Refactoring — Recursion

## Memoization
```java
private final Map<Integer, Long> memo = new HashMap<>();
public long fib(int n) {
    if (n <= 1) return n;
    return memo.computeIfAbsent(n, k -> fib(k-1) + fib(k-2));
}
```

## Iterative Conversion
```java
Deque<State> stack = new ArrayDeque<>();
stack.push(new State(initialArgs));
while (!stack.isEmpty()) {
    State s = stack.pop();
    // process, push substates
}
```
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
"@

wf "SECURITY.md" @"
# Security — Recursion

- Stack Overflow DoS: Deep recursion on untrusted input can crash JVM
- Resource Exhaustion: Exponential-time recursion can be exploited
- Thread Starvation: Recursion on shared thread pool can block tasks
- Input Validation: Validate input size before recursion
"@

wf "ARCHITECTURE.md" @"
# Architecture — Recursion

## Java Ecosystem
- Stream API: Some operations recursive internally
- CompletableFuture: Recursive async composition
- ForkJoinPool: RecursiveTask/RecursiveAction for parallel divide-and-conquer
- Tree structures: File systems, DOM, ASTs

## Design Patterns Using Recursion
- Composite Pattern: Tree structures with same-type children
- Visitor Pattern: Recursive traversal
- Interpreter Pattern: Recursive grammar evaluation
- Iterator Pattern: Tree iterators use recursion internally
"@

wf "EXERCISES.md" @"
# Exercises — Recursion

## Beginner
1. Factorial recursively and iteratively
2. Fibonacci (naive, first n terms)
3. Sum array elements recursively
4. Reverse a string recursively

## Intermediate
5. Tower of Hanoi
6. Generate all permutations of a string
7. Binary search recursively
8. GCD using Euclidean algorithm recursively

## Advanced
9. Recursive descent parser for arithmetic
10. Josephus problem
11. QuickSort with tail-call optimization pattern
12. Convert recursive directory walker to explicit stack
"@

wf "QUIZ.md" @"
# Quiz — Recursion

1. Two essential components of every recursive function?
2. What happens when recursion depth exceeds stack capacity?
3. Why is naive Fibonacci O(φⁿ)?
4. What is tail recursion? Does Java optimize it?
5. Space complexity of recursive binary search?
6. How can you optimize overlapping subproblems?
"@

wf "FLASHCARDS.md" @"
# Flashcards

- Q: Two parts of recursion? → A: Base case and recursive case
- Q: Java recursion limit? → A: ~10,000-20,000 calls
- Q: Does Java do TCO? → A: No
- Q: Naive Fibonacci time? → A: O(φⁿ)
- Q: Memoized Fibonacci time? → A: O(n)
- Q: Recursion space? → A: O(depth) for stack
"@

wf "INTERVIEW.md" @"
# Interview Questions

1. "Implement recursive Fibonacci with memoization."
2. "Reverse a linked list recursively."
3. "Generate all permutations of a string."
4. "Solve Tower of Hanoi."
5. "Implement recursive binary search."
6. "Convert recursive function to iterative."
7. "What is tail recursion and why doesn't Java optimize it?"
"@

wf "REFLECTION.md" @"
# Reflection

- How does recursion relate to mathematical induction?
- Why is recursion often more intuitive than iteration for certain problems?
- When should you avoid recursion in Java?
- How does the call stack help debug recursive code?
"@

wf "REFERENCES.md" @"
# References

- CLRS, Chapter 4: Divide-and-Conquer
- Sedgewick, R. "Algorithms"
- Abelson & Sussman "Structure and Interpretation of Computer Programs"
- Roberts, E. "Thinking Recursively"
- Java Tutorial: Recursion
"@

Write-Host "04-recursion: All 24 files created"
