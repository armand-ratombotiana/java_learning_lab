package com.algorithms.linearprogramming;

/**
 * Custom: Linear Programming Concepts
 * Simplex method for solving linear optimization problems.
 *
 * This is a conceptual demo — full simplex is complex.
 * Focus: Recognizing LP problems and applying dual simplex.
 *
 * Time Complexity: O(2^n) worst case, O(n) average for practical problems
 * Space Complexity: O(n*m)
 */
public class LinearProgramming {

    public double[] simplex(double[][] A, double[] b, double[] c) {
        int m = A.length, n = c.length;
        double[][] tableau = new double[m + 1][n + m + 1];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) tableau[i][j] = A[i][j];
            tableau[i][n + i] = 1;
            tableau[i][n + m] = b[i];
        }
        for (int j = 0; j < n; j++) tableau[m][j] = -c[j];

        while (true) {
            int pivotCol = -1;
            for (int j = 0; j < n + m; j++) {
                if (tableau[m][j] < 0) { pivotCol = j; break; }
            }
            if (pivotCol == -1) break;

            int pivotRow = -1;
            double minRatio = Double.MAX_VALUE;
            for (int i = 0; i < m; i++) {
                if (tableau[i][pivotCol] > 0) {
                    double ratio = tableau[i][n + m] / tableau[i][pivotCol];
                    if (ratio < minRatio) { minRatio = ratio; pivotRow = i; }
                }
            }
            if (pivotRow == -1) return null;

            double pivot = tableau[pivotRow][pivotCol];
            for (int j = 0; j <= n + m; j++) tableau[pivotRow][j] /= pivot;
            for (int i = 0; i <= m; i++) {
                if (i != pivotRow) {
                    double factor = tableau[i][pivotCol];
                    for (int j = 0; j <= n + m; j++) tableau[i][j] -= factor * tableau[pivotRow][j];
                }
            }
        }

        double[] result = new double[n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (Math.abs(tableau[i][j] - 1) < 1e-9) {
                    boolean isBasic = true;
                    for (int k = 0; k < m; k++) if (k != i && Math.abs(tableau[k][j]) > 1e-9) { isBasic = false; break; }
                    if (isBasic) result[j] = tableau[i][n + m];
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        LinearProgramming lp = new LinearProgramming();
        // maximize 3x + 2y subject to x + y <= 4, 2x + y <= 6, x, y >= 0
        double[][] A = { { 1, 1 }, { 2, 1 } };
        double[] b = { 4, 6 };
        double[] c = { 3, 2 };
        double[] sol = lp.simplex(A, b, c);
        System.out.println("LP solution: x=" + sol[0] + ", y=" + sol[1]);
        System.out.println("Max value: " + (3 * sol[0] + 2 * sol[1]) + " (expected: 10)");
    }
}
