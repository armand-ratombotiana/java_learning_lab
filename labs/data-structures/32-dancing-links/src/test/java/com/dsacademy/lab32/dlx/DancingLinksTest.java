package com.dsacademy.lab32.dlx;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

public class DancingLinksTest {

    private boolean[][] simpleMatrix;

    @BeforeEach
    void setUp() {
        simpleMatrix = new boolean[][] {
            { true, false, false, true },
            { true, true, false, false },
            { false, true, true, false },
            { false, false, true, true }
        };
    }

    @Test
    void testDancingLinksSimple() {
        DancingLinks dlx = new DancingLinks(simpleMatrix);
        assertTrue(dlx.solveOne());
    }

    @Test
    void testEmptyMatrix() {
        assertThrows(IllegalArgumentException.class, () -> new DancingLinks(new boolean[0][0]));
    }

    @Test
    void testNoSolution() {
        boolean[][] unsat = new boolean[][] {
            { true, false },
            { true, false },
            { false, true }
        };
        DancingLinks dlx = new DancingLinks(unsat);
        assertFalse(dlx.solveOne());
    }

    @Test
    void testSudokuSolveEasy() {
        int[][] grid = SudokuDLX.parseGrid(
            "53..7....",
            "6..195...",
            ".98....6.",
            "8...6...3",
            "4..8.3..1",
            "7...2...6",
            ".6....28.",
            "...419..5",
            "....8..79"
        );
        SudokuDLX sudoku = new SudokuDLX(grid);
        int[][] solution = sudoku.solve();
        assertNotNull(solution);
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                assertTrue(solution[r][c] >= 1 && solution[r][c] <= 9);
            }
        }
        validateSudoku(solution);
    }

    @Test
    void testSudokuSolveHard() {
        int[][] grid = SudokuDLX.parseGrid(
            "....6....",
            "...29....",
            "71..8....",
            "....3.6..",
            "..37...1.",
            ".5......8",
            "6.....4.5",
            "....1....",
            "...2....."
        );
        SudokuDLX sudoku = new SudokuDLX(grid);
        int[][] solution = sudoku.solve();
        assertNotNull(solution);
        validateSudoku(solution);
    }

    @Test
    void testSudokuSolveAlreadyComplete() {
        int[][] grid = new int[][] {
            {5,3,4,6,7,8,9,1,2},
            {6,7,2,1,9,5,3,4,8},
            {1,9,8,3,4,2,5,6,7},
            {8,5,9,7,6,1,4,2,3},
            {4,2,6,8,5,3,7,9,1},
            {7,1,3,9,2,4,8,5,6},
            {9,6,1,5,3,7,2,8,4},
            {2,8,7,4,1,9,6,3,5},
            {3,4,5,2,8,6,1,7,9}
        };
        SudokuDLX sudoku = new SudokuDLX(grid);
        int[][] solution = sudoku.solve();
        assertNotNull(solution);
        validateSudoku(solution);
    }

    @Test
    void testSudokuInvalidInputGrid() {
        assertThrows(IllegalArgumentException.class, () -> new SudokuDLX(new int[3][3]));
    }

    @Test
    void testExactCoverMatrixHasCorrectShape() {
        SudokuDLX sudoku = new SudokuDLX(new int[9][9]);
        boolean[][] matrix = sudoku.getExactCoverMatrix();
        assertEquals(729, matrix.length);
        assertEquals(324, matrix[0].length);
    }

    @Test
    void testDescribeSolution() {
        SudokuDLX sudoku = new SudokuDLX(new int[9][9]);
        String desc = sudoku.describeSolution(new int[]{0, 10, 20});
        assertNotNull(desc);
        assertTrue(desc.contains("+"));
    }

    @Test
    void testSolveAll() {
        boolean[][] matrix = new boolean[][] {
            { true, false },
            { false, true }
        };
        DancingLinks dlx = new DancingLinks(matrix);
        int[][] allSolutions = dlx.solveAll();
        assertEquals(1, allSolutions.length);
    }

    private void validateSudoku(int[][] grid) {
        for (int r = 0; r < 9; r++) {
            boolean[] seen = new boolean[10];
            for (int c = 0; c < 9; c++) {
                int v = grid[r][c];
                assertFalse(seen[v], "Row " + r + " has duplicate " + v);
                seen[v] = true;
            }
        }
        for (int c = 0; c < 9; c++) {
            boolean[] seen = new boolean[10];
            for (int r = 0; r < 9; r++) {
                int v = grid[r][c];
                assertFalse(seen[v], "Col " + c + " has duplicate " + v);
                seen[v] = true;
            }
        }
        for (int br = 0; br < 3; br++) {
            for (int bc = 0; bc < 3; bc++) {
                boolean[] seen = new boolean[10];
                for (int r = br * 3; r < br * 3 + 3; r++) {
                    for (int c = bc * 3; c < bc * 3 + 3; c++) {
                        int v = grid[r][c];
                        assertFalse(seen[v], "Box " + br + "," + bc + " has duplicate " + v);
                        seen[v] = true;
                    }
                }
            }
        }
    }
}
