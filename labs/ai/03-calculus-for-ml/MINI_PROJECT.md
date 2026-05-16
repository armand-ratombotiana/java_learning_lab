# Calculus - MINI PROJECT

Implement gradient descent optimizer with:
- Numerical gradient computation
- Learning rate scheduling
- Convergence detection

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