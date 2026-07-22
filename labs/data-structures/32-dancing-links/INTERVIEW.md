# Interview Questions: Dancing Links (DLX)

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| [LC 37 Sudoku Solver](https://leetcode.com/problems/sudoku-solver/) | Hard | Amazon, Meta, Google, Microsoft | Backtracking / DLX |
| [LC 51 N-Queens](https://leetcode.com/problems/n-queens/) | Hard | Amazon, Meta, Google, Microsoft, Apple | Backtracking / exact cover |
| [LC 52 N-Queens II](https://leetcode.com/problems/n-queens-ii/) | Hard | Amazon, Google, Meta | Backtracking counting |
| (System design focus) | — | Google, Microsoft, Oracle | Exact cover / Algorithm X |

## NeetCode Reference
Not in NeetCode 150. Dancing Links is an advanced algorithm for exact cover problems.

## Company-Specific Questions

### Google
- Explain how Dancing Links (DLX) solves the exact cover problem using Algorithm X
- How does the circular doubly linked list matrix enable O(1) cover/uncover operations?
- Apply DLX to solve Sudoku — how do you encode Sudoku constraints as an exact cover matrix?
- How would you solve the N-Queens problem using DLX? What is the constraint encoding?

### Microsoft
- Design a generic exact cover solver using Dancing Links — how would you model different problems?
- Compare Dancing Links vs simple backtracking for Sudoku — why is DLX faster for dense constraint problems?
- How does the "dancing" concept work? Walk through covering and uncovering a column

### Meta
- Solve pentomino tiling using Dancing Links (classic exact cover application)
- How would you implement a generic constraint satisfaction problem solver using DLX?
- Compare Dancing Links vs SAT solver for exact cover problems

### Amazon
- Apply DLX to solve a scheduling problem (assign shifts to employees with rotation coverage)
- How would you model a resource allocation problem as an exact cover?
- Design a system that uses DLX for optimal packing/placement

### Apple
- How would you use DLX to solve a crossword puzzle construction problem?
- Design a constraint solver that applies to Apple's scheduling systems
- Compare Dancing Links vs linear programming for combinatorial optimization

### Oracle
- How does the column-covering technique in DLX relate to database indexing and query optimization?
- How would you implement a generic exact cover solver in Java using the DLX data structure?
- What are the practical limitations of DLX for large problem instances?

## Real Production Scenarios

- **Scenario 1: Sudoku Solver** — A puzzle game app uses Dancing Links to solve Sudoku puzzles of any difficulty. The Sudoku constraints (row, column, box, cell) are encoded as an exact cover matrix with 324 columns (9×4 constraints) and 729 rows (9³ possible placements). DLX solves typical 9×9 Sudoku in milliseconds.

- **Scenario 2: Automated Scheduling** — A scheduling system uses Dancing Links to assign employees to shifts with constraints (each shift requires certain roles, each employee has availability constraints). The problem is modeled as an exact cover. Multiple solutions can be found by continuing the DLX search after the first solution.

- **Scenario 3: VLSI Design** — A chip design tool uses DLX for block floorplanning. Functional blocks must be placed on the chip without overlap (tiling problem). Each block's position is a row, and each grid cell is a column. Dancing Links finds a valid non-overlapping placement.

## Interview Tips

- Time: O(2^n) worst case (exponential in problem size), but in practice very fast for well-constrained problems (Sudoku, N-Queens)
- Space: O(rows × cols) for the full constraint matrix; DLX uses a sparse representation (only links for 1s)
- Common edge cases: unsatisfiable problem (no solution), multiple solutions (continue search), cyclic solutions in the constraint graph
- Algorithm X: deterministic backtracking with column selection (choose column with fewest 1s for efficiency)
- Cover column: remove the column and all rows that have a 1 in that column
- Uncover column: reinsert the column and rows in reverse order (this is the "dancing" — exact reversal)
- Each node in the DLX matrix stores L,R,U,D (left, right, up, down) and column header reference

## Java-Specific Considerations

- No standard Dancing Links class in Java — implement from scratch
- Node class: `class DLXNode { DLXNode L, R, U, D; ColumnNode C; }`
- ColumnNode extends DLXNode: `class ColumnNode extends DLXNode { int size; String name; }`
- Matrix construction: create a header row (column nodes), then insert rows with 1s
- Row insertion: link nodes horizontally (L↔R) and vertically (U↔D) into their respective columns
- Cover column: unlink column (L↔R of header), then unlink each row in the column (U↔D of each node)
- Uncover: reverse of cover — relink in exact reverse order (U↔D first, then L↔R)
- `search()` method: `if (header.R == header) { // found solution } choose column; cover column; for each row: try row (add to solution), recurse, remove from solution; uncover column`
- Root header node: dummy node with `header.L` and `header.R` pointing to first/last column
- Choosing column: heuristic: `ColumnNode col = root.R; for (ColumnNode c = root.R; c != root; c = c.R) { if (c.size < col.size) col = c; }` (choose smallest column)
- `java.util.ArrayList<DLXNode>` for solution tracking (list of row header nodes)
- For Sudoku: generate 729 rows × 324 columns; each row encodes (row, col, num) with 4 ones (row constraint, col constraint, box constraint, cell constraint)
- For N-Queens: N² rows × 4N columns (row, col, diag, anti-diag) — actually simpler encoding
- Memory: each node is an object (heavy in Java). For large problems, consider custom `int[]` arrays for link indices instead of objects
- Due to object overhead, DLX in Java may be slower than optimized backtracking for moderate-sized problems
