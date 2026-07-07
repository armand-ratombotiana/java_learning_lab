package com.dsacademy.lab32.dlx;

public final class SudokuDLX implements ExactCoverProblem {

    private static final int SIZE = 9;
    private static final int BOX_SIZE = 3;
    private static final int COLS = SIZE * SIZE * 4;
    private static final int ROWS = SIZE * SIZE * SIZE;

    private final int[][] grid;
    private boolean[][] matrix;

    public SudokuDLX(int[][] grid) {
        if (grid == null || grid.length != SIZE) {
            throw new IllegalArgumentException("Grid must be 9x9");
        }
        this.grid = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                this.grid[r][c] = grid[r][c];
            }
        }
        buildMatrix();
    }

    private void buildMatrix() {
        matrix = new boolean[ROWS][COLS];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                for (int v = 0; v < SIZE; v++) {
                    int row = r * SIZE * SIZE + c * SIZE + v;
                    if (grid[r][c] != 0 && grid[r][c] != v + 1) {
                        for (int col = 0; col < COLS; col++) {
                            matrix[row][col] = false;
                        }
                        continue;
                    }
                    int cellCon = r * SIZE + c;
                    int rowCon = SIZE * SIZE + r * SIZE + v;
                    int colCon = 2 * SIZE * SIZE + c * SIZE + v;
                    int boxCon = 3 * SIZE * SIZE + (r / BOX_SIZE * BOX_SIZE + c / BOX_SIZE) * SIZE + v;
                    matrix[row][cellCon] = true;
                    matrix[row][rowCon] = true;
                    matrix[row][colCon] = true;
                    matrix[row][boxCon] = true;
                }
            }
        }
    }

    @Override
    public boolean[][] getExactCoverMatrix() {
        return matrix;
    }

    @Override
    public int[] getRowCounts() {
        int[] counts = new int[ROWS];
        for (int i = 0; i < ROWS; i++) {
            int cnt = 0;
            for (int j = 0; j < COLS; j++) {
                if (matrix[i][j]) cnt++;
            }
            counts[i] = cnt;
        }
        return counts;
    }

    @Override
    public String describeSolution(int[] rows) {
        int[][] result = new int[SIZE][SIZE];
        for (int row : rows) {
            int r = row / (SIZE * SIZE);
            int c = (row / SIZE) % SIZE;
            int v = row % SIZE;
            result[r][c] = v + 1;
        }
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < SIZE; r++) {
            if (r % BOX_SIZE == 0) sb.append("+-------+-------+-------+\n");
            for (int c = 0; c < SIZE; c++) {
                if (c % BOX_SIZE == 0) sb.append("| ");
                sb.append(result[r][c] == 0 ? ". " : result[r][c] + " ");
            }
            sb.append("|\n");
        }
        sb.append("+-------+-------+-------+\n");
        return sb.toString();
    }

    public int[][] solve() {
        DancingLinks dlx = new DancingLinks(matrix);
        if (dlx.solveOne()) {
            int[] rows = dlx.getSolution();
            int[][] result = new int[SIZE][SIZE];
            for (int row : rows) {
                int r = row / (SIZE * SIZE);
                int c = (row / SIZE) % SIZE;
                int v = row % SIZE;
                result[r][c] = v + 1;
            }
            return result;
        }
        return null;
    }

    public static int[][] parseGrid(String... lines) {
        int[][] g = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            String line = lines[r];
            for (int c = 0; c < SIZE; c++) {
                char ch = line.charAt(c);
                g[r][c] = (ch == '.' || ch == '0') ? 0 : (ch - '0');
            }
        }
        return g;
    }
}
