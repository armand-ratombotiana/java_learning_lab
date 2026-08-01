# Mock Interview: Gradient Descent with Line Search

> Coding mock interview — 45-minute session, Java 21+, whiteboard style.

---

## Interview Setup

**Role**: Machine Learning Engineer (Recommendation/Ads Team)
**Candidate Level**: Senior Engineer
**Focus Area**: Convex optimization, convergence analysis, implementation
**Problem**: Implement gradient descent with backtracking line search for convex functions, and demonstrate convergence with a rate table.
**Language**: Java 21+ (records, sealed types, functional interfaces allowed)

---

## Interviewer Cheat Sheet (Prepared Questions)

1. When is gradient descent guaranteed to converge? State the assumptions.
2. Why can't you always use a fixed step size? What breaks?
3. Derive the Armijo condition and explain why backtracking is a good line search.
4. What is the difference between exact line search and backtracking?
5. How does the convergence rate depend on the condition number? Where does that come from?
6. Follow-up: compare with momentum, Nesterov acceleration, conjugate gradient, L-BFGS.

---

## Transcript

### Part 1: Problem Clarification (5 minutes)

**Interviewer**: "We're training a logistic-regression-like model in a low-latency serving loop. I want you to implement gradient descent with line search for a convex, differentiable function f: R^n -> R, with a convergence report. Questions first."

**Candidate**: "Three things to pin down. Is f guaranteed smooth and strongly convex — is there a known Lipschitz constant for the gradient, and a strong-convexity parameter? That determines what guarantees I can state. Second, what do we get to know about f — I'll assume an oracle that returns f(x) and the gradient simultaneously, since evaluating them together is usually cheaper. Third, what's the deliverable format — a final iterate plus a per-iteration loss trace so we can verify the rate?"

**Interviewer**: "Assume f is smooth with L-Lipschitz gradient and μ-strongly convex — the textbook setting. And yes, give me the loss trace; we verify convergence rates empirically."

**Candidate**: "Good. Then I can promise a linear convergence rate with the right step-size strategy, and I'll show the theoretical bound alongside the measured trace."

### Part 2: Theory (8 minutes)

**Interviewer**: "What goes wrong with a fixed step size? Walk me through it."

**Candidate**: "The descent lemma says f(x - η∇f(x)) ≤ f(x) - η‖∇f(x)‖² + (η²L/2)‖∇f(x)‖². Minimizing the right side over η gives the optimal fixed step η* = 1/L, with guaranteed decrease. If η > 2/L, the bound says the function value can increase — the algorithm oscillates or diverges. The problem: L is rarely known, and worse, a single η = 1/L converges like (1 - μ/L)^k, which is *extremely* slow when the condition number κ = L/μ is large — say κ = 10⁶, then 1 - μ/L = 0.999999, and you need millions of iterations. So: too small a step crawls, too large diverges, and the safe fixed step is crippled by ill-conditioning."

**Interviewer**: "So what does line search give you?"

**Candidate**: "An adaptive step: at each iteration, start from a candidate η (say 1.0) and shrink it by a factor ρ until the Armijo condition holds: f(x - η∇f(x)) ≤ f(x) - c·η·‖∇f(x)‖² with c ∈ (0, 1), typically c = 0.1–0.5. Because f is smooth, this terminates quickly — in at most O(log(1/η)) shrink steps for a fixed η, and crucially the Armijo condition guarantees a *sufficient decrease* proportional to the squared gradient norm. Backtracking is cheap: each trial costs one function evaluation, and we already need f values anyway. It's adaptive to the local curvature: near the minimum where the function is flatter, it picks smaller steps automatically."

### Part 3: Design (7 minutes)

**Interviewer**: "Design the solver API."

**Candidate**: "A sealed interface for step-size strategies would be overkill — I'll use an enum with FIXED and BACKTRACKING plus parameters, or actually simpler: pass a `LineSearch` functional interface so the strategy is a lambda. The solver takes an oracle, an initial point, tolerances, and returns a record with the iterate, the loss trace, gradient norms, and the steps chosen. I'll also compute the empirical convergence factor — the ratio of successive loss gaps log(f_k - f*) — and compare it against the theory."

**Interviewer**: "What stopping criteria?"

**Candidate**: "Three: gradient norm below tol (first-order optimality — for convex f, ‖∇f‖ ≤ ε means f(x) - f* ≤ L ε²/2 via smoothness); step size below tol (we're stuck — happens with floating-point plateaus); max iterations as a hard cap. I always report which one fired."

### Part 4: Implementation (18 minutes)

**Interviewer**: "Code it."

**Candidate**:

