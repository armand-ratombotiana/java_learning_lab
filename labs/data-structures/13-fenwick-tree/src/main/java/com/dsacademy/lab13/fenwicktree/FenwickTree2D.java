package com.dsacademy.lab13.fenwicktree;

public class FenwickTree2D {

    private final int[][] bit;
    private final int rows, cols;

    public FenwickTree2D(int rows, int cols) {
        if (rows < 0 || cols < 0) throw new IllegalArgumentException("Dimensions must be non-negative");
        this.rows = rows;
        this.cols = cols;
        this.bit = new int[rows + 1][cols + 1];
    }

    public void add(int x, int y, int delta) {
        validateIndex(x, y);
        for (int i = x + 1; i <= rows; i += i & -i) {
            for (int j = y + 1; j <= cols; j += j & -j) {
                bit[i][j] += delta;
            }
        }
    }

    public int sum(int x, int y) {
        validateIndex(x, y);
        int result = 0;
        for (int i = x + 1; i > 0; i -= i & -i) {
            for (int j = y + 1; j > 0; j -= j & -j) {
                result += bit[i][j];
            }
        }
        return result;
    }

    public int rangeSum(int x1, int y1, int x2, int y2) {
        if (x1 > x2 || y1 > y2) throw new IllegalArgumentException("Invalid rectangle");
        return sum(x2, y2) - sum(x1 - 1, y2) - sum(x2, y1 - 1) + sum(x1 - 1, y1 - 1);
    }

    public void set(int x, int y, int val) {
        int current = rangeSum(x, y, x, y);
        add(x, y, val - current);
    }

    private void validateIndex(int x, int y) {
        if (x < 0 || x >= rows || y < 0 || y >= cols) {
            throw new IndexOutOfBoundsException("Point (" + x + ", " + y + ") out of bounds");
        }
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
}
