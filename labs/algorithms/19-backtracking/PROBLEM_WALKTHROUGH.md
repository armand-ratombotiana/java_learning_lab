# LeetCode 51 — N-Queens — Problem Walkthrough

## Problem Statement

The **n-queens puzzle** is the problem of placing `n` queens on an `n x n` chessboard so that no two queens attack each other. Queens attack along rows, columns, and diagonals.

Given an integer `n`, return all **distinct solutions** to the n-queens puzzle. Each solution is a list of strings where `'Q'` indicates a queen and `'.'` indicates an empty square.

**Constraints:**
- `1 <= n <= 9`

**Examples:**
```
Input:  n = 4
Output: [[".Q..","...Q","Q...","..Q."],
         ["..Q.","Q...","...Q",".Q.."]]
Explanation: Two distinct solutions exist.

Input:  n = 1
Output: [["Q"]]
```

---

## Step-by-Step Solution

### Step 1: State Representation

- `cols[j]` = true if a queen occupies column `j`.
- `diag1[i - j + n - 1]` = true if a queen occupies the main diagonal (top-left to bottom-right). On this diagonal, `(i - j)` is constant.
- `diag2[i + j]` = true if a queen occupies the anti-diagonal (top-right to bottom-left). On this diagonal, `(i + j)` is constant.

### Step 2: Backtracking Strategy

Place queens row by row, from 0 to n-1. For each row, try each column:
1. Check if the column, main diagonal, and anti-diagonal are free.
2. If yes, mark them as occupied, place the queen.
3. Recursively proceed to the next row.
4. Backtrack by unmarking the positions.

### Step 3: Pruning

- The row constraint is satisfied automatically (one queen per row).
- Column and diagonal sets eliminate invalid placements before recursion.
- No need to check rows — the recursion ensures one per row.

---

## Full Compilable Solution

```java
import java.util.*;

/**
 * LeetCode 51 — N-Queens
 *
 * Backtracking with column and diagonal pruning.
 *
 * Time:  O(n!) — upper bound, pruned heavily
 * Space: O(n) — recursion depth + sets
 */
public class NQueens {

    public List<List<String>> solveNQueens(int n) {
        List<List<String>> results = new ArrayList<>();
        char[][] board = new char[n][n];
        for (char[] row : board) Arrays.fill(row, '.');

        boolean[] cols = new boolean[n];
        boolean[] diag1 = new boolean[2 * n - 1]; // i - j + n - 1
        boolean[] diag2 = new boolean[2 * n - 1]; // i + j

        backtrack(results, board, cols, diag1, diag2, 0, n);
        return results;
    }

    private void backtrack(List<List<String>> results, char[][] board,
                           boolean[] cols, boolean[] diag1, boolean[] diag2,
                           int row, int n) {
        if (row == n) {
            results.add(buildSolution(board));
            return;
        }

        for (int col = 0; col < n; col++) {
            int d1 = row - col + n - 1;
            int d2 = row + col;

            if (cols[col] || diag1[d1] || diag2[d2]) continue;

            board[row][col] = 'Q';
            cols[col] = true;
            diag1[d1] = true;
            diag2[d2] = true;

            backtrack(results, board, cols, diag1, diag2, row + 1, n);

            board[row][col] = '.';
            cols[col] = false;
            diag1[d1] = false;
            diag2[d2] = false;
        }
    }

    private List<String> buildSolution(char[][] board) {
        List<String> solution = new ArrayList<>();
        for (char[] row : board) solution.add(new String(row));
        return solution;
    }

    public static void main(String[] args) {
        NQueens s = new NQueens();

        for (int n = 1; n <= 9; n++) {
            List<List<String>> solutions = s.solveNQueens(n);
            System.out.printf("n=%d: %d solutions%n", n, solutions.size());
        }

        // Print solutions for n=4
        System.out.println("\nn=4 solutions:");
        for (List<String> sol : s.solveNQueens(4)) {
            for (String row : sol) System.out.println("  " + row);
            System.out.println();
        }

        // Verify n=1
        System.out.println("n=1 solutions: " + s.solveNQueens(1).size() + " (expected: 1)");
        System.out.println("n=2 solutions: " + s.solveNQueens(2).size() + " (expected: 0)");
        System.out.println("n=3 solutions: " + s.solveNQueens(3).size() + " (expected: 0)");
    }
}
```

