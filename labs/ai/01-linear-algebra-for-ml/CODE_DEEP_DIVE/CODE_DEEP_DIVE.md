# Linear Algebra - Code Deep Dive in Java

## Table of Contents
1. [Vector Implementation](#1-vector-implementation)
2. [Matrix Implementation](#2-matrix-implementation)
3. [Matrix Operations](#3-matrix-operations)
4. [Solving Linear Systems](#4-solving-linear-systems)
5. [Eigenvalue Computation](#5-eigenvalue-computation)
6. [SVD Implementation](#6-svd-implementation)
7. [Advanced Operations](#7-advanced-operations)
8. [ML-Specific Implementations](#8-ml-specific-implementations)

---

## 1. Vector Implementation

### 1.1 Basic Vector Class

```java
package com.ml.la.vector;

import java.util.Arrays;
import java.util.Objects;

public class Vector {
    private final double[] data;
    private final int size;

    public Vector(double... values) {
        this.data = Arrays.copyOf(values, values.length);
        this.size = values.length;
    }

    public static Vector zeros(int size) {
        return new Vector(new double[size]);
    }

    public static Vector ones(int size) {
        double[] ones = new double[size];
        Arrays.fill(ones, 1.0);
        return new Vector(ones);
    }

    public static Vector random(int size) {
        double[] rand = new double[size];
        java.util.Random gen = new java.util.Random();
        for (int i = 0; i < size; i++) {
            rand[i] = gen.nextGaussian();
        }
        return new Vector(rand);
    }

    public static Vector random(int size, long seed) {
        java.util.Random gen = new java.util.Random(seed);
        double[] rand = new double[size];
        for (int i = 0; i < size; i++) {
            rand[i] = gen.nextGaussian();
        }
        return new Vector(rand);
    }

    public int size() {
        return size;
    }

    public double get(int index) {
        return data[index];
    }

    public Vector set(int index, double value) {
        double[] copy = Arrays.copyOf(data, size);
        copy[index] = value;
        return new Vector(copy);
    }

    public double[] toArray() {
        return Arrays.copyOf(data, size);
    }
}
```

### 1.2 Vector Operations

```java
package com.ml.la.vector;

public class VectorOperations {

    public static Vector add(Vector a, Vector b) {
        if (a.size() != b.size()) {
            throw new IllegalArgumentException("Dimension mismatch");
        }
        double[] result = new double[a.size()];
        for (int i = 0; i < a.size(); i++) {
            result[i] = a.get(i) + b.get(i);
        }
        return new Vector(result);
    }

    public static Vector subtract(Vector a, Vector b) {
        if (a.size() != b.size()) {
            throw new IllegalArgumentException("Dimension mismatch");
        }
        double[] result = new double[a.size()];
        for (int i = 0; i < a.size(); i++) {
            result[i] = a.get(i) - b.get(i);
        }
        return new Vector(result);
    }

    public static Vector scale(Vector v, double scalar) {
        double[] result = new double[v.size()];
        for (int i = 0; i < v.size(); i++) {
            result[i] = v.get(i) * scalar;
        }
        return new Vector(result);
    }

    public static double dot(Vector a, Vector b) {
        if (a.size() != b.size()) {
            throw new IllegalArgumentException("Dimension mismatch");
        }
        double sum = 0;
        for (int i = 0; i < a.size(); i++) {
            sum += a.get(i) * b.get(i);
        }
        return sum;
    }

    public static double norm(Vector v) {
        return Math.sqrt(dot(v, v));
    }

    public static double normL1(Vector v) {
        double sum = 0;
        for (int i = 0; i < v.size(); i++) {
            sum += Math.abs(v.get(i));
        }
        return sum;
    }

    public static double normLInf(Vector v) {
        double max = 0;
        for (int i = 0; i < v.size(); i++) {
            max = Math.max(max, Math.abs(v.get(i)));
        }
        return max;
    }

    public static Vector normalize(Vector v) {
        double n = norm(v);
        if (n == 0) {
            throw new ArithmeticException("Cannot normalize zero vector");
        }
        return scale(v, 1.0 / n);
    }

    public static Vector elementwiseMultiply(Vector a, Vector b) {
        if (a.size() != b.size()) {
            throw new IllegalArgumentException("Dimension mismatch");
        }
        double[] result = new double[a.size()];
        for (int i = 0; i < a.size(); i++) {
            result[i] = a.get(i) * b.get(i);
        }
        return new Vector(result);
    }

    public static Vector elementwiseDivide(Vector a, Vector b) {
        if (a.size() != b.size()) {
            throw new IllegalArgumentException("Dimension mismatch");
        }
        double[] result = new double[a.size()];
        for (int i = 0; i < a.size(); i++) {
            result[i] = a.get(i) / b.get(i);
        }
        return new Vector(result);
    }

    public static Vector applyFunction(Vector v, java.util.function.DoubleUnaryOperator f) {
        double[] result = new double[v.size()];
        for (int i = 0; i < v.size(); i++) {
            result[i] = f.applyAsDouble(v.get(i));
        }
        return new Vector(result);
    }
}
```

### 1.3 Advanced Vector Operations

```java
package com.ml.la.vector;

public class AdvancedVectorOps {

    public static double cosineSimilarity(Vector a, Vector b) {
        double dotProd = VectorOperations.dot(a, b);
        double normProd = VectorOperations.norm(a) * VectorOperations.norm(b);
        return dotProd / normProd;
    }

    public static double euclideanDistance(Vector a, Vector b) {
        return VectorOperations.norm(VectorOperations.subtract(a, b));
    }

    public static double manhattanDistance(Vector a, Vector b) {
        return VectorOperations.normL1(VectorOperations.subtract(a, b));
    }

    public static Vector crossProduct(Vector a, Vector b) {
        if (a.size() != 3 || b.size() != 3) {
            throw new IllegalArgumentException("Cross product only defined for 3D vectors");
        }
        return new Vector(
            a.get(1) * b.get(2) - a.get(2) * b.get(1),
            a.get(2) * b.get(0) - a.get(0) * b.get(2),
            a.get(0) * b.get(1) - a.get(1) * b.get(0)
        );
    }

    public static double angleBetween(Vector a, Vector b) {
        double cosTheta = cosineSimilarity(a, b);
        return Math.toDegrees(Math.acos(Math.max(-1, Math.min(1, cosTheta))));
    }

    public static Vector projection(Vector a, Vector b) {
        double dotProd = VectorOperations.dot(a, b);
        double bNormSq = VectorOperations.dot(b, b);
        double scalar = dotProd / bNormSq;
        return VectorOperations.scale(b, scalar);
    }

    public static Vector rejection(Vector a, Vector b) {
        return VectorOperations.subtract(a, projection(a, b));
    }

    public static Vector hadamardProduct(Vector a, Vector b) {
        return VectorOperations.elementwiseMultiply(a, b);
    }

    public static double sum(Vector v) {
        double sum = 0;
        for (int i = 0; i < v.size(); i++) {
            sum += v.get(i);
        }
        return sum;
    }

    public static double mean(Vector v) {
        return sum(v) / v.size();
    }

    public static double variance(Vector v) {
        double m = mean(v);
        double sumSq = 0;
        for (int i = 0; i < v.size(); i++) {
            double diff = v.get(i) - m;
            sumSq += diff * diff;
        }
        return sumSq / v.size();
    }

    public static double std(Vector v) {
        return Math.sqrt(variance(v));
    }

    public static Vector center(Vector v) {
        double m = mean(v);
        return VectorOperations.applyFunction(v, x -> x - m);
    }

    public static Vector standardize(Vector v) {
        double m = mean(v);
        double s = std(v);
        return VectorOperations.applyFunction(v, x -> (x - m) / s);
    }
}
```

---

## 2. Matrix Implementation

### 2.1 Basic Matrix Class

```java
package com.ml.la.matrix;

import com.ml.la.vector.Vector;

public class Matrix {
    private final double[][] data;
    private final int rows;
    private final int cols;

    public Matrix(double[][] data) {
        this.data = copyOf(data);
        this.rows = data.length;
        this.cols = data[0].length;
    }

    public Matrix(int rows, int cols) {
        this.data = new double[rows][cols];
        this.rows = rows;
        this.cols = cols;
    }

    public static Matrix of(double[][] data) {
        return new Matrix(data);
    }

    public static Matrix identity(int n) {
        Matrix m = new Matrix(n, n);
        for (int i = 0; i < n; i++) {
            m.data[i][i] = 1.0;
        }
        return m;
    }

    public static Matrix zeros(int rows, int cols) {
        return new Matrix(rows, cols);
    }

    public static Matrix ones(int rows, int cols) {
        Matrix m = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m.data[i][j] = 1.0;
            }
        }
        return m;
    }

    public static Matrix random(int rows, int cols) {
        java.util.Random gen = new java.util.Random();
        Matrix m = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m.data[i][j] = gen.nextGaussian();
            }
        }
        return m;
    }

    public static Matrix random(int rows, int cols, long seed) {
        java.util.Random gen = new java.util.Random(seed);
        Matrix m = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m.data[i][j] = gen.nextGaussian();
            }
        }
        return m;
    }

    public static Matrix diagonal(double... values) {
        int n = values.length;
        Matrix m = new Matrix(n, n);
        for (int i = 0; i < n; i++) {
            m.data[i][i] = values[i];
        }
        return m;
    }

    public int rows() {
        return rows;
    }

    public int cols() {
        return cols;
    }

    public double get(int row, int col) {
        return data[row][col];
    }

    public Matrix set(int row, int col, double value) {
        Matrix copy = this.copy();
        copy.data[row][col] = value;
        return copy;
    }

    public Vector getRow(int row) {
        return new Vector(data[row]);
    }

    public Vector getCol(int col) {
        double[] colData = new double[rows];
        for (int i = 0; i < rows; i++) {
            colData[i] = data[i][col];
        }
        return new Vector(colData);
    }

    public double[][] toArray() {
        return copyOf(data);
    }

    private double[][] copyOf(double[][] arr) {
        double[][] copy = new double[arr.length][];
        for (int i = 0; i < arr.length; i++) {
            copy[i] = Arrays.copyOf(arr[i], arr[i].length);
        }
        return copy;
    }

    public Matrix copy() {
        return new Matrix(data);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Matrix[\n");
        for (int i = 0; i < rows; i++) {
            sb.append("  [");
            for (int j = 0; j < cols; j++) {
                sb.append(String.format("%8.4f", data[i][j]));
                if (j < cols - 1) sb.append(", ");
            }
            sb.append("]\n");
        }
        sb.append("]");
        return sb.toString();
    }
}
```

### 2.2 Matrix Operations

```java
package com.ml.la.matrix;

import com.ml.la.vector.Vector;

public class MatrixOperations {

    public static Matrix add(Matrix a, Matrix b) {
        if (a.rows() != b.rows() || a.cols() != b.cols()) {
            throw new IllegalArgumentException("Dimension mismatch");
        }
        Matrix result = new Matrix(a.rows(), a.cols());
        for (int i = 0; i < a.rows(); i++) {
            for (int j = 0; j < a.cols(); j++) {
                result.data[i][j] = a.get(i, j) + b.get(i, j);
            }
        }
        return result;
    }

    public static Matrix subtract(Matrix a, Matrix b) {
        if (a.rows() != b.rows() || a.cols() != b.cols()) {
            throw new IllegalArgumentException("Dimension mismatch");
        }
        Matrix result = new Matrix(a.rows(), a.cols());
        for (int i = 0; i < a.rows(); i++) {
            for (int j = 0; j < a.cols(); j++) {
                result.data[i][j] = a.get(i, j) - b.get(i, j);
            }
        }
        return result;
    }

    public static Matrix scale(Matrix m, double scalar) {
        Matrix result = new Matrix(m.rows(), m.cols());
        for (int i = 0; i < m.rows(); i++) {
            for (int j = 0; j < m.cols(); j++) {
                result.data[i][j] = m.get(i, j) * scalar;
            }
        }
        return result;
    }

    public static Matrix transpose(Matrix m) {
        Matrix result = new Matrix(m.cols(), m.rows());
        for (int i = 0; i < m.rows(); i++) {
            for (int j = 0; j < m.cols(); j++) {
                result.data[j][i] = m.get(i, j);
            }
        }
        return result;
    }

    public static Matrix multiply(Matrix a, Matrix b) {
        if (a.cols() != b.rows()) {
            throw new IllegalArgumentException("Dimension mismatch for multiplication");
        }
        Matrix result = new Matrix(a.rows(), b.cols());
        for (int i = 0; i < a.rows(); i++) {
            for (int j = 0; j < b.cols(); j++) {
                double sum = 0;
                for (int k = 0; k < a.cols(); k++) {
                    sum += a.get(i, k) * b.get(k, j);
                }
                result.data[i][j] = sum;
            }
        }
        return result;
    }

    public static Vector multiply(Matrix m, Vector v) {
        if (m.cols() != v.size()) {
            throw new IllegalArgumentException("Dimension mismatch");
        }
        double[] result = new double[m.rows()];
        for (int i = 0; i < m.rows(); i++) {
            double sum = 0;
            for (int j = 0; j < m.cols(); j++) {
                sum += m.get(i, j) * v.get(j);
            }
            result[i] = sum;
        }
        return new Vector(result);
    }

    public static Vector multiply(Vector v, Matrix m) {
        if (v.size() != m.rows()) {
            throw new IllegalArgumentException("Dimension mismatch");
        }
        double[] result = new double[m.cols()];
        for (int j = 0; j < m.cols(); j++) {
            double sum = 0;
            for (int i = 0; i < m.rows(); i++) {
                sum += v.get(i) * m.get(i, j);
            }
            result[j] = sum;
        }
        return new Vector(result);
    }

    public static Matrix elementwiseMultiply(Matrix a, Matrix b) {
        if (a.rows() != b.rows() || a.cols() != b.cols()) {
            throw new IllegalArgumentException("Dimension mismatch");
        }
        Matrix result = new Matrix(a.rows(), a.cols());
        for (int i = 0; i < a.rows(); i++) {
            for (int j = 0; j < a.cols(); j++) {
                result.data[i][j] = a.get(i, j) * b.get(i, j);
            }
        }
        return result;
    }

    public static Matrix hadamardProduct(Matrix a, Matrix b) {
        return elementwiseMultiply(a, b);
    }

    public static Matrix outerProduct(Vector a, Vector b) {
        Matrix result = new Matrix(a.size(), b.size());
        for (int i = 0; i < a.size(); i++) {
            for (int j = 0; j < b.size(); j++) {
                result.data[i][j] = a.get(i) * b.get(j);
            }
        }
        return result;
    }

    public static Matrix applyFunction(Matrix m, java.util.function.DoubleUnaryOperator f) {
        Matrix result = new Matrix(m.rows(), m.cols());
        for (int i = 0; i < m.rows(); i++) {
            for (int j = 0; j < m.cols(); j++) {
                result.data[i][j] = f.applyAsDouble(m.get(i, j));
            }
        }
        return result;
    }
}
```

### 2.3 Advanced Matrix Operations

```java
package com.ml.la.matrix;

import com.ml.la.vector.Vector;

public class AdvancedMatrixOps {

    public static double trace(Matrix m) {
        int n = Math.min(m.rows(), m.cols());
        double sum = 0;
        for (int i = 0; i < n; i++) {
            sum += m.get(i, i);
        }
        return sum;
    }

    public static double determinant(Matrix m) {
        if (m.rows() != m.cols()) {
            throw new IllegalArgumentException("Matrix must be square");
        }
        int n = m.rows();
        
        if (n == 1) return m.get(0, 0);
        if (n == 2) {
            return m.get(0, 0) * m.get(1, 1) - m.get(0, 1) * m.get(1, 0);
        }
        
        double det = 0;
        for (int j = 0; j < n; j++) {
            det += Math.pow(-1, j) * m.get(0, j) * determinant(minor(m, 0, j));
        }
        return det;
    }

    public static Matrix minor(Matrix m, int row, int col) {
        int n = m.rows();
        double[][] sub = new double[n-1][n-1];
        int ri = 0;
        for (int i = 0; i < n; i++) {
            if (i == row) continue;
            int ci = 0;
            for (int j = 0; j < n; j++) {
                if (j == col) continue;
                sub[ri][ci] = m.get(i, j);
                ci++;
            }
            ri++;
        }
        return new Matrix(sub);
    }

    public static Matrix inverse(Matrix m) {
        if (m.rows() != m.cols()) {
            throw new IllegalArgumentException("Matrix must be square");
        }
        int n = m.rows();
        
        double det = determinant(m);
        if (Math.abs(det) < 1e-10) {
            throw new ArithmeticException("Matrix is singular (non-invertible)");
        }
        
        if (n == 1) {
            return new Matrix(new double[][]{{1.0 / m.get(0, 0)}});
        }
        
        Matrix adj = adjugate(m);
        return scale(adj, 1.0 / det);
    }

    public static Matrix adjugate(Matrix m) {
        int n = m.rows();
        Matrix result = new Matrix(n, n);
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double sign = Math.pow(-1, i + j);
                double cofactor = sign * determinant(minor(m, i, j));
                result.data[j][i] = cofactor;
            }
        }
        return result;
    }

    public static Matrix power(Matrix m, int p) {
        if (m.rows() != m.cols()) {
            throw new IllegalArgumentException("Matrix must be square");
        }
        if (p < 0) {
            return power(inverse(m), -p);
        }
        if (p == 0) {
            return Matrix.identity(m.rows());
        }
        
        Matrix result = Matrix.identity(m.rows());
        Matrix base = m.copy();
        
        while (p > 0) {
            if (p % 2 == 1) {
                result = multiply(result, base);
            }
            base = multiply(base, base);
            p /= 2;
        }
        return result;
    }

    public static double frobeniusNorm(Matrix m) {
        double sum = 0;
        for (int i = 0; i < m.rows(); i++) {
            for (int j = 0; j < m.cols(); j++) {
                sum += m.get(i, j) * m.get(i, j);
            }
        }
        return Math.sqrt(sum);
    }

    public static double spectralNorm(Matrix m) {
        Matrix mt = transpose(m);
        Matrix mtm = multiply(mt, m);
        
        double[] eigenvalues = powerIterationAll(mtm, 1);
        return Math.sqrt(eigenvalues[0]);
    }

    public static Matrix blockDiagonal(Matrix... blocks) {
        int totalRows = 0;
        int totalCols = 0;
        for (Matrix b : blocks) {
            totalRows += b.rows();
            totalCols += b.cols();
        }
        
        Matrix result = new Matrix(totalRows, totalCols);
        int rowOffset = 0;
        int colOffset = 0;
        for (Matrix b : blocks) {
            for (int i = 0; i < b.rows(); i++) {
                for (int j = 0; j < b.cols(); j++) {
                    result.data[rowOffset + i][colOffset + j] = b.get(i, j);
                }
            }
            rowOffset += b.rows();
            colOffset += b.cols();
        }
        return result;
    }

    public static Matrix submatrix(Matrix m, int startRow, int endRow, int startCol, int endCol) {
        int rows = endRow - startRow;
        int cols = endCol - startCol;
        double[][] sub = new double[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sub[i][j] = m.get(startRow + i, startCol + j);
            }
        }
        return new Matrix(sub);
    }

    public static Matrix insertSubmatrix(Matrix target, Matrix source, int startRow, int startCol) {
        Matrix copy = target.copy();
        for (int i = 0; i < source.rows(); i++) {
            for (int j = 0; j < source.cols(); j++) {
                copy.data[startRow + i][startCol + j] = source.get(i, j);
            }
        }
        return copy;
    }
}
```

---

## 3. Solving Linear Systems

### 3.1 Gaussian Elimination

```java
package com.ml.la.matrix;

import com.ml.la.vector.Vector;

public class GaussianElimination {

    public static Vector solve(Matrix A, Vector b) {
        if (A.rows() != A.cols()) {
            throw new IllegalArgumentException("Matrix must be square");
        }
        if (A.rows() != b.size()) {
            throw new IllegalArgumentException("Dimension mismatch");
        }
        
        int n = A.rows();
        double[][] augmented = new double[n][n + 1];
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                augmented[i][j] = A.get(i, j);
            }
            augmented[i][n] = b.get(i);
        }
        
        for (int col = 0; col < n; col++) {
            int maxRow = findMaxRow(augmented, col, n);
            swapRows(augmented, col, maxRow);
            
            if (Math.abs(augmented[col][col]) < 1e-12) {
                throw new ArithmeticException("Matrix is singular");
            }
            
            for (int row = col + 1; row < n; row++) {
                double factor = augmented[row][col] / augmented[col][col];
                for (int j = col; j <= n; j++) {
                    augmented[row][j] -= factor * augmented[col][j];
                }
            }
        }
        
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            x[i] = augmented[i][n];
            for (int j = i + 1; j < n; j++) {
                x[i] -= augmented[i][j] * x[j];
            }
            x[i] /= augmented[i][i];
        }
        
        return new Vector(x);
    }

    private static int findMaxRow(double[][] augmented, int col, int n) {
        int maxRow = col;
        for (int i = col + 1; i < n; i++) {
            if (Math.abs(augmented[i][col]) > Math.abs(augmented[maxRow][col])) {
                maxRow = i;
            }
        }
        return maxRow;
    }

    private static void swapRows(double[][] augmented, int row1, int row2) {
        if (row1 == row2) return;
        double[] temp = augmented[row1];
        augmented[row1] = augmented[row2];
        augmented[row2] = temp;
    }

    public static double[] solveDouble(Matrix A, double[] b) {
        return solve(A, new Vector(b)).toArray();
    }
}
```

### 3.2 LU Decomposition

```java
package com.ml.la.matrix;

import com.ml.la.vector.Vector;

public class LUDecomposition {
    private final Matrix L;
    private final Matrix U;
    private final int[] piv;

    public LUDecomposition(Matrix A) {
        int n = A.rows();
        if (A.cols() != n) {
            throw new IllegalArgumentException("Matrix must be square");
        }
        
        L = new Matrix(n, n);
        U = new Matrix(n, n);
        piv = new int[n];
        
        double[][] Udata = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Udata[i][j] = A.get(i, j);
            }
        }
        
        for (int i = 0; i < n; i++) {
            piv[i] = i;
        }
        
        for (int col = 0; col < n; col++) {
            int maxRow = col;
            for (int i = col + 1; i < n; i++) {
                if (Math.abs(Udata[i][col]) > Math.abs(Udata[maxRow][col])) {
                    maxRow = i;
                }
            }
            
            if (Math.abs(Udata[maxRow][col]) < 1e-12) {
                continue;
            }
            
            if (maxRow != col) {
                double[] temp = Udata[col];
                Udata[col] = Udata[maxRow];
                Udata[maxRow] = temp;
                
                int tempPiv = piv[col];
                piv[col] = piv[maxRow];
                piv[maxRow] = tempPiv;
            }
            
            for (int i = col + 1; i < n; i++) {
                Udata[i][col] /= Udata[col][col];
                L.data[i][col] = Udata[i][col];
                
                for (int j = col + 1; j < n; j++) {
                    Udata[i][j] -= Udata[i][col] * Udata[col][j];
                }
            }
        }
        
        for (int i = 0; i < n; i++) {
            L.data[i][i] = 1.0;
            for (int j = 0; j < n; j++) {
                U.data[i][j] = Udata[i][j];
            }
        }
    }

    public Matrix getL() {
        return L;
    }

    public Matrix getU() {
        return U;
    }

    public int[] getPivot() {
        return piv;
    }

    public double determinant() {
        double det = 1;
        for (int i = 0; i < U.rows(); i++) {
            det *= U.get(i, i);
        }
        
        int swaps = 0;
        for (int i = 0; i < piv.length; i++) {
            if (piv[i] != i) swaps++;
        }
        
        return (swaps % 2 == 0) ? det : -det;
    }

    public Vector solve(Vector b) {
        int n = L.rows();
        
        double[] pb = new double[n];
        for (int i = 0; i < n; i++) {
            pb[i] = b.get(piv[i]);
        }
        
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            y[i] = pb[i];
            for (int j = 0; j < i; j++) {
                y[i] -= L.get(i, j) * y[j];
            }
        }
        
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            x[i] = y[i];
            for (int j = i + 1; j < n; j++) {
                x[i] -= U.get(i, j) * x[j];
            }
            x[i] /= U.get(i, i);
        }
        
        return new Vector(x);
    }

    public Matrix inverse() {
        int n = L.rows();
        Matrix inv = new Matrix(n, n);
        
        Vector e = new Vector(new double[n]);
        for (int i = 0; i < n; i++) {
            e.data[i] = 1.0;
            Vector col = solve(e);
            for (int j = 0; j < n; j++) {
                inv.data[j][i] = col.get(j);
            }
            e.data[i] = 0.0;
        }
        
        return inv;
    }
}
```

### 3.3 Cholesky Decomposition (SPD matrices)

```java
package com.ml.la.matrix;

import com.ml.la.vector.Vector;

public class CholeskyDecomposition {
    private final Matrix L;

    public CholeskyDecomposition(Matrix A) {
        int n = A.rows();
        if (A.cols() != n) {
            throw new IllegalArgumentException("Matrix must be square");
        }
        
        L = new Matrix(n, n);
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= i; j++) {
                double sum = 0;
                
                if (j == i) {
                    for (int k = 0; k < j; k++) {
                        sum += L.get(j, k) * L.get(j, k);
                    }
                    double val = A.get(j, j) - sum;
                    if (val <= 0) {
                        throw new ArithmeticException("Matrix is not positive definite");
                    }
                    L.data[j][j] = Math.sqrt(val);
                } else {
                    for (int k = 0; k < j; k++) {
                        sum += L.get(i, k) * L.get(j, k);
                    }
                    L.data[i][j] = (A.get(i, j) - sum) / L.get(j, j);
                }
            }
        }
    }

    public Matrix getL() {
        return L;
    }

    public Matrix getLT() {
        return MatrixOperations.transpose(L);
    }

    public Vector solve(Vector b) {
        int n = L.rows();
        
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            y[i] = b.get(i);
            for (int j = 0; j < i; j++) {
                y[i] -= L.get(i, j) * y[j];
            }
            y[i] /= L.get(i, i);
        }
        
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            x[i] = y[i];
            for (int j = i + 1; j < n; j++) {
                x[i] -= L.get(j, i) * x[j];
            }
            x[i] /= L.get(i, i);
        }
        
        return new Vector(x);
    }
}
```

---

## 4. Eigenvalue Computation

### 4.1 Power Iteration

```java
package com.ml.la.matrix;

import com.ml.la.vector.Vector;

public class PowerIteration {

    public static double[] dominantEigenvalue(Matrix A, int maxIter, double tol) {
        int n = A.rows();
        Vector v = VectorOperations.normalize(Vector.random(n));
        
        double eigenvalue = 0;
        
        for (int iter = 0; iter < maxIter; iter++) {
            Vector Av = MatrixOperations.multiply(A, v);
            double newEigenvalue = VectorOperations.dot(v, Av);
            
            v = VectorOperations.normalize(Av);
            
            if (Math.abs(newEigenvalue - eigenvalue) < tol) {
                eigenvalue = newEigenvalue;
                break;
            }
            eigenvalue = newEigenvalue;
        }
        
        return new double[]{eigenvalue, 0};
    }

    public static Vector dominantEigenvector(Matrix A, int maxIter, double tol) {
        int n = A.rows();
        Vector v = VectorOperations.normalize(Vector.random(n));
        
        for (int iter = 0; iter < maxIter; iter++) {
            Vector Av = MatrixOperations.multiply(A, v);
            Vector newV = VectorOperations.normalize(Av);
            
            double diff = 0;
            for (int i = 0; i < n; i++) {
                diff += Math.abs(newV.get(i) - v.get(i));
            }
            
            v = newV;
            
            if (diff < tol) {
                break;
            }
        }
        
        return v;
    }
}
```

### 4.2 QR Algorithm

```java
package com.ml.la.matrix;

import com.ml.la.vector.Vector;

public class QREigenvalue {
    
    public static double[] computeAll(Matrix A, int maxIter, double tol) {
        int n = A.rows();
        Matrix Ak = A.copy();
        
        for (int iter = 0; iter < maxIter; iter++) {
            QRResult qr = qrDecomposition(Ak);
            Ak = MatrixOperations.multiply(qr.R, qr.Q);
            
            if (isQuasiTriangular(Ak, tol)) {
                break;
            }
        }
        
        double[] eigenvalues = new double[n];
        for (int i = 0; i < n; i++) {
            if (i == n - 1 || Math.abs(Ak.get(i + 1, i)) < tol) {
                eigenvalues[i] = Ak.get(i, i);
            } else {
                double a = Ak.get(i, i);
                double b = Ak.get(i, i + 1);
                double c = Ak.get(i + 1, i);
                double d = Ak.get(i + 1, i + 1);
                
                double trace = a + d;
                double det = a * d - b * c;
                double disc = trace * trace - 4 * det;
                
                eigenvalues[i] = (trace + Math.sqrt(disc)) / 2;
                eigenvalues[i + 1] = (trace - Math.sqrt(disc)) / 2;
                i++;
            }
        }
        
        return eigenvalues;
    }

    private static QRResult qrDecomposition(Matrix A) {
        int m = A.rows();
        int n = A.cols();
        
        Matrix Q = Matrix.identity(m);
        Matrix R = A.copy();
        
        for (int j = 0; j < Math.min(n, m - 1); j++) {
            for (int i = j + 1; i < m; i++) {
                double a = R.get(j, j);
                double b = R.get(i, j);
                
                if (Math.abs(b) < 1e-12) continue;
                
                double r = Math.sqrt(a * a + b * b);
                double c = a / r;
                double s = -b / r;
                
                for (int k = j; k < n; k++) {
                    double rjk = R.get(j, k);
                    double rik = R.get(i, k);
                    R.data[j][k] = c * rjk - s * rik;
                    R.data[i][k] = s * rjk + c * rik;
                }
                
                for (int k = 0; k < m; k++) {
                    double qjk = Q.get(k, j);
                    double qik = Q.get(k, i);
                    Q.data[k][j] = c * qjk - s * qik;
                    Q.data[k][i] = s * qjk + c * qik;
                }
            }
        }
        
        return new QRResult(Q, R);
    }

    private static boolean isQuasiTriangular(Matrix A, double tol) {
        int n = A.rows();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i - 1; j++) {
                if (Math.abs(A.get(i, j)) > tol) {
                    return false;
                }
            }
        }
        return true;
    }

    private static class QRResult {
        final Matrix Q;
        final Matrix R;
        
        QRResult(Matrix Q, Matrix R) {
            this.Q = Q;
            this.R = R;
        }
    }

    public static double[] powerIterationAll(Matrix A, int numEigenvalues) {
        int n = A.rows();
        double[] eigenvalues = new double[numEigenvalues];
        Matrix remaining = A.copy();
        
        for (int k = 0; k < numEigenvalues; k++) {
            double[] result = PowerIteration.dominantEigenvalue(remaining, 1000, 1e-10);
            eigenvalues[k] = result[0];
            
            if (k < numEigenvalues - 1) {
                Vector eigenvector = PowerIteration.dominantEigenvector(remaining, 1000, 1e-10);
                remaining = MatrixOperations.subtract(
                    remaining,
                    MatrixOperations.scale(
                        MatrixOperations.outerProduct(eigenvector, eigenvector),
                        result[0]
                    )
                );
            }
        }
        
        return eigenvalues;
    }
}
```

---

## 5. SVD Implementation

### 5.1 Full SVD

```java
package com.ml.la.matrix;

import com.ml.la.vector.Vector;

public class SVD {

    public static SVDResult compute(Matrix A) {
        int m = A.rows();
        int n = A.cols();
        
        Matrix AtA = MatrixOperations.multiply(MatrixOperations.transpose(A), A);
        double[] singularValues = QREigenvalue.powerIterationAll(AtA, Math.min(m, n));
        
        for (int i = 0; i < singularValues.length; i++) {
            singularValues[i] = Math.sqrt(Math.max(0, singularValues[i]));
        }
        
        Matrix V = computeRightSingularVectors(A, singularValues);
        
        Matrix Sigma = new Matrix(m, n);
        int minDim = Math.min(m, n);
        for (int i = 0; i < minDim; i++) {
            Sigma.data[i][i] = singularValues[i];
        }
        
        Matrix U = computeLeftSingularVectors(A, singularValues, V);
        
        return new SVDResult(U, Sigma, MatrixOperations.transpose(V));
    }

    private static Matrix computeRightSingularVectors(Matrix A, double[] singularValues) {
        int n = A.cols();
        int r = singularValues.length;
        
        Matrix[] eigenvectors = new Matrix[r];
        for (int i = 0; i < r; i++) {
            eigenvectors[i] = PowerIteration.dominantEigenvector(
                MatrixOperations.multiply(
                    MatrixOperations.transpose(A), A
                ),
                1000, 1e-10
            ).toMatrix(n, 1);
        }
        
        Matrix V = new Matrix(n, r);
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < n; j++) {
                V.data[j][i] = eigenvectors[i].data[j][0];
            }
        }
        
        for (int i = r; i < n; i++) {
            for (int j = 0; j < n; j++) {
                V.data[j][i] = (i == j) ? 1.0 : 0.0;
            }
        }
        
        return V;
    }

    private static Matrix computeLeftSingularVectors(Matrix A, double[] singularValues, Matrix V) {
        int m = A.rows();
        int r = singularValues.length;
        
        Matrix U = new Matrix(m, r);
        for (int i = 0; i < r; i++) {
            Vector vCol = V.getCol(i);
            Vector u = MatrixOperations.multiply(A, vCol);
            u = VectorOperations.scale(u, 1.0 / singularValues[i]);
            for (int j = 0; j < m; j++) {
                U.data[j][i] = u.get(j);
            }
        }
        
        return U;
    }

    public static Matrix reconstruct(SVDResult svd) {
        Matrix U = svd.U;
        Matrix S = svd.S;
        Matrix Vt = svd.Vt;
        
        Matrix US = MatrixOperations.multiply(U, S);
        return MatrixOperations.multiply(US, Vt);
    }

    public static SVDResult truncate(SVDResult svd, int k) {
        int m = svd.U.rows();
        int n = svd.Vt.cols();
        
        Matrix Utrunc = MatrixOperations.submatrix(svd.U, 0, m, 0, k);
        Matrix Strunc = new Matrix(k, k);
        for (int i = 0; i < k; i++) {
            Strunc.data[i][i] = svd.S.get(i, i);
        }
        Matrix Vttrunc = MatrixOperations.submatrix(svd.Vt, 0, k, 0, n);
        
        return new SVDResult(Utrunc, Strunc, Vttrunc);
    }

    public static Matrix pseudoInverse(SVDResult svd) {
        int m = svd.U.rows();
        int n = svd.Vt.cols();
        int r = svd.S.cols();
        
        Matrix Splus = new Matrix(n, m);
        for (int i = 0; i < r; i++) {
            if (svd.S.get(i, i) > 1e-10) {
                Splus.data[i][i] = 1.0 / svd.S.get(i, i);
            }
        }
        
        Matrix VtSt = MatrixOperations.multiply(MatrixOperations.transpose(svd.Vt), Splus);
        return MatrixOperations.multiply(VtSt, MatrixOperations.transpose(svd.U));
    }

    public static class SVDResult {
        public final Matrix U;
        public final Matrix S;
        public final Matrix Vt;
        
        public SVDResult(Matrix U, Matrix S, Matrix Vt) {
            this.U = U;
            this.S = S;
            this.Vt = Vt;
        }
    }
}
```

---

## 6. Advanced Operations

### 6.1 Gram-Schmidt Orthogonalization

```java
package com.ml.la.matrix;

import com.ml.la.vector.Vector;

public class GramSchmidt {

    public static Vector[] orthogonalize(Vector[] vectors) {
        int n = vectors.length;
        Vector[] orthogonal = new Vector[n];
        
        for (int i = 0; i < n; i++) {
            Vector u = vectors[i].copy();
            
            for (int j = 0; j < i; j++) {
                double proj = VectorOperations.dot(vectors[i], orthogonal[j]);
                double normSq = VectorOperations.dot(orthogonal[j], orthogonal[j]);
                Vector u_j = VectorOperations.scale(orthogonal[j], proj / normSq);
                u = VectorOperations.subtract(u, u_j);
            }
            
            orthogonal[i] = u;
        }
        
        return orthogonal;
    }

    public static Vector[] orthonormalize(Vector[] vectors) {
        Vector[] orthogonal = orthogonalize(vectors);
        Vector[] orthonormal = new Vector[vectors.length];
        
        for (int i = 0; i < vectors.length; i++) {
            double norm = VectorOperations.norm(orthogonal[i]);
            if (norm > 1e-12) {
                orthonormal[i] = VectorOperations.scale(orthogonal[i], 1.0 / norm);
            } else {
                orthonormal[i] = orthogonal[i];
            }
        }
        
        return orthonormal;
    }

    public static QRResult qr(Matrix A) {
        int m = A.rows();
        int n = A.cols();
        
        Vector[] cols = new Vector[n];
        for (int j = 0; j < n; j++) {
            cols[j] = A.getCol(j);
        }
        
        Vector[] qVecs = orthonormalize(cols);
        
        Matrix Q = new Matrix(m, n);
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < m; i++) {
                Q.data[i][j] = qVecs[j].get(i);
            }
        }
        
        Matrix Qt = MatrixOperations.transpose(Q);
        Matrix R = MatrixOperations.multiply(Qt, A);
        
        return new QRResult(Q, R);
    }

    public static class QRResult {
        public final Matrix Q;
        public final Matrix R;
        
        public QRResult(Matrix Q, Matrix R) {
            this.Q = Q;
            this.R = R;
        }
    }
}
```

### 6.2 Matrix Conditioning

```java
package com.ml.la.matrix;

public class ConditionNumber {

    public static double compute(Matrix A) {
        SVD.SVDResult svd = SVD.compute(A);
        
        double maxSigma = 0;
        double minSigma = Double.MAX_VALUE;
        
        int minDim = Math.min(A.rows(), A.cols());
        for (int i = 0; i < minDim; i++) {
            double sigma = svd.S.get(i, i);
            if (sigma > maxSigma) maxSigma = sigma;
            if (sigma < minSigma && sigma > 1e-12) minSigma = sigma;
        }
        
        return maxSigma / minSigma;
    }

    public static boolean isWellConditioned(Matrix A, double threshold) {
        return compute(A) < threshold;
    }

    public static int estimatedRank(Matrix A, double tol) {
        SVD.SVDResult svd = SVD.compute(A);
        
        int minDim = Math.min(A.rows(), A.cols());
        int rank = 0;
        
        for (int i = 0; i < minDim; i++) {
            if (svd.S.get(i, i) > tol) {
                rank++;
            }
        }
        
        return rank;
    }

    public static Matrix rankApproximation(Matrix A, int rank) {
        SVD.SVDResult svd = SVD.compute(A);
        SVD.SVDResult truncated = SVD.truncate(svd, rank);
        return SVD.reconstruct(truncated);
    }
}
```

---

## 7. ML-Specific Implementations

### 7.1 Covariance Matrix

```java
package com.ml.la.ml;

import com.ml.la.matrix.Matrix;
import com.ml.la.matrix.MatrixOperations;
import com.ml.la.vector.Vector;
import com.ml.la.vector.VectorOperations;

public class Covariance {

    public static Matrix compute(Matrix X) {
        int n = X.rows();
        int d = X.cols();
        
        Vector mean = new Vector(new double[d]);
        for (int j = 0; j < d; j++) {
            double sum = 0;
            for (int i = 0; i < n; i++) {
                sum += X.get(i, j);
            }
            mean.data[j] = sum / n;
        }
        
        Matrix centered = new Matrix(n, d);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < d; j++) {
                centered.data[i][j] = X.get(i, j) - mean.get(j);
            }
        }
        
        Matrix XtX = MatrixOperations.multiply(MatrixOperations.transpose(centered), centered);
        return MatrixOperations.scale(XtX, 1.0 / (n - 1));
    }

    public static Matrix computeOnline(Matrix X, Vector runningMean, Matrix runningCov, int count) {
        int d = X.cols();
        
        for (int i = 0; i < X.rows(); i++) {
            Vector x = X.getRow(i);
            count++;
            
            Vector delta = VectorOperations.subtract(x, runningMean);
            runningMean = VectorOperations.add(
                runningMean,
                VectorOperations.scale(delta, 1.0 / count)
            );
            
            Vector delta2 = VectorOperations.subtract(x, runningMean);
            Matrix outer = MatrixOperations.outerProduct(delta, delta2);
            
            Matrix deltaOuterDelta = MatrixOperations.outerProduct(delta, delta);
            Matrix scaled = MatrixOperations.scale(deltaOuterDelta, 1.0 / (count - 1));
            
            runningCov = MatrixOperations.add(
                MatrixOperations.scale(runningCov, (count - 1.0) / count),
                MatrixOperations.scale(scaled, 1.0 / count)
            );
        }
        
        return runningCov;
    }
}
```

### 7.2 PCA Implementation

```java
package com.ml.la.ml;

import com.ml.la.matrix.Matrix;
import com.ml.la.matrix.MatrixOperations;
import com.ml.la.vector.Vector;
import com.ml.la.vector.VectorOperations;

public class PCA {

    public static PCAResult fit(Matrix X, int nComponents) {
        int n = X.rows();
        int d = X.cols();
        
        Vector mean = new Vector(new double[d]);
        for (int j = 0; j < d; j++) {
            double sum = 0;
            for (int i = 0; i < n; i++) {
                sum += X.get(i, j);
            }
            mean.data[j] = sum / n;
        }
        
        Matrix centered = new Matrix(n, d);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < d; j++) {
                centered.data[i][j] = X.get(i, j) - mean.get(j);
            }
        }
        
        Matrix XtX = MatrixOperations.multiply(MatrixOperations.transpose(centered), centered);
        Matrix cov = MatrixOperations.scale(XtX, 1.0 / (n - 1));
        
        SVD.SVDResult svd = SVD.compute(cov);
        
        Matrix components = new Matrix(d, nComponents);
        for (int i = 0; i < d; i++) {
            for (int j = 0; j < nComponents; j++) {
                components.data[i][j] = svd.Vt.get(j, i);
            }
        }
        
        Matrix scores = new Matrix(n, nComponents);
        for (int i = 0; i < n; i++) {
            Vector row = centered.getRow(i);
            Vector score = MatrixOperations.multiply(components.toMatrixColwise(), row);
            for (int j = 0; j < nComponents; j++) {
                scores.data[i][j] = score.get(j);
            }
        }
        
        double[] explainedVariance = new double[nComponents];
        double totalVariance = 0;
        for (int i = 0; i < d; i++) {
            totalVariance += svd.S.get(i, i);
        }
        for (int j = 0; j < nComponents; j++) {
            explainedVariance[j] = svd.S.get(j, j) / totalVariance;
        }
        
        return new PCAResult(components, scores, mean, explainedVariance);
    }

    public static Matrix transform(Matrix X, PCAResult pca) {
        int n = X.rows();
        int d = X.cols();
        
        Matrix centered = new Matrix(n, d);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < d; j++) {
                centered.data[i][j] = X.get(i, j) - pca.mean.get(j);
            }
        }
        
        Matrix components = MatrixOperations.transpose(pca.components);
        return MatrixOperations.multiply(centered, components);
    }

    public static Matrix inverseTransform(Matrix X, PCAResult pca) {
        Matrix componentsT = MatrixOperations.transpose(pca.components);
        Matrix centered = MatrixOperations.multiply(X, componentsT);
        
        int n = X.rows();
        int d = pca.mean.size();
        Matrix result = new Matrix(n, d);
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < d; j++) {
                result.data[i][j] = centered.get(i, j) + pca.mean.get(j);
            }
        }
        
        return result;
    }

    public static class PCAResult {
        public final Matrix components;
        public final Matrix scores;
        public final Vector mean;
        public final double[] explainedVariance;
        
        public PCAResult(Matrix components, Matrix scores, Vector mean, double[] explainedVariance) {
            this.components = components;
            this.scores = scores;
            this.mean = mean;
            this.explainedVariance = explainedVariance;
        }
    }
}
```

### 7.3 Linear Regression

```java
package com.ml.la.ml;

import com.ml.la.matrix.Matrix;
import com.ml.la.matrix.MatrixOperations;
import com.ml.la.vector.Vector;

public class LinearRegression {

    public static Vector leastSquares(Matrix X, Vector y) {
        Matrix Xt = MatrixOperations.transpose(X);
        Matrix XtX = MatrixOperations.multiply(Xt, X);
        
        LUDecomposition lu = new LUDecomposition(XtX);
        Vector Xty = MatrixOperations.multiply(Xt, y);
        
        return lu.solve(Xty);
    }

    public static Vector ridge(Matrix X, Vector y, double lambda) {
        int n = X.cols();
        Matrix Xt = MatrixOperations.transpose(X);
        Matrix XtX = MatrixOperations.multiply(Xt, X);
        
        for (int i = 0; i < n; i++) {
            XtX.data[i][i] += lambda;
        }
        
        LUDecomposition lu = new LUDecomposition(XtX);
        Vector Xty = MatrixOperations.multiply(Xt, y);
        
        return lu.solve(Xty);
    }

    public static Vector lasso(Matrix X, Vector y, double lambda, int maxIter, double tol) {
        int n = X.cols();
        Vector beta = VectorOperations.zeros(n);
        
        for (int iter = 0; iter < maxIter; iter++) {
            Vector betaOld = beta.copy();
            
            for (int j = 0; j < n; j++) {
                double residual = 0;
                for (int i = 0; i < X.rows(); i++) {
                    double xij = X.get(i, j);
                    double sum = 0;
                    for (int k = 0; k < n; k++) {
                        if (k != j) {
                            sum += X.get(i, k) * beta.get(k);
                        }
                    }
                    residual += xij * (y.get(i) - sum);
                }
                
                double softThreshold = Math.max(0, Math.abs(residual) - lambda) / Math.max(1e-10, 
                    Math.sqrt(VectorOperations.dot(X.getCol(j), X.getCol(j))));
                
                beta.data[j] = Math.signum(residual) * softThreshold;
            }
            
            double diff = 0;
            for (int j = 0; j < n; j++) {
                diff += Math.abs(beta.get(j) - betaOld.get(j));
            }
            
            if (diff < tol) break;
        }
        
        return beta;
    }

    public static double[] predict(Matrix X, Vector beta) {
        int n = X.rows();
        double[] predictions = new double[n];
        
        for (int i = 0; i < n; i++) {
            Vector row = X.getRow(i);
            predictions[i] = VectorOperations.dot(row, beta);
        }
        
        return predictions;
    }

    public static double mse(double[] yTrue, double[] yPred) {
        double sum = 0;
        for (int i = 0; i < yTrue.length; i++) {
            double diff = yTrue[i] - yPred[i];
            sum += diff * diff;
        }
        return sum / yTrue.length;
    }

    public static double r2Score(double[] yTrue, double[] yPred) {
        double mean = 0;
        for (double y : yTrue) mean += y;
        mean /= yTrue.length;
        
        double ssTot = 0;
        double ssRes = 0;
        for (int i = 0; i < yTrue.length; i++) {
            ssTot += (yTrue[i] - mean) * (yTrue[i] - mean);
            ssRes += (yTrue[i] - yPred[i]) * (yTrue[i] - yPred[i]);
        }
        
        return 1 - ssRes / ssTot;
    }
}
```

### 7.4 Matrix Factorization (Recommendation Systems)

```java
package com.ml.la.ml;

import com.ml.la.matrix.Matrix;
import com.ml.la.matrix.MatrixOperations;
import com.ml.la.vector.Vector;
import com.ml.la.vector.VectorOperations;

public class MatrixFactorization {

    public static MFResult factorize(Matrix R, int k, double lr, double reg, int maxIter) {
        int m = R.rows();
        int n = R.cols();
        
        Matrix P = Matrix.random(m, k, System.currentTimeMillis());
        Matrix Q = Matrix.random(n, k, System.currentTimeMillis() + 1);
        
        for (int iter = 0; iter < maxIter; iter++) {
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    if (Math.abs(R.get(i, j)) < 1e-10) continue;
                    
                    double pred = 0;
                    for (int f = 0; f < k; f++) {
                        pred += P.get(i, f) * Q.get(j, f);
                    }
                    
                    double error = R.get(i, j) - pred;
                    
                    for (int f = 0; f < k; f++) {
                        double pif = P.get(i, f);
                        double qjf = Q.get(j, f);
                        
                        P.data[i][f] += lr * (error * qjf - reg * pif);
                        Q.data[j][f] += lr * (error * pif - reg * qjf);
                    }
                }
            }
        }
        
        Matrix prediction = MatrixOperations.multiply(P, MatrixOperations.transpose(Q));
        
        return new MFResult(P, Q, prediction);
    }

    public static MFResult factorizeSGD(Matrix R, int k, double lr, double reg, int maxIter, double momentum) {
        int m = R.rows();
        int n = R.cols();
        
        Matrix P = Matrix.random(m, k);
        Matrix Q = Matrix.random(n, k);
        
        Matrix dP = Matrix.zeros(m, k);
        Matrix dQ = Matrix.zeros(n, k);
        
        java.util.Random gen = new java.util.Random();
        
        for (int iter = 0; iter < maxIter; iter++) {
            int i = gen.nextInt(m);
            int j = gen.nextInt(n);
            
            if (Math.abs(R.get(i, j)) < 1e-10) continue;
            
            double pred = 0;
            for (int f = 0; f < k; f++) {
                pred += P.get(i, f) * Q.get(j, f);
            }
            
            double error = R.get(i, j) - pred;
            
            for (int f = 0; f < k; f++) {
                dP.data[i][f] = momentum * dP.data[i][f] + lr * (error * Q.get(j, f) - reg * P.get(i, f));
                dQ.data[j][f] = momentum * dQ.data[j][f] + lr * (error * P.get(i, f) - reg * Q.get(j, f));
                
                P.data[i][f] += dP.data[i][f];
                Q.data[j][f] += dQ.data[j][f];
            }
        }
        
        Matrix prediction = MatrixOperations.multiply(P, MatrixOperations.transpose(Q));
        
        return new MFResult(P, Q, prediction);
    }

    public static class MFResult {
        public final Matrix P;
        public final Matrix Q;
        public final Matrix prediction;
        
        public MFResult(Matrix P, Matrix Q, Matrix prediction) {
            this.P = P;
            this.Q = Q;
            this.prediction = prediction;
        }
    }
}
```

---

## Summary

This code deep dive provided comprehensive Java implementations for:

1. **Vector operations**: Basic and advanced vector math
2. **Matrix operations**: All fundamental operations
3. **Linear system solvers**: Gaussian elimination, LU, Cholesky
4. **Eigenvalue computation**: Power iteration, QR algorithm
5. **SVD**: Full and truncated decompositions
6. **ML applications**: PCA, linear regression, matrix factorization

All implementations follow Java best practices and are suitable for ML applications.