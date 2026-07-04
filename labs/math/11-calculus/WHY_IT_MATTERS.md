# Why Calculus Matters

Calculus is the language of continuous change — essential for science and engineering.

## Applications

| Domain | Use |
|--------|-----|
| Physics | $F = ma$, Maxwell's equations, quantum mechanics |
| Engineering | Optimization, control theory, signal processing |
| Machine Learning | Gradient descent, backpropagation, loss optimization |
| Economics | Marginal analysis, elasticity, growth models |
| Biology | Population dynamics, pharmacokinetics |
| Computer Graphics | Bézier curves, splines, rendering equations |

## In Java

```java
// Gradient descent for ML
double[] gradientDescent(double[][] X, double[] y, double lr, int epochs) {
    int m = X[0].length;
    double[] theta = new double[m];
    for (int epoch = 0; epoch < epochs; epoch++) {
        double[] gradient = computeGradient(X, y, theta);
        for (int j = 0; j < m; j++)
            theta[j] -= lr * gradient[j];
    }
    return theta;
}
```
