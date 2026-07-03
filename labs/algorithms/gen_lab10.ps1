$labDir = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\algorithms\10-backtracking"

function wf($name, $content) {
    Set-Content -Path (Join-Path $labDir $name) -Value $content
}

wf "README.md" @"
# Backtracking — Overview

Covers N-Queens, Sudoku solver, subset sum, and general backtracking paradigm.

## Learning Objectives
- Understand backtracking as systematic trial-and-error
- Implement classic backtracking problems in Java
- Apply pruning techniques to optimize search
- Distinguish between backtracking and brute force

## Prerequisites
- Recursion fundamentals
- 2D arrays and matrix operations
- Basic algorithm analysis

## Estimated Time
- **Total**: 4–5 hours
"@

wf "THEORY.md" @"
# Backtracking — Theoretical Foundation

## The Backtracking Paradigm
Backtracking incrementally builds candidates and abandons partial candidates (backtracks) when they cannot become valid solutions.

## Key Components
1. **Choice**: At each step, make a decision
2. **Constraints**: Rules that must be satisfied
3. **Goal**: Condition that defines a complete solution

## Algorithm Template
```
void backtrack(candidate, state):
    if isSolution(candidate):
        output(candidate)
        return
    for each choice in generateChoices(candidate):
        if isValid(choice, state):
            makeChoice(choice, state)
            backtrack(nextCandidate, state)
            undoChoice(choice, state)
```

## Complexity
- Often exponential (O(branching^depth))
- Pruning can dramatically reduce search space
- Best case: solution found early with effective pruning
"@

wf "WHY_IT_EXISTS.md" @"
# Why Backtracking Exists

Many problems require exploring all possibilities (constraint satisfaction, puzzles). Backtracking provides a systematic way to explore the search space while pruning dead ends early, making it more efficient than naive brute force.
"@

wf "WHY_IT_MATTERS.md" @"
# Why Backtracking Matters

- Constraint Satisfaction: Sudoku, crosswords, scheduling
- Puzzles: N-Queens, mazes, word search
- Combinatorial Optimization: Subset sum, graph coloring
- AI: Constraint propagation in CSP solvers
- Compiler Design: Syntax parsing with backtracking
- Game AI: Decision tree exploration with pruning
"@

wf "HISTORY.md" @"
# History of Backtracking

- 1848: N-Queens problem posed by Max Bezzel
- 1850: Solutions by Gauss and others
- 1950s: Backtracking formalized as search algorithm
- 1960s: Golomb and Baumert pioneered systematic backtracking
- 1970s: Constraint satisfaction problems formalized
- 1980s: Backtracking used in Prolog (logic programming)
- 2000s: SAT solvers use DPLL (backtracking + unit propagation)
- 2010s+: Backtracking with constraint propagation in modern CSP solvers
"@

wf "MENTAL_MODELS.md" @"
# Mental Models

## Maze Solving
Walk down a path. If you hit a dead end, retrace your steps (backtrack) and try a different branch.

## Lock Picking
Try a combination. If the first few digits don't feel right, reset and try different ones. Each digit is a choice level.

## Chess Player
"I move here, then they move there, then I move here..." If the line leads to a loss, undo (backtrack) and try a different first move.
"@

wf "HOW_IT_WORKS.md" @"
# How Backtracking Works

## N-Queens Trace (4x4)
```
Start: Q at (0,0)
  Place Q at (1,2)
    Place Q at (2,? → none valid) ← BACKTRACK
  Undo (1,2), try (1,3)
    Place Q at (2,1)
      Place Q at (3,? → none valid) ← BACKTRACK
    Undo (2,1)
  No valid position at row 1 ← BACKTRACK
Undo (0,0), try Q at (0,1)
...
Solution: [1, 3, 0, 2] → Q at (0,1), (1,3), (2,0), (3,2)
```

## Subset Sum Trace (target=10)
```
[3, 5, 7, 2]
Include 3 → remaining 7
  Include 5 → remaining 2
    Include 7 → too big, backtrack
    Include 2 → remaining 0 ✓ Solution [3,5,2]
  Backtrack, try starting with 5...
```
"@

wf "INTERNALS.md" @"
# Backtracking — Internal Mechanics

## N-Queens
```java
public List<List<String>> solveNQueens(int n) {
    List<List<String>> solutions = new ArrayList<>();
    int[] queens = new int[n]; // queens[row] = column
    solve(queens, 0, solutions);
    return solutions;
}

private void solve(int[] queens, int row, List<List<String>> solutions) {
    if (row == queens.length) {
        solutions.add(boardToString(queens));
        return;
    }
    for (int col = 0; col < queens.length; col++) {
        if (isValid(queens, row, col)) {
            queens[row] = col;
            solve(queens, row + 1, solutions);
            // queens[row] = 0; // backtrack (not strictly needed)
        }
    }
}

private boolean isValid(int[] queens, int row, int col) {
    for (int i = 0; i < row; i++) {
        if (queens[i] == col) return false; // same column
        if (Math.abs(queens[i] - col) == Math.abs(i - row)) return false; // diagonal
    }
    return true;
}
```

