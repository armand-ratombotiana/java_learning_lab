# Mini Project: Matrix Operations Library in Java

## Project Overview
Build a comprehensive matrix operations library from scratch in Java, implementing all core linear algebra operations needed for machine learning.

## Requirements

### 1. Vector Class (Vector.java)
```java
public class Vector {
    double[] data;
    
    // Constructor and factory methods
    Vector(double... values);
    static Vector zeros(int size);
    static Vector ones(int size);
    static Vector random(int size);
    
    // Basic operations
    Vector add(Vector other);
    Vector subtract(Vector other);
    Vector scale(double scalar);
    double dot(Vector other);
    
    // Norms
    double normL1();
    double normL2();
    double normLInf();
    
    // Advanced
    double cosineSimilarity(Vector other);
    Vector normalize();
}
```

### 2. Matrix Class (Matrix.java)
```java
public class Matrix {
    double[][] data;
    int rows, cols;
    
    // Constructors
    Matrix(double[][] data);
    static Matrix identity(int n);
    static Matrix zeros(int rows, int cols);
    static Matrix random(int rows, int cols);
    static Matrix diagonal(double... values);
    
    // Operations
    Matrix add(Matrix other);
    Matrix subtract(Matrix other);
    Matrix multiply(Matrix other);
    Vector multiply(Vector v);
    Matrix transpose();
    Matrix scale(double scalar);
    
    // Properties
    double trace();
    double determinant();
    double frobeniusNorm();
}
```

### 3. Linear System Solver (LinearSolver.java)
```java
public class LinearSolver {
    // Gaussian elimination
    static Vector solveGaussian(Matrix A, Vector b);
    
    // LU decomposition
    static Vector solveLU(Matrix A, Vector b);
    
    // Cholesky for SPD matrices
    static Vector solveCholesky(Matrix A, Vector b);
}
```

### 4. Eigendecomposition (Eigen.java)
```java
public class Eigen {
    // Power iteration for dominant eigenvalue
    static double[] dominantEigenvalue(Matrix A);
    
    // All eigenvalues via QR algorithm
    static double[] allEigenvalues(Matrix A);
}
```

### 5. SVD Implementation (SVD.java)
```java
public class SVD {
    static SVDResult compute(Matrix A);
    static Matrix truncate(Matrix A, int k);
    static Matrix pseudoInverse(Matrix A);
}
```

## Implementation Steps

### Step 1: Vector Class
1. Implement basic vector operations
2. Test with simple vectors
3. Verify norm properties

### Step 2: Matrix Class
1. Implement matrix multiplication
2. Implement transpose
3. Test with known matrices

### Step 3: Gaussian Elimination
1. Forward elimination
2. Back substitution
3. Test with linear systems

### Step 4: LU Decomposition
1. Decompose A = LU
2. Solve via forward/back substitution
3. Compute determinant and inverse

### Step 5: Eigenvalue Computation
1. Implement power iteration
2. Implement QR algorithm
3. Compare with known eigenvalues

### Step 6: SVD
1. Compute via eigenvalue decomposition
2. Implement truncated version
3. Test reconstruction

## Testing

```java
public class TestMatrixLibrary {
    public static void main(String[] args) {
        // Test Vector
        Vector v1 = new Vector(1, 2, 3);
        Vector v2 = new Vector(4, 5, 6);
        System.out.println("Dot product: " + v1.dot(v2));
        
        // Test Matrix
        Matrix A = Matrix.of(new double[][]{{1,2},{3,4}});
        Matrix B = Matrix.of(new double[][]{{5,6},{7,8}});
        Matrix C = A.multiply(B);
        
        // Test Linear System
        Matrix LHS = Matrix.of(new double[][]{{1,1},{2,1}});
        Vector rhs = new Vector(3, 4);
        Vector x = LinearSolver.solveLU(LHS, rhs);
        
        // Test Eigenvalues
        double[] eigenvals = Eigen.allEigenvalues(A);
        
        // Test SVD
        SVD.SVDResult svd = SVD.compute(A);
    }
}
```

## Expected Results

### Vector Operations
- Dot product of [1,2,3] and [4,5,6] = 32
- L2 norm of [3,4] = 5

### Matrix Operations
- [[1,2],[3,4]] * [[5,6],[7,8]] = [[19,22],[43,50]]
- Trace of 2x2 identity = 2

### Linear System
- Solution of Ax=b where A=[[1,1],[2,1]], b=[3,4] = [-1, 4]

### Eigenvalues
- Eigenvalues of [[2,1],[1,2]] = 1, 3

## Bonus Challenges

1. Sparse Matrix: Implement sparse matrix storage and multiplication
2. Block Operations: Support block matrix operations
3. Benchmark: Compare with JAMA or MTJ library performance
4. GPU Support: Use Aparapi for GPU acceleration