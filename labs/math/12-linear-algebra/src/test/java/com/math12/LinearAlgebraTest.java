package com.math12;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class LinearAlgebraTest {

    @Test
    void testAddVectors() {
        double[] a = {1, 2, 3}, b = {4, 5, 6};
        assertArrayEquals(new double[]{5, 7, 9}, LinearAlgebra.addVectors(a, b), 1e-12);
    }

    @Test
    void testSubtractVectors() {
        double[] a = {5, 7, 9}, b = {1, 2, 3};
        assertArrayEquals(new double[]{4, 5, 6}, LinearAlgebra.subtractVectors(a, b), 1e-12);
    }

    @Test
    void testScaleVector() {
        assertArrayEquals(new double[]{2, 4, 6}, LinearAlgebra.scaleVector(new double[]{1, 2, 3}, 2), 1e-12);
    }

    @Test
    void testDotProduct() {
        assertEquals(32, LinearAlgebra.dotProduct(new double[]{1, 2, 3}, new double[]{4, 5, 6}), 1e-12);
    }

    @Test
    void testVectorNorm() {
        assertEquals(5, LinearAlgebra.vectorNorm(new double[]{3, 4}), 1e-12);
    }

    @Test
    void testCrossProduct() {
        double[] a = {1, 0, 0}, b = {0, 1, 0};
        assertArrayEquals(new double[]{0, 0, 1}, LinearAlgebra.crossProduct(a, b), 1e-12);
    }

    @Test
    void testAddMatrices() {
        double[][] a = {{1, 2}, {3, 4}};
        double[][] b = {{5, 6}, {7, 8}};
        double[][] expected = {{6, 8}, {10, 12}};
        double[][] result = LinearAlgebra.addMatrices(a, b);
        for (int i = 0; i < 2; i++) assertArrayEquals(expected[i], result[i], 1e-12);
    }

    @Test
    void testMultiplyMatrices() {
        double[][] a = {{1, 2}, {3, 4}};
        double[][] b = {{5, 6}, {7, 8}};
        double[][] expected = {{19, 22}, {43, 50}};
        double[][] result = LinearAlgebra.multiplyMatrices(a, b);
        for (int i = 0; i < 2; i++) assertArrayEquals(expected[i], result[i], 1e-12);
    }

    @Test
    void testTranspose() {
        double[][] m = {{1, 2, 3}, {4, 5, 6}};
        double[][] expected = {{1, 4}, {2, 5}, {3, 6}};
        double[][] result = LinearAlgebra.transpose(m);
        for (int i = 0; i < 3; i++) assertArrayEquals(expected[i], result[i], 1e-12);
    }

    @Test
    void testDeterminant2x2() {
        assertEquals(-2, LinearAlgebra.determinant(new double[][]{{1, 2}, {3, 4}}), 1e-12);
    }

    @Test
    void testDeterminant3x3() {
        double[][] m = {{6, 1, 1}, {4, -2, 5}, {2, 8, 7}};
        assertEquals(-306, LinearAlgebra.determinant(m), 1e-10);
    }

    @Test
    void testInverse() {
        double[][] m = {{4, 7}, {2, 6}};
        double[][] inv = LinearAlgebra.inverse(m);
        double[][] identity = LinearAlgebra.multiplyMatrices(m, inv);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                double expected = (i == j) ? 1.0 : 0.0;
                assertEquals(expected, identity[i][j], 1e-10);
            }
        }
    }

    @Test
    void testSolveLinearSystem() {
        double[][] a = {{2, 3}, {1, -1}};
        double[] b = {7, 1};
        double[] x = LinearAlgebra.solveLinearSystem(a, b);
        assertArrayEquals(new double[]{2, 1}, x, 1e-10);
    }

    @Test
    void testPowerIteration() {
        double[][] matrix = {{2, 0}, {0, 3}};
        var result = LinearAlgebra.powerIteration(matrix, 100);
        assertEquals(3, result.eigenvalue(), 0.01);
    }

    @Test
    void testGramSchmidtQR() {
        double[][] m = {{1, 0}, {1, 1}};
        double[][] q = LinearAlgebra.gramSchmidtQR(m);
        double[][] qt = LinearAlgebra.transpose(q);
        double[][] qtq = LinearAlgebra.multiplyMatrices(qt, q);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                double expected = (i == j) ? 1.0 : 0.0;
                assertEquals(expected, qtq[i][j], 1e-10);
            }
        }
    }

    @Test
    void testMultiplyMatrixVector() {
        double[][] m = {{1, 2}, {3, 4}};
        double[] v = {5, 6};
        assertArrayEquals(new double[]{17, 39}, LinearAlgebra.multiplyMatrixVector(m, v), 1e-12);
    }

    @Test
    void testDimensionMismatch() {
        assertThrows(IllegalArgumentException.class, () -> LinearAlgebra.addVectors(new double[2], new double[3]));
    }

    @Test
    void testSingularMatrix() {
        double[][] singular = {{1, 2}, {2, 4}};
        assertThrows(ArithmeticException.class, () -> LinearAlgebra.inverse(singular));
    }

    @Test
    void testIdentity() {
        double[][] I = LinearAlgebra.identity(3);
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                assertEquals(i == j ? 1 : 0, I[i][j], 1e-12);
    }
}