## Sudoku Solver
```java
public boolean solveSudoku(char[][] board) {
    for (int row = 0; row < 9; row++) {
        for (int col = 0; col < 9; col++) {
            if (board[row][col] == '.') {
                for (char num = '1'; num <= '9'; num++) {
                    if (isValid(board, row, col, num)) {
                        board[row][col] = num;
                        if (solveSudoku(board)) return true;
                        board[row][col] = '.'; // backtrack
                    }
                }
                return false; // no valid number
            }
        }
    }
    return true; // all cells filled
}
```
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for Backtracking

## Search Space Size
- N-Queens: N! permutations (8-Queens = 40,320 nodes without pruning; ~1,500 with pruning)
- Sudoku: 9²¹ combinations without constraints
- Subset Sum: 2ⁿ subsets

## Pruning Effectiveness
Pruning can reduce exponential to near-polynomial for many practical instances. The efficiency depends on constraint tightness.

## Backtracking vs Brute Force
- Brute force: N! (all permutations)
- Backtracking with pruning: significantly fewer (still exponential worst case)
"@

wf "VISUAL_GUIDE.md" @"
# Visual Guide — Backtracking

## N-Queens Search Tree (4x4)
```
Root
├── Q at (0,0)
│   ├── Q at (1,2) → (2,? nothing) ✗
│   ├── Q at (1,3) → (2,1) → (3,? nothing) ✗
│   └── nothing valid at row 1 ✗
├── Q at (0,1)
│   └── Q at (1,3) → (2,0) → (3,2) ✓ Solution!
├── Q at (0,2)
│   └── Q at (1,0) → (2,3) → (3,1) ✓ Solution!
└── Q at (0,3)
    ├── Q at (1,0) → (2,? nothing) ✗
    └── Q at (1,1) → (2,? nothing) ✗
```

## Sudoku Search
```
Cell (0,0) try '1' → valid
  Cell (0,1) try '1' → invalid (same row)
  Cell (0,1) try '2' → invalid
  ...
  Cell (0,1) try '9' → none valid → BACKTRACK
Cell (0,0) try '2' → valid
... continues until solution
```
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive — Backtracking

## Subset Sum
```java
public List<List<Integer>> subsetSum(int[] nums, int target) {
    List<List<Integer>> result = new ArrayList<>();
    backtrack(nums, target, 0, new ArrayList<>(), result);
    return result;
}

private void backtrack(int[] nums, int remaining, int start,
        List<Integer> current, List<List<Integer>> result) {
    if (remaining == 0) {
        result.add(new ArrayList<>(current));
        return;
    }
    for (int i = start; i < nums.length; i++) {
        if (nums[i] <= remaining) {
            current.add(nums[i]);
            backtrack(nums, remaining - nums[i], i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }
}
```

## Permutations
```java
public List<List<Integer>> permute(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    boolean[] used = new boolean[nums.length];
    backtrack(nums, used, new ArrayList<>(), result);
    return result;
}

private void backtrack(int[] nums, boolean[] used,
        List<Integer> current, List<List<Integer>> result) {
    if (current.size() == nums.length) {
        result.add(new ArrayList<>(current));
        return;
    }
    for (int i = 0; i < nums.length; i++) {
        if (!used[i]) {
            used[i] = true;
            current.add(nums[i]);
            backtrack(nums, used, current, result);
            current.remove(current.size() - 1);
            used[i] = false;
        }
    }
}
```
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step — Backtracking

## General Approach
1. Define the state representation
2. Identify base case (solution found)
3. Generate all possible next choices
4. Filter by constraints (pruning)
5. Make a choice, recurse
6. Undo the choice (backtrack)

## Optimization Techniques
- **Forward checking**: Pre-check future constraints before choosing
- **Constraint propagation**: Reduce domains of unassigned variables
- **Most constrained variable**: Choose variable with fewest options first
- **Least constraining value**: Choose value that restricts others least
- **Symmetry breaking**: Avoid symmetric solutions
"@

wf "COMMON_MISTAKES.md" @"
# Common Mistakes

- Forgetting to backtrack (undo choice) — leads to incorrect state
- Not pruning early enough — wastes time on invalid branches
- Modifying shared state without copy — references cause aliasing bugs
- Missing base case — infinite recursion
- Checking constraints after full assignment — should prune earlier
- Not handling duplicate elements in input
- Exceeding recursion limit on large problems
"@

