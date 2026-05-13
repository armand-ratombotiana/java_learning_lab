# Regression - Complete Theory

## Overview
Regression predicts continuous target variables from input features.

## 1. Linear Regression

### 1.1 Simple Linear Regression
y = wx + b

### 1.2 Multiple Linear Regression
y = Xw + b

### 1.3 Least Squares Solution
w = (X^T X)^{-1} X^T y

## 2. Polynomial Regression
y = w0 + w1 x + w2 x^2 + ...

## 3. Regularized Regression

### 3.1 Ridge (L2)
Minimize: ||y - Xw||^2 + lambda ||w||^2

### 3.2 Lasso (L1)
Minimize: ||y - Xw||^2 + lambda ||w||_1

### 3.3 Elastic Net
Minimize: ||y - Xw||^2 + lambda1 ||w||_1 + lambda2 ||w||^2

## 4. Support Vector Regression
Epsilon-insensitive loss

## 5. Gaussian Process Regression
Bayesian non-parametric approach

## Java Implementation

```java
public class LinearRegression {
    public Vector fit(Matrix X, Vector y) {
        Matrix Xt = MatrixOperations.transpose(X);
        Matrix XtX = MatrixOperations.multiply(Xt, X);
        Matrix Xty = MatrixOperations.multiply(Xt, y.toMatrix());
        LUDecomposition lu = new LUDecomposition(XtX);
        return lu.solve(Xty.toVector());
    }
    
    public double predict(Vector x, Vector w) {
        return VectorOperations.dot(x, w);
    }
}
```

## Gradient Descent for Regression

```java
public Vector gradientDescent(Matrix X, Vector y, double lr, int epochs) {
    int n = X.rows();
    Vector w = VectorOperations.zeros(X.cols());
    
    for (int epoch = 0; epoch < epochs; epoch++) {
        Vector grad = VectorOperations.zeros(X.cols());
        for (int i = 0; i < n; i++) {
            double pred = VectorOperations.dot(X.getRow(i), w);
            double err = pred - y.get(i);
            grad = VectorOperations.add(grad, 
                VectorOperations.scale(X.getRow(i), err));
        }
        grad = VectorOperations.scale(grad, 2.0/n);
        w = VectorOperations.subtract(w, VectorOperations.scale(grad, lr));
    }
    return w;
}
```