---

## Bitmask Version (Even Faster)

```java
/**
 * N-Queens using bitmasks for columns and diagonals.
 * Faster for larger n; uses integers as sets of bits.
 *
 * Time:  O(n!) pruned | Space: O(n)
 */
public class NQueensBitmask {

    public List<List<String>> solveNQueens(int n) {
        List<List<String>> results = new ArrayList<>();
        char[][] board = new char[n][n];
        for (char[] row : board) Arrays.fill(row, '.');
        backtrack(results, board, 0, 0, 0, 0, n);
        return results;
    }

    private void backtrack(List<List<String>> results, char[][] board,
                           int row, int cols, int diag1, int diag2, int n) {
        if (row == n) {
            results.add(buildSolution(board));
            return;
        }

        int available = ((1 << n) - 1) & ~(cols | diag1 | diag2);
        while (available != 0) {
            int bit = available & -available;   // lowest set bit
            int col = Integer.numberOfTrailingZeros(bit);

            board[row][col] = 'Q';
            backtrack(results, board, row + 1,
                cols | bit,
                (diag1 | bit) << 1,
                (diag2 | bit) >> 1,
                n);
            board[row][col] = '.';

            available ^= bit;  // remove this bit
        }
    }

    private List<String> buildSolution(char[][] board) {
        List<String> sol = new ArrayList<>();
        for (char[] row : board) sol.add(new String(row));
        return sol;
    }

    public static void main(String[] args) {
        NQueensBitmask s = new NQueensBitmask();
        long start = System.nanoTime();
        List<List<String>> sols = s.solveNQueens(12);
        long end = System.nanoTime();
        System.out.println("n=12: " + sols.size() + " solutions (expected: 14200)");
        System.out.println("Time: " + (end - start) / 1e6 + " ms");
    }
}
```

---

## Complexity Analysis

| Version | Time | Space | Notes |
|---------|------|-------|-------|
| Boolean arrays | O(n!) worst, much less in practice | O(n) | Clear code, good for n ≤ 12 |
| Bitmask | O(n!) worst, ~5x faster | O(n) | Constant-time collision checks |

### Why O(n!) With Heavy Pruning

- Row 0: try up to n positions.
- Row 1: up to n-2 valid positions (column + 2 diagonals blocked).
- Row 2: even fewer constraints.
- Total: roughly O(n!) in the worst case, but pruning makes it feasible for n ≤ 12.

The number of solutions for n=1..9: 1, 0, 0, 2, 10, 4, 40, 92, 352.

---

## Follow-Up: N-Queens II (LeetCode 52)

Count the number of distinct solutions without building them.

```java
public class NQueensII {

    public int totalNQueens(int n) {
        return backtrack(0, 0, 0, 0, n);
    }

    private int backtrack(int row, int cols, int diag1, int diag2, int n) {
        if (row == n) return 1;
        int count = 0;
        int available = ((1 << n) - 1) & ~(cols | diag1 | diag2);
        while (available != 0) {
            int bit = available & -available;
            available ^= bit;
            count += backtrack(row + 1,
                cols | bit,
                (diag1 | bit) << 1,
                (diag2 | bit) >> 1,
                n);
        }
        return count;
    }

    public static void main(String[] args) {
        NQueensII s = new NQueensII();
        for (int n = 1; n <= 12; n++)
            System.out.printf("n=%d: %d%n", n, s.totalNQueens(n));
    }
}
```

---

## Edge Cases & Test Coverage

| n | Solutions | Notes |
|---|-----------|-------|
| 1 | 1 | Single cell—trivially valid |
| 2 | 0 | Impossible: both queens attack |
| 3 | 0 | Impossible for 3x3 |
| 4 | 2 | Minimum interesting case |
| 5 | 10 | First n with >2 solutions |
| 8 | 92 | Classic 8-queens problem |
| 9 | 352 | ~4x increase from n=8 |

---

## Key Takeaways

1. **Backtracking with pruning** is the canonical approach for combinatorial constraint satisfaction.
2. **Diagonal indexing** is key: `(i - j)` is constant for main diagonals, `(i + j)` for anti-diagonals.
3. **Bitmask** representation is significantly faster — use it for larger n (competitive programming).
4. The **row-by-row** placement automatically handles the row constraint.
5. N-Queens generalizes to other constraint problems: Sudoku, graph coloring, exact cover.