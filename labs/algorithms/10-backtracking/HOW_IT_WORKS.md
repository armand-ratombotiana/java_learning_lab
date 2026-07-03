# How Backtracking Works

## N-Queens Trace (4x4)
`
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
`

## Subset Sum Trace (target=10)
`
[3, 5, 7, 2]
Include 3 → remaining 7
  Include 5 → remaining 2
    Include 7 → too big, backtrack
    Include 2 → remaining 0 ✓ Solution [3,5,2]
  Backtrack, try starting with 5...
`
"@

wf "INTERNALS.md" @"
# Backtracking — Internal Mechanics

## N-Queens
`java
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
`

## Sudoku Solver
`java
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
`
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
