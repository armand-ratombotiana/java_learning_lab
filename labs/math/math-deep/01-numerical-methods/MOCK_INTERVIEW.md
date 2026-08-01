# Mock Interview: Newton-Raphson Root Finding

> Coding mock interview — 45-minute session, Java 21+, whiteboard style.

---

## Interview Setup

**Role**: Numerical Software Engineer (Quant Trading Desk)
**Candidate Level**: Senior Engineer
**Focus Area**: Numerical methods, error analysis, convergence theory
**Problem**: Implement Newton-Raphson root finding with quadratic convergence, then prove and verify the convergence order empirically.
**Language**: Java 21+ (records, sealed types, functional interfaces allowed)

---

## Interviewer Cheat Sheet (Prepared Questions)

1. Can you state the Newton-Raphson iteration and derive it from a Taylor expansion?
2. What does "quadratic convergence" mean formally? Prove it for a simple root.
3. What happens near a multiple root? Does the order change?
4. How do you guard against divergence, zero derivative, and overshoot?
5. How would you verify the convergence order numerically?
6. Follow-up: compare with secant method, bisection, Brent's method.
7. Follow-up: how would you extend this to systems of equations?

---

## Transcript

### Part 1: Problem Clarification (5 minutes)

**Interviewer**: "We need to price a bond. The yield to maturity is the root of a smooth nonlinear equation — a price is given, and we solve for the interest rate. Implement Newton-Raphson root finding with quadratic convergence for a scalar function f: R -> R. Start by clarifying the requirements."

**Candidate**: "Before coding, three clarifications. First: do we have access to the derivative analytically, or must we approximate it? I'll assume we can pass f and f' as functions. Second: what behavior do we need on failure — throw, return a sentinel, or fall back to a bracketing method? Third: do you want a convergence-order diagnostic, or just the root itself?"

**Interviewer**: "Good. Assume an analytic derivative is available. On failure, I want a controlled exception with a clear message. And I'd like you to empirically demonstrate quadratic convergence — that's the interesting part."

**Candidate**: "Understood. So the deliverable is: a solver with proper termination criteria, robust failure handling, plus a driver that measures the order of convergence on a known function and shows it approaches 2 for a simple root."

### Part 2: Math Derivation (8 minutes)

**Interviewer**: "Derive the iteration for me. Where does x_{n+1} = x_n - f(x_n)/f'(x_n) come from?"

**Candidate**: "Expand f about the current iterate x_n: f(x) = f(x_n) + f'(x_n)(x - x_n) + (1/2)f''(ξ)(x - x_n)². If x is a root, f(x) = 0, so 0 ≈ f(x_n) + f'(x_n)(x - x_n). Solving for x gives the next guess: x_{n+1} = x_n - f(x_n)/f'(x_n). Geometrically it's the x-intercept of the tangent line at (x_n, f(x_n))."

**Interviewer**: "Why is it quadratic? Give me the formal argument."

**Candidate**: "Let r be the root and e_n = x_n - r the error. Taylor-expand at the root: 0 = f(r) = f(x_n) - f'(x_n)e_n + (1/2)f''(ξ_n)e_n². So f(x_n)/f'(x_n) = e_n - (1/2)(f''(ξ_n)/f'(x_n))e_n². Substitute into the iteration: e_{n+1} = x_{n+1} - r = e_n - f(x_n)/f'(x_n) = (1/2)(f''(ξ_n)/f'(x_n))e_n². Near a simple root f'(x_n) -> f'(r) != 0, so e_{n+1} ≈ C e_n² with C = f''(r)/(2f'(r)). Since e_{n+1} is proportional to e_n², the number of correct digits roughly doubles each step — that's quadratic convergence."

**Interviewer**: "What if the root is multiple, say f(x) = (x - r)^m * g(x) with m >= 2?"

**Candidate**: "Then f'(r) = 0 and the expansion breaks. The order drops to linear: e_{n+1} ≈ ((m-1)/m) e_n. Newton converges, but slowly, and the constant (m-1)/m approaches 1 as m grows. The fix is modified Newton with the multiplicity: x_{n+1} = x_n - m * f(x_n)/f'(x_n), which restores quadratic convergence when m is known."

### Part 3: Algorithm Design (7 minutes)

**Interviewer**: "Design the solver. What are the termination criteria and failure modes?"

**Candidate**: "Three termination conditions, in priority order: (1) |f(x_n)| < tol_f — the value itself is acceptably close to zero; (2) |x_{n+1} - x_n| < tol_x — the iterate has stopped moving, guarding against flat functions; (3) max iterations exceeded — declare failure. Failure modes I'll handle explicitly: a zero (or near-zero) derivative, which means the tangent is horizontal; non-convergence; NaN/Infinity appearing in an iterate; and a bad initial guess that sends the iterate toward infinity."

**Interviewer**: "How do you verify quadratic convergence empirically?"

**Candidate**: "Measure the asymptotic error constant empirically. From e_{n+1} ≈ C e_n^p, taking logs: log|e_{n+1}| ≈ p * log|e_n| + log C. I run the solver on a function with a known root, collect the error sequence, and fit p by the ratio p ≈ log|e_{n+1}/e_n| / log|e_n/e_{n-1}| once we're in the asymptotic regime. For a simple root that ratio should stabilize near 2. I can also demonstrate digit doubling: each step roughly doubles the number of correct significant digits."

### Part 4: Implementation (18 minutes)

**Interviewer**: "Code it."

