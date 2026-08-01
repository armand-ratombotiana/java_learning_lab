# Problem Walkthrough: Newton-Raphson Root Finding with Quadratic Convergence

## Problem Statement

Implement a numerically robust Newton-Raphson root finder for a scalar function f: R -> R with an analytic derivative, and **empirically verify quadratic convergence**. The solver must:

1. Accept f, f', an initial guess x₀, value/step tolerances, and a max-iteration cap.
2. Terminate when |f(x_n)| ≤ tolF or |x_{n+1} - x_n| ≤ tolX.
3. Fail loudly (controlled exceptions) on zero derivative, NaN/Inf iterates, and non-convergence.
4. Record the full error trace so the asymptotic convergence order can be estimated.
5. Demonstrate order p ≈ 2 on a simple root and p ≈ 1 on a multiple root.

**Deliverable**: `com.math.deep.lab01.NewtonRaphsonSolver` — a complete Java 21+ class with `solve(...)`, `estimatedOrder(...)`, a `RootResult` record, and a `main` demo that prints the verification table.

---

## Constraints & Requirements

| Item | Requirement |
|------|-------------|
| Language | Java 21+ (records, functional interfaces, no external libs) |
| Input | f, df as `DoubleUnaryOperator`, x₀, tolF, tolX, maxIter |
| Output | `RootResult(root, iterations, errorTrace)` |
| Failure | `ArithmeticException` with descriptive message |
| Extra credit | Order-of-convergence estimator, multiplicity-aware variant |

---

## Step 1: Mathematical Foundation

### 1.1 The iteration

Newton-Raphson solves f(x) = 0 by linearizing at each step:

f(x) ≈ f(xₙ) + f'(xₙ)(x - xₙ)

Setting the linear model to zero and solving for x:

xₙ₊₁ = xₙ - f(xₙ) / f'(xₙ)

This is the x-intercept of the tangent line at (xₙ, f(xₙ)).

### 1.2 Quadratic convergence theorem

**Theorem.** If f is twice continuously differentiable, r is a simple root (f(r) = 0, f'(r) ≠ 0), and x₀ is sufficiently close to r, then Newton's method converges quadratically:

|eₙ₊₁| ≤ C |eₙ|² where C = |f''(r)| / (2|f'(r)|)

**Proof sketch.** Write eₙ = xₙ - r. Taylor-expand f at the root:

0 = f(r) = f(xₙ) + f'(xₙ)(r - xₙ) + (1/2) f''(ξₙ)(r - xₙ)²

= f(xₙ) - f'(xₙ) eₙ + (1/2) f''(ξₙ) eₙ²

Divide by f'(xₙ) and rearrange:

f(xₙ)/f'(xₙ) = eₙ - (1/2) (f''(ξₙ)/f'(xₙ)) eₙ²

Then the error recurrence follows:

eₙ₊₁ = xₙ₊₁ - r = eₙ - f(xₙ)/f'(xₙ) = (1/2) (f''(ξₙ)/f'(xₙ)) eₙ²

As xₙ → r, f''(ξₙ)/f'(xₙ) → f''(r)/f'(r), so |eₙ₊₁| ≤ C|eₙ|². Convergence is **quadratic**: the exponent of the error doubles each step, i.e. the number of correct digits doubles.

### 1.3 Multiple roots

If r has multiplicity m ≥ 2, then f'(r) = 0 and the analysis breaks down. The error recurrence becomes:

eₙ₊₁ ≈ ((m - 1)/m) eₙ

which is **linear** (p = 1) with constant (m-1)/m < 1. Modified Newton restores quadratic convergence:

xₙ₊₁ = xₙ - m · f(xₙ)/f'(xₙ)

### 1.4 Order estimation from an error trace

Take logs of eₙ₊₁ ≈ C eₙᵖ:

log|eₙ₊₁| ≈ p · log|eₙ| + log C

Given three consecutive errors (e₀, e₁, e₂) in the asymptotic regime:

p ≈ log|e₂/e₁| / log|e₁/e₀|

The estimator must skip early iterates (pre-asymptotic) and the final accepted point (e = 0).

---

## Step 2: Design

### 2.1 Data types

```java
public record RootResult(double root, int iterations, double[] errorTrace) {}
```

The trace stores each iterate xₙ (including x₀); the caller converts to errors using the known root. `RootResult` is immutable — good record use.

### 2.2 Termination strategy

| Condition | Meaning | Priority |
|-----------|---------|----------|
| \|f(xₙ)\| ≤ tolF | Value test: we are numerically at a root | 1 |
| \|xₙ₊₁ - xₙ\| ≤ tolX | Step test: iterates stopped moving (flat region) | 2 |
| n == maxIter | Iteration cap: non-convergence | 3 (failure) |

### 2.3 Failure modes

