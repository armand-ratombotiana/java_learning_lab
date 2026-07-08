# Code Deep Dive: Numerical Methods in Java

## 1. Numerical Integration

### 1.1 Simpson's Rule
```java
public static double simpsonsRule(Function<Double, Double> f, double a, double b, int n) {
    if (n % 2 != 0) n++;
    double h = (b - a) / n;
    double sum = f.apply(a) + f.apply(b);
    for (int i = 1; i < n; i++) {
        sum += (i % 2 == 0 ? 2 : 4) * f.apply(a + i * h);
    }
    return sum * h / 3;
}
```

**Error analysis:** The error term for Simpson's rule is:
E = -(b-a)⁵ / (180·n⁴) · f⁽⁴⁾(ξ)

This is O(h⁴) — doubling n reduces error by factor 16 (for smooth functions).

**Why the pattern 1, 4, 2, 4, ..., 2, 4, 1?**
Simpson's rule fits a quadratic through each interval pair. The alternating pattern emerges from the quadratic interpolation coefficients.

### 1.2 Romberg Integration
```java
public static double rombergIntegration(Function<Double, Double> f, double a, double b, int order) {
    int n = order + 1;
    double[][] R = new double[n][n];
    double h = b - a;
    R[0][0] = 0.5 * h * (f.apply(a) + f.apply(b));
    for (int i = 1; i < n; i++) {
        h /= 2;
        double sum = 0;
        for (int k = 1; k <= (1 << (i - 1)); k++) {
            sum += f.apply(a + (2 * k - 1) * h);
        }
        R[i][0] = 0.5 * R[i - 1][0] + h * sum;
        for (int j = 1; j <= i; j++) {
            R[i][j] = R[i][j - 1] + (R[i][j - 1] - R[i - 1][j - 1]) / (Math.pow(4, j) - 1);
        }
    }
    return R[n - 1][n - 1];
}
```

Romberg uses Richardson extrapolation on the trapezoidal rule. The first column R[i][0] is the trapezoidal rule with 2ⁱ intervals. Each subsequent column performs extrapolation to eliminate the leading error term.

## 2. Root Finding

### 2.1 Bisection Method
```java
public static double bisection(Function<Double, Double> f, double a, double b, int maxIter) {
    double fa = f.apply(a), fb = f.apply(b);
    if (fa * fb > 0) throw new IllegalArgumentException("f(a) and f(b) must have opposite signs");
    for (int i = 0; i < maxIter; i++) {
        double c = (a + b) / 2;
        double fc = f.apply(c);
        if (Math.abs(fc) < 1e-15 || (b - a) / 2 < 1e-15) return c;
        if (fa * fc < 0) { b = c; fb = fc; }
        else { a = c; fa = fc; }
    }
    return (a + b) / 2;
}
```

**Guaranteed properties:**
- Linear convergence: error reduces by factor 2 each iteration
- 1 bit of precision per iteration
- Always converges (unlike Newton) given sign change
- Needs ~30 iterations for 10⁻⁹ precision

### 2.2 Newton-Raphson
```java
public static double newtonRaphson(Function<Double, Double> f, Function<Double, Double> fPrime,
                                    double start, int maxIter) {
    double x = start;
    for (int i = 0; i < maxIter; i++) {
        double fx = f.apply(x);
        if (Math.abs(fx) < 1e-15) return x;
        double fpx = fPrime.apply(x);
        if (Math.abs(fpx) < 1e-15) break;
        x = x - fx / fpx;
    }
    return x;
}
```

**Quadratic convergence:** once near the root, the number of correct digits doubles each iteration. However, the method may diverge if the initial guess is poor or if f'(x) is near zero.

## 3. Interpolation

### 3.1 Lagrange Interpolation
```java
public static double lagrangeInterpolation(double[] xs, double[] ys, double x) {
    double result = 0;
    for (int i = 0; i < xs.length; i++) {
        double term = ys[i];
        for (int j = 0; j < xs.length; j++) {
            if (i != j) term *= (x - xs[j]) / (xs[i] - xs[j]);
        }
        result += term;
    }
    return result;
}
```

**Numerical issues:** Lagrange interpolation is O(n²) per evaluation and suffers from Runge's phenomenon for equispaced points. For n > 10, it becomes numerically unstable.

### 3.2 Newton's Divided Differences
```java
public static double[] newtonDividedDifference(double[] xs, double[] ys) {
    int n = xs.length;
    double[] coeffs = ys.clone();
    for (int j = 1; j < n; j++) {
        for (int i = n - 1; i >= j; i--) {
            coeffs[i] = (coeffs[i] - coeffs[i - 1]) / (xs[i] - xs[i - j]);
        }
    }
    return coeffs;
}
```

Newton's form is more stable and efficient for evaluating at many points. The divided difference coefficients can be computed once and reused.

## 4. ODE Solvers (RK4)

```java
public static double[] rungeKutta4(BiFunction<Double, Double, Double> f,
                                    double t0, double y0, double h, int steps) {
    double[] ys = new double[steps + 1];
    double t = t0, y = y0;
    ys[0] = y0;
    for (int i = 1; i <= steps; i++) {
        double k1 = f.apply(t, y);
        double k2 = f.apply(t + h / 2, y + h * k1 / 2);
        double k3 = f.apply(t + h / 2, y + h * k2 / 2);
        double k4 = f.apply(t + h, y + h * k3);
        y = y + (h / 6) * (k1 + 2 * k2 + 2 * k3 + k4);
        t = t + h;
        ys[i] = y;
    }
    return ys;
}
```

**Butcher tableau for RK4:**
```
0    |
1/2  | 1/2
1/2  | 0   1/2
1    | 0   0   1
-----+-----------------
     | 1/6 1/3 1/3 1/6
```

RK4 achieves O(h⁴) accuracy with just 4 function evaluations per step. It's the standard go-to ODE solver for non-stiff problems.

## 5. Finite Differences

### 5.1 Central Difference
```java
public static double centralDifference(Function<Double, Double> f, double x, double h) {
    return (f.apply(x + h) - f.apply(x - h)) / (2 * h);
}
```

Error is O(h²). The optimal h balances truncation error (O(h²)) and roundoff error (O(ε/h)):
h_optimal ≈ ∛(ε·f(x)/f'''(x)) ≈ 10⁻⁵ for double precision

### 5.2 Richardson Extrapolation
```java
public static double richardsonExtrapolation(Function<Double, Double> fDerivative,
                                               double x, double h, int order) {
    double[] D = new double[order + 1];
    for (int i = 0; i <= order; i++) {
        D[i] = fDerivative.apply(h / Math.pow(2, i));
    }
    for (int k = 1; k <= order; k++) {
        for (int i = order; i >= k; i--) {
            D[i] = D[i] + (D[i] - D[i - 1]) / (Math.pow(2, k) - 1);
        }
    }
    return D[order];
}
```

Richardson extrapolation uses the same principle as Romberg integration: combine approximations at different step sizes to cancel error terms.