```java
public record GDResult(double[] x, int iterations, boolean converged,
                       double[] lossTrace, double[] gradNormTrace, String stopReason) {}

public static GDResult minimize(Oracle oracle, double[] x0, double tol, int maxIter,
                                LineSearch ls) {
    double[] x = x0.clone();
    int n = x.length;
    double[] loss = new double[maxIter + 1];
    double[] gnorm = new double[maxIter + 1];
    loss[0] = oracle.f(x);
    gnorm[0] = norm(oracle.g(x));
    int k = 0;
    for (; k < maxIter; k++) {
        if (gnorm[k] <= tol) {
            return new GDResult(x, k, true, loss, gnorm, "gradient-norm tolerance");
        }
        double[] g = oracle.g(x);
        double step = ls.step(oracle, x, g, loss[k]);
        if (step <= 0) {
            return new GDResult(x, k, true, loss, gnorm, "line search returned zero step");
        }
        for (int i = 0; i < n; i++) x[i] -= step * g[i];
        loss[k + 1] = oracle.f(x);
        gnorm[k + 1] = norm(oracle.g(x));
    }
    return new GDResult(x, k, false, loss, gnorm, "max iterations reached");
}
```

**Interviewer**: "Show me the backtracking line search."

**Candidate**:

```java
public static double backtracking(Oracle oracle, double[] x, double[] g, double fx,
                                  double c, double rho) {
    double step = 1.0;
    int n = x.length;
    while (true) {
        double[] trial = new double[n];
        for (int i = 0; i < n; i++) trial[i] = x[i] - step * g[i];
        if (oracle.f(trial) <= fx - c * step * dot(g, g)) return step;
        step *= rho;
        if (step < 1e-12) throw new ArithmeticException("Line search stalled");
    }
}
```

**Interviewer**: "What's the convergence rate statement you'll report?"

**Candidate**: "With backtracking (c = 0.5, ρ = 0.5), the accepted step satisfies η ≥ min(1, ρ·2(1-c)/L)... in the worst case η ≈ ρ·(2(1-c)/L), so the decrease per step is at least ρ·2c(1-c)/L · ‖∇f‖², and combining with strong convexity gives f(x_{k+1}) - f* ≤ (1 - 2cρ(1-c)μ/L)(f(x_k) - f*), i.e. linear rate with factor q = 1 - c'·μ/L. The theory predicts the loss gap shrinks by a constant factor per iteration; I'll measure log(f_k - f*) per iteration and show it's a straight line whose slope is log q."

### Part 5: Testing (5 minutes)

**Interviewer**: "Test cases?"

**Candidate**: "Four: (1) a well-conditioned quadratic f = ½xᵀAx - bᵀx with A = diag(1, 1) — expect fast linear convergence, rate factor from theory; (2) the same with A = diag(1, 1000) — fixed step with η = 1/L crawls, backtracking adapts; (3) a Rosenbrock-like function with small curvature near the minimum — verify the line search never oversteps; (4) a function where gradient descent overshoots with a fixed step — demonstrate that the fixed-step config fails while backtracking succeeds. For each, I'll print the trace and the measured rate factor."

### Part 6: Follow-ups (5 minutes)

**Interviewer**: "How would you speed up convergence on ill-conditioned problems?"

**Candidate**: "Three orthogonal axes. Preconditioning: run gradient descent in a transformed space where the Hessian is close to the identity — that's what Newton's method does with the exact Hessian. Momentum: the heavy-ball method x_{k+1} = x_k - η∇f + β(x_k - x_{k-1}) achieves the same linear rate but with the optimal constant √(1 - μ/L) instead of 1 - μ/L; Nesterov acceleration improves the function-value gap to O(1/k²) for smooth (non-strongly) convex, which is optimal for first-order methods. And quasi-Newton: L-BFGS maintains a cheap approximation of the inverse Hessian and typically gets a rate close to Newton with O(n) per-iteration cost. In production I'd almost always reach for L-BFGS or Adam variants; plain gradient descent with line search is the teaching example and the safety net."

**Interviewer**: "What does the loss trace verification buy us in production?"

**Candidate**: "A regression test: when we change the model or data pipeline, the rate table tells us immediately whether optimization behavior changed. Loss flat but gradient norm still large means a bug in the gradient — I check the gradient with finite differences as a unit test. The trace is observability."

---

## Scoring Rubric

| Area | Excellent (3) | Good (2) | Needs Work (1) |
|------|---------------|----------|----------------|
| Theory | States descent lemma, L-smoothness, μ-strong convexity, rate q = 1 - O(μ/L) | Mentions convergence but not the rate constant | No convergence argument |
| Line search | Derives Armijo condition, explains adaptive step behavior | Implements backtracking by rote | Fixed step only |
| Implementation | Clean records, oracle interface, trace output, stopping reasons | Works but monolithic | Incomplete |
| Verification | Compares measured rate to theoretical bound | Reports loss trace | No diagnostics |

## Red Flags
- Using a fixed step without justifying η < 2/L.
- Not checking the gradient via finite differences.
- Confusing "converges" with "converges at a usable rate".
- Forgetting that for convex f, ‖∇f‖ small ⇒ f(x) close to f*.

## Key Takeaways
- Descent lemma: f(x - η∇f) ≤ f(x) - η‖∇f‖² + (η²L/2)‖∇f‖².
- Backtracking Armijo line search: adaptive, cheap, guarantees sufficient decrease.
- Linear rate q = 1 - O(μ/L); condition number κ = L/μ is the enemy.
- Measure the rate from the loss trace; assert the gradient via finite differences.