1. |f'(xₙ)| < ε_machine → tangent is horizontal; division explodes. Throw.
2. Step is NaN or ±Inf → f' vanished between evaluations or f produced garbage. Throw.
3. Iteration count exhausted → oscillation (e.g. f(x) = x - tan x) or cycle. Throw.
4. `Double.isFinite` check on every candidate iterate → overflow containment.

---

## Step 3: Complete Solution (Java 21+)

```java
package com.math.deep.lab01;

import java.util.Arrays;
import java.util.function.DoubleUnaryOperator;

public final class NewtonRaphsonSolver {

    private static final double DERIVATIVE_FLOOR = 1e-15;

    public record RootResult(double root, int iterations, double[] errorTrace) {
        public RootResult {
            errorTrace = Arrays.copyOf(errorTrace, errorTrace.length);
        }
    }

    public static RootResult solve(DoubleUnaryOperator f, DoubleUnaryOperator df,
                                   double x0, double tolF, double tolX, int maxIter) {
        if (maxIter <= 0) throw new IllegalArgumentException("maxIter must be positive");
        if (tolF < 0 || tolX < 0) throw new IllegalArgumentException("tolerances must be non-negative");
        if (!Double.isFinite(x0)) throw new IllegalArgumentException("x0 must be finite");

        double x = x0;
        double[] trace = new double[maxIter + 1];
        trace[0] = x0;
        int n = 0;

        for (; n < maxIter; n++) {
            double fx = f.applyAsDouble(x);
            if (Math.abs(fx) <= tolF) break;

            double dfx = df.applyAsDouble(x);
            if (Math.abs(dfx) < DERIVATIVE_FLOOR) {
                throw new ArithmeticException(
                    "Derivative near zero at x=" + x + " (|df|=" + Math.abs(dfx) + ")");
            }

            double step = fx / dfx;
            if (!Double.isFinite(step)) {
                throw new ArithmeticException("Non-finite Newton step at x=" + x);
            }

            double next = x - step;
            if (!Double.isFinite(next)) {
                throw new ArithmeticException("Non-finite iterate produced at x=" + x);
            }

            trace[n + 1] = next;
            if (Math.abs(next - x) <= tolX) {
                n++;
                break;
            }
            x = next;
        }

        if (n >= maxIter) {
            throw new ArithmeticException(
                "Newton-Raphson failed to converge in " + maxIter + " iterations");
        }
        return new RootResult(x, n, Arrays.copyOf(trace, n + 1));
    }

    public static RootResult solveModified(DoubleUnaryOperator f, DoubleUnaryOperator df,
                                           double x0, double multiplicity,
                                           double tolF, double tolX, int maxIter) {
        if (multiplicity <= 0) throw new IllegalArgumentException("multiplicity must be positive");
        if (maxIter <= 0) throw new IllegalArgumentException("maxIter must be positive");
        double x = x0;
        double[] trace = new double[maxIter + 1];
        trace[0] = x0;
        int n = 0;
        for (; n < maxIter; n++) {
            double fx = f.applyAsDouble(x);
            if (Math.abs(fx) <= tolF) break;
            double dfx = df.applyAsDouble(x);
            if (Math.abs(dfx) < DERIVATIVE_FLOOR) {
                throw new ArithmeticException("Derivative near zero at x=" + x);
            }
            double next = x - multiplicity * fx / dfx;
            if (!Double.isFinite(next)) {
                throw new ArithmeticException("Non-finite iterate produced at x=" + x);
            }
            trace[n + 1] = next;
            if (Math.abs(next - x) <= tolX) {
                n++;
                break;
            }
            x = next;
        }
        if (n >= maxIter) {
            throw new ArithmeticException("Modified Newton failed to converge");
        }
        return new RootResult(x, n, Arrays.copyOf(trace, n + 1));
    }

    public static double estimatedOrder(double[] errorTrace, double root) {
        if (errorTrace.length < 3) return Double.NaN;
        double[] errs = new double[errorTrace.length];
        for (int i = 0; i < errorTrace.length; i++) {
            errs[i] = errorTrace[i] - root;
        }
        double p = Double.NaN;
        for (int i = errs.length - 1; i >= 2; i--) {
            double e2 = errs[i], e1 = errs[i - 1], e0 = errs[i - 2];
            if (Math.abs(e0) < 1e-300 || Math.abs(e1) < 1e-300) continue;
            double ratio = Math.log(Math.abs(e2 / e1)) / Math.log(Math.abs(e1 / e0));
            if (Double.isFinite(ratio) && ratio > 0) p = ratio;
        }
        return p;
    }

    private static void printRow(String label, RootResult r, double root) {
        double order = estimatedOrder(r.errorTrace(), root);
        System.out.printf("%-38s root=%.12f  iter=%d  order=%.4f%n",
                          label, r.root(), r.iterations(), order);
    }

    public static void main(String[] args) {
        System.out.println("=== Newton-Raphson Verification (quadratic convergence) ===%n".formatted());
        DoubleUnaryOperator f1 = x -> x * x - 2.0;
        DoubleUnaryOperator df1 = x -> 2.0 * x;
        RootResult simple = solve(f1, df1, 1.5, 1e-12, 1e-12, 20);
        printRow("simple root  x^2-2=0  x0=1.5", simple, Math.sqrt(2));

        DoubleUnaryOperator f2 = x -> x * x * x - 1.0;
        DoubleUnaryOperator df2 = x -> 3.0 * x * x;
        RootResult cube = solve(f2, df2, 0.5, 1e-12, 1e-12, 20);
        printRow("simple root  x^3-1=0  x0=0.5", cube, 1.0);

        DoubleUnaryOperator f3 = x -> (x - 1.0) * (x - 1.0);
        DoubleUnaryOperator df3 = x -> 2.0 * (x - 1.0);
        RootResult doubleRoot = solve(f3, df3, 1.5, 1e-12, 1e-12, 50);
        printRow("double root  (x-1)^2   plain", doubleRoot, 1.0);

        RootResult modified = solveModified(f3, df3, 1.5, 2.0, 1e-12, 1e-12, 20);
        printRow("double root  (x-1)^2   modified m=2", modified, 1.0);

        DoubleUnaryOperator f4 = x -> Math.exp(-x) - x;
        DoubleUnaryOperator df4 = x -> -Math.exp(-x) - 1.0;
        RootResult lambert = solve(f4, df4, 0.0, 1e-12, 1e-12, 20);
        System.out.printf("%-38s root=%.12f (expected ~0.5671432904)  iter=%d  order=%.4f%n",
                          "transcendental  exp(-x)-x=0", lambert.root(),
                          lambert.iterations(), estimatedOrder(lambert.errorTrace(),
                          Math.exp(-lambert.root())));

        try {
            solve(x -> x * x, x -> 2.0 * x, 0.0, 1e-12, 1e-12, 20);
            System.out.println("zero-derivative case: FAILED to throw");
        } catch (ArithmeticException e) {
            System.out.println("zero-derivative case: correctly threw -> " + e.getMessage());
        }

        try {
            solve(x -> x * x * x - 8.0, x -> 3.0 * x * x, 1000.0, 1e-12, 1e-12, 3);
            System.out.println("non-convergence case: FAILED to throw");
        } catch (ArithmeticException e) {
            System.out.println("non-convergence case: correctly threw -> " + e.getMessage());
        }
    }
}
```

