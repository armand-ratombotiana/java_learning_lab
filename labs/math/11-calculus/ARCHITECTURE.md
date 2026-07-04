# Architecture of Calculus

## Calculus in Machine Learning Frameworks

```
Computation Graph (e.g., TensorFlow, PyTorch)
├── Forward pass: compute output
├── Backward pass: automatic differentiation
│   ├── ∂Loss/∂z = ... (chain rule)
│   └── Gradient flows from output to input
└── Optimizer: GradientDescent, Adam, RMSProp
```

## Numerical Libraries in Java

```
├── Apache Commons Math
│   ├── Numerical differentiation
│   ├── Integration (Romberg, Simpson, adaptive)
│   ├── ODE solvers (Runge-Kutta)
│   └── Optimization (gradient descent, simplex)
├── ND4J
│   ├── Auto-differentiation
│   └── GPU-accelerated tensor ops
└── DeepLearning4J
    └── Neural network training
```

## The Calculus Pipeline

```
Function f
    → Derivative f' (rate of change)
        → Gradient ∇f (multivariate rate)
            → Optimization (find minima)
                → ML training, physics simulation

Function f
    → Integral ∫f (accumulation)
        → ODE/PDE solvers
            → Physical simulation, control systems
```

## Differentiation Framework

```java
interface DifferentiableFunction extends Function<Double, Double> {
    DifferentiableFunction derivative();
}

class Polynomial implements DifferentiableFunction {
    private final double[] coeffs;
    public DifferentiableFunction derivative() {
        double[] derivCoeffs = new double[coeffs.length - 1];
        for (int i = 1; i < coeffs.length; i++)
            derivCoeffs[i - 1] = coeffs[i] * i;
        return new Polynomial(derivCoeffs);
    }
}
```