**Candidate**: "I'll use a record for the result, a functional interface for the function, and a guarded solver. Java 21's records and pattern matching keep this tidy."

```java
public record RootResult(double root, int iterations, double[] errorTrace) {}

public static RootResult solve(DoubleUnaryOperator f, DoubleUnaryOperator df,
                               double x0, double tolF, double tolX, int maxIter) {
    if (maxIter <= 0) throw new IllegalArgumentException("maxIter must be positive");
    double x = x0;
    double[] trace = new double[maxIter + 1];
    int n = 0;
    trace[0] = x0;
    for (; n < maxIter; n++) {
        double fx = f.applyAsDouble(x);
        if (Math.abs(fx) <= tolF) break;
        double dfx = df.applyAsDouble(x);
        if (Math.abs(dfx) <= 1e-15) throw new ArithmeticException("Derivative near zero at x=" + x);
        double step = fx / dfx;
        if (!Double.isFinite(step)) throw new ArithmeticException("Non-finite Newton step");
        double next = x - step;
        if (Math.abs(next - x) <= tolX) { x = next; trace[n + 1] = x; n++; break; }
        x = next;
        trace[n + 1] = x;
    }
    if (n >= maxIter) throw new ArithmeticException("Newton-Raphson failed to converge");
    return new RootResult(x, n, Arrays.copyOf(trace, n + 1));
}
```

**Interviewer**: "The trace is useful. Now show me the convergence-order estimator."

**Candidate**:

```java
public static double estimatedOrder(double[] errs) {
    double p = Double.NaN;
    for (int i = errs.length - 1; i >= 2; i--) {
        double e2 = errs[i], e1 = errs[i - 1], e0 = errs[i - 2];
        if (e0 == 0 || e1 == 0) continue;
        double ratio = Math.log(Math.abs(e2 / e1)) / Math.log(Math.abs(e1 / e0));
        if (Double.isFinite(ratio)) p = ratio;
    }
    return p;
}
```

**Interviewer**: "On f(x) = x² - 2 with x0 = 1.5, what do you expect?"

**Candidate**: "The root is √2 ≈ 1.41421356. Errors will go roughly 0.0858, 0.00245, 2.1e-6, 1.6e-12 — squaring each time. The order estimator should return about 2.0. One subtlety: the last trace entry is the converged root, so I estimate the order on the interior of the trace, not including the final accepted point."

### Part 5: Testing & Edge Cases (5 minutes)

**Interviewer**: "Test cases?"

**Candidate**: "Five cases: (1) x² - 2 from x0 = 1.5 — expect ~4-5 iterations, order ≈ 2; (2) x³ - 1 from x0 = 0.5 — order 2 as well, but if I start from x0 = 0 the derivative is zero and I must throw cleanly; (3) f(x) = (x - 1)², a double root — expect the order to drop to 1, demonstrating the multiplicity effect; (4) f(x) = x - tan(x) — classic failure case where the method oscillates or jumps far; (5) exp(-x) with a huge initial guess — expect overflow protection."

**Interviewer**: "What about the tolerance interplay — tolF and tolX both set?"

**Candidate**: "Whichever fires first wins. That's the standard 'delta or value' rule. But there's a trap: if tolF is too loose, we stop early and the trace is short; if tolX is too loose, we stop when the step is small even if we're not at a root — that happens on flat functions where f' is large in magnitude relative to f. I'd default to relative-ish tolerances, e.g. tolX = 1e-12 and tolF = 1e-12 for doubles, and document that the caller can relax them."

### Part 6: Follow-ups (5 minutes)

**Interviewer**: "You mentioned systems. How does this generalize to f: R^n -> R^n?"

**Candidate**: "The derivative becomes the Jacobian J, and the update is x_{n+1} = x_n - J(x_n)^{-1} f(x_n), i.e. solve the linear system J d = -f. Each iteration costs an O(n³) factorization instead of one scalar division. For large n we'd use quasi-Newton methods like Broyden that update an approximate Jacobian cheaply. On the quant desk, root finding on systems is everywhere: calibrating volatility surfaces, solving SDE discretizations."

**Interviewer**: "One-line summary of when you'd choose each method?"

**Candidate**: "Bisection when you only need robustness and have a bracket; Newton when you have a good derivative and a decent start; secant when the derivative is expensive or unavailable; Brent when you want bracketing guarantees with superlinear speed."

---

## Scoring Rubric

| Area | Excellent (3) | Good (2) | Needs Work (1) |
|------|---------------|----------|----------------|
| Math derivation | Proves quadratic convergence from Taylor series; explains multiplicity | States iteration and convergence fact, no proof | Cannot derive the iteration |
| Robustness | Handles zero derivative, overflow, non-convergence with clear errors | Handles zero derivative only | No failure handling |
| Diagnostics | Empirically verifies order ≈ 2; explains digit doubling | Computes error trace | No convergence verification |
| Edge cases | Knows tan(x) failure mode, multiplicity, tolerance interplay | Knows multiplicity | Stops at first working test |

## Red Flags
- Claiming Newton always converges.
- Not checking for division by a near-zero derivative.
- Confusing "number of iterations" with "order of convergence".
- Using tolerance-only termination with no max-iteration guard.

## Key Takeaways
- e_{n+1} ≈ C e_n² near a simple root; order drops to 1 at multiple roots.
- Termination needs value test, step test, and max-iteration guard.
- Empirical order verification: ratio of log-errors → 2.
- Extension path: multivariate Newton with Jacobian factorization.