wf "DEBUGGING.md" @"
# Debugging — Backtracking

## Print Search Tree
```java
private static int depth = 0;
void log(String msg) {
    System.out.println("  ".repeat(depth) + msg);
}
// Before choice: depth++; log("Trying " + choice);
// After backtrack: depth--; log("Backtrack from " + choice);
```

## Visualization
For N-Queens, print the board at each solution candidate to verify correctness.
"@

wf "REFACTORING.md" @"
# Refactoring — Backtracking

## Generic Backtracking Framework
```java
abstract class BacktrackingSolver<T, R> {
    protected abstract boolean isSolution(T state);
    protected abstract List<T> getChoices(T state);
    protected abstract boolean isValid(T state, T choice);
    protected abstract T apply(T state, T choice);
    protected abstract T undo(T state, T choice);
    protected abstract R buildResult(T state);
}
```

## Iterative Backtracking
```java
Deque<State> stack = new ArrayDeque<>();
stack.push(initialState);
while (!stack.isEmpty()) {
    State s = stack.pop();
    // process, push continuations
}
```
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
"@

wf "SECURITY.md" @"
# Security — Backtracking

- Exponential backtracking: Attacker can craft inputs causing worst-case search
- Resource exhaustion: Backtracking can consume memory with deep recursion
- Timeout vulnerabilities: Long-running backtracking can be exploited
- Input validation: Limit input size to prevent explosion
- Use iterative deepening DFS for depth-limited search
"@

wf "ARCHITECTURE.md" @"
# Architecture — Backtracking

## Java Libraries
- No standard backtracking library
- Implement custom solvers for specific problems
- OptaPlanner: Constraint satisfaction (uses advanced search)

## Real-World Systems
- SAT Solvers: DPLL/CDCL (backtracking + conflict-driven learning)
- Constraint Logic Programming: Prolog, ECLiPSe
- Scheduling Systems: Resource-constrained project scheduling
- Circuit Design: Automated placement and routing
- Game Engines: Decision trees for AI
"@

wf "EXERCISES.md" @"
# Exercises — Backtracking

## Beginner
1. Generate all subsets of a set
2. Generate all permutations of a string
3. Generate all combinations of k elements

## Intermediate
4. N-Queens solver (output all solutions)
5. Sudoku solver
6. Subset sum (find one or all solutions)
7. Rat in a maze

## Advanced
8. Graph coloring (minimum colors)
9. Knight's tour (closed tour)
10. Cryptarithmetic puzzles (SEND+MORE=MONEY)
11. Regular expression matching
12. Word search in 2D grid
"@

wf "QUIZ.md" @"
# Quiz — Backtracking

1. What distinguishes backtracking from brute force?
2. What is pruning in backtracking?
3. Why is N-Queens O(N!) rather than O(N^N)?
4. How does Sudoku's constraint structure help pruning?
5. What is forward checking?
6. What does "most constrained variable" mean?
7. When is backtracking not appropriate?
"@

wf "FLASHCARDS.md" @"
# Flashcards

- Q: Backtracking key step? → A: Choose → explore → undo
- Q: N-Queens complexity? → A: O(N!) with pruning
- Q: Sudoku solver approach? → A: Backtracking with constraint propagation
- Q: Subset sum complexity? → A: O(2ⁿ)
- Q: Backtracking vs brute force? → A: Prunes invalid branches early
- Q: Forward checking? → A: Check future constraints before committing
"@

wf "INTERVIEW.md" @"
# Interview Questions

1. "Solve N-Queens." — Classic backtracking
2. "Sudoku solver." — Constraint satisfaction
3. "Generate all permutations." — Fundamental
4. "Generate all subsets/combinations." — Power set
5. "Word search in 2D grid." — Backtracking with direction
6. "Match regex with . and *." — Backtracking pattern matching
7. "Restore IP addresses from string." — Backtracking with partitioning
"@

wf "REFLECTION.md" @"
# Reflection

- How does pruning transform exponential search?
- What makes backtracking different from brute-force enumeration?
- How do problem constraints affect search space size?
- When should you use backtracking vs dynamic programming?
- How can symmetry breaking improve performance?
"@

wf "REFERENCES.md" @"
# References

- CLRS, Chapter 5 (Probabilistic Analysis and Randomized Algorithms)
- Russell & Norvig "Artificial Intelligence" — CSP chapter
- Golomb & Baumert "Backtrack Programming" (1965)
- Knuth, D. "The Art of Computer Programming, Vol. 4" — Combinatorial Algorithms
"@

Write-Host "10-backtracking: All 24 files created"