---

## Step 4: Walkthrough of a Concrete Run

Function: f(x) = x² - 2, root r = √2 ≈ 1.4142135623730951, x₀ = 1.5.

| n | xₙ | eₙ = xₙ - r | |eₙ₊₁|/|eₙ|² | Correct digits |
|---|--------|-----------------|----------------|----------------|
| 0 | 1.5000000000 | 0.0857864 | — | 0 |
| 1 | 1.4166666667 | 0.0024531 | 0.333 | 2 |
| 2 | 1.4142156863 | 0.0000021239 | 0.353 | 5 |
| 3 | 1.4142135624 | 1.59e-12 | 0.353 | 11 |
| 4 | 1.4142135624 | 0.0 (≤ tolX) | — | 16 |

Observations:
- Each step squares the previous error (0.0858 → 0.00245 → 2.1e-6 → 1.6e-12 ≈ 0.353 · e²).
- Correct digits double per iteration: 0 → 2 → 5 → 11.
- The estimator `estimatedOrder` reads the trace and returns ≈ 2.00.

For the double root f(x) = (x - 1)², x₀ = 1.5, plain Newton produces eₙ₊₁ ≈ 0.5·eₙ: error halves per step (linear, p = 1). Modified Newton with m = 2 restores the quadratic pattern.

---

## Step 5: Testing & Verification

| # | Test | Input | Expected | Verified |
|---|------|-------|----------|----------|
| 1 | Simple root | f = x² - 2, x₀ = 1.5 | root = √2, iter ≤ 6, order ≈ 2.0 | main() row 1 |
| 2 | Simple root | f = x³ - 1, x₀ = 0.5 | root = 1.0, order ≈ 2.0 | main() row 2 |
| 3 | Double root | f = (x-1)², plain Newton | root = 1.0, order ≈ 1.0, more iterations | main() row 3 |
| 4 | Double root | f = (x-1)², modified m=2 | root = 1.0, order ≈ 2.0, fewer iterations | main() row 4 |
| 5 | Transcendental | f = e⁻ˣ - x, x₀ = 0 | root ≈ 0.5671432904 (Lambert W(1)) | main() row 5 |
| 6 | Zero derivative | f = x², x₀ = 0 | ArithmeticException thrown | main() try/catch |
| 7 | Non-convergence | x₀ = 1000, maxIter = 3 | ArithmeticException thrown | main() try/catch |
| 8 | Bad input | maxIter = 0 | IllegalArgumentException | code review |
| 9 | NaN iterate | f → NaN mid-run | ArithmeticException, not silent NaN | code review |
| 10 | NaN root input | x₀ = NaN | IllegalArgumentException | code review |

