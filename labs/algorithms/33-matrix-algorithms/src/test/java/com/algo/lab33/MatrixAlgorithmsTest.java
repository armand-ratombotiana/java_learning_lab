package com.algo.lab33;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MatrixAlgorithmsTest {

    @Test
    void testStrassenMultiply() {
        double[][] A = {{1, 2}, {3, 4}};
        double[][] B = {{5, 6}, {7, 8}};
        double[][] C = StrassenMultiply.multiply(A, B);
        assertEquals(19, C[0][0], 1e-9);
        assertEquals(22, C[0][1], 1e-9);
        assertEquals(43, C[1][0], 1e-9);
        assertEquals(50, C[1][1], 1e-9);
    }

    @Test
    void testGaussianElimination() {
        double[][] A = {{2, 1, -1}, {1, 3, 2}, {3, -1, 1}};
        double[] b = {2, 10, 5};
        double[] x = GaussianElimination.solve(A, b);
        assertNotNull(x);
        assertEquals(1, x[2], 1e-6);
    }

    @Test
    void testDeterminant() {
        double[][] A = {{1, 2}, {3, 4}};
        assertEquals(-2, MatrixDecomposition.determinant(A), 1e-9);
    }

    @Test
    void testInverse() {
        double[][] A = {{1, 2}, {3, 4}};
        double[][] inv = MatrixDecomposition.inverse(A);
        assertEquals(1, inv[0][0] * A[0][0] + inv[0][1] * A[1][0], 1e-6);
    }

    @Test
    void testLU() {
        double[][] A = {{4, 3}, {6, 3}};
        MatrixDecomposition.LUResult lu = MatrixDecomposition.lu(A);
        assertNotNull(lu);
    }
}
