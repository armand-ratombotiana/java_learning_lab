# Calculus for Machine Learning - THEORY

## Overview

Calculus provides the mathematical foundation for optimization algorithms that train ML models.

## 1. Derivatives

### Definition
```
f'(x) = lim(h→0) [f(x+h) - f(x)] / h
```

### Rules
- **Power rule**: d/dx(xⁿ) = nxⁿ⁻¹
- **Product rule**: d/dx(fg) = f'g + fg'
- **Quotient rule**: d/dx(f/g) = (f'g - fg') / g²
- **Chain rule**: d/dx(f(g(x))) = f'(g(x)) × g'(x)

### Implementation

```java
public class DerivativeCalculator {
    public double numericDerivative(DoubleUnaryOperator f, double x) {
        double h = 1e-7;
        return (f.applyAsDouble(x + h) - f.applyAsDouble(x)) / h;
    }
    
    public double[] gradient(double[] x, Function<double[], Double> f) {
        double[] grad = new double[x.length];
        double eps = 1e-7;
        
        for (int i = 0; i < x.length; i++) {
            double[] xPlus = x.clone();
            xPlus[i] += eps;
            grad[i] = (f.apply(xPlus) - f.apply(x)) / eps;
        }
        return grad;
    }
}
```

## 2. Partial Derivatives

For functions of multiple variables:
```
∂f/∂xᵢ = limit as h→0 of [f(x₁,...,xᵢ+h,...,xₙ) - f(x)] / h
```

## 3. Gradient

Vector of partial derivatives:
```
∇f(x) = [∂f/∂x₁, ∂f/∂x₂, ..., ∂f/∂xₙ]ᵀ
```

Points in direction of steepest ascent.

## 4. Chain Rule (Multivariable)

```
∂z/∂x = Σᵢ (∂z/∂uᵢ)(∂uᵢ/∂x)
```

Essential for backpropagation in neural networks.

## 5. Integration

### Definition
```
∫f(x)dx = F(x) + C  where F'(x) = f(x)
```

### Numerical Integration

```java
public class IntegrationCalculator {
    public double trapezoidal(double a, double b, int n, DoubleUnaryOperator f) {
        double h = (b - a) / n;
        double sum = 0;
        
        for (int i = 0; i <= n; i++) {
            double x = a + i * h;
            double y = f.applyAsDouble(x);
            if (i == 0 || i == n) sum += y;
            else sum += 2 * y;
        }
        
        return (h / 2) * sum;
    }
    
    public double simpson(double a, double b, int n, DoubleUnaryOperator f) {
        double h = (b - a) / n;
        double sum = f.applyAsDouble(a) + f.applyAsDouble(b);
        
        for (int i = 1; i < n; i++) {
            double x = a + i * h;
            sum += (i % 2 == 0) ? 2 * f.applyAsDouble(x) : 4 * f.applyAsDouble(x);
        }
        
        return (h / 3) * sum;
    }
}
```

## 6. Optimization

### Gradient Descent
```java
public class GradientDescent {
    public double[] minimize(double[] init, Function<double[], Double> f) {
        double[] x = init.clone();
        double lr = 0.01;
        
        for (int iter = 0; iter < 1000; iter++) {
            double[] grad = computeGradient(x, f);
            
            for (int i = 0; i < x.length; i++) {
                x[i] -= lr * grad[i];
            }
            
            if (norm(grad) < 1e-6) break;
        }
        return x;
    }
}
```

### Jacobian and Hessian

```java
public class MatrixCalculus {
    // Jacobian: first-order derivatives for vector-valued functions
    public double[][] jacobian(Function<double[], double[]> f, double[] x) {
        double eps = 1e-7;
        int m = f.apply(x).length;
        int n = x.length;
        double[][] J = new double[m][n];
        
        for (int j = 0; j < n; j++) {
            double[] xPlus = x.clone();
            xPlus[j] += eps;
            double[] f1 = f.apply(x);
            double[] f2 = f.apply(xPlus);
            
            for (int i = 0; i < m; i++) {
                J[i][j] = (f2[i] - f1[i]) / eps;
            }
        }
        return J;
    }
    
    // Hessian: second-order derivatives
    public double[][] hessian(Function<double[], Double> f, double[] x) {
        double eps = 1e-5;
        int n = x.length;
        double[][] H = new double[n][n];
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= i; j++) {
                double f_ij = finiteDifference(f, x, i, j, eps);
                H[i][j] = f_ij;
                H[j][i] = f_ij;
            }
        }
        return H;
    }
}
```

## Summary

1. **Derivatives**: Rate of change
2. **Gradients**: Direction of steepest ascent
3. **Chain rule**: Computing nested derivatives
4. **Optimization**: Finding minima via gradient descent