---

## Complexity Analysis

**Time**: Each iteration costs one f and one f' evaluation plus O(1) arithmetic. The number of iterations to reach tolerance τ from error e₀:

- Simple root: eₙ ≈ C^(2ⁿ - 1) · e₀^(2ⁿ) ⇒ n = O(log log(1/τ)). For τ = 1e-12, that is 4–6 iterations regardless of how far e₀ is from τ (as long as the method converges).
- Multiple root (plain): eₙ ≈ ((m-1)/m)ⁿ e₀ ⇒ n = O(log(1/τ)) — linear behavior, much slower.
- Failure path: O(maxIter).

**Space**: O(maxIter) for the error trace; O(1) extra beyond the trace. If the trace were dropped, the solver would be O(1) space.

**Trade-off**: The trace is what enables empirical order verification — a real engineering win because "quadratic convergence" is a claim you can measure, not just assert.

---

## Edge Cases & Pitfalls

1. **Derivative exactly zero** at x₀ (e.g. f(x) = x², x₀ = 0): throw with a clear message rather than divide by zero.
2. **Horizontal tangents in the neighborhood**: |f'| tiny but nonzero → huge step. The finite-step check catches the overflow before it propagates.
3. **f(x) = x - tan(x)**: classic oscillation — iterates bounce around instead of converging. Only the max-iteration guard catches this.
4. **Multiple roots**: order drops to 1; the estimator will report p ≈ 1. Use the m-modified variant when multiplicity is known, or switch to f/f' (the "function ratio" trick) which turns the multiple root of f into a simple root of f/f'.
5. **tolerance interplay**: if tolF is loose, the value test fires early and the trace is short — estimator returns NaN. Document that tolF ≈ tolX ≈ 1e-12 is the sensible default for doubles.
6. **Sign of the step**: the iteration subtracts f/f'. A common bug is adding it, which turns the method into an *antagonistic* iteration that diverges symmetrically.
7. **Complex roots**: Newton over the reals cannot converge to complex roots; the iterates bounce. Out of scope for the scalar real solver.

---

## Follow-up Questions

1. **Secant method**: replace f'(xₙ) with the secant slope (f(xₙ) - f(xₙ₋₁))/(xₙ - xₙ₋₁). Order drops to φ ≈ 1.618 (golden ratio) — superlinear but not quadratic, and no derivative is needed. Cost per iteration halves. Which wins in practice? It depends on the ratio of f vs f' evaluation costs.

2. **Halley's method**: third-order variant xₙ₊₁ = xₙ - 2 f f' / (2 f'² - f f''). Cubic convergence (p = 3) but needs f''. Rarely worth it unless f'' is free.

3. **Brent's method**: hybrid of bisection, secant, and inverse quadratic interpolation. Guaranteed convergence (bracketing) with superlinear rate — this is what real libraries (SciPy, GSL) ship. Would you swap the pure Newton solver for a Brent hybrid?

4. **Multivariate systems**: xₙ₊₁ = xₙ - J⁻¹ f with Jacobian J. Cost per iteration: one O(n³) factorization. For n large, use Broyden's quasi-Newton update or Krylov-based Newton-GMRES. Where does Newton-on-systems appear in your domain? (In quant: root of pricing equations, calibration.)

5. **Basins of attraction**: for which x₀ does the method converge? For polynomials of degree ≥ 3 the basins form fractals (Newton fractals). How would you build a robust global wrapper — grid the domain, run Newton from many starts, cluster the roots?

6. **If f' is only approximate** (finite differences, AD): convergence degrades; with relative error δ in the derivative, the asymptotic error is bounded by ~δ·|eₙ| — the order floor is 1 plus an error floor.

---

## Extension Ideas

- **Bisection fallback**: wrap `solve` so that if Newton throws, fall back to bisection on a bracketed interval. Combine both in a `RobustSolver` facade.
- **Adaptive multiplicity detection**: estimate m ≈ f'²/(f f'' - f'²) at the root during iteration and inject it into modified Newton automatically.
- **Complex root finder**: repeat the solver with complex arithmetic to find complex roots of polynomials (companion matrix / Durand-Kerner).
- **Instrumentation**: count function evaluations and derivative evaluations separately; report cost, not just iterations, since f may be expensive.
- **Benchmark suite**: compare against `Math.sqrt`, bisection, secant, and Brent on a 100-function corpus; chart iterations vs. initial guess distance.
