# Calculus Performance

## Algorithm Complexity

| Method | Error | Evaluations | Notes |
|--------|-------|-------------|-------|
| Forward difference | $O(h)$ | 1 | Simple, inaccurate |
| Central difference | $O(h^2)$ | 2 | Good balance |
| Richardson extrap. | $O(h^4)$ | 4 | High accuracy |
| Automatic diff. | Exact | 1 | Compile-time |

| Integration | Error | Evaluations |
|-------------|-------|-------------|
| Midpoint | $O(h^2)$ | $n$ |
| Simpson | $O(h^4)$ | $n+1$ |
| Adaptive quad | Variable | Minimal |
| Gauss-Legendre | $O(h^{2n})$ | Few |

## Vectorized Operations

For computing derivatives at many points, batch computations:

```java
double[] xs = ...;
double[] ys = new double[n];
for (int i = 0; i < n; i++)
    ys[i] = f.apply(xs[i]);

// Central differences (vectorized)
double[] derivs = new double[n];
for (int i = 1; i < n - 1; i++)
    derivs[i] = (ys[i+1] - ys[i-1]) / (xs[i+1] - xs[i-1]);
```

## GPU Acceleration

For large-scale differentiation (deep learning), use GPUs via libraries like:
- **DeepLearning4J** (Java + CUDA)
- **ND4J** (n-dimensional arrays for Java)
- **DJL** (Deep Java Library)
